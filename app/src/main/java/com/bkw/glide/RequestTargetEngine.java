package com.bkw.glide;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bkw.glide.bitmap_pool.BitmapPoolImpl;
import com.bkw.glide.cache.ActivieCache;
import com.bkw.glide.cache.MemoryCache;
import com.bkw.glide.cache.MemoryCacheCallback;
import com.bkw.glide.cache.disk.DiskLruCacheImpl;
import com.bkw.glide.fragment.LifecycleCallback;
import com.bkw.glide.resource.GlideLoadDataManager;
import com.bkw.glide.resource.Key;
import com.bkw.glide.resource.ResponseListener;
import com.bkw.glide.resource.Value;
import com.bkw.glide.resource.ValueCallback;

/**
 * 加载图片资源
 * 真正监听生命周期的实现类
 */
public class RequestTargetEngine implements LifecycleCallback, ValueCallback, MemoryCacheCallback, ResponseListener {

    private static final String TAG = "RequestTargetEngine";

    /**
     * 缓存大小
     */
    private final int MAX_SIZE = 1024 * 1024 * 60;

    @Override
    public void glideInitAction() {
        Log.e(TAG, "glideInitAction  初始化。。。");
    }

    @Override
    public void glideStopAction() {
        Log.e(TAG, "glideInitAction  停止了。。。");
    }

    @Override
    public void glideRecycleAction() {
        Log.e(TAG, "glideRecycleAction  进行释放操作。。。");
        //释放活动缓存
        if (activieCache != null) {
            activieCache.closeThread();
        }

    }

    /**
     * 活动缓存
     */
    private ActivieCache activieCache;

    /**
     * 内存缓存
     */
    private MemoryCache memoryCache;

    /**
     * 磁盘缓存
     */
    private DiskLruCacheImpl diskLruCache;

    private String path;
    private Context context;
    /**
     * 保存资源地址加密值
     */
    private Key key;
    private ImageView imageView;

    /**
     * Bitmap复用池
     */
    private BitmapPoolImpl bitmapPool;

    public RequestTargetEngine() {
        if (null == activieCache) {
            //this,回调告诉外界，资源Value不再使用了
            activieCache = new ActivieCache(this);
        }

        if (null == memoryCache) {
            memoryCache = new MemoryCache(MAX_SIZE);
            memoryCache.setMemoryCacheCallback(this);
        }

        if (null == diskLruCache) {
            diskLruCache = new DiskLruCacheImpl();
        }

//        if (bitmapPool == null) {
//            bitmapPool = new BitmapPoolImpl(MAX_SIZE);
//        }

    }

    /**
     * RequestManager传递的值
     */
    public void loadValueInitAction(String path, Context context) {
        this.path = path;
        this.context = context;
        //将地址转为加密的key
        key = new Key(path);
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;
        //控件是否为空
        Tool.checkNotNull(imageView);
        //是否为主线程
        Tool.assertMainThread();

        //开始加载
        Value value = cacheAction();

        if (null != value) {
            imageView.setImageBitmap(value.getmBitmap());

            //使用完成 减一
            value.subCount();
        }
    }

    private Value cacheAction() {
        //第一步：从活动缓存中寻找
        Value value = activieCache.get(key.getKey());
        if (null != value) {
            Log.e("TAG", "在活动缓存中找到了资源。");
            //表示该资源使用了一次
            value.useCount();
            return value;
        }

        //第二步：从内存缓存中寻找
        value = memoryCache.get(key.getKey());
        if (null != value) {
            Log.e("TAG", "在内存缓存中找到了资源。");
            //手动从内存缓存中移除
            memoryCache.userRemove(key.getKey());
            //将内存缓存中的该元素加入到活动缓存中
            activieCache.put(key.getKey(), value);
            //使用次数加1
            value.useCount();
            return value;
        }

        //第三步：磁盘缓存
        Log.e(TAG, key.toString());
        value = diskLruCache.get(key.getKey());
        if (null != value) {
            Log.e("TAG", "在磁盘缓存中找到了资源。");
            //找到了，将磁盘缓存中的元素加入到活动缓存
            activieCache.put(key.getKey(), value);

            //使用次数加1
            value.useCount();
            return value;
        }

        //第四步：本地缓存策略中都没有。从网络或SD卡上加载，外部加载
        value = new GlideLoadDataManager().loadResource(path, this, context);

        if (value != null) {
            Log.e("TAG", "加载外部资源。");
            return value;
        }
        return null;
    }

    /**
     * 活动缓存某个资源不再使用时的回调
     *
     * @param key   资源唯一标识
     * @param value 资源
     */
    @Override
    public void valueNonUseListener(String key, Value value) {
        if (key != null && value != null) {
            //将活动缓存中某个不再使用的资源添加到内存缓存中
            if (memoryCache != null) {
                memoryCache.put(key, value);
            }
        }
    }


    /**
     * 内存缓存最少使用元素被移除的回调
     *
     * @param key
     * @param value
     */
    @Override
    public void entryRemovedMemory(String key, Value value) {
        //TODO 添加到Bitmap复用池
//        bitmapPool.put(value.getmBitmap());
    }

    /**
     * 从外部加载资源成功
     *
     * @param value
     */
    @Override
    public void onSucess(Value value) {
        if (null != value) {
            imageView.setImageBitmap(value.getmBitmap());
            saveCache(key.getKey(), value);
        }
    }

    /**
     * 加载外部资源成功后，将资源保存到磁盘缓存中
     *
     * @param key
     * @param value
     */
    private void saveCache(String key, Value value) {
        Log.e("Glide", ">>>>>>>>>>>>>>>加载外部资源成功，保存到缓存中");
        value.setKey(key);

        //这里以保存到磁盘缓存为例
        if (diskLruCache != null) {
            diskLruCache.put(key, value);
        }
    }

    /**
     * 从外部加载资源失败
     *
     * @param e
     */
    @Override
    public void onFailed(Exception e) {
        Log.e("Glide", "加载外部资源发生异常" + e.getMessage());
    }
}
