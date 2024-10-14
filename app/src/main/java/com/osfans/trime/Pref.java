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

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.speech.RecognitionService;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.androlua.LuaUtil;
import com.nirenr.LocaleComparator;
import com.osfans.trime.pro.R;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;


/**
 * 配置輸入法
 */

public class Pref extends Activity {

    private static String TAG = Pref.class.getSimpleName();
    private ArrayList<String> permissions;
    private boolean is_show_window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = Function.getPref(this);
        boolean is_dark = prefs.getBoolean("pref_ui", false);
        is_show_window = prefs.getBoolean("show_window", true);
        /*if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            setTheme(is_dark ? android.R.style.Theme_Material : android.R.style.Theme_Material_Light);
        } else {
            setTheme(is_dark ? android.R.style.Theme_Holo : android.R.style.Theme_Holo_Light);
        }*/
        super.onCreate(savedInstanceState);
        PackageManager pm = getPackageManager();
        try {
            String ver = pm.getPackageInfo(getPackageName(), 0).versionName;
            ActionBar bar = getActionBar();
            if (bar != null)
                bar.setSubtitle(ver);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //if (VERSION.SDK_INT >= VERSION_CODES.M) requestPermission();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new TrimePreferenceFragment()).commit();
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                permissions = new ArrayList<String>();
                String[] ps2 = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
                for (String p : ps2) {
                    try {
                        checkPermission(p);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!permissions.isEmpty()) {
                    String[] ps = new String[permissions.size()];
                    permissions.toArray(ps);
                    requestPermissions(ps,
                            0);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (is_show_window && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                //startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                startActivity(intent);
                Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_SHORT).show();
            }
        }
        //Config.get(this);

    }

    private void checkPermission(String permission) {
        if (checkCallingOrSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(permission);
        }
    }

