package com.osfans.trime;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ArrayListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androlua.LuaApplication;
import com.nirenr.LocaleComparator;
import com.osfans.trime.pro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by nirenr on 2019/7/15.
 */

public class DownloadActivity extends ListActivity implements HttpUtil.HttpCallback, AdapterView.OnItemLongClickListener {
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String BASE = "http://jieshuo666.com/download/rime/";
    private String mUrl;
    private ArrayListAdapter<String> mAdapter;
    private static final String[] mTypes = new String[]{
            "downloaded/",
            "schema/",
            "sound/",
            "skin/",
            "plugin/",
            "tool/",
    };
    private ArrayList<String> mUrls = new ArrayList<>();
    private String sharedDataDir;
    private File mDownloadDir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedDataDir = Function.getPref(this).getString("user_data_dir", getString(R.string.default_shared_data_dir));
        mDownloadDir = new File(sharedDataDir, "Download");
        if (!mDownloadDir.exists())
            mDownloadDir.mkdirs();
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(URL);
        if(mUrl==null)
            mUrl=intent.getDataString();
        mAdapter = new ArrayListAdapter<String>(this);
        setListAdapter(mAdapter);
        if (mUrl == null) {
            setTitle(R.string.download_activity_title);
            mAdapter.add(getString(R.string.downloaded));
            mAdapter.add(getString(R.string.schema_title));
            mAdapter.add(getString(R.string.sound_package_title));
            mAdapter.add(getString(R.string.skin_title));
            mAdapter.add(getString(R.string.plugin_title));
            mAdapter.add(getString(R.string.tool_title));
            return;
        }
        String title = intent.getStringExtra(TITLE);
        if(TextUtils.isEmpty(title))
            title=intent.getAction();
        if(TextUtils.isEmpty(title))
            title=getString(R.string.download_activity_title);
        setTitle(title);
        if (mUrl.equals(mTypes[0])) {
            mUrls.addAll(Arrays.asList(mDownloadDir.list()));
            Collections.sort(mUrls, new LocaleComparator());
            mAdapter.addAll(mUrls);
            getListView().setOnItemLongClickListener(this);
            return;
        }
        //setTitle(intent.getStringExtra(TITLE));
        HttpUtil.get(mUrl, this);
    }

    @Override
    public void onDone(HttpUtil.HttpResult result) {
        if (result.code == 200) {
            String[] list = result.text.split("\n");
            if (list.length == 1 && TextUtils.isEmpty(list[0]))
                list[0] = getString(R.string.empty);
            Arrays.sort(list, new LocaleComparator());
            setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
        } else {
            setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{mUrl, "加载出错: " + result.text}));
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final String text = ((TextView) v).getText().toString();
        if (mUrl == null) {
            if (position == 0)
                startActivity(new Intent(this, DownloadActivity.class).putExtra(URL, mTypes[position]).putExtra(TITLE, text));
            else
                startActivity(new Intent(this, DownloadActivity.class).putExtra(URL, BASE + mTypes[position]).putExtra(TITLE, text));
            return;
        }
        if (!mUrls.isEmpty()) {
            final String name = mUrls.get(position);
            final String path = new File(mDownloadDir, name).getAbsolutePath();
            if (new File(path).exists()) {
                open1(path);
            }
            return;
        }

        if (text.endsWith("rime")) {
            final String path = new File(mDownloadDir, text).getAbsolutePath();
            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(String.format("正在下载 %s...", text));
            mProgressDialog.show();
            HttpUtil.download(mUrl + text, path + ".tmp", new HttpUtil.HttpCallback() {
                @Override
                public void onDone(HttpUtil.HttpResult result) {
                    mProgressDialog.dismiss();
                    new File(path + ".tmp").renameTo(new File(path));
                    open1(path);
                }
            }).setUpdateListerer(new HttpUtil.UpdateListener() {
                @Override
                public void onUpdate(String[] values) {
                    mProgressDialog.setMessage(String.format("%s \n%s %s", text, getString(R.string.waiting), values[0]));
                }
            });
        }
    }

    private void open1(final String path) {
        new AlertDialog.Builder(this)
                .setTitle(new File(path).getName())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FileUtil.unZip(path, sharedDataDir);
                            Trime.getService().resetEffect();
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
                            logs(name, path);
                            new AlertDialog.Builder(DownloadActivity.this)
                                    .setMessage(R.string.done)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .create()
                                    .show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    private void logs(String name, String path) {
        LuaApplication app = LuaApplication.getInstance();
        File f = new File(app.getLuaExtPath("install.json"));
        JSONArray arr = JsonUtil.loadArray(f);
        ArrayList<JSONObject> log = new ArrayList<>();
        int len = arr.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                if (name.equals(o.optString("name"))) {
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
            json.put("name", name);
            json.put("time", System.currentTimeMillis());
            json.put("list", new JSONArray(list));
            log.add(0, json);
            JsonUtil.save(f, new JSONArray(log));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete) + " " + mUrls.get(position))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new File(mDownloadDir, mUrls.get(position)).delete();
                        mUrls.clear();
                        mAdapter.clear();
                        String[] fs = mDownloadDir.list();
                        if (fs == null)
                            fs = new String[0];
                        mUrls.addAll(Arrays.asList(fs));
                        Collections.sort(mUrls, new LocaleComparator());
                        mAdapter.addAll(mUrls);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
        return true;
    }
}
