package com.hebut.help2decide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.content.Intent.EXTRA_TITLE;

public class AboutActivity extends Activity {

    QMUITopBarLayout mTopBar;
    TextView mVersionTextView;
    QMUIGroupListView mAboutGroupListView;
    TextView mCopyrightTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mTopBar=findViewById(R.id.about_topbar);
        mVersionTextView=findViewById(R.id.version);
        mAboutGroupListView=findViewById(R.id.about_list);
        mCopyrightTextView=findViewById(R.id.copyright);
        //状态栏设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.qmui_config_color_blue));
        initTopBar();

        mVersionTextView.setText(QMUIPackageHelper.getAppVersion(this));

        QMUIGroupListView.newSection(this)
                .addItemView(mAboutGroupListView.createItemView("访问决定喵官网"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://www.baidu.com";
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
                .addItemView(mAboutGroupListView.createItemView("Github"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://github.com/";
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new java.util.Date());
        mCopyrightTextView.setText("© "+ currentYear+" CS164 B467 Team All rights reserved.");
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTopBar.setTitle("关于");
    }




}
