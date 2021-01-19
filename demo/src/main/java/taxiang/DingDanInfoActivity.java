package taxiang;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.example.taxiang.R;
import com.gwl.encryption.DataEncryption;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class DingDanInfoActivity extends AppCompatActivity {
    private LinearLayout ll_back;
    private TextView data,name,phone,adress,yajin,peidata,remake,jiedan,tv_yajin;
    private DingDanBean dingDanBean;
    private ListView shop_list;
    private String   identifyNumber;
    public static DingDanInfoActivity instance = null;
    private MapView mapView;//声明地图组件
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;
    // 当前定位模式
    private MyLocationConfiguration.LocationMode locationMode;
    // 是否是第一次定位
    private boolean isFirstLocate = true;
    private Marker marker = null ;
    private InfoWindow mInfoWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //初始化地图SDK

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_ding_dan_info);
        if(ContextCompat.checkSelfPermission(DingDanInfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(DingDanInfoActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);

        }else{
            //开始定位
            if(!isLocServiceEnable(getApplicationContext())){
                toOpenGPS(DingDanInfoActivity.this);
            }
        }


        instance = this;
        dingDanBean= (DingDanBean) getIntent().getSerializableExtra("DingDanBean");
        identifyNumber=getIntent().getStringExtra("identifyNumber");
        initViews();//初始化控件
        initListener();
//        EventBus.getDefault().register(this);
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
    private void initViews() {
        ll_back=findViewById(R.id.ll_back);
        shop_list=findViewById(R.id.shop_list);
        data=findViewById(R.id.data);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        adress=findViewById(R.id.adress);
        yajin=findViewById(R.id.yajin);
        peidata=findViewById(R.id.peidata);
        remake=findViewById(R.id.remake);
        jiedan=findViewById(R.id.jiedan);
        tv_yajin=findViewById(R.id.tv_yajin);
        mapView = (MapView) findViewById(R.id.bmapview);
    }
    private void initListener() {
        if(isLocServiceEnable(getApplicationContext())){
            mBaiduMap = mapView.getMap();
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            //定位初始化
            mLocationClient = new LocationClient(this);
            //通过LocationClientOption设置LocationClient相关参数
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            // 可选，设置地址信息
            option.setIsNeedAddress(true);
            //可选，设置是否需要地址描述
            option.setIsNeedLocationDescribe(true);

            //设置locationClientOption
            mLocationClient.setLocOption(option);

            //注册LocationListener监听器
            MyLocationListener myLocationListener = new MyLocationListener();
            mLocationClient.registerLocationListener(myLocationListener);
            //开启地图定位图层
            mLocationClient.start();
        }

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent();
                setResult(1, intent);
            }
        });
        data.setText(dingDanBean.getOrder_place_order_time_str());
        name.setText(dingDanBean.getOrder_customer_name());
        phone.setText(dingDanBean.getOrder_customer_telephone());
        adress.setText(dingDanBean.getOrder_address_name());
//        hui_num.setText(dingDanBean.getStr);
//        yajin.setText(dingDanBean.get);
        peidata.setText(dingDanBean.getOrder_estimated_time_str());
        remake.setText(dingDanBean.getOrder_remarks());
        staffSelectOrderDepositInfo();
        if(dingDanBean.getOrder_state()==1){
            jiedan.setText("接单");
            tv_yajin.setVisibility(View.GONE);
        }else if(dingDanBean.getOrder_state()==2){
                jiedan.setText("配送完成");
                if(dingDanBean.getOrder_isback_barrel()==1){
                    tv_yajin.setVisibility(View.GONE);
                }
        }else if(dingDanBean.getOrder_state()==3){
            jiedan.setVisibility(View.GONE);
            tv_yajin.setVisibility(View.GONE);
        }

        shop_list.setAdapter(new MyAdapter(dingDanBean.getCommodityList()));
        setListViewHeightBasedOnChildren(shop_list);

            jiedan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dingDanBean.getOrder_state()==1){
                        jiedan(2);

                    }else if(dingDanBean.getOrder_state()==2){
                        findAllStaff_warehousing(3);
                    }

                }
            });

        tv_yajin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staffUpdateDepositInfo();
            }
        });
        //关键！！！！小程序返回坐标转换百度坐标
        //关键！！！！小程序返回坐标转换百度坐标
        //关键！！！！小程序返回坐标转换百度坐标
        //关键！！！！小程序返回坐标转换百度坐标
        LatLng ll = new LatLng(Double.parseDouble(dingDanBean.getOrder_cas_latitude()), Double.parseDouble(dingDanBean.getOrder_cas_longitude()));
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(ll);
        LatLng desLatLng = converter.convert();
        double la = desLatLng.latitude;
        double ln = desLatLng.longitude;
