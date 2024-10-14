package com.osfans.trime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.osfans.trime.pro.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nirenr on 2019/1/30.
 */

public class ClipboardMgr extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private File mKey;
    private ArrayListAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mDir = Function.getPref(this).getString("user_data_dir", getString(R.string.default_user_data_dir));
        mKey = new File(mDir, "clipboard.json");
        ArrayList<String> mList = JsonUtil.load(mKey);
        ListView mListView = new ListView(this);
        mAdapter = new ArrayListAdapter<String>(this, mList);
        mListView.setAdapter(mAdapter);
        setContentView(mListView);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        JsonUtil.save(mKey, mAdapter.getData());
        Trime trime = Trime.getService();
        if (trime != null)
            trime.loadClipboard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "新建").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 0:
                addDialog(-1);
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDialog(int position) {
        new EditDialog(position).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        addDialog(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        delDialog(position);
        return true;
    }

    private void delDialog(final int idx) {
        final String app = mAdapter.getData().get(idx);
        new AlertDialog.Builder(this).setTitle("删除 " + app)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.remove(idx);
                        Toast.makeText(ClipboardMgr.this, "已删除", Toast.LENGTH_SHORT).show();
                    }
                })
                .create().show();

    }

    private class EditDialog implements DialogInterface.OnClickListener {
        private final int mIdx;
        private final EditText mEdit;
        private AlertDialog dlg;

        public EditDialog(int idx) {
            mIdx = idx;
            mEdit = new EditText(ClipboardMgr.this);
            if (mIdx != -1) {
                mEdit.setText(mAdapter.getItem(idx));
                mEdit.setSelection(mEdit.length());
            }
        }

        public void show() {
            dlg = new AlertDialog.Builder(ClipboardMgr.this)
                    .setTitle(R.string.edit_title)
                    .setView(mEdit)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, this)
                    .setCancelable(false)
                    .create();

            mEdit.setFocusable(true);
            mEdit.requestFocus();
            Window window = dlg.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            dlg.show();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String text = mEdit.getText().toString();
            if (mIdx != -1)
                mAdapter.remove(mIdx);
            if (text.isEmpty())
                return;
            mAdapter.insert(0, text);
        }
    }

}

