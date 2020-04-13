package com.ramon.ramonrm;

import com.ramon.ramonrm.model.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class Session {
    public static final String[] StrandTitles = new String[]{"第一流", "第二流", "第三流", "第四流", "第五流", "第六流", "第七流", "第八流", "第九流", "第十流"};

    public static String SessionId;
    public static String ValidMD5;
    public static UserInfo CurrUser;
    public static long LoginTime;
    public static String UserSig;
    public static String ClientId;
    public static ArrayList<UserInfo>ArrayUsers;
    public static HashMap<String,String>[]DataZhanShiPZ;
    public static boolean IsDebug = true;
}