    @TargetApi(VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Config.get(this);
        if (is_show_window && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            //startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            startActivity(intent);
            Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(VERSION_CODES.M)
    private void requestPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    0);
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            //startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            startActivity(intent);
        }
    }

    private boolean isClear;

    public void setClear() {
        isClear = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isClear)
            System.exit(0); //清理內存
    }

    public static class TrimePreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private ProgressDialog mProgressDialog;
        private Preference mKeySoundVolumePref, mKeyVibrateDurationPref;
        private boolean isClear;

        private String getCommit(String version) {
            String commit;
            if (version.contains("-g")) {
                commit = version.replaceAll("^(.*-g)([0-9a-f]+)(.*)$", "$2");
            } else {
                commit = version.replaceAll("^([^-]*)(-.*)$", "$1");
            }
            return commit;
        }

        private void setVersion(Preference pref, String version) {
            String commit = getCommit(version);
            pref.setSummary(version);
            Intent intent = pref.getIntent();
            intent.setData(Uri.withAppendedPath(intent.getData(), "commits/" + commit));
            pref.setIntent(intent);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final SharedPreferences prefs = Function.getPref(getActivity());
            Activity activity = getActivity();
            if (activity == null)
                return;
            addPreferencesFromResource(R.xml.prefs);
            Preference pref;
            String version;

            pref = findPreference("pref_changelog");
            version = Rime.get_trime_version();
            setVersion(pref, version);

            pref = findPreference("pref_librime_ver");
            version = Rime.get_librime_version();
            setVersion(pref, version);

            pref = findPreference("pref_opencc_ver");
            version = Rime.get_opencc_version();
            setVersion(pref, version);

            pref = findPreference("pref_enable");
            if (isEnabled())
                getPreferenceScreen().removePreference(pref);
            if (isSelect())
                getPreferenceScreen().removePreference(findPreference("pref_select"));

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
            mKeySoundVolumePref = findPreference("key_sound_volume");
            mKeyVibrateDurationPref = findPreference("key_vibrate_duration");
            boolean isPay = Function.isAppAvailable(getActivity(), "com.eg.android.AlipayGphone");
            if (!isPay) {
                findPreference("pref_donate_nirenr").setEnabled(false);
                findPreference("pref_donate_nirenr").setSummary("未安装支付宝");
            }
            ArrayList<String> list = new ArrayList<>();
            list.add(getString(R.string.value_default));
            try {
                reloadInstalledRecognitionService(getActivity().getPackageManager(), list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] mEngines = new String[list.size()];
            list.toArray(mEngines);
            ListPreference rs = (ListPreference) findPreference("recognition_service");
            rs.setEntryValues(mEngines);
            rs.setEntries(mEngines);

            rs = (ListPreference) findPreference("key_sound_package");
            String userDataDir = prefs.getString("user_data_dir", getString(R.string.default_user_data_dir));
            File dir = new File(userDataDir, "sounds");
            if (!dir.exists())
                dir.mkdirs();
            String[] fs = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isDirectory();
                }
            });
            Arrays.sort(fs, new LocaleComparator());
            String[] ls = new String[fs.length + 1];
            ls[0] = "默认";
            String[] vs = new String[fs.length + 1];
            vs[0] = "none";
            for (int i = 0; i < fs.length; i++) {
                ls[i + 1] = fs[i];
                vs[i + 1] = fs[i];
            }
            rs.setEntryValues(vs);
            rs.setEntries(ls);

            rs = (ListPreference) findPreference("background_package");
            dir = new File(userDataDir, "backgrounds");
            if (!dir.exists())
                dir.mkdirs();
            fs = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isDirectory();
                }
            });
            Arrays.sort(fs, new LocaleComparator());
            ls = new String[fs.length + 1];
            ls[0] = "默认";
            vs = new String[fs.length + 1];
            vs[0] = "none";
            for (int i = 0; i < fs.length; i++) {
                ls[i + 1] = fs[i];
                vs[i + 1] = fs[i];
            }
            rs.setEntryValues(vs);
            rs.setEntries(ls);

            final PreferenceScreen ps = (PreferenceScreen) findPreference("custom_colors");
            final Config config = Config.get();
            String[] color_keys = new String[]{
                    "background_color",
                    "keyboard_back_color",
                    "candidate_back_color",
                    "composition_back_color",


                    null,//按键
                    "hilited_key_text_color",
                    "key_text_color",

                    "hilited_key_back_color",
                    "key_back_color",

                    "preview_text_color",
                    "preview_back_color",

                    "hilited_key_border_color",
                    "key_border_color",

                    "hilited_key_symbol_color",
                    "key_symbol_color",

                    "shadow_color",

                    null,//功能键
                    "hilited_off_key_text_color",
                    "off_key_text_color",

                    "hilited_off_key_back_color",
                    "off_key_back_color",

                    null,//shift键
                    "hilited_on_key_text_color",
                    "on_key_text_color",

                    "hilited_on_key_back_color",
                    "on_key_back_color",

                    null,//候选区
                    "candidate_separator_color",

                    "hilited_comment_text_color",
                    "comment_text_color",

                    "hilited_candidate_back_color",

                    "hilited_candidate_text_color",
                    "candidate_text_color",

                    "hilited_candidate_border_color",
                    "candidate_border_color",

                    null,//悬浮窗口
                    "hilited_text_color",
                    "text_color",

                    "hilited_back_color",
                    "back_color",

                    "hilited_label_color",
                    "label_color",
                    //"text_back_color",

                    "border_color"
            };


            String[] name_keys = new String[]{
                    "主背景",
                    "键盘背景",
                    "候选背景",
                    "悬浮窗背景",


                    "按键",
                    "高亮文字",
                    "文字",

                    "高亮背景",
                    "背景",

                    "预览文字",
                    "预览背景",

                    "高亮边框",
                    "边框",

                    "高亮符号",
                    "符号",

                    "文字阴影",

                    "功能键",//
                    "高亮文字",
                    "文字",

                    "高亮背景",
                    "背景",

                    "shift键",
                    "高亮文字",
                    "文字",

                    "高亮背景",
                    "背景",

                    "候选区",
                    "分割线",

                    "高亮编码文字",
                    "编码文字",

                    "高亮候选背景",

                    "高亮候选文字",
                    "候选文字",

                    "高亮候选边框",
                    "候选栏边框",

                    "悬浮窗口",
                    "高亮文字",
                    "文字",

                    "高亮背景",
                    "背景",

                    "高亮标签",
                    "标签",

                    "边框"
            };

            for (int i = 0; i < color_keys.length; i++) {
                String key = color_keys[i];
                if (config != null)
                    createPreference(ps, key, config.getColor(key), prefs, name_keys[i]);
                else
                    createPreference(ps, key, null, prefs, name_keys[i]);
            }

            createPreference(ps, prefs);

           invalidatePref(prefs);
        }

        private void createPreference(final PreferenceScreen ps, final SharedPreferences prefs) {
            final String[] boards = new String[]{
                    "hilited_candidate_border",
                    "candidate_border",
                    "key_border",
                    "layout/border",
                    "hilited_candidate_round_corner",
                    "candidate_round_corner",
                    "key_round_corner",
                    "round_corner",
                    "layout/round_corner",

                    "vertical_gap",
                    "horizontal_gap",

                    "candidate_text_size",
                    "comment_text_size",
                    "text_size",
                    "key_text_size",
                    "key_long_text_size",
                    "label_text_size",
            };

            final PreferenceCategory pr = new PreferenceCategory(getActivity());
            pr.setTitle("尺寸");
            Preference pd = new Preference(getActivity());
            pd.setTitle("恢复默认");
            pd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Config config = Config.get();
                    SharedPreferences.Editor edit = prefs.edit();
                    for (String board : boards) {
                        edit.putInt(board, (int) config.getRawFloat(board));
                    }
                    edit.commit();
                    ps.removePreference(pr);
                    return true;
                }
            });
            ps.addPreference(pr);
            pr.addPreference(pd);
            Config config = Config.get();
            for (int i = 0; i < boards.length; i++) {
                String key = boards[i];
                if (config != null) {
                    if (key.endsWith("_gap"))
                        createPreference2(pr, key, (int) (config.getRawFloat(key) * 20), prefs);
                    else if (key.endsWith("_size") || key.endsWith("_corner") || key.endsWith("_gap"))
                        createPreference2(pr, key, (int) (config.getRawFloat(key)), prefs);
                    else
                        createPreference2(pr, key, (int) (config.getRawFloat(key) * 10), prefs);

                } else
                    createPreference2(pr, key, 0, prefs);
            }
        }


        private Preference createPreference(PreferenceScreen ps, final String key, Integer color, SharedPreferences prefs, String name) {
            if (key == null) {
                PreferenceCategory pr = new PreferenceCategory(getActivity());
                pr.setTitle(name);
                ps.addPreference(pr);
                return pr;
            }

            final Preference pref = new ColorPreference(getActivity());
            if (color == null)
                color = 0;
            pref.setDefaultValue(color);
            pref.setTitle(name + ": " + key);
            pref.setKey(key);
            final int def = prefs.getInt(key, color);
            pref.setSummary(String.format("#%08x", def));
            /*pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new ColorSelectDiaLog(getActivity(), key, def, pref).show();
                    return true;
                }
            });*/
            ps.addPreference(pref);
            return pref;
        }

        private Preference createPreference2(PreferenceCategory ps, final String key, Integer color, SharedPreferences prefs) {
            final SeekBarPreference pref = new SeekBarPreference(getActivity());
            pref.setWidgetLayoutResource(R.layout.preference_widget_seekbar);
            if (color == null)
                color = 0;
            pref.setDefaultValue(color);
            pref.setTitle(key);
            pref.setKey(key);
            pref.setMax(100);
            pref.setProgress(prefs.getInt(key, color));
            ps.addPreference(pref);
            return pref;
        }

        public static String reloadInstalledRecognitionService(PackageManager pm, List<String> results) {
            final Intent intent = new Intent(RecognitionService.SERVICE_INTERFACE);
            final List<ResolveInfo> resolveInfos =
                    pm.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY);

            String systemTtsEngine = null;

            for (ResolveInfo resolveInfo : resolveInfos) {
                final ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                final ApplicationInfo appInfo = serviceInfo.applicationInfo;
                final String packageName = serviceInfo.packageName;
                final CharSequence title = resolveInfo.loadLabel(pm);

                final boolean isSystemApp = ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);

                results.add(title + "/" + packageName + "/" + serviceInfo.name);

                if (isSystemApp) {
                    systemTtsEngine = packageName;
                }
            }

            return systemTtsEngine;
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            if (isClear)
                System.exit(0); //清理內存
            else {
                Trime trime = Trime.getService();
                if (trime != null) {
                    trime.invalidate();
                }
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            //getActivity().finish();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            Trime trime = Trime.getService();
            boolean value;
            switch (key) {
                case "key_sound":
                    if (trime != null) trime.resetEffect();
                    value = prefs.getBoolean(key, false);
                    break;
                case "key_vibrate":
                    if (trime != null) trime.resetEffect();
                    value = prefs.getBoolean(key, false);
                    break;
                case "key_sound_random":
                case "key_sound_volume":
                case "key_sound_package":
                    if (trime != null) {
                        trime.resetEffect();
                        trime.soundEffect();
                    }
                    break;
                case "key_vibrate_duration":
                    if (trime != null) {
                        trime.resetEffect();
                        trime.vibrateEffect();
                    }
                    break;
                case "speak_key":
                case "speak_commit":
                    if (trime != null) trime.resetEffect();
                    break;
                case "cloud_input":
                    if (trime != null) trime.resetCloudInput();
                    break;
                case "select_schema_id":
                case "keyboard_height":
                case "show_bottom_key":
                case "show_top_key":
                case "speak_key_label":
                case "key_alpha":
                case "keyboard_width":
                case "keyboard_small":
                case "background_package":
                case "custom_color":
                    if (trime != null) trime.invalidate();
                    break;
                case "key_swipe":
                case "key_longpress":
                case "longpress_timeout":
                case "repeat_interval":
                case "show_preview":
                case "show_hint":
                case "pref_colors_keyboard":
                    if (trime != null) trime.resetKeyboard();
                    break;
                case "show_window":
                    if (VERSION.SDK_INT >= VERSION_CODES.M) {
                        if (prefs.getBoolean(key, true) && !Settings.canDrawOverlays(getActivity())) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                            //startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                            startActivity(intent);
                            Toast.makeText(getActivity(), "需要悬浮窗权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                case "layout_cloud_max_entries":
                case "layout_max_entries":
                case "layout_line_max_length":
                case "candidate_scroll":
                case "composition_single":
                case "composition_end_top":
                    invalidatePref(prefs);
                    if (trime != null) {
                        trime.resetCandidate();
                    }
                    break;
                case "layout_position":
                case "layout_movable":
                case "inline_preedit":
                case "soft_cursor":
                case "layout_min_length":
                case "system_speak":
                case "phrase_sort":
                case "composition_width":
                case "async_key":
                    if (trime != null) trime.loadConfig();
                    break;
                case "pref_notification_icon": //通知欄圖標
                    value = prefs.getBoolean(key, false);
                    if (trime != null) {
                        if (value) trime.showStatusIcon(R.drawable.status);
                        else trime.hideStatusIcon();
                    }
                    break;

                case "show_switches": //候選欄顯示狀態
                    value = prefs.getBoolean(key, false);
                    Rime.setShowSwitches(value);
                    break;
                case "custom_candidate":
                case "filter_switches":
                    Rime.resetSchema();
                    break;
                case "keyboard_float":
                    isClear = true;
                    break;
                default:
                    if (trime != null) trime.invalidate();

            }
        }

        private void invalidatePref(SharedPreferences prefs) {
            if(prefs.getBoolean("composition_end_top",false)){
                findPreference("composition_single").setEnabled(false);
                findPreference("layout_min_length").setEnabled(false);
                findPreference("layout_line_max_length").setEnabled(false);
                return;
            } else {
                findPreference("composition_single").setEnabled(true);
                findPreference("layout_min_length").setEnabled(true);
                findPreference("layout_line_max_length").setEnabled(true);
            }
            if(prefs.getBoolean("composition_single",false)){
                findPreference("composition_end_top").setEnabled(false);
                findPreference("layout_line_max_length").setEnabled(false);
                return;
            } else {
                findPreference("composition_end_top").setEnabled(true);
                findPreference("layout_line_max_length").setEnabled(true);
            }
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            boolean b;
            String key = preference.getKey();
            if (TextUtils.isEmpty(key))
                return false;
            switch (key) {
                case "pref_schemas": //方案
                case "pref_deploy": //部署
                case "pref_colors": //配色
                case "pref_themes": //主題
                case "pref_sync": //同步
                    mProgressDialog.show();
                    if (Config.get(getActivity()) == null) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.no_select)
                                .setNegativeButton(
                                        android.R.string.cancel,
                                        null)
                                .create()
                                .show();
                        return true;
                    }
                    mProgressDialog.dismiss();
            }

            switch (key) {
                case "pref_enable": //啓用
                    if (!isEnabled())
                        startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                    return true;
                case "pref_select": //切換
                    ((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker();
                    return true;
                case "pref_themes": //主題
                    new ThemeDlg(getActivity());
                    return true;
                case "pref_colors": //配色
                    new ColorDialog(getActivity()).show();
                    return true;
                case "pref_schemas": //方案
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.pref_schemas)
                                    .setNegativeButton(
                                            android.R.string.cancel,
                                            null);
                    if (Rime.getSchemaId() == null || Rime.isEmpty()) {
                        if (!isSelect())
                            builder.setMessage(R.string.no_select); //提示安裝碼表
                        else
                            builder.setMessage(R.string.no_schemas); //提示安裝碼表
                    } else {
                        builder.setPositiveButton(
                                R.string.schemas_mgr,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int id) {
                                        new SchemaDialog(getActivity());
                                        di.dismiss();
                                    }
                                });
                        builder.setSingleChoiceItems(
                                Rime.getSchemaNames(),
                                Rime.getSchemaIndex(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int id) {
                                        di.dismiss();
                                        Rime.selectSchema(id); //切換方案
                                        Function.getPref(getActivity()).edit().putString("select_schema_id", Rime.getSchemaId()).apply();
                                    }
                                });
                    }
                    builder.create().show();
                    return true;
                case "pref_maintenance": //維護
                    Function.check();
                    return true;
                case "pref_deploy_opencc": //部署OpenCC
                    deployOpencc();
                    return true;
                case "pref_deploy": //部署
                    deploy();
                    return true;
                case "pref_sync": //同步
                    mProgressDialog.setMessage(getString(R.string.sync_progress));
                    mProgressDialog.show();
                    new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            try{
                                File[] fs = new File(Rime.get_sync_dir()).listFiles();
                                Rime.get_user_data_dir();
                                /*if(fs!=null){
                                    for (File f : fs) {
                                        if(f.getName().endsWith("userdb.txt"))
                                            f.delete();
                                    }
                                }*/
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                Function.sync();
                                File[] fs = new File(Rime.get_sync_dir(),Rime.get_user_id()).listFiles();
                                if(fs!=null){
                                    for (File f : fs) {
                                        if(!f.getName().endsWith("userdb.txt"))
                                            f.delete();
                                    }
                                }
                                JsonUtil.save(new File(new File(Rime.get_sync_dir(),Rime.get_user_id()),"settings.json"),new JSONObject(Function.getPref(getActivity()).getAll()));
                            } catch (Exception ex) {
                                Log.e(TAG, "Sync Exception" + ex);
                                return ex.getMessage();
                            }
                            return "";
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            mProgressDialog.dismiss();
                            isClear = true;
                            if (TextUtils.isEmpty(s)) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.done)
                                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgressDialog.dismiss();
                                            }
                                        }).create().show();
                            } else {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("提示")
                                        .setMessage(s)
                                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgressDialog.dismiss();
                                            }
                                        }).create().show();
                            }
                        }
                    }.execute();
                    return true;
                case "pref_reset": //回廠
                    new ResetDialog(getActivity()).show();
                    return true;
                case "pref_licensing": //許可協議
                    showLicenseDialog();
                    return true;
                case "pref_ui": //色調
                    getActivity().recreate();
                    //Function.showPrefDialog(getActivity());
                    return true;
                case "save":
                    new EditDialog(getActivity(), "输入文件名", "", new EditDialog.EditDialogCallback() {
                        @Override
                        public void onCallback(String text) {
                            save(text);
                        }
                    }).show();
                    return true;
            }
            return false;
        }

        private void save(String text) {
            try {
                String mDir = Function.getPref(getActivity()).getString("user_data_dir", getString(R.string.default_user_data_dir));
                File dir = new File(new File(mDir, "colors"), text + ".lua");
                BufferedWriter buf = new BufferedWriter(new FileWriter(dir));
                buf.write("{");
                buf.newLine();
                buf.write(String.format("name=\"%s\";", text));
                buf.newLine();
                String[] color_keys = new String[]{
                        "background_color",
                        "keyboard_back_color",
                        "candidate_back_color",
                        "composition_back_color",

                        "hilited_on_key_text_color",
                        "on_key_text_color",

                        "hilited_off_key_text_color",
                        "off_key_text_color",

                        "hilited_key_text_color",
                        "key_text_color",

                        "preview_text_color",
                        "preview_back_color",

                        "hilited_key_symbol_color",
                        "key_symbol_color",

                        "hilited_on_key_back_color",
                        "on_key_back_color",

                        "hilited_off_key_back_color",
                        "off_key_back_color",

                        "hilited_key_back_color",
                        "key_back_color",

                        "hilited_text_color",
                        "text_color",

                        "hilited_back_color",
                        "back_color",

                        "candidate_separator_color",

                        "hilited_comment_text_color",
                        "comment_text_color",

                        "hilited_candidate_back_color",

                        "hilited_candidate_text_color",
                        "candidate_text_color",

                        "hilited_label_color",
                        "label_color",
                        "text_back_color",

                        "shadow_color",
                        "hilited_key_border_color",
                        "key_border_color",
                        "hilited_candidate_border_color",
                        "candidate_border_color",
                        "border_color"
                };
                Config config = Config.get();
                SharedPreferences pref = Function.getPref(getActivity());
                for (String key : color_keys) {
                    int v = pref.getInt(key, 0);
                    if (v == 0) {
                        if (config != null) {
                            Integer o = config.getColor(key);
                            if (o != null)
                                v = o;
                            else
                                continue;
                        } else
                            continue;
                    }
                    buf.write(key);
                    buf.write("=");
                    buf.write(String.format("\"#%08x\";", v));
                    //buf.write(";");
                    buf.newLine();
                }

                String[] boards = new String[]{
                        "hilited_candidate_border",
                        "candidate_border",
                        "key_border",
                        "layout/border",
                        "hilited_candidate_round_corner",
                        "candidate_round_corner",
                        "key_round_corner",
                        "round_corner",
                        "layout/round_corner",

                        "vertical_gap",
                        "horizontal_gap",

                        "candidate_text_size",
                        "comment_text_size",
                        "text_size",
                        "key_text_size",
                        "key_long_text_size",
                        "label_text_size",

                };
                for (String key : boards) {
                    int v = pref.getInt(key, 0);
                    if (v == 0)
                        continue;
                    buf.write(key.replace("/", "_"));
                    buf.write("=");
                    if (key.endsWith("_size")) {
                        int f = pref.getInt(key, 0);
                        if (f == 0)
                            continue;
                        if (f > 0) {
                            buf.write(String.format("\"%d\"", f));
                        }
                    } else if (key.endsWith("_gap"))
                        buf.write(String.format("\"%f\"", (float) pref.getInt(key, 0) / 20));
                    else if (key.endsWith("_corner"))
                        buf.write(String.format("\"%d\"", pref.getInt(key, 0)));
                    else
                        buf.write(String.format("\"%f\"", (float) pref.getInt(key, 0) / 10));
                    buf.write(";");
                    buf.newLine();

                }
                buf.write("}");
                buf.close();
                Toast.makeText(getActivity(), "保存到：" + dir, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "保存失败：" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressLint("StaticFieldLeak")
        private void deploy() {
            mProgressDialog.setMessage(getString(R.string.deploy_progress));
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
                        Function.deploy();
                    } catch (Exception ex) {
                        Log.e(TAG, "Deploy Exception" + ex);
                        return ex.getMessage();
                    }
                    return execCmd("logcat -d -v long native:*");
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    mProgressDialog.dismiss();
                    isClear = true;
                    if (TextUtils.isEmpty(s)) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.done)
                                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mProgressDialog.dismiss();
                                    }
                                }).create().show();
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("提示")
                                .setMessage(s)
                                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mProgressDialog.dismiss();
                                    }
                                }).create().show();
                    }
                }
            }.execute();
        }

        public String execCmd(String cmd) {
            StringBuilder result = new StringBuilder();
            BufferedReader dis = null;
            BufferedWriter buf = null;
            try {
                String mDir = Function.getPref(getActivity()).getString("user_data_dir", getString(R.string.default_user_data_dir));
                File dir = new File(mDir, "logs");
                if (!dir.exists())
                    dir.mkdirs();
                buf = new BufferedWriter(new FileWriter(new File(dir, "deploy.log")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Process p = Runtime.getRuntime().exec(cmd);
                dis = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line = null;
                int n = 0;
                while ((line = dis.readLine()) != null) {
                    n++;
                    if (buf != null) {
                        buf.write(line);
                        buf.newLine();
                        buf.flush();
                    }
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
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result.toString().trim();
        }

        private void showLicenseDialog() {
            View licenseView = View.inflate(getActivity(), R.layout.licensing, null);
            WebView webView = (WebView) licenseView.findViewById(R.id.license_view);
            webView.setWebViewClient(
                    new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            // Disable all links open from this web view.
                            return true;
                        }
                    });
            String licenseUrl = "file:///android_asset/licensing.html";
            webView.loadUrl(licenseUrl);

            new AlertDialog.Builder(getActivity()).setTitle(R.string.ime_name).setView(licenseView).show();
        }

        private boolean isEnabled() {
            boolean enabled = false;
            for (InputMethodInfo i :
                    ((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE)).getEnabledInputMethodList()) {
                if (getActivity().getPackageName().contentEquals(i.getPackageName())) {
                    enabled = true;
                    break;
                }
            }
            return enabled;
        }

        private boolean isSelect() {
            String ss = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            if (ss.startsWith(getActivity().getPackageName()))
                return true;
            return false;
        }

        private void deployOpencc() {
            boolean b = Config.deployOpencc();
        }
    }
}
