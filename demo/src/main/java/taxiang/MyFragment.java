package taxiang;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFragment extends Fragment {
    private TextView setting,tongnum,kongtongnum;
    private String identifyNumber;
    private List<KuCunShopBean> list;
    private List<KuCunShopBean> list1;
    private TextView rijiesuan,zongnum,kongzongnum,yazongnum,result;
    private ListView shop_list;
    private LinearLayout rijie;
    private  String [] name;
    private List<Integer> id;
   private int kong;
    private AlertDialog alertDialog1;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_fragment, container, false);
        setting=view.findViewById(R.id.setting);
        tongnum=view.findViewById(R.id.tongnum);
        kongtongnum=view.findViewById(R.id.kongtongnum);
        shop_list=view.findViewById(R.id.shop_list);
        rijiesuan=view.findViewById(R.id.rijiesuan);
        zongnum=view.findViewById(R.id.zongnum);
        kongzongnum=view.findViewById(R.id.kongzongnum);
        yazongnum=view.findViewById(R.id.yazongnum);
        rijie=view.findViewById(R.id.rijie);
        result=view.findViewById(R.id.result);
        rijie.setVisibility(View.GONE);

        identifyNumber=getActivity().getIntent().getStringExtra("identifyNumber");
        getAllManager();
        CheckForWarehousing();
        rijiesuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetUpRiderwork();
            }
        });
        return view;
    }
    protected void  CheckForWarehousing(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findAllCommodity";
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
                    Log.e("sssss查看全部商品信息",object.toString());
                   JSONArray array= object.getJSONArray("data");
                    list=new ArrayList<>();
                   for (int i=0;i<array.length();i++){
                       KuCunShopBean kuCunShopBean=new KuCunShopBean();
                       kuCunShopBean.setCommodity_id(array.getJSONObject(i).getInt("commodity_id"));
                       kuCunShopBean.setCommodity_name(array.getJSONObject(i).getString("commodity_name"));
                       kuCunShopBean.setCommodity_picture_url(array.getJSONObject(i).getJSONArray("commPicList").getJSONObject(0).getString("comm_pic"));
                       kuCunShopBean.setSddd_charged_barrels(0);
                       kuCunShopBean.setSddd_empty_barrels(0);
                       kuCunShopBean.setSddd_margin(0);

                       list.add(kuCunShopBean);
                   }
                    findAllDynamicInformation();
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
                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
    protected void  findAllDynamicInformation(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findAllDynamicInformation?"+"staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssssss查询骑手的实时商品信息",object.toString());
                    JSONObject object1=object.getJSONObject("data");
                    KuCunBean kuCunBean=new KuCunBean();
                    kong=object1.getInt("sdc_total_margin");
                    kongtongnum.setText("剩余空桶数："+object1.getInt("sdc_empty_barrels"));
                    tongnum.setText("剩余桶数："+object1.getInt("sdc_total_margin"));
//                    kongtongnum.setText(object1.getInt("sdc_empty_barrels"));
                    JSONObject object2=object1.getJSONObject("detailed");
                    list1=new ArrayList<>();
                    for(int i=0;i<list.size();i++){
                        if(!object2.optString(list.get(i).getCommodity_id()+"").equals("")){
                            list.get(i).setSddd_empty_barrels(object2.getJSONObject(list.get(i).getCommodity_id()+"").getInt("sddd_empty_barrels"));
                            list.get(i).setSddd_margin(object2.getJSONObject(list.get(i).getCommodity_id()+"").getInt("sddd_margin"));
                            list1.add(list.get(i));
                        }
                    }
                    shop_list.setAdapter(new MyAdapter(list1));
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
    protected void  SetUpRiderwork(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/SetUpRiderwork?"+"staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssssss查询骑手工作时间段",object.toString());
                    JSONObject object1=object.getJSONObject("data");
                   String start_time=object1.getString("start_time");
                    String end_time=object1.getString("end_time");
                    SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("HH:mm:ss");
                    Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
                    String    str    =    formatter.format(curDate);
                    str=str.substring(0,5);
                    Log.e("sssssssss",str);
                    Integer.parseInt(str.replace(":",""));
                    if(Integer.parseInt(str.replace(":",""))<Integer.parseInt(start_time.replace(":",""))||Integer.parseInt(str.replace(":",""))>Integer.parseInt(end_time.replace(":",""))){
//                        findDaily_settlementData();
//                        getAllManager();
                        Log.e("sssssss",name.length+"");
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                        alertBuilder.setTitle("选择审核库管");
                        alertBuilder.setItems(name, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                findDaily_settlementData(id.get(i));
                                alertDialog1.dismiss();
                            }
                        });
                        alertDialog1 = alertBuilder.create();
                        alertDialog1.show();
                    }else{

//                        Log.e("sssssss",name.length+"");
//                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
//                        alertBuilder.setTitle("选择审核库管");
//                        alertBuilder.setItems(name, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                findDaily_settlementData(id.get(i));
//                                alertDialog1.dismiss();
//                            }
//                        });
//                        alertDialog1 = alertBuilder.create();
//                        alertDialog1.show();

                        ToastUtil.show(getActivity(),"请在"+end_time+"后提交日审核！");
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

    protected void  getAllManager(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/getAllManager?"+"staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssssss查询所有库管",object.toString());
                   JSONArray array=  object.getJSONArray("data");
                    name=new String[array.length()];
                    id=new ArrayList<>();
                    for (int i=0;i<array.length();i++){

                        name[i]=array.getJSONObject(i).getString("staff_name");
                        id.add(array.getJSONObject(i).getInt("staff_id"));
                    }


//                    final String[] items = {"男", "女"};

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
    protected void  findDaily_settlementData(int id){
        SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyy-MM-dd");
        Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findDaily_settlementData/"+getActivity().getIntent().getIntExtra("staff_id",0)+"/"+str+"/"+kong+"/"+id+"?staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);
      Log.e("ssssssss",url);
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssssss骑手日结单查看",object.toString());
                    JSONObject object1=object.getJSONObject("data");
                    KuCunBean kuCunBean=new KuCunBean();
                    zongnum.setText("当日总出库桶数:"+object1.getInt("dst_barrelsdelivered_number"));
                    kongzongnum.setText("当日总回空桶数:"+object1.getInt("dst_barrels_empty"));
                    yazongnum.setText("当日押金缴纳桶数:"+object1.getInt("dst_charged_barrels"));
                    result.setText("审核结果："+object1.getString("dst_check_results"));
                    rijie.setVisibility(View.VISIBLE);
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
    private class MyAdapter extends BaseAdapter {

        private List<KuCunShopBean> mList;

        public MyAdapter( List<KuCunShopBean> list) {
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
//            Picasso.with(getContext()).load(mList.get(position).getCommodity_picture_url()).into(viewHolder.shop_img);
            viewHolder.shop_img.setImageURL(mList.get(position).getCommodity_picture_url());
            viewHolder.shop_name.setText(mList.get(position).getCommodity_name());

                viewHolder.is_hui.setText("空桶："+mList.get(position).getSddd_empty_barrels());
            viewHolder.is_hui.setTextColor(Color.parseColor("#929699"));
            viewHolder.shop_num.setText("余桶："+mList.get(position).getSddd_margin());
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
