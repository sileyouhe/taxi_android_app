package com.example.admin.studio_didicar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.lang.StringBuffer;
import java.util.jar.Attributes;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by Admin on 2017/12/24.
 */
public class driver extends AppCompatActivity {

    private Button  Login, clear,instruction;
    private EditText edit_Name, Key,Key2,Nick_call;
    private TextView text_Key2,text_Name;
    private Map<String, String> map;
    private StringBuffer str_num=new StringBuffer();
    private StringBuffer str_object_id=new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "8d9c244e1e2f48e5f9a75c4e00966515");
        setContentView(R.layout.driver);
        Log.i("tag", "3");
        Nick_call = (EditText) findViewById(R.id.edit_nick);
        Key = (EditText) findViewById(R.id.keynum);
        text_Name=findViewById(R.id.text_name);
        edit_Name=findViewById(R.id.edit_name);
        text_Key2=findViewById(R.id.text_key2);
        Key2=findViewById(R.id.keynum2);
        instruction=findViewById(R.id.instruction);
        Login = findViewById(R.id.login);
        clear = findViewById(R.id.clear);
        Login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                if(Login.getText().equals("登陆")){
                    loginMethod();
                }
                else{
                    regeditMethod();
                }
            }
        });
        clear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                edit_Name.setText("");
                Key.setText("");
                Nick_call.setText("");
                Key2.setText("");
            }
        });
        instruction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionMethod();
            }
        });

    }

    private void instructionMethod() {
        if(instruction.getText().equals("前去登陆")){
            instruction.setText("还没有账号,立即注册");
            Key2.setVisibility(View.GONE);
            Login.setText("登陆");
            text_Key2.setVisibility(View.GONE);
            text_Name.setVisibility(View.GONE);
            edit_Name.setVisibility(View.GONE);
        }
        else{
            instruction.setText("前去登陆");
            Key2.setVisibility(View.VISIBLE);
            Login.setText("注册");
            text_Key2.setVisibility(View.VISIBLE);
            text_Name.setVisibility(View.VISIBLE);
            edit_Name.setVisibility(View.VISIBLE);
        }
    }

    private void loginMethod() {
        final String nameString = edit_Name.getText().toString();
        final String nickCallString=Nick_call.getText().toString();
        String keyString = Key.getText().toString();
        if (keyString.equals("")) {
            Toast.makeText(driver.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nickCallString.equals("")) {
            Toast.makeText(driver.this, "登录名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        str_object_id.delete(0,str_object_id.length());
        str_num.delete(0,str_num.length());
        BmobQuery<Driver_table> query = new BmobQuery<Driver_table>();
        query.addWhereEqualTo("nickCall", nickCallString);
        query.findObjects(new FindListener<Driver_table>() {

            @Override
            public void done(List<Driver_table> object, BmobException e) {
                if(e==null){
                    for (int i=0;i<object.size();i++) {
                        Driver_table gameScore=object.get(i);
                        str_object_id.append(gameScore.getObjectId());
                        break;
                    }
                    name_ObjectId(nickCallString);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void name_ObjectId(String nickCall) {
        BmobQuery<Driver_table> bmobQuery = new BmobQuery<Driver_table>();
        bmobQuery.addWhereEqualTo("nickCall", nickCall);
        bmobQuery.findObjects(new FindListener<Driver_table>() {
            @Override
            public void done(List<Driver_table> Driver_tables, BmobException e) {
                // TODO 自动生成的方法存根
                if (Driver_tables.size() > 0) {
                    if (Driver_tables.get(0).getKey().equals(Key.getText().toString())) {
                        Intent intent = new Intent();

                        Log.i("tag",String.valueOf(Driver_tables.get(0).getWallet()));
                        intent.putExtra("name", Driver_tables.get(0).getName());
                        intent.putExtra("nickCall", Driver_tables.get(0).getNickCall());
                        intent.putExtra("money", String.valueOf(Driver_tables.get(0).getWallet()));
                        intent.putExtra("objectId", String.valueOf(Driver_tables.get(0).getObjectId()));
                        intent.putExtra("Is", "true");
                        setResult(2, intent);
                        Toast.makeText(driver.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        Log.i("tag",String.valueOf(Driver_tables.get(0).getWallet()));

                        Intent mainIntent=getIntent();
                        String mLatitude=mainIntent.getStringExtra("latitude");
                        String mlongitude=mainIntent.getStringExtra("longitude");
                        String installationID=BmobInstallationManager.getInstallationId();
                        Driver_table dri=new Driver_table();
                        dri.setMlongitude(mlongitude);
                        dri.setMlatitude(mLatitude);
                        dri.setInstallationID(installationID);
                        dri.setWallet(Driver_tables.get(0).getWallet());

                        Log.i("tag",String.valueOf(Driver_tables.get(0).getWallet()));
                        Log.i("tag",String.valueOf(dri.getWallet()));
                        dri.update(str_object_id.toString(),new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.i("tag","修改成功");
                                }
                                else Log.i("tag","修改失败");
                            }
                        });

                        Log.i("tag",String.valueOf(Driver_tables.get(0).getWallet()));
                        finish();
                    } else {
                        Toast.makeText(driver.this, "密码错误", Toast.LENGTH_SHORT).show();
                        Key.setText("");
                    }
                } else {
                    Toast.makeText(driver.this, "该用户未注册", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void regeditMethod() {
        final String nameString = edit_Name.getText().toString();
        final String nickCallString=Nick_call.getText().toString();
        final String keyString = Key.getText().toString();
        if (nameString.equals("")) {
            Toast.makeText(driver.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nickCallString.equals("")) {
            Toast.makeText(driver.this, "电话不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (keyString.equals("")) {
            Toast.makeText(driver.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!keyString.equals(Key2.getText().toString())){
            Toast.makeText(driver.this, "密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Driver_table> bmobQuery = new BmobQuery<Driver_table>();
        bmobQuery.addWhereEqualTo("nickCall", nickCallString);
        bmobQuery.findObjects(new FindListener<Driver_table>() {
            @Override
            public void done(List<Driver_table> Driver_tables, BmobException e) {
                // TODO 自动生成的方法存根
                if (Driver_tables.size() == 0) {
                    Driver_table pass = new Driver_table();
                    pass.setName(nameString);
                    pass.setKey(keyString);
                    pass.setNickCall(nickCallString);
                    pass.setWallet(100);
                    pass.save();
                    Toast.makeText(driver.this, "注册成功", Toast.LENGTH_SHORT).show();

                    instructionMethod();
                } else {
                    Toast.makeText(driver.this, "该用户已注册", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

