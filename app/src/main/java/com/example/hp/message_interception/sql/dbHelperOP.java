package com.example.hp.message_interception.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.example.hp.message_interception.sql.Bean;

import com.example.hp.message_interception.sql.SMSdbHelper;

import java.util.ArrayList;
import java.util.List;

public class dbHelperOP {

        //把数据库创建出来
        private SMSdbHelper dbHelper;

        //black_num表名
        private String table_black_num ="black_sms";

        //单例模式
        //不能让每一个类都能new一个  那样就不是同一个对象了 所以首先构造函数要私有化    以上下文作为参数
        private dbHelperOP(Context ctx){

            //由于数据库只需要调用一次，所以在单例中建出来
            dbHelper= new SMSdbHelper(ctx, "black_sms.db", null, 1);
        }

        //public static 为静态类型  要调用就要有一个静态的变量    为私有的
        private static com.example.hp.message_interception.sql.dbHelperOP instance;


        //既然BlackDao类是私有的  那么别的类就不能够调用    那么就要提供一个public static（公共的  共享的）的方法
        //方法名为getInstance 参数为上下文    返回值类型为BlackDao
//要加上一个synchronized（同步的）
//如果同时有好多线程 同时去调用getInstance()方法  就可能会出现一些创建（new）多个BlackDao的现象  所以要加上synchronized
        public static synchronized com.example.hp.message_interception.sql.dbHelperOP getInstance(Context ctx){

            //就可以判断  如果为空 就创建一个， 如果不为空就还用原来的  这样整个应用程序中就只能获的一个实例
            if(instance == null){
                instance = new dbHelperOP(ctx);
            }
            return  instance;
        }


        //常用方法  增删改查

        /**
         * 添加黑名单  至数据库

         */
        public void addBlackNum(String number,String sms){

            //获得一个可写的数据库的一个引用
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values= new ContentValues();
            values.put("number", number); // KEY 是列名，vlaue 是该列的值
            values.put("sms", sms);// KEY 是列名，vlaue 是该列的值

            // 参数一：表名，参数三，是插入的内容
            // 参数二：只要能保存 values中是有内容的，第二个参数可以忽略
            db.insert(table_black_num, null, values);

        }

        /**
         * 删除黑名单
         * @param number
         */
        public void deleteBlackNum(String number){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //表名  删除的条件
            db.delete(table_black_num, "number = ?", new String[] {number});

        }
    /**
     * //查找 每一个黑名单都有 号码和模式  先把号码和模式封装一个bean
     * 获得所有的黑名单
     * @return
     */
    //分页查询 修改

    public List<Bean> getBlackNumByPage(int pageIndex, int pageSize){
        //public List<BlackNumBean> getAllBlackNum(){
        //创建集合对象
        List<Bean> allBlackNum = new ArrayList<Bean>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //Cursor cursor = db.query(table_black_num, null, null, null, null, null, null);

        //分页查询  修改
        //Cursor cursor = db.rawQuery("select * from black_num limit "+pageSize+"offent"+(pageIndex * pageSize)+";", null);

        //order by _id desc 根据_id倒叙排列   使每次添加的黑名单在下次打开时显示上面     同时每页限制20个
        Cursor cursor = db.rawQuery("select * from black_sms order by _id desc limit "+pageSize+" offset "+(pageIndex*pageSize)+";", null);
        // 返回的 cursor 默认是在第一行的上一行
        //遍历
        while(cursor.moveToNext()){// cursor.moveToNext() 向下移动一行,如果有内容，返回true
            String number = cursor.getString(cursor.getColumnIndex("number"));// 获得number 这列的值
            //获得模式   一共三列   mode为第二列
            String sms = cursor.getString(cursor.getColumnIndex("sms"));

            //将number mode 封装到bean中
            Bean bean = new Bean(number, sms);
            //封装的对象添加到集合中
            allBlackNum.add(bean);
        }

        //关闭cursor
        cursor.close();
        SystemClock.sleep(1000);// 休眠2秒，模拟黑名单比较多，比较耗时的情况
        return allBlackNum;

    };
    /**
     * 获得黑名单的数量
     */
    public int getBlackNumCount(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table_black_num, new String[] {"count(*)"}, null, null, null, null, null);

        cursor.moveToNext();
        int count = cursor.getInt(0);// 仅查了一列，count(*) 这一刻列

        cursor.close();

        return count;

    }



    /**
         * 根据号码，获得拦截模式
         * @param number
         * @return，返回 -1
         * 如果不是黑名单，那么
         */
        public int getMOdeByNumber(String number) {

            int mode =  -1;
            //获得一个可读的数据库的一个引用
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询  表   列   条件
            Cursor cursor = db.query(table_black_num, null, "number = ?", new String []{number}, null, null, null);

            if( cursor.moveToNext()){// 如果查到了，移动成功
                mode = cursor.getInt(cursor.getColumnIndex("mode"));

            }

            cursor.close();
            return mode ;
        }

    }

