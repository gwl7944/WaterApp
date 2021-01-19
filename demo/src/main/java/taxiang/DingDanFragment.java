package taxiang;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jpushdemo.ExampleUtil;
import com.example.jpushdemo.LocalBroadcastManager;
import com.example.taxiang.R;
import com.gwl.encryption.DataEncryption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DingDanFragment extends Fragment implements View.OnClickListener {
//    private LinearLayout ll_yuyue,ll_shishi;
    private TextView tv_yuyue,tv_shishi,tv_daijiedan,tv_peisongzhong,tv_yiwancheng;
    private View v_daijiedan,v_peisongzhong,v_yiwancheng;
    private ListView lv_dingdan;
    private int staff_id,staff_gender,staff_spn_id;
    private String staff_name,staff_loginname,staff_telephone,staff_job_number,staff_position,identifyNumber;
    private List<DingDanBean> list,list1,list2,list3;
    public static DingDanFragment instance = null;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ding_dan_fragment, container, false);
        instance = this;
//        ll_yuyue=view.findViewById(R.id.ll_yuyue);
//        ll_shishi=view.findViewById(R.id.ll_shishi);
        tv_yuyue=view.findViewById(R.id.tv_yuyue);
        tv_shishi=view.findViewById(R.id.tv_shishi);
        tv_daijiedan=view.findViewById(R.id.tv_daijiedan);
        tv_peisongzhong=view.findViewById(R.id.tv_peisongzhong);
        tv_yiwancheng=view.findViewById(R.id.tv_yiwancheng);
        v_daijiedan=view.findViewById(R.id.v_daijiedan);
        v_peisongzhong=view.findViewById(R.id.v_peisongzhong);
        v_yiwancheng=view.findViewById(R.id.v_yiwancheng);
        lv_dingdan=view.findViewById(R.id.lv_dingdan);
