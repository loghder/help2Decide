package com.hebut.help2decide.Fragment;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.cretin.www.wheelsruflibrary.listener.RotateListener;
import com.cretin.www.wheelsruflibrary.view.WheelSurfView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.hebut.help2decide.R;
import com.hebut.help2decide.ShakeUtils;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cn.jiguang.imui.messages.MessageList;
import cn.jiguang.imui.messages.MsgListAdapter;
import czm.android.support.v7.widget.DividerItemDecoration;
import czm.android.support.v7.widget.GridLayoutManager;
import czm.android.support.v7.widget.RecyclerView;
import czm.android.support.v7.widget.SXRecyclerView;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectFragment extends Fragment {

    public ChoiceAdapter getChoiceAdapter() {
        return choiceAdapter;
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor sp=userProjects.edit();
        int size=mRecyclerView.getCount();
        sp.putInt("length",size);
        for (int i = 0; i < size; i++) {
            String[] strs =  choices.get(i);
            Set<String> set2 = new HashSet<String>(Arrays.asList(strs));
            sp.putStringSet(""+i,set2);
        }
        sp.putBoolean("isFirst",false);
        sp.commit();
    }

    public ProjectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project, container, false);

    }

    private QMUITopBar mTopBar;
    private SXRecyclerView mRecyclerView;
    private ArrayList<String[]> choices=new ArrayList<>();
    private ChoiceAdapter choiceAdapter;
    private SharedPreferences userProjects;
    private MessageList messageList;
    private MsgListAdapter adapter;
    private WheelSurfView wheelSurfView;
    private ShakeUtils mShakeUtils = null;

    public void setmShakeUtils(ShakeUtils mShakeUtils) {
        this.mShakeUtils = mShakeUtils;
    }

    public ArrayList<String[]> getChoices() {
        return choices;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        mTopBar = activity.findViewById(R.id.Topbar2);
        mTopBar.setTitle("方案");
        mTopBar.addRightImageButton(R.drawable.icon_topbar_plus, R.id.topbar_right_plus_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                builder.setTitle("添加方案")
                        .setPlaceholder("例：跑步 游泳")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(builder.getEditText().getWindowToken(), 0);
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                /*CharSequence text = builder.getEditText().getText();*/
                                String text = builder.getEditText().getText().toString();
                                String[] strs=text.split(" |，|,|　");
                                if (text != null && text.length() > 0) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(builder.getEditText().getWindowToken(), 0);
                                    dialog.dismiss();
                                    choiceAdapter.choices.add(strs);
                                    choiceAdapter.notifyDataSetChanged();
                                    mRecyclerView.scrollToPosition(choiceAdapter.getItemCount()-1);
                                } else {
                                    Toast.makeText(getActivity(), "请输入方案", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
            }
        });

        messageList=activity.findViewById(R.id.msg_list);
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        adapter = new MsgListAdapter<>("0", holdersConfig, null);
        messageList.setAdapter(adapter);
        adapter.addToStart(new MyMessage("达芬奇密码还是达芬奇验证码？",2),true);
        adapter.addToStart(new MyMessage("达芬奇验证码",1),true);

        //大转盘！
        messageList.setVisibility(View.INVISIBLE);
        initWheel(activity,new String[]{"1","2","3","4","5","6"},3);

        //方案
        mRecyclerView=activity.findViewById(R.id.sxRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        //准备数据
        userProjects = getActivity().getSharedPreferences("SaveSetting", Context.MODE_PRIVATE + Context.MODE_PRIVATE);

        if(userProjects.getBoolean("isFirst",true)){
            String[][] strs=new String[][]{{"唱歌","跳舞"},{"夏天","冬天"},{"长发","短发"},
                    {"牛奶","豆浆"},{"上海","北京"},{"粤菜","川菜"},
                    {"红色","蓝色"},{"3岁","18岁"},{"眉毛","口红"}};
            for (int i = 0; i < strs.length; i++) {
                choices.add(strs[i]);
            }
        }else{
            int length=userProjects.getInt("length",0);
            for (int i = 0; i < length; i++) {
                Set<String> set3=userProjects.getStringSet(""+i,null);
                String[] strSet = new String[set3.size()];
                set3.toArray(strSet);
                choices.add(strSet);
            }
        }

        choiceAdapter=new ChoiceAdapter(choices,activity);
        mRecyclerView.setAdapter(choiceAdapter);

        mRecyclerView.setOnItemClickListener(new SXRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {

                /*initNormalPopupIfNeed(getContext(), "本喵说：" +neirong );
                mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mNormalPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mNormalPopup.show(view);*/

                if(!sf.isWheeled){
                    int positioner=new Random().nextInt(choices.get(position).length);
                    String neirong=choices.get(position)[positioner];
                    String[] t=choices.get(position);
                    String str=t[0];
                    for (int i = 1; i < t.length; i++) {
                        if(i==t.length-1){
                            str=str+"还是"+t[i]+"?";
                        }else {
                            str = str + "、" + t[i];
                        }
                    }
                    adapter.addToStart(new MyMessage(str,2),true);
                    adapter.addToStart(new MyMessage(neirong,1),true);
                }else{
                    initWheel(activity,choices.get(position),new Random().nextInt(choices.get(position).length));
                }


            }
        });

        mRecyclerView.setOnItemLongClickListener(new SXRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View view, int position, long id) {
                if (mRecyclerView.isInMutiChoiceState()) {
                    Toast.makeText(activity, "该数据项不可选中", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "long click " + position, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        mRecyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        //设置选择模式为多选模式
        mRecyclerView.setChoiceMode(SXRecyclerView.CHOICE_MODE_MULTIPLE);
        //设置多选模式监听器
        mRecyclerView.setMultiChoiceModeListener(new SXRecyclerView.MultiChoiceModeListener() {
            TextView textView;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedItemCount = mRecyclerView.getCheckedItemCount();
                if (checkedItemCount == 0) {
                    textView.setText("选择数据项");
                } else {
                    textView.setText("选择了" + checkedItemCount + "项");
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                textView = new TextView(activity);
                textView.setGravity(Gravity.CENTER);
                textView.setText("选择数据项");
                textView.setTextSize(16);/*
                textView.setTextColor(Color.rgb(255,255,255));
                textView.setBackgroundColor(android.graphics.Color.rgb(28,136,238));*/
                mode.setCustomView(textView);
                activity.getMenuInflater().inflate(R.menu.recycler_multi, menu);
                //返回true才能正常创建ActionMode，从而启动多选模式
                mTopBar.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.confirm) {
                    List<Integer> checkedPos = mRecyclerView.getCheckedItemPositions();
                    if (checkedPos == null || checkedPos.size() == 0) {
                        Toast.makeText(activity, "没有选中任何数据", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i =  checkedPos.size()-1; i >=0; i--) {
                            choiceAdapter.choices.remove((int)checkedPos.get(i));
                        }
                        choiceAdapter.notifyDataSetChanged();
                        Toast.makeText(activity, "选中的数据项已删除", Toast.LENGTH_SHORT).show();
                    }
                    //退出多选模式
                    mRecyclerView.finishMultiChoice();
                }
                mTopBar.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mTopBar.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Activity activity=getActivity();
        if(isVisibleToUser){
            if(userProjects.getBoolean("isFirstGuidePF",true)) {
                SharedPreferences userProjects = activity.getSharedPreferences("SaveSetting", Context.MODE_PRIVATE + Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=userProjects.edit();
                new TapTargetSequence(activity)
                        .targets(
                                TapTarget.forView(mRecyclerView
                                        , "点击已经储存好的方案", "然后旋转轮盘\n")
                                        .transparentTarget(true)
                                        .titleTextSize(20)
                                        .descriptionTextSize(15)
                                        .dimColor(R.color.qmui_config_color_black)
                                .targetCircleColor(R.color.qmui_config_color_50_blue))
                        .start();
                editor.putBoolean("isFirstGuidePF",false);
                editor.commit();
            }
        }
    }

    class ChoiceAdapter extends RecyclerView.Adapter<MyViewHolder> {

        List<String[]> choices;
        LayoutInflater mLayoutInflater;

        public ChoiceAdapter(List<String[]> choices, Context context) {
            this.choices=choices;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String[] t=choices.get(position);
            String str=t[0];
            for (int i = 1; i < t.length; i++) {
                str=str+"，"+t[i];
            }
            holder.mTextView.setText(str);
        }

        @Override
        public int getItemCount() {
            return choices.size();
        }

        //返回false的数据项不可被选中
        @Override
        public boolean isSelectable(int position) {
            return true;
        }
    }

    // 实现Checkable接口，可以简单直接地实现多选标记功能
    class MyViewHolder extends RecyclerView.ViewHolder implements Checkable {

        TextView mTextView;
        ImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.item_tv);
            mImageView = (ImageView) itemView.findViewById(R.id.img_check);
        }

        //选中此ItemView时该方法会回调
        @Override
        public void setChecked(boolean checked) {
            if (checked) {
                //mImageView.setVisibility(View.VISIBLE);
                mTextView.setBackgroundColor(0xFFE0E0E0);
            } else {
                //mImageView.setVisibility(View.GONE);
                mTextView.setBackgroundColor(0xFFFFFFFF);
            }
        }


        @Override
        public boolean isChecked() {
            return false;
        }

        @Override
        public void toggle() {

        }
    }

    private QMUIPopup mNormalPopup;

    private void initNormalPopupIfNeed(final Context context, String result) {
            mNormalPopup = new QMUIPopup(context, QMUIPopup.DIRECTION_NONE);
            TextView textView = new TextView(context);
            textView.setLayoutParams(mNormalPopup.generateLayoutParam(
                    QMUIDisplayHelper.dp2px(context, 250),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            //Log.d("232", "initNormalPopupIfNeed: "+result);
            textView.setLineSpacing(QMUIDisplayHelper.dp2px(context, 4), 1.0f);
            int padding = QMUIDisplayHelper.dp2px(context, 15);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(result);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(context, R.color.qmui_config_color_60_pure_black));
            mNormalPopup.setContentView(textView);
            mNormalPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //mActionButton1.setText(context.getResources().getString(R.string.popup_normal_action_button_text_show));
                }
            });
    }

    void initWheel(final Activity activity,String[] str,final int position){
        //颜色
        Integer[] color = new Integer[]{Color.parseColor("#76b8f5") , Color.parseColor("#a4cff8")
                , Color.parseColor("#d1e7fc") , Color.parseColor("#ffffff")};
        final Integer[] colors =new Integer[str.length*4];
        for (int i = 0; i < colors.length; i++) {
            colors[i]=color[i%color.length];
        }
        //文字
        String[] des = new String[colors.length];
        for (int i = 0; i < des.length; i++) {
            des[i]=str[i%str.length];
        }
        //图标
        List<Bitmap> mListBitmap = new ArrayList<>();
        for ( int i = 0; i < colors.length; i++ ) {
            mListBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.bg));
        }
        //主动旋转一下图片
        //mListBitmap = WheelSurfView.rotateBitmaps(mListBitmap);

        //获取第三个视图
        wheelSurfView =activity.findViewById(R.id.wheelSurfView2);
        WheelSurfView.Builder build = new WheelSurfView.Builder()
                .setmColors(colors)
                .setmDeses(des)
                .setmIcons(mListBitmap)
                .setmType(1)
                .setmTypeNum(colors.length)
                .setmVarTime(600/colors.length)
                .setmTextColor(R.color.qmui_config_color_75_pure_black)
                .setmTextSize(QMUIDisplayHelper.dp2px(activity,20))
                //.setmGoImgRes(R.drawable.node)
                //.setmHuanImgRes(R.drawable.yuanhuan)
                .build();
        wheelSurfView.setConfig(build);

        //设置监听
        wheelSurfView.setRotateListener(new RotateListener() {
            @Override
            public void rotateEnd(int position, String des) {
            }

            @Override
            public void rotating(ValueAnimator valueAnimator) {

            }

            @Override
            public void rotateBefore(ImageView goImg) {
                wheelSurfView.startRotate(new Random().nextInt(colors.length));
            }
        });

        //设置摇一摇监听
        mShakeUtils.setOnShakeListener(new ShakeUtils.OnShakeListener(){
            @Override
            public void onShake() {
                if(sf.isShaked) {
                    wheelSurfView.startRotate(new Random().nextInt(colors.length));
                }
            }
        });

    }

    private SettingFragment sf;

    public void setSf(SettingFragment sf) {
        this.sf = sf;
    }

    public MessageList getMessageList() {
        return messageList;
    }

    public WheelSurfView getWheelSurfView() {
        return wheelSurfView;
    }
}
