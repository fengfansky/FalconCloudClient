package com.rokid.falconcloudclient.action;

import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.voice.VoiceItemBean;
import com.rokid.falconcloudclient.state.CloudStateMonitor;
import com.rokid.falconcloudclient.tts.TTSHelper;
import com.rokid.falconcloudclient.util.Logger;

public class VoiceAction extends BaseAction<VoiceBean> {

    private static volatile VoiceAction voiceAction;

    private VoiceBean voiceBean;

    public static VoiceAction getInstance() {
        if (voiceAction == null) {
            synchronized (VoiceAction.class) {
                if (voiceAction == null)
                    voiceAction = new VoiceAction();
            }
        }
        return voiceAction;
    }


    @Override
    public void userStartPlay(VoiceBean actionBean) {
        if (actionBean.isValid()){
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                FalconCloudTask.getInstance().getCloudStateMonitor().setCurrentVoiceState(CloudStateMonitor.VOICE_STATE.VOICE_START);
                FalconCloudTask.getInstance().getCloudStateMonitor().setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE.VOICE_START);
            }
            this.voiceBean = actionBean;
            VoiceItemBean voiceItemBean = actionBean.getItem();
            String ttsContent;
            ttsContent = voiceItemBean.getTts();
            TTSHelper.getInstance().speakTTS(ttsContent);
        }
    }


    @Override
    public synchronized void pausePlay() {
        Logger.d("pause play voice");
        TTSHelper.getInstance().pauseTTS();
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
        TTSHelper.getInstance().stopTTS();
    }

    @Override
    public synchronized void userPausedPlay() {
        pausePlay();
        if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
            FalconCloudTask.getInstance().getCloudStateMonitor().setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE.VOICE_PAUSE);
        }
    }


    @Override
    public void userResumePlay() {
        resumePlay();
        if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
            FalconCloudTask.getInstance().getCloudStateMonitor().setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE.VOICE_RESUME);
        }
    }

    @Override
    public synchronized void userStopPlay() {
        stopPlay();
        if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
            FalconCloudTask.getInstance().getCloudStateMonitor().setUserVoiceControlType(CloudStateMonitor.USER_VOICE_CONTROL_TYPE.VOICE_STOP);
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
