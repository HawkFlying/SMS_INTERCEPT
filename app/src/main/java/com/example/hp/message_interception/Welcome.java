package com.example.hp.message_interception;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import util.DataBaseUtil;
import util.MyOpenHelper;


public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        copyDataBaseToPhone();
        MyOpenHelper myOpenHelper=new MyOpenHelper(this);
        SQLiteDatabase db=myOpenHelper.getReadableDatabase();
        db.execSQL("CREATE TABLE if not exists login(_id integer primary key autoincrement, pwd char(20), name char(20))");
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                 Intent intent=new Intent(Welcome.this,Sign_in.class);
                 startActivity(intent);
                 finish();
            }
        };
        timer.schedule(task,3000);

    }
    private void copyDataBaseToPhone() {
       DataBaseUtil util = new DataBaseUtil(this);
        // 判断数据库是否存在
       // Toast.makeText(Welcome.this,"Hello",Toast.LENGTH_LONG).show();
        boolean dbExit = util.checkDataBase();

        if (dbExit) {
            Log.i("tag", "this database is exit.");
        } else {
            try {
                util.copyDataBase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
