package com.rokid.falconcloudclient.parser;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.bean.ActionNode;
import com.rokid.falconcloudclient.bean.CommonResponseBean;
import com.rokid.falconcloudclient.bean.NLPBean;
import com.rokid.falconcloudclient.bean.response.CloudActionResponseBean;
import com.rokid.falconcloudclient.player.ErrorPromoter;
import com.rokid.falconcloudclient.proto.SendEvent;
import com.rokid.falconcloudclient.reporter.BaseReporter;
import com.rokid.falconcloudclient.util.CommonResponseHelper;
import com.rokid.falconcloudclient.util.Logger;
import com.squareup.okhttp.Response;

import java.io.IOException;

//import com.android.okhttp.Response;

/**
 * Created by fanfeng on 2017/6/1.
 */

public class ResponseParser {

    private static ResponseParser parser;

    public static ResponseParser getInstance() {
        if (parser == null) {
            synchronized (ResponseParser.class) {
                if (parser == null)
                    parser = new ResponseParser();
            }
        }
        return parser;
    }


    public void parseSendEventResponse(String event, Response response) {

        SendEvent.SendEventResponse eventResponse = null;

        try {
            eventResponse = SendEvent.SendEventResponse.parseFrom(response.body().source().readByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                FalconCloudTask.getInstance().getCloudStateMonitor().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_PARSE_EXCEPTION);
            }
        }

        if (eventResponse == null) {
            Logger.d(" eventResponse is null");
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                FalconCloudTask.getInstance().getCloudStateMonitor().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
            }
            return;
        }

        Logger.d(" eventResponse.response : " + eventResponse.getResponse());

        if (eventResponse.getResponse() == null) {
            Logger.d("eventResponse is null !");
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                FalconCloudTask.getInstance().getCloudStateMonitor().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
            }
            return;
        }

        CloudActionResponseBean cloudResponse = new Gson().fromJson(eventResponse.getResponse(), CloudActionResponseBean.class);

        if (cloudResponse == null) {
            Logger.d("cloudResponse parsed null !");
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                FalconCloudTask.getInstance().getCloudStateMonitor().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
            }
            return;
        }

        CommonResponseBean commonResponse = new CommonResponseBean();
        commonResponse.setAction(cloudResponse);
        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);

        //update appState
        if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
            FalconCloudTask.getInstance().getCloudStateMonitor().onNewEventActionNode(actionNode);
        }

    }

    public ActionNode parseMessage(String asr, String nlp, String cloudAction) throws IOException {

        if (TextUtils.isEmpty(nlp) || TextUtils.isEmpty(cloudAction)) {
            Logger.d("nlp or action is null ");
            return null;
        }

        Logger.d("parse nlp : " + nlp);

        NLPBean nlpBean = null;
        try{
            nlpBean = new Gson().fromJson(nlp, NLPBean.class);
        }catch (JsonParseException jsonException){
            dealException(jsonException);
        }

        if (null == nlpBean) {
            Logger.d("NLPData is empty!!!");
            return null;
        }

        Logger.d("parse cloudAction : " + cloudAction);
        CloudActionResponseBean cloudActionResponseBean = null;

        try{
            cloudActionResponseBean  = new Gson().fromJson(cloudAction, CloudActionResponseBean.class);
        }catch (JsonParseException jsonException){
            dealException(jsonException);
        }


        if (null == cloudActionResponseBean){
            Logger.d("cloudAction is empty!!!");
            return null;
        }

        CommonResponseBean commonResponseBean = null;

        commonResponseBean.setAsr(asr);
        commonResponseBean.setNlp(nlpBean);
        commonResponseBean.setAction(cloudActionResponseBean);

        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponseBean);

        return actionNode;
    }

    private void dealException(JsonParseException jsonException) throws IOException {
        Logger.e(" json exception ! " + jsonException.getMessage());
        ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
        jsonException.printStackTrace();
    }

}
