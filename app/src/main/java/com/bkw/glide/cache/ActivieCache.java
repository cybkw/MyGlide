package com.bkw.glide.cache;

import com.bkw.glide.Tool;
import com.bkw.glide.resource.Value;
import com.bkw.glide.resource.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动缓存--正在被使用的资源
 *
 * @author bkw
 */
public class ActivieCache {

    /**
     * 缓存集合
     */
    private Map<String, WeakReference<Value>> cacheMap = new HashMap<>();
    /**
     * 此变量是为了监听弱引用是否被回收了
     */
    private ReferenceQueue<Value> queue;

    /**
     * 关闭线程
     */
    private boolean isCloseThread;

    /**
     * 循环线程
     */
    private Thread thread;

    /**
     * 用于判断是否为手动移除，true为手动移除
     */
    private boolean isUserRemove;

    /**
     * 监听Value使用情况
     */
    private ValueCallback valueCallback;

    public ActivieCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    /**
     * 添加活动缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        Tool.checkNotNull(value);
        //绑定Value监听
        value.setCallback(valueCallback);

        cacheMap.put(key, new ValueWeakReference(value, getQueue(), key));
    }

    /**
     * 获取Value
     *
     * @param key
     * @return
     */
    public Value get(String key) {
        WeakReference<Value> valueWeakReference = cacheMap.get(key);
        if (null != valueWeakReference) {
            //返回Value
            return valueWeakReference.get();
        }
        return null;
    }

    /**
     * 手动移除
     *
     * @param key
     * @return 告知用户移除的对应对象
     */
    public Value remove(String key) {
        isUserRemove = true;
        //移除后会返回弱引用
        WeakReference<Value> valueWeakReference = cacheMap.remove(key);
        //移除完毕，手动改回
        isUserRemove = false;
        if (null != valueWeakReference) {
            return valueWeakReference.get();
        }
        return null;
    }

    /**
     * 停止线程循环
     */
    public void closeThread() {
        isCloseThread = true;
       /* if (null != thread) {
            //线程挂起
            thread.interrupt();
            try {
                //让线程稳定停止，时长5秒
                thread.join(TimeUnit.SECONDS.toMillis(5));
                if (thread.isAlive()) {
                    //如果线程还没停止
                    throw new IllegalArgumentException("活动缓存 线程未能正常停止");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        cacheMap.clear();
        System.gc();
    }

    /**
     * 监听弱引用，称为弱引用的子类
     */
    public class ValueWeakReference extends WeakReference<Value> {

        private String key;

        /**
         * @param referent
         * @param queue    可以监听到弱引用对象是否被回收掉了
         */
        public ValueWeakReference(Value referent, ReferenceQueue<? super Value> queue, String key) {
            super(referent, queue);
            this.key = key;
        }
    }

    /**
     * 为了监听到弱引用被引用，GC被动回收
     *
     * @return
     */
    private ReferenceQueue<Value> getQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();

            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (!isCloseThread) {
                        try {
                            if (!isUserRemove) {
                                //queue.remove(); 阻塞式方法，回收时扫描到才会调用

                                //如果被回收了，就会执行到这个方法
                                Reference<? extends Value> reference = queue.remove();
                                //这里强转就是为了得到key
                                ValueWeakReference weakReference = (ValueWeakReference) reference;

                                //从容器中移除 isUserRemove 为了区分手动移除和被动移除
                                if (cacheMap != null && !cacheMap.isEmpty()) {
                                    cacheMap.remove(weakReference.key);
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        }
        return queue;
    }
}
