package com.example.hp.message_interception.sql;

public class Bean {
    public String number;
    public String sms;
    public String tag;

    public Bean() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Bean(String number, String sms,String tag) {
        super();
        this.number = number;
        this.sms = sms;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Bean [number=" + number + ", sms=" + sms + "]";
    }
}
