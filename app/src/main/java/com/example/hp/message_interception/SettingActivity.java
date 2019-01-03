package com.example.hp.message_interception;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hp.message_interception.BlackDao;
import com.example.hp.message_interception.R;

import java.util.List;

/**
 * 具有固定规则的大量的数据 可以储存在数据库中    所以建库  然后增删改查
 * 可以有三列  _id主键列     number号码列   mode int 0全部拦截   1拦截电话    2拦截短信
 * 新建一个数据库的类 db (database) 类要继承squliteOpenhelper
 */
public class SettingActivity extends AppCompatActivity {

    private ListView listView;
    private LinearLayout llLoading;
    private ProgressBar progressBar;
    private TextView tvDesc;

    private MyAdapter adapter;

    /**
     * 黑名单操作的工具类
     */
    private BlackDao blackDao;
    //黑名单的集合
    //private List<BlackNumBean> allBlackNum;
    private List<com.example.hp.message_interception.BlackNumBean> blackNums;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ctx = this;

        listView = (ListView) findViewById(R.id.li_listView);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        progressBar = (ProgressBar) findViewById(R.id.pb_progressBar);
        tvDesc = (TextView) findViewById(R.id.tv_desc);

        blackDao = BlackDao.getInstance(this);

        // 只有当onCraete 执行完了以后，页面才会显示出来，
        // 在onCreate 方法中不能有耗时的操作，哪怕是 1 秒也不行，会严重影响用户体验,
        // 如果有耗时的操作(加载数据)，一定要开子线程
        fillData();

        //添加监听
        regListener();

    }


    //修改黑名单  当长按某一个条目时 弹出提个修改对话框

    //添加一个长按的监听
    private void regListener() {
        // listView 添加条目长按的监听
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            /**
             * 长按某个条目时，调用此方法 ,
             * 注意，返回  true
             */
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showUpdataBlackNumDialog(position);

                return true;
            }
        });
    }

    /**
     * 显示更新黑名单对话框
     *
     * @param position 长按的条目的下标
     */
    protected void showUpdataBlackNumDialog(int position) {
        //复制添加黑名单的code 修改

        //在java代码中创建对话框
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        dialog = adb.create();

        //从集合中获取封装的对象
        final com.example.hp.message_interception.BlackNumBean blackNumBean = blackNums.get(position);
        //将布局转换为view对
        View view = getLayoutInflater().inflate(R.layout.dialog_updata_blacknum, null);
        final TextView tvBlackNum = (TextView) view.findViewById(R.id.tv_black_number);
        //获取电话号码 并赋值
        tvBlackNum.setText("黑名单：" + blackNumBean.number);
        final CheckBox cbStopCall = (CheckBox) view.findViewById(R.id.cb_stop_call);
        final CheckBox cbStopSms = (CheckBox) view.findViewById(R.id.cb_stop_sms);

        //获取模式  并初始化模式
        switch (blackNumBean.mode) {
            case 0:
                cbStopCall.setChecked(true);
                cbStopSms.setChecked(true);
                break;
            case 1:
                cbStopCall.setChecked(true);
                cbStopSms.setChecked(false);
                break;
            case 2:
                cbStopCall.setChecked(false);
                cbStopSms.setChecked(true);

                break;
        }

        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);

        //为两个按钮 添加点击事件
        //取消按钮
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        //点击确定按钮
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入框号码
                String number = tvBlackNum.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    com.example.hp.message_interception.MyUtils.showToast(ctx, "号码不能为空");
                    return;
                }
                //获取模式
                int newMode = 0;// 初始化模式
                if (cbStopCall.isChecked() && cbStopSms.isChecked()) {
                    newMode = 0;

                } else if (cbStopCall.isChecked() && !cbStopSms.isChecked()) {
                    newMode = 1;
                } else if (!cbStopCall.isChecked() && cbStopSms.isChecked()) {
                    newMode = 2;
                } else {
                    com.example.hp.message_interception.MyUtils.showToast(ctx, "请选择拦截模式");
                    return;
                }

                //更新黑名单
                blackDao.updateBlackNumMode(blackNumBean.number, newMode);

                //显示至listview页面
                blackNumBean.mode = newMode;  //修改数据
                adapter.notifyDataSetChanged();//刷新

                //关闭对话框
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();

    }


    /**
     * 当手指在listview上滑动时
     */
    private void resListener() {
        //为ListView设置一个滑动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            /**
             * 当滑动状态发生改变时
             */
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_IDLE;  //空闲状态     idle空闲
//				OnScrollListener.SCROLL_STATE_FLING;// 快速滑东，  没有触摸  但在滑动
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL; // 触摸并滑动

                //在空闲的时候 判断屏幕最后一个条目，是否是listvist 的最后一个条目， 如果是  说命该加载项更多的数据了
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //获得可见的最后一个条目的下表
                    int lastVisiblePosition = listView.getLastVisiblePosition();

                    if (lastVisiblePosition == adapter.getCount() - 1) {// 看到最后一个条目了

                        if (pageIndex < totalPage - 1) {
                            //当前页面的下标 加一
                            pageIndex++;
                            fillData();

                        } else {
                            com.example.hp.message_interception.MyUtils.showToast(ctx, "没有数据了");

                        }
                    }
                }
            }

            @Override
            /**
             * 滑动时不断调用此方法
             */
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

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
                    blackNums = blackDao.getBlackNumByPage(pageIndex, pageSize);

                } else { ///加载更多  ，数据追加
                    blackNums.addAll(blackDao.getBlackNumByPage(pageIndex, pageSize));

                }

                //获取黑名单的数量
                int totalcount = blackDao.getBlackNumCount();
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

            ;
        }.start();
    }


    //黑名单数据加载的方式二   分批加载
    //生成变量
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
                        tvDesc.setText("没有黑名单,请添加几个吧");

                    } else {
                        //  有黑名单，关闭加载框，listView 展示黑名单  就是为Listview 设置Adapter
                        llLoading.setVisibility(View.GONE);

                        if (adapter == null) { //第一次加载
                            adapter = new MyAdapter();
                            listView.setAdapter(adapter);

                        } else {
                            //追加数据
                            adapter.notifyDataSetChanged();// 刷新listView 否则仍会从头开始 显示
                        }
                    }
                    break;
            }
        }

        ;
    };


    /**
     * listview 如果不优化可能出现的问题：
     * 一： getView 方法 大量调用，创建大量的对象，造成内存的浪费，甚至是 OOM 异常
     * 二：如果getview 方法执行的时间过长，超过 150 毫秒，用户就会明显的感觉到卡顿现象
     *
     * @author Administrator
     *         <p/>
     *         优化的目标：创建尽可能少的对象，执行getView的时间尽可能短
     *         <p/>
     *         listview 优化一：复用convertView
     *         优化的结果：当convertView 不为空时，不再创建新的 view 对象，省略了
     *         getLayoutInflater().inflate(R.layout.list_item_black_num, null);
     *         而  inflate 是一个比较耗时的动作。
     *         <p/>
     *         listView 优化二：使用ViewHolder
     *         优化的结果：当convertView 不为空时 ，通过 convertView 身上的背包，获得他的子view 然后，为子view赋值，
     *         从而，省略了 findViewById 这个方法
     */
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
            ViewHolder vh;
            if (convertView == null) {

                view = getLayoutInflater().inflate(R.layout.list_item_black_num, null);
                //创建ViewHolder
                vh = new ViewHolder();
                // 找到  子 view
                TextView tvNum = (TextView) view.findViewById(R.id.tv_number_list_item);
                TextView tvMode = (TextView) view.findViewById(R.id.tv_mode_list_item);

                ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete_list_item);
                // 将子view 打包
                vh.tvMode = tvMode;
                vh.tvNum = tvNum;

                vh.ivDelete = ivDelete;
                // 将背包背在view的身上
                view.setTag(vh);

            } else {
                view = convertView;
                //取出背包
                vh = (ViewHolder) convertView.getTag();
            }

            com.example.hp.message_interception.BlackNumBean blackNumBean = blackNums.get(position);
            //用取出的背包赋值
            vh.tvNum.setText(blackNumBean.number);

            switch (blackNumBean.mode) {
                case 0:
                    vh.tvMode.setText("全部拦截");
                    break;
                case 1:
                    vh.tvMode.setText("拦截电话");
                    break;
                case 2:
                    vh.tvMode.setText("拦截短信");
                    break;
            }

            //删除黑名单
