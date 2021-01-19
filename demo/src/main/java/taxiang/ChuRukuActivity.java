package taxiang;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class ChuRukuActivity extends Fragment {

    private ListView churukulist;
    private String identifyNumber;
    private int wsn_id,staff_id;
    private LinearLayout ll_yuyue,ll_shishi;
    private TextView tv_yuyue,tv_shishi;
    private List<ChuRuKuBean> list,list1;
    public static ChuRukuActivity instance = null;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chu_ruku, container, false);
        identifyNumber=getActivity().getIntent().getStringExtra("identifyNumber");
        staff_id= getActivity().getIntent().getIntExtra("staff_id",0);
        churukulist=view.findViewById(R.id.churukulist);
        ll_yuyue=view.findViewById(R.id.ll_yuyue);
        ll_shishi=view.findViewById(R.id.ll_shishi);
        tv_yuyue=view.findViewById(R.id.tv_yuyue);
        tv_shishi=view.findViewById(R.id.tv_shishi);
        instance=this;
        findAllStaff_warehousing("出库");
        ll_yuyue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_yuyue.setTextColor(Color.parseColor("#18191A"));
                tv_shishi.setTextColor(Color.parseColor("#919699"));
                findAllStaff_warehousing("出库");
            }
        });
        ll_shishi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_yuyue.setTextColor(Color.parseColor("#919699"));
                tv_shishi.setTextColor(Color.parseColor("#18191A"));
                findAllStaff_warehousing("入库");
            }
        });

        return view;
    }
    protected void  findAllStaff_warehousing(final String type){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/findAllStaff_warehousing";
        Log.e("ddddd", DataEncryption.ENCRYPT(identifyNumber,null));
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
                    list=new ArrayList<>();
                    list1=new ArrayList<>();
                  JSONArray array= object.getJSONArray("data");

                  for (int i=0;i<array.length();i++){
                      ChuRuKuBean chuRuKuBean=new ChuRuKuBean();
                      chuRuKuBean.setSwg_water_station_name(array.getJSONObject(i).getString("swg_water_station_name"));
                      chuRuKuBean.setSwg_application_time_str(array.getJSONObject(i).getString("swg_application_time_str"));
                      chuRuKuBean.setSwg_applicant_name(array.getJSONObject(i).getString("swg_applicant_name"));
                      chuRuKuBean.setSwg_id(array.getJSONObject(i).getInt("swg_id"));
                      chuRuKuBean.setSwg_state(array.getJSONObject(i).getInt("swg_state"));
                      chuRuKuBean.setSwg_type(array.getJSONObject(i).getString("swg_type"));
                      chuRuKuBean.setStaff_identification_code(array.getJSONObject(i).getString("staff_identification_code"));
                      if(type.equals("出库")){
                          list.add(chuRuKuBean);

                      }else if(type.equals("入库")){
                          list1.add(chuRuKuBean);
                      }
//                      if(array.getJSONObject(i).getInt("swg_state")==1){
//                          if(type.equals("出库")){
//                              list.add(chuRuKuBean);
//
//                          }else if(type.equals("入库")){
//                              list1.add(chuRuKuBean);
//                          }
//
//                      }
                  }
                    if(type.equals("出库")){
                        MyAdapter myAdapter=new MyAdapter(list);
                        churukulist.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
                        churukulist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent =new Intent(getActivity(),ChuRuInfoActivity.class);
                                intent.putExtra("identifyNumber",identifyNumber);
                                intent.putExtra("ChuRuKuBean",list.get(position));
                                intent.putExtra("staff_id",staff_id);
                                startActivity(intent);
                            }
                        });
                    }else if(type.equals("入库")){
                        MyAdapter myAdapter1=new MyAdapter(list1);
                        churukulist.setAdapter(myAdapter1);
                        myAdapter1.notifyDataSetChanged();
                        churukulist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent =new Intent(getActivity(),ChuRuInfoActivity.class);
                                intent.putExtra("identifyNumber",identifyNumber);
                                intent.putExtra("ChuRuKuBean",list1.get(position));
                                intent.putExtra("staff_id",staff_id);
                                startActivity(intent);
                            }
                        });
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
                params.put("swg_type", type);
                params.put("swg_state","");

                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
    private class MyAdapter extends BaseAdapter {

        private List<ChuRuKuBean> mList;

        public MyAdapter( List<ChuRuKuBean> list) {
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
                convertView = getLayoutInflater().inflate(R.layout.item_dingdan, null);

                //对viewHolder的属性进行赋值
                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
                viewHolder.tv_phone = convertView.findViewById(R.id.tv_phone);
                viewHolder.tv_data = convertView.findViewById(R.id.tv_data);
                viewHolder.tv_adress = convertView.findViewById(R.id.tv_adress);

                //通过setTag将convertView与viewHolder关联
                convertView.setTag(viewHolder);
            } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_name.setText(mList.get(position).getSwg_applicant_name());
            viewHolder.tv_phone.setText(mList.get(position).getSwg_application_time_str());
            viewHolder.tv_data.setText(mList.get(position).getSwg_water_station_name());
//            viewHolder.tv_adress.setVisibility(View.GONE);
            if(mList.get(position).getSwg_state()==1){
                viewHolder.tv_adress.setText("未审核");
            }else if(mList.get(position).getSwg_state()==2){
                viewHolder.tv_adress.setText("已审核");
            }

            return convertView;
        }
        class ViewHolder {
            public TextView tv_name,tv_phone,tv_data,tv_adress;
            public RelativeLayout rl;
        }
    }
}
