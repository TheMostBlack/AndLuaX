package com.osfans.trime;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayListAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androlua.LuaActivity;
import com.androlua.LuaApplication;
import com.androlua.LuaUtil;
import com.osfans.trime.pro.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/09/07 0007.
 */

public class ToolActivity extends ListActivity implements AdapterView.OnItemLongClickListener {

    private String mDir;
    private String[] mList;
    private ArrayList<String> mHistory;
    private ArrayListAdapter<String> mAdapter;
    private EditText mEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);

        LuaApplication app = LuaApplication.getInstance();
        mDir = app.getLuaExtDir("tools") + File.separator;
        /*Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String path = data.getPath();
            if (path != null) {
                if (new File(path).exists()) {
                    try {
                        String name = new File(path).getName();
                        int i = name.lastIndexOf(".");
                        if (i > 0) {
                            name = name.substring(0, i);
                        }
                        i = name.indexOf("_");
                        if (i > 0) {
                            name = name.substring(0, i);
                        }
                        LuaUtil.unZip(path, mDir + name);
                        Toast.makeText(this, R.string.message_load_done, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
        String[] temp = new File(mDir).list();
        if (temp == null)
            temp = new String[0];
        ArrayList<String> list = new ArrayList<>();
        if (mHistory != null) {
            for (String name : mHistory) {
                if (new File(new File(mDir, name), "main.lua").exists())
                    list.add(name);
            }
        } else {
            mHistory = new ArrayList<>();
        }

        for (String name : temp) {
            if (!list.contains(name) && new File(new File(mDir, name), "main.lua").exists())
                list.add(name);
        }
        mList = new String[list.size()];
        list.toArray(mList);
        //mList = new File(mDir).list();
        mAdapter = new ArrayListAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        setListAdapter(mAdapter);
        getListView().setOnItemLongClickListener(this);
    }

    private void refresh() {
        String[] temp = new File(mDir).list();
        if (temp == null)
            temp = new String[0];
        ArrayList<String> list = new ArrayList<>();
        //mHistory = ClipboardUtil.loadToolHistory();
        if (mHistory != null) {
            for (String name : mHistory) {
                if (new File(new File(mDir, name), "main.lua").exists())
                    list.add(name);
            }
        } else {
            mHistory = new ArrayList<>();
        }

        for (String name : temp) {
            if (!list.contains(name) && new File(new File(mDir, name), "main.lua").exists())
                list.add(name);
        }
        mList = new String[list.size()];
        list.toArray(mList);
        mAdapter.clear();
        mAdapter.addAll(mList);
        mEditText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mEditText = new EditText(this) {
            @Override
            protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                super.onTextChanged(text, start, lengthBefore, lengthAfter);
                mAdapter.filter(text);
            }
        };
        mEditText.setHint(R.string.kayword);
        menu.add("").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS).setActionView(mEditText);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView view = (TextView) v;
        String text = view.getText().toString();
        if (mHistory.contains(text))
            mHistory.remove(text);
        mHistory.add(0, text);
        //ClipboardUtil.saveToolHistory(mHistory);
        Intent intent = new Intent(this, LuaActivity.class);
        intent.setData(Uri.parse(mDir + text + "/main.lua"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        TextView view = (TextView) v;
        final String text = view.getText().toString();
        new AlertDialog.Builder(this)
                .setItems(new String[]{
                        "添加到桌面",
                        "删除",
                        "取消"
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                addShortcut(text);
                                break;
                            case 1:
                                new AlertDialog.Builder(ToolActivity.this)
                                        .setTitle("确定删除 " + text)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                LuaUtil.rmDir(new File(mDir, text));
                                                refresh();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .create()
                                        .show();
                        }
                    }
                })
                .create().show();
        return true;
    }

    private void addShortcut(String text) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setClassName(getPackageName(), LuaActivity.class.getName());
        intent.setData(Uri.parse(mDir + text + "/main.lua"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager scm = (ShortcutManager) getSystemService(SHORTCUT_SERVICE);
            ShortcutInfo si = new ShortcutInfo.Builder(this, text)
                    .setIcon(Icon.createWithResource(this, R.drawable.icon))
                    .setShortLabel(text)
                    .setIntent(intent)
                    .build();
            try {
                scm.requestPinShortcut(si, null);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "添加快捷方式出错", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent addShortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            Intent.ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(this,
                    R.drawable.icon);
            addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, text);
            addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            addShortcut.putExtra("duplicate", 0);
            addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            sendBroadcast(addShortcut);
            Toast.makeText(this, "已添加快捷方式", Toast.LENGTH_SHORT).show();
        }

    }

}
