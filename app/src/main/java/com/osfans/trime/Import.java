package com.osfans.trime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.androlua.LuaApplication;
import com.androlua.LuaUtil;
import com.osfans.trime.pro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by nirenr on 2019/6/3.
 */

public class Import extends Activity {
    private String sharedDataDir;
    private File mDownloadDir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.import_title);
        sharedDataDir = Function.getPref(this).getString("user_data_dir", getString(R.string.default_shared_data_dir));
        mDownloadDir = new File(sharedDataDir, "Download");
        if (!mDownloadDir.exists())
            mDownloadDir.mkdirs();
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String path = data.getPath();
            Log.i("ManagerActivity", path + "");
            if (path != null) {
                if ("content".equals(data.getScheme())) {
                    try {
                        InputStream in = getContentResolver().openInputStream(data);
                        String path2 = new File(mDownloadDir,new File(data.getPath()).getName()).getAbsolutePath();
                        FileOutputStream out = new FileOutputStream(path2);
                        LuaUtil.copyFile(in, out);
                        out.close();
                        load(path2);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                int idx = path.indexOf("/storage/emulated/");
                if (idx > 0)
                    path = path.substring(idx);
                load(path);
            }
        }
    }

    private boolean load(String path) {
        if (!new File(path).exists()) {
            finish();
            return false;
        }
        String dir;
        String type = path.substring(path.length() - 4);
        LuaApplication app = LuaApplication.getInstance();
        return load(path, app.getLuaExtDir() + File.separator, type);
    }

    private boolean load(final String path, final String dir, final String type) {
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
        String title = "导入 " + name;
        final String fname=name;
        new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            LuaUtil.unZip(path, dir);
                            logs(fname,path);
                            Toast.makeText(Import.this, R.string.done, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(Import.this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAndRemoveTask();
                        } else {
                            finish();
                        }

                    }
                })
                .create()
                .show();
        return true;
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
