package com.example.donny.listify20.model;

import android.widget.Button;

import java.util.List;

/**
 * Created by donny on 8/27/17.
 */

public class ListItem {
    private String text;
    private boolean focused;
    private boolean isChecked;

    public ListItem(String text, boolean checked){
        this.text = text;
        focused = false;
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getText() {
        return text;

    }


    public void setText(String title) {
        this.text = title;
    }

    public void setFocused(boolean val){
        focused = val;
    }

    public boolean isFocused(){
        return focused;
    }
}
