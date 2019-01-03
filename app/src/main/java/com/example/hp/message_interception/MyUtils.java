package com.example.hp.message_interception;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2017/6/29.
 */
public class MyUtils {// 加final提高生命周期
    public static void showToast(final Activity ctx, final String msg) {
        // 如果是主线程，直接弹
        String threadName = Thread.currentThread().getName();
        if (threadName.equals("main")) {
            Toast.makeText(ctx, msg,Toast.LENGTH_SHORT).show();

        } else {
            // 如果不是主线程，那么， runOnUiThread();
            // runOnUiThread()是Activity中的方法不是context中的方法
            // 所以不能在context中runOnUiThread() 要把context改为activity
            // activity是context的子类
            ctx.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(ctx, msg,Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public static Boolean isServiceRunning(Context ctx, String serviceName) {

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);


        // 获得正在运行的服务的信息，参数是返回集合的最大值，如果运行的服务为10个，那么集合的size 就是10
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            // 得到所有的正在运行的 服务的服务的 组件名（就是对四大组建的包装 ）
            ComponentName service = runningServiceInfo.service;

            // 得到正在运行的 服务的类名 （ 可以通过这个方法 判断有哪些后台服务在运行）

            // 而判断某个服务是否正在运行 可以抽成一个工具类 更方便使用这个方法
            // 在 MyUtils 里生成这个方法
            String className = service.getClassName();


            //所以只需要判断名称是否一样就可以
            if(className.equals(serviceName)){

                return true;

            }

        }
        return false;
    }
}
