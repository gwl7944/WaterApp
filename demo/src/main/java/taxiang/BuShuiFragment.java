package taxiang;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class BuShuiFragment extends Fragment {
    private ListView shuizhanlist;
    private TextView shuizhanname,bushui;
    private String identifyNumber;
    private String[] items;
    private Integer[] id;
    private int wsn_id,staff_id,staff_spn_id;
    private List<ShopBean> list;
    public static BuShuiFragment instance = null;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bu_shui_fragment, container, false);
        instance = this;
        shuizhanname=view.findViewById(R.id.shuizhanname);
        shuizhanlist=view.findViewById(R.id.shuizhanlist);
        bushui=view.findViewById(R.id.bushui);
        identifyNumber=getActivity().getIntent().getStringExtra("identifyNumber");
        staff_id= getActivity().getIntent().getIntExtra("staff_id",0);
        staff_spn_id=getActivity().getIntent().getIntExtra("staff_spn_id",0);
        if(TaXiangActivity.flag){
            selectDeductImpurityList();
        }

//        findAllStaff_warehousing();
//        findStaff_warehousing_detailedBySwg_id();
//        findStaff_warehousingById();
//        CheckForWarehousing();
        shuizhanname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });
        bushui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sum=0;
                String detailed="";
                int kong=0;
                for(int i=0;i<list.size();i++){

                    if(list.get(i).getShop_num()>0||list.get(i).getHui_shop_num()>0){
                        sum=sum+list.get(i).getShop_num();
                        detailed=detailed+"s"+list.get(i).getWsdd_commodity_id()+","+list.get(i).getShop_num()+","+list.get(i).getHui_shop_num();
                        kong=kong+list.get(i).getHui_shop_num();
                    }

                }
                if(!detailed.equals("")){
                    detailed=detailed.substring(1,detailed.length());
                    ApplyForWarehousing(sum,detailed,kong);
                }else{
                    ToastUtil.show(getActivity(),"请输入补水数量");
                }


            }
        });
        return view;
    }
    private void showListDialog() {

        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(getActivity());
        listDialog.setTitle("请选择水站");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                // ...To-do

                shuizhanname.setText(items[which]);
                wsn_id=id[which];
                Water_stationDetailedInfo(wsn_id);
            }
        });
        listDialog.show();
    }
    public void  selectDeductImpurityList(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/Water_stationInfo?"+"staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("sssss获取全部水站信息",object.toString());
                  JSONObject obj=  object.getJSONObject("data");
                  JSONArray arr=obj.getJSONArray("datalist");
                     items = new String[arr.length()];
                    id=new Integer[arr.length()];
                  for (int i=0;i<arr.length();i++){
                      items[i]= arr.getJSONObject(i).getString("wsn_name");
                      id[i]=arr.getJSONObject(i).getInt("wsn_id");
                  }
                  if(items.length>0){
                      shuizhanname.setText(items[0]);
                      wsn_id=id[0];
                      Water_stationDetailedInfo(id[0]);
                  }

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
    protected void  Water_stationDetailedInfo(int id){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/Water_stationDetailedInfo/"+id+"?staff_identification_code="+DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("sssss查询单个水站信息",object.toString());
                    list=new ArrayList<>();
                    JSONArray arr=object.getJSONArray("data");
                    for (int i=0;i<arr.length();i++){
                       ShopBean shopBean=new ShopBean();
                       shopBean.setWsdd_barrels_empty(arr.getJSONObject(i).getInt("wsdd_barrels_empty"));
                        shopBean.setWsdd_commodity_id(arr.getJSONObject(i).getInt("wsdd_commodity_id"));
                        shopBean.setWsdd_number(arr.getJSONObject(i).getInt("wsdd_number"));
                        shopBean.setWsdd_id(arr.getJSONObject(i).getInt("wsdd_id"));
                        shopBean.setWsdd_water_station_id(arr.getJSONObject(i).getInt("wsdd_water_station_id"));
                        shopBean.setWsdd_commodity_name(arr.getJSONObject(i).getString("wsdd_commodity_name"));
                        shopBean.setWsdd_commodity_url(arr.getJSONObject(i).getString("wsdd_commodity_url"));
                        shopBean.setShop_num(0);
                        shopBean.setHui_shop_num(0);
                        list.add(shopBean);
                    }
                    MyAdapter myAdapter=new MyAdapter(list);
                    shuizhanlist.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
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
    protected void  ApplyForWarehousing(final int sum, final String detailed,final int kong){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/ApplyForWarehousing";

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("sssss骑手出入库申请/审批",object.toString());
                    if(object.getInt("code")==503){
                        ToastUtil.show(getActivity(),"提交成功");
                    }else{
                        ToastUtil.show(getActivity(),object.getString("resMsg"));
                    }
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
                params.put("swg_water_station", wsn_id+"");
                params.put("swg_type", staff_spn_id==1?"出库":"入库");
                params.put("swg_applicant", staff_id + "");
                params.put("swg_total_merchandise", sum + "");
                params.put("swg_empty_barrels", kong + "");
                params.put("DetailedData", detailed);
                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
    protected void  findAllStaff_warehousing(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findAllStaff_warehousing";
        Log.e("ddddd",DataEncryption.ENCRYPT(identifyNumber,null));
        Log.e("sssss",url.toString());
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("sssss查询全部出入库信息",object.toString());

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
                params.put("swg_water_station", wsn_id+"");
                params.put("swg_type", "出库");
                params.put("swg_state","");

                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
    public void  findStaff_warehousingById(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findStaff_warehousingById/1"+"?staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssss查看某个出入库信息",object.toString());

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

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findStaff_warehousing_detailedBySwg_id/22"+"?staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssss查询某个出入库下的商品详细信息",object.toString());

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
    protected void  CheckForWarehousing(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/CheckForWarehousing";
        Log.e("ddddd",DataEncryption.ENCRYPT(identifyNumber,null));
        Log.e("sssss",url.toString());
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("sssss审核骑手出入库",object.toString());

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
                params.put("swg_state", 2+"");
                params.put("swg_approval_opinions", "同意");
                params.put("swg_approved",34+"");
                params.put("swg_id",22+"");

                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
    private class MyAdapter extends BaseAdapter {

        private List<ShopBean> mList;

        public MyAdapter( List<ShopBean> list) {
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
                convertView = getLayoutInflater().inflate(R.layout.item_shop, null);

                //对viewHolder的属性进行赋值
                viewHolder.shop_img = convertView.findViewById(R.id.shop_img);
                viewHolder.shop_name = convertView.findViewById(R.id.shop_name);
//                viewHolder.shop_content = convertView.findViewById(R.id.shop_content);
                viewHolder.shop_kucun = convertView.findViewById(R.id.shop_kucun);
                viewHolder.jian = convertView.findViewById(R.id.jian);
                viewHolder.jia = convertView.findViewById(R.id.jia);
                viewHolder.num = convertView.findViewById(R.id.num);
                viewHolder.huijian = convertView.findViewById(R.id.huijian);
                viewHolder.huijia = convertView.findViewById(R.id.huijia);
                viewHolder.huinum = convertView.findViewById(R.id.huinum);
                //通过setTag将convertView与viewHolder关联
                convertView.setTag(viewHolder);
            } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.shop_name.setText(mList.get(position).getWsdd_commodity_name());
            viewHolder.shop_kucun.setText("库存："+mList.get(position).getWsdd_number());
            viewHolder.shop_img.setImageURL(mList.get(position).getWsdd_commodity_url());
            viewHolder.jian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(viewHolder.num.getText().toString())==0){

                    } else{
                        mList.get(position).setShop_num( mList.get(position).getShop_num()-1);
                        viewHolder.num.setText(mList.get(position).getShop_num()+"");
                    }
                }
            });
            if(staff_spn_id==1){
                viewHolder.jia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Integer.parseInt(viewHolder.num.getText().toString())==mList.get(position).getWsdd_number()){

                        } else{
                            mList.get(position).setShop_num( mList.get(position).getShop_num()+1);
                            viewHolder.num.setText(mList.get(position).getShop_num()+"");
                        }
                    }
                });
            }else {
                viewHolder.jia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Integer.parseInt(viewHolder.num.getText().toString())==9999){

                        } else{
                            mList.get(position).setShop_num( mList.get(position).getShop_num()+1);
                            viewHolder.num.setText(mList.get(position).getShop_num()+"");
                        }
                    }
                });
            }


            viewHolder.num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(viewHolder.num.getText().toString().equals("")){

                    } else{
                        mList.get(position).setShop_num(Integer.parseInt(viewHolder.num.getText().toString()));

                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
//                    viewHolder.num.setText(mList.get(position).getShop_num()+"");

                }
            });

            viewHolder.huijian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(viewHolder.huinum.getText().toString())==0){

                    } else{
                        mList.get(position).setHui_shop_num( mList.get(position).getHui_shop_num()-1);
                        viewHolder.huinum.setText(mList.get(position).getHui_shop_num()+"");
                    }
                }
            });
            viewHolder.huijia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(viewHolder.huinum.getText().toString())==9999){

                    } else{
                        mList.get(position).setHui_shop_num( mList.get(position).getHui_shop_num()+1);
                        viewHolder.huinum.setText(mList.get(position).getHui_shop_num()+"");
                    }
                }
            });
            viewHolder.huinum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(viewHolder.huinum.getText().toString().equals("")){

                    }else{
                        mList.get(position).setHui_shop_num(Integer.parseInt(viewHolder.huinum.getText().toString()));

                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
//                    viewHolder.num.setText(mList.get(position).getShop_num()+"");
                }
            });
            return convertView;
        }
        class ViewHolder {
            public TextView shop_name,shop_content,shop_kucun,jian,jia,huijian,huijia;
            public EditText num,huinum;
            public MyImageView shop_img;
            public RelativeLayout rl;
        }
    }

}
