package com.bkw.glide.thread;

import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {

    private static MyThreadPool instance;

    public static MyThreadPool getInstance() {
        if (null == instance) {
            synchronized (MyThreadPool.class) {
                if (null == instance) {
                    instance = new MyThreadPool();
                }
            }
        }
        return instance;
    }

    public ThreadPoolExecutor executors() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        Thread thread = new Thread(r, "Glide Thread");
                        thread.setDaemon(false);
                        return thread;
                    }
                });
    }
}
