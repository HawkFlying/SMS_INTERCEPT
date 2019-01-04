package com.example.hp.message_interception;

import android.app.Activity;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hp.message_interception.sql.Bean;
import com.example.hp.message_interception.sql.dbHelperOP;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private LinearLayout llLoading;
    private ProgressBar progressBar;
    private TextView tvDesc;

    private MainActivity.MyAdapter adapter;
    /**
     * 黑名单操作的工具类
     */
    private dbHelperOP dbHelperop;
    //黑名单的集合
    //private List<BlackNumBean> allBlackNum;
    private List<Bean> blackNums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;

        listView = (ListView) findViewById(R.id.li_listView1);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        progressBar = (ProgressBar) findViewById(R.id.pb_progressBar1);
        tvDesc = (TextView) findViewById(R.id.tv_desc1);

        dbHelperop = dbHelperop.getInstance(this);
        fillData();

    }


    private void fillData() {
        //当点击上一页 下一页 是将加载狂显示出来
        llLoading.setVisibility(View.VISIBLE);

        // 进入页面后  加载数据（开子线程）  根据结果 显示页面
        new Thread() {
            public void run() {
                //当第一次加载后  会把数据设置给集合
                //当第二次加载后  会把数据追加在第一次后
                if (blackNums == null) {//第一次
                    //所有的黑名单的集合
                    //	allBlackNum = blackDao.getAllBlackNum();
                    blackNums = dbHelperop.getBlackNumByPage(pageIndex, pageSize);

                } else { ///加载更多  ，数据追加
                    blackNums.addAll(dbHelperop.getBlackNumByPage(pageIndex, pageSize));

                }

                //获取黑名单的数量
                int totalcount = dbHelperop.getBlackNumCount();
                if (totalcount % pageSize == 0) {// 判断是否能整除
                    totalPage = totalcount / pageSize;

                } else {
                    totalPage = totalcount / pageSize + 1;
                }

                //如果集的size 是0 则没有黑名单  ，如果不是零 则有黑名单 则要展示出来、、
                //而子线程不能改变页面   那么就要发送handler信息

                //发送一个空的消息 数据获取完了 可以刷新页面
                handler.sendEmptyMessage(FLUSH_UI);
            }
        }.start();
    }
    /**
     * 当前页面的下标
     */
    private int pageIndex = 0;
    /**
     * 每一页的数量
     */
    private int pageSize = 20;
    /**
     * 总页数
     */
    private int totalPage;
    protected Activity ctx;

    //刷新界面用的  当获得了数据后 就发送一个信息
    private final int FLUSH_UI = 100;

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FLUSH_UI://子线程获得了数据，开始刷新页面

                    // 没有黑名单的情况
                    if (blackNums.size() == 0) {// 没有黑名单的情况
                        progressBar.setVisibility(View.GONE);
                        tvDesc.setText("没有拦截信息");

                    } else {
                        //  有黑名单，关闭加载框，listView 展示黑名单  就是为Listview 设置Adapter
                        llLoading.setVisibility(View.GONE);

                        if (adapter == null) { //第一次加载
                            adapter = new MainActivity.MyAdapter();
                            listView.setAdapter(adapter);

                        } else {
                            //追加数据
                            adapter.notifyDataSetChanged();// 刷新listView 否则仍会从头开始 显示
                        }
                    }
                    break;
            }
        }


    };



    private class MyAdapter extends BaseAdapter {

        @Override
        /**
         * 告诉 listview 有多少个条目
         */
        public int getCount() {
            // TODO Auto-generated method stub
            return blackNums.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        /**
         * 返回第一个条目对应的 view ,
         * 当某个 条目 将要显示在屏幕上时，就会调用getView 方法 ，将该条目创建出来
         * @param position 条目的下标
         * 新建一个list_item_black_num.xml的条目布局
         */
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View view;
            MainActivity.ViewHolder vh;
            if (convertView == null) {

                view = getLayoutInflater().inflate(R.layout.list_item_black_num, null);
                //创建ViewHolder
                vh = new MainActivity.ViewHolder();
                // 找到  子 view
                TextView tvNum = (TextView) view.findViewById(R.id.tv_number_list_item);
                TextView tvsms = (TextView) view.findViewById(R.id.tv_mode_list_item);

                ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete_list_item);
                // 将子view 打包
                vh.tvsms = tvsms;
                vh.tvNum = tvNum;

                vh.ivDelete = ivDelete;
                // 将背包背在view的身上
                view.setTag(vh);

            } else {
                view = convertView;
                //取出背包
                vh = (MainActivity.ViewHolder) convertView.getTag();
            }

            com.example.hp.message_interception.sql.Bean blackNumBean = blackNums.get(position);
            //用取出的背包赋值
            vh.tvNum.setText(blackNumBean.number);
            vh.tvsms.setText("拦截短信");

            //删除黑名单
//			为ivdelete设置一个点击事件
            vh.ivDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //从数据库中删除数据
                    dbHelperop.deleteBlackNum(blackNums.get(position).number);
                    //从集合中删除数据
                    blackNums.remove(position);

                    //刷新页面
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }


    private static class ViewHolder {

        public ImageView ivDelete;
        TextView tvNum;
        TextView tvsms;
    }

    /**
     * 添加黑名单
     *
     * @param v
     */
    public void addBlackNum(View v) {
        // 展示天年黑名单的对话框
        View view = getLayoutInflater().inflate(R.layout.dialog_add_blacknum, null);

        final EditText etInputNum = (EditText) view.findViewById(R.id.et_input_number);

        //获取输入框号码
        String number = etInputNum.getText().toString().trim();

        String sms = "2";
        String tag = "2";

        //将黑名单信息插入数据库
        dbHelperop.addBlackNum(number, sms,tag);

        //显示至listview页面
        //加入集合
        blackNums.add(0, new com.example.hp.message_interception.sql.Bean(number, sms,tag));//要 将黑名单添加至集合的第一位

        //祖传错误   当数据库中没有数据    会直接进入黑名单添加数据  但是adapter在这个时候并没有创建   所以刷新的时候会显示空指针异常
        //所以要先判断adapter 是否为空

        if (adapter == null) {
            adapter = new MainActivity.MyAdapter();
            //
            listView.setAdapter(adapter);

        }
        llLoading.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();//刷新

        //showAddBlackNumDialog();

    }
}
