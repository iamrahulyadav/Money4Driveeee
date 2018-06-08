package com.hvantage2.money4driveeee.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.model.StateCityModel;

import java.util.List;

public class StateCityAdapter extends ArrayAdapter<StateCityModel> {
    LayoutInflater flater;

    public StateCityAdapter(Activity context, int resouceId, int textviewId, List<StateCityModel> list) {
        super(context, resouceId, textviewId, list);
        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView,position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView,position);
    }

    private View rowview(View convertView , int position){

        StateCityModel rowItem = getItem(position);

        viewHolder holder ;
        View rowview = convertView;
        if (rowview==null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.state_city_item_layout, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.tvTitle);
            rowview.setTag(holder);
        }else{
            holder = (viewHolder) rowview.getTag();
        }
        holder.txtTitle.setText(rowItem.getName());

        return rowview;
    }

    private class viewHolder{
        TextView txtTitle;
    }


}
