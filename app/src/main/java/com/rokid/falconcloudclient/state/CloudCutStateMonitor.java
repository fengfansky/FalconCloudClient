package com.rokid.falconcloudclient.state;

import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.util.Logger;

import rokid.context.RokidState;

/**
 * Created by fanfeng on 2017/8/6.
 */

public class CloudCutStateMonitor extends CloudStateMonitor {

    public CloudCutStateMonitor(RokidState rokidState) {
        super(rokidState);
    }


    @Override
    public void onStateResume() {

    }

    @Override
    public void onStatePause() {
        Logger.d("form: cut  ,  mediaState: " + currentMediaState + " voiceState : " + currentVoiceState);
        if (currentMediaState == MEDIA_STATE.MEDIA_START || userMediaControlType == USER_MEDIA_CONTROL_TYPE.MEDIA_START) {
            voiceAction.stopPlay();
        }
        if (currentVoiceState == VOICE_STATE.VOICE_START || userVoiceControlType == USER_VOICE_CONTROL_TYPE.VOICE_START) {
            voiceAction.stopPlay();
        }
        FalconCloudTask.getInstance().finishState(rokidState, true);
    }

    @Override
    public void onStateDestory() {

    }

    @Override
    protected void actionFinished() {
        super.actionFinished();
        FalconCloudTask.getInstance().finishState(rokidState, true);
    }

}
