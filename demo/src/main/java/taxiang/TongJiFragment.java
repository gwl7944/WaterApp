package taxiang;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class TongJiFragment extends Fragment {
    private LinearLayout ll_yuyue;
    private String identifyNumber;
    private int staff_id;
    private List<JieSuan> list;
    private ListView lv_dingdan;
    public static TongJiFragment instance = null;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tong_ji_fragment, container, false);
        instance=this;
        ll_yuyue=view.findViewById(R.id.ll_yuyue);
        lv_dingdan=view.findViewById(R.id.lv_dingdan);
        identifyNumber=getActivity().getIntent().getStringExtra("identifyNumber");
        staff_id= getActivity().getIntent().getIntExtra("staff_id",0);
        getAllManager();
        ll_yuyue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllManager();
            }
        });
        return view;
    }
    protected void  getAllManager(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/getAllSettlementStatement?"+"staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null)+"&staff_id="+staff_id;
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
                    Log.e("ssssss查询日结算",object.toString());
                    JSONArray array=  object.getJSONArray("data");
                  list=new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        JieSuan jieSuan =new JieSuan();
                        jieSuan.setDst_applicant(array.getJSONObject(i).getInt("dst_applicant"));
                        jieSuan.setDst_applicant_name(array.getJSONObject(i).getString("dst_applicant_name"));
                        jieSuan.setDst_application_time_str(array.getJSONObject(i).getString("dst_application_time_str"));
                        jieSuan.setDst_approved(array.getJSONObject(i).getInt("dst_approved"));
                        jieSuan.setDst_barrels_empty(array.getJSONObject(i).getInt("dst_barrels_empty"));
                        jieSuan.setDst_barrelsdelivered_number(array.getJSONObject(i).getInt("dst_barrelsdelivered_number"));
                        jieSuan.setDst_check_results(array.getJSONObject(i).getString("dst_check_results"));
                        jieSuan.setDst_document_status(array.getJSONObject(i).getInt("dst_document_status"));
                        jieSuan.setDst_id(array.getJSONObject(i).getInt("dst_id"));
                        list.add(jieSuan);
                    }
                     MyAdapter myAdapter=new  MyAdapter(list);
                    lv_dingdan.setAdapter(myAdapter);
                    lv_dingdan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent =new Intent(getActivity(),ShenHeInfoActivity.class);
                            intent.putExtra("identifyNumber",identifyNumber);
                            intent.putExtra("jiesuan",list.get(position));
                            intent.putExtra("staff_id",staff_id);
                            startActivity(intent);
                        }
                    });
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
    private class MyAdapter extends BaseAdapter {

        private List<JieSuan> mList;

        public MyAdapter( List<JieSuan> list) {
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
                convertView = getLayoutInflater().inflate(R.layout.shenhe_item, null);

                //对viewHolder的属性进行赋值
                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
                viewHolder.tv_data = convertView.findViewById(R.id.tv_data);
                viewHolder.tv_number = convertView.findViewById(R.id.tv_number);
                viewHolder.tv_empty = convertView.findViewById(R.id.tv_empty);
                viewHolder.tv_barrels = convertView.findViewById(R.id.tv_barrels);
                viewHolder.tv_result = convertView.findViewById(R.id.tv_result);
                //通过setTag将convertView与viewHolder关联
                convertView.setTag(viewHolder);
            } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_name.setText("申请人:"+mList.get(position).getDst_applicant_name());
            viewHolder.tv_data.setText("申请时间:"+mList.get(position).getDst_application_time_str());
            viewHolder.tv_number.setText("当日总出库桶数:"+mList.get(position).getDst_barrelsdelivered_number());
            viewHolder.tv_empty.setText("空桶数:"+mList.get(position).getDst_barrels_empty());
            viewHolder.tv_barrels.setText("押金缴纳桶数:"+mList.get(position).getDst_charged_barrels());
            if(mList.get(position).getDst_document_status()==1){
                viewHolder.tv_result.setText("待审核");
            }else if(mList.get(position).getDst_document_status()==2){
                viewHolder.tv_result.setText("已审核");
            }

            return convertView;
        }
        class ViewHolder {
            public TextView tv_name,tv_data,tv_number,tv_empty,tv_barrels,tv_result;

        }
    }
}
