package com.example.hp.message_interception;

/**
 * Created by Administrator on 2017/6/29.
 */
public class BlackNumBean {
    public String number;
    public int mode;

    public BlackNumBean() {
        super();
        // TODO Auto-generated constructor stub
    }

    public BlackNumBean(String number, int mode) {
        super();
        this.number = number;
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackNumBean [number=" + number + ", mode=" + mode + "]";
    }
}
