package com.crystal.viewdraghelperdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mainlistview;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainlistview = (ListView) findViewById(R.id.mainlistview);
        listView = (ListView) findViewById(R.id.list);
        List<String> list = new ArrayList<String>();
        for(int i = 1; i <= 100; i++){
            list.add("hello" + i);
        }
        ArrayAdapter<String> adapter1= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        mainlistview.setAdapter(adapter1);
        listView.setAdapter(adapter2);
        /*TextView textView = (TextView) findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "你好", Toast.LENGTH_LONG).show();
            }
        });*/
    }


}
