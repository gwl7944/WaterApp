package com.example.jpushdemo;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.taxiang.R;
import com.gwl.encryption.DataEncryption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import taxiang.App;
import taxiang.DingDanBean;
import taxiang.DingDanFragment;
import taxiang.DingDanInfoActivity;
import taxiang.DingDanShopBean;
import taxiang.LoginActivity;
import taxiang.TaXiangActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PushMessageReceiver extends JPushMessageReceiver{
    private static final String TAG = "PushMessageReceiver";
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Log.e(TAG,"[onMessage] "+customMessage);
        processCustomMessage(context,customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageOpened] "+message);
//        try{
//            //打开自定义的Activity
//            Intent i = new Intent(context, TaXiangActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE,message.notificationTitle);
//            bundle.putString(JPushInterface.EXTRA_ALERT,message.notificationContent);
//            i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//            context.startActivity(i);
//        }catch (Throwable throwable){
//
//        }

        try {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
            //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
            notification.setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher);

            notification.setSound(
                    Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.jiedan));


            //最后刷新notification是必须的
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1,notification.build());

            final JSONObject object=new JSONObject(message.notificationExtras);

            Log.e(TAG,"[onNotifyMessageArrived] "+object.getString("order_number"));
             if(message.notificationTitle.contains("新退押金单提醒")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            SharedPreferences sp = App.getApp().getSharedPreferences("SI",  0x0000);
                            SharedPreferences.Editor editor = sp.edit();//获取编辑器
                            editor.putString("document", object.getString("order_number"));
                            editor.commit();
                            TaXiangActivity.instance.staffGetDepositInfo(object.getString("order_number"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Looper.loop();
                    }
                }).start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if(nActionExtra==null){
            Log.d(TAG,"ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (nActionExtra.equals("my_extra1")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义");
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageArrived] "+message);
        Log.e(TAG,"[onNotifyMessageArrived] "+message.notificationExtras);

        try {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
            //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
            notification.setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher);

            notification.setSound(
                    Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.jiedan));


            //最后刷新notification是必须的
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1,notification.build());

            final JSONObject object=new JSONObject(message.notificationExtras);

            Log.e(TAG,"[onNotifyMessageArrived] "+object.getString("order_number"));
            if(message.notificationTitle.contains("新押金单提醒")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        DingDanInfoActivity.instance.staffSelectOrderDepositInfo();
                        //                BmainActivity bmainActivity=new BmainActivity();
//                bmainActivity.selectDeductImpurityList();
                        Looper.loop();
                    }
                }).start();
            }else  if(message.notificationTitle.contains("新退押金单提醒")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            TaXiangActivity.instance.staffGetDepositInfo(object.getString("order_number"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Looper.loop();
                    }
                }).start();
            }else if(message.notificationTitle.contains("新订单提醒")){
                String url="https://tx.naturallywater.com:8889/applets/customer/updateOrderState?"+"order_number="+object.getString("order_number")+"&order_state=2";
                Log.e(TAG,"[onNotifyMessageArrived] "+url);
                RequestQueue requestQueue= Volley.newRequestQueue(DingDanFragment.instance.getActivity());

                // 1 创建一个get请求
                StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
                    //正确接受数据之后的回调
                    @Override
                    public void onResponse(String response) {
                        try {
                            String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                            JSONObject object=new JSONObject(newResponse);
//                    String ss=object.getString("message");
                            Log.e("sssss",object.toString());

                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {//发生异常之后的监听回调
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ssssssssss",""+error);
                    }
                });
                //将创建的请求添加到请求队列当中
                requestQueue.add(stringRequest);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    DingDanFragment dingDanFragment=new DingDanFragment();
                    dingDanFragment.instance.selectDeductImpurityList();
//                BmainActivity bmainActivity=new BmainActivity();
//                bmainActivity.selectDeductImpurityList();
                    Looper.loop();
                }
            }).start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageDismiss] "+message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        Log.e(TAG,"[onRegister] "+registrationId);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        Log.e(TAG,"[onConnected] "+isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        Log.e(TAG,"[onCommandResult] "+cmdMessage);
    }

    @Override
    public void onTagOperatorResult(Context context,JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context,JPushMessage jPushMessage){
        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, CustomMessage customMessage) {
        if (MainActivity.isForeground) {
            String message = customMessage.message;
            String extras = customMessage.extra;
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }

        }
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
        Log.e(TAG,"[onNotificationSettingsCheck] isOn:"+isOn+",source:"+source);
    }
    
}
