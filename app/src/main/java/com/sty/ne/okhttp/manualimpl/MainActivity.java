package com.sty.ne.okhttp.manualimpl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sty.ne.okhttp.manualimpl.okhttp.Call;
import com.sty.ne.okhttp.manualimpl.okhttp.Callback;
import com.sty.ne.okhttp.manualimpl.okhttp.OkHttpClient;
import com.sty.ne.okhttp.manualimpl.okhttp.Request;
import com.sty.ne.okhttp.manualimpl.okhttp.RequestBody;
import com.sty.ne.okhttp.manualimpl.okhttp.Response;
import com.sty.ne.okhttp.manualimpl.okhttp.connpool.ConnectionPool;
import com.sty.ne.okhttp.manualimpl.okhttp.connpool.UserConnectionPool;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String GET_PATH = "http://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2";
    private static final String POST_PATH = "http://restapi.amap.com/v3/weather/weatherInfo";
    private Button btnRequestGet;
    private Button btnRequestPost;
    private Button btnConnectionPool;
    private TextView tvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        addListeners();
    }

    private void initView() {
        btnRequestGet = findViewById(R.id.btn_request_get);
        btnRequestPost = findViewById(R.id.btn_request_post);
        btnConnectionPool = findViewById(R.id.btn_connection_pool);
        tvContent = findViewById(R.id.tv_content);
    }

    private void addListeners() {
        btnRequestGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnRequestGetClicked();
            }
        });
        btnRequestPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnRequestPostClicked();
            }
        });
        btnConnectionPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnConnectionPoolClicked();
            }
        });
    }

    private void onBtnRequestGetClicked() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Request request = new Request.Builder().get().setUrl(GET_PATH).build();

        Call call = okHttpClient.newCall(request);

        //异步执行
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "自定义OKHTTP请求失败...");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response != null) {
                    Log.d(TAG, "自定义OKHTTP请求成功： " + response.getBody() + "\n响应码为： " + response.getStatusCode());

                    showUI("自定义OKHTTP请求成功： " + response.getBody() + "\n响应码为： " + response.getStatusCode());
                }
            }
        });
    }

    private void onBtnRequestPostClicked() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        //请求体 ?city=110101&key=13cb58f5884f9749287abbead9c658f2
        RequestBody requestBody = new RequestBody();
        requestBody.addBody("city", "110101");
        requestBody.addBody("key", "13cb58f5884f9749287abbead9c658f2");

        Request request = new Request.Builder().post(requestBody).setUrl(POST_PATH).build();

        Call call = okHttpClient.newCall(request);

        //异步执行
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "自定义OKHTTP请求失败...");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response != null) {
                    Log.d(TAG, "自定义OKHTTP请求成功： " + response.getBody() + "\n响应码为： " + response.getStatusCode());
                    showUI("自定义OKHTTP请求成功： " + response.getBody() + "\n响应码为： " + response.getStatusCode());
                }
            }
        });
    }

    private void showUI(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContent.setText(text);
            }
        });
    }


    private void onBtnConnectionPoolClicked() {
        //拿到连接池
        final ConnectionPool connectionPool = new ConnectionPool();
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserConnectionPool userConnectionPool = new UserConnectionPool();
                for (int i = 0; i < 5; i++) {
                    userConnectionPool.usePool(connectionPool, "restapi.amap.com", 80);
                }
            }
        }).start();
        // D/UserConnectionPool: usePool: 连接池中没有连接对象，需要实例化一个连接对象...
        // D/ConnectionPool: putConnection size: 1
        // D/UserConnectionPool: usePool: 给服务器请求...
        // D/UserConnectionPool: usePool: 复用一个连接对象
        // D/ConnectionPool: putConnection size: 1
        // D/UserConnectionPool: usePool: 给服务器请求...
        // D/UserConnectionPool: usePool: 复用一个连接对象
        // D/ConnectionPool: putConnection size: 1
        // D/UserConnectionPool: usePool: 给服务器请求...
        // D/UserConnectionPool: usePool: 复用一个连接对象
        // D/ConnectionPool: putConnection size: 1
        // D/UserConnectionPool: usePool: 给服务器请求...
        // D/UserConnectionPool: usePool: 复用一个连接对象
        // D/ConnectionPool: putConnection size: 1
        // D/UserConnectionPool: usePool: 给服务器请求...
    }
}