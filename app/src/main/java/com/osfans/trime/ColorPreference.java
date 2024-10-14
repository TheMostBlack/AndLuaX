package com.osfans.trime;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nirenr on 2020/2/20.
 */

public class ColorPreference extends Preference{
    private final SharedPreferences mPref;
    private View mView;
    private int mColor;

    public ColorPreference(Context context) {
        super(context);
        mPref=Function.getPref(context);
        /*GradientDrawable gd = new GradientDrawable();
        int w = Config.getPixel(48f);
        gd.setBounds(w,w,w,w);
        gd.setColor(getColor());
        gd.setCornerRadius(Config.getPixel(48f));*/
       // setIcon(gd);
        //setIcon(new BitmapDrawable(Bitmap.createBitmap(Config.getPixel(256f), Config.getPixel(256f), Bitmap.Config.ARGB_8888)));
    }

    @Override
    protected void onClick() {
        new ColorSelectDiaLog(getContext(), getKey(), getColor(), this).show();
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        mView = super.getView(convertView, parent);
        ViewGroup v= (ViewGroup) mView;
        int w = Config.getPixel(48f);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(getColor());
        gd.setCornerRadius(w);
        v.getChildAt(0).setBackground(gd);
        ViewGroup.LayoutParams lp = v.getChildAt(0).getLayoutParams();
        lp.width=w;
        lp.height=w;
        v.getChildAt(0).setLayoutParams(lp);
        v.getChildAt(0).setVisibility(View.VISIBLE);
        //mView.setBackgroundColor(mColor);
        return mView;
    }

    public int getColor(){
        int def=0;
        Config config = Config.get();
        if(config!=null) {
            Integer c = config.getColor(getKey());
            if(c!=null)
                def=c;
        }
        mColor=mPref.getInt(getKey(),def);
        return mColor;
    }

    public void setColor(int color){
        ViewGroup v= (ViewGroup) mView;
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(Config.getPixel(48f));
        v.getChildAt(0).setBackground(gd);
    }
}