//			为ivdelete设置一个点击事件
            vh.ivDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //从数据库中删除数据
                    blackDao.deleteBlackNum(blackNums.get(position).number);
                    //从集合中删除数据
                    blackNums.remove(position);

                    //刷新页面
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }

    //优化二   先声明一个临时的辅助类  然后有几个子view  声明几个成员变量
    private static class ViewHolder {

        public ImageView ivDelete;
        TextView tvNum;
        TextView tvMode;
    }

    /**
     * 添加黑名单
     *
     * @param v
     */
    public void addBlackNum(View v) {
        // 展示天年黑名单的对话框
        showAddBlackNumDialog();

    }

    private AlertDialog dialog;

    private void showAddBlackNumDialog() {
        //在java代码中创建对话框
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        dialog = adb.create();

        //将布局转换为view对象
        View view = getLayoutInflater().inflate(R.layout.dialog_add_blacknum, null);

        final EditText etInputNum = (EditText) view.findViewById(R.id.et_input_number);
        final CheckBox cbStopCall = (CheckBox) view.findViewById(R.id.cb_stop_call);
        final CheckBox cbStopSms = (CheckBox) view.findViewById(R.id.cb_stop_sms);

        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);

        //为两个按钮 添加点击事件
        //取消按钮
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        //点击确定按钮
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入框号码
                String number = etInputNum.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    com.example.hp.message_interception.MyUtils.showToast(ctx, "号码不能为空");
                    return;
                }
                //先初始化mode
                int mode = 0;
                if (cbStopCall.isChecked() && cbStopSms.isChecked()) {
                    mode = 0;

                } else if (cbStopCall.isChecked() && !cbStopSms.isChecked()) {
                    mode = 1;
                } else if (!cbStopCall.isChecked() && cbStopSms.isChecked()) {
                    mode = 2;
                } else {
                    com.example.hp.message_interception.MyUtils.showToast(ctx, "请选择拦截模式");
                    return;
                }

                //将黑名单信息插入数据库
                blackDao.addBlackNum(number, mode);

                //显示至listview页面
                //加入集合
                blackNums.add(0, new com.example.hp.message_interception.BlackNumBean(number, mode));//要 将黑名单添加至集合的第一位

                //祖传错误   当数据库中没有数据    会直接进入黑名单添加数据  但是adapter在这个时候并没有创建   所以刷新的时候会显示空指针异常
                //所以要先判断adapter 是否为空

                if (adapter == null) {
                    adapter = new MyAdapter();
                    //
                    listView.setAdapter(adapter);

                }
                llLoading.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();//刷新

                //关闭对话框
                dialog.dismiss();

            }
        });
        dialog.setView(view);
        dialog.show();

    }
}