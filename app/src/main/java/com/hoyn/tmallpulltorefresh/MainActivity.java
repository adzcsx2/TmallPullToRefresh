package com.hoyn.tmallpulltorefresh;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final String TAG = "PtrFrameLayout";
    private HoynPtrFrameLayout ptrFrameLayout;
    private HoynRadioGroup group;
    private RadioButton button1, button2;

    //    private View progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        ptrFrameLayout = (HoynPtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        View view_header = LayoutInflater.from(this).inflate(R.layout.view_header, null);
        View tabview = LayoutInflater.from(this).inflate(R.layout.view_header_tab, null);
        tabview.findViewById(R.id.iv_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击了首页", Toast.LENGTH_SHORT).show();
            }
        });
        group = (HoynRadioGroup) view_header.findViewById(R.id.group);
        group.setTabView(tabview);
//        progressBar = view_header.findViewById(R.id.progressBar);
        button1 = (RadioButton) group.findViewById(R.id.radioButton1);
        button2 = (RadioButton) group.findViewById(R.id.radioButton2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "选择了button1", Toast.LENGTH_SHORT).show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "选择了button2", Toast.LENGTH_SHORT).show();
            }
        });

        ptrFrameLayout.setMyRadioGroup(group);
        ptrFrameLayout.setHeaderView(view_header);
//        ptrFrameLayout.setProgressBar(progressBar);

        ptrFrameLayout.setOnFiggerUpListener(new HoynPtrFrameLayout.OnFiggerUpListener() {
            @Override
            public void onFiggerUp(int checkedId) {
                if (checkedId == button2.getId()) {
                    ptrFrameLayout.showProgressBar();
                    Toast.makeText(MainActivity.this, "选择了button2", Toast.LENGTH_SHORT).show();

                } else {
                    ptrFrameLayout.scrollToTop();
                    Toast.makeText(MainActivity.this, "选择了button1", Toast.LENGTH_SHORT).show();
                }
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrameLayout.refreshComplete();
                    }
                }, 1000);
            }
        });
    }

}
