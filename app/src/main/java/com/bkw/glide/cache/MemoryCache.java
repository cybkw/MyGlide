package com.bkw.glide.cache;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.bkw.glide.resource.Value;

/**
 * 内存缓存--LRU算法
 */
public class MemoryCache extends LruCache<String, Value> {

    private boolean isUserRemove;

    private MemoryCacheCallback memoryCacheCallback;

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }

    /**
     * 手动移除
     *
     * @param key
     */
    public Value userRemove(String key) {
        isUserRemove = true;
        Value value = remove(key);
        isUserRemove = false;
        //调用这里最后会调用到entryRemoved方法，isUserRemove区分手动和被动移除。
        return value;
    }

    /**
     * 缓存最大元素值
     *
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);

    }

    @Override
    protected int sizeOf(String key, Value value) {
        Bitmap bitmap = value.getmBitmap();

        //获取Bitmap的大小，不同版本API获取的方式。 在Bitmap内存复用方式有区别
        //最开始时
//        int result = bitmap.getRowBytes() * bitmap.getHeight();

//        //后续时 API-11 3.0
//        result = bitmap.getByteCount();

        //API-19 4.4 使用native方法
//        result= bitmap.getAllocationByteCount();

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt > Build.VERSION_CODES.KITKAT) {
            //API-19 4.4 使用native方法
            return bitmap.getAllocationByteCount();
        }

        //API 4.4一下的
        return bitmap.getByteCount();
    }


    /**
     * 1.重复的key移除掉一个
     * 2.最少使用的元素会被移除
     *
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, Value oldValue, Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (null != memoryCacheCallback && !isUserRemove) {
            memoryCacheCallback.entryRemovedMemory(key, oldValue);
        }
    }
}
