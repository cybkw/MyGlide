package com.bkw.glide.bitmap_pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.TreeMap;

/**
 * 复用池:复用Bitmap内存空间
 */
public class BitmapPoolImpl extends LruCache<Integer, Bitmap> implements BitmapPool {
    private static final String TAG = BitmapPoolImpl.class.getSimpleName();

    /**
     * 为了筛选出合适的bitmap,使用容器存放
     */
    private TreeMap<Integer, Object> treeMap = new TreeMap<>();


    public BitmapPoolImpl(int maxSize) {
        super(maxSize);
    }

    /**
     * 计算每个元素的大小
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    protected int sizeOf(@NonNull Integer key, @NonNull Bitmap value) {
        return getBitmapSize(value);
    }

    /**
     * 存入复用池
     *
     * @param bitmap
     */
    @Override
    public void put(Bitmap bitmap) {
//       条件一 bitmap.isMutable()==true-控制bitmap的setPixel方法能否使用，也就是外界能否修改bitmap的像素。
        if (!bitmap.isMutable()) {
            if (!bitmap.isRecycled()) {
                //如果没有回收，则调用回收方法
                bitmap.recycle();
            }
            Log.e(TAG, "此bitmap.isMutable=false,  不能存入复用池");
            return;
        }

        //条件二 bitmap的大小不能大于maxSize
        int bitmapSize = getBitmapSize(bitmap);
        if (bitmapSize > maxSize()) {
            if (!bitmap.isRecycled()) {
                //如果没有回收，则调用回收方法
                bitmap.recycle();
            }
            Log.e(TAG, "bitmap size 大于maxSize,不能存入复用池");
            return;
        }

        //存入复用池
        put(bitmapSize, bitmap);

        //存入筛选容器
        treeMap.put(bitmapSize, null);
        Log.e(TAG, "添加到复用池成功");
    }


    @Override
    public Bitmap get(int w, int h, Bitmap.Config config) {
        //Bitmap.Config.ALPHA_8, Android自动处理，只有透明度 8位 ==1个字节。
        //Bitmap.Config 位图信息不同，占用的字节大小也不同。
        //Bitmap.Config.RGB_565 R红色，G绿色，B蓝色 没有透明度 16位，占两个字节。
        //宽度*高度*Bitmap.Config =一个完整bitmap
        //ARGB_8888： Alpha 8位，Red 8位，G 8位，B 8位， 32位=4个字节。Android默认ARGB_8888
        //常用ARGB_8888  RGB_565

        //此处只考虑到ARGB_8888  RGB_565 两种。
        int getSize = w * h * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);

        //从容器中找出和getSize一样大或是更大的bitmap空间
        Integer key = treeMap.ceilingKey(getSize);
        //如果未找到，表示没有合适可复用，直接结束
        if (key == null) {
            return null;
        }

        //查找到的key的大小必须小于计算的(getSize*2)的2倍
        if (key <= (getSize * 2)) {
            //复用池取出，需要移除，不给其他人使用。
            Bitmap bitmap = remove(key);
            Log.e(TAG, "从复用池内获取了Bitmap");
            return bitmap;
        }
        return null;
    }


    private int getBitmapSize(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("getBitmapSize() bitmap is null");
        }

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt > Build.VERSION_CODES.KITKAT) {
            //API-19 4.4 使用native方法
            return bitmap.getAllocationByteCount();
        }

        //API 4.4以下的
        return bitmap.getByteCount();
    }


    /**
     * 元素被移除时的回调
     *
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, @NonNull Integer key, @NonNull Bitmap oldValue, @Nullable Bitmap newValue) {
        //将容器中的移除
        treeMap.remove(key);
    }
}
