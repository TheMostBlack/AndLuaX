package com.osfans.trime;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nirenr on 2019/1/22.
 */

public class EditKeyboard extends Keyboard {

    public EditKeyboard(Context context, int height) {
        super(context, getKeysMap(height));
    }

    private static Map<String, Object> getKeysMap(float height) {
        height = height / 8 / Resources.getSystem().getDisplayMetrics().scaledDensity - Config.get().getPixel("vertical_gap")*2;
        HashMap<String, Object> map = new HashMap<>();
        List<Map<String, Object>> keys = new ArrayList<>();
        Trime trime = Trime.getService();
        if (trime != null) {
            List<String> cb = trime.getPhrase();
            int len = cb.size();
            for (int i = 0; i < 30; i++) {
                HashMap<String, Object> key = new HashMap<>();
                if (i < len) {
                    String s = cb.get(i);
                    if (s.length() > 6) {
                        key.put("label", s.trim());
                    }else {
                        key.put("label", s);
                    }
                    key.put("preview", s);
                    key.put("send", "1");
                    key.put("commit", s);
                    key.put("description", s);
                }
                key.put("width", 25);
                key.put("height", height);
                keys.add(key);
            }
        }
        HashMap<String, Object> key = new HashMap<>();
        key.put("click", "_add_phrase");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);
        key = new HashMap<>();
        key.put("click", "Keyboard_default");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);
        map.put("width", 25);
        map.put("height", height);
        map.put("name", "剪切板");
        map.put("lock", false);
        map.put("keys", keys);
        return map;
    }
}


