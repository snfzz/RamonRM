package com.ramon.ramonrm.model;

import java.util.ArrayList;
import java.util.List;

public class PickerMultiColumnData {
    public PickerMultiColumnData(){
        Title = "";
        Key = "";
        ParentKey = "";
        Level = 0;
        Children = new ArrayList<>();
    }
    public String Title;
    public String Key;
    public String ParentKey;
    public Object Tag;
    public int Level;
    public List<PickerMultiColumnData>Children;
}
