package com.example.donny.listify20.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.donny.listify20.R;
import com.example.donny.listify20.model.ListItem;
import com.example.donny.listify20.ui.ListInterface;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by donny on 8/27/17.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    private final LayoutInflater inflator;
    private ArrayList<ArrayList<ListItem>> data;
    private Context context;
    private ArrayList<String> titles;
    private SharedPreferences prefs;

    public MainAdapter(Context context, ArrayList<ArrayList<ListItem>> data , ArrayList<String>titles){
        inflator = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        this.titles = titles;
        prefs = context.getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflator.inflate(R.layout.main_item,parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText(titles.get(position));
        holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final MainAdapter.MyViewHolder hol = holder;
        holder.listNumber.setText((position+1)+")");
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ListInterface.class);
                int pos = hol.getAdapterPosition();
                ArrayList<ListItem> cur = data.get(pos);
                //final int pos = holder.getAdapterPosition(); HERE LIES THE PROBLEM
                int curSize = cur.size();
                //intent.putExtra(pos+"Title",titles.get(pos));
                intent.putExtra(pos+"Size",curSize);
                intent.putExtra("requestCode",pos);
                ((Activity) context).startActivityForResult(intent,pos);
            }
        });
        //holder.numberCompleted.setText("Completed: " +prefs.getInt(position+"NumComplete",0));
        holder.numberCompleted.setText(prefs.getInt(position+"NumComplete",0)+"/" + prefs.getInt(position+"Size",0));

    }

    public void remove(int position){
       prefs.edit().clear().apply();
       ArrayList<ListItem> removedList = data.remove(position);
       titles.remove(position);
       int size = data.size();
       for (int i = 0; i < size; i++) {
           saveList(i);
        }

        notifyDataSetChanged();
        prefs.edit().putInt("numLists",size).apply();
        //notifyItemRangeChanged(position,getItemCount());
    }

    public void saveList(int ID){
            ArrayList<ListItem> curList = data.get(ID);
            int numComplete = 0;
            int size = curList.size();
            for (int i = 0; i < size; i++) {
                ListItem current = curList.get(i);
                prefs.edit().putString(ID + "Text" + i, current.getText()).apply();
                prefs.edit().putBoolean(ID + "Bool" + i, current.isChecked()).apply();
                if(current.isChecked()){
                    numComplete++;
                }
                //returnData.add(current);
            }
            //getIntent().putExtra("Size", size);
            prefs.edit().putInt(ID+"NumComplete",numComplete).apply();
            prefs.edit().putInt(ID + "Size", size).apply();
            prefs.edit().putString(ID + "Title", titles.get(ID)).apply();

    }
    public boolean onItemMove(int fromPosition, int toPosition) {
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
            swapPrefs(fromPosition,toPosition);
            notifyItemMoved(fromPosition, toPosition);
            //notifyDataSetChanged();
        }
        return true;
    }

    private void swapPrefs(int fromPosition, int toPosition){ //todo error checking/handling
        ArrayList<ListItem> fromList = data.get(fromPosition);
        ArrayList<ListItem> toList = data.get(toPosition);
        int fromSize = data.get(fromPosition).size();
        int toSize = data.get(toPosition).size();

        //
        for (int i = 0; i < toSize; i++) {
            ListItem cur = toList.get(i);
            prefs.edit().putString(toPosition+"Text"+i,cur.getText()).apply();
            prefs.edit().putBoolean(toPosition+"Bool"+i, cur.isChecked()).apply();
        }

        for (int i = 0; i < fromSize; i++) {
            ListItem cur = fromList.get(i);
            prefs.edit().putString(fromPosition+"Text"+i,cur.getText()).apply();
            prefs.edit().putBoolean(fromPosition+"Bool"+i,cur.isChecked()).apply();
        }

        if(fromSize > toSize){
            for (int i = toSize; i < fromSize; i++) {
                prefs.edit().remove(toPosition+"Text"+i).apply();
                prefs.edit().remove(toPosition+"Bool"+i).apply();
            }
        } else if(toSize > fromSize){
            for(int i = fromSize; i < toSize; i++){
                prefs.edit().remove(fromPosition+"Text"+i).apply();
                prefs.edit().remove(fromPosition+"Bool"+i).apply();
            }
        }
        String fromTitle = prefs.getString(fromPosition+"Title","YOU FUCKED SOMETHING UP");
        prefs.edit().putString(fromPosition+"Title",titles.get(toPosition)).apply();
        prefs.edit().putString(toPosition+"Title",fromTitle).apply();
        prefs.edit().putInt(fromPosition+"Size",fromSize).apply();
        prefs.edit().putInt(toPosition+"Size",toSize).apply();
        Collections.swap(titles,fromPosition,toPosition);

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

   // public ListInterface<String> getData(){
       // return data;
    //}


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView listNumber;
        TextView numberCompleted;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.main_textView);
            listNumber = (TextView)itemView.findViewById(R.id.main_listNumber);
            numberCompleted = (TextView)itemView.findViewById(R.id.numberCompleted);
        }
    }
}
