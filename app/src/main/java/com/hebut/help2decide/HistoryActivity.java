package com.hebut.help2decide;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hebut.help2decide.LoginUI.UserHolder;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;
import java.util.List;

import czm.android.support.v7.widget.DividerItemDecoration;
import czm.android.support.v7.widget.LinearLayoutManager;
import czm.android.support.v7.widget.RecyclerView;
import czm.android.support.v7.widget.SXRecyclerView;
import decisioncat.Record;
import decisioncat.Result;
import sqlyog.SqlOperation;
import sqlyog.SqlOperationImpl;

public class HistoryActivity extends AppCompatActivity {

    QMUITopBar mTopBar;
    SXRecyclerView mRecyclerView;
    ArrayList<String> arrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //状态栏设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.qmui_config_color_blue));

        mRecyclerView=findViewById(R.id.sxRecyclerView_history);

        mTopBar = findViewById(R.id.Topbar_history);
        mTopBar.setTitle("历史记录");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView=findViewById(R.id.sxRecyclerView_history);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Result<List<Record>> result=new Result<>();
        //准备数据
        try {
            String userId= UserHolder.user.getUserId();
            SqlOperation u = SqlOperationImpl.getSingleInstance();
            result = u.userSearch(Integer.parseInt(userId));
            }catch (Exception e){
            e.printStackTrace();
    }
            List<Record> list=result.getData();
            int length=list.size();
        arrayList=new ArrayList<>();
        for (int i = 0; i < length; i++) {
            arrayList.add(list.get(length-1-i).getDecision());
        }

        HistoryAdapter historyAdapter=new HistoryAdapter(arrayList,this);
        mRecyclerView.setAdapter(historyAdapter);

        mRecyclerView.setOnItemClickListener(new SXRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
            }
        });
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    class HistoryAdapter extends RecyclerView.Adapter<MyViewHolder> {

        List<String> histories;
        LayoutInflater mLayoutInflater;

        public HistoryAdapter(List<String> histories, Context context) {
            this.histories =histories;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String t= histories.get(position);
            holder.mTextView.setText(t);
        }

        @Override
        public int getItemCount() {
            return histories.size();
        }

        //返回false的数据项不可被选中
        @Override
        public boolean isSelectable(int position) {
            return true;
        }
    }

    // 实现Checkable接口，可以简单直接地实现多选标记功能
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        ImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.item_tv);
            mImageView = (ImageView) itemView.findViewById(R.id.img_check);
        }

    }
}
