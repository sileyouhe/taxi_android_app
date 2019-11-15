package com.example.admin.studio_didicar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Admin on 2018/1/2.
 */

public class account extends AppCompatActivity implements View.OnClickListener {
    private Button charge,withdraw;
    TextView tv1,tv2;
    EditText edittext;
    private double money=0;
    private StringBuffer str_objectId=new StringBuffer();
    private StringBuffer str_nickCall=new StringBuffer();
    private double str_dou;
    private Integer num=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        Intent intent=getIntent();
        initview();
        Log.i("tag","1");
        num=Integer.parseInt(intent.getStringExtra("num"));
        if(num==1){
            tv1.setText("乘客");
        }
        else{
            tv1.setText("司机");
        }
        Log.i("tag","2");
        str_objectId.delete(0,str_objectId.length());
        str_objectId.append(intent.getStringExtra("objectId"));
        str_nickCall.delete(0,str_nickCall.length());
        str_nickCall.append(intent.getStringExtra("nickCall"));

        Log.i("tag",str_objectId.toString()+" "+num+str_nickCall.toString());

        if(num==2){
            BmobQuery<Driver_table> bmobQuery = new BmobQuery<Driver_table>();
            Log.i("tag","3");
            bmobQuery.addWhereEqualTo("nickCall", str_nickCall.toString());
            Log.i("tag","4");
            bmobQuery.findObjects(new FindListener<Driver_table>() {
                @Override
                public void done(List<Driver_table> list, BmobException e) {
                    tv2.setText(String.valueOf(list.get(0).getWallet()));
                }
            });
        }
        else{
            BmobQuery<Passenger_table> bmobQuery = new BmobQuery<Passenger_table>();
            Log.i("tag","3");
            bmobQuery.addWhereEqualTo("nickCall", str_nickCall.toString());
            Log.i("tag","4");
            bmobQuery.findObjects(new FindListener<Passenger_table>() {
                @Override
                public void done(List<Passenger_table> list, BmobException e) {
                    tv2.setText(String.valueOf(list.get(0).getWallet()));
                }
            });
        }
        Log.i("tag","5");
    }

    private void initview() {
        charge=findViewById(R.id.charge);
        withdraw=findViewById(R.id.withdraw);
        tv1=findViewById(R.id.text1);
        tv2=findViewById(R.id.text2);
        edittext=findViewById(R.id.money);

        charge.setOnClickListener(this);
        withdraw.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.charge:
                if(!chech()){
                    Toast.makeText(account.this,"请正确输入充值量",Toast.LENGTH_SHORT).show();
                    break;
                }
                money=Double.parseDouble(edittext.getText().toString());
                if(num==1)payMethod1();
                else payMethod2();
                break;
            case R.id.withdraw:
                if(!chech()){
                    Toast.makeText(account.this,"请正确输入提现量",Toast.LENGTH_SHORT).show();
                    break;
                }
                money=Double.parseDouble(edittext.getText().toString());
                money=-money;
                if(num==1)payMethod1();
                else payMethod2();
                break;
        }
    }

    private boolean chech() {
        String str=edittext.getText().toString();
        if(str.length()==0)return false;
        int num_midd=0;
        for(int i=0;i<str.length();i++){
            char ch=str.charAt(i);
            if(ch=='.'){
                if(i==0)num_midd+=2;
                num_midd++;
            }
            else if(!Character.isDigit(ch))return false;
            if(num_midd>1)return false;
        }
        return true;
    }

    private void payMethod1() {
        BmobQuery<Passenger_table> bmobQuery = new BmobQuery<Passenger_table>();
        bmobQuery.addWhereEqualTo("nickCall", str_nickCall.toString());
        bmobQuery.findObjects(new FindListener<Passenger_table>() {
            @Override
            public void done(List<Passenger_table> Driver_tables, BmobException e) {
                // TODO 自动生成的方法存根
                if (Driver_tables.size() > 0) {
                    Passenger_table dri = new Passenger_table();
                    dri.setWallet(Driver_tables.get(0).getWallet() + money);
                    str_dou=Driver_tables.get(0).getWallet() + money;
                    dri.update(str_objectId.toString(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("tag", "修改成功");
                            } else Log.i("tag", "修改失败");
                            tv2.setText(String.valueOf(str_dou));
                        }
                    });
                } else {
                    Toast.makeText(account.this, "司机信息查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void payMethod2() {
        BmobQuery<Driver_table> bmobQuery = new BmobQuery<Driver_table>();
        bmobQuery.addWhereEqualTo("nickCall", str_nickCall.toString());
        bmobQuery.findObjects(new FindListener<Driver_table>() {
            @Override
            public void done(List<Driver_table> Driver_tables, BmobException e) {
                // TODO 自动生成的方法存根
                if (Driver_tables.size() > 0) {
                    Driver_table dri = new Driver_table();
                    dri.setWallet(Driver_tables.get(0).getWallet() + money);
                    str_dou=Driver_tables.get(0).getWallet() + money;
                    dri.update(str_objectId.toString(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("tag", "修改成功");
                            } else Log.i("tag", "修改失败");
                            tv2.setText(String.valueOf(str_dou));
                        }
                    });
                } else {
                    Toast.makeText(account.this, "司机信息查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
