package taxiang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.taxiang.R;
import com.gwl.encryption.DataEncryption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShenHeInfoActivity extends AppCompatActivity {
    private LinearLayout ll_back;
    private TextView data,name,phone,adress,yajin,peidata,remake,tongyi,butongyi,statue;
    private ListView shop_list;
    private int  swg_id,staff_id;
    private String   identifyNumber;
    private JieSuan jieSuan;
    private List<JieSuan> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shen_he_info);
        jieSuan= (JieSuan) getIntent().getSerializableExtra("jiesuan");
        identifyNumber=getIntent().getStringExtra("identifyNumber");
        staff_id=getIntent().getIntExtra("staff_id",0);
        initViews();//初始化控件
        initListener();
    }
    private void initViews() {
        ll_back=findViewById(R.id.ll_back);
        shop_list=findViewById(R.id.shop_list);
        data=findViewById(R.id.data);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        adress=findViewById(R.id.adress);
        statue=findViewById(R.id.statue);
        peidata=findViewById(R.id.peidata);
//        yajin=findViewById(R.id.yajin);
//        peidata=findViewById(R.id.peidata);
//        remake=findViewById(R.id.remake);
        tongyi=findViewById(R.id.tongyi);
        butongyi=findViewById(R.id.butongyi);
        data.setText(jieSuan.getDst_application_time_str());
        name.setText(jieSuan.getDst_applicant_name());
        phone.setText(jieSuan.getDst_barrelsdelivered_number()+"");
        adress.setText(jieSuan.getDst_barrels_empty()+"");
        statue.setText(jieSuan.getDst_charged_barrels()+"");
        peidata.setText(jieSuan.getDst_check_results());

        if(jieSuan.getDst_document_status()==2){
            tongyi.setVisibility(View.GONE);
            butongyi.setVisibility(View.GONE);
        }else{
            tongyi.setVisibility(View.VISIBLE);
            butongyi.setVisibility(View.VISIBLE);
        }

//        hui_num.setText(dingDanBean.getStr);
//        yajin.setText(dingDanBean.get);
//                    peidata.setText(dingDanBean.getOrder_estimated_time_str());
//                    remake.setText(dingDanBean.getOrder_remarks());
    }
    private void initListener() {


        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tongyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckForWarehousing("同意" );
            }
        });
        butongyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckForWarehousing("驳回" );
            }
        });
    }
    protected void  CheckForWarehousing(final String status ){

            String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/ApproveSettlementStatement";
        Log.e("ddddd", DataEncryption.ENCRYPT(identifyNumber,null));
        Log.e("sssss",url.toString());
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("sssss审核骑手日结算",object.toString());
                    ToastUtil.show(getApplicationContext(),"审核成功");
                    TongJiFragment.instance.getAllManager();

                    finish();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {//发生异常之后的监听回调
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ssssssssss",""+error);

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // POST 参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("staff_identification_code", DataEncryption.ENCRYPT(identifyNumber,null));
                params.put("dst_approved", staff_id+"");
                params.put("dst_approval_opinions", status);
                params.put("dst_id",jieSuan.getDst_id()+"");

                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
}
