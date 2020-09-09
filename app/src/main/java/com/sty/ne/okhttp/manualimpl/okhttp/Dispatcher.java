package com.sty.ne.okhttp.manualimpl.okhttp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/8 9:30 PM
 */
public class Dispatcher {
    private int maxRequests = 64; //同时访问任务最大限制64个
    private int maxRequestsPerHost = 5; //同时访问同一个服务器域名的任务最大限制5个

    private Deque<RealCall.AsyncCall> runningAsyncCalls = new ArrayDeque<>(); //存储运行的队列
    private Deque<RealCall.AsyncCall> readyAsyncCalls = new ArrayDeque<>(); //等待运行的队列

    public void enqueue(RealCall.AsyncCall call) {
        //同时运行的队列数必须小于配置的64，同时访问同一个服务器域名不能超过5个
        if(runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
            runningAsyncCalls.add(call); //先把任务加入到运行队列中
            executorService().execute(call); //然后再执行
        } else {
            readyAsyncCalls.add(call); //加入到等待队列
        }
    }

    /**
     * 缓存方案线程池
     * @return
     */
    private ExecutorService executorService() {
        ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("自定义的线程...");
                        thread.setDaemon(false); //不是守护线程
                        return thread;
                    }
                });
        return executorService;
    }

    /**
     * 判断AsyncCall中的Host，在运行的队列中，计数，然后放回
     * 参数AsyncCall.Request.Host == runningAsyncCall.for{AsyncCall.Request.Host} + 1
     * @param call
     * @return
     */
    private int runningCallsForHost(RealCall.AsyncCall call) {
        int count = 0;
        if(runningAsyncCalls.isEmpty()) {
            return 0;
        }

        SocketRequestServer srs = new SocketRequestServer();
        //遍历运行队列中的所有任务，取出任务中的host == call.host  --> +1
        for (RealCall.AsyncCall runningAsyncCall : runningAsyncCalls) {
            //取出任务中的host == call.host
            if(srs.getHost(runningAsyncCall.getRequest()).equals(call.getRequest())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 1.移除运行完成的任务 (真实的源码中有对 maxRequests 和 maxRequestsPerHost 的限制处理）
     * 2.把等待队列中所有的任务取出来执行 --> run()
     * @param call
     */
    public void finished(RealCall.AsyncCall call) {
        //当前运行的任务回收
        runningAsyncCalls.remove(call);

        //考虑等待队列中是否有任务需要被执行
        if(readyAsyncCalls.isEmpty()) {
            return;
        }
        //把等待队列中的任务移动到运行队列
        for (RealCall.AsyncCall readyAsyncCall : readyAsyncCalls) {
            readyAsyncCalls.remove(readyAsyncCall); //删除等待队列中的任务
            runningAsyncCalls.add(readyAsyncCall); //把刚刚删除的等待队列任务加入到运行队列
            //开始执行
            executorService().execute(readyAsyncCall);
        }
    }
}
