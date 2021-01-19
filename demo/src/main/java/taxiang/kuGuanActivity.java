package taxiang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.taxiang.R;

import java.util.ArrayList;
import java.util.List;

public class kuGuanActivity  extends FragmentActivity implements View.OnClickListener  {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ku_guan);
        TaXiangActivity.flag=true;
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
    }
    private void initDatas() {
        mFragments = new ArrayList<>();
        //将四个Fragment加入集合中
        mFragments.add(new ChuRukuActivity());
        mFragments.add(new BuShuiFragment());
        mFragments.add(new TongJiFragment());
//        mFragments.add(new MyFragment());

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
        mTabSetting.setVisibility(View.GONE);
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
//            case R.id.id_tab_setting:
//                selectTab(3);
//                break;
        }
    }

    private void selectTab(int i) {
        //根据点击的Tab设置对应的ImageButton为绿色
        switch (i) {
            case 0:
                mImgWeixin.setImageResource(R.mipmap.dingdan);
                mImgFrd.setImageResource(R.mipmap.bushuip);
                mImgAddress.setImageResource(R.mipmap.tongjip);
//                mImgSetting.setImageResource(R.mipmap.myp);
                tv_dingdan.setTextColor(Color.parseColor("#198CFF"));
                tv_bushui.setTextColor(Color.parseColor("#616466"));
                tv_tongji.setTextColor(Color.parseColor("#616466"));
//                tv_my.setTextColor(Color.parseColor("#616466"));
                break;
            case 1:
                mImgWeixin.setImageResource(R.mipmap.dingdanp);
                mImgFrd.setImageResource(R.mipmap.bushui);
                mImgAddress.setImageResource(R.mipmap.tongjip);
//                mImgSetting.setImageResource(R.mipmap.myp);
                tv_dingdan.setTextColor(Color.parseColor("#616466"));
                tv_bushui.setTextColor(Color.parseColor("#198CFF"));
                tv_tongji.setTextColor(Color.parseColor("#616466"));
//                tv_my.setTextColor(Color.parseColor("#616466"));
                break;
            case 2:
                mImgWeixin.setImageResource(R.mipmap.dingdanp);
                mImgFrd.setImageResource(R.mipmap.bushuip);
                mImgAddress.setImageResource(R.mipmap.tongji);
//                mImgSetting.setImageResource(R.mipmap.myp);
                tv_dingdan.setTextColor(Color.parseColor("#616466"));
                tv_bushui.setTextColor(Color.parseColor("#616466"));
                tv_tongji.setTextColor(Color.parseColor("#198CFF"));
//                tv_my.setTextColor(Color.parseColor("#616466"));
                break;
            case 3:
                mImgWeixin.setImageResource(R.mipmap.dingdanp);
                mImgFrd.setImageResource(R.mipmap.bushuip);
                mImgAddress.setImageResource(R.mipmap.tongjip);
//                mImgSetting.setImageResource(R.mipmap.my);
                tv_dingdan.setTextColor(Color.parseColor("#616466"));
                tv_bushui.setTextColor(Color.parseColor("#616466"));
                tv_tongji.setTextColor(Color.parseColor("#616466"));
//                tv_my.setTextColor(Color.parseColor("#198CFF"));
                break;
        }
        //设置当前点击的Tab所对应的页面
        mViewPager.setCurrentItem(i);
    }
}
