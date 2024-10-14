package com.osfans.trime;

import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androlua.LuaActivity;
import com.androlua.LuaWebView;
import com.osfans.trime.pro.R;

/**
 * Created by nirenr on 2019/1/30.
 */

public class DialogActivity extends LuaActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        WindowManager.LayoutParams attr = window.getAttributes();
        attr.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(attr);

        setContentView(R.layout.edit_dialog);
        final EditText edit = findViewById(R.id.edit);
        edit.requestFocus();
        Button ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trime.getService().addPhrase(edit.getText().toString());
                finishAndRemoveTask();
            }
        });
        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });
        String uri = getIntent().getDataString();
        if (uri != null) {
            LinearLayout layout=new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout bar = new LinearLayout(this);
            ImageButton close = new ImageButton(this);
            close.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAndRemoveTask();
                }
            });
            Button title = new Button(this);
            title.setText(R.string.ime_name);
            title.setBackgroundColor(0);
            bar.addView(close);
            bar.addView(title);
            layout.addView(bar);
            LuaWebView web = new LuaWebView(this);
            web.loadUrl(uri);
            layout.addView(web,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            setContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDisplayMetrics().widthPixels));
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    /*@Override
    public String getLuaPath() {
        return getLuaPath("adFilter.lua");
    }*/

    @Override
    public void sendError(String title, Exception msg) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAndRemoveTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,android.R.id.home,0,"").setIcon(android.R.drawable.ic_menu_close_clear_cancel).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Implement this method
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
