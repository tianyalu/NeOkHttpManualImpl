package com.sty.ne.okhttp.manualimpl.okhttp;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/8 8:56 PM
 */
public class Request {
    public static final String GET = "GET";
    public static final String POST = "POST";

    private String url;
    private String requestMethod = GET; //默认请求是GET
    private Map<String, String> mHeaderList = new HashMap<>(); //请求头集合
    private RequestBody requestBody; //请求体

    public Request() {
        this(new Builder());
    }

    public Request(Builder builder) {
        this.url = builder.url;
        this.requestMethod = builder.requestMethod;
        this.mHeaderList = builder.mHeaderList;
        this.requestBody = builder.requestBody;
    }

    public static final class Builder {
        private String url;
        private String requestMethod = GET; //默认请求是GET
        private Map<String, String> mHeaderList = new HashMap<>();
        private RequestBody requestBody; //请求体

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder get() {
            requestMethod = GET;
            return this;
        }

        public Builder post(RequestBody requestBody) {
            requestMethod = POST;
            this.requestBody = requestBody;
            return this;
        }

        /**
         * Connection: keep-alive
         * Host: restapi.amap.com
         * @param key
         * @param value
         * @return
         */
        public Builder addRequestHeader(String key, String value) {
            mHeaderList.put(key, value);
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public Map<String, String> getHeaderList() {
        return mHeaderList;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
