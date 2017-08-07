package com.rokid.falconcloudclient.reporter;

/**
 * Created by fanfeng on 2017/5/9.
 */

public class MediaReporter extends BaseReporter {

    public static final String START = "Media.START_PLAYING";
    public static final String PAUSED = "Media.PAUSED";
    public static final String FINISHED = "Media.FINISHED";
    public static final String ERROR = "Media.ERROR";

    public MediaReporter(String appId, String event){
        super(appId, event);
    }

    public MediaReporter(String appId, String event, String extra) {
        super(appId, event, extra);
    }
}
