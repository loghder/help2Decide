package com.hebut.help2decide;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

/**
 * Android6.0动态权限请求工具类
 */
public class PermissionRequestUtil {

    /**
     * 默认构造方法
     */
    private PermissionRequestUtil() {

    }

    /**
     * 定义一个接口，处理回调结果
     */
    public interface PermissionRequestListener {
        /**
         * 处理回调结果的接口方法
         *
         * @param reqCode 请求码
         * @param isAllow 是否被授权(eg:如有N个权限，只要有其中一个权限没有被授予,那么isAllow为false，否则为true)
         */
        void onPermissionReqResult(int reqCode, boolean isAllow);
    }


    /**
     * 处理回调结果
     *
     * @param listener     回调结果接口方法
     * @param reqCode      请求码
     * @param grantResults 权限请求结果集
     */
    public static void solvePermissionRequest(PermissionRequestListener listener, int reqCode, int[] grantResults) {
        if (grantResults == null) {
            return;
        }
        Log.e(null, "grantResults.length=" + grantResults.length);
        boolean isAllow = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isAllow = false;
                break;
            }
        }
        if (listener == null) {
            return;
        }
        //调用接口
        listener.onPermissionReqResult(reqCode, isAllow);
    }

    /**
     * @param context     上下文
     * @param permissions 权限数组
     * @param requestCode 请求码
     * @return 检查是否需要进行权限动态申请 true：有此权限 false：无权限需要申请
     */
    public static boolean judgePermissionOver23(Context context, String[] permissions, int requestCode) {
        try {
            Log.e(null, "Android api=" + Build.VERSION.SDK_INT);
            if (permissions == null || permissions.length == 0) {
                return true;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                ArrayList<String> checkResult = new ArrayList<>();
                for (String permission : permissions) {
                    if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        checkResult.add(permission);
                    }
                }
                if (checkResult.size() == 0) {
                    return true;
                }
                int i = 0;
                String[] reqPermissions = new String[checkResult.size()];
                for (String string : checkResult) {
                    reqPermissions[i] = string;
                    i++;
                }
                ((Activity) context).requestPermissions(reqPermissions, requestCode);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}