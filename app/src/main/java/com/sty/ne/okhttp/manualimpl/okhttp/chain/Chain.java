package com.sty.ne.okhttp.manualimpl.okhttp.chain;

import com.sty.ne.okhttp.manualimpl.okhttp.Request;
import com.sty.ne.okhttp.manualimpl.okhttp.Response;

import java.io.IOException;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/9 7:46 PM
 */
public interface Chain {

    Request getRequest();

    Response getResponse(Request request) throws IOException;
}
