package com.bkw.glide;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

/**
 * 管理RequestManager
 */
public class RequestManagerRetriever {
    public RequestManager get(FragmentActivity activity) {
        return new RequestManager(activity);
    }

    public RequestManager get(Activity activity) {
        return new RequestManager(activity);
    }

    public RequestManager get(Context context) {
        return new RequestManager(context);
    }
}
