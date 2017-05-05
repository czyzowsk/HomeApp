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
        protected Switch mSwitch;
        protected RelativeLayout relativeLayout;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        final SharedPref config = new SharedPref(context);
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.row_layout, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.texFirst);
            viewHolder.textSecond = (TextView) view.findViewById(R.id.textSecond);

            viewHolder.mSwitch = (Switch) view.findViewById(R.id.switch1);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getName());
        holder.textSecond.setText(list.get(position).getInfo());

        holder.mSwitch.setChecked(list.get(position).isSelected());
        holder.mSwitch.setVisibility(list.get(position).switchIsVisible());

        return view;
    }


}
