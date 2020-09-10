package com.sty.ne.okhttp.manualimpl;

import android.os.SystemClock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/10 7:48 PM
 */
public class SocketHttp {
    private static final String GET_PATH = "http://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2";
    public static void main(String[] args) throws IOException {
        //socketHTTP();
        //socketHTTPs();
        manualInputRequestHTTP();
    }

    /**
     * Socket封装请求HTTP
     */
    private static void socketHTTP() {
        /**
         * GET /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1
         * Host: restapi.amap.com
         */
        try {
            Socket socket = new Socket("restapi.amap.com", 80); //http:80
            //写出去，请求
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write("GET /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1\r\n");
            bw.write("Host: restapi.amap.com\r\n\r\n");
            bw.flush();

            //读取数据 响应
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String readLine = null;
                if((readLine = br.readLine()) != null) {
                    System.out.println("响应的数据：" + readLine);
                }else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Socket封装请求HTTPS
     */
    private static void socketHTTPs() {
        try {
            //SSL 握手，访问HTTPS的socket客户端
            Socket socket = SSLSocketFactory.getDefault().createSocket("www.baidu.com", 443);
            //写出去，请求
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            /**
             * GET / HTTP/1.1
             * Host: baidu.com
             */
            bw.write("GET / HTTP/1.1\r\n");
            bw.write("Host: baidu.com\r\n\r\n");
            bw.flush();

            //读取数据 响应
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String readLine = null;
                if((readLine = br.readLine()) != null) {
                    System.out.println("响应的数据：" + readLine);
                }else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 手动输入网址请求HTTP
     */
    private static void manualInputRequestHTTP() throws IOException {
        System.out.println("输入网址，然后回车"); //www.baidu.com
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String inputPath = br.readLine();

        URL url = new URL("https://" + inputPath);

        String hostName = url.getHost();
        Socket socket = null;

        int port = 0;
        //HTTP HTTPS
        if("HTTP".equalsIgnoreCase(url.getProtocol())) {
            port = 80;
            socket = new Socket(hostName, port);
        }else if("HTTPS".equalsIgnoreCase(url.getProtocol())) {
            port = 443;
            socket = SSLSocketFactory.getDefault().createSocket(hostName, port);
        }
        if(socket == null) {
            System.out.println("error");
            return;
        }

        //写出去，请求
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        /**
         * GET / HTTP/1.1
         * Host: baidu.com
         */
        bw.write("GET / HTTP/1.1\r\n");
        bw.write("Host: " + hostName + "\r\n\r\n");
        bw.flush();

        //读取数据 响应
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            String readLine = null;
            if((readLine = bufferedReader.readLine()) != null) {
                System.out.println("响应的数据：" + readLine);
            }else {
                break;
            }
        }

    }
}
