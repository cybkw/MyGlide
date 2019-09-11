package com.bkw.glide.fragment;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;

/**
 * AppCompatActivity,FragmentActivity
 */
public class AppFragmentManager extends Fragment {

    private LifecycleCallback lifecycleCallback;

    public AppFragmentManager() {
    }

    @SuppressLint("ValidFragment")
    public AppFragmentManager(LifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideRecycleAction();
        }
    }
}
