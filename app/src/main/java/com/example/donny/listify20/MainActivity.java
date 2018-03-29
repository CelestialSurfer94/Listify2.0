package com.example.donny.listify20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.donny.listify20.adapter.MainAdapter;
import com.example.donny.listify20.adapter.MainTouchHelperCallback;
import com.example.donny.listify20.model.ListItem;
import com.example.donny.listify20.ui.ListInterface;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ArrayList<ListItem>> data;
    private RecyclerView recView;
    private MainAdapter adapter;
    SharedPreferences prefs;
    private LinearLayoutManager linearLayoutManager;
    private int numLists;
    private ArrayList<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = this.getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        //prefs.edit().clear().apply();
        data = new ArrayList<>();
        numLists = prefs.getInt("numLists", 0);
        titles = new ArrayList<>();
        for (int i = 0; i < numLists; i++) {
            int curListSize = prefs.getInt(i + "Size", 0);
            titles.add(prefs.getString(i + "Title", "New List!"));
            ArrayList<ListItem> temp = new ArrayList<>();
            for (int j = 0; j < curListSize; j++) {
                String textVal = prefs.getString(i + "Text" + j, "");
                Boolean boolVal = prefs.getBoolean(i + "Bool" + j, false);
                ListItem curItem = new ListItem(textVal, boolVal);
                temp.add(j, curItem);
            }
            data.add(i, temp);
        }
        recView = (RecyclerView) findViewById(R.id.rec_list_main);
        adapter = new MainAdapter(this, data, titles);
        adapter.notifyDataSetChanged();
        recView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper.Callback callback = new MainTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recView);

        //initialize floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewList();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //add new list to main activity
    public void addNewList() { //addNewList new list to view
        Intent i = new Intent(this, ListInterface.class);
        i.putExtra("firstStart", true);
        i.putExtra("requestCode", numLists);
        titles.add("New List!!");
        prefs.edit().putInt("numLists",++numLists).apply();
        startActivityForResult(i, numLists -1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { //changed: was && data != null
            super.onActivityResult(requestCode, resultCode, data);
            String title = prefs.getString(requestCode + "Title", "uh oh?");
            titles.set(requestCode, title);
            Bundle Mbundle = data.getExtras();
            int size = Mbundle.getInt("Size");
            //int size = data.getIntExtra("Size",0);
            ArrayList<ListItem>  newList = new ArrayList<ListItem>();
            for (int i = 0; i <size ; i++) {
                String curStr = prefs.getString(requestCode+"Text"+i,"errorOnResult");
                Boolean curBool = prefs.getBoolean(requestCode+"Bool"+i, false);
                ListItem cur = new ListItem(curStr, curBool);
                newList.add(cur);
            }
            if(requestCode < this.data.size()){
                this.data.set(requestCode, newList);
            } else {
                this.data.add(newList);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reset) {
            Snackbar.make(findViewById(android.R.id.content), "Are you sure you want to reset?", Snackbar.LENGTH_LONG)
                    .setAction("YES!", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Reset!", Toast.LENGTH_SHORT).show();
                            int size = data.size();
                            data.clear();
                            titles.clear();
                            adapter.notifyItemRangeRemoved(0, size);
                            prefs.edit().clear().apply();
                            numLists = 0;
                        }
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
