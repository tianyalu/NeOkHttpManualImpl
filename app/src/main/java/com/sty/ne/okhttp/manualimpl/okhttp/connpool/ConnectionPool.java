package com.sty.ne.okhttp.manualimpl.okhttp.connpool;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 连接池，一个很大的池子，可以存入取出（连接对象）
 * 网络连接的时候，需要减少服务器的压力，所以需要复用Socket，才有了连接池的概念
 * @Author: tian
 * @UpdateDate: 2020/9/10 8:58 PM
 */
public class ConnectionPool {
    private static final String TAG = ConnectionPool.class.getSimpleName();
    private boolean cleanRunnableFlag; //标记
    private static Deque<HttpConnection> httpConnectionDeque = null; //双端队列
    //定义最大允许的闲置时间 1分钟（考虑扩展）
    private long keepAlive;

    public ConnectionPool() {
        this(1, TimeUnit.SECONDS);
    }

    public ConnectionPool(long keepAlive, TimeUnit timeUnit) {
        this.keepAlive = timeUnit.toMillis(keepAlive);
        httpConnectionDeque = new ArrayDeque<>();
    }

    //任务，为清理机制工作
    //专门去检查连接池中的连接对象，清理连接池中的连接对象
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                //下次的检查时间(动态变化)
                long nextCheckCleanTime = clean(System.currentTimeMillis());
                if(nextCheckCleanTime == -1) {
                    //全部结束
                    cleanRunnableFlag = false;
                    return;
                }
                //nextCheckCleanTime = 30 下一次的检查时间（动态变化）
                if(nextCheckCleanTime > 0) {
                    //等待一段时间后，再去检查是否需要清理的对象
                    synchronized (ConnectionPool.this) {
                        try {
                            ConnectionPool.this.wait(nextCheckCleanTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    };

    private long clean(long currentTimeMillis) {
        //定义最终最大的闲置时间 result
        long idleRecordSave = -1;
        synchronized (this) {
            Iterator<HttpConnection> iterator = httpConnectionDeque.iterator();
            //遍历 池子 容器
            while (iterator.hasNext()) {
                HttpConnection httpConnection = iterator.next();
                //计算出来的闲置时间
                long idleTime = currentTimeMillis - httpConnection.getLastUseTime();
                if(idleTime > keepAlive) {
                    //清理 移除对象
                    iterator.remove();
                    //关闭socket
                    httpConnection.recycleSocket();
                    continue; //继续检查后续是否有连接对象需要清理
                }

                //时间的处理
                if(idleRecordSave < idleTime) { //证明计算出来的闲置时间idleTime是合格的
                    idleRecordSave = idleTime;
                }
            } //while end
            //循环完毕之后，代表最终闲置时间idleRecordSave终于计算出来了（动态变化）
            if(idleRecordSave >= 0) {
                //keepAlive = 60S
                return (keepAlive - idleRecordSave);
            }
        }
        //返回-1，表示没有计算好，连接池中没有连接对象，则不作任何操作，马上结束所有任务
        return idleRecordSave;
    }

    //线程池，为清理机制工作
    private Executor threadExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "线程池标记...");
                    thread.setDaemon(true); //设置守护线程
                    return thread;
                }
            });

    //添加
    public synchronized void putConnection(HttpConnection httpConnection) {
        //一旦put的时候，就需要去检查连接池中需要释放清理的对象
        if(!cleanRunnableFlag) {
            cleanRunnableFlag = true;
            //启动检查清理机制
        }
        httpConnectionDeque.add(httpConnection);
        int size = httpConnectionDeque.size();
        Log.d(TAG, "putConnection size: " + size);
    }

    //获取
    public HttpConnection getConnection(String host, int port) {
        Iterator<HttpConnection> iterator = httpConnectionDeque.iterator();
        while (iterator.hasNext()) {
            HttpConnection httpConnection = iterator.next();
            if(httpConnection.isConnectionAction(host, port)) {
                //移除(如果get,就把容器中的连接对象移除了）
                iterator.remove();
                return httpConnection;
            }
        }
        return null;
    }
}
