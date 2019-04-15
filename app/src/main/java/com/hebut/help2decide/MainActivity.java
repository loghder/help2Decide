package com.hebut.help2decide;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.hebut.help2decide.Fragment.DecisionFragment;
import com.hebut.help2decide.Fragment.ProjectFragment;
import com.hebut.help2decide.Fragment.SettingFragment;
import com.hebut.help2decide.LoginUI.UserHolder;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.next.easynavigation.constant.Anim;
import com.next.easynavigation.view.EasyNavigationBar;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import decisioncat.CodeStatus;
import decisioncat.Result;
import decisioncat.User;
import site.gemus.openingstartanimation.OpeningStartAnimation;
import site.gemus.openingstartanimation.RotationDrawStrategy;
import sqlyog.SqlOperation;
import sqlyog.SqlOperationImpl;

public class MainActivity extends AppCompatActivity implements PermissionRequestUtil.PermissionRequestListener {

    private QMUITopBar mTopBar;
    private EasyNavigationBar navigationBar;
    private Button microphone;

    //底部Tab相关
    private String[] tabText = {"决定", "方案", "设置"};
    //未选中icon
    private int[] normalIcon = {R.drawable.desicion, R.drawable.project, R.drawable.setting};
    //选中时icon
    private int[] selectIcon = {R.drawable.decision_selected, R.drawable.project_selected, R.drawable.setting_selected};
    private List<Fragment> fragments = new ArrayList<>();

    //权限申请相关
    private final String TAG = this.getClass().getName();
    private static final int myCode = 0x11;

    //摇一摇功能
    private ShakeUtils mShakeUtils = null;

    @Override
    protected void onResume() {
        super.onResume();
        mShakeUtils.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShakeUtils.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //权限申请
        //动态申请权限(动态申请的权限需要在AndroidManifest.xml中声明)
        PermissionRequestUtil.judgePermissionOver23(this,
                new String[]{Manifest.permission.INTERNET,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                },
                myCode);

        //科大讯飞
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=5ca002f4");

       //状态栏设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.qmui_config_color_blue));

        //登陆
        SharedPreferences login_sp = getSharedPreferences("userInfo", 0);
        SharedPreferences.Editor editor =login_sp.edit();
        if(login_sp.getString("USER_NAME","notLogin").equals("notLogin")){
            editor.putString("USER_NAME","notLogin");
            editor.commit();
        }else{
            SqlOperation sql= SqlOperationImpl.getSingleInstance();
            Result<User> result = null;
            try {
                result = sql.userLogin(login_sp.getString("USER_NAME",""),login_sp.getString("PASSWORD",""));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(result.getCode()== CodeStatus.SUCCESS) {                                             //返回1说明用户名和密码均正确
                User user = result.getData();
                UserHolder.user = user;
            }
        }

         //tab
        navigationBar = findViewById(R.id.navigationBar);
        DecisionFragment df=new DecisionFragment();
        pf=new ProjectFragment();
        sf=new SettingFragment();
        df.setPf(pf);
        sf.setPf(pf);
        mShakeUtils = new ShakeUtils( this );
        pf.setmShakeUtils(mShakeUtils);
        fragments.add(df);
        fragments.add(pf);
        fragments.add(sf);
        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .smoothScroll(false)  //点击Tab  Viewpager切换是否有动画
                .canScroll(true)    //Viewpager能否左右滑动
                .lineHeight(5)         //分割线高度  默认1px
                .anim(Anim.RubberBand)
                .selectTextColor(Color.parseColor("#1b88ee"))
                .build();
    }


    public void onPermissionReqResult(int reqCode, boolean isAllow) {
        if (reqCode != myCode) {
            return;
        }
        if (isAllow) {
            //被授权
            Toast.makeText(this, "已获取所有权限", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "已获取所有权限");
        } else {
            //被拒绝
            Toast.makeText(this,
                    "App申请的权限已被拒绝,为了能正常使用,请进入设置--权限管理打开相关权限", Toast.LENGTH_LONG).show();
            Log.i(TAG, "App申请的权限已被拒绝,为了能正常使用,请进入设置--权限管理打开相关权限");
        }
    }

    SettingFragment sf;
    ProjectFragment pf;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            sf.getLoginButton().setText(data.getStringExtra("data_return"));
            sf.getLoginButton().setDetailText("点击登出");
        }
    }


    /**
     * 重写这个系统方法
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 请求结果数据集
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //调用封装好的方法
        PermissionRequestUtil.solvePermissionRequest(this, requestCode, grantResults);
    }
}
