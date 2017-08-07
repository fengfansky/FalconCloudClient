package com.rokid.falconcloudclient.state;

/**
 * Created by fanfeng on 2017/6/14.
 */

public interface MediaStateMonitor {

    void onMediaStart();

    void onMediaPause(int position);

    void onMediaResume();

    void onMediaStop();

    void onMediaError(int errorCode);
}
