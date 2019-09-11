package com.bkw.glide.resource;

/**
 * 定义监听Value使用情况接口
 */
public interface ValueCallback {
    /**
     * 不再使用
     *
     * @param key   资源唯一标识
     * @param value 资源
     */
    public void valueNonUseListener(String key, Value value);
}
