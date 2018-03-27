package com.example.donny.listify20.adapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donny.listify20.R;
import com.example.donny.listify20.model.ListItem;
import com.example.donny.listify20.ui.ListInterface;

import java.util.Collections;
import java.util.List;
import com.example.donny.listify20.ui.ListInterface;

/**
 * Created by donny on 8/27/17.
 */

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.MyViewHolder> {

    private final LayoutInflater inflator;
    private List<ListItem> data;
    private MyViewHolder holder;
    private ListInterface listInterface;

    public RecAdapter(Context context, List<ListItem> data, ListInterface listInterface){
        inflator = LayoutInflater.from(context);
        this.data = data;
        this.listInterface = listInterface;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflator.inflate(R.layout.list_item,parent, false);
        holder = new MyViewHolder(v, new MyCustomEditTextListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ListItem current = data.get(position);
        holder.textwatcher.updatePosition(holder.getAdapterPosition());
        holder.text.setText(data.get(holder.getAdapterPosition()).getText());
        holder.checkBox.setChecked(data.get(holder.getAdapterPosition()).isChecked());
        if (current.isFocused()) {
            holder.text.requestFocus();
        }
        current.setFocused(false);
    }

    public void add(ListItem item){
        data.add(item);
        if(item.isFocused()){
        }
        Toast.makeText(listInterface, "recAdapter Add was called", Toast.LENGTH_SHORT).show();
        //listInterface.tempSave();
    }

    public void remove(int position){
        data.remove(position);
        notifyItemRemoved(position);
        listInterface.remove(position);

    }
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.v("", "Log position" + fromPosition + " " + toPosition);
        if (fromPosition < data.size() && toPosition < data.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(data, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(data, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<ListItem> getData(){
        return data;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        EditText text;
        CheckBox checkBox;
        MyCustomEditTextListener textwatcher;
        public MyViewHolder(final View itemView, MyCustomEditTextListener watcher) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                    int pos = getAdapterPosition();
                    data.get(getAdapterPosition()).setChecked(isChecked);
                    listInterface.saveSingle(pos); //TODO change to only update the current item, not the entire list here
                }
            });
            text = (EditText) itemView.findViewById(R.id.editText);
            text.setLongClickable(false);
            text.setBackgroundResource(R.drawable.custom_edit_text);
            textwatcher = watcher;
            text.addTextChangedListener(textwatcher);
            text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if(actionId == EditorInfo.IME_ACTION_DONE){
                        //Toast.makeText(textView.getContext(), "TEST", Toast.LENGTH_SHORT).show();
                        listInterface.add();
                        //listInterface.tempSave(); //TODO same as above
                        return true;
                    }
                    return true;
                }
            });
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            boolean isChecked = data.get(position).isChecked();
            data.set(position, new ListItem(charSequence.toString(),isChecked));

        }

        @Override
        public void afterTextChanged(Editable editable) {
            listInterface.saveSingle(position);
        }
    }
}
