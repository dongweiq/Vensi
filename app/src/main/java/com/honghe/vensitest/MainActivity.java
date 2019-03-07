package com.honghe.vensitest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lmiot.lmiot_mqtt_sdk.MqttActionListener;
import com.lmiot.lmiot_mqtt_sdk.MqttManager;
import com.lmiot.lmiot_mqtt_sdk.MqttService;
import com.lmiot.lmiot_mqtt_sdk.api.HostApi;
import com.lmiot.lmiot_mqtt_sdk.api.device.DeviceBaseApi;
import com.lmiot.lmiot_mqtt_sdk.bean.device.DeviceList;
import com.lmiot.lmiot_mqtt_sdk.bean.device.DeviceState;
import com.lmiot.lmiot_mqtt_sdk.bean.host.HostLogin;
import com.lmiot.lmiot_mqtt_sdk.callback.IBaseCallback;
import com.lmiot.lmiot_mqtt_sdk.callback.OnStateChangedListener;
import com.lmiot.lmiot_mqtt_sdk.util.Logger;

import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HostApi mHostApi;
    // MQTT 服务器 ip
    protected static final String MQTT_SERVICE_IP_TEST = "mqtt.vensi.cn:1883";
    // 用户 id
    protected static final String mUniqueId = "861608044873816";
    // 网关 id
    protected static final String HOST_ID = "a09dc10a00e4";
    protected static final String DEVICE_ID = "000D6F000BE7D0B101";
    DeviceBaseApi mDeviceBaseApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHostApi = new HostApi(MQTT_SERVICE_IP_TEST, mUniqueId, HOST_ID);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });
    }

    private void connect() {
        Logger.d("connect");
        MqttManager.getInstance().setOs("MI 2s");
        MqttManager.getInstance().setType("app");

        MqttManager.getInstance().createConnect(this.getApplicationContext(), MQTT_SERVICE_IP_TEST);
        MqttManager.getInstance().addOnMqttServiceStateChangedListener(MQTT_SERVICE_IP_TEST, new OnStateChangedListener() {
            @Override
            public void onStateChanged(int state, int retryCount, Throwable cause) {
                switch (state) {
                    case MqttService.MQTT_STATE_INIT:
                        Logger.d("初始化中");
                        break;
                    case MqttService.MQTT_STATE_CONNECTING:
                        break;
                    case MqttService.MQTT_STATE_CONNECTED:
                        Logger.d("连接服务器成功");
                        subscribeUserId();
                        break;
                    case MqttService.MQTT_STATE_CONNECT_FAILURE:
                        break;
                    case MqttService.MQTT_STATE_CONNECT_LOST:
                        break;
                    case MqttService.MQTT_STATE_CONNECT_RETRY:
                        break;
                    case MqttService.MQTT_STATE_NETWORK_UNAVAILABLE:
                        break;
                    case MqttService.MQTT_STATE_CONNECT_FAILURE_OUT_OF_RETRY_COUNT:
                        break;
                }
            }
        });
    }

    private void subscribeUserId() {
        Logger.d("subscribeUserId");
        MqttManager.getInstance().subscribe(MQTT_SERVICE_IP_TEST, mUniqueId, 1, new MqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.d("subscribeUserId onSuccess ");
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                mHostApi.loginHost(new IBaseCallback<HostLogin.Recv>() {
                    @Override
                    public void onSuccess(HostLogin.Recv recv, String originMsg, String errorCode) {
                        if (TextUtils.equals(errorCode, "10025")) {
                            VensiUtil.showNotice("正在等待管理员同意");
                        } else {
                            VensiUtil.showNotice("登录成功" + recv.getSessionId());
                            mDeviceBaseApi = new DeviceBaseApi(MQTT_SERVICE_IP_TEST, mUniqueId, HOST_ID);
                            MqttManager.getInstance().putHostSessionId(MQTT_SERVICE_IP_TEST, recv.getSessionId());
                            MqttManager.getInstance().subscribe(MQTT_SERVICE_IP_TEST, recv.getSessionId(), 1, new MqttActionListener());
                            MqttManager.getInstance().subscribe(MQTT_SERVICE_IP_TEST, recv.getHostId() + "mqtt", 1, new MqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
//                            registerDeviceEvent();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(String msg, String errorCode) {
                        if (TextUtils.equals(errorCode, "-1")) {
                            VensiUtil.showNotice("登录失败");
                        }
                    }
                });
                break;
            case R.id.btnGetDevices:

                mDeviceBaseApi.getDeviceList(new IBaseCallback<DeviceList.Recv>() {
                    @Override
                    public void onSuccess(DeviceList.Recv recv, String originMsg, String errorCode) {
                        if (recv.getConfig() == null || recv.getConfig().isEmpty()) return;
                        for (DeviceList.Recv.Device device : recv.getConfig()) {
                            WHHLog.d(device.getDeviceId());
                        }
                    }

                    @Override
                    public void onFailure(String msg, String errorCode) {
                        if (TextUtils.equals(errorCode, "-1")) {
                            VensiUtil.showNotice("获取失败");
                        }
                    }
                });
                break;
            case R.id.btnRefreshDevices:

                List<String> deviceIds = new ArrayList<>();
                deviceIds.add(DEVICE_ID);
                mDeviceBaseApi.getDevicesState(deviceIds, new IBaseCallback<DeviceState.Recv>() {
                    @Override
                    public void onSuccess(DeviceState.Recv recv, String originMsg, String errorCode) {
                        DeviceState.State device = recv.getStateList().get(0);
                        VensiUtil.showNotice(device.getId() + " " + device.getStatus());
                    }

                    @Override
                    public void onFailure(String msg, String errorCode) {
                        if (TextUtils.equals(errorCode, "-1")) {
                            VensiUtil.showNotice("获取失败");
                        }
                    }
                });
                break;
            case R.id.btnOpen:
                mDeviceBaseApi.controlDevice(DEVICE_ID, "", "on", new IBaseCallback<String>() {
                    @Override
                    public void onSuccess(String recv, String originMsg, String errorCode) {
                        VensiUtil.showNotice("控制成功");
                    }

                    @Override
                    public void onFailure(String s, String s1) {

                    }
                });
                break;
            case R.id.btnClose:
                mDeviceBaseApi.controlDevice(DEVICE_ID, "", "off", new IBaseCallback<String>() {
                    @Override
                    public void onSuccess(String recv, String originMsg, String errorCode) {
                        VensiUtil.showNotice("控制成功");
                    }

                    @Override
                    public void onFailure(String s, String s1) {

                    }
                });
                break;
        }
    }
}
