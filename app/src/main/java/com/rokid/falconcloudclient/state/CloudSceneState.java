package com.rokid.falconcloudclient.state;

import android.os.Bundle;
import android.view.View;

import com.rokid.falconcloudclient.bean.ActionNode;
import com.rokid.falconcloudclient.util.Logger;
import com.rokid.rkcontext.RKBaseTask;
import com.rokid.rkcontext.SceneState;

public class CloudSceneState extends SceneState{

    private CloudStateMonitor cloudStateMonitor;

    public CloudSceneState(RKBaseTask baseTask) {
        super(baseTask);
        Logger.d("CloudSceneState create ");
        cloudStateMonitor = new CloudSceneStateMonitor(this);
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
        cloudStateMonitor.onStatePause();
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
