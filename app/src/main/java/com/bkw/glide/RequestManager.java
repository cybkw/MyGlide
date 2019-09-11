package com.bkw.glide;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.bkw.glide.fragment.ActivityFragmentManager;
import com.bkw.glide.fragment.AppFragmentManager;

public class RequestManager {
    private static final String TAG = "RequestManager";

    private static final String ACTIVITY_NAME = "Activity_NAME";
    private static final String FRAGMENT_ACTIVITY_NAME = "Fragment_Activity_NAME";
    private static final int NEXT_HANDLER_MSG = 99645;

    /**
     * 上下文
     */
    private Context requestManagerContext;

    /**
     * 监听生命周期LifecycleListener的实现类
     */
    private static RequestTargetEngine requestTargetEngine;

    //构造方法代码块，统一实例化RequestTargetEngine
    {
        if (null == requestTargetEngine) {
            requestTargetEngine = new RequestTargetEngine();
        }
    }

    /**
     * V4-V7包
     * 传递的是FragmentActivity,可以管理生命周期（Fragment可以监听到Activity的生命周期）
     *
     * @param fragmentActivity activity是有生命周期的
     */
    private FragmentActivity fragmentActivity;

    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;
        this.fragmentActivity = fragmentActivity;
        //获取Fragment
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (null == fragment) {
            //如果等于null,就创建fragment ，且将监听接口与fragment生命周期关联
            fragment = new AppFragmentManager(requestTargetEngine);
            //添加到supportFragmentManager
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }

        //发送一次Handler，为了让beginTransaction操作不再排队。
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

        // 这样查找fragment2==null，@TODO @3还在排队中,还没有消费
//        Fragment fragment2 = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
//        Log.e(TAG, "RequestManager fragment2=" + fragment2);
    }

    /**
     * android.app包
     * 传递的是Activity,可以管理生命周期（Fragment可以监听到Activity的生命周期）
     *
     * @param activity activity是有生命周期的
     * @todo @2
     */
    public RequestManager(Activity activity) {
        this.requestManagerContext = activity;

        //@todo @3
        //获取Fragment
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if (null == fragment) {
            //将监听接口与fragment生命周期关联
            fragment = new ActivityFragmentManager(requestTargetEngine);
            //添加到管理器
            fragmentManager.beginTransaction().add(fragment, ACTIVITY_NAME).commitAllowingStateLoss();
        }

        //这样查找fragment2==null， @TODO @3还在排队中,还没有消费
        //android.app.Fragment fragment2 = fragmentManager.findFragmentByTag(ACTIVITY_NAME);

        //发送一次Handler
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
    }

    /**
     * 传递的是Context,表示无法管理生命周期,如：Application无法管理
     *
     * @param context
     */
    public RequestManager(Context context) {
        this.requestManagerContext = context;
    }

    /**
     *
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Fragment fragment2 = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
            //不在排队中，所以fragment2有值了
            Log.e(TAG, "Handler : fragment2" + fragment2);
            return false;
        }
    });


    /**
     * 根据图片路径
     * 加载要显示图片资源
     *
     * @param path
     * @return
     */
    public RequestTargetEngine load(String path) {
        //移除handler
        mHandler.removeCallbacksAndMessages(NEXT_HANDLER_MSG);

        //把值传递给资源加载引擎
        requestTargetEngine.loadValueInitAction(path, requestManagerContext);
        return requestTargetEngine;
    }


}
