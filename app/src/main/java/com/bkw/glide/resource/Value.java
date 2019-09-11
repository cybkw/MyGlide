package com.bkw.glide.resource;

import android.graphics.Bitmap;
import android.util.Log;

import com.bkw.glide.Tool;

/**
 * Bitmap封装
 *
 * @author bkw
 */
public class Value {

    private static Value value;

    private Bitmap mBitmap;

    /**
     * 使用计数
     */
    private int count;

    /**
     * 监听
     */
    private ValueCallback callback;

    /**
     * 唯一标识
     */
    private String key;

    private Value() {
    }

    public static Value getInstance() {
        if (null == value) {
            synchronized (Value.class) {
                if (null == value) {
                    value = new Value();
                }
            }
        }
        return value;
    }


    /**
     * 资源使用次数，每使用一次 +1
     */
    public void useCount() {
        //进行判空处理
        Tool.checkNotEmpty(mBitmap);

        if (mBitmap.isRecycled()) {
            //bitmap已经被回收
            Log.e("TAG", "使用次数 count: " + count);
            return;
        }
        count++;
    }

    /**
     * 使用完成 -1
     * count-- <= 0) 表示不再使用
     */
    public void subCount() {
        count--;
        if (count <= 0 && callback != null) {
            //回调告诉外界，不再使用
            callback.valueNonUseListener(key, this);
        }
        Log.e("TAG", "subCount 减一 count:"+count);
    }

    /**
     * 释放bitmap
     */
    public void recycleBitmap() {
        if (count > 0) {
            Log.e("TAG", "mBitmap 还在使用中。。");
        }

        value = null;

        if (mBitmap.isRecycled()) {
            Log.e("TAG", "mBitmap已经被释放");
            return;
        }
    }


    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ValueCallback getCallback() {
        return callback;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
