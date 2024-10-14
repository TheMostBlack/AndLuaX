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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.androlua.LuaBitmapDrawable;
import com.luajava.LuaJavaAPI;
import com.luajava.LuaState;
import com.luajava.LuaTable;
import com.osfans.trime.pro.BuildConfig;
import com.osfans.trime.pro.R;
import com.osfans.trime.enums.InlineModeType;
import com.osfans.trime.enums.WindowsPositionType;

import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 解析YAML配置文件
 */
public class Config {
    // 默认的用户数据路径
    private static final String RIME = "rime";
    private static final String TAG = "Config";
    private static String userDataDir;
    private static String sharedDataDir;

    private Map<String, Object> mStyle = new HashMap<>(), mDefaultStyle = new HashMap<>();
    private String themeName;
    private static String defaultName = "trime";
    private String schema_id;

    private static Config self = null;
    private SharedPreferences mPref;

    private Map<String, String> fallbackColors;
    private Map<String, Object> presetColorSchemes = new HashMap<>();
    private Map<String, Object> presetKeyboards = new HashMap<>();

    private Map<String, Object> mDefaultKeyboards = new HashMap<>();
    private Map<String, Map> mDefaultKeys = new HashMap<>();
    private Context mContext;
    private boolean hasDefault;

    public Config(Context context) {
        self = this;
        mContext = context;
        mPref = Function.getPref(context);
        userDataDir = context.getString(R.string.default_user_data_dir);
        sharedDataDir = context.getString(R.string.default_shared_data_dir);
        themeName = mPref.getString("pref_selected_theme", "trime");
        CrashHandler.getInstance().init(context, getUserDataDir());
        prepareRime(context);
        deployTheme();
        init();
    }

    public Context getContext() {
        return mContext;
    }

    public String getTheme() {
        return themeName;
    }

    public String getSharedDataDir() {
        return mPref.getString("shared_data_dir", sharedDataDir);
    }

    public String getUserDataDir() {
        return mPref.getString("user_data_dir", userDataDir);
    }

    public String getResDataDir(String sub) {
        String name = new File(getSharedDataDir(), sub).getPath();
        if (new File(name).exists()) {
            return name;
        }
        if (new File(name).mkdirs()) {
            return name;
        }
        return new File(getUserDataDir(), sub).getPath();
    }

    private void prepareRime(Context context) {
        boolean isExist = new File(getSharedDataDir()).exists();
        boolean isOverwrite = Function.isDiffVer(context);
        boolean auto_reset = Function.getPref(context).getBoolean("pref_auto_reset", false);

        String defaultFile = "trime.yaml";
        if (isOverwrite && auto_reset) {
            copyFileOrDir(context, RIME, true);
        } else if (isExist) {
            String path = new File(RIME, defaultFile).getPath();
            copyFileOrDir(context, path, false);
        } else {
            copyFileOrDir(context, RIME, false);
        }
        while (!new File(getSharedDataDir(), defaultFile).exists()) {
            SystemClock.sleep(3000);
            copyFileOrDir(context, RIME, isOverwrite);
        }
        Rime.get(!isExist); //覆蓋時不強制部署
    }

