package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hvantage2.money4driveeee.R;


/**
 * Created by Hvantage2 on 2018-02-17.
 */

  public class CustomSwipeAdapter extends PagerAdapter {
    private int[] image_resources={R.drawable.appicon, R.drawable.appicon, R.drawable.appicon,R.drawable.appicon};
    private Context ctx;
    private LayoutInflater layoutInflater;


    public CustomSwipeAdapter(Context ctx){
        this.ctx=ctx;
    }
    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater=(LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view=layoutInflater.inflate(R.layout.swipe_layout,container,false);
        ImageView imageView=(ImageView) item_view.findViewById(R.id.image_count);
        TextView textView =(TextView) item_view.findViewById(R.id.advertise);

        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.airport_cab);
                textView.setText("Airport Cabs Advertising");
                break;
            case 1:
                imageView.setImageResource(R.drawable.auto);
                textView.setText("Airport Auto Advertising");

                break;
            case 2:
                imageView.setImageResource(R.drawable.bus);
                textView.setText("Airport Bus Advertising");

                break;
            case 3:
                imageView.setImageResource(R.drawable.truck);
                textView.setText("Airport Truck Advertising");

                break;


        }
        container.addView(item_view);

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
