package com.osfans.trime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.FileProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androlua.LuaActivity;
import com.androlua.LuaApplication;
import com.androlua.LuaBitmapDrawable;
import com.androlua.LuaEditor;
import com.androlua.LuaUtil;
import com.myopicmobile.textwarrior.android.OnSelectionChangedListener;
import com.myopicmobile.textwarrior.common.AutoIndent;
import com.myopicmobile.textwarrior.common.LanguageLua;
import com.myopicmobile.textwarrior.common.LanguageNonProg;
import com.myopicmobile.textwarrior.common.Lexer;
import com.nirenr.LocaleComparator;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static android.os.Environment.getExternalStorageDirectory;

public class OpenActivity extends Activity {
    private static String mHelp;
    private LuaEditor edit;
    private String path;
    private ArrayList<String> permissions;
    private File mDir;
    private String mName;
    private DisplayMetrics dm;
    private ArrayList<JsonUtil.HistoryData> history = new ArrayList<>();
    private String mHistoryPath;
    private TextView mTitle;
    private File mRootDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dm = getResources().getDisplayMetrics();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initDir();
        mHistoryPath = new File(mDir, "history.json").getAbsolutePath();
        history = JsonUtil.loadHistoryData(mHistoryPath);

        edit = new LuaEditor(this);
        edit.setNonPrintingCharVisibility(true);
        loadConfig();

