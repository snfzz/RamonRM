package com.ramon.ramonrm.video;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.Session;
import com.ramon.ramonrm.util.AppManagerUtil;
import com.ramon.ramonrm.util.BaseActivity;
import com.ramon.ramonrm.util.MethodUtil;
import com.ramon.ramonrm.video.sdkadapter.TRTCCloudListenerImpl;
import com.ramon.ramonrm.video.sdkadapter.TRTCCloudManagerListener;
import com.ramon.ramonrm.webapi.APIConfig;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCStatistics;

import java.util.ArrayList;

public class VideoCallActivity extends BaseActivity  implements View.OnClickListener, TRTCCloudManagerListener {

    public final static int CALLIN = 0;
    public final static int CALLOUT = 1;

    public static VideoCallActivity instance;

    public static VideoCallActivity getInstance() {
        return instance;
    }

    public static boolean isCalling = false;


    private Ringtone mRingtone;
    private String fromUser,fromUserName,fromUserDept;
    private int roomId;
    private int callType = 0;

    private TextView lblName,lblInfo,lblJuJue,lblJieTing;
    private ImageView imgHeader,imgJuJue,imgJieTing,imgJieShu,imgCameraQH;

    private RelativeLayout rlayCreateRoom,rlayRoom;

    private TRTCCloud mTRTCCloud;
    private TRTCCloudDef.TRTCParams mTRTCParams;
    private TRTCCloudListenerImpl mTRTCListener;

    private TXCloudVideoView mTRTCLocalVideo;
    private TXCloudVideoView mTRTCRemoteVideo;

    public VideoCallActivity() {
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
        setContentView(R.layout.activity_videocall);
        fromUser = getIntent().getStringExtra("fromUser");
        fromUserName = getIntent().getStringExtra("fromUserName");
        fromUserDept = getIntent().getStringExtra("fromUserDept");
        roomId = getIntent().getIntExtra("roomID", 0);
        int role = getIntent().getIntExtra("role", 0);
        callType = getIntent().getIntExtra("callType",CALLIN);
        initTRTCSDK();
        initView();
    }

    public void defaultCallMediaPlayer() throws Exception {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mRingtone = RingtoneManager.getRingtone(this, notification);
        mRingtone.play();
    }

    private void initTRTCSDK(){
        try {
            mTRTCCloud = TRTCCloud.sharedInstance(VideoCallActivity.this);
        }catch (Exception e){
            Log.e("fwwwweq",e.getMessage());
        }

        mTRTCParams = new TRTCCloudDef.TRTCParams(APIConfig.SDKAppId, Session.CurrUser.YongHuSNo, Session.UserSig, roomId, "", "");
        mTRTCParams.role = TRTCCloudDef.TRTCRoleAnchor;
        mTRTCCloud.setDefaultStreamRecvMode(true,true);
        mTRTCListener = new TRTCCloudListenerImpl(this);
        mTRTCCloud.setListener(mTRTCListener);
    }

