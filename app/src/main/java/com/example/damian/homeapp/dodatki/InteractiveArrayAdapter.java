package com.example.damian.homeapp.dodatki;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.damian.homeapp.R;
import com.example.damian.homeapp.SharedPref;
import com.example.damian.homeapp.TaskService;

import java.lang.annotation.Target;
import java.util.List;

/**
 * Created by Damian on 2017-04-26.
 */

public class InteractiveArrayAdapter extends ArrayAdapter<Model> {

    private final List<Model> list;
    private final Activity context;

    public InteractiveArrayAdapter(Activity context, List<Model> list) {
        super(context, R.layout.row_layout, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected TextView textSecond;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.row_layout, null, true);

        ViewHolder holder = new ViewHolder();

        holder.text = (TextView) view.findViewById(R.id.texFirst);
        holder.textSecond = (TextView) view.findViewById(R.id.textSecond);

        Model mModel = list.get(position);

        holder.text.setText(mModel.getName());
        holder.textSecond.setText(mModel.getInfo());

        return view;
    }


}
