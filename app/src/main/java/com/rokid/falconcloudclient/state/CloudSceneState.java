package com.rokid.falconcloudclient.state;

import android.view.View;

import com.rokid.rkcontext.RKBaseTask;
import com.rokid.rkcontext.SceneState;

public class CloudSceneState extends SceneState{

    private CloudStateMonitor cloudStateMonitor;

    public CloudSceneState(RKBaseTask baseTask) {
        super(baseTask);
        cloudStateMonitor = new CloudSceneStateMonitor(this);
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
