package com.sty.ne.okhttp.manualimpl.okhttp;

import java.io.IOException;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/8 8:54 PM
 */
public interface Callback {

    void onFailure(Call call, IOException e);

    void onResponse(Call call, Response response) throws IOException;
}