//        LatLng ll = new LatLng(la, ln);
        initOverlay(la,ln);
//        initOverlay(dingDanBean.getOrder_cas_latitude(),dingDanBean.getOrder_cas_longitude());
        marker.setClickable(true);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Bundle bundle = marker.getExtraInfo();
//                    MarkerInfoUtil infoUtil = (MarkerInfoUtil) bundle.getSerializable("info");

                LatLng latLng = marker.getPosition();
                //实例化一个地理编码查询对象
                GeoCoder geoCoder = GeoCoder.newInstance();

                //设置反地理编码位置坐标
                ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                option.location(latLng);

                //为地理编码查询对象设置一个请求结果监听器
                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        //获取反向地理编码结果
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult.getLocation()));

                        // 获取周边poi结果
                        List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();
                        final Button button = new Button(getApplicationContext());
                        button.setBackgroundColor(Color.WHITE);
                        button.setText(poiList.get(0).getName());
                        button.setTextColor(Color.BLACK);
                        button.setWidth(300);
                        InfoWindow.OnInfoWindowClickListener listener = null;
                        // InfoWindow点击事件监听接口
                        listener = new InfoWindow.OnInfoWindowClickListener() {
                            public void onInfoWindowClick() {
                                // 隐藏地图上的所有InfoWindow
                                mBaiduMap.hideInfoWindow();
                            }
                        };
                        LatLng latLng = marker.getPosition();
                        // 创建InfoWindow
                        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), latLng, -47, listener);
                        // 显示 InfoWindow, 该接口会先隐藏其他已添加的InfoWindow, 再添加新的InfoWindow
                        mBaiduMap.showInfoWindow(mInfoWindow);
                    }
                });
                //发起反地理编码请求
                geoCoder.reverseGeoCode(option);

                return true;
            }
        });
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
            if (location == null || mapView == null){
                return;
            }

            // 如果是第一次定位
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            if (isFirstLocate) {
                isFirstLocate = false;
                //给地图设置状态
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            // ------------------  以下是可选部分 ------------------
            // 自定义地图样式，可选
            // 更换定位图标，这里的图片是放在 drawble 文件下的
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.hdpiqishou);
            // 定位模式 地图SDK支持三种定位模式：NORMAL（普通态）, FOLLOWING（跟随态）, COMPASS（罗盘态）
            locationMode = MyLocationConfiguration.LocationMode.NORMAL;
            // 定位模式、是否开启方向、设置自定义定位图标、精度圈填充颜色以及精度圈边框颜色5个属性（此处只设置了前三个）。
            MyLocationConfiguration mLocationConfiguration = new MyLocationConfiguration(locationMode,true,mCurrentMarker);
            // 使自定义的配置生效
            mBaiduMap.setMyLocationConfiguration(mLocationConfiguration);
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

        }
    }

    //初始化添加覆盖物mark
    private void initOverlay(Double latitude,Double longitude) {
        Log.e("sssssssssss","latitude:"+latitude+"-longitude:"+longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        //设置覆盖物添加的方式与效果
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)//mark出现的位置
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hdpidingwei))       //mark图标
                .draggable(true)//mark可拖拽
                .animateType(MarkerOptions.MarkerAnimateType.drop)//从天而降的方式
                //.animateType(MarkerOptions.MarkerAnimateType.grow)//从地生长的方式
                ;
        //添加mark
        marker = (Marker) (mBaiduMap.addOverlay(markerOptions));//地图上添加mark

        //弹出View(气泡，意即在地图中显示一个信息窗口)，显示当前mark位置信息
        setPopupTipsInfo(marker);


    }
    //想根据Mark中的经纬度信息，获取当前的位置语义化结果，需要使用地理编码查询和地理反编码请求
    //在地图中显示一个信息窗口
    private void setPopupTipsInfo(Marker marker){
        //获取当前经纬度信息
        final LatLng latLng = marker.getPosition();
        final String[] addr = new String[1];
        //实例化一个地理编码查询对象
        GeoCoder geoCoder = GeoCoder.newInstance();
        //设置反地理编码位置坐标
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(latLng);
        //发起反地理编码请求
        geoCoder.reverseGeoCode(option);
        //为地理编码查询对象设置一个请求结果监听器
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                Log.d("TAG", "地理编码信息 ---> \nAddress : " + geoCodeResult.getAddress()
                        + "\ntoString : " + geoCodeResult.toString()
                        + "\ndescribeContents : " + geoCodeResult.describeContents());
            }

            //当获取到反编码信息结果的时候会调用
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                //获取地理反编码位置信息
                addr[0] = reverseGeoCodeResult.getAddress();
                //获取地址的详细内容对象，此类表示地址解析结果的层次化地址信息。
                ReverseGeoCodeResult.AddressComponent addressDetail = reverseGeoCodeResult.getAddressDetail();
