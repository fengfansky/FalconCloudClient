package com.rokid.falconcloudclient.state;

import android.view.View;

import com.rokid.rkcontext.CutState;
import com.rokid.rkcontext.RKBaseTask;

public class CloudCutState extends CutState {

    private CloudStateMonitor cloudStateMonitor;

    public CloudCutState(RKBaseTask baseTask) {
        super(baseTask);
        cloudStateMonitor = new CloudCutStateMonitor(this);
    }

    @Override
    protected void onStateResume() {
        super.onStateResume();
        cloudStateMonitor.onStateResume();
    }

    @Override
    protected void onStatePause() {
        super.onStatePause();
        cloudStateMonitor.onStatePause();
    }

    @Override
    protected void onStateCreate() {
        super.onStateCreate();
        cloudStateMonitor.onStateCreate();
    }

    @Override
    protected void onStateDestroy() {
        super.onStateDestroy();
        cloudStateMonitor.onStateDestory();
    }

    @Override
    protected View inflateContentView() {
        return null;
    }

    public CloudStateMonitor getCloudStateMonitor() {
        return cloudStateMonitor;
    }
}
