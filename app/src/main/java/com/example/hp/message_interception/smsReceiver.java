package com.example.hp.message_interception;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;


public class smsReceiver extends BroadcastReceiver {

    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final SmsMessage message = null;
    @Override

    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        abortBroadcast();

        Toast.makeText(context, "收到信息", Toast.LENGTH_SHORT).show();
        if (SMS_RECEIVED_ACTION.equals(action)){

            Bundle bundle = intent.getExtras();
            if (bundle != null){

                Object[] pdus = (Object[])bundle.get("pdus");

                for (Object pdu : pdus){

                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                    Date date = new Date(message.getTimestampMillis());//时间
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String receiveTime = format.format(date);
                    System.out.println("number:" + message.getOriginatingAddress()
                            + "   body:" + message.getDisplayMessageBody() + "  time:"
                            + message.getTimestampMillis());
                    String sender = message.getOriginatingAddress();

                    if ("10086".equals(sender)){
                        Toast.makeText(context, "10086", Toast.LENGTH_SHORT).show();
                        abortBroadcast();

                    }
                    if ("12315".equals(sender)){
                        Toast.makeText(context, "12315", Toast.LENGTH_SHORT).show();
                        abortBroadcast();

                    }

                }

            }

        }

    }



}
