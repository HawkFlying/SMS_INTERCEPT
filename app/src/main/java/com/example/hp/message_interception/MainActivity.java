package com.example.hp.message_interception;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private SimpleAdapter sa;
    private Intent intent;
    private List<Map<String, Object>> data;
    public static final int  REQ_CODE_CONTACT = 1;

    //Button button_re = (Button) findViewById(R.id.button_re);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button_set = (Button) findViewById(R.id.button_set);
        smsReceiver abc = new smsReceiver();
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECEIVE_SMS)
                        !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                    1);
        }//动态申请权限
        button_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity (intent);
            }
        });

        initView();
    }


    private void initView() {
        //得到ListView
        mListView = (ListView) findViewById(R.id.listView);
        data = new ArrayList<Map<String, Object>>();
        //配置适配置器S
        ListView listView = new ListView(this);
        sa = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"names", "mes"}, new int[]{android.R.id.text1,
                android.R.id.text2});
        mListView.setAdapter(sa);
    }

    /**
     * 检查申请短信权限
     */
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //未获取到读取短信权限

            //向系统申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, REQ_CODE_CONTACT);
        } else {
            query();
        }
    }

    /**
     * 点击读取短信
     * @param view
     */
    public void readSMS(View view) {
        checkSMSPermission();
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //判断用户是否，同意 获取短信授权
        if (requestCode == REQ_CODE_CONTACT && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //获取到读取短信权限
            query();
        } else {
            Toast.makeText(this, "未获取到短信权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void query() {

        //读取所有短信
        Uri uri = Uri.parse("content://sms/");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id", "address", "body", "date", "type"}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            int _id;
            String address;
            String body;
            String date;
            int type;
            while (cursor.moveToNext()) {
                address = cursor.getString(1);
                if ("12315".equals(address)){
                    Toast.makeText(MainActivity.this,"已获取黑名单短信内容！", Toast.LENGTH_SHORT).show();

                }
                else if ("10086".equals(address)){
                    Toast.makeText(MainActivity.this,"已获取黑名单短信内容！", Toast.LENGTH_SHORT).show();

                }
                else{
                    continue;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                _id = cursor.getInt(0);
                body = cursor.getString(2);
                date = cursor.getString(3);
                type = cursor.getInt(4);
                map.put("names", address);
                map.put("mes",body);
                Log.i("test", "_id=" + _id + " address=" + address + " body=" + body + " date=" + date + " type=" + type);
                data.add(map);
                //通知适配器发生改变
                sa.notifyDataSetChanged();
                long threadId = cursor.getLong(1);
                resolver.delete(Uri.parse("content://sms/conversations/"
                        + threadId), null, null);
            }
        }
    }
}