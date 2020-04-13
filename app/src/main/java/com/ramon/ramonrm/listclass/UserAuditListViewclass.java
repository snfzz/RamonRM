package com.ramon.ramonrm.listclass;

public class UserAuditListViewclass {
    private String name;//姓名
    private String department;//部门
    private String type;//类型

    public UserAuditListViewclass(String name,String department,String type){
        this.name=name;
        this.department=department;
        this.type=type;
    }

    public String getName(){
        return name;
    }

    public String getDepartment(){
        return department;
    }

    public String getType(){
        return type;
    }
}