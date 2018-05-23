package com.hvantage2.money4driveeee.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


import com.hvantage2.money4driveeee.R;


/**
 * Created by ebabu on 30/4/15.
 */
public class CustomButton extends Button {
    private Typeface tf = null;
    private String customFont;

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFontTextView(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFontTextView(context, attrs);
    }

    public CustomButton(Context context) {
        super(context);

    }

    public boolean setCustomFontTextView(Context ctx, String asset) {
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            return false;
        }
        setTypeface(tf);
        return true;
    }

    private void setCustomFontTextView(Context ctx, AttributeSet attrs) {
        final TypedArray a = ctx.obtainStyledAttributes(attrs,
                R.styleable.CustomEditText);
        customFont = a.getString(R.styleable.CustomEditText_edittextfont);

        // custompwd = a.getString(R.styleable.CustomEditText_edittextpwd);

        setCustomFontTextView(ctx, customFont);
        a.recycle();
    }

}