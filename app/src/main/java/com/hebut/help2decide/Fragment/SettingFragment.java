package com.hebut.help2decide.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.hebut.help2decide.AboutActivity;
import com.hebut.help2decide.HistoryActivity;
import com.hebut.help2decide.LoginUI.Login;
import com.hebut.help2decide.LoginUI.UserHolder;
import com.hebut.help2decide.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;
import java.util.List;

import decisioncat.Plan;
import decisioncat.Result;
import sqlyog.SqlOperation;
import sqlyog.SqlOperationImpl;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Activity activity=getActivity();
        if(isVisibleToUser){
            SharedPreferences userProjects = activity.getSharedPreferences("SaveSetting", Context.MODE_PRIVATE + Context.MODE_PRIVATE);
            if(userProjects.getBoolean("isFirstGuideSF",true)) {SharedPreferences.Editor editor=userProjects.edit();
                new TapTargetSequence(activity)
                        .targets(
                                TapTarget.forView(loginButton
                                        , "登陆账号", "随处同步您的方案")
                                        .transparentTarget(true)
                                        .dimColor(R.color.qmui_config_color_black)
                                        .targetCircleColor(R.color.qmui_config_color_50_blue))
                        .start();
                editor.putBoolean("isFirstGuideSF",false);
                editor.commit();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    QMUITopBar mTopBar;
    QMUIGroupListView mGroupListView;
    QMUICommonListItemView loginButton;
    QMUICollapsingTopBarLayout mCollapsingTopBarLayout;

    public QMUICommonListItemView getLoginButton() {
        return loginButton;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Activity activity = getActivity();
        mTopBar = activity.findViewById(R.id.Topbar3);

        mCollapsingTopBarLayout=activity.findViewById(R.id.collapsing_topbar_layout);
        mCollapsingTopBarLayout.setTitle("设置");
        /*mTopBar.addRightImageButton(R.drawable.icon_topbar_setting, R.id.topbar_right_setting_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });*/

        //GroupListView
        mGroupListView=activity.findViewById(R.id.groupListView);

        SharedPreferences login_sp = getActivity().getSharedPreferences("userInfo", 0);
        final SharedPreferences.Editor editor =login_sp.edit();
        if(login_sp.getString("USER_NAME","notLogin").equals("notLogin")){
            loginButton = mGroupListView.createItemView(
                    ContextCompat.getDrawable(getContext(), R.drawable.icon_person),
                    "登陆/注册",
                    null,
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        }else {
            loginButton = mGroupListView.createItemView(
                    ContextCompat.getDrawable(getContext(), R.drawable.icon_person),
                    login_sp.getString("USER_NikeNAME",""),
                    "点击登出",
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        }


        loginButton.setId(R.id.loginButton);
        loginButton.setOrientation(QMUICommonListItemView.VERTICAL);

        QMUICommonListItemView aboutButton = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_about),
                "关于我们",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        aboutButton.setOrientation(QMUICommonListItemView.VERTICAL);
        aboutButton.setId(R.id.aboutButton);

        QMUICommonListItemView upButton = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_up),
                "上传方案",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        upButton.setOrientation(QMUICommonListItemView.VERTICAL);
        upButton.setId(R.id.upButton);

        QMUICommonListItemView downButton = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_down),
                "下载方案",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        downButton.setOrientation(QMUICommonListItemView.VERTICAL);
        downButton.setId(R.id.downButton);

        QMUICommonListItemView historyButton = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_history),
                "历史记录",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        historyButton.setOrientation(QMUICommonListItemView.VERTICAL);
        historyButton.setId(R.id.historyButton);

        QMUICommonListItemView modeButton = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_shake),
                "转盘模式",
                "关闭此选项时，为对话模式",
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        modeButton.setOrientation(QMUICommonListItemView.VERTICAL);
        modeButton.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        modeButton.setId(R.id.modeButton);
        modeButton.getSwitch().setChecked(true);
        modeButton.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isWheeled=isChecked;
                if(isChecked){
                    pf.getMessageList().setVisibility(View.INVISIBLE);
                    pf.getWheelSurfView().setVisibility(View.VISIBLE);
                }else {
                    pf.getMessageList().setVisibility(View.VISIBLE);
                    pf.getWheelSurfView().setVisibility(View.INVISIBLE);
                }
            }
        });


        QMUICommonListItemView shakeButton = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_mode),
                "摇一摇",
                "摇晃手机，可以使转盘转动",
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        shakeButton.setOrientation(QMUICommonListItemView.VERTICAL);
        shakeButton.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        shakeButton.setId(R.id.modeButton);
        shakeButton.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShaked=isChecked;
            }
        });
        pf.setSf(this);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                    ((QMUICommonListItemView) v).getSwitch().toggle();
                }
                switch (v.getId()) {
                    case R.id.loginButton:
                        if(loginButton.getText().equals("登陆/注册")) {
                            Intent intent1 = new Intent(getActivity(), Login.class);
                            startActivityForResult(intent1,1);
                        }else {
                            loginButton.setText("登陆/注册");
                            editor.putString("USER_NAME", "notLogin");
                            editor.commit();
                            loginButton.setDetailText(null);
                            Toast.makeText(getActivity(), "已退出登陆", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.upButton:
                        try {
                            String userId= UserHolder.user.getUserId();
                            SqlOperation u = SqlOperationImpl.getSingleInstance();
                            int length=pf.getChoiceAdapter().choices.size();
                            List<Plan> list= new ArrayList<>();
                            for (int i = 0; i <length ; i++) {
                                Plan plan=new Plan();
                                plan.setChoices(pf.getChoiceAdapter().choices.get(i));
                                plan.setUserId(Integer.parseInt(userId));
                                list.add(plan);
                            }
                            u.planAdd(list);
                        }catch (Exception e){
                            e.printStackTrace();                        }

                        Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.downButton:
                        //提示将会覆盖已有的方案，是否继续
                        new QMUIDialog.MessageDialogBuilder(getActivity())
                                .setTitle("提示")
                                .setMessage("将会覆盖已有的方案，是否继续？")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                        try {
                                            String userId= UserHolder.user.getUserId();
                                            SqlOperation u = SqlOperationImpl.getSingleInstance();
                                            Result<List<Plan>> result = u.getPlans(Integer.parseInt(userId));
                                            List<Plan> plans = result.getData();
                                            int length=plans.size();
                                            pf.getChoiceAdapter().choices.clear();
                                            for (int i = 0; i <length ; i++) {
                                                pf.getChoiceAdapter().choices.add(plans.get(i).getChoices());
                                            }
                                            pf.getChoiceAdapter().notifyDataSetChanged();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(getActivity(), "同步成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                        break;
                    case R.id.aboutButton:
                        Intent intent4 = new Intent(getActivity(), AboutActivity.class);
                        startActivity(intent4);
                        break;
                    case  R.id.historyButton:
                        if(UserHolder.user!=null) {
                            Intent intent5 = new Intent(getActivity(), HistoryActivity.class);
                            startActivity(intent5);
                        }else{
                            Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }
        };

        int size = QMUIDisplayHelper.dp2px(getContext(), 16);
        QMUIGroupListView.newSection(getContext())
                .setTitle(" ")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(loginButton, onClickListener)
                .addTo(mGroupListView);

        QMUIGroupListView.newSection(getContext())
                .setTitle(" ")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(upButton, onClickListener)
                .addItemView(downButton, onClickListener)
                .addTo(mGroupListView);

        QMUIGroupListView.newSection(getContext())
                .setTitle(" ")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(modeButton,onClickListener)
                .addItemView(shakeButton, onClickListener)
                .addTo(mGroupListView);

        QMUIGroupListView.newSection(getContext())
                .setTitle(" ")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(historyButton, onClickListener)
                .addTo(mGroupListView);

        QMUIGroupListView.newSection(getContext())
                .setTitle(" ")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(aboutButton, onClickListener)
                .addTo(mGroupListView);
    }

    ProjectFragment pf;

    boolean isShaked=false;
    boolean isWheeled=true;

    public void setPf(ProjectFragment pf) {
        this.pf = pf;
    }
}
