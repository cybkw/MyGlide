package com.bkw.glide.resource;

import com.bkw.glide.Tool;

/**
 * 资源的唯一标识
 *
 * @author bkw
 */
public class Key {
    /**
     * 需要对key字符串进行加密
     * 如：网络图片地址加密成SHA256字符
     */
    private String key;

    public Key(String key) {
        this.key = Tool.getSHA256StrJava(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Key{" +
                "key='" + key + '\'' +
                '}';
    }
}
