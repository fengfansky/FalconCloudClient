package com.rokid.falconcloudclient.state;

import com.rokid.falconcloudclient.reporter.BaseReporter;

/**
 * Created by fanfeng on 2017/8/6.
 */

public interface ICloudStateMonitor extends StateLifeMonitor,MediaStateMonitor, VoiceStateMonitor, MsgMonitor, BaseReporter.ReporterResponseCallBack {

}
