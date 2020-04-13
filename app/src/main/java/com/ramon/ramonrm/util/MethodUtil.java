package com.ramon.ramonrm.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2016/10/24 0024.
 */

public class MethodUtil {//公共方法类

    //打印Toast
    public static void showToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    // 文件读取方法
    public static String getWenjian(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        StringBuffer buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append((char) reader.read());
        }
        reader.close();
        return buffer.toString();
    }

    /**
     * 根据布局文件id得到view
     *
     * @param layoutId layout id
     * @return
     */
    public static View getView(Context context, int layoutId, ViewGroup group) {
        return View.inflate(context, layoutId, group);
    }

    /**
     * 影藏软键盘
     */
    public static void hideSoftInputFromActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            View view = activity.getCurrentFocus();
            if(view!=null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //用来隐藏小键盘
    public static void hideSoftInputFromFragment(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive( ) ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
        }
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInputFromWindow(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    /**
     * 给电话号码自动添加空格
     */
    public static void phoneAddSpace(final EditText phoneEt) {
        phoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s == null || s.length() == 0)
                    return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9)
                                && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    phoneEt.setText(sb.toString());
                    phoneEt.setSelection(index);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 移除字符串中的所有空格
     */
    public static String removeAllSpace(String str) {
        String tmpstr = str.replace(" ", "");
        return tmpstr;
    }

    /**
     * 判断是否是合格的手机号码
     */
    public static boolean judgePhoneQual(String number) {
        return number
                .matches("^(\\+86-|\\+86|86-|86){0,1}1[3|4|5|7|8]{1}\\d{9}$");
    }


    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
     *
     * @param time
     * @return
     */
    public static String timeLongToString(long time) {
        Date date = new Date(time);
        String strs = "";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            //进行格式化
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;

    }


    //判断时间显示样式
    private String timeStyle(long times) {
        String time = null;
        long timedata = System.currentTimeMillis();

        if (timedata - times < 60000) {
            time = "刚刚发布";
        } else if (timedata - times >= 60000 && timedata - times < 3600000) {
            float timege = (timedata - times) / 60000;
            time = Math.round(timege) + "分钟前发布";
        } else if (timedata - times >= 3600000 && timedata - times < 3600000 * 60) {
            float timege = (timedata - times) / 3600000;
            time = Math.round(timege) + "小时前发布";
        } else {
            time = timeLongToString(timedata - times);
        }


        return time;
    }
}
