package com.example.hp.message_interception;

import android.content.ContentValues;
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


public class Sign_up extends AppCompatActivity implements View.OnClickListener {
    EditText editText1;
    EditText editText2;
    EditText editText3;
    Button button1;
    Button button2;
    String name;
    String psw;
    String repsw;
    MyOpenHelper myOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editText1 = (EditText) findViewById(R.id.et_name_r);
        editText2 = (EditText) findViewById(R.id.et_psw_r);
        editText3 = (EditText) findViewById(R.id.et_repsw);
        button1 = (Button) findViewById(R.id.sign_up_r);
        button2 = (Button) findViewById(R.id.reset);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        name = editText1.getText().toString();
        psw = editText2.getText().toString();
        repsw = editText3.getText().toString();
        switch (v.getId()) {
            case R.id.sign_up_r:
                if (name != null && !name.equals("") && psw != null && !psw.equals("") && repsw != null && !repsw.equals("")) {
                    myOpenHelper = new MyOpenHelper(Sign_up.this);
                    SQLiteDatabase sqLiteDatabase = myOpenHelper.getReadableDatabase();

                    Cursor cursor = sqLiteDatabase.query("login", new String[]{"name"}, "name=?", new String[]{name}, null, null, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        Toast.makeText(Sign_up.this, "该用户名已经被注册", Toast.LENGTH_SHORT).show();
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", name);
                        contentValues.put("pwd", psw);
                        long a = sqLiteDatabase.insert("login", null, contentValues);
                        if (a > 0) {
                            Toast.makeText(Sign_up.this, "注册成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Sign_up.this, Sign_in.class);
                            Sign_up.this.startActivity(intent);
                        } else {
                            Toast.makeText(Sign_up.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(Sign_up.this, "存在为空的输入", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.reset:
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");
             break;
         default:
             break;
        }
    }
}
