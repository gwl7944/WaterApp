package taxiang;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.taxiang.R;
import com.gwl.encryption.DataEncryption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import cn.jiguang.verifysdk.api.JVerificationInterface;
import cn.jiguang.verifysdk.api.JVerifyUIConfig;
import cn.jiguang.verifysdk.api.VerifyListener;
import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText username,password;
    private TextView login;
    private String rid;
    private boolean mHasPermission;
    private int winWidth;
    private int winHeight;
    private LinearLayout mProgressbar;
    private boolean isfrist=false;
    private String phone;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        MyImageView.ignoreSSLHandshake();
        if (point.x>point.y){
            winHeight = point.x;
            winWidth =   point.y;
        }else {
            winHeight =   point.y;
            winWidth = point.x;
        }

        setContentView(R.layout.activity_login);
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        initView();
        initPermission();
        initListener();
    }
    private void initView(){
        rid = JPushInterface.getRegistrationID(getApplicationContext());
        Log.e("ssss",rid);
        mProgressbar = (LinearLayout) findViewById(R.id.progressbar);
        login=findViewById(R.id.login);

        mProgressbar.setVisibility(View.GONE);
        SharedPreferences sp = LoginActivity.this.getSharedPreferences("SI", MODE_PRIVATE);
        phone = sp.getString("PHONE", "");
        id = sp.getInt("ID", 0);
        if(!phone.equals("")){
            final String aa=  DataEncryption.ENCRYPT(rid,null);
            Log.e("sssssssssss",aa);
            String url="https://tx.naturallywater.com:8889/app/checklogin?"+"staff_identification_code="+aa+"&staff_telephone="+phone;
            Log.e("sasss",url);
            RequestQueue requestQueue= Volley.newRequestQueue(LoginActivity.this);
            // 1 创建一个get请求

            StringRequest stringRequest=new StringRequest(url, new com.android.volley.Response.Listener<String>() {
                //正确接受数据之后的回调
                @Override
                public void onResponse(String response) {
                    try {
                        String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                        JSONObject object=new JSONObject(newResponse);
//                                String ss=object.getString("message");
                        Log.e("sssssss",object.toString());
                        if(object.getInt("code")==168){
                            isfrist=true;
                            SharedPreferences sp = LoginActivity.this.getSharedPreferences("SI", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();//获取编辑器
                            editor.putString("PHONE", phone);
                            editor.putString("document", "");
                            editor.commit();
                            JSONObject jsonObject=  object.getJSONObject("data");
                            int  staff_id= jsonObject.getInt("staff_id");
                            String staff_name=jsonObject.getString("staff_name");
                            String staff_loginname=jsonObject.getString("staff_loginname");
                            String staff_telephone=jsonObject.getString("staff_telephone");
                            int staff_gender=jsonObject.getInt("staff_gender");
                            String staff_job_number=jsonObject.getString("staff_job_number");
                            String staff_position=jsonObject.getString("staff_position");
                            int staff_spn_id=jsonObject.getInt("staff_spn_id");
                            if(staff_spn_id==1){
                                Intent intent=new Intent(LoginActivity.this,TaXiangActivity.class);

                                intent.putExtra("ID",1);
                                intent.putExtra("staff_id",staff_id);
                                intent.putExtra("staff_name",staff_name);
                                intent.putExtra("staff_loginname",staff_loginname);
                                intent.putExtra("staff_telephone",staff_telephone);
                                intent.putExtra("staff_gender",staff_gender);
                                intent.putExtra("staff_job_number",staff_job_number);
                                intent.putExtra("staff_position",staff_position);
                                intent.putExtra("staff_spn_id",staff_spn_id);
                                intent.putExtra("identifyNumber",rid);
                                intent.putExtra("status","0");
                                startActivity(intent);
                            }else{
                                Intent intent=new Intent(LoginActivity.this,kuGuanActivity.class);
                                intent.putExtra("ID",1);
                                intent.putExtra("staff_id",staff_id);
                                intent.putExtra("staff_name",staff_name);
                                intent.putExtra("staff_loginname",staff_loginname);
                                intent.putExtra("staff_telephone",staff_telephone);
                                intent.putExtra("staff_gender",staff_gender);
                                intent.putExtra("staff_job_number",staff_job_number);
                                intent.putExtra("staff_position",staff_position);
                                intent.putExtra("staff_spn_id",staff_spn_id);
                                intent.putExtra("identifyNumber",rid);
                                intent.putExtra("status","0");
                                startActivity(intent);
                            }

                        }else if(object.getInt("code")==409){
                                Intent intent=new Intent(getApplicationContext(),RegisActivity.class);
                                intent.putExtra("phone",phone);
                                intent.putExtra("identifyNumber",aa);
                                intent.putExtra("status",isfrist);
                                startActivity(intent);

                        }
//                                Toast.makeText(getApplicationContext(),ss,Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {//发生异常之后的监听回调
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ssss",""+error);
                }
            });
            // 3 将创建的请求添加到请求队列中
            requestQueue.add(stringRequest);
        }
    }
    @SuppressLint("WrongConstant")
    private void initPermission() {
        PermissionUtils.permission(PermissionConstants.STORAGE,PermissionConstants.PHONE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        mHasPermission = true;
                    }

                    @Override
                    public void onDenied() {
                        mHasPermission = false;
                    }
                }).request();
    }
    private void initListener(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mHasPermission){
                    ToastUtil.show(App.getApp(),"请先授予权限");
                    initPermission();
                    return;
                }
                mProgressbar.setVisibility(View.VISIBLE);
                JVerificationInterface.setCustomUIWithConfig(getDialogPortraitConfig(),getDialogLandscapeConfig());
                JVerificationInterface.loginAuth(getApplicationContext(), new VerifyListener() {
                    @Override
                    public void onResult(final int code, final String token, String operator) {
                        Log.e("TAG", "onResult: code=" + code + ",token=" + token + ",operator=" + operator);
                        final String errorMsg = "operator=" + operator + ",code=" + code + "\ncontent=" + token;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressbar.setVisibility(View.GONE);
//                                btnLoginDialog.setEnabled(true);
//                                btnLogin.setEnabled(true);
                                    id=1;
                                    getPhoneNum(token);
                                if (code == Constants.CODE_LOGIN_SUCCESS) {
//                                    toSuccessActivity(Constants.ACTION_LOGIN_SUCCESS,token);
                                    Log.e("TAG", "onResult: loginSuccess");
                                } else if(code != Constants.CODE_LOGIN_CANCELD){
                                    Log.e("TAG", "onResult: loginError");
//                                    toFailedActivigy(code,token);
                                }
                            }
                        });
                    }
                });


            }
        });

    }
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private void getPhoneNum(final String token){
        new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                super.run();
                try {
//                    Log.e("TAG","Base64:"+ string);
                    OkHttpClient client = new OkHttpClient();
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put("loginToken",token);
                    bodyJson.put("exID","taxiang");
                    String body = bodyJson.toString();
                    RequestBody requestBody = RequestBody.create(JSON,body);
                    Log.d("TAG","request url:"+  Constants.verifyUrl);
                    Request request = new Request.Builder().url(Constants.verifyUrl).addHeader("Authorization","Basic " + Base64.getUrlEncoder()
                            .encodeToString(("7ab3a8d53b416dc3be3c760e"+ ":" + "5729c032cf183c128ef7835b").getBytes())).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("TAG","response :"+responseData);
                    JSONObject responseJson = new JSONObject(responseData);
                    String phone = responseJson.optString("phone");
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    message.obj = phone;
//                    phoneNum = phone;
                    handler.sendMessage(message);
                } catch (Throwable e) {
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        }.start();

    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
//                tvSuccess.setText("登录成功!");
//                tvPhone.setVisibility(View.VISIBLE);
                Log.e("ssss",(String)msg.obj);
                String encrypted=(String)msg.obj;
 //                String prikey=
//                        "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALyMmEkUfHNhLq/+8KoHHkCk2GM/uCRapBV94nHfJTFy7NdBbiJEehjxGwEPPnppGyris9+sBPgaGEOTbJ98S2I3K1LuuFaejUdbZfDZWTeRj9yr1Df9LuS9kfaSDqpPwbqigAL0PuKBU6h6rctMWoGI6jAdI+rkz9oszPtGEbgXAgMBAAECgYBMXqIcD6kszrMrZBa0t7dgNaTT5m4+g1wsvFVpDBozgjo+IelrBdNyoM3wSu1ihYoUf8idkz5cA63KST9ZBe4+AnpP0G9lIdQkk0jwRK0scgBCgRrVIXIPb3E/fabqxz+sPdoNE/2lm0mqvWOGs8xuUAcbyVVy+YToTA+f4n31gQJBAOsObh/u5xW7/pYZYDqHCTNQpHfz1b8aYthQoYZ1/Oi6JeiciJqYvd2AYKmVw9DJA/lB+bq1iv0uGMGbcxitZEECQQDNWVllgswsLLIjDZgKU/z6wyUEZ81zwbvto46XjkvXlwzO4/EHY2kX/BMLVNfqVBEXNYccM4YiSa28Uc+ZgyZXAkEA44RtLbWlsZDtJMZdMWmBi0z0VTMMYtScdnrdTfQon++GP/g79UABD0Ryy9Mt3YqksQCeQ43zgYq75bKcnnc+wQJBALCRw2ylqynQxNufNLysuzpj0dCX8PE4BH+xw0pN8KjQGRNMrFPOBu+4sCduLLowzwFF5Yc6Rnv1Ho5vSjKVssECQFAIYNYe7wB+T8KXqACllfLNsaVF4GFWZsLkhVoIDkd6UcWroSyQWbgOnxGN4FbkKXBCCGqdbPaovZy/7GY6cAg=";
                String prikey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALyMmEkUfHNhLq/+\n" +
                        "8KoHHkCk2GM/uCRapBV94nHfJTFy7NdBbiJEehjxGwEPPnppGyris9+sBPgaGEOT\n" +
                        "bJ98S2I3K1LuuFaejUdbZfDZWTeRj9yr1Df9LuS9kfaSDqpPwbqigAL0PuKBU6h6\n" +
                        "rctMWoGI6jAdI+rkz9oszPtGEbgXAgMBAAECgYBMXqIcD6kszrMrZBa0t7dgNaTT\n" +
                        "5m4+g1wsvFVpDBozgjo+IelrBdNyoM3wSu1ihYoUf8idkz5cA63KST9ZBe4+AnpP\n" +
                        "0G9lIdQkk0jwRK0scgBCgRrVIXIPb3E/fabqxz+sPdoNE/2lm0mqvWOGs8xuUAcb\n" +
                        "yVVy+YToTA+f4n31gQJBAOsObh/u5xW7/pYZYDqHCTNQpHfz1b8aYthQoYZ1/Oi6\n" +
                        "JeiciJqYvd2AYKmVw9DJA/lB+bq1iv0uGMGbcxitZEECQQDNWVllgswsLLIjDZgK\n" +
                        "U/z6wyUEZ81zwbvto46XjkvXlwzO4/EHY2kX/BMLVNfqVBEXNYccM4YiSa28Uc+Z\n" +
                        "gyZXAkEA44RtLbWlsZDtJMZdMWmBi0z0VTMMYtScdnrdTfQon++GP/g79UABD0Ry\n" +
                        "y9Mt3YqksQCeQ43zgYq75bKcnnc+wQJBALCRw2ylqynQxNufNLysuzpj0dCX8PE4\n" +
                        "BH+xw0pN8KjQGRNMrFPOBu+4sCduLLowzwFF5Yc6Rnv1Ho5vSjKVssECQFAIYNYe\n" +
                        "7wB+T8KXqACllfLNsaVF4GFWZsLkhVoIDkd6UcWroSyQWbgOnxGN4FbkKXBCCGqd\n" +
                        "bPaovZy/7GY6cAg=";
                try {
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(prikey.replace("\n","").getBytes("UTF-8")));
                    PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);

                    byte[] b = Base64.getDecoder().decode(encrypted);
                    String s=new String(cipher.doFinal(b));
                      phone=s.substring(s.length()-11,s.length());
