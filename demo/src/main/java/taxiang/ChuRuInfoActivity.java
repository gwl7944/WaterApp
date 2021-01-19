package taxiang;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChuRuInfoActivity extends AppCompatActivity {
    private LinearLayout ll_back;
    private TextView data,name,phone,adress,yajin,peidata,remake,tongyi,butongyi,statue;
    private ListView shop_list;
    private int  swg_id,staff_id;
    private String   identifyNumber;
    private ChuRuKuBean chuRuKuBean;
    private List<ChuRuKuShopBean> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chu_ru_info);
        chuRuKuBean= (ChuRuKuBean) getIntent().getSerializableExtra("ChuRuKuBean");
        identifyNumber=getIntent().getStringExtra("identifyNumber");
        staff_id=getIntent().getIntExtra("staff_id",0);
        initViews();//初始化控件
        initListener();
//        findStaff_warehousingById();
        findStaff_warehousing_detailedBySwg_id();
    }
    private void initViews() {
        ll_back=findViewById(R.id.ll_back);
        shop_list=findViewById(R.id.shop_list);
        data=findViewById(R.id.data);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        adress=findViewById(R.id.adress);
        statue=findViewById(R.id.statue);
//        yajin=findViewById(R.id.yajin);
//        peidata=findViewById(R.id.peidata);
//        remake=findViewById(R.id.remake);
        tongyi=findViewById(R.id.tongyi);
        butongyi=findViewById(R.id.butongyi);
                    data.setText(chuRuKuBean.getSwg_application_time_str());
                    name.setText(chuRuKuBean.getSwg_applicant_name());
                    phone.setText(chuRuKuBean.getSwg_water_station_name());
                    adress.setText(chuRuKuBean.getSwg_type());
        if(chuRuKuBean.getSwg_state()==1){
            statue.setText("待审核");
        }else if(chuRuKuBean.getSwg_state()==2){
            statue.setText("同意");
        }else if(chuRuKuBean.getSwg_state()==3){
            statue.setText("拒绝");
        }
        if(chuRuKuBean.getSwg_type().equals("入库")){
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
                CheckForWarehousing("同意","2");
            }
        });
        butongyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckForWarehousing("不同意","3");
            }
        });
    }
    public void  findStaff_warehousingById(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findStaff_warehousingById/"+chuRuKuBean.getSwg_id()+"?staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssss查看某个出入库信息",object.toString());
//                    data.setText(dingDanBean.getOrder_place_order_time_str());
//                    name.setText(dingDanBean.getOrder_customer_name());
//                    phone.setText(dingDanBean.getOrder_customer_telephone());
//                    adress.setText(dingDanBean.getOrder_address_name());
////        hui_num.setText(dingDanBean.getStr);
////        yajin.setText(dingDanBean.get);
//                    peidata.setText(dingDanBean.getOrder_estimated_time_str());
//                    remake.setText(dingDanBean.getOrder_remarks());
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

    }
    public void  findStaff_warehousing_detailedBySwg_id(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findStaff_warehousing_detailedBySwg_id/"+chuRuKuBean.getSwg_id()+"?staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssss查询某个出入库下的商品详细信息",object.toString());
                    list=new ArrayList<>();
                   JSONArray array= object.getJSONArray("data");
                   for (int i=0;i<array.length();i++){
                       ChuRuKuShopBean chuRuKuShopBean=new ChuRuKuShopBean();
                       chuRuKuShopBean.setSwdd_barrels_empty(array.getJSONObject(i).getInt("swdd_barrels_empty"));
                       chuRuKuShopBean.setSwdd_commodity_id(array.getJSONObject(i).getInt("swdd_commodity_id"));
                       chuRuKuShopBean.setSwdd_commodity_name(array.getJSONObject(i).getString("swdd_commodity_name"));
                       chuRuKuShopBean.setSwdd_id(array.getJSONObject(i).getInt("swdd_id"));
                       chuRuKuShopBean.setSwdd_number(array.getJSONObject(i).getInt("swdd_number"));
                       chuRuKuShopBean.setSwdd_staff_warehousing_id(array.getJSONObject(i).getInt("swdd_staff_warehousing_id"));
                       chuRuKuShopBean.setSwdd_commodity_url(array.getJSONObject(i).getString("swdd_commodity_url"));
                       list.add(chuRuKuShopBean);
                   }
                    shop_list.setAdapter(new MyAdapter(list));
                    setListViewHeightBasedOnChildren(shop_list);
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

    }
    protected void  CheckForWarehousing(final String status, final String flag){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/CheckForWarehousing";
        Log.e("ddddd",DataEncryption.ENCRYPT(identifyNumber,null));
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
                    Log.e("sssss审核骑手出入库",object.toString());
                    ToastUtil.show(getApplicationContext(),"审核成功");
                    ChuRukuActivity.instance.findAllStaff_warehousing("出库");
                    ChuRukuActivity.instance.findAllStaff_warehousing("入库");
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
                params.put("swg_state", flag);
                params.put("swg_approval_opinions", status);
                params.put("swg_approved",staff_id+"");
                params.put("swg_id",chuRuKuBean.getSwg_id()+"");
                params.put("staff_identification_code",chuRuKuBean.getStaff_identification_code()+"");
                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
    private class MyAdapter extends BaseAdapter {

        private List<ChuRuKuShopBean> mList;

        public MyAdapter( List<ChuRuKuShopBean> list) {
            mList = list;
        }

        @Override
        public int getCount() {

            return mList.size();

        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"SetTextI18n", "InflateParams"})
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final  ViewHolder viewHolder;
            //如果view未被实例化过，缓存池中没有对应的缓存
            if (convertView == null) {
                viewHolder = new  ViewHolder();
                // 由于我们只需要将XML转化为View，并不涉及到具体的布局，所以第二个参数通常设置为null
                convertView = getLayoutInflater().inflate(R.layout.item_dingdan_shop, null);

                //对viewHolder的属性进行赋值
                viewHolder.shop_img = convertView.findViewById(R.id.shop_img);
                viewHolder.shop_name = convertView.findViewById(R.id.shop_name);
                viewHolder.is_hui = convertView.findViewById(R.id.is_hui);
                viewHolder.shop_num = convertView.findViewById(R.id.shop_num);

                //通过setTag将convertView与viewHolder关联
                convertView.setTag(viewHolder);
            } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.shop_img.setImageURL(mList.get(position).getSwdd_commodity_url());
//            viewHolder.shop_img.setText(mList.get(position).getOrder_customer_name());
            viewHolder.shop_name.setText(mList.get(position).getSwdd_commodity_name());

            viewHolder.is_hui.setText("空桶数:"+mList.get(position).getSwdd_barrels_empty());

            viewHolder.shop_num.setText("数量"+mList.get(position).getSwdd_number()+"");
            return convertView;
        }
        class ViewHolder {
            public TextView shop_name,is_hui,shop_num;
            public MyImageView shop_img;
        }
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams)params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }
}
