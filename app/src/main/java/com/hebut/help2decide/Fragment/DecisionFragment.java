package com.hebut.help2decide.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chrischen.waveview.WaveView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.gson.Gson;
import com.hebut.help2decide.Demo;
import com.hebut.help2decide.LoginUI.UserHolder;
import com.hebut.help2decide.R;
import com.hebut.help2decide.Voice;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import decisioncat.Record;
import sqlyog.SqlOperation;
import sqlyog.SqlOperationImpl;


/**
 * A simple {@link Fragment} subclass.
 */
public class DecisionFragment extends Fragment {


    public DecisionFragment() {
        // Required empty public constructor
    }

    private ImageButton microphone;
    private WaveView waveView;
    private TextView textView,textView4;
    private QMUITopBar mTopBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_decision, null);


        return inflater.inflate(R.layout.fragment_decision, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        microphone = activity.findViewById(R.id.microphoneButton);
        waveView = activity.findViewById(R.id.waveView);
        textView = activity.findViewById(R.id.textResult);
        textView4=activity.findViewById(R.id.textView4);
        for (int i = 0; i < 50; i++) {
            waveView.putValue(new Random().nextInt(30));
        }

        microphone.setOnTouchListener(new View.OnTouchListener() {

            float downY = 0, downX = 0, upY = 0, upX = 0;
            SpeechRecognizer mDialog;
            MyListener myListener;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        YoYo.with(Techniques.FadeOutUp)
                                .duration(500).delay(100)
                                .playOn(textView);
                        YoYo.with(Techniques.FadeOutUp)
                                .duration(500).delay(100)
                                .playOn(textView4);
                        downY = event.getRawY();
                        downX = event.getRawX();
                        mDialog = SpeechRecognizer.createRecognizer(view.getContext(), null);
                        myListener = new MyListener();
                        myListener.setPopView(view);
                        //开始听写
                        //2.设置accent、language等参数
                        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
                        //最长静音时长
                        mDialog.setParameter(SpeechConstant.VAD_EOS, "10000");
                        //3.设置回调接口
                        mDialog.startListening(myListener);
                        break;
                    case MotionEvent.ACTION_UP:
                        upY = event.getRawY();
                        upX = event.getRawX();
                        if (Math.abs(upX - downX) > 80 || Math.abs(upY - downY) > 80) {
                            //取消听写搜索
                            mDialog.cancel();
                            textView.setText("");
                        } else {
                            //结束录音
                            textView.setText("");
                            mDialog.stopListening();
                            textView4.setVisibility(View.INVISIBLE);
                        }
                        break;
                }
                return false;
            }
        });

        mTopBar = activity.findViewById(R.id.Topbar);
        mTopBar.setTitle("决定");
        QMUIAlphaImageButton rightImageButton=
        mTopBar.addRightImageButton(R.drawable.icon_topbar_setting, R.id.topbar_right_about_button);
        rightImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
        rightImageButton.setId(R.id.rightImageButton);


        //新手引导
        SharedPreferences userProjects = getActivity().getSharedPreferences("SaveSetting", Context.MODE_PRIVATE + Context.MODE_PRIVATE);
        if(userProjects.getBoolean("isFirstGuideDF",true)) {
            new TapTargetSequence(activity)
                    .targets(
                            TapTarget.forView(activity.findViewById(R.id.microphoneButton)
                                    , "说出一时兴起的决定", "长按期间对手机说出烦恼\n若说话中途想取消\n可以把手指从按钮处平移一段距离再抬起")
                                    .transparentTarget(true)
                                    .outerCircleColor(android.R.color.holo_purple)
                                    .dimColor(R.color.qmui_config_color_black),
                            TapTarget.forView(activity.findViewById(R.id.rightImageButton), "保存为方案", "你也可以在这里再次做决定")
                                    .dimColor(R.color.qmui_config_color_black))
                    .start();
            SharedPreferences.Editor editor=userProjects.edit();
            editor.putBoolean("isFirstGuideDF",false);
            editor.commit();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    ArrayList<String> list;
    class MyListener implements RecognizerListener {
        View popView;

        public void setPopView(View popView) {
            this.popView = popView;
        }


        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            if (!isLast) {
                //解析语音
                final String result = parseVoice(recognizerResult.getResultString());
                textView.setVisibility(View.INVISIBLE);
                textView.setText(result + "?");
                textView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInDown)
                        .duration(500)
                        .playOn(textView);
                //分割字符串
                /*String[] array = result.split("我是|还是|？|，|,|。|呢");
                list = new ArrayList<>();
                for (int i = 0; i < array.length; i++) {
                    if (!array[i].equals(""))
                        list.add(array[i]);
                }*/
                final Map<String,String> map = new HashMap();
                map.put("str", result);
                try {
                    FutureTask<String> future = new FutureTask<>(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return Demo.post(map,"http://39.105.221.212:8080/IDecisionCat/parseSentence");
                        }
                    });
                    new Thread(future).start();
                    String s = future.get();
                    list = new ArrayList<>();
                    String[] array=s.split("\\[|\\]|,");
                    for (int i = 0; i < array.length; i++) {
                        if (!array[i].equals(""))
                            list.add(array[i]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    String temp=list.get(new Random().nextInt(list.size()));
                    initNormalPopupIfNeed(getContext(), "本喵说：" + temp);
                    mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                    mNormalPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                    mNormalPopup.show(popView);
                    String userId= UserHolder.user.getUserId();
                    SqlOperation u = SqlOperationImpl.getSingleInstance();
                    Record r=new Record();
                    r.setDecision(temp);
                    r.setUserId(Integer.parseInt(userId));
                    u.RecordAdd(r);
                }catch (Exception e){
                e.printStackTrace();
            }
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            waveView.putValue(i);
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

        /**
         * 解析语音json
         */
        public String parseVoice(String resultString) {
            Gson gson = new Gson();
            Voice voiceBean = gson.fromJson(resultString, Voice.class);
            StringBuffer sb = new StringBuffer();
            ArrayList<Voice.WSBean> ws = voiceBean.ws;
            for (Voice.WSBean wsBean : ws) {
                String word = wsBean.cw.get(0).w;
                sb.append(word);
            }
            return sb.toString();
        }
    }

    //QMUIPopup
    private QMUIPopup mNormalPopup;

    private void initNormalPopupIfNeed(final Context context, String result) {
            mNormalPopup = new QMUIPopup(context, QMUIPopup.DIRECTION_NONE);
            TextView textView = new TextView(context);
            textView.setLayoutParams(mNormalPopup.generateLayoutParam(
                    QMUIDisplayHelper.dp2px(context, 250),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
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


    private ProjectFragment pf;

    public void setPf(ProjectFragment pf) {
        this.pf = pf;
    }

    private void showBottomSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(getContext())
                .addItem("再次做决定")
                .addItem("保存为方案")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        try{
                            switch (position) {
                                case 0:
                                    if(!textView.getText().equals("")){
                                        String temp=list.get(new Random().nextInt(list.size()));
                                        initNormalPopupIfNeed(getContext(), "本喵说：" + temp);
                                        mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                                        mNormalPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                                        mNormalPopup.show(getActivity().findViewById(R.id.microphoneButton));
                                        String userId= UserHolder.user.getUserId();
                                        SqlOperation u = SqlOperationImpl.getSingleInstance();
                                        Record r=new Record();
                                        r.setDecision(temp);
                                        r.setUserId(Integer.parseInt(userId));
                                        try {
                                            u.RecordAdd(r);;
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        Toast.makeText(getActivity(),"请长按按钮说出你的烦恼",Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 1:
                                    if(!textView.getText().equals("")) {
                                        String[] arr = list.toArray(new String[list.size()]);
                                        pf.getChoiceAdapter().choices.add(arr);
                                        pf.getChoiceAdapter().notifyDataSetChanged();
                                    }else{
                                        Toast.makeText(getActivity(),"请长按按钮说出你的烦恼",Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"请长按按钮说出你的烦恼",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .build().show();
    }
}

