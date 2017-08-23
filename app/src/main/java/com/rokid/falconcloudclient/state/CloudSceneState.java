package com.rokid.falconcloudclient.state;

import com.rokid.falconcloudclient.util.Logger;

import rokid.context.SceneState;

public class CloudSceneState extends SceneState {

    private CloudStateMonitor cloudStateMonitor;

    public CloudSceneState() {
        super();
        Logger.d("CloudSceneState create ");
        cloudStateMonitor = new CloudSceneStateMonitor(this);
    }

    @Override
    public void onNlpMessage(String nlp, String asr, String action) {
        super.onNlpMessage(nlp, asr, action);
        cloudStateMonitor.onNewIntent(nlp, asr, action);
    }

    @Override
    public void onStateResume() {
        super.onStateResume();
        cloudStateMonitor.onStateResume();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        cloudStateMonitor.onStatePause();
    }

    @Override
    public void onStateCreate() {
        super.onStateCreate();
        cloudStateMonitor.onStatePause();
    }

    @Override
    public void onStateDestroy() {
        super.onStateDestroy();
        cloudStateMonitor.onStateDestory();
    }

    public CloudStateMonitor getCloudStateMonitor() {
        return cloudStateMonitor;
    }
}
