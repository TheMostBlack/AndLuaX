package com.osfans.trime;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.androlua.LuaApplication;
import com.osfans.trime.pro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nirenr on 2019/6/4.
 */

public class LogsActivity extends ListActivity {
    private ArrayList<String> name;
    private ArrayList<JSONObject> log;
    private JSONArray mArr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LuaApplication app = LuaApplication.getInstance();
        File f = new File(app.getLuaExtPath("install.json"));
        mArr = JsonUtil.loadArray(f);
        log = new ArrayList<>();
        name = new ArrayList<>();
        int len = mArr.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject o = mArr.getJSONObject(i);
                name.add(o.optString("name"));
                log.add(o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setListAdapter(new ArrayListAdapter<String>(this, name));
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            final JSONArray arr = log.get(position).getJSONArray("list");
            final int len = arr.length();
            final String[] list = new String[len];
            //boolean[] set = new boolean[len];
            for (int i = 0; i < len; i++) {
                list[i] = arr.optString(i);
                //set[i]=true;
            }
            final AlertDialog dlg = new AlertDialog.Builder(this)
                    .setTitle(name.get(position))
                    .setItems(list, null)
                    .setPositiveButton(R.string.delete, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
            dlg.show();
            dlg.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                    ListView lv = dlg.getListView();
                    LuaApplication app = LuaApplication.getInstance();
                    for (int i = 0; i < len; i++) {
                        //if(lv.isItemChecked(i))
                        //noinspection ResultOfMethodCallIgnored
                        try{
                            new File(app.getLuaExtPath(list[i])).delete();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //LuaUtil.rmDir(new File(app.getLuaExtPath(list[i])));
                    }
                    for (int i = 0; i < len; i++) {
                        //if(lv.isItemChecked(i))
                        //noinspection ResultOfMethodCallIgnored
                        try{
                            new File(app.getLuaExtPath(list[i])).delete();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //LuaUtil.rmDir(new File(app.getLuaExtPath(list[i])));
                    }
                    log.remove(position);
                    mArr.remove(position);
                    name.remove(position);
                    JsonUtil.save(new File(app.getLuaExtPath("install.json")), mArr);
                    setListAdapter(new ArrayListAdapter<String>(LogsActivity.this, name));
                    Toast.makeText(LogsActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
