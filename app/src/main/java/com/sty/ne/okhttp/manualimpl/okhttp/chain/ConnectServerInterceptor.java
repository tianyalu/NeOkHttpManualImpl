package com.sty.ne.okhttp.manualimpl.okhttp.chain;

import android.util.Log;

import com.sty.ne.okhttp.manualimpl.okhttp.Request;
import com.sty.ne.okhttp.manualimpl.okhttp.Response;
import com.sty.ne.okhttp.manualimpl.okhttp.SocketRequestServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

/**
 * 连接服务器拦截器
 * @Author: tian
 * @UpdateDate: 2020/9/9 8:59 PM
 */
public class ConnectServerInterceptor implements Interceptor {
    private static final String TAG = ConnectServerInterceptor.class.getSimpleName();

    @Override
    public Response doNext(Chain chain) throws IOException {
        SocketRequestServer srs = new SocketRequestServer();
        Request request = chain.getRequest();
        Socket socket = null;

        String result = srs.queryHttpOrHttps(request.getUrl());
        if(request != null) {
            if("HTTP".equalsIgnoreCase(result)) {
                //只能访问Http，不能访问HTTPs
                socket = new Socket(srs.getHost(request), srs.getPort(request));
            }else if("HTTPS".equalsIgnoreCase(result)) {
                //HTTPS
                socket = SSLSocketFactory.getDefault().createSocket(srs.getHost(request), srs.getPort(request));
            }
        }

        //todo 请求
        OutputStream os = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
        String requestAll = srs.getRequestHeaderAll(request);
        Log.d(TAG, "requestAll: " + requestAll);
        bufferedWriter.write(requestAll); //给服务器发送请求
        bufferedWriter.flush(); //真正请求出去

        //TODO 响应
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                String readerLine = null;
//                while (true) {
//                    try {
//                        if ((readerLine = bufferedReader.readLine()) != null) {
//                            Log.d(TAG, "服务器响应为： " + readerLine);
//                        }else {
//                            return;
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }.start();

        Response response = new Response();
        // 取出响应码
        String readLine = bufferedReader.readLine(); //读取第一行响应头信息
        // 服务器响应为： HTTP/1.1 200 OK
        String[] strs = readLine.split(" ");
        response.setStatusCode(Integer.parseInt(strs[1]));
        // 取出响应体 只要是空行下面的就是响应体
        readLine = null;
        try {
            while ((readLine = bufferedReader.readLine()) != null) {
                if ("".equals(readLine)) {
                    //读到空行了，就代表下面就是响应体了；
                    response.setBody(bufferedReader.readLine()); //读取响应体
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
