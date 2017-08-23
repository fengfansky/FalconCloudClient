package com.rokid.falconcloudclient.state;

import com.rokid.falconcloudclient.bean.ActionNode;

/**
 * Created by fanfeng on 2017/6/14.
 */

public interface MsgMonitor {

    void onNewIntent(String nlp, String asr, String action);

    void onNewEvent(ActionNode actionNode);

}
