package com.sty.ne.okhttp.manualimpl.okhttp;

import android.util.Log;

import com.sty.ne.okhttp.manualimpl.okhttp.chain.ChainManager;
import com.sty.ne.okhttp.manualimpl.okhttp.chain.ConnectServerInterceptor;
import com.sty.ne.okhttp.manualimpl.okhttp.chain.Interceptor;
import com.sty.ne.okhttp.manualimpl.okhttp.chain.ReRequestInterceptor;
import com.sty.ne.okhttp.manualimpl.okhttp.chain.RequestHeaderInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/8 9:23 PM
 */
public class RealCall implements Call {
    private static final String TAG = RealCall.class.getSimpleName();
    private OkHttpClient okHttpClient;
    private Request request;
    private boolean executed;

    public RealCall(OkHttpClient okHttpClient, Request request) {
        this.okHttpClient = okHttpClient;
        this.request = request;
    }

    @Override
    public void enqueue(Callback responseCallback) {
        //不能重复执行enqueue
        synchronized (this) {
            if(executed) {
                throw new IllegalStateException("不能重复执行enqueue Already Executed");
            }
            executed = true;
        }
        okHttpClient.getDispatcher().enqueue(new AsyncCall(responseCallback));
    }

    public final class AsyncCall implements Runnable {
        private Callback callback;

        public AsyncCall(Callback callback) {
            this.callback = callback;
        }

        public Request getRequest() {
            return RealCall.this.request;
        }

        @Override
        public void run() {
            //耗时操作
            boolean signalledCallback = false;
            try {
                Response response = getResponseWithInterceptorChain();
                //如果用户取消了请求，回调给用户，说失败了
                if(okHttpClient.getCanceled()) {
                    signalledCallback = true;
                    callback.onFailure(RealCall.this, new IOException("用户取消了 Canceled"));
                } else {
                    signalledCallback = true;
                    callback.onResponse(RealCall.this, response);
                }
            } catch (IOException e) {
                //责任的划分
                if(signalledCallback) { //如果等于true,表示回调给用户了，是用户操作的时候报错的
                    Log.d(TAG, "用户在使用过程中出错了");
                }else {
                    callback.onFailure(RealCall.this,
                            new IOException("OKHTTP getResponseWithInterceptorChain 错误... e: " + e.toString()));
                }
                e.printStackTrace();
            } finally {
                //回收处理
                okHttpClient.getDispatcher().finished(this);
            }
        }
    }

    private Response getResponseWithInterceptorChain() throws IOException{
        List<Interceptor> interceptorList = new ArrayList<>();
        interceptorList.add(new ReRequestInterceptor()); //重试拦截器
        interceptorList.add(new RequestHeaderInterceptor()); //请求头拦截器
        interceptorList.add(new ConnectServerInterceptor()); //连接服务器的拦截器

        ChainManager chainManager = new ChainManager(interceptorList, 0, request, this);
        return chainManager.getResponse(request); //最终返回的Response
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public Request getRequest() {
        return request;
    }

    public boolean isExecuted() {
        return executed;
    }
}
