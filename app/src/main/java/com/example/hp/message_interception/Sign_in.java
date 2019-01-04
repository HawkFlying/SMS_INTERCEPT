package com.example.hp.message_interception;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import util.MyOpenHelper;


public class Sign_in extends AppCompatActivity implements View.OnClickListener{
EditText editText1;
EditText editText2;
Button button1;
Button button2;
String name;
String psw;
MyOpenHelper myOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin_in);
        editText1=(EditText)findViewById(R.id.et_name);
        editText2=(EditText)findViewById(R.id.et_psw);
        button1=(Button)findViewById(R.id.login);
        button2=(Button)findViewById(R.id.sign_up);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        name = editText1.getText().toString();
        psw = editText2.getText().toString();
        switch (v.getId()) {
            case R.id.login:
            myOpenHelper = new MyOpenHelper(this);
            SQLiteDatabase sqLiteDatabase = myOpenHelper.getReadableDatabase();

                Cursor cursor = sqLiteDatabase.query("login", new String[]{"pwd"}, "name=?", new String[]{name}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String pwd_1 = cursor.getString(0);
                    if (pwd_1.equals(psw)) {
                        Toast.makeText(Sign_in.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Sign_in.this, MainActivity.class);
                        Sign_in.this.startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Sign_in.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(Sign_in.this, "不存在该用户", Toast.LENGTH_SHORT).show();
            }
        break;
            case R.id.sign_up:
            Intent intent=new Intent(Sign_in.this,Sign_up.class);
                startActivity(intent);
              break;
            default:
                break;
        }

    }
    }
