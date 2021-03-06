package com.rokid.falconcloudclient.player;

import android.content.res.AssetManager;

import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.util.Logger;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by fanfeng on 2017/7/12.
 */

public class ErrorPromoter {

    private static volatile ErrorPromoter errorPromoteHelper;
    private RKAudioPlayer rkAudioPlayer;
    private AssetManager assetManager;
    private ErrorPromoteCallback errorPromoteCallback;

    public ErrorPromoter() {
        initRKAudioPlayer();
    }

    private void initRKAudioPlayer() {
        assetManager = FalconCloudTask.getInstance().getAssets();
        rkAudioPlayer = new RKAudioPlayer(FalconCloudTask.getInstance());
        rkAudioPlayer.setmOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                if (errorPromoteCallback != null){
                    errorPromoteCallback.onPromoteStarted();
                }
            }
        });
        rkAudioPlayer.setmOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (errorPromoteCallback != null){
                    errorPromoteCallback.onPromoteFinished();
                }
            }
        });
    }

    public static ErrorPromoter getInstance() {
        if (errorPromoteHelper == null) {
            synchronized (ErrorPromoter.class) {
                if (errorPromoteHelper == null)
                    errorPromoteHelper = new ErrorPromoter();
            }
        }
        return errorPromoteHelper;
    }

    public void speakErrorPromote(ERROR_TYPE errorType, ErrorPromoteCallback errorPromoteCallback) throws IOException {
        Logger.d(" speakErrorPromote errorType is " + errorType);
        this.errorPromoteCallback = errorPromoteCallback;
        switch (errorType) {
            case MEDIA_TIME_OUT:
                //突然卡了一下，稍后再试吧。
                rkAudioPlayer.setAssetVideo(assetManager.openFd("media_timeout.mp3"));
                break;
            case MEDIA_ERROR:
                //找不到要播放的文件，换一个试试吧。
                rkAudioPlayer.setAssetVideo(assetManager.openFd("media_error.mp3"));
                break;
            case DATA_INVALID:
            case TTS_ERROR:
                //遇到了一点小问题，稍后再试一下吧
                rkAudioPlayer.setAssetVideo(assetManager.openFd("common_error.mp3"));
                break;
        }
        rkAudioPlayer.start();
    }

    public interface ErrorPromoteCallback {
        void onPromoteStarted();
        void onPromoteFinished();
    }

    public enum ERROR_TYPE {
        DATA_INVALID,
        MEDIA_TIME_OUT,
        MEDIA_ERROR,
        TTS_ERROR
    }

}
