package taxiang;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
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
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.taxiang.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class RegisActivity extends AppCompatActivity {
    private TextView gender,phone, spnid, comtin,spn;
    private EditText name;
    private boolean mHasPermission;
    private int REQUEST_READ_PHONE_STATE=0;
    private int winWidth;
    private int winHeight;
    private static RegisActivity SApp;
    private int staff_gender;
    private int staff_spn_id;
    private String phonenum,identifyNumber;
    private String rid;
    private  LinearLayout ll_back;
    public static RegisActivity getApp() {
        return SApp;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);
        SApp=this;


        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
//        JVerificationInterface.setDebugMode(true);
//        JVerificationInterface.init(this);
        phonenum= getIntent().getStringExtra("phone");
        identifyNumber= getIntent().getStringExtra("identifyNumber");
        initView();

        initListener();
    }

    private void initView() {
        phone = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        spn=findViewById(R.id.spn);
        comtin = findViewById(R.id.comtin);
        phone.setText(phonenum);
        ll_back=findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        JVerificationInterface.setCustomUIWithConfig(getFullScreenPortraitConfig(),getFullScreenLandscapeConfig());

    }
    private void showSpnDialog() {
        final String[] items = { "骑手","库管"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(RegisActivity.this);
        listDialog.setTitle("请选择身份");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                // ...To-do

                spn.setText(items[which]);
                staff_spn_id=which+1;
            }
        });
        listDialog.show();
    }
    private void showListDialog() {
        final String[] items = { "男","女"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(RegisActivity.this);
        listDialog.setTitle("请选择性别");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                // ...To-do

                gender.setText(items[which]);
                staff_gender=which+1;
            }
        });
        listDialog.show();
    }
    private void initListener() {
//        JVerificationInterface.setCustomUIWithConfig(getFullScreenPortraitConfig(),getFullScreenLandscapeConfig());
        rid = JPushInterface.getRegistrationID(getApplicationContext());
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });
        spn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpnDialog();
            }
        });
        comtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("")||name.getText().toString()==null){
                    ToastUtil.show(getApplicationContext(),"请输入姓名");

                }else if(gender.getText().equals("请选择性别")){
                    ToastUtil.show(getApplicationContext(),"请选择性别");
                }else if(spn.getText().equals("请选择身份")){
                    ToastUtil.show(getApplicationContext(),"请选择身份");
                }else{
                    String url="https://tx.naturallywater.com:8889/app/LoginController/StaffRegister";

                    RequestQueue requestQueue= Volley.newRequestQueue(RegisActivity.this);

                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // 处理返回的JSON数据
                                    try {
                                        String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                                        Log.d("Response", newResponse);
                                        JSONObject object=new JSONObject(newResponse);
                                        if(object.getInt("code")==503){
                                            ToastUtil.show(getApplicationContext(),"注册成功");

                                            JSONObject jsonObject=  object.getJSONObject("data");
                                            int  staff_id= jsonObject.getInt("staff_id");
                                            String staff_name=jsonObject.getString("staff_name");
                                            String staff_loginname=jsonObject.getString("staff_loginname");
                                            String staff_telephone=jsonObject.getString("staff_telephone");
                                            int staff_gender=jsonObject.getInt("staff_gender");
                                            String staff_job_number=jsonObject.getString("staff_job_number");
                                            String staff_position=jsonObject.getString("staff_position");
                                            int staff_spn_id=jsonObject.getInt("staff_spn_id");
                                            SharedPreferences sp = RegisActivity.this.getSharedPreferences("SI", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sp.edit();//获取编辑器
                                            editor.putString("PHONE", jsonObject.getString("staff_telephone"));
                                            editor.putInt("ID", staff_spn_id);
                                            editor.commit();
                                            if(staff_spn_id==1){
                                                Intent intent=new Intent(RegisActivity.this,TaXiangActivity.class);
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
                                                startActivity(intent);
                                            }else if(staff_spn_id==2){

                                            }


                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Response", error.getMessage(), error);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            // POST 参数
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("staff_name", name.getText().toString().trim());
                            params.put("staff_telephone", phonenum);
                            params.put("staff_identification_code", identifyNumber);
                            params.put("staff_gender", staff_gender + "");
                            params.put("staff_spn_id", staff_spn_id + "");
                            return params;
                        }
                    } ;
                    requestQueue.add(postRequest);

                }
            }
        });

    }

}
