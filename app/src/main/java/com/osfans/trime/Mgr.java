package com.osfans.trime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androlua.LuaApplication;
import com.osfans.trime.pro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by nirenr on 2018/12/30.
 */

public class Mgr extends ListActivity implements HttpUtil.HttpCallback {
    private String sharedDataDir;
    private String url = "http://jieshuo666.com/download/rime/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"正在加载..."}));
        sharedDataDir = Function.getPref(this).getString("user_data_dir", getString(R.string.default_shared_data_dir));
        String u = getIntent().getDataString();
        Log.i("trime", "onCreate: "+u);
        if(!TextUtils.isEmpty(u))
            url=u;
        HttpUtil.get(url, this);
    }

    @Override
    public void onDone(HttpUtil.HttpResult result) {
        if (result.code == 200) {
            String[] list = result.text.split("\n");
            Arrays.sort(list);
            setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
        } else {
            setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"加载出错: " + result.text}));
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final String text = ((TextView) v).getText().toString();
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(String.format("正在下载 %s...", text));
        mProgressDialog.show();
        if (text.endsWith("rime")) {
            final String path = new File(sharedDataDir, "download/" + text).getAbsolutePath();
            HttpUtil.download(url + text, path, new HttpUtil.HttpCallback() {
                @Override
                public void onDone(HttpUtil.HttpResult result) {
                    mProgressDialog.dismiss();
                    try {
                        FileUtil.unZip(path, sharedDataDir);
                        String name = new File(path).getName();
                        int i = name.lastIndexOf(".");
                        if (i > 0) {
                            name = name.substring(0, i);
                        }
                        i = name.indexOf("_");
                        if (i > 0) {
                            name = name.substring(0, i);
                        }
                        i = name.indexOf("(");
                        if (i > 0) {
                            name = name.substring(0, i);
                        }
                        logs(name,path);
                        new AlertDialog.Builder(Mgr.this)
                                .setMessage(R.string.done)
                                .setPositiveButton(android.R.string.ok, null)
                                .create()
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).setUpdateListerer(new HttpUtil.UpdateListener() {
                @Override
                public void onUpdate(String[] values) {
                    mProgressDialog.setMessage(String.format("%s \n%s %s", text, getString(R.string.waiting), values[0]));
                }
            });
        }
    }

    private void logs(String name,String path) {
        LuaApplication app = LuaApplication.getInstance();
        File f = new File(app.getLuaExtPath("install.json"));
        JSONArray arr = JsonUtil.loadArray(f);
        ArrayList<JSONObject> log = new ArrayList<>();
        int len = arr.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                if(name.equals(o.optString("name"))){
                    continue;
                }
                log.add(o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            ZipFile zip = new ZipFile(path);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            ArrayList<String> list = new ArrayList<>();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                list.add(entry.getName());
            }
            JSONObject json = new JSONObject();
            json.put("name",name);
            json.put("time",System.currentTimeMillis());
            json.put("list",new JSONArray(list));
            log.add(0,json);
            JsonUtil.save(f,new JSONArray(log));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
