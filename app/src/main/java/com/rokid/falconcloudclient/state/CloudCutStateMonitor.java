package com.rokid.falconcloudclient.state;

import com.rokid.falconcloudclient.action.MediaAction;
import com.rokid.falconcloudclient.action.VoiceAction;
import com.rokid.falconcloudclient.http.HttpClientWrapper;
import com.rokid.rkcontext.RokidState;

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
        VoiceAction.getInstance().stopPlay();
        MediaAction.getInstance().stopPlay();
        HttpClientWrapper.getInstance().close();
        finishTask();
    }

    @Override
    public void onStateDestory() {

    }

}
