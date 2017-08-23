package com.rokid.falconcloudclient;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rokid.falconcloudclient.http.BaseUrlConfig;
import com.rokid.falconcloudclient.http.HttpClientWrapper;
import com.rokid.falconcloudclient.state.CloudStateMonitor;
import com.rokid.falconcloudclient.state.CloudCutState;
import com.rokid.falconcloudclient.state.CloudSceneState;
import com.rokid.falconcloudclient.bean.ActionNode;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.falconcloudclient.parser.ResponseParser;
import com.rokid.falconcloudclient.player.ErrorPromoter;
import com.rokid.falconcloudclient.util.Logger;

import rokid.app.TaskBundle;
import rokid.context.RKBaseTask;
import rokid.context.RokidState;
import rokid.context.utils.NlpMockUtils;
import rokid.event.CVInputEvent;
import rokid.event.SensorInputEvent;
import rokid.event.TouchInputEvent;
import rokid.event.VoiceCommand;
import rokid.event.VoiceInputEvent;

import java.io.IOException;

/**
 * Modified by fanfeng on 2017/7/20.
 */
public class FalconCloudTask extends RKBaseTask {

    private static FalconCloudTask instance;

    @Override
    public void onCreate(TaskBundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        BaseUrlConfig.initDeviceInfo();
        NlpMockUtils.setMockNlp(true);
    }

    public static FalconCloudTask getInstance() {
        return instance;
    }

    @Override
    public void onVoiceCommand(VoiceCommand voiceCommand) {
        if (voiceCommand == null) {
            Logger.d("voiceCommand is null !");
            return;
        }
        String nlp = voiceCommand.getNLP();
        String asr = voiceCommand.getASR();
        String action = voiceCommand.getAction();
        Logger.d(" nlp " + nlp);
        Logger.d(" asr " + asr);
        Logger.d(" action " + action);

        ActionNode actionNode = null;

        try {
            actionNode = ResponseParser.getInstance().parseMessage(nlp, asr, action);
        } catch (IOException e) {
            Logger.e(" speak error info exception !");
            e.printStackTrace();
        }

        if (actionNode == null) {
            try {
                ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            } catch (IOException e) {
                Logger.e(" speak error info exception !");
                e.printStackTrace();
            }
            return;
        }

        switch (actionNode.getForm()) {
            case ActionBean.FORM_SCENE:
                startState(CloudSceneState.class, voiceCommand);
                break;
            case ActionBean.FORM_CUT:
                startState(CloudCutState.class, voiceCommand);
                break;
        }
    }

    @Override
    public void setContentView(View view) {

    }

    @Override
    public void onTaskResult(TaskBundle taskBundle) {

    }

    @Override
    protected RokidState getState() {

        return super.getState();
    }

    public CloudStateMonitor getCloudStateMonitor() {
        if (getState() == null) {
            Logger.d(" rokidState is null !");
            return null;
        }

        Logger.d(" getState() is  " + getState().getStateType());

        if (getState() instanceof CloudSceneState) {
            return ((CloudSceneState) getState()).getCloudStateMonitor();
        } else if (getState() instanceof CloudCutState) {
            return ((CloudCutState) getState()).getCloudStateMonitor();
        }

        return null;
    }

    public void openSiren(boolean pickupEnable, int durationInMilliseconds) {
        Logger.d(" process openSiren ");
        Intent intent = new Intent();
        ComponentName compontent = new ComponentName("com.rokid.activation", "com.rokid.activation.service.CoreService");
        intent.setComponent(compontent);
        intent.putExtra("InputAction", "confirmEvent");
        Bundle bundle = new Bundle();
        bundle.putBoolean("isConfirm", pickupEnable);//拾音打开或关闭
        bundle.putInt("durationInMilliseconds", durationInMilliseconds);//当enable=true时，在用户不说话的情况下，拾音打开持续时间
        intent.putExtra("intent", bundle);
        startService(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        HttpClientWrapper.getInstance().close();
    }

    @Override
    public void onRapture() {

    }


    @Override
    protected void handleOnBindIntent(Intent intent) {

    }

    @Override
    public void onTouchInputEvent(TouchInputEvent touchInputEvent) {

    }

    @Override
    public void onCVInputEvent(CVInputEvent cvInputEvent) {

    }

    @Override
    public void onSensorInputEvent(SensorInputEvent sensorInputEvent) {

    }

    @Override
    public void onVoiceInputEvent(VoiceInputEvent voiceInputEvent) {

    }
}
