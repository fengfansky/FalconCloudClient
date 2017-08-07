package com.rokid.falconcloudclient;

import android.animation.AnimatorSet;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rokid.falconcloudclient.state.CloudStateMonitor;
import com.rokid.falconcloudclient.state.CloudCutState;
import com.rokid.falconcloudclient.state.CloudSceneState;
import com.rokid.falconcloudclient.bean.ActionNode;
import com.rokid.falconcloudclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.falconcloudclient.http.BaseUrlConfig;
import com.rokid.falconcloudclient.parser.ResponseParser;
import com.rokid.falconcloudclient.player.ErrorPromoter;
import com.rokid.falconcloudclient.util.Logger;
import com.rokid.rkcontext.RKBaseTask;
import com.rokid.rkcontext.RokidState;
import com.rokid.rkcontext.utils.LogUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Modified by fanfeng on 2017/7/20.
 */
public class FalconCloudTask extends RKBaseTask implements CloudStateMonitor.TaskProcessCallback{

    private static FalconCloudTask instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        BaseUrlConfig.initDeviceInfo();

        //This for test
        LogUtils.addLogListener(logListener);

        setNeedsMenuKey();
    }

    public static FalconCloudTask getInstance() {
        return instance;
    }

    @Override
    protected void onNlpMessage(String nlp) {
        //TODO set asr action
        String asr = "";
        String actionStr = "";

        ActionNode actionNode = null;
        try {
            actionNode = ResponseParser.getInstance().parseMessage(asr, nlp, actionStr);
        } catch (IOException e) {
            Logger.e(" speak error info exception !");
            e.printStackTrace();
        }

        if (actionNode == null){
            try {
                ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            } catch (IOException e) {
                Logger.e(" speak error info exception !");
                e.printStackTrace();
            }
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("data",actionNode);

        switch (actionNode.getForm()){
            case ActionBean.FORM_CUT:
                startState(CloudSceneState.class, bundle);
                break;
            case ActionBean.FORM_SCENE:
                startState(CloudCutState.class, bundle);
                break;
        }
        if(getCloudStateMonitor() != null){
            getCloudStateMonitor().setTaskProcessCallback(this);
        }

    }

    @Override
    protected RokidState getState() {

        return super.getState();
    }

    public CloudStateMonitor getCloudStateMonitor(){
        if (getState() == null){
            Logger.d(" rokidState is null !");
            return null;
        }

        if (getState() instanceof CloudSceneState){
            return ((CloudSceneState) getState()).getCloudStateMonitor();
        }else if (getState() instanceof CloudCutState){
            return ((CloudCutState) getState()).getCloudStateMonitor();
        }

        return null;
    }

    public void openSiren() {
        Logger.d(" process confirm ");
        Intent intent = new Intent();
        ComponentName compontent = new ComponentName("com.rokid.activation", "com.rokid.activation.service.CoreService");
        intent.setComponent(compontent);
        intent.putExtra("InputAction", "confirmEvent");
        Bundle bundle = new Bundle();
        bundle.putInt("isConfirm", 1);   //isConfirm  参数 目前支持 1: 打开拾音 , 0: 关闭拾音
        intent.putExtra("intent", bundle);
        startService(intent);
    }

    @Override
    public void onAllTaskFinished() {
        //TODO 此处应用应该置于后台 回调OnPaused
        finish();
    }

    @Override
    public void onExitCallback() {
        //此处应用应该destory
        finish();
    }


    /************* for  test ***********/
    private TextView tv;
    private TextView tv2;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.removeLogListener(logListener);
    }

    @Override
    public AnimatorSet getAppearingAnimator() {

        return null;
    }

    @Override
    public AnimatorSet getDisappearingAnimator() {

        return null;
    }

    @Override
    protected View initBodyView() {

        View view = inflate(R.layout.activity_main);
        tv = (TextView)view.findViewById(R.id.textView);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv2 = (TextView)view.findViewById(R.id.textView2);
        tv2.setMovementMethod(ScrollingMovementMethod.getInstance());

        return view;
    }


    private LogUtils.LogListener logListener = new LogUtils.LogListener() {
        @Override
        public void onLog(String tag, String msg) {
            if("RokidState".equals(tag)){
                //
                tv.append(msg + "\n");
            }else if("BackStage".equals(tag)){
                //
                tv2.append(msg + "\n");
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.start_cut1:
                Bundle ba = new Bundle();
                ba.putString("param1","data1");
                startState(CloudCutState.class,ba);
                break;
            case R.id.start_scene1:
                Bundle bc = new Bundle();
                bc.putString("param3","data3");
                startState(CloudSceneState.class,bc);
                break;
            case R.id.nlp:
                onNlpMessage("I'm nlp message");
                break;
            case R.id.stop_backstageA:
                sendBroadcast(new Intent("stop_backstageA"));
                break;
            case R.id.stop_backstageB:
                sendBroadcast(new Intent("stop_backstageB"));
                break;
            case R.id.clean:
                tv.setText("");
                tv2.setText("");
                break;
        }
        return true;
    }


    private void setNeedsMenuKey() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return;
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            try {
                int flags = WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null);
                getWindow().addFlags(flags);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Method setNeedsMenuKey = Window.class.getDeclaredMethod("setNeedsMenuKey", int.class);
                setNeedsMenuKey.setAccessible(true);
                int value = WindowManager.LayoutParams.class.getField("NEEDS_MENU_SET_TRUE").getInt(null);
                setNeedsMenuKey.invoke(getWindow(), value);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
