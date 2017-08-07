package com.rokid.falconcloudclient.state;

/**
 * Created by fanfeng on 2017/8/6.
 */

public interface StateLifeMonitor {

    void onStateCreate();

    void onStatePause();

    void onStateResume();

    void onStateDestory();
}