//                    Log.e("ssss", s.substring(s.length()-11,s.length()));
                                      final String aa=  DataEncryption.ENCRYPT(rid,null);
                                      Log.e("sssssssssssssssssssssss",aa);
                    String url="https://tx.naturallywater.com:8889/app/checklogin?"+"staff_identification_code="+aa+"&staff_telephone="+phone;
                    Log.e("sasss",url);
                    RequestQueue requestQueue= Volley.newRequestQueue(LoginActivity.this);
                    // 1 创建一个get请求
                    StringRequest stringRequest=new StringRequest(url, new com.android.volley.Response.Listener<String>() {
                        //正确接受数据之后的回调
                        @Override
                        public void onResponse(String response) {
                            try {
                                String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                                JSONObject object=new JSONObject(newResponse);
//                                String ss=object.getString("message");
                                Log.e("sssssss",object.toString());
                                if(object.getInt("code")==168){
                                    isfrist=true;
                                    SharedPreferences sp = LoginActivity.this.getSharedPreferences("SI", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();//获取编辑器
                                    editor.putString("PHONE", phone);
                                    editor.putString("document", "");
                                    editor.commit();
                                  JSONObject jsonObject=  object.getJSONObject("data");
                                  int  staff_id= jsonObject.getInt("staff_id");
                                  String staff_name=jsonObject.getString("staff_name");
                                    String staff_loginname=jsonObject.getString("staff_loginname");
                                    String staff_telephone=jsonObject.getString("staff_telephone");
                                    int staff_gender=jsonObject.getInt("staff_gender");
                                    String staff_job_number=jsonObject.getString("staff_job_number");
                                    String staff_position=jsonObject.getString("staff_position");
                                    int staff_spn_id=jsonObject.getInt("staff_spn_id");
                                    if(staff_spn_id==1){
                                       Intent intent=new Intent(LoginActivity.this,TaXiangActivity.class);
                                        intent.putExtra("staff_id",staff_id);
                                        intent.putExtra("staff_name",staff_name);
                                        intent.putExtra("staff_loginname",staff_loginname);
                                        intent.putExtra("staff_telephone",staff_telephone);
                                        intent.putExtra("staff_gender",staff_gender);
                                        intent.putExtra("staff_job_number",staff_job_number);
                                        intent.putExtra("staff_position",staff_position);
                                        intent.putExtra("staff_spn_id",staff_spn_id);
                                        intent.putExtra("identifyNumber",rid);
                                        startActivity(intent);
                                    }else if(staff_spn_id==2){
                                        Intent intent=new Intent(LoginActivity.this,kuGuanActivity.class);
                                        intent.putExtra("staff_id",staff_id);
                                        intent.putExtra("staff_name",staff_name);
                                        intent.putExtra("staff_loginname",staff_loginname);
                                        intent.putExtra("staff_telephone",staff_telephone);
                                        intent.putExtra("staff_gender",staff_gender);
                                        intent.putExtra("staff_job_number",staff_job_number);
                                        intent.putExtra("staff_position",staff_position);
                                        intent.putExtra("staff_spn_id",staff_spn_id);
                                        intent.putExtra("identifyNumber",rid);
                                        startActivity(intent);
                                    }

                                }else if(object.getInt("code")==409){
                                    isfrist=false;
                                    Intent intent=new Intent(getApplicationContext(),RegisActivity.class);
                                    intent.putExtra("phone",phone);
                                    intent.putExtra("identifyNumber",aa);
                                    intent.putExtra("status",isfrist);
                                    startActivity(intent);
                                }
//                                Toast.makeText(getApplicationContext(),ss,Toast.LENGTH_SHORT).show();

                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {//发生异常之后的监听回调
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("ssss",""+error);
                        }
                    });
                    // 3 将创建的请求添加到请求队列中
                    requestQueue.add(stringRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                phone.setText((String)msg.obj);
            }else {
//                tvSuccess.setText("登录失败!");
//                Intent intent = new Intent(LoginResultActivity.this, VerifyActivity.class);
//                intent.putExtra(Constants.KEY_ACTION,Constants.ACTION_LOGIN_FAILED);
//                intent.putExtra(Constants.KEY_ERORRO_MSG,"获取从后台手机号失败");
//                intent.putExtra(Constants.KEY_ERROR_CODE,Constants.CODE_LOGIN_FAILED);
//                startActivity(intent);
//                LoginResultActivity.this.finish();
            }

        }
    };
    private JVerifyUIConfig getDialogPortraitConfig(){
        int widthDp = px2dip(this, winWidth);
        JVerifyUIConfig.Builder uiConfigBuilder = new JVerifyUIConfig.Builder().setDialogTheme(widthDp-60, 300, 0, 0, false);
//        uiConfigBuilder.setLogoHeight(30);
//        uiConfigBuilder.setLogoWidth(30);
//        uiConfigBuilder.setLogoOffsetY(-15);
//        uiConfigBuilder.setLogoOffsetX((widthDp-40)/2-15-20);
//        uiConfigBuilder.setLogoImgPath("logo_login_land");
        uiConfigBuilder.setLogoHidden(true);

        uiConfigBuilder.setNumFieldOffsetY(104).setNumberColor(Color.BLACK);
        uiConfigBuilder.setSloganOffsetY(135);
        uiConfigBuilder.setSloganTextColor(0xFFD0D0D9);
        uiConfigBuilder.setLogBtnOffsetY(161);

        uiConfigBuilder.setPrivacyOffsetY(15);
        uiConfigBuilder.setCheckedImgPath("cb_chosen");
        uiConfigBuilder.setUncheckedImgPath("cb_unchosen");
        uiConfigBuilder.setNumberColor(0xFF222328);
        uiConfigBuilder.setLogBtnImgPath("selector_btn_normal");
        uiConfigBuilder.setPrivacyState(true);
        uiConfigBuilder.setLogBtnText("一键登录");
        uiConfigBuilder.setLogBtnHeight(44);
        uiConfigBuilder.setLogBtnWidth(250);
        uiConfigBuilder.setAppPrivacyColor(0xFFBBBCC5,0xFF8998FF);
        uiConfigBuilder.setPrivacyText("登录即同意《","","","》并授权塔乡山泉获取本机号码");
        uiConfigBuilder.setPrivacyCheckboxHidden(true);
        uiConfigBuilder.setPrivacyTextCenterGravity(true);
//      uiConfigBuilder.setPrivacyOffsetX(52-15);
        uiConfigBuilder.setPrivacyTextSize(10);



        // 图标和标题
        LinearLayout layoutTitle = new LinearLayout(this);
        RelativeLayout.LayoutParams layoutTitleParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutTitleParam.setMargins(0,dp2Pix(this,50), 0,0);
        layoutTitleParam.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        layoutTitleParam.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        layoutTitleParam.setLayoutDirection(LinearLayout.HORIZONTAL);
        layoutTitle.setLayoutParams(layoutTitleParam);

        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.logo_login_land);
        TextView tvTitle = new TextView(this);
        tvTitle.setText("塔乡山泉");
        tvTitle.setTextSize(19);
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgParam.setMargins(0,0,dp2Pix(this,6),0);
        LinearLayout.LayoutParams titleParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgParam.setMargins(0,0,dp2Pix(this,4),0);
//        layoutTitle.addView(img,imgParam);
        layoutTitle.addView(tvTitle,titleParam);
        uiConfigBuilder.addCustomView(layoutTitle,false,null);

        // 关闭按钮
        ImageView closeButton = new ImageView(this);

        RelativeLayout.LayoutParams mLayoutParams1 = new RelativeLayout.LayoutParams(130, 130);
        mLayoutParams1.setMargins(0, dp2Pix(this,10.0f),dp2Pix(this,10),0);
        mLayoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        mLayoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        closeButton.setLayoutParams(mLayoutParams1);
        closeButton.setImageResource(R.drawable.btn_close);
        uiConfigBuilder.addCustomView(closeButton, true, null);

        return uiConfigBuilder.build();
    }

    private JVerifyUIConfig getDialogLandscapeConfig(){
        int widthDp = px2dip(this, winWidth);
        JVerifyUIConfig.Builder uiConfigBuilder = new JVerifyUIConfig.Builder().setDialogTheme(480, widthDp-100, 0, 0, false);
//        uiConfigBuilder.setLogoHeight(40);
//        uiConfigBuilder.setLogoWidth(40);
//        uiConfigBuilder.setLogoOffsetY(-15);
//        uiConfigBuilder.setLogoImgPath("logo_login_land");
        uiConfigBuilder.setLogoHidden(true);

        uiConfigBuilder.setNumFieldOffsetY(104).setNumberColor(Color.BLACK);
        uiConfigBuilder.setNumberSize(22);
        uiConfigBuilder.setSloganOffsetY(135);
        uiConfigBuilder.setSloganTextColor(0xFFD0D0D9);
        uiConfigBuilder.setLogBtnOffsetY(161);

        uiConfigBuilder.setPrivacyOffsetY(15);
        uiConfigBuilder.setCheckedImgPath("cb_chosen");
        uiConfigBuilder.setUncheckedImgPath("cb_unchosen");
        uiConfigBuilder.setNumberColor(0xFF222328);
        uiConfigBuilder.setLogBtnImgPath("selector_btn_normal");
        uiConfigBuilder.setPrivacyState(true);
        uiConfigBuilder.setLogBtnText("一键登录");
        uiConfigBuilder.setLogBtnHeight(44);
        uiConfigBuilder.setLogBtnWidth(250);
        uiConfigBuilder.setAppPrivacyColor(0xFFBBBCC5,0xFF8998FF);
        uiConfigBuilder.setPrivacyText("登录即同意《","","","》并授权极光认证Demo获取本机号码");
        uiConfigBuilder.setPrivacyCheckboxHidden(true);
        uiConfigBuilder.setPrivacyTextCenterGravity(true);
//        uiConfigBuilder.setPrivacyOffsetX(52-15);
        uiConfigBuilder.setPrivacyTextSize(10);

        // 图标和标题
        LinearLayout layoutTitle = new LinearLayout(this);
        RelativeLayout.LayoutParams layoutTitleParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutTitleParam.setMargins(dp2Pix(this,20),dp2Pix(this,15), 0,0);
        layoutTitleParam.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        layoutTitleParam.setLayoutDirection(LinearLayout.HORIZONTAL);
        layoutTitle.setLayoutParams(layoutTitleParam);

        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.logo_login_land);
        TextView tvTitle = new TextView(this);
        tvTitle.setText("极光认证");
        tvTitle.setTextSize(19);
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgParam.setMargins(0,0,dp2Pix(this,6),0);
        LinearLayout.LayoutParams titleParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgParam.setMargins(0,0,dp2Pix(this,4),0);
//        layoutTitle.addView(img,imgParam);
        layoutTitle.addView(tvTitle,titleParam);
        uiConfigBuilder.addCustomView(layoutTitle,false,null);


        // 关闭按钮
        ImageView closeButton = new ImageView(this);

        RelativeLayout.LayoutParams mLayoutParams1 = new RelativeLayout.LayoutParams(130, 130);
        mLayoutParams1.setMargins(0, dp2Pix(this,10.0f),dp2Pix(this,10),0);
        mLayoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        mLayoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        closeButton.setLayoutParams(mLayoutParams1);
        closeButton.setImageResource(R.drawable.btn_close);
        uiConfigBuilder.addCustomView(closeButton, true, null);

        return uiConfigBuilder.build();
    }
    private int dp2Pix(Context context, float dp) {
        try {
            float density = context.getResources().getDisplayMetrics().density;
            return (int) (dp * density + 0.5F);
        } catch (Exception e) {
            return (int) dp;
        }
    }

    private int px2dip(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
