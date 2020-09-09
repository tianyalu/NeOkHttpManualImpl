package com.sty.ne.okhttp.manualimpl.okhttp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求体对象
 * @Author: tian
 * @UpdateDate: 2020/9/9 8:39 PM
 */
public class RequestBody {
    //表单提交type   application/x-www-form-urlencoded
    public static final String TYPE = "application/x-www-form-urlencoded";
    private static final String ENC = "utf-8";

    //请求体集合 a=123&b=777
    Map<String, String> bodys = new HashMap<>();

    /**
     * 添加请求体信息
     * @param key
     * @param value
     */
    public void addBody(String key, String value) {
        //需要URL编码
        try {
            bodys.put(URLEncoder.encode(key, ENC), URLEncoder.encode(value, ENC));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到请求体信息
     * @return
     */
    public String getBody() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> stringStringEntry : bodys.entrySet()) {
            //a=123&b=777
            stringBuffer.append(stringStringEntry.getKey())
                    .append("=")
                    .append(stringStringEntry.getValue())
                    .append("&");
        }
        //删除最后一个 &
        if(stringBuffer.length() != 0) {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }
}
