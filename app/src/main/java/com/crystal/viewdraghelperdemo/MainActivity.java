package com.crystal.viewdraghelperdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

   /* private ListView mainlistview;
    private ListView listView;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        /*mainlistview = (ListView) findViewById(R.id.mainlistview);
        listView = (ListView) findViewById(R.id.list);
        List<String> list = new ArrayList<String>();
        for(int i = 1; i <= 100; i++){
            list.add("hello" + i);
        }
        ArrayAdapter<String> adapter1= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        mainlistview.setAdapter(adapter1);
        listView.setAdapter(adapter2);
        *//*TextView textView = (TextView) findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "你好", Toast.LENGTH_LONG).show();
            }
        });*/



        final TextView viewHeader = (TextView) findViewById(R.id.header);
        final YoutubeLayout youtubeLayout = (YoutubeLayout) findViewById(R.id.dragLayout);
        final ListView listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewHeader.setText(listView.getAdapter().getItem(i).toString());
                youtubeLayout.setVisibility(View.VISIBLE);
                youtubeLayout.maximize();
            }
        });

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 50;
            }

            @Override
            public String getItem(int i) {
                return "object" + i;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View rView, ViewGroup viewGroup) {
                View view = rView;
                if (view == null) {
                    view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                }
                ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(i));
                return view;
            }
        });
    }


}
