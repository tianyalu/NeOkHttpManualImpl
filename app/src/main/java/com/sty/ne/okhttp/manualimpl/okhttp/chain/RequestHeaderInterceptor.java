package com.sty.ne.okhttp.manualimpl.okhttp.chain;

import com.sty.ne.okhttp.manualimpl.okhttp.Request;
import com.sty.ne.okhttp.manualimpl.okhttp.RequestBody;
import com.sty.ne.okhttp.manualimpl.okhttp.Response;
import com.sty.ne.okhttp.manualimpl.okhttp.SocketRequestServer;

import java.io.IOException;
import java.util.Map;

/**
 * 请求头拦截器处理
 * @Author: tian
 * @UpdateDate: 2020/9/9 8:25 PM
 */
public class RequestHeaderInterceptor implements Interceptor {
    @Override
    public Response doNext(Chain chain) throws IOException {
        //拼接请求头之请求集
        ChainManager manager = (ChainManager) chain;
        Request request = manager.getRequest();

        Map<String, String> headerList = manager.getRequest().getHeaderList();
        //get pos hostName     Host:restapi.amap.com
        headerList.put("Host", new SocketRequestServer().getHost(manager.getRequest()));

        if("POST".equalsIgnoreCase(request.getRequestMethod())) {
            // 请求体 type len
            /**
             * Content-Length: 48
             * Content-Type: application/x-www-form-urlencoded
             */
            headerList.put("Content-Length", request.getRequestBody().getBody().length() + "");
            headerList.put("Content-Type", RequestBody.TYPE);
        }
        return chain.getResponse(request);
    }
}
