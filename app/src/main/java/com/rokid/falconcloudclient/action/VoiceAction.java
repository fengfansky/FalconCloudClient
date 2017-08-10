package com.rokid.falconcloudclient.action;

import android.text.TextUtils;

import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.voice.VoiceItemBean;
import com.rokid.falconcloudclient.state.CloudStateMonitor;
import com.rokid.falconcloudclient.util.Logger;

import rokid.os.RKTTS;
import rokid.os.RKTTSCallback;

public class VoiceAction extends BaseAction<VoiceBean> {

    private VoiceBean voiceBean;

    private static final int STOP = -1;
    private RKTTS mRktts = new RKTTS();
    private volatile int ttsId = STOP;
    private boolean isPaused = false;
    private CloudStateMonitor cloudStateMonitor;

    public VoiceAction(CloudStateMonitor cloudStateMonitor) {
        this.cloudStateMonitor = cloudStateMonitor;
    }

    @Override
    public void userStartPlay(VoiceBean actionBean) {
        if (actionBean.isValid()){
            if (cloudStateMonitor!= null) {
                if (cloudStateMonitor != null) {
                    cloudStateMonitor.setCurrentVoiceState(CloudStateMonitor.VOICE_STATE.VOICE_START);
                }
                cloudStateMonitor.setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE.VOICE_START);
            }
            this.voiceBean = actionBean;
            VoiceItemBean voiceItemBean = actionBean.getItem();
            String ttsContent;
            ttsContent = voiceItemBean.getTts();
            if (TextUtils.isEmpty(ttsContent)) {
                Logger.e("The TTS Content can't be empty!!!");
                return;
            }

            if (ttsId > 0) {
                mRktts.stop(ttsId);
            }

            ttsId = mRktts.speak(ttsContent, rkttsCallback);
            Logger.d(" speak TTS ttiId " + ttsId);
        }
    }

    private RKTTSCallback rkttsCallback = new RKTTSCallback() {
        @Override
        public void onStart(int id) {
            super.onStart(id);
            Logger.i("TTS is onTTSStart - id: " + id);
            if (cloudStateMonitor != null){
                cloudStateMonitor.onVoiceStart();
            }
        }

        @Override
        public void onCancel(int id) {
            super.onCancel(id);
            Logger.i("TTS is onCancel - id: " + id + ", current id: " + ttsId + " isPaused : " + isPaused);
            if (id != ttsId) {
                Logger.i("The new tts is already speaking, previous tts stop should not ttsCallback");
                return;
            }
            ttsId = STOP;
            if (cloudStateMonitor != null) {
                if (isPaused){
                    cloudStateMonitor.onVoicePaused();
                }else {
                    cloudStateMonitor.onVoiceCancled();
                }
            }
        }

        @Override
        public void onComplete(int id) {
            super.onComplete(id);
            Logger.i("TTS is onComplete - id: " + id);
            ttsId = STOP;
            if (cloudStateMonitor != null) {
                cloudStateMonitor.onVoiceStop();
            }
        }

        @Override
        public void onError(int id, int err) {
            super.onError(id, err);
            Logger.i("tts onError - id: " + id + ", error: " + err);
            ttsId = STOP;
            if (cloudStateMonitor != null) {
                cloudStateMonitor.onVoiceError();
            }
        }
    };


    @Override
    public synchronized void pausePlay() {
        Logger.d("pause play voice");
        if (ttsId > 0){
            isPaused = true;
            mRktts.stop(ttsId);
        }
    }


    @Override
    public synchronized void resumePlay() {
        Logger.d("resume play voiceBean " + voiceBean);
        if (voiceBean != null){
            userStartPlay(voiceBean);
        }
    }

    @Override
    public synchronized void stopPlay() {
        Logger.d("stop play voice");
        voiceBean = null;
        if (ttsId > 0) {
            isPaused = false;
            mRktts.stop(ttsId);
        }
    }

    @Override
    public synchronized void userPausedPlay() {
        pausePlay();
        if (cloudStateMonitor != null) {
            cloudStateMonitor.setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE.VOICE_PAUSE);
        }
    }


    @Override
    public void userResumePlay() {
        resumePlay();
        if (cloudStateMonitor != null) {
            cloudStateMonitor.setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE.VOICE_RESUME);
        }
    }

    @Override
    public synchronized void userStopPlay() {
        stopPlay();
        if (cloudStateMonitor != null) {
            cloudStateMonitor.setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE.VOICE_STOP);
        }
    }



    @Override
    public void forward() {

    }

    @Override
    public void backward() {

    }

    @Override
    public ACTION_TYPE getActionType() {
        return ACTION_TYPE.VOICE;
    }

}
