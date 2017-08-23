package com.rokid.falconcloudclient.reporter;

import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.http.BaseUrlConfig;
import com.rokid.falconcloudclient.http.HttpClientWrapper;
import com.rokid.falconcloudclient.proto.SendEvent;
import com.rokid.falconcloudclient.proto.SendEventCreator;
import com.rokid.falconcloudclient.util.Logger;
import com.squareup.okhttp.Response;

import java.io.IOException;

//import com.android.okhttp.Response;


/**
 * Created by fanfeng on 2017/5/9.
 */

public abstract class BaseReporter implements Runnable {

    String appId;
    String event;
    String extra;

    public BaseReporter(String appId, String event){
        this.appId = appId;
        this.event = event;
        this.extra = "{}";
    }

    public BaseReporter(String appId, String event, String extra) {
        this.appId = appId;
        this.event = event;
        this.extra = extra;
    }

    @Override
    public void run() {
        report();
    }

    public void report() {

        if (FalconCloudTask.getInstance().getCloudStateMonitor() == null) {
            Logger.d("appStateManager is null ");
            return;
        }

        SendEvent.SendEventRequest eventRequest =
                SendEventCreator.generateSendEventRequest(appId, event, extra);
        Logger.d(" eventRequest : " + eventRequest.toString());
        Logger.d(" eventRequest url: " + BaseUrlConfig.getUrl());
        Response response = null;
        try {
            response = HttpClientWrapper.getInstance().sendRequest(BaseUrlConfig.getUrl(), eventRequest);
        } catch (IOException e) {
            e.printStackTrace();
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                FalconCloudTask.getInstance().getCloudStateMonitor().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_CODE.ERROR_IOEXCEPTION);
            }
        }finally {
            try {
                if (response != null && response.body() != null){
                    if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                        FalconCloudTask.getInstance().getCloudStateMonitor().onEventResponseCallback(event, response);
                    }
                    response.body().close();
                }else {
                    if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                        FalconCloudTask.getInstance().getCloudStateMonitor().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                    FalconCloudTask.getInstance().getCloudStateMonitor().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_CODE.ERROR_IOEXCEPTION);
                }
            }
        }


    }

    public interface ReporterResponseCallBack {

        enum ERROR_CODE{
            ERROR_CONNNECTION_TIMEOUT,
            ERROR_RESPONSE_NULL,
            ERROR_IOEXCEPTION ,
            ERROR_PARSE_EXCEPTION

        }

        void onEventErrorCallback(String event, ERROR_CODE errorCode);

        void onEventResponseCallback(String event, Response response);
    }
}
