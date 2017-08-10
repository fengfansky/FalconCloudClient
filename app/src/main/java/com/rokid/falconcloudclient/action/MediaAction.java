package com.rokid.falconcloudclient.action;

import android.net.Uri;
import android.text.TextUtils;

import com.rokid.falconcloudclient.FalconCloudTask;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.media.MediaItemBean;
import com.rokid.falconcloudclient.player.RKAudioPlayer;
import com.rokid.falconcloudclient.state.CloudStateMonitor;
import com.rokid.falconcloudclient.util.Logger;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MediaAction extends BaseAction<MediaBean> {

    private CloudStateMonitor cloudStateMonitor;

    private RKAudioPlayer rkAudioPlayer;

    public MediaAction(CloudStateMonitor cloudStateMonitor) {
        this.cloudStateMonitor = cloudStateMonitor;
        initRKAudioPlayer();
    }

    private void initRKAudioPlayer() {
        rkAudioPlayer = new RKAudioPlayer(FalconCloudTask.getInstance());

        rkAudioPlayer.setmOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                if (cloudStateMonitor != null){
                    cloudStateMonitor.onMediaStart();
                }
            }
        });

        rkAudioPlayer.setmOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                Logger.d(" onMediaError what : " + what + " extra :" + extra);
                if (cloudStateMonitor != null) {
                    cloudStateMonitor.onMediaError(extra);
                }
                return false;
            }
        });

        rkAudioPlayer.setmOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (cloudStateMonitor != null) {
                    cloudStateMonitor.onMediaStop();
                }
            }
        });
        rkAudioPlayer.setmOnPausedListener(new IMediaPlayer.OnPausedListener() {
            @Override
            public void onPaused(IMediaPlayer mp) {
                if (cloudStateMonitor != null) {
                    cloudStateMonitor.onMediaPause((int) mp.getCurrentPosition());
                }
            }
        });
        rkAudioPlayer.setmOnStopedListener(new IMediaPlayer.OnStopedListener() {
            @Override
            public void onStoped(IMediaPlayer mp) {
                if (cloudStateMonitor != null) {
                    cloudStateMonitor.onMediaStop();
                }
            }
        });
    }
    
    public synchronized void userStartPlay(MediaBean mediaBean) {
        Logger.d(" rkAudioPlayer is null ? " + (rkAudioPlayer == null));
        if (mediaBean == null) {
            Logger.d(" userStartPlay mediaBean is null ");
            return;
        }

        if (rkAudioPlayer == null) {
            initRKAudioPlayer();
        }
        MediaItemBean mediaBeanItem = mediaBean.getItem();
        if (mediaBeanItem == null) {
            Logger.d("start play media mediaBeanItem null!");
            return;
        }

        Logger.d("play mediaBean : " + mediaBean);

        String url = mediaBeanItem.getUrl();
        if (!TextUtils.isEmpty(mediaBeanItem.getToken())) {
            url.concat("&").concat(mediaBeanItem.getToken());
        }

        if (TextUtils.isEmpty(url)) {
            Logger.d("media url invalidate!");
            return;
        }

        Logger.d("start play media url : " + url);
        if (cloudStateMonitor != null) {
            cloudStateMonitor.setUserMediaControlType(CloudStateMonitor.USER_MEDIA_CONTROL_TYPE.MEDIA_START);
        }
        rkAudioPlayer.setVideoURI(Uri.parse(url));
        rkAudioPlayer.start();
        rkAudioPlayer.seekTo(mediaBeanItem.getOffsetInMilliseconds());
    }

    @Override
    public synchronized void pausePlay() {
        Logger.d(" pausePlay  canPause: " + rkAudioPlayer.canPause());
        if (rkAudioPlayer != null && rkAudioPlayer.canPause()) {
            rkAudioPlayer.pause();
        }
    }

    @Override
    public synchronized void stopPlay() {
        Logger.d(" stopPlay");
        if (rkAudioPlayer != null) {
            rkAudioPlayer.stop();
        }
    }

    @Override
    public synchronized void resumePlay() {
        Logger.d(" resumePlay isPlaying: " + rkAudioPlayer.isPlaying());
        if (rkAudioPlayer != null && !rkAudioPlayer.isPlaying()) {
            rkAudioPlayer.start();
            if (cloudStateMonitor != null) {
                cloudStateMonitor.onMediaResume();
            }
        }
    }

    @Override
    public synchronized void userPausedPlay() {
        pausePlay();
        if (cloudStateMonitor != null) {
            cloudStateMonitor.setUserMediaControlType(CloudStateMonitor.USER_MEDIA_CONTROL_TYPE.MEDIA_PAUSE);
        }
    }

    @Override
    public synchronized void userStopPlay() {
        stopPlay();
        if (cloudStateMonitor != null) {
            cloudStateMonitor.setUserMediaControlType(CloudStateMonitor.USER_MEDIA_CONTROL_TYPE.MEDIA_STOP);
        }
    }

    @Override
    public synchronized void userResumePlay() {
        resumePlay();
        if (cloudStateMonitor != null) {
            cloudStateMonitor.setUserMediaControlType(CloudStateMonitor.USER_MEDIA_CONTROL_TYPE.MEDIA_RESUME);
        }
    }

    @Override
    public synchronized void forward() {
        if (rkAudioPlayer != null && !rkAudioPlayer.canSeekForward()) {
            int totalTime = rkAudioPlayer.getDuration();
            int currentTime = rkAudioPlayer.getCurrentPosition();
            int seekTime = currentTime + totalTime / 10;
            if (seekTime > totalTime) {
                seekTime = totalTime;
            }
            rkAudioPlayer.seekTo(seekTime);
        }
    }

    @Override
    public synchronized void backward() {
        if (rkAudioPlayer != null && !rkAudioPlayer.canSeekForward()) {
            int totalTime = rkAudioPlayer.getDuration();
            int currentTime = rkAudioPlayer.getCurrentPosition();
            int seekTime = currentTime - totalTime / 10;
            if (seekTime <= 0) {
                seekTime = 0;
            }
            rkAudioPlayer.seekTo(seekTime);
        }
    }

    public int getMediaDuration() {
        if (rkAudioPlayer == null) {
            return 0;
        }
        return rkAudioPlayer.getDuration();
    }

    public int getMediaPosition() {
        if (rkAudioPlayer == null) {
            return 0;
        }
        return rkAudioPlayer.getCurrentPosition();
    }

    public void releasePlayer() {
        if (rkAudioPlayer != null) {
            rkAudioPlayer.release(true);
            rkAudioPlayer = null;
        }
    }

    @Override
    public ACTION_TYPE getActionType() {
        return ACTION_TYPE.MEDIA;
    }

}
