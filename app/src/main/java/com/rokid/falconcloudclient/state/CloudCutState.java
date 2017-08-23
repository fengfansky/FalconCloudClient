package com.rokid.falconcloudclient.state;

import com.rokid.falconcloudclient.util.Logger;

import rokid.context.CutState;

public class CloudCutState extends CutState {

    private CloudStateMonitor cloudStateMonitor;

    public CloudCutState() {
        super();
        Logger.d("CloudCutState create ");
        cloudStateMonitor = new CloudCutStateMonitor(this);
    }

    @Override
    public void onNlpMessage(String nlp, String asr, String action) {
        super.onNlpMessage(nlp, asr, action);
        cloudStateMonitor.onNewIntent(nlp,asr,action);
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
        cloudStateMonitor.onStateCreate();
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
