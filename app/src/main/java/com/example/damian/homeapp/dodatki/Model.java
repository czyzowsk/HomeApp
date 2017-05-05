package com.example.damian.homeapp.dodatki;

import android.view.View;

/**
 * Created by Damian on 2017-04-26.
 */
public class Model {

    private String name;
    private boolean selected;
    private boolean visible = false;
    private String info = null;

    public Model(String name) {
        this.name = name;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String data) {
        this.info = data;
        visible = true;
    }

    public String getInfo() {
        return info;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int switchIsVisible() {
        if (!visible)
            return View.VISIBLE;
        else return View.INVISIBLE;
    }

}