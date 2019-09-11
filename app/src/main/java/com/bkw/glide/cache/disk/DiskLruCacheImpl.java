package com.bkw.glide.cache.disk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.bkw.glide.Tool;
import com.bkw.glide.bitmap_pool.BitmapPool;
import com.bkw.glide.resource.Value;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存的封装
 * 对DiskLruCache的封装
 */
public class DiskLruCacheImpl {

    private static final String TAG = "DiskLruCacheImpl";
    /**
     * 存储目录
     * 如：SDCard/disk_cache_dir/sdadadsadsada...
     */
    private static final String DISK_CACHE_DIR = "disk_cache_dir";

    /**
     * 版本号，一旦修改版本号，之前的缓存失效
     */
    private final int APP_VERSION = 1;

    /**
     * 通常情况都是1
     */
    private final Integer VALUE_COUNT = 1;
    /**
     * 磁盘缓存最大值
     * 可修改，使用者可以自定义
     */
    private final long MAX_SIZE = 1024 * 1024 * 10;

    /**
     * DiskLruCache
     */
    private DiskLruCache diskLruCache;

    public DiskLruCacheImpl() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + DISK_CACHE_DIR);
        try {
            diskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加
     *
     * @param key
     * @param value
     */
    public void put(String key, Value value) {
        //DiskLruCache的内部编辑器
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            //index不能大于VALUE_COUNT
            outputStream = editor.newOutputStream(0);
            Bitmap bitmap = value.getmBitmap();
            //指定格式PNG， 将bitmap写入到outputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
            //写入失败
            try {
                editor.abort();
            } catch (IOException e1) {
                Log.e(TAG, "写入失败 editor.abort() e:" + e.getMessage());
                e1.printStackTrace();
            }
        } finally {
            try {
                editor.commit();
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "失败 editor.commit(); e:" + e.getMessage());
            }


            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "失败 outputStream.close(); e:" + e.getMessage());
                }
            }

        }
    }

    /**
     * 获取Value
     * 不使用复用池
     *
     * @param key
     * @return
     */
    public Value get(String key) {
        InputStream inputStream = null;
        try {
            //返回一个快照
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);

            //快照不为空才读取
            if (snapshot != null) {
                //index不能大于VALUE_CONUT
                inputStream = snapshot.getInputStream(0);


                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                Value value = Value.getInstance();
                value.setmBitmap(bitmap);
                //对key进行判空处理
                Tool.checkNotEmpty(key);
                value.setKey(key);

                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "失败  inputStream.close(); e:" + e.getMessage());
                }
            }
        }

        return null;
    }

    /**
     * 获取Value
     *
     * @param key
     * @param bitmapPool 复用池
     * @return
     */
    public Value get(String key, BitmapPool bitmapPool) {
        InputStream inputStream = null;
        try {
            //返回一个快照
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);

            //快照不为空才读取
            if (snapshot != null) {
                //index不能大于VALUE_CONUT
                inputStream = snapshot.getInputStream(0);

                //取得bitmap周边信息
//                options.inJustDecodeBounds = true;
//                //执行以下代码会计算得到options信息
//                BitmapFactory.decodeStream(new BufferedInputStream(bytes), null, options);
//                int width = options.outWidth;
//                int height = options.outHeight;
//                Log.e("TAG", "width=" + width + ",height=" + height);
//                int width = 1920;
//                int height = 1080;

                //使用复用池
//                BitmapFactory.Options options = new BitmapFactory.Options();
                  /*
                拿到复用池，对options进行处理
                把复用池的bitmap给inBitmap,。如果设置inBitmap为null，内部就不会去申请新的内存空间，依然会造成内存抖动，内存碎片
                bitmap.isMutable()==true 条件才成立
                * */
//                Bitmap bitmapPoolResult = bitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
//                options.inBitmap = bitmapPoolResult;
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                options.inJustDecodeBounds = false;
//                options.inMutable = true; //符合复用机制
                // inSampleSize:是采样率，当inSampleSize为2时，一个2000 1000的图片，将被缩小为1000 500， 采样率为1 代表和原图宽高最接近
//                options.inSampleSize = Tool.sampleBitmapSize(options, width, height);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                Value value = Value.getInstance();
                value.setmBitmap(bitmap);
                //对key进行判空处理
                Tool.checkNotEmpty(key);
                value.setKey(key);

                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "失败  inputStream.close(); e:" + e.getMessage());
                }
            }
        }

        return null;
    }
}