//                Log.d("TAG", "反地理编码信息 ---> \nAddress : " + addr[0]
//                        + "\nBusinessCircle : " + reverseGeoCodeResult.getBusinessCircle()//位置所属商圈名称
//                        + "\ncity : " + addressDetail.city  //所在城市名称
//                        + "\ndistrict : " + addressDetail.district  //区县名称
//                        + "\nprovince : " + addressDetail.province  //省份名称
//                        + "\nstreet : " + addressDetail.street      //街道名
//                        + "\nstreetNumber : " + addressDetail.streetNumber);//街道（门牌）号码

                StringBuilder poiInfoBuilder = new StringBuilder();
                //poiInfo信息
                List<PoiInfo> poiInfoList = reverseGeoCodeResult.getPoiList();
                if(poiInfoList != null) {
                    poiInfoBuilder.append("\nPoilist size : " + poiInfoList.size());
                    for (PoiInfo p : poiInfoList) {
                        poiInfoBuilder.append("\n\taddress: " + p.address);//地址信息
                        poiInfoBuilder.append(" name: " + p.name + " postCode: " + p.postCode);//名称、邮编
                        //还有其他的一些信息，我这里就不打印了，请参考API
                    }
                }
//                Log.d("TAG","poiInfo --> " + poiInfoBuilder.toString());

                //动态创建一个View用于显示位置信息
                Button button = new Button(getApplicationContext());
                //设置view是背景图片
                button.setBackgroundResource(R.drawable.hdpidingwei);
                //设置view的内容（位置信息）
                button.setText(addr[0]);
                //在地图中显示一个信息窗口，可以设置一个View作为该窗口的内容，也可以设置一个 BitmapDescriptor 作为该窗口的内容
                InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), latLng, -47, new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        //当InfoWindow被点击后隐藏
                        mBaiduMap.hideInfoWindow();
                    }
                });
                //InfoWindow infoWindow = new InfoWindow(button, latLng, -47);
                //显示信息窗口
                mBaiduMap.showInfoWindow(infoWindow);
            }
        });
        /*不能放在下面 因为可能得到的数据还没来得及回调
        Button button = new Button(getApplicationContext());
        button.setBackgroundResource(R.drawable.location_tips);
        button.setText(addr[0]);
        //InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button),latLng,-47,null);
        InfoWindow infoWindow = new InfoWindow(button,latLng,-47);
        baiduMap.showInfoWindow(infoWindow);
        */
        return ;
    }
    protected void  findAllStaff_warehousing(final int order_state){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/updateAppointmentOrder";
        RequestQueue requestQueue= Volley.newRequestQueue(this);

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("sssss骑手接单",object.toString());
                    if(object.getInt("code")==507){
                        ToastUtil.show(getApplicationContext(),object.getString("resMsg"));
                        finish();
                        Intent intent = new Intent();
                        setResult(1, intent);
                    }else{
                        ToastUtil.show(getApplicationContext(),object.getString("resMsg"));
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {//发生异常之后的监听回调
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // POST 参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("staff_identification_code", DataEncryption.ENCRYPT(identifyNumber,null));
                params.put("order_id", dingDanBean.getOrder_id()+"");
                params.put("order_order_number", dingDanBean.getOrder_order_number());
                params.put("order_state",order_state+"");
                params.put("order_staff_id",dingDanBean.getOrder_staff_id()+"");
                params.put("order_commodity_id",dingDanBean.getOrder_commodity_id()+"");
                params.put("order_number",dingDanBean.getCommodityList().get(0).getOddr_number()+"");
                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
    protected void  jiedan(final int order_state){

        String url="https://tx.naturallywater.com:8889/app/StaffBusinessController/updateAppointmentOrder";
        RequestQueue requestQueue= Volley.newRequestQueue(this);

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(newResponse);
                    Log.e("sssss骑手接单",object.toString());
                    if(object.getInt("code")==507){
                        ToastUtil.show(getApplicationContext(),object.getString("resMsg"));
                        finish();
                        Intent intent = new Intent();
                        setResult(1, intent);
                    }else{
                        ToastUtil.show(getApplicationContext(),object.getString("resMsg"));
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {//发生异常之后的监听回调
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // POST 参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("staff_identification_code", DataEncryption.ENCRYPT(identifyNumber,null));
                params.put("order_id", dingDanBean.getOrder_id()+"");
                params.put("order_order_number", dingDanBean.getOrder_order_number());
                params.put("order_state",order_state+"");
                params.put("order_staff_id",dingDanBean.getOrder_staff_id()+"");

                return params;
            }
        } ;
        //将创建的请求添加到请求队列当中
        requestQueue.add(stringRequest);

    }
    public void  staffSelectOrderDepositInfo(){

        String url="https://tx.naturallywater.com:8889/applets/customer/staffSelectOrderDepositInfo?"+"cr_deposit_document_no="+ dingDanBean.getOrder_order_number() ;
        Log.e("ssssssssss",url);
        RequestQueue requestQueue= Volley.newRequestQueue(DingDanInfoActivity.this);

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                Log.e("sssssss",response.toString());
                try {
//                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(response);
//                    String ss=object.getString("message");
//                    Log.e("sssssss",object.toString());
                    if(object.getInt("code")==501){
                        JSONObject obj=  object.getJSONObject("data");
                        int totalprice  =obj.getInt("sdt_totalprice_deposit");
                        int sdt_state  =obj.getInt("sdt_state");
                        yajin.setText(totalprice+"元");
                        if(sdt_state==1){
                            tv_yajin.setVisibility(View.VISIBLE);
                            tv_yajin.setFocusable(true);
                            tv_yajin.setFocusableInTouchMode(true);
                            tv_yajin.requestFocus();
                        }else if(sdt_state==2){
                            tv_yajin.setVisibility(View.GONE);
                        }

                    }else if(object.getInt("code")==502){
                        tv_yajin.setVisibility(View.GONE);
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

    public void  staffUpdateDepositInfo(){

        String url="https://tx.naturallywater.com:8889/applets/customer/staffUpdateDepositInfo?staff_code="+DataEncryption.ENCRYPT(identifyNumber,null)+"&cr_deposit_document_no="+ dingDanBean.getOrder_order_number() ;

        RequestQueue requestQueue= Volley.newRequestQueue(DingDanInfoActivity.this);

        // 1 创建一个get请求
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            //正确接受数据之后的回调
            @Override
            public void onResponse(String response) {
                try {
//                    String newResponse = new String(response.getBytes("ISO-8859-1"),"UTF-8");
                    JSONObject object=new JSONObject(response);
//                    String ss=object.getString("message");
                    Log.e("sssssss",object.toString());
                    if(object.toString().contains("保存")){
                        ToastUtil.show(getApplicationContext(),"确认成功");
                        tv_yajin.setVisibility(View.GONE);
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

        private List<DingDanShopBean> mList;

        public MyAdapter( List<DingDanShopBean> list) {
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
                viewHolder = new ViewHolder();
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

            viewHolder.shop_img.setImageURL(mList.get(position).getOddr_commodity_picture_url());
//            Picasso.with(getApplicationContext()).load(mList.get(position).getOddr_commodity_picture_url()).into(viewHolder.shop_img);
            viewHolder.shop_name.setText(mList.get(position).getOddr_commodity_name());
            if(mList.get(position).getOddr_isback_barrel()==1){
                viewHolder.is_hui.setVisibility(View.GONE);
            }else{
                viewHolder.is_hui.setVisibility(View.VISIBLE);
            }

            viewHolder.shop_num.setText(mList.get(position).getOddr_number()+"");
            return convertView;
        }
        class ViewHolder {
            public TextView  shop_name,is_hui,shop_num;
            public MyImageView  shop_img;
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
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if(isLocServiceEnable(getApplicationContext())){
            mBaiduMap = mapView.getMap();
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            //定位初始化
            mLocationClient = new LocationClient(this);
            //通过LocationClientOption设置LocationClient相关参数
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            // 可选，设置地址信息
            option.setIsNeedAddress(true);
            //可选，设置是否需要地址描述
            option.setIsNeedLocationDescribe(true);

            //设置locationClientOption
            mLocationClient.setLocOption(option);

            //注册LocationListener监听器
            MyLocationListener myLocationListener = new MyLocationListener();
            mLocationClient.registerLocationListener(myLocationListener);
            //开启地图定位图层
            mLocationClient.start();
        }
    }
}
