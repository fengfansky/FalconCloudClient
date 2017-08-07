package com.rokid.falconcloudclient.state;

import com.rokid.falconcloudclient.action.MediaAction;
import com.rokid.falconcloudclient.action.VoiceAction;
import com.rokid.falconcloudclient.http.HttpClientWrapper;
import com.rokid.falconcloudclient.util.Logger;
import com.rokid.rkcontext.RokidState;

/**
 * Created by fanfeng on 2017/8/6.
 */

public class CloudSceneStateMonitor extends CloudStateMonitor {

    public CloudSceneStateMonitor(RokidState rokidState) {
        super(rokidState);
    }


    @Override
    public void onStateResume() {
        Logger.d("scene  onStateResume mediaType: " + currentMediaState + " voiceType : " + currentVoiceState + " userMediaControlType: " + userMediaControlType + " userVoiceControlType: " + userVoiceControlType);

        //应用onResume的时候要考虑到用户上次操作是否是暂停
        if (currentMediaState == MEDIA_STATE.MEDIA_PAUSED && !(userMediaControlType == USER_MEDIA_CONTROL_TYPE.MEDIA_PAUSE)) {
            Logger.d("scene: onAppResume resume play audio");
            MediaAction.getInstance().resumePlay();
        }

        if (currentVoiceState == VOICE_STATE.VOICE_PAUSED && !(userVoiceControlType == USER_VOICE_CONTROL_TYPE.VOICE_PAUSE)) {
            Logger.d("scene onAppResume play voice");
            VoiceAction.getInstance().resumePlay();
        }
    }

    @Override
    public void onStatePause() {
        MediaAction.getInstance().pausePlay();
        VoiceAction.getInstance().pausePlay();
    }

    @Override
    public void onStateDestory() {
        MediaAction.getInstance().stopPlay();
        VoiceAction.getInstance().stopPlay();
        HttpClientWrapper.getInstance().close();
    }

}