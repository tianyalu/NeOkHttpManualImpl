package com.sty.ne.okhttp.manualimpl.okhttp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/8 9:52 PM
 */
public class SocketRequestServer {
    private static final String TAG = SocketRequestServer.class.getSimpleName();
    private static final String SPACE = " ";
    private static final String VERSION = "HTTP/1.1";
    private static final String GRGN = "\r\n";
    /**
     * 通过Request对象寻找到域名
     * @param request
     * @return
     */
    public String getHost(Request request) {
        try {
            // http://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2
            URL url = new URL(request.getUrl());
            return url.getHost(); //restapi.amap.com
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过Request对象寻找到端口号
     * @param request
     * @return
     */
    public int getPort(Request request) {
        try {
            URL url = new URL(request.getUrl());
            int port = url.getPort();
            return port == -1 ? url.getDefaultPort() : port;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取请求头的所有信息
     * @param request
     * @return
     */
    public String getRequestHeaderAll(Request request) {
        //得到请求方式
        URL url = null;
        try {
            url = new URL(request.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String file = url.getFile();

        //拼接请求头的请求行： GET /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(request.getRequestMethod()) //GET/POST
                .append(SPACE)
                .append(file) // /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2
                .append(SPACE)
                .append(VERSION)
                .append(GRGN); //回车换行

        //获取请求集 进行拼接
        /**
         * Content-Length: 48
         * Content-Type: application/x-www-form-urlencoded
         * Host: restapi.amap.com
         */
        if(!request.getHeaderList().isEmpty()) {
            Map<String, String> mapList = request.getHeaderList();
            for (Map.Entry<String, String> entry : mapList.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append(":")
                        .append(SPACE)
                        .append(entry.getValue())
                        .append(GRGN);
            }
            //拼接空行，代码下面的是POST的请求体了
            stringBuffer.append(GRGN);
        }

        //请求体的拼接(只有POST请求才有请求体)
        if("POST".equalsIgnoreCase(request.getRequestMethod())) {
            stringBuffer.append(request.getRequestBody().getBody())
                    .append(GRGN);
        }

        return stringBuffer.toString();
    }
}
