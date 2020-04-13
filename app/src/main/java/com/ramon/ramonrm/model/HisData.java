package com.ramon.ramonrm.model;

public class HisData implements Comparable<HisData> {

    public String DataCode;
    public String DataTimeStr;
    public String DataValue;
    public double DataTime;
    @Override
    public int compareTo(HisData o) {
        if (DataTime - o.DataTime <= 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
