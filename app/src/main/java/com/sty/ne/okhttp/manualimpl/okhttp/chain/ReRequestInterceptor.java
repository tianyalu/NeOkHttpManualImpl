package com.sty.ne.okhttp.manualimpl.okhttp.chain;

import android.util.Log;

import com.sty.ne.okhttp.manualimpl.okhttp.OkHttpClient;
import com.sty.ne.okhttp.manualimpl.okhttp.RealCall;
import com.sty.ne.okhttp.manualimpl.okhttp.Response;

import java.io.IOException;

/**
 * 重试拦截器
 * @Author: tian
 * @UpdateDate: 2020/9/9 7:49 PM
 */
public class ReRequestInterceptor implements Interceptor{
    private static final String TAG = ReRequestInterceptor.class.getSimpleName();

    @Override
    public Response doNext(Chain chain) throws IOException{
        Log.d(TAG, "我是重试拦截器，我要返回Response了");

        ChainManager chainManager = (ChainManager) chain;

        RealCall realCall = chainManager.getRealCall();
        OkHttpClient okHttpClient = realCall.getOkHttpClient();

        IOException ioException = null;

        //重试次数
        if(okHttpClient.getRetryCount() != 0) {
            for (int i = 0; i < okHttpClient.getRetryCount(); i++) {
                try {
                    Log.d(TAG, "我是重试拦截器，我要返回Response了");
                    //如果没有异常，循环就结束了
                    Response response = chain.getResponse(chainManager.getRequest()); //执行下一个拦截器（任务节点）
                    return response;
                } catch (IOException e) {
                    ioException = e;
                }
            }
        }
        //return null;
        throw ioException;
    }
}
