package com.ramon.ramonrm.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ramon.ramonrm.LIMSApplication;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;


public abstract class BaseActivity extends AppCompatActivity {//封装类，所有activity都继承此类

    public Context context;

    // 监听做成静态可以让每个子类重写时都注册相同的一份。
//    private static IMEventListener mIMEventListener = new IMEventListener() {
//        @Override
//        public void onForceOffline() {
//            MethodUtil.showToast("您的帐号已在其它终端登录",LIMSApplication.getInstance());
//            logout(LIMSApplication.getInstance(), false);
//        }
//    };

    public static void logout(Context context, boolean autoLogin) {
//        SharedPreferences shareInfo = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = shareInfo.edit();
//        editor.putBoolean(Constants.AUTO_LOGIN, autoLogin);
//        editor.commit();
//
//        Intent intent = new Intent(context, LoginForDevActivity.class);
//        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(Constants.LOGOUT, true);
//        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        AppManagerUtil.instance().addActivity(this);
    }

    public void sendTIMMessage(String toUser,String text) {
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                toUser);                      //会话对方用户帐号//对方ID
        TIMMessage msg = new TIMMessage();

        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText(text);

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d("Tag:TIM", "addElement failed");
            return;
        }

        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                Log.d("Tag:TIM", "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e("Tag:TIM", "SendMsg ok");
            }
        });
    }

    /**
     * cache parameter
     *
     * @param key
     * @param obj
     */
    public final Object saveParam(String key, Object obj) {
        PreferencesUtil.getInstance(this).saveParam(key, obj);
        return key;
    }

    /**
     * get parameter
     *
     * @param key
     */
    public final Object getParam(String key, Object defaultObject) {
        return PreferencesUtil.getInstance(this).getParam(key, defaultObject);
    }


    /**
     * 进入下一个activity并且带一个动画效果
     */
    public final void startActivity(Intent intent) {
        super.startActivity(intent);
        enterBeginAnimation();
    }

    /**
     * 进入下一个activity并且带一个动画效果
     */
    protected final void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        super.startActivity(intent);
        enterBeginAnimation();
    }

    /**
     * 进入下一个activity并且带一个动画效果
     */
    protected final void enterNextActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
        enterBeginAnimation();
    }

    /**
     * To get to the next activity with an animation
     */
    protected final void enterBeginAnimation() {
        //overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void onBackPressed() {
        AppManagerUtil.instance().finishActivity(this);
    }

    @Override
    public void finish() {
        MethodUtil.hideSoftInputFromActivity(this);
        super.finish();
    }
}
