package com.bkw.glide;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

public class Glide {

    RequestManagerRetriever retriever;

    public Glide(RequestManagerRetriever retriever) {
        this.retriever = retriever;
    }

    public static RequestManager with(FragmentActivity activity) {
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(Activity activity) {
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(Context context) {
        return getRetriever(context).get(context);
    }

    /**
     * RequestManager对象由RequestManagerRetriever去创建
     *
     * @return
     */
    public static RequestManagerRetriever getRetriever(Context context) {
        return Glide.get(context).getRetriever();
    }

    /**
     * Glide是new出来的，转变了，不再是静态了
     *
     * @param context
     * @return
     */
    public static Glide get(Context context) {
        return new GlideBuilder().build();
    }

    /**
     * 此处就是为了构建RequestManagerRetriever，进行一个转变，得到实例而不是静态实例
     */
    static class GlideBuilder {
        public Glide build() {
            RequestManagerRetriever requestManagerRetriever = new RequestManagerRetriever();

            Glide glide = new Glide(requestManagerRetriever);
            return glide;
        }
    }


    public RequestManagerRetriever getRetriever() {
        return retriever;
    }
}
