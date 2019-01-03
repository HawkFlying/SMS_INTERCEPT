package com.example.hp.message_interception;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 新建一个数据库的类 db (database) 类要继承squliteOpenhelper
 * 再建一个操作数据库的工具类（增删改查）
 * Created by Administrator on 2017/6/29.
 */
public class BlackNumDbHelper extends SQLiteOpenHelper {


    public BlackNumDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    /**
     * 第一次运行时，创建数据库，调用此方法
     */
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table black_num(_id integer primary key autoincrement, number varchar(20) , mode integer);");

    }

    @Override
    /**
     * 当数据库版本不一样，升级数据库时，调用此方法
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
