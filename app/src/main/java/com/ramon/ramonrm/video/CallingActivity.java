package com.ramon.ramonrm.video;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.video.sdkadapter.AudioConfig;
import com.ramon.ramonrm.video.sdkadapter.ConfigHelper;
import com.ramon.ramonrm.video.sdkadapter.PkConfig;
import com.ramon.ramonrm.video.sdkadapter.TRTCCloudManager;
import com.ramon.ramonrm.video.sdkadapter.TRTCCloudManagerListener;
import com.ramon.ramonrm.video.sdkadapter.TRTCRemoteUserManager;
import com.ramon.ramonrm.video.sdkadapter.VideoConfig;
import com.ramon.ramonrm.video.videolayout.TRTCVideoLayoutManager;
import com.ramon.ramonrm.webapi.APIConfig;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCStatistics;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CallingActivity extends BaseActivity implements View.OnClickListener, TRTCCloudManagerListener, TRTCCloudManager.IView, TRTCRemoteUserManager.IView, ITXLivePlayListener {

    public static CallingActivity instance;
    public static CallingActivity getInstance(){
        return instance;
    }

    public static boolean isCalling = false;

    private Ringtone mRingtone;
    private String fromUser;
    private String fromName;
    private int roomId;
    private String  mMainUserId     = ""; //主播id

    private TextView lblName,lblInfo,lblJuJue,lblJieTing;
    private ImageView imgHeader,imgJuJue,imgJieTing;

    private RelativeLayout rlayCreateRoom,rlayRoom;

    private TRTCCloud mTRTCCloud;                 // SDK 核心类
    private TRTCCloudDef.TRTCParams         mTRTCParams;                // 进房参数
    private int                             mAppScene;                  // 推流模式，文件头第一点注释
    private TRTCCloudManager mTRTCCloudManager;          // 提供核心的trtc
    private TRTCRemoteUserManager mTRTCRemoteUserManager;
    /**
     * 控件布局相关
     */
    private TRTCVideoLayoutManager mTRTCVideoLayout;           // 视频 View 的管理类，包括：预览自身的 View、观看其他主播的 View。

    private boolean              mReceivedVideo            = true;
    private boolean              mReceivedAudio            = true;
    private int                  mVolumeType               = 0;
    private boolean              mIsAudioHandFreeMode      = false;
    private boolean              mIsCustomCaptureAndRender = false;

    public CallingActivity() {
        super();
        instance = this;
        isCalling = true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calling);
        fromUser = getIntent().getStringExtra("fromUser");
        fromName = getIntent().getStringExtra("fromUserName");
        roomId = getIntent().getIntExtra("roomID",0);
        int role = getIntent().getIntExtra("role",0);
        mMainUserId = Session.CurrUser.YongHuSNo;
        mTRTCParams = new TRTCCloudDef.TRTCParams(APIConfig.SDKAppId, fromUser, Session.UserSig, roomId, "", "");
        mTRTCParams.role = role;
        initTRTCSDK();
        initView();
        try {
            defaultCallMediaPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("****",""+e);
        }
    }

    public void defaultCallMediaPlayer() throws Exception {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mRingtone = RingtoneManager.getRingtone(this, notification);
        mRingtone.play();
    }

    private void initView(){
        lblName = (TextView)findViewById(R.id.activity_calling_lblname);
        lblInfo = (TextView)findViewById(R.id.activity_calling_lblinfo);
        imgHeader = (ImageView)findViewById(R.id.activity_calling_imgheader);
        lblJuJue = (TextView)findViewById(R.id.activity_calling_lbljujue);
        lblJuJue.setOnClickListener(this);
        imgJuJue = (ImageView)findViewById(R.id.activity_calling_imgjujue);
        imgJuJue.setOnClickListener(this);
        lblJieTing = (TextView)findViewById(R.id.activity_calling_lbljieting);
        lblJieTing.setOnClickListener(this);
        imgJieTing = (ImageView)findViewById(R.id.activity_calling_imgjieting);
        imgJieTing.setOnClickListener(this);

        rlayCreateRoom = (RelativeLayout)findViewById(R.id.activity_calling_rlaycreateroom);
        rlayCreateRoom.setVisibility(View.VISIBLE);
        rlayRoom = (RelativeLayout)findViewById(R.id.activity_calling_rlayroom);
        rlayRoom.setVisibility(View.GONE);


        // 界面视频 View 的管理类
        mTRTCVideoLayout = (TRTCVideoLayoutManager) findViewById(R.id.trtc_video_view_layout);
        mTRTCVideoLayout.setMySelfUserId(mTRTCParams.userId);
    }


    /**
     * 初始化 SDK
     */
    private void initTRTCSDK() {
        Log.e("Tag", "enter initTRTCSDK ");

        mTRTCCloud = TRTCCloud.sharedInstance(this);
        mTRTCCloudManager = new TRTCCloudManager(this, mTRTCCloud, mTRTCParams, mAppScene);
        mTRTCCloudManager.setViewListener(this);
        mTRTCCloudManager.setTRTCListener(this);
        mTRTCCloudManager.initTRTCManager(mIsCustomCaptureAndRender, mReceivedAudio, mReceivedVideo);
        mTRTCCloudManager.setSystemVolumeType(mVolumeType);
        mTRTCCloudManager.enableAudioHandFree(mIsAudioHandFreeMode);
        mTRTCRemoteUserManager = new TRTCRemoteUserManager(mTRTCCloud, this, mIsCustomCaptureAndRender);
        mTRTCRemoteUserManager.setMixUserId(mTRTCParams.userId);
        Log.e("Tag", "exit initTRTCSDK ");
    }


    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_calling_imgjujue || vId == R.id.activity_calling_lbljujue) {
                //拒接
                if (mRingtone!=null){
                    mRingtone.stop();
                }
                sendTIMMessage(fromUser, "video:over:");
                over("挂断");
                AppManagerUtil.instance().finishActivity(CallingActivity.this);
            } else if (vId == R.id.activity_calling_imgjieting || vId == R.id.activity_calling_lbljieting) {
                //接听
                rlayCreateRoom.setVisibility(View.GONE);
                rlayRoom.setVisibility(View.VISIBLE);
                if (mRingtone!=null){
                    mRingtone.stop();
                }
                enterRoom();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void enterRoom() {
        VideoConfig videoConfig = ConfigHelper.getInstance().getVideoConfig();
        AudioConfig audioConfig = ConfigHelper.getInstance().getAudioConfig();
        mTRTCCloudManager.setSystemVolumeType(mVolumeType);
        // 开启本地预览
        startLocalPreview();
        videoConfig.setEnableVideo(true);
        // 如果当前角色是主播, 才能打开本地摄像头
        videoConfig.setPublishVideo(true);
        // 开始采集声音
        mTRTCCloudManager.startLocalAudio();
        audioConfig.setEnableAudio(true);

        // 耳返
        mTRTCCloudManager.enableEarMonitoring(audioConfig.isEnableEarMonitoring());
        mTRTCCloudManager.enterRoom();
    }

    /**
     * 退房
     */
    private void exitRoom() {
        stopLocalPreview();
        // 退房设置为非录制状态
        ConfigHelper.getInstance().getAudioConfig().setRecording(false);
        mTRTCCloudManager.exitRoom();
    }

    private void startLocalPreview() {
        TXCloudVideoView localVideoView = mTRTCVideoLayout.allocCloudVideoView(mTRTCParams.userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
        if (!mIsCustomCaptureAndRender) {
            // 开启本地预览
            mTRTCCloudManager.setLocalPreviewView(localVideoView);
            mTRTCCloudManager.startLocalPreview();
        }
    }

    private void stopLocalPreview() {
        if (!mIsCustomCaptureAndRender) {
            // 关闭本地预览
            mTRTCCloudManager.stopLocalPreview();
        }
        mTRTCVideoLayout.recyclerCloudViewView(mTRTCParams.userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
    }

    private void onVideoChange(String userId, int streamType, boolean available) {
        if (available) {
            // 首先需要在界面中分配对应的TXCloudVideoView
            TXCloudVideoView renderView = mTRTCVideoLayout.findCloudViewView(userId, streamType);
            if (renderView == null) {
                renderView = mTRTCVideoLayout.allocCloudVideoView(userId, streamType);
            }
            // 启动远程画面的解码和显示逻辑
            if (renderView != null) {
                mTRTCRemoteUserManager.remoteUserVideoAvailable(userId, streamType, renderView);
            }
            if (!userId.equals(mMainUserId)) {
                mMainUserId = userId;
            }
        } else {
            mTRTCRemoteUserManager.remoteUserVideoUnavailable(userId, streamType);
            if (streamType == TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB) {
                // 辅路直接移除画面，不会更新状态。主流需要更新状态，所以保留
                mTRTCVideoLayout.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB);
            }
        }
        if (streamType == TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG) {
            // 根据当前视频流的状态，展示相关的 UI 逻辑。
            mTRTCVideoLayout.updateVideoStatus(userId, available);
        }
        mTRTCRemoteUserManager.updateCloudMixtureParams();
    }

    /**
     * 加入房间回调
     *
     * @param elapsed 加入房间耗时，单位毫秒
     */
    @Override
    public void onEnterRoom(long elapsed) {
        if (elapsed >= 0) {
            // 发起云端混流
            mTRTCRemoteUserManager.updateCloudMixtureParams();
        } else {
            Toast.makeText(this, "加入房间失败", Toast.LENGTH_SHORT).show();
            exitRoom();
        }
    }

    @Override
    public void onExitRoom(int reason) {
    }

    /**
     * ERROR 大多是不可恢复的错误，需要通过 UI 提示用户
     * 然后执行退房操作
     *
     * @param errCode   错误码 TXLiteAVError
     * @param errMsg    错误信息
     * @param extraInfo 扩展信息字段，个别错误码可能会带额外的信息帮助定位问题
     */
    @Override
    public void onError(int errCode, String errMsg, Bundle extraInfo) {
        Toast.makeText(this, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_SHORT).show();
        // 执行退房
        exitRoom();
        finish();
    }

    /**
     * 有新的主播{@link TRTCCloudDef#TRTCRoleAnchor}加入了当前视频房间
     * 该方法会在主播加入房间的时候进行回调，此时音频数据会自动拉取下来，但是视频需要有 View 承载才会开始渲染。
     * 为了更好的交互体验，Demo 选择在 onUserVideoAvailable 中，申请 View 并且开始渲染。
     * 您可以根据实际需求，选择在 onUserEnter 还是 onUserVideoAvailable 中发起渲染。
     *
     * @param userId 用户标识
     */
    @Override
    public void onUserEnter(String userId) {
    }

    /**
     * 主播{@link TRTCCloudDef#TRTCRoleAnchor}离开了当前视频房间
     * 主播离开房间，要释放相关资源。
     * 1. 释放主画面、辅路画面
     * 2. 如果您有混流的需求，还需要重新发起混流，保证混流的布局是您所期待的。
     *
     * @param userId 用户标识
     * @param reason 离开原因代码，区分用户是正常离开，还是由于网络断线等原因离开。
     */
    @Override
    public void onUserExit(String userId, int reason) {
        mTRTCRemoteUserManager.removeRemoteUser(userId);
        // 回收分配的渲染的View
        mTRTCVideoLayout.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
        mTRTCVideoLayout.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB);
        // 更新混流参数
        mTRTCRemoteUserManager.updateCloudMixtureParams();
    }

    /**
     * 若当对应 userId 的主播有上行的视频流的时候，该方法会被回调，available 为 true；
     * 若对应的主播通过{@link TRTCCloud#muteLocalVideo(boolean)}，该方法也会被回调，available 为 false。
     * Demo 在收到主播有上行流的时候，会通过{@link TRTCCloud#startRemoteView(String, TXCloudVideoView)} 开始渲染
     * Demo 在收到主播停止上行的时候，会通过{@link TRTCCloud#stopRemoteView(String)} 停止渲染，并且更新相关 UI
     *
     * @param userId    用户标识
     * @param available 画面是否开启
     */
    @Override
    public void onUserVideoAvailable(String userId, boolean available) {
        onVideoChange(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG, available);
    }


    /**
     * 是否有辅路上行的回调，Demo 中处理方式和主画面的一致 {link TRTCCloudListenerImpl # onUserVideoAvailable(String, boolean)}
     *
     * @param userId    用户标识
     * @param available 屏幕分享是否开启
     */
    @Override
    public void onUserSubStreamAvailable(final String userId, boolean available) {
        onVideoChange(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB, available);
    }

    /**
     * 是否有音频上行的回调
     * <p>
     * 您可以根据您的项目要求，设置相关的 UI 逻辑，比如显示对端闭麦的图标等
     *
     * @param userId    用户标识
     * @param available true：音频可播放，false：音频被关闭
     */
    @Override
    public void onUserAudioAvailable(String userId, boolean available) {
    }

    /**
     * 视频首帧渲染回调
     * <p>
     * 一般客户可不关注，专业级客户质量统计等；您可以根据您的项目情况决定是否进行统计或实现其他功能。
     *
     * @param userId     用户 ID
     * @param streamType 视频流类型
     * @param width      画面宽度
     * @param height     画面高度
     */
    @Override
    public void onFirstVideoFrame(String userId, int streamType, int width, int height) {
        Log.i("Tag", "onFirstVideoFrame: userId = " + userId + " streamType = " + streamType + " width = " + width + " height = " + height);
    }

    /**
     * 音量大小回调
     * <p>
     * 您可以用来在 UI 上显示当前用户的声音大小，提高用户体验
     *
     * @param userVolumes 所有正在说话的房间成员的音量（取值范围0 - 100）。即 userVolumes 内仅包含音量不为0（正在说话）的用户音量信息。其中本地进房 userId 对应的音量，表示 local 的音量，也就是自己的音量。
     * @param totalVolume 所有远端成员的总音量, 取值范围 [0, 100]
     */
    @Override
    public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume) {
        for (int i = 0; i < userVolumes.size(); ++i) {
            mTRTCVideoLayout.updateAudioVolume(userVolumes.get(i).userId, userVolumes.get(i).volume);
        }
    }

    /**
     * SDK 状态数据回调
     * <p>
     * 一般客户无需关注，专业级客户可以用来进行统计相关的性能指标；您可以根据您的项目情况是否实现统计等功能
     *
     * @param statics 状态数据
     */
    @Override
    public void onStatistics(TRTCStatistics statics) {
    }

    /**
     * 跨房连麦会结果回调
     *
     * @param userID
     * @param err
     * @param errMsg
     */
    @Override
    public void onConnectOtherRoom(final String userID, final int err, final String errMsg) {
        PkConfig pkConfig = ConfigHelper.getInstance().getPkConfig();
        if (err == 0) {
            pkConfig.setConnected(true);
            Toast.makeText(this, "跨房连麦成功", Toast.LENGTH_LONG).show();
        } else {
            pkConfig.setConnected(false);
            Toast.makeText(this, "跨房连麦失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 断开跨房连麦结果回调
     *
     * @param err
     * @param errMsg
     */
    @Override
    public void onDisConnectOtherRoom(final int err, final String errMsg) {
        PkConfig pkConfig = ConfigHelper.getInstance().getPkConfig();
        pkConfig.reset();
    }

    /**
     * 网络行质量回调
     * <p>
     * 您可以用来在 UI 上显示当前用户的网络质量，提高用户体验
     *
     * @param localQuality  上行网络质量
     * @param remoteQuality 下行网络质量
     */
    @Override
    public void onNetworkQuality(TRTCCloudDef.TRTCQuality localQuality, ArrayList<TRTCCloudDef.TRTCQuality> remoteQuality) {
        mTRTCVideoLayout.updateNetworkQuality(localQuality.userId, localQuality.quality);
        for (TRTCCloudDef.TRTCQuality qualityInfo : remoteQuality) {
            mTRTCVideoLayout.updateNetworkQuality(qualityInfo.userId, qualityInfo.quality);
        }
    }

    /**
     * 音效播放回调
     *
     * @param effectId
     * @param code     0：表示播放正常结束；其他为异常结束，暂无异常值
     */
    @Override
    public void onAudioEffectFinished(int effectId, int code) {
//        Toast.makeText(this, "effect id = " + effectId + " 播放结束" + " code = " + code, Toast.LENGTH_SHORT).show();
//        mBgmSettingFragmentDialog.onAudioEffectFinished(effectId, code);
    }

    @Override
    public void onRecvCustomCmdMsg(String userId, int cmdID, int seq, byte[] message) {
        String msg = "";
        if (message != null && message.length > 0) {
            try {
                msg = new String(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ToastUtils.showLong("收到" + userId + "的消息：" + msg);
        }
    }

    @Override
    public void onRecvSEIMsg(String userId, byte[] data) {
        String msg = "";
        if (data != null && data.length > 0) {
            try {
                msg = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ToastUtils.showLong("收到" + userId + "的消息：" + msg);
        }
    }

    @Override
    public void onAudioVolumeEvaluationChange(boolean enable) {
        if (enable) {
            mTRTCVideoLayout.showAllAudioVolumeProgressBar();
        } else {
            mTRTCVideoLayout.hideAllAudioVolumeProgressBar();
        }
    }

    @Override
    public void onStartLinkMic() {

    }

    @Override
    public void onMuteLocalVideo(boolean isMute) {
        mTRTCVideoLayout.updateVideoStatus(mTRTCParams.userId, !isMute);
    }

    @Override
    public void onMuteLocalAudio(boolean isMute) {
        //mIvEnableAudio.setImageResource(!isMute ? R.drawable.mic_enable : R.drawable.mic_disable);
    }

    @Override
    public void onSnapshotLocalView(final Bitmap bmp) {
        showSnapshotImage(bmp);
    }

    @Override
    public TXCloudVideoView getRemoteUserViewById(String userId, int steamType) {
        TXCloudVideoView view = mTRTCVideoLayout.findCloudViewView(userId, steamType);
        if (view == null) {
            view = mTRTCVideoLayout.allocCloudVideoView(userId, steamType);
        }
        return view;
    }

    @Override
    public void onRemoteViewStatusUpdate(String userId, boolean enableVideo) {
        mTRTCVideoLayout.updateVideoStatus(userId, enableVideo);
    }

    @Override
    public void onSnapshotRemoteView(Bitmap bm) {
        showSnapshotImage(bm);
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
//        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
//            dismissLoading();
//            ToastUtils.showLong("播放成功：" + event);
//        } else if (event == TXLiveConstants.PLAY_EVT_GET_MESSAGE) {
//            if (param != null) {
//                byte[] data       = param.getByteArray(TXLiveConstants.EVT_GET_MSG);
//                String seiMessage = "";
//                if (data != null && data.length > 0) {
//                    try {
//                        seiMessage = new String(data, "UTF-8");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                ToastUtils.showLong(seiMessage);
//            }
//        } else if (event < 0) {
//            dismissLoading();
//            ToastUtils.showLong("播放失败：" + event);
//        }
    }

    @Override
    public void onNetStatus(Bundle status) {

    }

    private void showSnapshotImage(final Bitmap bmp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bmp == null) {
                    ToastUtils.showLong("截图失败");
                } else {
                    ImageView imageView = new ImageView(CallingActivity.this);
                    imageView.setImageBitmap(bmp);
                    AlertDialog dialog = new AlertDialog.Builder(CallingActivity.this)
                            .setView(imageView)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();

                    dialog.show();

                    final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                    positiveButtonLL.gravity = Gravity.CENTER;
                    positiveButton.setLayoutParams(positiveButtonLL);
                }
            }
        });
    }

    public void over(String msg){
        MethodUtil.showToast(msg,context);
        AppManagerUtil.instance().finishActivity(CallingActivity.this);
    }

    @Override
    public void onBackPressed() {
        if (isCalling)
            MethodUtil.showToast("正在通话中", this);
        else {
            AppManagerUtil.instance().finishActivity(CallingActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCalling = false;
        instance = null;
        exitRoom();
        mTRTCCloudManager.destroy();
        mTRTCRemoteUserManager.destroy();

        if (mAppScene == TRTCCloudDef.TRTC_APP_SCENE_LIVE) {
            TRTCCloud.destroySharedInstance();
        }
    }
}