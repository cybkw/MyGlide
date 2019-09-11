package com.bkw.glide.resource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GlideLoadDataManager implements GlideLoadData, Runnable {

    private String path;
    private ResponseListener responseListener;
    private Context context;

    @Override
    public Value loadResource(String path, ResponseListener responseListener, Context context) {
        this.context = context;
        this.path = path;
        this.responseListener = responseListener;


        //加载网络图片or SD卡本地图片
        Uri uri = Uri.parse(path);

        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
            //加载网络图片属于耗时操作，所以使用了缓存方案线程池
            new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>()).execute(this);
        }

        //TODO 加载SD卡中的资源文件 返回Value,如用户传递是本地文件路径

        return null;
    }


    @Override
    public void run() {

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        //发起网络请求
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20 * 1000);
            connection.setConnectTimeout(5000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();

                //加载网络图片不使用复用池
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                //取得bitmap周边信息
//                options.inJustDecodeBounds = true;
//                //执行以下代码会计算得到options信息
//                BitmapFactory.decodeStream(new BufferedInputStream(bytes), null, options);
//                int width = options.outWidth;
//                int height = options.outHeight;
//                Log.e("TAG", "width=" + width + ",height=" + height);

//                int width = 1920;
//                int height = 1080;

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

                //BitmapFactory.decodeStream(inputStream, null, options) 复用内存
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


                //切换到主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = Value.getInstance();
                        value.setmBitmap(bitmap);

                        //成功回调
                        responseListener.onSucess(value);
                    }
                });
            } else {
                //加载失败
                responseListener.onFailed(new IllegalArgumentException("请求失败 请求码：" + connection.getResponseCode()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Glide", "inputStream.close() 关闭异常");
            }

        }
    }
}
