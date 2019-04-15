package com.hebut.help2decide.LoginUI;

/**
 * Created by cc on 2019/3/29 0029.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hebut.help2decide.R;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import decisioncat.User;
import sqlyog.SqlOperation;
import sqlyog.SqlOperationImpl;

public class Register extends Activity {
    private ImageView pictureView;
    private TextView uploadText;                        //点击上传
    private EditText accountText;                        //用户名编辑
    private EditText phoneText;                        //手机号编辑
    private EditText pwdText1;                            //密码编辑
    private EditText pwdText2;                       //密码编辑
    private EditText codeText;                       //验证码编辑
    private Button registerButton;                       //确定按钮
    private Button getcodeButton;                       //获取验证码按钮
    private CheckBox agreeButton;                   //是否同意单选框
    private TextView agreementText;                   //协议名称
    boolean isAgree=false;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private ImageView iv_personal_icon;
    QMUITopBar mTopBar;


   // private UserDataManager mUserDataManager;         //用户数据管理类
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_register);

        //状态栏设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.qmui_config_color_blue));

        mTopBar = findViewById(R.id.Topbar);
        mTopBar.setTitle("注册");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pictureView=(ImageView)findViewById(R.id.register_logo);
        uploadText=(TextView)findViewById(R.id.Register_logo_uploading);
        accountText = (EditText) findViewById(R.id.register_edit_name);
        phoneText=(EditText) findViewById(R.id.register_edit_phone);
        pwdText1 = (EditText) findViewById(R.id.register_edit_pwd1);
        pwdText2 = (EditText) findViewById(R.id.register_edit_pwd2);
        codeText = (EditText) findViewById(R.id.register_edit_verify);
        registerButton = (Button) findViewById(R.id.register_btn_register);
        getcodeButton = (Button) findViewById(R.id.register_btn_getcode);
        agreeButton=(CheckBox)findViewById(R.id.register_agreement_radio);
        agreementText=(TextView)findViewById(R.id.register_agreement_text);
        registerButton.setOnClickListener(clickListener);      //注册界面两个按钮的监听事件
        getcodeButton.setOnClickListener(clickListener);
        agreementText.setOnClickListener(clickListener);
        agreeButton.setOnClickListener(clickListener);
        uploadText.setOnClickListener(clickListener);
        pictureView.setOnClickListener(clickListener);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
    View.OnClickListener clickListener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Register_logo_uploading:
                    choosePicture();
                    break;
                case R.id.register_logo:
                    choosePicture();
                    break;
                case R.id.register_btn_getcode:                       //获取验证码按钮的监听事件
                    if(isPhoneValid()) {
                        new TimeCounter(60000, 1000, getcodeButton).start();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendCode();
                            }
                        }).start();
                    }
                    break;
                case R.id.register_btn_register:                     //注册按钮的监听事件,由注册界面返回登录界面
                    Toast.makeText(getApplicationContext(),"注册成功", Toast.LENGTH_SHORT).show();
                    register_check();
                    break;
                case R.id.register_agreement_text:                    //阅读协议按钮
                    Intent intent_Register_to_Agreement = new Intent() ;    //切换至Login Activity
                    intent_Register_to_Agreement.setClass(Register.this, Agreement.class);
                    startActivity(intent_Register_to_Agreement);
                case R.id.register_agreement_radio:                 //是否同意协议
                    isAgree=!isAgree;
            }
        }
    };
    public void register_check() {                                //确认按钮的监听事件
        if (isPhoneValid()&&isUserNameAndPwdValid()&&isVerifyCodedValid()&&isAgree()) {
            String userName = accountText.getText().toString().trim();
            String userPhoneNum=phoneText.getText().toString().trim();
            String userPwd = pwdText1.getText().toString().trim();
            SqlOperation sql= SqlOperationImpl.getSingleInstance();
            try {
                sql.userRegister(userName,userPhoneNum,userPwd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finish();
        }
    }
    public boolean isAgree(){                          //同意协议判断
        if(isAgree==false)
            Toast.makeText(this,"请阅读并同意使用协议", Toast.LENGTH_SHORT).show();
        else;
        return isAgree;
    }
    public boolean isVerifyCodedValid() {                        //验证码判断
        if(codeText.getText().toString().trim().equals("")){
            Toast.makeText(this,"请输入验证码", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(vertifyCode()==true)
            return true;
        else {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public boolean isPhoneValid() {                        //手机号判断
        if(phoneText.getText().toString().trim().equals("")){
            Toast.makeText(this, getString(R.string.phone_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean isUserNameAndPwdValid() {
        String pwd1=pwdText1.getText().toString();
        String pwd2=pwdText2.getText().toString();
        if (accountText.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (pwdText1.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (pwdText2.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (pwd1.equals(pwd2)==false){
            Toast.makeText(this, getString(R.string.pwd_different),
                    Toast.LENGTH_SHORT).show();
        return false;
        }
        return true;
    }
    void sendCode(){
        String phone=phoneText.getText().toString();
        //获取验证码
        try {
           String issend=new vertifyCode().send(phone);
        } catch (Exception e) {}
    }
    boolean vertifyCode(){
        String phone=phoneText.getText().toString();
        String code=codeText.getText().toString();
        String isTrue="0";
        vertifyCode vertifycode=new vertifyCode();
        vertifyCode.Vertify vertify=vertifycode.new Vertify(phone,code);
        //1.执行 Callable 方式，需要 FutureTask 实现类的支持，用于接收运算结果。
        FutureTask<String> result = new FutureTask<>(vertify);
        new Thread(result).start();

        //2.接收线程运算后的结果
        try {
            isTrue = result.get();  //FutureTask 可用于 闭锁 类似于CountDownLatch的作用，在所有的线程没有执行完成之后这里是不会执行的
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if(isTrue.equals("1"))
            return true;
        else
            return false;
    }

    File outputImage;
    public void choosePicture() {                            //选择照片
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "从相册选取", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                       outputImage=new File(getExternalCacheDir(),"output_image.jpg");

                        try{
                            if(outputImage.exists()){
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        if(Build.VERSION.SDK_INT>=24){
                            tempUri= FileProvider.getUriForFile(Register.this,"com.example.cameraalbumtest.fileprovider",outputImage);
                        }
                        else{
                            tempUri= Uri.fromFile(outputImage);
                        }
                       //tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        pictureView.setImageURI(tempUri);
                        break;
                }
            }
        });
       builder.create().show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    //setBitmap(ImageUtil.decodeSampledBitmapFromFilePath(imagePath, 100, 100));
                    startPhotoZoom(saveBitmap(ImageUtil.decodeSampledBitmapFromFilePath(outputImage.getPath(),1000,1000))); // 开始对图片进行裁剪处理
                    pictureView.setImageURI(tempUri);
                   // phoneText.setText(tempUri.toString());
                    break;
                case CHOOSE_PICTURE:
                   //startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    pictureView.setImageURI(data.getData());
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            if (photo != null) {
                                Uri uri = saveBitmap(photo);
                                //发送图片的方法
                                pictureView.setImageBitmap(photo);
                               // sendimage(uri.getPath());
                            } else {
                                Toast.makeText(this,"未获取到拍摄的图片",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this,"照片获取失败请稍后再试",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
    public  void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    private Uri saveBitmap(Bitmap bitmap) {   //保存裁剪后的图片
        File file = new File(Environment.getExternalStorageDirectory() + "/image");
        if (!file.exists())
            file.mkdirs();
        File imgFile = new File(file.getAbsolutePath() + "output_image.jpg");
        if (imgFile.exists())
            imgFile.delete();
        try {
            FileOutputStream outputStream = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
            outputStream.close();
            Uri uri = Uri.fromFile(imgFile);
            return uri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
//Bitmap too large to be uploaded into a texture
//https://blog.csdn.net/ydxlt/article/details/48024017
//https://blog.csdn.net/gaoyongshuo/article/details/75052124
