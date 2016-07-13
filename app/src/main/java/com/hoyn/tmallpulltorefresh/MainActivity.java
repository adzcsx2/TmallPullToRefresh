package com.hoyn.tmallpulltorefresh;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private HoynPtrFrameLayout hoynPtrFrameLayout;
    private HoynRadioGroup group;
    private RadioButton btn_share, btn_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        hoynPtrFrameLayout = (HoynPtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        View view_header = LayoutInflater.from(this).inflate(R.layout.view_header, null);
        View tabview = LayoutInflater.from(this).inflate(R.layout.view_header_tab, null);
        tabview.findViewById(R.id.iv_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击了首页", Toast.LENGTH_SHORT).show();
            }
        });
        group = (HoynRadioGroup) view_header.findViewById(R.id.group);
        btn_share = (RadioButton) group.findViewById(R.id.radioButton1);
        btn_refresh = (RadioButton) group.findViewById(R.id.radioButton2);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "选择了分享", Toast.LENGTH_SHORT).show();
            }
        });
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "选择了刷新", Toast.LENGTH_SHORT).show();
            }
        });
        hoynPtrFrameLayout.setHeaderView(view_header);
        hoynPtrFrameLayout.setTabView(tabview);

        hoynPtrFrameLayout.setOnFiggerUpListener(new HoynPtrFrameLayout.OnFiggerUpListener() {
            @Override
            public void onFiggerUp(int checkedId) {
                if (checkedId == btn_share.getId()) {
                    //选择分享，直接到顶部
                    hoynPtrFrameLayout.scrollToTop();
                    btn_share.performClick();
                } else {
                    //选择刷新，显示loading，数据读取完，再回到顶部
                    hoynPtrFrameLayout.showProgressBar();
                    btn_refresh.performClick();
                }
                hoynPtrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hoynPtrFrameLayout.completeRefresh();
                    }
                }, 1000);
            }
        });
    }
}
