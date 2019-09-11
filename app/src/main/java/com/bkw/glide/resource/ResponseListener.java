package com.bkw.glide.resource;

public interface ResponseListener {

    /**
     * 加载成功
     *
     * @param value
     */
    void onSucess(Value value);

    void onFailed(Exception e);
}
