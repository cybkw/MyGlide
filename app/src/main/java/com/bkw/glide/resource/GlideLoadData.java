package com.bkw.glide.resource;

import android.content.Context;

/**
 * 加载外部资源
 */
public interface GlideLoadData {
    /**
     * @param path             资源地址
     * @param responseListener 资源加载回调,成功or失败）
     * @param context          上下文
     * @return
     */
    Value loadResource(String path, ResponseListener responseListener, Context context);

}