    private void initView() {
        lblName = (TextView) findViewById(R.id.activity_videocall_lblname);
        lblName.setText(fromUserDept + "  " + fromUserName);
        lblInfo = (TextView) findViewById(R.id.activity_videocall_lblinfo);
        imgHeader = (ImageView) findViewById(R.id.activity_videocall_imgheader);
        lblJuJue = (TextView) findViewById(R.id.activity_videocall_lbljujue);
        lblJuJue.setOnClickListener(this);
        imgJuJue = (ImageView) findViewById(R.id.activity_videocall_imgjujue);
        imgJuJue.setOnClickListener(this);
        lblJieTing = (TextView) findViewById(R.id.activity_videocall_lbljieting);
        lblJieTing.setOnClickListener(this);
        imgJieTing = (ImageView) findViewById(R.id.activity_videocall_imgjieting);
        imgJieTing.setOnClickListener(this);
        imgJieShu = (ImageView) findViewById(R.id.activity_videocall_imgjieshu_calling);
        imgJieShu.setOnClickListener(this);
        imgCameraQH = (ImageView) findViewById(R.id.activity_videocall_imgcameraqh);
        imgCameraQH.setOnClickListener(this);

        mTRTCLocalVideo = (TXCloudVideoView) findViewById(R.id.activity_videocall_trtclocalvideo);
        mTRTCRemoteVideo = (TXCloudVideoView) findViewById(R.id.activity_videocall_trtcremotevideo);

        rlayCreateRoom = (RelativeLayout) findViewById(R.id.activity_videocall_rlaycreateroom);
        rlayCreateRoom.setVisibility(View.VISIBLE);
        rlayRoom = (RelativeLayout) findViewById(R.id.activity_videocall_rlayroom);
        if(callType == CALLIN){
            rlayCreateRoom.setVisibility(View.VISIBLE);
            rlayRoom.setVisibility(View.GONE);
            try {
                defaultCallMediaPlayer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(callType == CALLOUT){
            rlayCreateRoom.setVisibility(View.GONE);
            rlayRoom.setVisibility(View.VISIBLE);
            enterRoom();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.activity_videocall_imgjujue || vId == R.id.activity_videocall_lbljujue || vId == R.id.activity_videocall_imgjieshu_calling) {
                //拒接
                if (mRingtone != null) {
                    mRingtone.stop();
                }
                exitRoom();
                sendTIMMessage(fromUser, "video:over:");
                over("挂断");
                AppManagerUtil.instance().finishActivity(VideoCallActivity.this);
            } else if (vId == R.id.activity_videocall_imgjieting || vId == R.id.activity_videocall_lbljieting) {
                //接听
                rlayCreateRoom.setVisibility(View.GONE);
                rlayRoom.setVisibility(View.VISIBLE);
                if (mRingtone != null) {
                    mRingtone.stop();
                }
                enterRoom();
            }
            else if(vId == R.id.activity_videocall_imgcameraqh){
                //镜头切换
                if(mTRTCCloud!=null) {
                    mTRTCCloud.switchCamera();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void over(String msg){
        MethodUtil.showToast(msg,context);
        AppManagerUtil.instance().finishActivity(VideoCallActivity.this);
    }


    @Override
    public void onBackPressed() {
        if (isCalling)
            MethodUtil.showToast("正在通话中", this);
        else {
            AppManagerUtil.instance().finishActivity(VideoCallActivity.this);
        }
    }

    private void enterRoom() {
        mTRTCCloud.enterRoom(mTRTCParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
        mTRTCCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
        mTRTCCloud.startLocalPreview(true, mTRTCLocalVideo);
        mTRTCCloud.setRemoteViewFillMode(fromUser, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
        mTRTCCloud.startLocalAudio();
        mTRTCCloud.enableAudioEarMonitoring(true);
    }
    private void exitRoom() {
        mTRTCCloud.stopLocalPreview();
        mTRTCCloud.stopRemoteView(fromUser);
        mTRTCCloud.stopLocalAudio();
        mTRTCCloud.exitRoom();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCalling = false;
        instance = null;
        if (mRingtone != null) {
            mRingtone.stop();
        }
        if (mTRTCCloud != null) {
            mTRTCCloud.setListener(null);
        }
        mTRTCCloud = null;
        TRTCCloud.destroySharedInstance();
    }


    //region 监听
    /**
     * 加入房间回调
     *
     * @param elapsed 加入房间耗时，单位毫秒
     */
    public void onEnterRoom(long elapsed) {
        Log.d("Tag:VideoCallActivity", "EnterRoom:耗时 " + elapsed);
    }

    public void onExitRoom(int reason){
        Log.d("Tag:VideoCallActivity", "ExitRoom: " + reason);
    }

    public void onError(int errCode, String errMsg, Bundle extraInfo) {
        Log.d("VideoCallActivity", "sdk callback onError " + errCode + " " + errMsg);
    }

    /**
     * 有新的主播{@link TRTCCloudDef#TRTCRoleAnchor}加入了当前视频房间
     * 该方法会在主播加入房间的时候进行回调，此时音频数据会自动拉取下来，但是视频需要有 View 承载才会开始渲染。
     * 为了更好的交互体验，Demo 选择在 onUserVideoAvailable 中，申请 View 并且开始渲染。
     * 您可以根据实际需求，选择在 onUserEnter 还是 onUserVideoAvailable 中发起渲染。
     *
     * @param userId 用户标识
     */
    public void  onUserEnter(String userId) {
        Log.d("VideoCallActivity", "UserEnter:" + userId);
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
    public void onUserExit(String userId, int reason){
        Log.d("VideoCallActivity", "UserExit:" + userId);
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
    public void onUserVideoAvailable(String userId, boolean available){
        Log.d("VideoCallActivity", "UserVideoAvailable:" + userId);
        mTRTCCloud.startRemoteView(userId, mTRTCRemoteVideo);
    }

    /**
     * 是否有辅路上行的回调，Demo 中处理方式和主画面的一致
     *
     * @param userId    用户标识
     * @param available 屏幕分享是否开启
     */
    public  void onUserSubStreamAvailable(String userId, boolean available){
        Log.d("VideoCallActivity", "UserSubStreamAvailable:" + userId);
    }

    /**
     * 是否有音频上行的回调
     * <p>
     * 您可以根据您的项目要求，设置相关的 UI 逻辑，比如显示对端闭麦的图标等
     *
     * @param userId    用户标识
     * @param available true：音频可播放，false：音频被关闭
     */
    public void onUserAudioAvailable(String userId, boolean available){
        Log.d("VideoCallActivity", "UserUserAudioAvailable:" + userId);
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
    public void onFirstVideoFrame(String userId, int streamType, int width, int height){

    }

    /**
     * 音量大小回调
     * <p>
     * 您可以用来在 UI 上显示当前用户的声音大小，提高用户体验
     *
     * @param userVolumes 所有正在说话的房间成员的音量（取值范围0 - 100）。即 userVolumes 内仅包含音量不为0（正在说话）的用户音量信息。其中本地进房 userId 对应的音量，表示 local 的音量，也就是自己的音量。
     * @param totalVolume 所有远端成员的总音量, 取值范围 [0, 100]
     */
    public  void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume){

    }

    /**
     * SDK 状态数据回调
     * <p>
     * 一般客户无需关注，专业级客户可以用来进行统计相关的性能指标；您可以根据您的项目情况是否实现统计等功能
     *
     * @param statics 状态数据
     */
    public void onStatistics(TRTCStatistics statics){

    }

    /**
     * 跨房连麦会结果回调
     *
     * @param userID
     * @param err
     * @param errMsg
     */
    public  void onConnectOtherRoom(String userID, int err, String errMsg){

    }

    /**
     * 断开跨房连麦结果回调
     *
     * @param err
     * @param errMsg
     */
    public  void onDisConnectOtherRoom(int err, String errMsg){

    }

    /**
     * 网络行质量回调
     * <p>
     * 您可以用来在 UI 上显示当前用户的网络质量，提高用户体验
     *
     * @param localQuality  上行网络质量
     * @param remoteQuality 下行网络质量
     */
    public void onNetworkQuality(TRTCCloudDef.TRTCQuality localQuality, ArrayList<TRTCCloudDef.TRTCQuality> remoteQuality){

    }

    /**
     * 音效播放回调
     *
     * @param effectId
     * @param code     0：表示播放正常结束；其他为异常结束，暂无异常值
     */
    public void onAudioEffectFinished(int effectId, int code){

    }

    public void onRecvCustomCmdMsg(String userId, int cmdID, int seq, byte[] message){

    }

    public  void onRecvSEIMsg(String userId, byte[] data){

    }
    //endregion
}
