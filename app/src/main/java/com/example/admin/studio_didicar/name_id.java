package com.example.admin.studio_didicar;

import cn.bmob.v3.BmobObject;

/**
 * Created by Admin on 2017/12/26.
 */

public class name_id extends BmobObject{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObject_Id() {
        return object_Id;
    }

    public void setObject_Id(String object_Id) {
        this.object_Id = object_Id;
    }

    private String name,object_Id;
}
