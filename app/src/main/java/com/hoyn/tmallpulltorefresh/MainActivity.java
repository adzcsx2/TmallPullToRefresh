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
        group = (HoynRadioGroup) view_header.findViewById(R.id.group);
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


        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RadioButton rb = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
                        rb.performClick();
                        ptrFrameLayout.refreshComplete();
                    }
                }, 1000);
            }
        });
    }

}
