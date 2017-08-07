package com.rokid.falconcloudclient.state;

/**
 * Created by fanfeng on 2017/6/14.
 */

public interface VoiceStateMonitor {

    void onVoiceStart();

    void onVoiceStop();

    void onVoicePaused();

    void onVoiceCancled();

    void onVoiceError();
}