        LinearLayout hList = new LinearLayout(this);
        String[] btn = {"(", ")", "[", "]", "{", "}", "\"", "=", ":", ".", ",", "_", "+", "-", "*", "/", "\\", "%", "#", "^", "$", "?", "&", "|", "<", ">", "~", ";", "'"};
        for (String s : btn) {
            final TextView view = new TextView(this);
            view.setText(s);
            view.setGravity(Gravity.CENTER);
            view.setPadding(0, 0, 0, 0);
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            view.setLayoutParams(new ViewGroup.LayoutParams(dp(36), dp(32)));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit.paste(view.getText().toString());
                    final ColorStateList tc = view.getTextColors();
                    view.setBackgroundColor(tc.getDefaultColor());
                    view.setTextColor(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setBackgroundColor(0);
                            view.setTextColor(tc);
                        }
                    }, 100);
                }
            });
            hList.addView(view);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        //edit.setNonPrintingCharVisibility(true);
        //标题栏
        LinearLayout mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        mTitle = new TextView(this);
        //mTitle.setGravity(Gravity.CENTER);
        mTitle.setEllipsize(TextUtils.TruncateAt.START);
        mTitle.setSingleLine(true);
        //mLayout.addView();
        mLayout.addView(mTitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //菜单栏
        LinearLayout mList = new LinearLayout(this);
        mList.addView(createButton("撤消", 1));
        mList.addView(createButton("重做", 2));
        View sBtn = createButton("搜索", 7);
        mList.addView(sBtn);
        mList.addView(createButton("替换", 13));
        mList.addView(createButton("最近", 12));
        mList.addView(createButton("打开", 4));
        mList.addView(createButton("保存", 6));
        mList.addView(createButton("格式化", 3));
        mList.addView(createButton("新建文件", 5));
        //mList.addView(createButton("新建工程", 11));
        HorizontalScrollView mScroll = new HorizontalScrollView(this);
        mScroll.addView(mList);
        mLayout.addView(mScroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(mLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        final LinearLayout sList = new LinearLayout(this);
        final EditText sEdit = new EditText(this) {
            @Override
            protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                super.onTextChanged(text, start, lengthBefore, lengthAfter);
                edit.findNext(text.toString());
            }
        };
        sList.addView(sEdit, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        sList.addView(createButton("▼", 28));
        sList.addView(createButton("▲", 29));
        sList.setVisibility(View.GONE);
        layout.addView(sList, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sList.getVisibility() == View.GONE) {
                    sList.setVisibility(View.VISIBLE);
                    sEdit.setText(edit.getSelectedText());
                } else {
                    sList.setVisibility(View.GONE);
                }
            }
        });
        layout.addView(edit, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        //底部工具栏
        LinearLayout hLayout = new LinearLayout(this);
        //hLayout.addView(createButton("运行", 0));

        //剪切板栏
        final LinearLayout cList = new LinearLayout(this);
        cList.addView(createButton("全选", 20));
        cList.addView(createButton("剪切", 21));
        cList.addView(createButton("复制", 22));
        cList.addView(createButton("粘贴", 23));
        cList.addView(createButton("◀", 24));
        cList.addView(createButton("▶", 26));
        cList.addView(createButton("▲", 25));
        cList.addView(createButton("▼", 27));
        cList.setVisibility(View.GONE);
        HorizontalScrollView cScroll = new HorizontalScrollView(this);
        cScroll.addView(cList);
        hLayout.addView(cScroll);
        edit.setOnSelectionChangedListener(new OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(boolean active, int selStart, int selEnd) {
                if (active)
                    cList.setVisibility(View.VISIBLE);
                else
                    cList.setVisibility(View.GONE);
            }
        });

        //符号栏
        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        hScroll.addView(hList);
        hLayout.addView(hScroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(hLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // layout.addView(hScroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(layout);
        Uri data = getIntent().getData();
        if (data != null) {
            String p = data.getPath();
            Log.i("rime", "onCreate: " + p);
            readFile(p);
            return;
        }

        openFile(mDir);
        //readFile();
    }

    private View createButton(String s, int id) {
        final TextView view = new TextView(this);
        view.setId(id);
        view.setText(s);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(8), 0, dp(8), 0);
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(32)));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(v);
                final ColorStateList tc = view.getTextColors();
                view.setBackgroundColor(tc.getDefaultColor());
                //view.setTextColor(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(0);
                        view.setTextColor(tc);
                    }
                }, 100);
            }
        });
        return view;
    }

    private int dp(float n) {
        // TODO: Implement this method
        return (int) TypedValue.applyDimension(1, n, dm);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //save();
    }

    private void save() {
        try {
            if (edit.isEdited()) {
                edit.save();
            }
            JsonUtil.HistoryData c = null;
            for (JsonUtil.HistoryData data : history) {
                if (data.getPath().equals(path)) {
                    c = data;
                    break;
                }
            }
            if (c != null)
                history.remove(c);
            history.add(0, new JsonUtil.HistoryData(path, edit.getCaretPosition()));
            JsonUtil.saveHistoryData(mHistoryPath, history);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            File path = new File(mDir, "config.json");
            if (!path.exists())
                return;
            InputStream stream = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder(8196);
            String input;
            while ((input = reader.readLine()) != null) {
                stringBuilder.append(input);
            }
            stream.close();
            JSONObject colors = new JSONObject(stringBuilder.toString());
            if (colors.has("dark")) {
                if (colors.optBoolean("dark")) {
                    setTheme(android.R.style.Theme_DeviceDefault_NoActionBar);
                    edit.setDark(true);
                } else {
                    setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
                    edit.setDark(false);
                }
            }

            if (colors.has("wrap"))
                edit.setWordWrap(colors.optBoolean("wrap"));

            if (colors.has("text"))
                edit.setTextColor(Color.parseColor(colors.optString("text")));
            if (colors.has("highlight"))
                edit.setTextHighlightColor(Color.parseColor(colors.optString("Highlight")));
            if (colors.has("background")) {
                String bg = colors.optString("background");
                edit.setBackgroundColor(Color.parseColor(bg));
            }

            if (colors.has("keyword"))
                edit.setKeywordColor(Color.parseColor(colors.optString("keyword")));
            if (colors.has("package"))
                edit.setBasewordColor(Color.parseColor(colors.optString("package")));
            if (colors.has("number"))
                edit.setUserwordColor(Color.parseColor(colors.optString("number")));
            if (colors.has("comment"))
                edit.setCommentColor(Color.parseColor(colors.optString("comment")));
            if (colors.has("string"))
                edit.setStringColor(Color.parseColor(colors.optString("string")));
            if (colors.has("panel")) {
                colors = colors.getJSONObject("panel");
                if (colors.has("background")) {
                    String bg = colors.optString("background");
                    edit.setPanelBackgroundColor(Color.parseColor(bg));
                }
                //    edit.setPanelBackgroundColor(Color.parseColor(colors.optString("background")));
                if (colors.has("text"))
                    edit.setPanelTextColor(Color.parseColor(colors.optString("text")));
            } /*else {
                edit.setPanelTextColor(edit.getColorScheme().getColor(ColorScheme.Colorable.FOREGROUND));
                edit.setPanelBackgroundColor(edit.getColorScheme().getColor(ColorScheme.Colorable.BACKGROUND));
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initDir() {
        mDir = new File(LuaApplication.getInstance().getLuaExtDir());
        mRootDir = mDir;
        if (!mDir.exists())
            mDir.mkdirs();
    }

    private void checkPermission(String permission) {
        if (checkCallingOrSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(permission);
        }
    }


    private void readFile() {
        try {
            path = new File(mDir, "main.lua").getAbsolutePath();
            edit.open(path);
            mName = "main.lua";
            //getActionBar().setSubtitle("main.lua");
            mTitle.setText(path);
        } catch (Exception e) {
            Toast.makeText(this, "打开出错：" + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean readHistory(int i) {
        try {
            path = history.get(i).getPath();
            edit.open(path);
            mName = new File(path).getName();
            mDir = new File(path).getParentFile();
            edit.setSelection(history.get(i).getIdx());
            //getActionBar().setSubtitle(mName);
            mTitle.setText(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void readFile(String p) {
        try {
            if (p.endsWith(".lua"))
                Lexer.setLanguage(LanguageLua.getInstance());
            else
                Lexer.setLanguage(LanguageNonProg.getInstance());
            File f = new File(p);
            if (f.exists()) {
                mDir = f.getParentFile();
                p = f.getName();
            }
            path = new File(mDir, p).getAbsolutePath();
            edit.open(path);
            mName = p;
            mTitle.setText(path);
            for (JsonUtil.HistoryData data : history) {
                if (data.getPath() == null)
                    continue;
                if (data.getPath().equals(path))
                    edit.setSelection(data.getIdx());
            }
            //getActionBar().setSubtitle(p);
            save();
        } catch (Exception e) {
            Toast.makeText(this, "打开出错：" + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void onOptionsItemSelected(View item) {
        switch (item.getId()) {
            case 0:
                try {
                    edit.save();
                    startActivity(new Intent(this, LuaActivity.class).setData(Uri.fromFile(mDir)));
                } catch (Exception e) {
                    Toast.makeText(this, "保存出错：" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case 1:
                edit.undo();
                break;
            case 2:
                edit.redo();
                break;
            case 3:
                edit.format();
                break;

            case 4:
                save();
                openFile(mDir);
                break;
            case 5:
                save();
                new EditDialog(this, "输入文件名", "", new EditDialog.EditDialogCallback() {
                    @Override
                    public void onCallback(String text) {
                        if (TextUtils.isEmpty(text))
                            return;
                        if (!text.contains("."))
                            text = text + ".lua";
                        readFile(text);
                    }
                }).show();
                break;
            case 6:
                try {
                    edit.save();
                    Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "保存出错：" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case 7:
                edit.search();
                break;
            case 11:
                save();
                new EditDialog(this, "输入工程名", "", new EditDialog.EditDialogCallback() {
                    @Override
                    public void onCallback(String text) {
                        if (TextUtils.isEmpty(text))
                            return;
                        File d = new File(mDir, text);
                        if (!d.exists() && !d.mkdirs()) {
                            Toast.makeText(OpenActivity.this, "创建出错", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mDir = d;
                        readFile("main.lua");
                    }
                }).show();
                break;
            case 12:
                String[] hs = new String[history.size()];
                for (int i = 0; i < history.size(); i++) {
                    hs[i] = history.get(i).getPath();
                }
                new AlertDialog.Builder(this).setTitle("最近打开")
                        .setItems(hs, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                save();
                                if (readHistory(which)) {
                                    history.remove(which);
                                    save();
                                } else {
                                    history.remove(which);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                break;
            case 13:
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText from = new EditText(this);
                layout.addView(from, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                final EditText to = new EditText(this);
                layout.addView(to, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                new AlertDialog.Builder(this)
                        .setTitle("替换")
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                edit.replaceText(0, edit.getLength()-1, edit.getText().toString().replaceAll(from.getText().toString(), to.getText().toString()));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
                break;
            case 20:
                edit.selectAll();
                break;
            case 21:
                edit.cut();
                break;
            case 22:
                edit.copy();
                break;
            case 23:
                edit.paste();
                break;
            case 24:
                edit.moveCaretLeft();
                break;
            case 25:
                edit.moveCaretUp();
                break;
            case 26:
                edit.moveCaretRight();
                break;
            case 27:
                edit.moveCaretDown();
                break;
            case 28:
                edit.findNext();
                break;
            case 29:
                edit.findBack();
                break;

        }
    }

    private void openFile(final File dir) {
        File[] ls = dir.listFiles();
        ArrayList<String> ds = new ArrayList<>();
        ArrayList<String> fs = new ArrayList<>();
        for (File l : ls) {
            if (l.isDirectory())
                ds.add(l.getName());
            else
                fs.add(l.getName());
        }
        Collections.sort(ds, new LocaleComparator());
        Collections.sort(fs, new LocaleComparator());
        ds.add(0, "..");
        ds.addAll(fs);
        final String[] list = new String[ds.size()];
        ds.toArray(list);
        final AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("打开 " + dir.getAbsolutePath())
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("luaj", "onClick: " + list[which]);
                        if (which == 0) {
                            if (dir.getParentFile() != null)
                                openFile(dir.getParentFile());
                        } else if (new File(dir, list[which]).isDirectory()) {
                            openFile(new File(dir, list[which]));
                        } else {
                            OpenActivity.this.mDir = dir;
                            readFile(list[which]);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dlg.show();
        dlg.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                dlg.dismiss();
                new AlertDialog.Builder(OpenActivity.this)
                        .setItems(new String[]{
                                "删除",
                                "重命名",
                                "取消"
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        new File(dir, list[position]).delete();
                                        if (list[position].equals(mName))
                                            openFile(mDir);
                                        break;
                                    case 1:
                                        new EditDialog(OpenActivity.this, "输入文件名", list[position], new EditDialog.EditDialogCallback() {
                                            @Override
                                            public void onCallback(String text) {
                                                if (TextUtils.isEmpty(text))
                                                    return;
                                                new File(dir, list[position]).renameTo(new File(dir, text));
                                                if (list[position].equals(mName))
                                                    readFile(list[position]);
                                            }
                                        }).show();
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

                return true;
            }
        });
    }


    private String getType(File file) {
        int lastDot = file.getName().lastIndexOf(46);
        if (lastDot >= 0) {
            String extension = file.getName().substring(lastDot + 1);
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }
        return "application/octet-stream";
    }

    public Uri getUriForFile(File path) {
        return FileProvider.getUriForFile(this, getPackageName(), path);
    }

    public void installApk(String path) {
        Intent share = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setDataAndType(getUriForFile(file), getType(file));
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(share);
    }


}
