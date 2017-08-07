package com.rokid.falconcloudclient.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rokid.falconcloudclient.bean.base.BaseBean;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.confirm.ConfirmBean;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.voice.VoiceBean;

/**
 * Created by fengfan on 3/16/17.
 */

public class ActionNode extends BaseBean implements Parcelable{
    private String asr;
    private NLPBean nlp;
    private String respId;
    private String resType;
    private String appId;
    private String form;
    private String actionType;
    private boolean shouldEndSession;
    private VoiceBean voice;
    private MediaBean media;
    private ConfirmBean confirmBean;

    public ActionNode() {
    }

    protected ActionNode(Parcel in) {
        asr = in.readString();
        respId = in.readString();
        resType = in.readString();
        appId = in.readString();
        form = in.readString();
        actionType = in.readString();
        shouldEndSession = in.readByte() != 0;
    }

    public static final Creator<ActionNode> CREATOR = new Creator<ActionNode>() {
        @Override
        public ActionNode createFromParcel(Parcel in) {
            return new ActionNode(in);
        }

        @Override
        public ActionNode[] newArray(int size) {
            return new ActionNode[size];
        }
    };

    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    public NLPBean getNlp() {
        return nlp;
    }

    public void setNlp(NLPBean nlp) {
        this.nlp = nlp;
    }

    public String getRespId() {
        return respId;
    }

    public void setRespId(String respId) {
        this.respId = respId;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String respType) {
        this.resType = respType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public boolean isShouldEndSession() {
        return shouldEndSession;
    }

    public void setShouldEndSession(boolean shouldEndSession) {
        this.shouldEndSession = shouldEndSession;
    }

    public VoiceBean getVoice() {
        return voice;
    }

    public void setVoice(VoiceBean voice) {
        this.voice = voice;
    }

    public MediaBean getMedia() {
        return media;
    }

    public void setMedia(MediaBean media) {
        this.media = media;
    }

    public ConfirmBean getConfirmBean() {
        return confirmBean;
    }

    public void setConfirmBean(ConfirmBean confirmBean) {
        this.confirmBean = confirmBean;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(asr);
        dest.writeString(respId);
        dest.writeString(resType);
        dest.writeString(appId);
        dest.writeString(form);
        dest.writeString(actionType);
        dest.writeByte((byte) (shouldEndSession ? 1 : 0));
    }
}
