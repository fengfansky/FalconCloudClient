package com.rokid.falconcloudclient.bean;

import com.rokid.falconcloudclient.bean.base.BaseBean;
import com.rokid.falconcloudclient.bean.response.CloudActionResponseBean;

/**
 * Created by showingcp on 3/13/17.
 */

public class CommonResponseBean extends BaseBean{

    /**
     * corresponding asr result for current response
     */
    private String asr;

    /**
     * corresponding nlp result for current response
     */
    private NLPBean nlp;

    /**
     * corresponding CloudActionResponseBean for current response
     */
    private CloudActionResponseBean action;

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

    public CloudActionResponseBean getAction() {
        return action;
    }

    public void setAction(CloudActionResponseBean action) {
        this.action = action;
    }
}
