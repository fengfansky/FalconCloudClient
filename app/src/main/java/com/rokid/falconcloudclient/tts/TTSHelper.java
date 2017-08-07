package com.rokid.falconcloudclient.tts;

import android.text.TextUtils;

import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.state.CloudStateMonitor;
import com.rokid.falconcloudclient.util.Logger;

import rokid.os.RKTTS;
import rokid.os.RKTTSCallback;

/**
 * This is a TTS tools, used to send the TTS, stop the TTS.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/10
 */
public class TTSHelper {
    private static final int STOP = -1;
    private volatile static TTSHelper instance;
    private RKTTS mRktts = new RKTTS();
    private volatile int ttsId = STOP;
    public boolean isPaused = false;
    public static TTSHelper getInstance() {
        if (null == instance) {
            synchronized (TTSHelper.class) {
                if (null == instance) {
                    instance = new TTSHelper();
                }
            }
        }

        return instance;
    }

    public void speakTTS(String ttsContent) {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            return;
        }

        if (ttsId > 0) {
            mRktts.stop(ttsId);
        }

        ttsId = mRktts.speak(ttsContent, rkttsCallback);
        Logger.d(" speak TTS ttiId " + ttsId);

        if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
            FalconCloudTask.getInstance().getCloudStateMonitor().setCurrentVoiceState(CloudStateMonitor.VOICE_STATE.VOICE_START);
        }
    }

    private RKTTSCallback rkttsCallback = new RKTTSCallback() {
        @Override
        public void onStart(int id) {
            super.onStart(id);
            Logger.i("TTS is onTTSStart - id: " + id);
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null){
                FalconCloudTask.getInstance().getCloudStateMonitor().onVoiceStart();
            }
        }

        @Override
        public void onCancel(int id) {
            super.onCancel(id);
            Logger.i("TTS is onCancel - id: " + id + ", current id: " + ttsId + " isPaused : " + isPaused);
            if (id != ttsId) {
                Logger.i("The new tts is already speaking, previous tts stop should not ttsCallback");
                return;
            }
            ttsId = STOP;
            if (isPaused){
                if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                    FalconCloudTask.getInstance().getCloudStateMonitor().onVoicePaused();
                }
            }else {
                if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                    FalconCloudTask.getInstance().getCloudStateMonitor().onVoiceCancled();
                }
            }
        }

        @Override
        public void onComplete(int id) {
            super.onComplete(id);
            Logger.i("TTS is onComplete - id: " + id);
            ttsId = STOP;
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                FalconCloudTask.getInstance().getCloudStateMonitor().onVoiceStop();
            }
        }

        @Override
        public void onError(int id, int err) {
            super.onError(id, err);
            Logger.i("tts onError - id: " + id + ", error: " + err);
            ttsId = STOP;
            if (FalconCloudTask.getInstance().getCloudStateMonitor() != null) {
                FalconCloudTask.getInstance().getCloudStateMonitor().onVoiceError();
            }
        }
    };

    public void stopTTS() {
        if (ttsId > 0) {
            isPaused = false;
            mRktts.stop(ttsId);
        }
    }

    public void pauseTTS(){
        if (ttsId > 0){
            isPaused = true;
            mRktts.stop(ttsId);
        }
    }

}
