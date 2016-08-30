package com.crystal.viewdraghelperdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button vdh = (Button) findViewById(R.id.btn_vdh);
        final Button youtube = (Button) findViewById(R.id.btn_youtube);
        vdh.setOnClickListener(this);
        youtube.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btn_vdh:
                intent.setClass(MainActivity.this, VdhActivity.class);
                break;
            case R.id.btn_youtube:
                intent.setClass(MainActivity.this, YoutubeActivity.class);
                break;
        }
        startActivity(intent);
    }
}
