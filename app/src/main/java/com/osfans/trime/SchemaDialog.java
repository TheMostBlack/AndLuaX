/*
 * Copyright (C) 2015-present, osfans
 * waxaca@163.com https://github.com/osfans
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.osfans.trime;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.osfans.trime.pro.R;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 顯示輸入法方案列表
 */
class SchemaDialog extends AsyncTask {
    private boolean[] checkedSchemaItems;
    private String[] schemaItems;
    private List<Map<String, String>> schemas;
    private String[] schemaNames;
    private Context mContext;
    private IBinder mToken;
    private ProgressDialog mProgressDialog;
    private static String TAG = SchemaDialog.class.getSimpleName();

    private class SortByName implements Comparator<Map<String, String>> {
        @Override
        public int compare(Map<String, String> m1, Map<String, String> m2) {
            String s1 = m1.get("schema_id");
            String s2 = m2.get("schema_id");
            return s1.compareTo(s2);
        }
    }

    private void selectSchema() {
        List<String> checkedIds = new ArrayList<String>();
        int i = 0;
        for (boolean b : checkedSchemaItems) {
            if (b) checkedIds.add(schemaItems[i]);
            i++;
        }
        int n = checkedIds.size();
        if (n > 0) {
            String[] schema_id_list = new String[n];
            checkedIds.toArray(schema_id_list);
            Rime.select_schemas(schema_id_list);
            Function.deploy();
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.schemas_progress));
        mProgressDialog.setCancelable(false);
        if (mToken != null) {
            Window window = mProgressDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.token = mToken;
            lp.type = Trime.getDialogType();
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        mProgressDialog.show();
    }

    public SchemaDialog(Context context) {
        this(context, null);
    }

    public SchemaDialog(Context context, IBinder token) {
        mContext = context;
        mToken = token;
        showProgressDialog();
        execute();
        Config.get(context);
    }

    private void initSchema() {
        schemas = Rime.get_available_schema_list();
        if (schemas == null || schemas.size() == 0) {
            //不能在線程中使用Toast
            //Toast.makeText(mContext, R.string.no_schemas, Toast.LENGTH_LONG).show();
            return;
        }
        Collections.sort(schemas, new SortByName());
        List<Map<String, String>> selected_schemas = Rime.get_selected_schema_list();
        List<String> selected_Ids = new ArrayList<String>();
        int n = schemas.size();
        schemaNames = new String[n];
        String schema_id;
        checkedSchemaItems = new boolean[n];
        schemaItems = new String[n];
        int i = 0;
        if (selected_schemas.size() > 0) {
            for (Map<String, String> m : selected_schemas) {
                selected_Ids.add(m.get("schema_id"));
            }
        }
        for (Map<String, String> m : schemas) {
            schemaNames[i] = m.get("name");
            schema_id = m.get("schema_id");
            schemaItems[i] = schema_id;
            checkedSchemaItems[i] = selected_Ids.contains(schema_id);
            i++;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.pref_schemas)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, null);
        if (schemas == null || schemas.size() == 0) {
            builder.setMessage(R.string.no_schemas);
        } else {
            builder.setMultiChoiceItems(
                    schemaNames,
                    checkedSchemaItems,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface di, int id, boolean isChecked) {
                            checkedSchemaItems[id] = isChecked;
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(
                    android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface di, int id) {
                            deploy();
                        }
                    });
        }
        AlertDialog mDialog = builder.create();
        if (mToken != null) {
            Window window = mDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.token = mToken;
            /* WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            DisplayMetrics outMetrics2 = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            wm.getDefaultDisplay().getRealMetrics(outMetrics2);
            int mHeight = outMetrics2.heightPixels-outMetrics.heightPixels;
            lp.gravity = Gravity.BOTTOM;
            lp.y=mHeight;
            lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);*/

            lp.type = Trime.getDialogType();
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        mDialog.show();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Object... o) {
        initSchema();
        return "ok";
    }

    protected void onProgressUpdate(Object o) {
    }

    @Override
    protected void onPostExecute(Object o) {
        mProgressDialog.dismiss();
        showDialog();
    }

    @SuppressLint("StaticFieldLeak")
    private void deploy() {
        mProgressDialog.setMessage(mContext.getString(R.string.deploy_progress));
        mProgressDialog.show();
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    Runtime.getRuntime().exec("logcat logcat -c");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    selectSchema();
                } catch (Exception ex) {
                    Log.e(TAG, "Select Schema" + ex);
                    return ex.getMessage();
                }
                return execCmd("logcat -d -v long native:*");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mProgressDialog.dismiss();
                if (mContext instanceof Pref)
                    ((Pref) mContext).setClear();
                AlertDialog mDialog;
                if (TextUtils.isEmpty(s)) {
                    mDialog = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.done)
                            .setPositiveButton(mContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mContext instanceof Service)
                                        System.exit(0);
                                }
                            }).create();
                } else {
                    mDialog = new AlertDialog.Builder(mContext)
                            .setTitle("提示")
                            .setMessage(s)
                            .setPositiveButton(mContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mContext instanceof Service)
                                        System.exit(0);
                                }
                            }).create();
                }
                if (mToken != null) {
                    Window window = mDialog.getWindow();
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.token = mToken;
                    lp.type = Trime.getDialogType();
                    window.setAttributes(lp);
                    window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                }
                mDialog.show();
            }
        }.execute();
    }

    public static String execCmd(String cmd) {
        StringBuilder result = new StringBuilder();
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec(cmd);
            dis = new DataInputStream(p.getInputStream());

            String line = null;
            while ((line = dis.readLine()) != null) {
                if (line.contains("E/")) {
                    int idx = line.indexOf("]");
                    if (idx > 1)
                        line = line.substring(idx + 1);
                    result.append(line).append("\n");
                }
            }
            //p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString().trim();
    }

}
