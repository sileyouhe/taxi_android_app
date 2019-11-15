package com.example.admin.studio_didicar;

import cn.bmob.v3.BmobObject;

/**
 * Created by Admin on 2017/12/25.
 */

public class Driver_table extends BmobObject {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMlatitude() {
        return mlatitude;
    }

    public void setMlatitude(String mlatitude) {
        this.mlatitude = mlatitude;
    }

    public String getMlongitude() {
        return mlongitude;
    }

    public void setMlongitude(String mlongitude) {
        this.mlongitude = mlongitude;
    }

    public String getInstallationID() {
        return installationID;
    }

    public void setInstallationID(String installationID) {
        this.installationID = installationID;
    }

    public String getNickCall() {
        return nickCall;
    }

    public void setNickCall(String nickCall) {
        this.nickCall = nickCall;
    }

    private String nickCall;
    private String name;
    private String key;
    private String mlatitude, mlongitude;
    private String installationID;


    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    private double wallet;
}
