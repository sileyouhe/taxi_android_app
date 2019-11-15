package com.example.admin.studio_didicar;

import cn.bmob.v3.BmobObject;

/**
 * Created by Admin on 2017/12/25.
 */


public class Passenger_table extends BmobObject {

    private String name;
    private String key;


    private String nickCall;
    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    private double wallet;

    public String getNickCall() {
        return nickCall;
    }

    public void setNickCall(String nickCall) {
        this.nickCall = nickCall;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

}
