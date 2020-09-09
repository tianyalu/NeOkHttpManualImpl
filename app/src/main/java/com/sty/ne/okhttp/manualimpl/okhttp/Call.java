package com.sty.ne.okhttp.manualimpl.okhttp;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/8 8:54 PM
 */
public interface Call {

    void enqueue(Callback responseCallback);
}
