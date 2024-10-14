package com.osfans.trime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nirenr.LocaleComparator;
import com.osfans.trime.pro.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by nirenr on 2019/1/22.
 */

public class CommandListSetting extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private String mKey = "custom_candidate";
    private ArrayList<String> mData;
    private ArrayListAdapter<String> mAdapter;
    private String[] mDef = new String[0];
    private int mPosition = -1;
    private HashMap<String, String> keyMap;
    private String[] mLabels;
    private HashMap<String, String> labelMap;
    private boolean no_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKey = getIntent().getAction();
        mData = new ArrayList<>();
        String text = Function.getPref(this).getString(mKey, "");
        String[] list = text.split("\\|");
        if (list.length == 0 || TextUtils.isEmpty(text)) {
            list = mDef;
            if (mKey.equals("custom_bottom_key"))
                list = new String[]{"Menu", "_Keyboard_phrase", "_Keyboard_edit", "VOICE_ASSIST"};
            if (mKey.equals("custom_top_key"))
                list = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        }

        if (getList().length == 0) {
            TextView msg = new TextView(this);
            msg.setText(R.string.no_select);
            setContentView(msg);
            no_select = true;
            return;
        }
        for (int i = 0; i < list.length; i++) {
            list[i] = labelMapGet(list[i]);
        }
        ListView mListView = new ListView(this);
        mAdapter = new ArrayListAdapter<>(this, mData);
        mAdapter.addAll(list);
        if (list == mDef)
            mAdapter.remove("取消");
        mListView.setAdapter(mAdapter);
        setContentView(mListView);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    private String labelMapGet(String s) {
        String r = labelMap.get(s);
        if (r != null)
            return r;
        return s;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (no_select)
            return;
        String text = format(mAdapter.getData());
        Function.getPref(this).edit().putString(mKey, text).apply();
        Rime.resetSchema();
        Trime trime = Trime.getService();
        if (trime != null) trime.invalidate();

    }

    private void save() {
        String text = format(mAdapter.getData());
        Function.getPref(this).edit().putString(mKey, text).apply();
    }

    private String format(ArrayList<String> data) {
        StringBuilder buf = new StringBuilder();
        for (String s : data) {
            buf.append(keyMap.get(s)).append("|");
        }
        return buf.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (no_select)
            return true;
        menu.add(0, 0, 0, R.string.add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 0:
                addDialog(-1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDialog(int position) {
        mPosition = position;
        final String[] list = getList();
        new AlertDialog.Builder(this)
                .setTitle("选择功能")
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = list[which];
                        onAdd(list[which]);
                    }
                })
                .setPositiveButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    private String[] getList() {
        if (mLabels != null)
            return mLabels;
        Map<String, Map> keys = Key.getPresetKeys();
        if (keys == null)
            return new String[0];

        Set<String> sets = keys.keySet();
        keyMap = new HashMap<>();
        labelMap = new HashMap<>();
        ArrayList<String> labels = new ArrayList<>();
        for (String s : sets) {
            try {
                Map k = keys.get(s);
                if (!k.containsKey("label") || !k.containsKey("send"))
                    continue;
                String l = k.get("label").toString() + ":" + s;
                labels.add(l);
                keyMap.put(l, s);
                labelMap.put(s, l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(labels);
        for (int i = 0; i <= 9; i++) {
            String s = String.valueOf(i);
            String l = s;
            labels.add(l);
            keyMap.put(l, s);
            labelMap.put(s, l);
        }
        String[] ret = new String[labels.size()];
        mLabels = labels.toArray(ret);
        Arrays.sort(mLabels, new LocaleComparator());
        return mLabels;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        addDialog(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(this)
                .setItems(new String[]{
                        "上移",
                        "下移",
                        "删除",
                        "取消"
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = mAdapter.getItem(position);
                        switch (which) {
                            case 0:
                                if (position == 0)
                                    return;
                                mAdapter.remove(position);
                                mAdapter.insert(position - 1, text);
                                save();
                                break;
                            case 1:
                                if (position == mAdapter.getCount() - 1)
                                    return;
                                mAdapter.remove(position);
                                mAdapter.insert(position + 1, text);
                                save();
                                break;
                            case 2:
                                delDialog(position);
                                break;
                        }
                    }
                })
                .create()
                .show();
        return true;
    }

    private void delDialog(final int idx) {
        final String app = mAdapter.getData().get(idx);
        new AlertDialog.Builder(this).setTitle("删除 " + app)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(idx);
                        Toast.makeText(CommandListSetting.this, "已删除", Toast.LENGTH_SHORT).show();
                        save();
                    }
                })
                .create().show();

    }

    public void onAdd(String text) {
        if (mPosition == -1) {
            add(text);
        } else {
            remove(mPosition);
            insert(mPosition, text);
        }
        save();
    }

    private void add(String text) {
        mAdapter.add(text);
    }

    private void insert(int idx, String text) {
        mAdapter.insert(idx, text);
    }

    private void remove(int idx) {
        mAdapter.remove(idx);
    }

    private static ArrayList<String> mAppList = new ArrayList<>();


}

