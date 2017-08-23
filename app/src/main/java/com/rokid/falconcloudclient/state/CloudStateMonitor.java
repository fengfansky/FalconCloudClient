package com.rokid.falconcloudclient.state;

import android.text.TextUtils;

import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.action.MediaAction;
import com.rokid.falconcloudclient.action.VoiceAction;
import com.rokid.falconcloudclient.bean.ActionNode;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.falconcloudclient.parser.ResponseParser;
import com.rokid.falconcloudclient.player.ErrorPromoter;
import com.rokid.falconcloudclient.player.RKAudioPlayer;
import com.rokid.falconcloudclient.reporter.ExtraBean;
import com.rokid.falconcloudclient.reporter.MediaReporter;
import com.rokid.falconcloudclient.reporter.ReporterManager;
import com.rokid.falconcloudclient.reporter.VoiceReporter;
import com.rokid.falconcloudclient.util.Logger;
import rokid.context.RokidState;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * It management common lifecycle and have some common methods to parse intent、NLP and error TTS.
 * <p>
 * Author: fengfan
 * Modified: 2017/06/01
 */
public abstract class CloudStateMonitor implements ICloudStateMonitor {

    protected RokidState rokidState;
    protected VoiceAction voiceAction;
    protected MediaAction mediaAction;

    public CloudStateMonitor(RokidState rokidState) {
        this.rokidState = rokidState;
        voiceAction = new VoiceAction(this);
        mediaAction = new MediaAction(this);
    }

    //只有在cut应用入栈的时候才会调onResume
    boolean isNeedResume;

    public ActionNode mActionNode;
    public String mAppId;

    //表明当此次返回的action执行完后 falconcloudclient 是否要退出，同时，当 shouldEndSession 为 true 时，CloudAppClient 将会忽略 EventRequests，即在action执行过程中不会产生 EventRequest。
    public boolean shouldEndSession;

    public CloudStateMonitor.PROMOTE_STATE promoteState;

    public CloudStateMonitor.MEDIA_STATE currentMediaState;
    public CloudStateMonitor.VOICE_STATE currentVoiceState;

    public CloudStateMonitor.USER_MEDIA_CONTROL_TYPE userMediaControlType;
    public CloudStateMonitor.USER_VOICE_CONTROL_TYPE userVoiceControlType;

    public ReporterManager reporterManager = ReporterManager.getInstance();

    @Override
    public void onNewIntent(String nlp, String asr, String action) {
        ActionNode actionNode = null;

        try {
            actionNode = ResponseParser.getInstance().parseMessage(nlp, asr, action);
        } catch (IOException e) {
            Logger.e(" speak error info exception !");
            e.printStackTrace();
        }
        Logger.d("form: " + rokidState.getStateType() + " onNewIntent actioNode : " + actionNode);
        if (actionNode != null) {
            if (TextUtils.isEmpty(actionNode.getAppId())) {
                Logger.d("new cloudAppId is null !");
                checkAppState();
                return;
            }

            if (!actionNode.getAppId().equals(mAppId)) {
                Logger.d("onNewEvent the appId is the not the same with lastAppId");
                mediaAction.stopPlay();
                voiceAction.stopPlay();
                this.currentMediaState = null;
                this.currentVoiceState = null;
            }
            this.mActionNode = actionNode;
            this.mAppId = actionNode.getAppId();
            this.shouldEndSession = actionNode.isShouldEndSession();
            processActionNode(actionNode);

        } else {
            checkAppState();
        }
    }


