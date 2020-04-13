package com.ramon.ramonrm.charts;

import java.util.ArrayList;
import java.util.List;

//数据对象
public class AccessData {
    //日期
    private String date;
    //访问量
    private Integer nums;

    AccessData(String date, Integer nums) {
        this.date = date;
        this.nums = nums;
    }

    public String getDate() {
        return date;
    }

    public Integer getNums() {
        return nums;
    }

    /**
     * 模拟获取数据
     * @return 此处可按照自定义的数据类型
     */
    public static List<AccessData> getWeekData() {
        List<AccessData> list = new ArrayList<AccessData>(7);
        list.add(new AccessData("09-16", 4));
        list.add(new AccessData("09-17", 7));
        list.add(new AccessData("09-18", 14));
        list.add(new AccessData("09-19", 304));
        list.add(new AccessData("09-20", 66));
        list.add(new AccessData("09-21", 16));
        list.add(new AccessData("09-22", 205));
        return list;
    }
}