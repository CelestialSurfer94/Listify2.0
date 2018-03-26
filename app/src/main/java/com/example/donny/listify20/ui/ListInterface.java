package com.example.donny.listify20.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donny.listify20.R;
import com.example.donny.listify20.adapter.ItemTouchHelperCallback;
import com.example.donny.listify20.adapter.RecAdapter;
import com.example.donny.listify20.model.ListItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class ListInterface extends AppCompatActivity {
    private java.util.List<ListItem> data;
    private RecyclerView recView;
    private RecAdapter adapter;
    private SharedPreferences prefs;
    private LinearLayoutManager linearLayoutManager;
    private int ID;
    private int size;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recView = (RecyclerView) findViewById(R.id.rec_list_activity);
        adapter = new RecAdapter(this, data, this);
        prefs = this.getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
        ID = getIntent().getIntExtra("requestCode", -1);
        title = getIntent().getStringExtra(ID + "Title");
        size = getIntent().getIntExtra(ID + "Size", 0);
        data = new ArrayList<>();
        setTitle(title);
        for (int i = 0; i < size; i++) { //grab all saved items in list
            String str = prefs.getString(ID + "Text" + i, "");
            //getIntent().getStringExtra(ID + "Text" + i);
            Boolean isChecked = prefs.getBoolean(ID + "Bool" + i, false);
            ListItem current = new ListItem(str, isChecked);
            data.add(current);
        }
        toolbar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                changeTitleDialog();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_create_new_item);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });


        if (getIntent().getBooleanExtra("firstStart", false)) { // ?WTf
            changeTitleDialog();
            //tempSave();
        }

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recView);
        recView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(linearLayoutManager);
    }

    public void add() {
        ListItem item = new ListItem("", false);
        item.setFocused(true);
        data.add(item);
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        //recView.scrollToPosition(data.size()-1);
        //recView.smoothScrollToPosition(data.size()-1);
        int offset = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                - linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int pos = data.size() - 1;
        recView.smoothScrollToPosition(Math.min(pos + offset + 1, adapter.getItemCount()));
        //getIntent().putExtra(ID + "Size", adapter.getItemCount());
        prefs.edit().putInt(ID + "Size", adapter.getItemCount()).apply();
        View curView = this.getCurrentFocus();
        if (curView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(curView, 0); //TODO
        }
        //prefs.edit().putInt("numItems",adapter.getItemCount());
        //tempSave();
    }

    public void remove() {

    }

    @Override
    public void onBackPressed() {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        //save();
    }

    public void changeTitleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert list name");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                title = input.getText().toString();
                setTitle(title);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        //tempSave();
        input.requestFocus();
    }

    private void save() {
        tempSave();
        finish();
    }

    public void tempSave() {
        //List<ListItem> list = adapter.getData();
        for (int i = 0; i < size; i++) {
            ListItem current = data.get(i);
            prefs.edit().putString(ID + "Text" + i, current.getText()).apply();
            prefs.edit().putBoolean(ID + "Bool" + i, current.isChecked()).apply();
        }
        prefs.edit().putInt(ID + "Size", size).apply();
        prefs.edit().putString(ID + "Title", title).apply();
        setResult(RESULT_OK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            Snackbar.make(findViewById(android.R.id.content), "Are you sure you want to reset?", Snackbar.LENGTH_LONG)
                    .setAction("YES!", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(ListInterface.this, "Reset!", Toast.LENGTH_SHORT).show();
                            int size = data.size();
                            data.clear();
                            adapter.notifyItemRangeRemoved(0, size);
                            //prefs.edit().clear().apply();
                        }
                    }).show();
        }

        return super.onOptionsItemSelected(item);
    }

}
