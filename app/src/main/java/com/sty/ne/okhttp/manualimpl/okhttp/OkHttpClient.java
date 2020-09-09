package com.sty.ne.okhttp.manualimpl.okhttp;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/8 8:50 PM
 */
public class OkHttpClient {
    private Dispatcher dispatcher;
    private boolean isCanceled;
    private int retryCount;

    public OkHttpClient() {
        this(new Builder());
    }

    public OkHttpClient(Builder builder) {
        dispatcher = builder.dispatcher;
        isCanceled = builder.isCanceled;
        retryCount = builder.retryCount;
    }

    public static final class Builder {
        private Dispatcher dispatcher;
        private boolean isCanceled;
        int retryCount = 3; //重试次数

        public Builder() {
            this.dispatcher = new Dispatcher();
        }

        public OkHttpClient build() {
            return new OkHttpClient(this);
        }

        public Builder setDispatcher(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        //用户取消请求
        public Builder canceled() {
            isCanceled = true;
            return this;
        }

        public Builder setRetryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }
    }

    public Call newCall(Request request) {
        //RealCall
        return new RealCall(this, request);
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public boolean getCanceled() {
        return isCanceled;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
