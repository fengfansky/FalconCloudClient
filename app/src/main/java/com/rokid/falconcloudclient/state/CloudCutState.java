package com.rokid.falconcloudclient.state;

import android.os.Bundle;
import android.view.View;

import com.rokid.falconcloudclient.bean.ActionNode;
import com.rokid.falconcloudclient.util.Logger;
import com.rokid.rkcontext.CutState;
import com.rokid.rkcontext.RKBaseTask;

public class CloudCutState extends CutState {

    private CloudStateMonitor cloudStateMonitor;

    public CloudCutState(RKBaseTask baseTask) {
        super(baseTask);
        Logger.d("CloudCutState create ");
        cloudStateMonitor = new CloudCutStateMonitor(this);
    }

    @Override
    protected void onReceiveData(Bundle data) {
        super.onReceiveData(data);
        cloudStateMonitor.onNewIntentActionNode((ActionNode) data.getParcelable("data"));
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
