package com.example.admin.studio_didicar;

import android.content.Intent;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.route.IndoorRouteLine;

import org.xml.sax.SAXNotRecognizedException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Admin on 2017/12/24.
 */

public class Passenger extends AppCompatActivity {

    private Button Regedit, clear,instruction;
    private TextView textName,textKey2;
    private EditText editName, editKey,editKey2,editNickCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "8d9c244e1e2f48e5f9a75c4e00966515");
        setContentView(R.layout.passenager);

        editName = findViewById(R.id.edit_name);
        textName=findViewById(R.id.text_name);
        editNickCall=findViewById(R.id.edit_nick);
        editKey=findViewById(R.id.keynum);
        textKey2=findViewById(R.id.text_key2);
        editKey2=findViewById(R.id.keynum2);
        Regedit=findViewById(R.id.login);
        instruction=findViewById(R.id.instruction);
        clear=findViewById(R.id.clear);

        Regedit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                if(Regedit.getText().toString().equals("登陆")){
                    loginMethod();
                }
                else{
                    regeditMethod();
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                editKey.setText("");
                editKey2.setText("");
                editName.setText("");
                editNickCall.setText("");
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
        if(instruction.getText().toString().equals("前去登陆")){
            textKey2.setVisibility(View.GONE);
            editKey2.setVisibility(View.GONE);
            textName.setVisibility(View.GONE);
            editName.setVisibility(View.GONE);
            Regedit.setText("登陆");
            instruction.setText("还没有账号,立即注册");
        }
        else{
            textKey2.setVisibility(View.VISIBLE);
            editKey2.setVisibility(View.VISIBLE);
            textName.setVisibility(View.VISIBLE);
            editName.setVisibility(View.VISIBLE);
            Regedit.setText("注册");
            instruction.setText("前去登陆");
        }
    }

    private void regeditMethod() {
        final String nameString = editName.getText().toString();
        final String nickCallString=editNickCall.getText().toString();
        final String keyString = editKey.getText().toString();
        Log.i("tag", nameString + keyString);
        if (nameString.equals("")) {
            Toast.makeText(Passenger.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nickCallString.equals("")) {
            Toast.makeText(Passenger.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (keyString.equals("")) {
            Toast.makeText(Passenger.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!keyString.equals(editKey2.getText().toString())) {
            Toast.makeText(Passenger.this, "密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Passenger_table> bmobQuery = new BmobQuery<Passenger_table>();
        bmobQuery.addWhereEqualTo("nickCall", nickCallString);
        bmobQuery.findObjects(new FindListener<Passenger_table>() {
            @Override
            public void done(List<Passenger_table> Passenger_tables, BmobException e) {
                // TODO 自动生成的方法存根
                if (Passenger_tables.size() == 0) {
                    Passenger_table pass = new Passenger_table();
                    pass.setName(nameString);
                    pass.setKey(keyString);
                    pass.setNickCall(nickCallString);
                    pass.setWallet(100);
                    pass.save();
                    Toast.makeText(Passenger.this, "注册成功", Toast.LENGTH_SHORT).show();
                    instructionMethod();
                } else {
                    Toast.makeText(Passenger.this, "该用户已注册", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginMethod() {
        final String nickCallString=editNickCall.getText().toString();
        final String keyString = editKey.getText().toString();
        if (nickCallString.equals("")) {
            Toast.makeText(Passenger.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (keyString.equals("")) {
            Toast.makeText(Passenger.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Passenger_table> bmobQuery = new BmobQuery<Passenger_table>();
        bmobQuery.addWhereEqualTo("nickCall", nickCallString);
        bmobQuery.findObjects(new FindListener<Passenger_table>() {
            @Override
            public void done(List<Passenger_table> Passenger_tables, BmobException e) {
                // TODO 自动生成的方法存根
                if (Passenger_tables.size() > 0) {
                    if (Passenger_tables.get(0).getKey().equals(keyString)) {
                        Intent intent = new Intent();
                        intent.putExtra("nickCall", nickCallString);
                        intent.putExtra("Is", "true");
                        intent.putExtra("objectId",Passenger_tables.get(0).getObjectId());
                        intent.putExtra("money",String.valueOf(Passenger_tables.get(0).getWallet()));
                        Log.i("tag","数据传入成功");
                        setResult(1, intent);
                        Log.i("tag","数据回传成功");
                        Toast.makeText(Passenger.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Passenger.this, "密码错误", Toast.LENGTH_SHORT).show();
                        editKey.setText("");
                    }
                } else {
                    Toast.makeText(Passenger.this, "该用户未注册", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}