package com.example.admin.studio_didicar;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import cn.bmob.push.PushConstants;

/**
 * Created by Admin on 2017/12/25.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String str=intent.getStringExtra("msg");
            String strEnd=str.substring(10,str.length()-2);
            Log.i("tag",strEnd);

            String from=strEnd.substring(strEnd.indexOf(' ')+1,strEnd.lastIndexOf(' '));
            String to=strEnd.substring(strEnd.lastIndexOf(' ')+1,strEnd.length());
            String name=strEnd.substring(0,strEnd.indexOf(' '));
            Intent mIntent=new Intent("android.content.action.MAIN");
            mIntent.putExtra("name",name);
            mIntent.putExtra("from",from);
            mIntent.putExtra("to",to);
            context.sendBroadcast(mIntent);


//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setTitle("您收到一个乘客的请求");
//            builder.setMessage("起始地："+from+"\n"+"目的地:"+to);
//            builder.show();

        }
    }

}