package com.sty.ne.okhttp.manualimpl.okhttp;

/**
 * 响应的结果信息
 * @Author: tian
 * @UpdateDate: 2020/9/8 9:09 PM
 */
public class Response {
    private String body;
    private int statusCode;

    public String string() {
        return body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