    public static String[] getThemeKeys(boolean isUser) {
    /*File d = new File(isUser ? get().getUserDataDir() : get().getSharedDataDir());
    FilenameFilter trimeFilter =
            new FilenameFilter() {
              @Override
              public boolean accept(File dir, String filename) {
                return filename.endsWith("trime.yaml");
              }
            };
    return d.list(trimeFilter);*/

        String u = get().getUserDataDir();
        String s = get().getSharedDataDir();
        File d = new File(u);
        FilenameFilter trimeFilter =
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith("trime.yaml");
                    }
                };
        String[] ul = d.list(trimeFilter);
        if (u.toLowerCase().equals(s.toLowerCase()))
            return ul;
        d = new File(s);
        String[] sl = d.list(trimeFilter);
        String[] l = new String[ul.length + sl.length];
        System.arraycopy(ul, 0, l, 0, ul.length);
        System.arraycopy(sl, 0, l, ul.length, sl.length);
        return l;
    }

    public static String[] getThemeNames(String[] keys) {
        if (keys == null) return null;
        int n = keys.length;
        String[] names = new String[n];
        for (int i = 0; i < n; i++) {
            String k = keys[i].replace(".trime.yaml", "").replace(".yaml", "");
            names[i] = k;
        }
        return names;
    }

    public static boolean deployOpencc() {
        String dataDir = get().getResDataDir("opencc");
        File d = new File(dataDir);
        if (d.exists()) {
            FilenameFilter txtFilter =
                    new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.endsWith(".txt");
                        }
                    };
            for (String txtName : d.list(txtFilter)) {
                txtName = new File(dataDir, txtName).getPath();
                String ocdName = txtName.replace(".txt", ".ocd");
                Rime.opencc_convert_dictionary(txtName, ocdName, "text", "ocd");
            }
        }
        return true;
    }

    public static String[] list(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
        } catch (IOException ex) {
            Log.e(TAG, "I/O Exception", ex);
        }
        return assets;
    }

    public boolean copyFileOrDir(Context context, String path, boolean overwrite) {
        AssetManager assetManager = context.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(context, path, overwrite);
            } else {
                File dir = new File(getSharedDataDir(), path.length() >= 5 ? path.substring(5) : "");
                if (!dir.exists()) dir.mkdirs();
                for (int i = 0; i < assets.length; ++i) {
                    String assetPath = new File(path, assets[i]).getPath();
                    copyFileOrDir(context, assetPath, overwrite);
                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "I/O Exception", ex);
            return false;
        }
        return true;
    }

    private boolean copyFile(Context context, String filename, boolean overwrite) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = new File(filename.endsWith(".bin") ? getUserDataDir() : getSharedDataDir(), filename.length() >= 5 ? filename.substring(5) : "").getPath();
            if (new File(newFileName).exists() && !overwrite) return true;
            out = new FileOutputStream(newFileName);
            int BLK_SIZE = 1024;
            byte[] buffer = new byte[BLK_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    private void deployTheme() {
        if (getUserDataDir().contentEquals(getSharedDataDir())) return; //相同文件夾不部署主題
        String[] configs = getThemeKeys(false);
        if (configs == null)
            return;
        for (String config : configs) Rime.deploy_config_file(config, "config_version");
    }

    public void setTheme(String theme) {
        themeName = theme;
        SharedPreferences.Editor edit = mPref.edit();
        edit.putString("pref_selected_theme", themeName);
        edit.apply();
        init();
    }

    private void copy(Object o1, Object o2) {
        if (o1 instanceof Map && o2 instanceof Map) {
            Map m = (Map) o1;
            Map m2 = (Map) o2;
            if (m.containsKey("keys")) {
                return;
            }
            for (Object o : m2.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                Object k = entry.getKey();
                Object v = entry.getValue();
                if (m.containsKey(k)) {
                    if (k.equals("preset_color_schemes") || k.equals("window"))
                        continue;
                    copy(m.get(k), v);
                } else {
                    if (k.toString().startsWith("qwerty") && m.containsKey("default"))
                        continue;
                    m.put(k, v);
                }
            }
        } else if (o1 instanceof List && o2 instanceof List) {
            List l = (List) o1;
            List l2 = (List) o2;
            for (Object o : l2) {
                if (!l.contains(o))
                    l.add(o);
            }
        }
    }

    private void init() {
        Log.i("rime", "reset: init");
        try {
            Rime.deploy_config_file(themeName + ".yaml", "config_version");
            Map<String, Object> m = Rime.config_get_map(themeName, "");
            if (m == null) {
                themeName = defaultName;
                m = Rime.config_get_map(themeName, "");
            }
            if (!themeName.equals(defaultName)) {
                copy(m, Rime.config_get_map(defaultName, ""));
            }
            try {
                File path = new File(getSharedDataDir(), themeName + ".lua");
                if (path.exists()) {
                    Trime service = Trime.getService();
                    service.doFile(path.getAbsolutePath(), m);
                    LuaState L = service.getLuaState();
                    String[] keys = new String[]{"style", "fallback_colors", "preset_keys", "preset_color_schemes", "preset_keyboards"};
                    for (String key : keys) {
                        Map mp = (Map) m.get(key);
                        L.getGlobal(key);
                        int t = L.getTop();
                        if (L.isTable(-1)) {
                            Map tb = LuaJavaAPI.createMap(L, t);
                            copyMap(mp, tb);
                        }
                        L.pop(1);
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                Toast.makeText(getContext(), "init error:" + e.toString(), Toast.LENGTH_LONG).show();
            }

            Map mk = (Map<String, Object>) m.get("android_keys");
            mDefaultStyle = (Map<String, Object>) m.get("style");
            fallbackColors = (Map<String, String>) m.get("fallback_colors");
            Key.androidKeys = (List<String>) mk.get("name");
            Key.setSymbolStart(Key.androidKeys.contains("A") ? Key.androidKeys.indexOf("A") : 284);
            Key.setSymbols((String) mk.get("symbols"));
            if (Function.isEmpty(Key.getSymbols()))
                Key.setSymbols("ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+[]\\{}|;':\",./<>?");
            mDefaultKeys = (Map<String, Map>) m.get("preset_keys");
            presetColorSchemes = (Map<String, Object>) m.get("preset_color_schemes");

            try {
                File dir = new File(getSharedDataDir(), "colors");
                if (dir.exists()) {
                    File[] fs = dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.isFile();
                        }
                    });
                    Trime service = Trime.getService();
                    for (File f : fs) {
                        Object obj = service.doFile(f.getAbsolutePath());
                        if (obj instanceof Map) {
                            LuaTable map = (LuaTable) obj;
                            presetColorSchemes.put(f.getName(), map.asMap());
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                Toast.makeText(getContext(), "init error:" + e.toString(), Toast.LENGTH_LONG).show();
            }

            mDefaultKeyboards = (Map<String, Object>) m.get("preset_keyboards");
            Rime.setShowSwitches(getShowSwitches());
            reset();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e(TAG, e.getMessage());
            if (!themeName.equals(defaultName)) {
                setTheme(defaultName);
            } else {
                Toast.makeText(getContext(), "init error:" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        Log.i("rime", "reset: init");
    }

    private void copyMap(Map mp, Map tb) {
        Set ks = tb.keySet();
        for (Object k : ks) {
            Object o = mp.get(k);
            if (o == null) {
                mp.put(k, tb.get(k));
            } else if (o instanceof List) {
                List ls = ((List) o);
                Collection vs = ((Map) tb.get(k)).values();
                for (Object v : vs) {
                    if (!ls.contains(v))
                        ls.add(v);
                }
            } else if (o instanceof Map) {
                ((Map) o).putAll((Map) tb.get(k));
            } else {
                mp.put(k, tb.get(k));
            }
        }
    }

    public void reset() {
        schema_id = Rime.getSchemaId();
        if (BuildConfig.DEBUG) {
            try {
                throw new RuntimeException("reset " + schema_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "reset: " + schema_id);
        }


        hasDefault = false;
        presetKeyboards = new HashMap(mDefaultKeyboards);
        Map<String, Map> km = (Map<String, Map>) Rime.schema_get_value(schema_id, "preset_keyboards");
        if (km != null) {
            if (km.containsKey("default"))
                hasDefault = true;
            presetKeyboards.putAll(km);
        }

        Key.presetKeys = new HashMap<>(mDefaultKeys);
        Map<String, Map> ks = (Map<String, Map>) Rime.schema_get_value(schema_id, "preset_keys");
        if (ks != null)
            Key.presetKeys.putAll(ks);
        mStyle = (Map<String, Object>) Rime.schema_get_value(schema_id, "style");
        if (mStyle == null)
            return;
        List<String> names = (List<String>) mStyle.get("keyboards");
        List<String> ls = (List<String>) mDefaultStyle.get("keyboards");
        if (names != null) {
            for (String l : ls) {
                if (!names.contains(l))
                    names.add(l);
            }
        }
        try {
            File path = new File(getSharedDataDir(), schema_id + ".lua");
            if (path.exists()) {
                Trime service = Trime.getService();
                service.doFile(path.getAbsolutePath());
                LuaState L = service.getLuaState();
                String[] keys = new String[]{"style", "fallback_colors", "preset_keys", "preset_color_schemes", "preset_keyboards"};
                String key = "style";
                L.getGlobal(key);
                int t = L.getTop();
                if (L.isTable(-1)) {
                    Map tb = LuaJavaAPI.createMap(L, t);
                    copyMap(mStyle, tb);
                }
                L.pop(1);
                key = "preset_keys";
                L.getGlobal(key);
                t = L.getTop();
                if (L.isTable(-1)) {
                    Map tb = LuaJavaAPI.createMap(L, t);
                    Key.presetKeys.putAll(tb);
                }
                L.pop(1);
                key = "preset_keyboards";
                L.getGlobal(key);
                t = L.getTop();
                if (L.isTable(-1)) {
                    Map tb = LuaJavaAPI.createMap(L, t);
                    if (tb.containsKey("default"))
                        hasDefault = true;
                    presetKeyboards.putAll(tb);
                }
                L.pop(1);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "init error:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private Object _getValue(String k1, String k2) {
        Map<String, Object> m;
        if (mStyle != null && mStyle.containsKey(k1)) {
            m = (Map<String, Object>) mStyle.get(k1);
            if (m != null && m.containsKey(k2)) return m.get(k2);
        }
        if (mDefaultStyle != null && mDefaultStyle.containsKey(k1)) {
            m = (Map<String, Object>) mDefaultStyle.get(k1);
            if (m != null && m.containsKey(k2)) return m.get(k2);
        }
        return null;
    }

    private Object _getValue(String k1) {
        Object o = getValueFromColorScheme(k1);
        if (o != null)
            return o;
        if (mStyle != null && mStyle.containsKey(k1)) return mStyle.get(k1);
        if (mDefaultStyle != null && mDefaultStyle.containsKey(k1)) return mDefaultStyle.get(k1);
        return null;
    }

    private Object getValueFromColorScheme(String key) {
        String scheme = getColorScheme();
        if (!presetColorSchemes.containsKey(scheme)) {
            return null;
        }
        Map map = (Map<String, Object>) presetColorSchemes.get(scheme);
        if (map == null) return null;
        Object o = map.get(key);
        return o;
    }

    public Object getValue(String s) {

        String[] ss = s.split("/");
        if (ss.length == 1) {
            return _getValue(ss[0]);
        } else if (ss.length == 2) {
            String sss = s.replaceAll("/", "_");
            Object o = _getValue(sss);
            if (o != null)
                return o;
            return _getValue(ss[0], ss[1]);
        }
        return null;
    }

    public boolean hasKey(String s) {
        return getValue(s) != null;
    }

    private String getKeyboardName(String name) {
        if (name.contentEquals(".default")) {
            if (hasDefault)
                return "default";
            if (presetKeyboards.containsKey(schema_id)) {
                name = schema_id; //匹配方案名
            } else {
                if (schema_id.indexOf("_") >= 0) name = schema_id.split("_")[0];
                if (!presetKeyboards.containsKey(name)) { //匹配“_”前的方案名
                    Object o = Rime.schema_get_value(schema_id, "speller/alphabet");
                    name = "qwerty"; //26
                    if (o != null) {
                        String alphabet = o.toString();
                        if (presetKeyboards.containsKey(alphabet)) {
                            name = alphabet; //匹配字母表
                        } else {
                            if (alphabet.indexOf(",") >= 0 || alphabet.indexOf(";") >= 0)
                                name += "_";
                            if (alphabet.indexOf("0") >= 0 || alphabet.indexOf("1") >= 0)
                                name += "0";
                        }
                    }
                }
            }
        }
        if (!presetKeyboards.containsKey(name)) {
            name = "default";
        }
        Map<String, Object> m = (Map<String, Object>) presetKeyboards.get(name);
        if (m.containsKey("import_preset")) {
            name = m.get("import_preset").toString();
        }
        return name;
    }

    public List<String> getKeyboardNames() {
        List<String> names = (List<String>) getValue("keyboards");
        if (names == null) {
            ArrayList<String> ret = new ArrayList<String>();
            ret.add("default");
            return ret;
        }
        List<String> keyboards = new ArrayList<String>();
        for (String s1 : names) {
            String s = getKeyboardName(s1);
            if (!keyboards.contains(s)) {
                keyboards.add(s);
                if (s1.contentEquals(".default")) {
                    if (presetKeyboards.containsKey(s + "_land"))
                        keyboards.add(1, s + "_land");
                    else
                        keyboards.add(1, s);
                } else if (presetKeyboards.containsKey(s + "_land")) {
                    keyboards.add(s + "_land");
                }
            }

        }
        return keyboards;
    }

    public Map<String, Object> getKeyboard(String name) {
        if (presetKeyboards == null) {
            return new HashMap<>();
        }

        if (!presetKeyboards.containsKey(name))
            name = "default";
        return (Map<String, Object>) presetKeyboards.get(name);
    }

    public static Config get() {
        return self;
    }

    public static Config get(Context context) {
        if (self == null) self = new Config(context);
        return self;
    }

    public void destroy() {
        if (mDefaultStyle != null) mDefaultStyle.clear();
        if (mStyle != null) mStyle.clear();
        self = null;
    }

    public static int getPixel(Float f) {
        if (f == null) return 0;
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, f, Resources.getSystem().getDisplayMetrics()));
    }

    public int getPixel(String key) {
        return getPixel(getFloat(key));
    }

    public static Integer getPixel(Map m, String k, Object s) {
        Object o = getValue(m, k, s);
        if (o == null) return null;
        return getPixel(Float.valueOf(o.toString()));
    }

    public static float getFloatPixel(Map m, String k, Object s) {
        Object o = getValue(m, k, s);
        if (o == null) return 0;
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, Float.valueOf(o.toString()), Resources.getSystem().getDisplayMetrics());
    }

    public static Integer getPixel(Map m, String k) {
        return getPixel(m, k, null);
    }

    public static Integer getColor(Map m, String k) {
        Integer color = null;
        if (m.containsKey(k)) {
            Object o = m.get(k);
            String s = o.toString();
            color = parseColor(s);
            if (color == null) color = get().getCurrentColor(s);
        }
        return color;
    }

    public static Drawable getColorDrawable(Map m, String k) {
        if (m.containsKey(k)) {
            Object o = m.get(k);
            String s = o.toString();
            Integer color = parseColor(s);
            if (color != null) {
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(color);
                return gd;
            } else {
                Config config = get();
                Drawable d = config.getCurrentColorDrawable(s);
                if (d == null) d = config.drawableObject(o);
                return d;
            }
        }
        return null;
    }

    public static Object getValue(Map m, String k, Object o) {
        return m.containsKey(k) ? m.get(k) : o;
    }

    public static Integer getInt(Map m, String k, int s) {
        Object o = getValue(m, k, s);
        if (o == null) return s;
        return Long.decode(o.toString()).intValue();
    }

    public static Float getFloat(Map m, String k) {
        Object o = getValue(m, k, null);
        if (o == null) return null;
        return Float.valueOf(o.toString());
    }

    public static Double getDouble(Map m, String k, Object s) {
        Object o = getValue(m, k, s);
        if (o == null) return null;
        return Double.valueOf(o.toString());
    }

    public static String getString(Map m, String k, Object s) {
        Object o = getValue(m, k, s);
        if (o == null) return "";
        return o.toString();
    }

    public static String getString(Map m, String k) {
        return getString(m, k, "");
    }

    public static Boolean getBoolean(Map m, String k, Object s) {
        Object o = getValue(m, k, s);
        if (o == null) return null;
        return Boolean.valueOf(o.toString());
    }

    public static Boolean getBoolean(Map m, String k) {
        return getBoolean(m, k, true);
    }

    public boolean getBoolean(String key) {
        Object o = getValue(key);
        if (o == null) return true;
        return Boolean.valueOf(o.toString());
    }

    public double getDouble(String key) {
        Object o = getValue(key);
        if (o == null) return 0d;
        return Double.valueOf(o.toString());
    }

    public float getFloat(String key) {
        Float i = BackUtil.getPixel(key);
        if (i != null)
            return i;
        Object o = getValue(key);
        if (o == null) return 0f;
        if (o instanceof Number)
            return ((Number) o).floatValue();
        return Float.valueOf(o.toString());
    }

    public float getRawFloat(String key) {
        Object o = getValue(key);
        if (o == null) return 0f;
        return Float.valueOf(o.toString());
    }

    public int getInt(String key) {
        Object o = getValue(key);
        if (o == null) return 0;
        return Long.decode(o.toString()).intValue();
    }

    public String getString(String key) {
        Object o = getValue(key);
        if (o == null) return "";
        return o.toString();
    }

    private Object getColorObject(String key) {
        int i = BackUtil.get(key);
        if (i != 0)
            return String.format("#%08x", i);
        String scheme = getColorScheme();
        if (!presetColorSchemes.containsKey(scheme)) scheme = getString("color_scheme"); //主題中指定的配色
        if (!presetColorSchemes.containsKey(scheme)) scheme = "default"; //主題中的default配色
        Map map = (Map<String, Object>) presetColorSchemes.get(scheme);
        if (map == null) return null;
        setColor(scheme);
        Object o = map.get(key);
        String fallbackKey = key;
        while (o == null && fallbackColors.containsKey(fallbackKey)) {
            fallbackKey = fallbackColors.get(fallbackKey);
            o = map.get(fallbackKey);
        }
        if (o instanceof Number) {
            int c = ((Number) o).intValue();
            if (Integer.toHexString(c).length() < 7) {
                o = String.format("0xff%06x", c);
            } else {
                o = String.format("0x%08x", c);
            }
        }
        return o;
    }

    private static Integer parseColor(String s) {
        Integer color = null;
        if (s.contains(".")) return color; //picture name
        try {
            s = s.toLowerCase(Locale.getDefault());
            if (s.startsWith("0x")) {
                if (s.length() == 3 || s.length() == 4)
                    s = String.format("#%02x000000", Long.decode(s.substring(2))); //0xAA
                else if (s.length() < 8) s = String.format("#%06x", Long.decode(s.substring(2)));
                else if (s.length() == 9) s = "#0" + s.substring(2);
            }
            color = Color.parseColor(s.replace("0x", "#"));
        } catch (Exception e) {
            //Log.e(TAG, "unknown color " + s);
        }
        return color;
    }

    public Integer getCurrentColor(String key) {
        Object o = getColorObject(key);
        if (o == null) return null;
        return parseColor(o.toString());
    }

    private Object getColorFromDefault(String key) {
        Map<String, Object> def = (Map<String, Object>) presetColorSchemes.get("default");
        if (def == null)
            return null;
        return def.get(key);
    }

    public Integer getColor(String key) {
        Object o = getColorObject(key);
        if (o == null) {
            o = getColorFromDefault(key);
        }
        if (o == null) return null;
        return parseColor(o.toString());
    }

    public Integer getRawColor(String key) {
        String scheme = getColorScheme();
        if (!presetColorSchemes.containsKey(scheme)) scheme = getString("color_scheme"); //主題中指定的配色
        if (!presetColorSchemes.containsKey(scheme)) scheme = "default"; //主題中的default配色
        Map map = (Map<String, Object>) presetColorSchemes.get(scheme);
        if (map == null) return null;
        setColor(scheme);
        Object o = map.get(key);
        String fallbackKey = key;
        while (o == null && fallbackColors.containsKey(fallbackKey)) {
            fallbackKey = fallbackColors.get(fallbackKey);
            o = map.get(fallbackKey);
        }
        if (o == null) {
            o = getColorFromDefault(key);
        }
        if (o == null) return null;
        if (o instanceof Number)
            o = String.format("#%08x", ((Number) o).intValue());
        return parseColor(o.toString());
    }


    public String getColorScheme() {
        return mPref.getString("pref_selected_color_scheme", "default");
    }

    public void setColor(String color) {
        SharedPreferences.Editor edit = mPref.edit();
        edit.putString("pref_selected_color_scheme", color);
        edit.apply();
        //deployTheme();
    }

    public String[] getColorKeys() {
        if (presetColorSchemes == null) return null;
        try {
            Set<String> keys = new HashSet<>(presetColorSchemes.keySet());
            for (String key : keys) {
                if (key.endsWith(".lua"))
                    presetColorSchemes.remove(key);
            }
            File dir = new File(getSharedDataDir(), "colors");
            if (dir.exists()) {
                File[] fs = dir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile();
                    }
                });
                Trime service = Trime.getService();
                for (File f : fs) {
                    Object obj = service.doFile(f.getAbsolutePath());
                    if (obj instanceof Map) {
                        LuaTable map = (LuaTable) obj;
                        presetColorSchemes.put(f.getName(), map.asMap());
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(getContext(), "init error:" + e.toString(), Toast.LENGTH_LONG).show();
        }

        String[] keys = new String[presetColorSchemes.size()];
        presetColorSchemes.keySet().toArray(keys);
        return keys;
    }

    public String[] getColorNames(String[] keys) {
        if (keys == null) return null;
        int n = keys.length;
        String[] names = new String[n];
        for (int i = 0; i < n; i++) {
            Map<String, Object> m = (Map<String, Object>) presetColorSchemes.get(keys[i]);
            names[i] = m.get("name").toString();
        }
        return names;
    }

    public Typeface getFont(String key) {
        String name = getString(key);
        if (name != null) {
            File f = new File(getResDataDir("fonts"), name);
            if (f.exists()) return Typeface.createFromFile(f);
        }
        return Typeface.DEFAULT;
    }

    public Drawable drawableObject(Object o) {
        if (o == null) return null;
        String name = o.toString();
        Integer color = parseColor(name);
        if (color != null) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(color);
            return gd;
        } else {
            Drawable d = BackUtil.get(Trime.getService(), name);
            if (d != null)
                return d;
            String nameDirectory = getResDataDir(getTheme());
            if (new File(nameDirectory).exists()) {
                File file = new File(nameDirectory, name);
                if (file.exists()) {
                    return new LuaBitmapDrawable(Trime.getService(), file.getAbsolutePath());
                }
            }
            nameDirectory = getResDataDir("backgrounds");
            String pkg = Function.getPref(mContext).getString("background_package", "none");
            if (!pkg.equals("none")) {
                File dir = new File(nameDirectory, pkg);
                if (dir.exists()) {
                    File f = new File(dir, name);
                    if (f.exists()) {
                        return new LuaBitmapDrawable(Trime.getService(), f.getAbsolutePath());
                    }
                }
            }

            name = new File(nameDirectory, name).getPath();
            File f = new File(name);
            if (f.exists()) {
                //return new BitmapDrawable(BitmapFactory.decodeFile(name));
                return new LuaBitmapDrawable(Trime.getService(), f.getAbsolutePath());
            }
        }
        return null;
    }

    public LuaBitmapDrawable getDrawableObject(String name) {
        if (name == null) return null;
        LuaBitmapDrawable d = BackUtil.get(Trime.getService(), name);
        //Log.i(TAG, "getDrawableObject:1 "+d);
        if (d != null)
            return d;
        String nameDirectory = getResDataDir(getTheme());
        if (new File(nameDirectory).exists()) {
            File file = new File(nameDirectory, name);
            if (file.exists()) {
                return new LuaBitmapDrawable(Trime.getService(), file.getAbsolutePath());
            }
        }
        nameDirectory = getResDataDir("backgrounds");
        String pkg = Function.getPref(mContext).getString("background_package", "none");
        if (!pkg.equals("none")) {
            File dir = new File(nameDirectory, pkg);
            if (dir.exists()) {
                File f = new File(dir, name);
                if (f.exists()) {
                    Log.i(TAG, "getDrawableObject:3 "+f.getAbsolutePath());
                    return new LuaBitmapDrawable(Trime.getService(), f.getAbsolutePath());
                }
            }
        }

        name = new File(nameDirectory, name).getPath();
        File f = new File(name);
        if (f.exists()) {
            //return new BitmapDrawable(BitmapFactory.decodeFile(name));
            return new LuaBitmapDrawable(Trime.getService(), f.getAbsolutePath());
        }
        return null;
    }

    private Drawable getCurrentColorDrawable(String key) {
        Object o = getColorObject(key);
        return drawableObject(o);
    }

    public Drawable getColorDrawable(String key) {
        Drawable d = BackUtil.get(Trime.getService(), key);
        if (d != null)
            return d;
        Object o = getColorObject(key);
        if (o == null) {
            o = getColorFromDefault(key);
        }
        return drawableObject(o);
    }

    public Drawable getDrawable(String key) {
        Object o = getValue(key);
        return drawableObject(o);
    }

    public InlineModeType getInlinePreedit() {
        switch (mPref.getString("inline_preedit", "preview")) {
            case "preview":
            case "preedit":
            case "true":
                return InlineModeType.INLINE_PREVIEW;
            case "composition":
                return InlineModeType.INLINE_COMPOSITION;
            case "input":
                return InlineModeType.INLINE_INPUT;
        }
        return InlineModeType.INLINE_NONE;
    }

    public WindowsPositionType getWinPos() {
        //return WindowsPositionType.fromString(getString("layout/position"));
        return WindowsPositionType.fromString(getPosition());
    }

    public boolean isShowStatusIcon() {
        return mPref.getBoolean("pref_notification_icon", false);
    }

    public boolean isCandidateScroll() {
        return mPref.getBoolean("candidate_scroll", true);
    }

    public boolean isKeyLongPress() {
        return mPref.getBoolean("key_longpress", true);
    }

    public boolean isShowBottomKey() {
        return mPref.getBoolean("show_bottom_key", false);
    }

    public boolean isShowTopKey() {
        return mPref.getBoolean("show_top_key", false);
    }

    public boolean isDestroyOnQuit() {
        return mPref.getBoolean("pref_destroy_on_quit", false);
    }

    public int getLongTimeout() {
        int progress = mPref.getInt("longpress_timeout", 20);
        //if (progress > 60) progress = 60;
        return progress * 10 + 100;
    }

    public int getRepeatInterval() {
        int progress = mPref.getInt("repeat_interval", 40);
        //if (progress > 90) progress = 90;
        return progress * 10 + 10;
    }

    private boolean getShowSwitches() {
        return mPref.getBoolean("show_switches", true);
    }

    public boolean getShowPreview() {
        return mPref.getBoolean("show_preview", false);
    }

    public boolean getShowHint() {
        return mPref.getBoolean("show_hint", true);
    }

    public boolean getShowWindow() {
        return mPref.getBoolean("show_window", true) && hasKey("window");
    }

    public boolean getSoftCursor() {
        return mPref.getBoolean("soft_cursor", true);
    }

    public float getKeyboardHeight() {
        return Float.valueOf(mPref.getString("keyboard_height", "1"));
    }

    public boolean isCloudInput() {
        return mPref.getBoolean("cloud_input", false);
    }

    public boolean isKeySwipe() {
        return mPref.getBoolean("key_swipe", true);
    }

    public String getMovable() {
        return mPref.getString("layout_movable", "false");
    }

    public String getPosition() {
        String p = mPref.getString("layout_position", "fixed");
        if (p.equals("default"))
            p = getString("layout/position");
        return p;
    }

    public Set<String> getFilterSwitches() {
        return mPref.getStringSet("filter_switches", new HashSet<String>());
    }

    public Set<String> getSwitchesStates() {
        return mPref.getStringSet("switches_states", new HashSet<String>());
    }

    public int getMinLength() {
        int ret = Integer.valueOf(mPref.getString("layout_min_length", "0"));
        if (ret == 0)
            ret = getInt("layout/min_length");
        return ret;
    }

    public int getMaxEntries() {
        int ret = Integer.valueOf(mPref.getString("layout_max_entries", "0"));
        if (ret == 0)
            ret = getInt("layout/max_entries");
        return ret;
    }

    public int getLineMaxLength() {
        int ret = Integer.valueOf(mPref.getString("layout_line_max_length", "0"));
        if (ret == 0)
            ret = getInt("layout/max_length");
        return ret;
    }

    public boolean getAllPhrases() {
        return /*getMaxEntries()==-1||*/getBoolean("layout/all_phrases");
    }

    public int getCloudMaxEntries() {
        return Integer.valueOf(mPref.getString("layout_cloud_max_entries", "1"));
    }

    public List<Map<String, Object>> getCustomCandidate() {
        List<Map<String, Object>> ret = new ArrayList<>();
        if (Key.presetKeys == null)
            return ret;
        String[] ss = mPref.getString("custom_candidate", "").split("\\|");
        for (String s : ss) {
            if (!Key.presetKeys.containsKey(s))
                continue;
            Map<String, Object> m = new HashMap<>();
            ArrayList<String> o = new ArrayList<>();
            o.add(s);
            m.put("options", o);
            ArrayList<String> t = new ArrayList<>();
            t.add(Key.presetKeys.get(s).get("label").toString());
            m.put("states", t);
            ret.add(m);
        }
        return ret;
    }

    public boolean isSystemSpeak() {
        return mPref.getBoolean("system_speak", false);
    }

    public int getKeyAlpha() {
        return mPref.getInt("key_alpha", 255);
    }

    public boolean isKeyboardFloat() {
        return mPref.getBoolean("keyboard_float", false);
    }

    public float getKeyboardWidth() {
        return Integer.valueOf(mPref.getInt("keyboard_width", 100)).floatValue() / 200 + 0.5f;
    }

    public float getCompositionWidth() {
        return Integer.valueOf(mPref.getInt("composition_width", 100)).floatValue() / 200 + 0.5f;
    }

    public boolean isKeyboardSmall() {
        return mPref.getBoolean("keyboard_small", false);
    }

    public boolean isPhraseSort() {
        return mPref.getBoolean("phrase_sort", true);
    }

    public boolean isCompositionSingleLine() {
        return mPref.getBoolean("composition_single", false);
    }

    public boolean isAsyncKey() {
        return mPref.getBoolean("async_key", true);
    }

    public boolean isCompositionEndTop() {
        return mPref.getBoolean("composition_end_top", false);
    }
}
