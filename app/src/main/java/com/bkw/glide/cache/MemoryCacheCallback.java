package com.bkw.glide.cache;

import com.bkw.glide.resource.Value;

/**
 * 内存缓存中，元素被移除的接口回调
 */
public interface MemoryCacheCallback {

    /** 移除内存缓存中的key--value
     * @param key
     * @param value
     */
    public void entryRemovedMemory(String key, Value value);
}
