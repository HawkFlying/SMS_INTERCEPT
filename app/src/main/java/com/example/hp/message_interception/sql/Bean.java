package com.example.hp.message_interception.sql;

public class Bean {
    public String number;
    public String sms;

    public Bean() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Bean(String number, String sms) {
        super();
        this.number = number;
        this.sms = sms;
    }

    @Override
    public String toString() {
        return "BlackNumBean [number=" + number + ", sms=" + sms + "]";
    }
}