    @Override
    public synchronized void onNewEvent(ActionNode actionNode) {
        Logger.d("form: " + rokidState.getStateType() + " onNewEvent actioNode : " + actionNode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (actionNode != null) {

            if (TextUtils.isEmpty(actionNode.getAppId())) {
                Logger.d("new cloudAppId is null !");
                checkAppState();
                return;
            }

            if (!actionNode.getAppId().equals(mAppId)) {
                Logger.d("onNewEvent the appId is the not the same with lastAppId");
                checkAppState();
                return;
            }

            this.shouldEndSession = actionNode.isShouldEndSession();
            processActionNode(actionNode);
        } else {
            checkAppState();
        }
    }

    @Override
    public void onStateCreate() {

    }

    @Override
    public synchronized void onMediaStart() {
        currentMediaState = MEDIA_STATE.MEDIA_START;
        Logger.d("form: " + rokidState.getStateType() + " onMediaStart ! " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);

        sendMediaReporter(MediaReporter.START);
    }

    @Override
    public synchronized void onMediaPause(int position) {
        currentMediaState = MEDIA_STATE.MEDIA_PAUSED;
        Logger.d("form: " + rokidState.getStateType() + " onMediaPause ! position : " + position + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendMediaReporter(MediaReporter.PAUSED);
    }

    @Override
    public synchronized void onMediaResume() {
        currentMediaState = MEDIA_STATE.MEDIA_RESUME;
        Logger.d("form: " + rokidState.getStateType() + " onMediaResume ! " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onMediaStop() {
        currentMediaState = MEDIA_STATE.MEDIA_STOP;
        Logger.d("form: " + rokidState.getStateType() + " onMediaStop !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (shouldEndSession) {
            checkAppState();
        } else {
            if (TextUtils.isEmpty(mAppId)) {
                Logger.d(" appId is null !");
                return;
            }

            sendMediaReporter(MediaReporter.FINISHED);
        }
    }

    @Override
    public synchronized void onMediaError(int errorCode) {
        currentMediaState = MEDIA_STATE.MEDIA_ERROR;
        Logger.d("form: " + rokidState.getStateType() + " onMediaError ! errorCode : " + errorCode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (errorCode == RKAudioPlayer.MEDIA_ERROR_TIME_OUT) {
            promoteErrorInfo(ErrorPromoter.ERROR_TYPE.MEDIA_TIME_OUT);
        } else {
            promoteErrorInfo(ErrorPromoter.ERROR_TYPE.MEDIA_ERROR);
        }
    }

    @Override
    public synchronized void onVoiceStart() {
        currentVoiceState = VOICE_STATE.VOICE_START;
        Logger.d("form: " + rokidState.getStateType() + " onVoiceStart !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendVoiceReporter(VoiceReporter.START);
    }

    @Override
    public synchronized void onVoiceStop() {
        currentVoiceState = VOICE_STATE.VOICE_STOP;
        Logger.d("form: " + rokidState.getStateType() + " onVoiceStop !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (shouldEndSession) {
            checkAppState();
        } else {
            sendVoiceReporter(VoiceReporter.FINISHED);
        }
    }

    @Override
    public void onVoicePaused() {
        currentVoiceState = VOICE_STATE.VOICE_PAUSED;
        Logger.d("form: " + rokidState.getStateType() + " onVoiceCancled !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onVoiceCancled() {
        currentVoiceState = VOICE_STATE.VOICE_CANCLED;
        Logger.d("form: " + rokidState.getStateType() + " onVoiceCancled !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        checkAppState();
    }

    @Override
    public synchronized void onVoiceError() {
        currentVoiceState = VOICE_STATE.VOICE_ERROR;
        Logger.d("form: " + rokidState.getStateType() + " onVoiceError !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        promoteErrorInfo(ErrorPromoter.ERROR_TYPE.TTS_ERROR);
    }

    @Override
    public synchronized void onEventErrorCallback(String event, ERROR_CODE errorCode) {
        Logger.e(" event error call back !!!");
        Logger.e("form: " + rokidState.getStateType() + " onEventErrorCallback " + " event : " + event + " errorCode " + errorCode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        checkAppState();
//        promoteErrorInfo(ErrorPromoter.ERROR_TYPE.NO_TASK_PROCESS);
    }


    @Override
    public synchronized void onEventResponseCallback(String event, Response response) {
        Logger.d("form: " + rokidState.getStateType() + " onEventResponseCallback event : " + event + " response : " + response + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        ResponseParser.getInstance().parseSendEventResponse(event, response);
    }

    /**
     * To process real action
     *
     * @param actionNode the validated action
     */
    protected void processActionNode(ActionNode actionNode) {

        if (ActionBean.TYPE_EXIT.equals(actionNode.getActionType())) {
            Logger.d("current response is a INTENT EXIT - Finish Activity");
            exit();
            return;
        }

        if (ActionBean.TYPE_NORMAL.equals(actionNode.getActionType())) {

            if (actionNode.getVoice() != null) {
                voiceAction.processAction(actionNode.getVoice());
            }
            if (actionNode.getMedia() != null) {
                mediaAction.processAction(actionNode.getMedia());
            }
        }
    }


    private void sendVoiceReporter(String action) {
        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            checkAppState();
            return;
        }
        if (mActionNode == null || mActionNode.getVoice() == null) {
            Logger.d(" mActionNode or voice is null ! ");
            checkAppState();
            return;
        }
        if (mActionNode.getVoice().isDisableEvent()) {
            Logger.d("SendEventRequest disableEvent closed!");
            checkAppState();
            return;
        }

        ExtraBean extraBean = new ExtraBean();
        if (mActionNode.getVoice().getItem() == null) {
            reporterManager.executeReporter(new VoiceReporter(mAppId, action));
        } else {
            Logger.d(" extraBean : " + extraBean.toString());
            extraBean.setVoice(new ExtraBean.VoiceExtraBean(mActionNode.getVoice().getItem().getItemId()));
            reporterManager.executeReporter(new VoiceReporter(mAppId, action, extraBean.toString()));
        }
    }

    private void sendMediaReporter(String action) {
        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            checkAppState();
            return;
        }
        if (mActionNode == null || mActionNode.getMedia() == null) {
            Logger.d("mActionNode or media is null!");
            checkAppState();
            return;
        }

        if (mActionNode.getMedia().isDisableEvent()) {
            Logger.d("SendEventRequest disableEvent closed!");
            checkAppState();
            return;
        }
        ExtraBean extraBean = new ExtraBean();

        if (mActionNode.getMedia().getItem() == null) {
            extraBean.setMedia(new ExtraBean.MediaExtraBean(String.valueOf(mediaAction.getMediaPosition()), String.valueOf(mediaAction.getMediaDuration())));
        } else {
            extraBean.setMedia(new ExtraBean.MediaExtraBean(mActionNode.getMedia().getItem().getItemId(), mActionNode.getMedia().getItem().getToken(), String.valueOf(mediaAction.getMediaPosition()), String.valueOf(mediaAction.getMediaDuration())));
        }
        Logger.d(" extraBean : " + extraBean.toString());

        reporterManager.executeReporter(new MediaReporter(mAppId, action, extraBean.toString()));
    }

    private void promoteErrorInfo(ErrorPromoter.ERROR_TYPE errorType) {
        Logger.d(" promoteErrorInfo isStateInvalid : " + isStateInvalid());
        if (isStateInvalid()) {
            try {
                ErrorPromoter.getInstance().speakErrorPromote(errorType, new ErrorPromoter.ErrorPromoteCallback() {
                    @Override
                    public void onPromoteStarted() {
                        Logger.d(" onPromoteStarted!");
                        promoteState = PROMOTE_STATE.STARTED;
                    }

                    @Override
                    public void onPromoteFinished() {
                        promoteState = PROMOTE_STATE.FINISHED;
                        Logger.d(" onPromoteFinished !");
                        actionFinished();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isStateInvalid() {
        Logger.d("form: " + rokidState.getStateType() + " isStateInvalid shouldEndSession : " + shouldEndSession + " mediaType : " + currentMediaState + " videoType : " + currentVoiceState +
                " promoteState : " + promoteState);
        return (currentMediaState == null || currentMediaState == MEDIA_STATE.MEDIA_STOP || currentMediaState == MEDIA_STATE.MEDIA_ERROR) && (currentVoiceState == null || currentVoiceState == VOICE_STATE.VOICE_STOP || currentVoiceState == VOICE_STATE.VOICE_CANCLED || currentVoiceState == VOICE_STATE.VOICE_ERROR) && (promoteState == null || promoteState == PROMOTE_STATE.FINISHED);
    }

    private void checkAppState() {
        if (!isStateInvalid()) {
            Logger.d(" state is valid , don't suspend cloudstate");
        }else {
            actionFinished();
        }
    }

    protected void exit() {
        Logger.d("form: " + rokidState.getStateType() + " onExitCallback actionFinished");
        //用户主动退出应用
        FalconCloudTask.getInstance().finishState(rokidState,false);
    }

    protected void actionFinished() {
        Logger.d(" all action has finished");
        if (mActionNode.getConfirmBean() != null) {
            FalconCloudTask.getInstance().openSiren(mActionNode.getPickup().isEnable(), mActionNode.getPickup().getDurationInMilliseconds());
        }

        //打开拾音
        if (mActionNode.getPickup() != null) {
            Logger.d("pickUp : " + mActionNode.getPickup().toString());
            FalconCloudTask.getInstance().openSiren(mActionNode.getPickup().isEnable(), mActionNode.getPickup().getDurationInMilliseconds());
        }
    }


    public enum PROMOTE_STATE {
        STARTED,
        FINISHED
    }

    public enum VOICE_STATE {
        VOICE_START,
        VOICE_PAUSED,
        VOICE_STOP,
        VOICE_CANCLED,
        VOICE_ERROR
    }

    public enum MEDIA_STATE {
        MEDIA_START,
        MEDIA_PAUSED,
        MEDIA_RESUME,
        MEDIA_STOP,
        MEDIA_ERROR
    }

    public void setCurrentMediaState(MEDIA_STATE currentMediaState) {
        this.currentMediaState = currentMediaState;
    }

    public void setCurrentVoiceState(VOICE_STATE currentVoiceState) {
        this.currentVoiceState = currentVoiceState;
    }

    public void setUserMediaControlType(CloudStateMonitor.USER_MEDIA_CONTROL_TYPE userMediaControlType) {
        this.userMediaControlType = userMediaControlType;
    }


    public void setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE userVoiceControlType) {
        this.userVoiceControlType = userVoiceControlType;
    }

    public enum USER_MEDIA_CONTROL_TYPE {
        MEDIA_START,
        MEDIA_PAUSE,
        MEDIA_RESUME,
        MEDIA_STOP
    }

    public enum USER_VOICE_CONTROL_TYPE {
        VOICE_START,
        VOICE_PAUSE,
        VOICE_RESUME,
        VOICE_STOP
    }

}

