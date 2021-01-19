package taxiang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.taxiang.R;
import com.gwl.encryption.DataEncryption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaXiangActivity extends FragmentActivity  implements View.OnClickListener {
    //声明ViewPager
    private ViewPager mViewPager;
    //适配器
    private FragmentPagerAdapter mAdapter;
    //装载Fragment的集合
    private List<Fragment> mFragments;

    //四个Tab对应的布局
    private LinearLayout mTabWeixin;
    private LinearLayout mTabFrd;
    private LinearLayout mTabAddress;
    private LinearLayout mTabSetting;

    //四个Tab对应的ImageButton
    private ImageButton mImgWeixin;
    private ImageButton mImgFrd;
    private ImageButton mImgAddress;
    private ImageButton mImgSetting;
    private boolean isfrist;
    private TextView tv_dingdan,tv_bushui,tv_tongji,tv_my;
    private int staff_id,staff_gender,staff_spn_id;
    private String staff_name,staff_loginname,staff_telephone,staff_job_number,staff_position,identifyNumber;
    private LocationClient mLocationClient = null;
    private ImageButton status;
    public static boolean flag=false;
    private String cr_deposit_document_no;
    public static TaXiangActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //初始化地图SDK
        instance=this;
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_ta_xiang);
        staff_id= getIntent().getIntExtra("staff_id",0);
        staff_name=getIntent().getStringExtra("staff_name");
        staff_loginname= getIntent().getStringExtra("staff_loginname");
        staff_telephone=getIntent().getStringExtra("staff_telephone");
        staff_gender=getIntent().getIntExtra("staff_gender",0);
        staff_job_number=getIntent().getStringExtra("staff_job_number");
        staff_position=getIntent().getStringExtra("staff_position");
        staff_spn_id=getIntent().getIntExtra("staff_spn_id",0);
        identifyNumber=getIntent().getStringExtra("identifyNumber");

        initViews();//初始化控件
        initEvents();//初始化事件
        initDatas();//初始化数据
        if(ContextCompat.checkSelfPermission(TaXiangActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(TaXiangActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);

        }else{
            //开始定位
            if(!isLocServiceEnable(getApplicationContext())){
                toOpenGPS(TaXiangActivity.this);
            }else{
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                    }
                }, 10000);
            }
        }
        SharedPreferences pref = getSharedPreferences("SI",MODE_PRIVATE);
        String document = pref.getString("document","");

        Log.e("ssss","document"+document);

        if(!document.equals("")){
            staffGetDepositInfo(document);
        }
    }
    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }
    /**
     * 提示用户去开启定位服务
     **/
    public static void toOpenGPS(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage("手机定位服务未开启，无法获取到您的准确位置信息，是否前往开启？")
                .setNegativeButton("取消",null)
                .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(intent);
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
    private void initDatas() {
        mFragments = new ArrayList<>();
        //将四个Fragment加入集合中
        mFragments.add(new DingDanFragment());
        mFragments.add(new BuShuiFragment());
        mFragments.add(new TongJiFragment());
        mFragments.add(new MyFragment());

        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return mFragments.size();
            }

        };
        //不要忘记设置ViewPager的适配器
        mViewPager.setAdapter(mAdapter);
        //设置ViewPager的切换监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                //设置position对应的集合中的Fragment
                mViewPager.setCurrentItem(position);
                selectTab(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initEvents() {
        //设置四个Tab的点击事件
        mTabWeixin.setOnClickListener(this);
        mTabFrd.setOnClickListener(this);
        mTabAddress.setOnClickListener(this);
        mTabSetting.setOnClickListener(this);
        status.setOnClickListener(this);
    }

    //初始化控件
    private void initViews() {
        mViewPager =  findViewById(R.id.id_viewpager);

        mTabWeixin = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);
        mTabSetting = (LinearLayout) findViewById(R.id.id_tab_setting);

        mImgWeixin = (ImageButton) findViewById(R.id.id_tab_weixin_img);
        mImgFrd = (ImageButton) findViewById(R.id.id_tab_frd_img);
        mImgAddress = (ImageButton) findViewById(R.id.id_tab_address_img);
        mImgSetting = (ImageButton) findViewById(R.id.id_tab_setting_img);

        tv_dingdan=findViewById(R.id.tv_dingdan);
        tv_bushui=findViewById(R.id.tv_bushui);
        tv_tongji=findViewById(R.id.tv_tongji);
        tv_my=findViewById(R.id.tv_my);

        status=findViewById(R.id.status);
        ViewRiderOnlineStatus();
        //定位初始化
        mLocationClient = new LocationClient(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(60000);
        // 可选，设置地址信息
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        option.setIsNeedLocationDescribe(true);

        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
         MyLocationListener myLocationListener = new  MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();

    }

    @Override
    public void onClick(View v) {

        //根据点击的Tab切换不同的页面及设置对应的ImageButton为绿色
        switch (v.getId()) {
            case R.id.id_tab_weixin:
                selectTab(0);
                break;
            case R.id.id_tab_frd:
                selectTab(1);
                break;
            case R.id.id_tab_address:
                selectTab(2);
                break;
            case R.id.id_tab_setting:
                selectTab(3);
                break;
            case R.id.status:
                if(flag){
                    SetWorkingState(2);
                }else {
                    SetWorkingState(1);
                }
                break;
        }
    }

    private void selectTab(int i) {
        //根据点击的Tab设置对应的ImageButton为绿色
        switch (i) {
            case 0:
                mImgWeixin.setImageResource(R.mipmap.dingdan);
                mImgFrd.setImageResource(R.mipmap.bushuip);
                mImgAddress.setImageResource(R.mipmap.tongjip);
                mImgSetting.setImageResource(R.mipmap.myp);
                tv_dingdan.setTextColor(Color.parseColor("#198CFF"));
                tv_bushui.setTextColor(Color.parseColor("#616466"));
                tv_tongji.setTextColor(Color.parseColor("#616466"));
                tv_my.setTextColor(Color.parseColor("#616466"));
                break;
            case 1:
                mImgWeixin.setImageResource(R.mipmap.dingdanp);
                mImgFrd.setImageResource(R.mipmap.bushui);
                mImgAddress.setImageResource(R.mipmap.tongjip);
                mImgSetting.setImageResource(R.mipmap.myp);
                tv_dingdan.setTextColor(Color.parseColor("#616466"));
                tv_bushui.setTextColor(Color.parseColor("#198CFF"));
                tv_tongji.setTextColor(Color.parseColor("#616466"));
                tv_my.setTextColor(Color.parseColor("#616466"));
                break;
            case 2:
                mImgWeixin.setImageResource(R.mipmap.dingdanp);
                mImgFrd.setImageResource(R.mipmap.bushuip);
                mImgAddress.setImageResource(R.mipmap.tongji);
                mImgSetting.setImageResource(R.mipmap.myp);
                tv_dingdan.setTextColor(Color.parseColor("#616466"));
                tv_bushui.setTextColor(Color.parseColor("#616466"));
                tv_tongji.setTextColor(Color.parseColor("#198CFF"));
                tv_my.setTextColor(Color.parseColor("#616466"));
                break;
            case 3:
                mImgWeixin.setImageResource(R.mipmap.dingdanp);
                mImgFrd.setImageResource(R.mipmap.bushuip);
                mImgAddress.setImageResource(R.mipmap.tongjip);
                mImgSetting.setImageResource(R.mipmap.my);
                tv_dingdan.setTextColor(Color.parseColor("#616466"));
                tv_bushui.setTextColor(Color.parseColor("#616466"));
                tv_tongji.setTextColor(Color.parseColor("#616466"));
                tv_my.setTextColor(Color.parseColor("#198CFF"));
                break;
        }
        //设置当前点击的Tab所对应的页面
        mViewPager.setCurrentItem(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case
                    200:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                }else {
                    Toast.makeText(this,"未开启定位权限，请手动到设置去开去权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:break;
        }
    }
    // 继承抽象类BDAbstractListener并重写其onReceieveLocation方法来获取定位数据，并将其传给MapView
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            // ------------------  可选部分结束 ------------------

            // 显示当前信息
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n经度：" + location.getLatitude());
            stringBuilder.append("\n纬度："+ location.getLongitude());
            stringBuilder.append("\n状态码："+ location.getLocType());
            stringBuilder.append("\n国家：" + location.getCountry());
            stringBuilder.append("\n城市："+ location.getCity());
            stringBuilder.append("\n区：" + location.getDistrict());
            stringBuilder.append("\n街道：" + location.getStreet());
            stringBuilder.append("\n地址：" + location.getAddrStr());
//            Log.e("sssssss",stringBuilder.toString());
            if(flag){
                getLongitudeLatitude(location.getLatitude()+"",location.getLongitude()+"");
            }

        }
    }
    protected void  getLongitudeLatitude(String Latitude,String Longitude ){

        String url="https://tx.naturallywater.com:8889/app/ChangeAddress/getLongitudeLatitude?latitude="+Latitude+"&longitude="+Longitude+"&staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);
        Log.e("ssssss",url);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
//                    JSONObject object=new JSONObject(newResponse);


                } catch (UnsupportedEncodingException e) {
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
    protected void  ViewRiderOnlineStatus(){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/ViewRiderOnlineStatus?staff_identification_code="+ DataEncryption.ENCRYPT(identifyNumber,null);
        Log.e("ssssss",url);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssssss查看骑手在线状态",newResponse.toString());
                    String qistatus=object.getString("data");
                    if(qistatus.equals("离线")){
                        status.setBackgroundResource(R.drawable.shangxian);
                        flag=false;
                    }else if(qistatus.equals("在线")){
                        status.setBackgroundResource(R.drawable.xiaxian);
                        flag=true;
                        DingDanFragment.instance.selectDeductImpurityList();
                        BuShuiFragment.instance.selectDeductImpurityList();
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
    protected void  SetWorkingState(final int a){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/SetWorkingState/"+a+"?staff_id="+staff_id+"&staff_identification_code="+DataEncryption.ENCRYPT(identifyNumber,null);
        Log.e("ssssss",url);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssssss查看骑手在线状态",newResponse.toString());

                            if(object.getInt("code")==200){
                                if(a==2){
                                    status.setBackgroundResource(R.drawable.shangxian);
                                    flag=false;
                                    ToastUtil.show(getApplicationContext(),"停止接单");
                                }else if(a==1){
                                    status.setBackgroundResource(R.drawable.xiaxian);
                                    flag=true;
                                    ToastUtil.show(getApplicationContext(),"开始接单");
                                    DingDanFragment.instance.selectDeductImpurityList();
                                    BuShuiFragment.instance.selectDeductImpurityList();
                                }
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
    public  void staffGetDepositInfo(String document){

        String url="https://tx.naturallywater.com:8889/applets/customer/staffGetDepositInfo?cr_deposit_document_no="+document;
        Log.e("ssssss",url);
        RequestQueue requestQueue= Volley.newRequestQueue(TaXiangActivity.this);

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssssss查看退押金单",object.toString());

                    cr_deposit_document_no=    object.getString("cr_deposit_document_no");
                    showListDialog("总押金桶数："+ object.getInt("cr_number"),"总押金额："+object.getInt("cr_totalprice_deposit"));
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
    private void showListDialog(String a,String b) {
        final String[] items = {a,b};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(TaXiangActivity.this).setCancelable(false);
        listDialog.setTitle("退押金明细");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        listDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        staffAddDepositInfo(cr_deposit_document_no);

                        dialog.dismiss();
                    }
                });
        listDialog.show();
    }
    public  void staffAddDepositInfo(String document){

        String url="https://tx.naturallywater.com:8889/applets/customer/staffAddDepositInfo?cr_deposit_document_no="+document+"&staff_code="+DataEncryption.ENCRYPT(identifyNumber,null);
        Log.e("ssssss",url);
        RequestQueue requestQueue= Volley.newRequestQueue(TaXiangActivity.this);

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("ssssss确认退押金单",object.toString());
                    SharedPreferences sp = TaXiangActivity.this.getSharedPreferences("SI", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();//获取编辑器
                    editor.putString("document", "");
                    editor.commit();
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

    @Override
    protected void onResume() {
        super.onResume();
        ViewRiderOnlineStatus();
    }
}