//        ll_yuyue.setOnClickListener(this);
//        ll_shishi.setOnClickListener(this);
        tv_daijiedan.setOnClickListener(this);
        tv_peisongzhong.setOnClickListener(this);
        tv_yiwancheng.setOnClickListener(this);
        staff_id= getActivity().getIntent().getIntExtra("staff_id",0);
        staff_name=getActivity().getIntent().getStringExtra("staff_name");
        staff_loginname= getActivity().getIntent().getStringExtra("staff_loginname");
        staff_telephone=getActivity().getIntent().getStringExtra("staff_telephone");
        staff_gender=getActivity().getIntent().getIntExtra("staff_gender",0);
        staff_job_number=getActivity().getIntent().getStringExtra("staff_job_number");
        staff_position=getActivity().getIntent().getStringExtra("staff_position");
        staff_spn_id=getActivity().getIntent().getIntExtra("staff_spn_id",0);
        identifyNumber=getActivity().getIntent().getStringExtra("identifyNumber");
        if(TaXiangActivity.flag){
            selectDeductImpurityList();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.ll_yuyue:
//                tv_yuyue.setTextColor(Color.parseColor("#18191A"));
//                tv_shishi.setTextColor(Color.parseColor("#919699"));
//                tv_daijiedan.performClick();
//                selectDeductImpurityList();
//                break;
//            case R.id.ll_shishi:
//                tv_yuyue.setTextColor(Color.parseColor("#919699"));
//                tv_shishi.setTextColor(Color.parseColor("#18191A"));
//                tv_daijiedan.performClick();
//                break;
            case R.id.tv_daijiedan:
                tv_daijiedan.setTextColor(Color.parseColor("#198CFF"));
                tv_peisongzhong.setTextColor(Color.parseColor("#919699"));
                tv_yiwancheng.setTextColor(Color.parseColor("#919699"));
                v_daijiedan.setVisibility(View.VISIBLE);
                v_peisongzhong.setVisibility(View.INVISIBLE);
                v_yiwancheng.setVisibility(View.INVISIBLE);
                selectDeductImpurityList();
//                MyAdapter myAdapter=new MyAdapter(list);
//                lv_dingdan.setAdapter(myAdapter);
//                myAdapter.notifyDataSetChanged();
//                lv_dingdan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent intent=new Intent(getActivity(),DingDanInfoActivity.class);
//                        intent.putExtra("DingDanBean",list.get(position));
//                        intent.putExtra("identifyNumber",identifyNumber);
//                        startActivity(intent);
//                    }
//                });

                break;
            case R.id.tv_peisongzhong:
                tv_daijiedan.setTextColor(Color.parseColor("#919699"));
                tv_peisongzhong.setTextColor(Color.parseColor("#198CFF"));
                tv_yiwancheng.setTextColor(Color.parseColor("#919699"));
                v_daijiedan.setVisibility(View.INVISIBLE);
                v_peisongzhong.setVisibility(View.VISIBLE);
                v_yiwancheng.setVisibility(View.INVISIBLE);
                selectStaffOrderList(2);
//                MyAdapter myAdapter1=new MyAdapter(list2);
//                lv_dingdan.setAdapter(myAdapter1);
//                myAdapter1.notifyDataSetChanged();
//                lv_dingdan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent intent=new Intent(getActivity(),DingDanInfoActivity.class);
//                        intent.putExtra("DingDanBean",list2.get(position));
//                        intent.putExtra("identifyNumber",identifyNumber);
//                        startActivity(intent);
//                    }
//                });
                break;
            case R.id.tv_yiwancheng:
                tv_daijiedan.setTextColor(Color.parseColor("#919699"));
                tv_peisongzhong.setTextColor(Color.parseColor("#919699"));
                tv_yiwancheng.setTextColor(Color.parseColor("#198CFF"));
                v_daijiedan.setVisibility(View.INVISIBLE);
                v_peisongzhong.setVisibility(View.INVISIBLE);
                v_yiwancheng.setVisibility(View.VISIBLE);
                selectStaffOrderList(3);
//                MyAdapter myAdapter2=new MyAdapter(list3);
//                lv_dingdan.setAdapter(myAdapter2);
//                lv_dingdan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            Intent intent=new Intent(getActivity(),DingDanInfoActivity.class);
//                            intent.putExtra("DingDanBean",list3.get(position));
//                        intent.putExtra("identifyNumber",identifyNumber);
//                            startActivity(intent);
//                    }
//                });
//                myAdapter2.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==1){
            selectDeductImpurityList(); //刷新操作
        }    else if (requestCode==2){
            selectStaffOrderList(2); //刷新操作
        }  else if (requestCode==3){
            selectStaffOrderList(3); //刷新操作
        }
    }

    public void  selectDeductImpurityList(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findAllAppointmentOrder?"+"staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

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
                    JSONArray array= object.getJSONArray("data");
                   list =new ArrayList<>();
                    list1 =new ArrayList<>();
                    list2 =new ArrayList<>();
                    list3 =new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        DingDanBean dingDanBean=new DingDanBean();
                        dingDanBean.setOrder_actual_time(array.getJSONObject(i).getString("order_actual_time"));
                        dingDanBean.setOrder_actual_time_str(array.getJSONObject(i).getString("order_actual_time_str"));
                        dingDanBean.setOrder_address_id(array.getJSONObject(i).getInt("order_address_id"));
                        dingDanBean.setOrder_commodity_id(array.getJSONObject(i).getInt("order_commodity_id"));
                        dingDanBean.setOrder_del(array.getJSONObject(i).getInt("order_del"));
                        dingDanBean.setOrder_estimated_time(array.getJSONObject(i).getString("order_estimated_time"));
                        dingDanBean.setOrder_actual_time_str(array.getJSONObject(i).getString("order_actual_time_str"));
                        dingDanBean.setOrder_customer_id(array.getJSONObject(i).getInt("order_customer_id"));
                        dingDanBean.setOrder_estimated_time_str(array.getJSONObject(i).getString("order_estimated_time_str"));
                        dingDanBean.setOrder_id(array.getJSONObject(i).getInt("order_id"));
                        dingDanBean.setOrder_isback_barrel(array.getJSONObject(i).getInt("order_isback_barrel"));
                        dingDanBean.setOrder_method_payment(array.getJSONObject(i).getString("order_method_payment"));
                        dingDanBean.setOrder_money(array.getJSONObject(i).getDouble("order_money"));
                        dingDanBean.setOrder_remarks(array.getJSONObject(i).getString("order_remarks"));
                        dingDanBean.setOrder_state(array.getJSONObject(i).getInt("order_state"));
//                        dingDanBean.setOrder_staff_id(array.getJSONObject(i).getInt("order_staff_id"));
                        dingDanBean.setOrder_place_order_time_str(array.getJSONObject(i).getString("order_place_order_time_str"));
                        dingDanBean.setOrder_order_number(array.getJSONObject(i).getString("order_order_number"));
                        dingDanBean.setOrder_payment_channel(array.getJSONObject(i).getString("order_payment_channel"));
                        dingDanBean.setOrder_address_name(array.getJSONObject(i).getString("order_address_name"));
                        dingDanBean.setOrder_commodity_name(array.getJSONObject(i).getString("order_commodity_name"));
                        dingDanBean.setOrder_customer_name(array.getJSONObject(i).getString("order_customer_name"));
                        dingDanBean.setOrder_staff_name(array.getJSONObject(i).getString("order_staff_name"));
                        dingDanBean.setOrder_customer_telephone(array.getJSONObject(i).getString("order_customer_telephone"));
                        dingDanBean.setOrder_cas_latitude(array.getJSONObject(i).getString("order_cas_latitude"));
                        dingDanBean.setOrder_cas_longitude(array.getJSONObject(i).getString("order_cas_longitude"));
                       JSONArray arr= array.getJSONObject(i).getJSONArray("commodityList");
                       List<DingDanShopBean> listshop=new ArrayList<>();
                        for (int j=0;j<arr.length();j++){
                            DingDanShopBean dingDanShopBean=new DingDanShopBean();
                            dingDanShopBean.setOddr_id(arr.getJSONObject(j).getInt("oddr_id"));
                            dingDanShopBean.setOddr_commodity_id(arr.getJSONObject(j).getInt("oddr_commodity_id"));
                            dingDanShopBean.setOddr_commodity_name(arr.getJSONObject(j).getString("oddr_commodity_name"));
                            dingDanShopBean.setOddr_commodity_picture_url(arr.getJSONObject(j).getString("oddr_commodity_picture_url"));
                            dingDanShopBean.setOddr_isback_barrel(arr.getJSONObject(j).getInt("oddr_isback_barrel"));
                            dingDanShopBean.setOddr_number(arr.getJSONObject(j).getInt("oddr_number"));
                            dingDanShopBean.setOddr_order_id(arr.getJSONObject(j).getInt("oddr_order_id"));
                            listshop.add(dingDanShopBean);
                        }
                        dingDanBean.setCommodityList(listshop);
                        list.add(dingDanBean);
//                        if(array.getJSONObject(i).getInt("order_state")==1){
//                            list1.add(dingDanBean);
//                        }else if(array.getJSONObject(i).getInt("order_state")==2){
//                            list2.add(dingDanBean);
//                        }else if(array.getJSONObject(i).getInt("order_state")==3){
//                            list3.add(dingDanBean);
//                        }

                    }

                    lv_dingdan.setAdapter(new MyAdapter(list));
                    lv_dingdan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent=new Intent(getActivity(),DingDanInfoActivity.class);
                            intent.putExtra("DingDanBean",list.get(position));
                            intent.putExtra("identifyNumber",identifyNumber);
                            startActivityForResult(intent,1);
                        }
                    });
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
    public void  selectStaffOrderList(final int state){

        String url="https://tx.naturallywater.com:8889/applets/customer/selectStaffOrderList?"+"staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null)+"&order_type=0&order_state="+state;

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
//                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(response);
//                    String ss=object.getString("message");
                    Log.e("sssss",object.toString());
                    JSONArray array= object.getJSONArray("data");
                    list =new ArrayList<>();
                    list1 =new ArrayList<>();
                    list2 =new ArrayList<>();
                    list3 =new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        DingDanBean dingDanBean=new DingDanBean();
//                        dingDanBean.setOrder_actual_time(array.getJSONObject(i).getString("order_actual_time"));
//                        dingDanBean.setOrder_actual_time_str(array.getJSONObject(i).getString("order_actual_time_str"));
//                        dingDanBean.setOrder_address_id(array.getJSONObject(i).getInt("order_address_id"));
                        dingDanBean.setOrder_commodity_id(array.getJSONObject(i).getInt("order_commodity_id"));
//                        dingDanBean.setOrder_del(array.getJSONObject(i).getInt("order_del"));
//                        dingDanBean.setOrder_estimated_time(array.getJSONObject(i).getString("order_estimated_time"));
//                        dingDanBean.setOrder_actual_time_str(array.getJSONObject(i).getString("order_actual_time_str"));
                        dingDanBean.setOrder_customer_id(array.getJSONObject(i).getInt("order_customer_id"));
                        dingDanBean.setOrder_estimated_time_str(array.getJSONObject(i).getString("order_estimated_time_str"));
                        dingDanBean.setOrder_id(array.getJSONObject(i).getInt("order_id"));
                        dingDanBean.setOrder_isback_barrel(array.getJSONObject(i).getInt("order_isback_barrel"));
                        dingDanBean.setOrder_method_payment(array.getJSONObject(i).getString("order_method_payment"));
                        dingDanBean.setOrder_money(array.getJSONObject(i).getDouble("order_money"));
                        dingDanBean.setOrder_remarks(array.getJSONObject(i).getString("order_remarks"));
                        dingDanBean.setOrder_state(array.getJSONObject(i).getInt("order_state"));
                        dingDanBean.setOrder_staff_id(array.getJSONObject(i).getInt("order_staff_id"));
                        dingDanBean.setOrder_place_order_time_str(array.getJSONObject(i).getString("order_place_order_time_str"));
                        dingDanBean.setOrder_order_number(array.getJSONObject(i).getString("order_order_number"));
                        dingDanBean.setOrder_payment_channel(array.getJSONObject(i).getString("order_payment_channel"));
                        dingDanBean.setOrder_address_name(array.getJSONObject(i).getString("order_address_name"));
                        dingDanBean.setOrder_commodity_name(array.getJSONObject(i).getString("order_commodity_name"));
                        dingDanBean.setOrder_customer_name(array.getJSONObject(i).getString("order_cas_consignee_name"));
//                        dingDanBean.setOrder_staff_name(array.getJSONObject(i).getString("order_staff_name"));
                        dingDanBean.setOrder_customer_telephone(array.getJSONObject(i).getString("order_cas_receiver_telephone"));
                        dingDanBean.setOrder_cas_latitude(array.getJSONObject(i).getString("order_cas_latitude"));
                        dingDanBean.setOrder_cas_longitude(array.getJSONObject(i).getString("order_cas_longitude"));
                        dingDanBean.setOrder_type(array.getJSONObject(i).getInt("order_type"));
                        JSONArray arr= array.getJSONObject(i).getJSONArray("commodityList");
                        List<DingDanShopBean> listshop=new ArrayList<>();
                        for (int j=0;j<arr.length();j++){
                            DingDanShopBean dingDanShopBean=new DingDanShopBean();
//                            dingDanShopBean.setOddr_id(arr.getJSONObject(j).getInt("oddr_id"));
                            dingDanShopBean.setOddr_commodity_id(arr.getJSONObject(j).getInt("oddr_commodity_id"));
                            dingDanShopBean.setOddr_commodity_name(arr.getJSONObject(j).getString("oddr_commodity_name"));
                            dingDanShopBean.setOddr_commodity_picture_url("");
                            dingDanShopBean.setOddr_commodity_picture_url(arr.getJSONObject(j).getString("oddr_commodity_picture_url"));
                            dingDanShopBean.setOddr_isback_barrel(arr.getJSONObject(j).getInt("oddr_isback_barrel"));
                            dingDanShopBean.setOddr_number(arr.getJSONObject(j).getInt("oddr_number"));
//                            dingDanShopBean.setOddr_order_id(arr.getJSONObject(j).getInt("oddr_order_id"));
                            listshop.add(dingDanShopBean);
                        }
                        dingDanBean.setCommodityList(listshop);

                        if(state==2){
                            list2.add(dingDanBean);
                        }else if(state==3){
                            list3.add(dingDanBean);
                        }

                    }

                    if(state==2){

                        MyAdapter myAdapter =new MyAdapter(list2);
                        lv_dingdan.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
                        lv_dingdan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent=new Intent(getActivity(),DingDanInfoActivity.class);
                                intent.putExtra("DingDanBean",list2.get(position));
                                intent.putExtra("identifyNumber",identifyNumber);
                                startActivityForResult(intent,2);
                            }
                        });
                    }else if(state==3){

                        MyAdapter myAdapter= new MyAdapter(list3);
                        lv_dingdan.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
                        lv_dingdan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent=new Intent(getActivity(),DingDanInfoActivity.class);
                                intent.putExtra("DingDanBean",list3.get(position));
                                intent.putExtra("identifyNumber",identifyNumber);
                                startActivityForResult(intent,3);
                            }
                        });
                    }

                } catch (JSONException e) {
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
    private class MyAdapter extends BaseAdapter {

        private List<DingDanBean> mList;

        public MyAdapter( List<DingDanBean> list) {
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
            final ViewHolder viewHolder;
            //如果view未被实例化过，缓存池中没有对应的缓存
            if (convertView == null) {
                viewHolder = new ViewHolder();
                // 由于我们只需要将XML转化为View，并不涉及到具体的布局，所以第二个参数通常设置为null
                convertView = getLayoutInflater().inflate(R.layout.item_dingdan, null);

                //对viewHolder的属性进行赋值
                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
                viewHolder.tv_phone = convertView.findViewById(R.id.tv_phone);
                viewHolder.tv_data = convertView.findViewById(R.id.tv_data);
                viewHolder.tv_adress = convertView.findViewById(R.id.tv_adress);
                viewHolder.lv = convertView.findViewById(R.id.lv);
                //通过setTag将convertView与viewHolder关联
                convertView.setTag(viewHolder);
            } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if(mList.get(position).getOrder_type()==1){
                viewHolder.lv.setBackgroundResource(R.drawable.round6);
            }else{
                viewHolder.lv.setBackgroundResource(R.drawable.round7);
            }
            viewHolder.tv_name.setText(mList.get(position).getOrder_customer_name());
            viewHolder.tv_phone.setText(mList.get(position).getOrder_customer_telephone());
            viewHolder.tv_data.setText(mList.get(position).getOrder_place_order_time_str());
            viewHolder.tv_adress.setText(mList.get(position).getOrder_address_name());
            return convertView;
        }
        class ViewHolder {
            public TextView tv_name,tv_phone,tv_data,tv_adress;
            public RelativeLayout rl;
            private LinearLayout lv;
        }
    }

}
