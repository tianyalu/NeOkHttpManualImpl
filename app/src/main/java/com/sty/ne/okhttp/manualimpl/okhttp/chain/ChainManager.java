package com.sty.ne.okhttp.manualimpl.okhttp.chain;

import com.sty.ne.okhttp.manualimpl.okhttp.Call;
import com.sty.ne.okhttp.manualimpl.okhttp.RealCall;
import com.sty.ne.okhttp.manualimpl.okhttp.Request;
import com.sty.ne.okhttp.manualimpl.okhttp.Response;

import java.io.IOException;
import java.util.List;

/**
 * 责任节点管理器
 * @Author: tian
 * @UpdateDate: 2020/9/9 7:50 PM
 */
public class ChainManager implements Chain{
    private final List<Interceptor> interceptors;
    private int index;
    private final Request request;
    private final RealCall realCall;

    public ChainManager(List<Interceptor> interceptors, int index, Request request, RealCall realCall) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
        this.realCall = realCall;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public Response getResponse(Request request) throws IOException {
        //判断index++ 计数，不能大于等于interceptors.size()
        if(index >= interceptors.size()) {
            throw new AssertionError();
        }
        if(interceptors.isEmpty()) {
            throw new IOException("interceptors is empty");
        }

        //取出第一个拦截器
        Interceptor interceptor = interceptors.get(index);//0
        ChainManager manager = new ChainManager(interceptors, index + 1, request, realCall);
        Response response = interceptor.doNext(manager);

        return response;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public int getIndex() {
        return index;
    }

    public RealCall getRealCall() {
        return realCall;
    }
}
