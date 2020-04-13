package com.ramon.ramonrm.model;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public String YongHuSNo;
    public String Sex;
    public String DeptSNames;
    public String DeptSNos;
    public String YuanGongSNo;
    public String Mobile;
    public String Name;
    public String YongHuLB;

    public String getYongHuSNo() {
        return YongHuSNo;
    }

    public void setYongHuSNo(String yongHuSNo) {
        YongHuSNo = yongHuSNo;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getDeptSNames() {
        return DeptSNames;
    }

    public void setDeptSNames(String deptSNames) {
        DeptSNames = deptSNames;
    }

    public String getDeptSNos() {
        return DeptSNos;
    }

    public void setDeptSNos(String deptSNos) {
        DeptSNos = deptSNos;
    }

    public String getYuanGongSNo() {
        return YuanGongSNo;
    }

    public void setYuanGongSNo(String yuanGongSNo) {
        YuanGongSNo = yuanGongSNo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getYongHuLB() {
        return YongHuLB;
    }

    public void setYongHuLB(String yonghuLB) {
        YongHuLB = yonghuLB;
    }
}
