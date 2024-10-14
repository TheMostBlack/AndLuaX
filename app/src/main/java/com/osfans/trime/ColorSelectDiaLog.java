package com.osfans.trime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.Preference;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by nirenr on 2020/2/19.
 */

public class ColorSelectDiaLog implements SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener {
    private final ColorPreference mPreference;
    private Context mContext;
    private String mKey;
    private GridLayout layout;
    private SharedPreferences mPref;
    private int mColor;
    private int mAlpha;
    private int mRed;
    private int mGreen;
    private int mBlue;
    private TextView mText;
    private AlertDialog mDlg;

    public ColorSelectDiaLog(Context context, String key, Integer finalColor, ColorPreference pref) {
        mContext = context;
        mKey = key;
        mPreference=pref;
        mPref = Function.getPref(mContext);
        mColor = mPref.getInt(key, finalColor);

    }

    public void show() {
        mAlpha = mColor >>> 24;
        mRed = mColor >>> 16 & 0xff;
        mGreen = mColor >>> 8 & 0xff;
        mBlue = mColor & 0xff;
        layout = new GridLayout(mContext);
        //layout.setOrientation(LinearLayout.VERTICAL);
        layout.setColumnCount(2);
        layout.setBackgroundColor(mColor);
        layout.addView(new TextView(mContext));
        mText = new TextView(mContext);
        mText.setTextSize(18);
        mText.setGravity(Gravity.CENTER);
        //mText.setText(Integer.toHexString(mColor));
        layout.addView(mText,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        createSeekBar(layout, 0, mAlpha, "A");
        createSeekBar(layout, 1, mRed, "R");
        createSeekBar(layout, 2, mGreen, "G");
        createSeekBar(layout, 3, mBlue, "B");
        mDlg=new AlertDialog.Builder(mContext)
                .setTitle("选择颜色")
                .setView(layout)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton("默认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Config c = Config.get();
                        if(c==null)
                            return;
                        Integer cl = c.getRawColor(mKey);
                        if(cl==null)
                            cl=0;
                        mColor=cl;
                        dialog.dismiss();
                        show();
                    }
                })
                .create();
                mDlg.show();
        mDlg.setTitle(Integer.toHexString(mColor));
    }

    private SeekBar createSeekBar(GridLayout layout, int i, int def, String t) {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SeekBar seek = new SeekBar(mContext);
        seek.setOnSeekBarChangeListener(this);
        seek.setId(i);
        seek.setMax(255);
        seek.setProgress(def);
        TextView tv = new TextView(mContext);
        tv.setText(t);
        layout.addView(tv);
        layout.addView(seek, lp);
        return null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case 0:
                mAlpha = progress;
                break;
            case 1:
                mRed = progress;
                break;
            case 2:
                mGreen = progress;
                break;
            case 3:
                mBlue = progress;
                break;
        }
        mColor=Color.argb(mAlpha, mRed, mGreen, mBlue);
        if(mDlg!=null)
           mDlg.setTitle(Integer.toHexString(mColor));
        layout.setBackgroundColor(mColor);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mPref.edit().putInt(mKey, Color.argb(mAlpha, mRed, mGreen, mBlue)).apply();
        mPreference.setSummary(String.format("#%08x", mColor));
        mPreference.setColor(mColor);
        Trime trime = Trime.getService();
        if(trime!=null)
            trime.invalidate();
    }
}
