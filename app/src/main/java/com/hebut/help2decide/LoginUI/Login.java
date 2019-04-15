package com.hebut.help2decide.LoginUI;
/**
 * Created by cc on 2019/3/31 0031.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hebut.help2decide.R;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.sql.SQLException;

import decisioncat.CodeStatus;
import decisioncat.Result;
import decisioncat.User;
import sqlyog.SqlOperation;
import sqlyog.SqlOperationImpl;


public class Login extends Activity {                 //登录界面活动


    ImageView imgView;
    EditText accountText;
    EditText pwdText;
    Button loginButton;
    TextView forgetpwdText;
    CheckBox rememberCheck;
    SharedPreferences login_sp;
    QMUITopBar mTopBar;
    private Bitmap bitmap ;//存放裁剪后的头像
    private String fileName;//头像名称
    private String picturePath;//头像路径


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_login);

        //状态栏设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.qmui_config_color_blue));

        mTopBar = findViewById(R.id.Topbar);
        mTopBar.setTitle("登陆");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopBar.addRightTextButton("注册", R.id.topbar_right_register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_Login_to_Register = new Intent() ;    //切换Login Activity至Register Activity
                intent_Login_to_Register.setClass(Login.this,Register.class);
                startActivity(intent_Login_to_Register);
            }
        });
        //通过id找到相应的控件
        imgView = (ImageView) findViewById(R.id.login_view);             //使用ImageView显示logo
        accountText = (EditText) findViewById(R.id.login_edit_account);   //用户名
        pwdText = (EditText) findViewById(R.id.login_edit_pwd);   //密码
        loginButton = (Button) findViewById(R.id.login_btn_login);  //登录按钮
        forgetpwdText = (TextView) findViewById(R.id.login_text_forget_pwd);   //忘记密码
        rememberCheck = (CheckBox) findViewById(R.id.Login_Remember);     //记住密码
        View loginSuccessView=findViewById(R.id.login_success_view);
        TextView loginSuccessShow=(TextView) findViewById(R.id.login_success_show);

        login_sp = getSharedPreferences("userInfo", 0);
        String account=login_sp.getString("USER_NAME", "");
        String pwd =login_sp.getString("PASSWORD", "");

        boolean choseRemember =login_sp.getBoolean("rememberCheck", false);
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if(choseRemember){
            if(!login_sp.getString("USER_NAME","notLogin").equals("notLogin")) {
                accountText.setText(account);
                pwdText.setText(pwd);
                rememberCheck.setChecked(true);
            }else{
                SharedPreferences.Editor t=login_sp.edit();
                t.putBoolean("rememberCheck", false);
                t.commit();
            }
        }
        loginButton.setOnClickListener(clickListener);   //采用OnClickListener方法设置不同按钮按下之后的监听事件
        forgetpwdText.setOnClickListener(clickListener);
    }
    OnClickListener clickListener = new OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    login();      //登录
                    break;
                case R.id.login_text_forget_pwd:                             //忘记密码按钮
                    Intent intent_Login_to_FindPwd = new Intent() ;    //切换Login Activity至FindPwd Activity
                    intent_Login_to_FindPwd.setClass(Login.this,FindPwd.class);
                    startActivity(intent_Login_to_FindPwd);
                    break;
            }
        }
    };

    public void login() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String userName = accountText.getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = pwdText.getText().toString().trim();
            SharedPreferences.Editor editor =login_sp.edit();
            SqlOperation sql= SqlOperationImpl.getSingleInstance();
            Result<User> result = null;
            try {
                result = sql.userLogin(userName,userPwd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(result.getCode()== CodeStatus.SUCCESS){                                             //返回1说明用户名和密码均正确
                User user = result.getData();
                UserHolder.user = user;
                //保存用户名和密码
                editor.putString("USER_NAME", userName);
                editor.putString("PASSWORD", userPwd);
                editor.putString("USER_id",user.getUserId());
                editor.putString("USER_NikeNAME", user.getNickname());

                //是否记住密码
                if(rememberCheck.isChecked()){
                    editor.putBoolean("rememberCheck", true);
                }else{
                    editor.putBoolean("rememberCheck", false);
                }
                editor.commit();
                //登录成功提示*/
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("data_return",user.getNickname());
                setResult(RESULT_OK,intent);
               finish();
            }
            else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();  //登录失败提示
            }
        }
    }
    public boolean isUserNameAndPwdValid() {
        if (accountText.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (pwdText.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
