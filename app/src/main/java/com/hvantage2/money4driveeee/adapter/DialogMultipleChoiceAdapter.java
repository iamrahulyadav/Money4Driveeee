package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.hvantage2.money4driveeee.model.ShopActivity;
import com.hvantage2.money4driveeee.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hvantage on 3/26/2018.
 */

public class DialogMultipleChoiceAdapter extends BaseAdapter {
    LayoutInflater mLayoutInflater;
    List<ShopActivity> mItemList;

    public DialogMultipleChoiceAdapter(Context context, List<ShopActivity> itemList) {
        mLayoutInflater = LayoutInflater.from(context);
        mItemList = itemList;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public ShopActivity getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public List<String> getSelectedActivitiesIds() {
        List<String> checkedItemList = new ArrayList<>();
        for (ShopActivity item : mItemList) {
            if (item.isSeleted()) {
                checkedItemList.add(item.getActivity_id());
            }
        }
        return checkedItemList;
    }

    public List<String> getSelectedActivitiesNames() {
        int counter=0;
        List<String> checkedItemList = new ArrayList<>();
        for (ShopActivity item : mItemList) {
            if (item.isSeleted()) {
                counter=counter+1;
                checkedItemList.add(counter+". "+item.getActivity_name());
            }
        }
        return checkedItemList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mItemList.get(position).isSeleted())
            holder.checkbox.setChecked(true);
        else
            holder.checkbox.setChecked(false);

        // ShopActivity item = getItem(position);
        holder.checkbox.setText(mItemList.get(position).getActivity_name());

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemList.get(position).isSeleted()) {
                    mItemList.get(position).setSeleted(false);
                    holder.checkbox.setChecked(false);
                } else {
                    mItemList.get(position).setSeleted(true);
                    holder.checkbox.setChecked(true);
                }
            }
        });
        return convertView;
    }

    private void updateItemState(ViewHolder holder, boolean checked) {
        holder.checkbox.setChecked(checked);
    }

    private static class ViewHolder {
        View root;
        CheckBox checkbox;

        ViewHolder(View view) {
            root = view;
            checkbox = view.findViewById(R.id.checkbox);
        }
    }
}