package com.bkw.glide.fragment;

/**
 * 告知外界生命周期情况的接口
 */
public interface LifecycleCallback {

    /**
     * 生命周期初始化了
     */
    public void glideInitAction();

    /**
     * 生命周期停止了
     */
    public void glideStopAction();

    /**
     * 释放
     */
    public void glideRecycleAction();
}
