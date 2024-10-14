package com.osfans.trime;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.androlua.LuaBitmapDrawable;
import com.androlua.LuaContext;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

/**
 * Created by nirenr on 2020/2/18.
 */

public class BackUtil {
    private static final HashMap<String, String> cache = new HashMap<>();
    private static final HashMap<String, String> cache_land = new HashMap<>();
    private static final HashMap<Integer, String> icache = new HashMap<>();
    private static final HashMap<Integer, String> icache2 = new HashMap<>();
    private static final HashMap<String, Integer> colors = new HashMap<>();
    private static final HashMap<String, Float> pixels = new HashMap<>();

    public static void reset(Context context) {
        cache.clear();
        cache_land.clear();
        icache.clear();
        icache2.clear();
        colors.clear();
        pixels.clear();
        String userDataDir = Config.get().getUserDataDir();

        File dir = new File(userDataDir, "backgrounds");
        if (dir.exists()) {
            File[] fs = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }

            });
            if (fs != null) {
                for (File ff : fs) {
                    String f = ff.getName();
                    f = f.toLowerCase();
                    if (f.startsWith("keyboard.")) {
                        cache.put("keyboard", ff.getAbsolutePath());
                    } else if (f.startsWith("key.")) {
                        cache.put("key", ff.getAbsolutePath());
                    } else if (f.startsWith("background.")) {
                        cache.put("background", ff.getAbsolutePath());
                    } else if (f.startsWith("candidate.")) {
                        cache.put("candidate", ff.getAbsolutePath());
                    } else if (f.startsWith("composition.")) {
                        cache.put("composition", ff.getAbsolutePath());
                    }
                    cache.put(f, ff.getAbsolutePath());

                    //加载横屏图片
                    if (f.startsWith("keyboard_land.")) {
                        cache_land.put("keyboard", ff.getAbsolutePath());
                    } else if (f.startsWith("key_land.")) {
                        cache_land.put("key", ff.getAbsolutePath());
                    } else if (f.startsWith("background_land.")) {
                        cache_land.put("background", ff.getAbsolutePath());
                    } else if (f.startsWith("candidate_land.")) {
                        cache_land.put("candidate", ff.getAbsolutePath());
                    } else if (f.startsWith("composition_land.")) {
                        cache_land.put("composition", ff.getAbsolutePath());
                    } else if (f.contains("_land.")) {
                        cache_land.put(f.replace("_land.", "."), ff.getAbsolutePath());
                    }
                }
            }
        }

        String pkg = Function.getPref(context).getString("background_package", "none");
        if (!pkg.equals("none")) {
            dir = new File(new File(userDataDir, "backgrounds"), pkg);
            if (dir.exists()) {
                File[] fs = dir.listFiles();
                if (fs != null) {
                    for (File ff : fs) {
                        String f = ff.getName();
                        String n = f;
                        try {
                            int i = f.indexOf(".");
                            if (i > 0)
                                n = f.substring(0, i);
                            i = Integer.valueOf(n);
                            icache.put(i, ff.getAbsolutePath());
                            //continue;
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        f = f.toLowerCase();
                        if (f.startsWith("keyboard.")) {
                            cache.put("keyboard", ff.getAbsolutePath());
                        } else if (f.startsWith("key.")) {
                            cache.put("key", ff.getAbsolutePath());
                        } else if (f.startsWith("background.")) {
                            cache.put("background", ff.getAbsolutePath());
                        } else if (f.startsWith("candidate.")) {
                            cache.put("candidate", ff.getAbsolutePath());
                        } else if (f.startsWith("composition.")) {
                            cache.put("composition", ff.getAbsolutePath());
                        }
                        cache.put(n, ff.getAbsolutePath());
                        cache.put(f, ff.getAbsolutePath());
                        //cache.put(n,ff.getAbsolutePath());
                        if (f.startsWith("keyboard_land.")) {
                            cache_land.put("keyboard", ff.getAbsolutePath());
                        } else if (f.startsWith("key_land.")) {
                            cache_land.put("key", ff.getAbsolutePath());
                        } else if (f.startsWith("background_land.")) {
                            cache_land.put("background", ff.getAbsolutePath());
                        } else if (f.startsWith("candidate_land.")) {
                            cache_land.put("candidate", ff.getAbsolutePath());
                        } else if (f.startsWith("composition_land.")) {
                            cache_land.put("composition", ff.getAbsolutePath());
                        } else if (f.contains("_land.")) {
                            cache_land.put(f.replace("_land.", "."), ff.getAbsolutePath());
                        }
                    }
                }
            }
        }

        dir = new File(userDataDir, Config.get().getTheme());
        if (dir.exists()) {
            File[] fs = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }

            });
            if (fs != null) {
                for (File ff : fs) {
                    String f = ff.getName();
                    f = f.toLowerCase();
                    if (f.startsWith("keyboard.")) {
                        cache.put("keyboard", ff.getAbsolutePath());
                    } else if (f.startsWith("key.")) {
                        cache.put("key", ff.getAbsolutePath());
                    } else if (f.startsWith("background.")) {
                        cache.put("background", ff.getAbsolutePath());
                    } else if (f.startsWith("candidate.")) {
                        cache.put("candidate", ff.getAbsolutePath());
                    } else if (f.startsWith("composition.")) {
                        cache.put("composition", ff.getAbsolutePath());
                    }
                    cache.put(f, ff.getAbsolutePath());

                    //加载横屏图片
                    if (f.startsWith("keyboard_land.")) {
                        cache_land.put("keyboard", ff.getAbsolutePath());
                    } else if (f.startsWith("key_land.")) {
                        cache_land.put("key", ff.getAbsolutePath());
                    } else if (f.startsWith("background_land.")) {
                        cache_land.put("background", ff.getAbsolutePath());
                    } else if (f.startsWith("candidate_land.")) {
                        cache_land.put("candidate", ff.getAbsolutePath());
                    } else if (f.startsWith("composition_land.")) {
                        cache_land.put("composition", ff.getAbsolutePath());
                    } else if (f.contains("_land.")) {
                        cache_land.put(f.replace("_land.", "."), ff.getAbsolutePath());
                    }
                }
            }
        }


        SharedPreferences pref = Function.getPref(context);
        if (!pref.getBoolean("custom_color", false))
            return;
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

        for (String key : color_keys) {
            colors.put(key, pref.getInt(key, 0));
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
            if (key.endsWith("_size")) {
                int f = pref.getInt(key, 0);
                if (f > 0) {
                    pixels.put(key, (float) f);
                }
            } else if (key.endsWith("_gap"))
                pixels.put(key, (float) pref.getInt(key, 0) / 20);
            else if (key.endsWith("_corner"))
                pixels.put(key, (float) pref.getInt(key, 0));
            else
                pixels.put(key, (float) pref.getInt(key, 0) / 10);
        }
        //Log.i("rime", "reset:init "+pkg+cache);
        //Log.i("rime", "backget "+pkg+cache);
    }

    public static LuaBitmapDrawable get(LuaContext context, String name) {
        name = name.toLowerCase();
        //if(name.contains("shift")) Log.i("rime", "backget "+name+";"+cache.get(name));
        if(Trime.getService()!=null&&Trime.getService().isLandscape()&&cache_land.containsKey(name)){
            return new LuaBitmapDrawable(context, cache_land.get(name));
        }
        if (cache.containsKey(name))
            return new LuaBitmapDrawable(context, cache.get(name));
        if (name.endsWith("_color")) {
            name = name.substring(0, name.length() - 6);
            return get(context, name);
        }
        return null;
    }

    public static Drawable get(LuaContext context, int name) {
        //Log.i("rime", "back get "+name+cache.get(name));
        if (icache.containsKey(name))
            return new LuaBitmapDrawable(context, icache.get(name));
        return null;
    }

    public static int get(String key) {
        //Log.i("rime", "back get "+key+colors.get(key));
        if (colors.containsKey(key))
            return colors.get(key);
        return 0;
    }

    public static Integer getColor(String key) {
        //Log.i("rime", "back get "+key+colors.get(key));
        if (colors.containsKey(key))
            return colors.get(key);
        return null;
    }

    public static Float getPixel(String key) {
        //Log.i("rime", "back get "+key+colors.get(key));
        if (pixels.containsKey(key))
            return pixels.get(key);
        return null;
    }
}
