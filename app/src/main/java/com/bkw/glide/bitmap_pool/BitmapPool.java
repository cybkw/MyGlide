package com.bkw.glide.bitmap_pool;

import android.graphics.Bitmap;

/**
 * f复用池标准接口
 */
public interface BitmapPool {

    /**
     * 存放到复用池
     *
     * @param bitmap
     */
    void put(Bitmap bitmap);

    /**
     * 获取匹配可用复用的Bitmap
     * @param w      宽
     * @param h      高
     * @param config bitmap配置
     * @return
     */
    Bitmap get(int w, int h, Bitmap.Config config);
}
