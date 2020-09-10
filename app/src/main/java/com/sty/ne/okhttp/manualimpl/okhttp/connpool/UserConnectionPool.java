package com.sty.ne.okhttp.manualimpl.okhttp.connpool;

import android.util.Log;

/**
 * 模拟连接拦截器
 * @Author: tian
 * @UpdateDate: 2020/9/10 9:52 PM
 */
public class UserConnectionPool {
    private static final String TAG = UserConnectionPool.class.getSimpleName();

    public void usePool(ConnectionPool pool, String host, int port) {
        HttpConnection httpConnection = pool.getConnection(host, port);
        if(httpConnection == null) {
            httpConnection = new HttpConnection(host, port);
            Log.d(TAG, "usePool: 连接池中没有连接对象，需要实例化一个连接对象..." );
        }else {
            Log.d(TAG, "usePool: 复用一个连接对象" );
        }

        //模拟请求 服务器

        //使用完之后，一定要更新时间，并且加入到连接池里面去
        httpConnection.setLastUseTime(System.currentTimeMillis()); //更新最后使用的时间
        pool.putConnection(httpConnection);
        Log.d(TAG, "usePool: 给服务器请求..." );
    }
}
