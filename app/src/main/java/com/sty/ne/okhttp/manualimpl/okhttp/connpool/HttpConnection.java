package com.sty.ne.okhttp.manualimpl.okhttp.connpool;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * 连接对象:对Socket的封装管理
 * @Author: tian
 * @UpdateDate: 2020/9/10 8:58 PM
 */
public class HttpConnection {
    private static final String TAG = HttpConnection.class.getSimpleName();

    private Socket socket; //域名 端口
    private long lastUseTime; //连接对象最后使用的时间

    public HttpConnection(final String host, final int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //连接对象，自己最清楚上一次Socket和当前Socket到底有没有重复
    public boolean isConnectionAction(final String host, final int port) {
        if(socket == null) {
            Log.d(TAG, "isConnectionAction: socket is null" );
            return false;
        }
        if(socket.getPort() == port && socket.getInetAddress().getHostName().equals(host)) {
            return true;
        }
        return false;
    }

    /**
     * 释放Socket
     */
    public void recycleSocket() {
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "recycleSocket exception: " + e.getMessage());
            }
        }
    }

    public long getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(long lastUseTime) {
        this.lastUseTime = lastUseTime;
    }
}
