package com.hebut.help2decide.LoginUI;

/**
 * Created by cc on 2019/3/31 0031.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.hebut.help2decide.R;

public class Agreement extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_agreement);
        //状态栏设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.qmui_config_color_blue));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
