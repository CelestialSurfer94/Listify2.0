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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.donny.listify20.adapter.ItemTouchHelperCallback;
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
        prefs.edit().clear().apply();
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void add() { //add new list to view
        ListItem newItem = new ListItem("",false);
        ArrayList<ListItem>  newList = new ArrayList<ListItem>();
        //newList.add(newItem);
        adapter.notifyItemInserted(numLists);

        Intent i = new Intent(this, ListInterface.class);
/*
        i.putExtra(numLists + "Size", 1);
        i.putExtra(numLists + "Text" + 0, "");
        i.putExtra(numLists + "Bool" + 0, false);
        i.putExtra("requestCode", numLists);
        i.putExtra(numLists + "Title", "New List!!");
        i.putExtra("firstStart", true);
        data.add(newList); // was (0,newList)
        titles.add("New List!!");
        int temp = numLists;
        prefs.edit().putInt("numLists",++numLists).apply();
        */

        startActivityForResult(i, numLists);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            super.onActivityResult(requestCode, resultCode, data);
            Bundle MBundle = data.getExtras();
            String title = MBundle.getString(requestCode + "Title");
            titles.set(requestCode, title);
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
