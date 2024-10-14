package com.osfans.trime;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nirenr on 2019/3/2.
 */

public class CandidateKeyboard extends Keyboard {

    public CandidateKeyboard(Context context, int height) {
        super(context, getKeysMap(height));
    }

    private static Map<String, Object> getKeysMap2(float mHeight) {
        int h = Math.max(Trime.getService().getHeight(), Trime.getService().getWidth());
        if (mHeight < h / 8)
            mHeight = h / 3;
        float height = mHeight / 7 / Resources.getSystem().getDisplayMetrics().scaledDensity;
        Config config = Config.get();
        int mKeyTextSize = config.getInt("key_text_size");
        int mLabelTextSize = config.getInt("key_long_text_size");
        if (mLabelTextSize == 0) mLabelTextSize = mKeyTextSize;
        if (KeyboardView.sCandidateKeyboardTextSize > 0)
            mKeyTextSize = KeyboardView.sCandidateKeyboardTextSize;

        HashMap<String, Object> map = new HashMap<>();
        List<Map<String, Object>> keys = new ArrayList<>();
        Trime trime = Trime.getService();
        int max = 0;
        int line = 0;
        if (trime != null) {
            Rime.RimeCandidate[] cb = Rime.getCandidates();
            if (cb == null)
                cb = new Rime.RimeCandidate[0];
            int len = 0;
            if (cb.length != 0)
                len = cb.length;
            for (int i = 0; i < 30; i++) {
                HashMap<String, Object> key = new HashMap<>();
                key.put("width", 20);
                int width = 20;
                if (i < len) {
                    String s = cb[i].text;
                    int sl = s.length();
                    if (sl > 6) {
                        key.put("label", s.trim());
                    } else {
                        key.put("label", s);
                    }
                    key.put("hint", cb[i].comment);
                    key.put("key_text_size", mKeyTextSize);
                    key.put("preview", s);
                    key.put("option", "2");
                    key.put("commit", s);
                    key.put("description", s);
                    key.put("index", i);
                    if (sl >= 8)
                        width = 100;
                    else if (sl >= 4)
                        width = 50;
                    key.put("width", width);
                } else {
                    if (line >= 5)
                        break;
                }
                max += width;
                if (max >= 100) {
                    max = 0;
                    line++;
                }
                key.put("functional", false);
                key.put("height", height);
                keys.add(key);
            }
        }

        HashMap<String, Object> key;
        if (max > 0) {
            key = new HashMap<>();
            key.put("width", 100 - max);
            key.put("height", height);
            keys.add(key);
        }

        key = new HashMap<>();
        key.put("click", "Page_Down");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);
        key = new HashMap<>();
        key.put("click", "Page_Up");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);
        key = new HashMap<>();
        key.put("click", "BackSpace");
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
        map.put("name", "候选键盘");
        map.put("lock", false);
        map.put("keys", keys);
        map.put("vertical_gap", 1);
        map.put("horizontal_gap", 1);
        //map.put("keyboard_height", mHeight);
        map.put("type", "scroll");
        return map;
    }

    private static Map<String, Object> getKeysMap(float mHeight) {
        int h = Math.max(Trime.getService().getHeight(), Trime.getService().getWidth());
        if (mHeight < h / 8)
            mHeight = h / 3;
        float height = mHeight / 7 / Resources.getSystem().getDisplayMetrics().scaledDensity;
        Config config = Config.get();
        int mKeyTextSize = config.getInt("key_text_size");
        int mLabelTextSize = config.getInt("key_long_text_size");
        if (mLabelTextSize == 0) mLabelTextSize = mKeyTextSize;
        if (KeyboardView.sCandidateKeyboardTextSize > 0)
            mKeyTextSize = KeyboardView.sCandidateKeyboardTextSize;

        HashMap<String, Object> map = new HashMap<>();
        List<Map<String, Object>> keys = new ArrayList<>();
        Trime trime = Trime.getService();
        if (trime != null) {
            Rime.RimeCandidate[] cb = Rime.getCandidates();
            if (cb == null)
                cb = new Rime.RimeCandidate[0];
            int len = 0;
            if (cb.length != 0)
                len = cb.length;
            int ssl = 0;
            if (len > 1) {
                ssl = cb[0].text.length();
            }
            int start = 0;
            if (ssl > 8) {
                start = 5;
                height = mHeight / 9 / Resources.getSystem().getDisplayMetrics().scaledDensity;
                for (int i = 0; i < 1; i++) {
                    HashMap<String, Object> key = new HashMap<>();
                    if (i < len) {
                        String s = cb[i].text;
                        if (s.length() > 6) {
                            key.put("label", s.trim());
                        } else {
                            key.put("label", s);
                        }
                        key.put("hint", cb[i].comment);
                        key.put("preview", s);
                        key.put("option", "2");
                        key.put("commit", s);
                        key.put("description", s);
                        key.put("index", i);
                    }
                    key.put("key_text_size", mKeyTextSize);
                    key.put("functional", false);
                    key.put("width", 100);
                    key.put("height", height);
                    keys.add(key);
                }
                for (int i = 1; i < start; i++) {
                    HashMap<String, Object> key = new HashMap<>();
                    if (i < len) {
                        String s = cb[i].text;
                        if (s.length() > 6) {
                            key.put("label", s.trim());
                        } else {
                            key.put("label", s);
                        }
                        key.put("hint", cb[i].comment);
                        key.put("preview", s);
                        key.put("option", "2");
                        key.put("commit", s);
                        key.put("description", s);
                        key.put("index", i);
                    }
                    key.put("key_text_size", mKeyTextSize);
                    key.put("functional", false);
                    key.put("width", 50);
                    key.put("height", height);
                    keys.add(key);
                }
            } else if (ssl > 3) {
                start = 5;
                height = mHeight / 8 / Resources.getSystem().getDisplayMetrics().scaledDensity;
                for (int i = 0; i < 2; i++) {
                    HashMap<String, Object> key = new HashMap<>();
                    if (i < len) {
                        String s = cb[i].text;
                        if (s.length() > 6) {
                            key.put("label", s.trim());
                        } else {
                            key.put("label", s);
                        }
                        key.put("hint", cb[i].comment);
                        key.put("preview", s);
                        key.put("option", "2");
                        key.put("commit", s);
                        key.put("description", s);
                        key.put("index", i);
                    }
                    key.put("key_text_size", mKeyTextSize);
                    key.put("functional", false);
                    key.put("width", 50);
                    key.put("height", height);
                    keys.add(key);
                }
                for (int i = 2; i < start; i++) {
                    HashMap<String, Object> key = new HashMap<>();
                    if (i < len) {
                        String s = cb[i].text;
                        if (s.length() > 6) {
                            key.put("label", s.trim());
                        } else {
                            key.put("label", s);
                        }
                        key.put("hint", cb[i].comment);
                        key.put("preview", s);
                        key.put("option", "2");
                        key.put("commit", s);
                        key.put("description", s);
                        key.put("index", i);
                    }
                    key.put("key_text_size", mKeyTextSize);
                    key.put("functional", false);
                    key.put("width", 33);
                    key.put("height", height);
                    keys.add(key);
                }
            }

            for (int i = start; i < 30; i++) {
                HashMap<String, Object> key = new HashMap<>();
                if (i < len) {
                    String s = cb[i].text;
                    int sl = s.length();
                    if (sl > 6) {
                        key.put("label", s.trim());
                    } else {
                        key.put("label", s);
                    }
                    key.put("hint", cb[i].comment);
                    key.put("key_text_size", mKeyTextSize);
                    key.put("preview", s);
                    key.put("option", "2");
                    key.put("commit", s);
                    key.put("description", s);
                    key.put("index", i);
                }
                key.put("functional", false);
                key.put("width", 20);
                key.put("height", height);
                keys.add(key);
            }
        }
        HashMap<String, Object> key;

        key = new HashMap<>();
        key.put("click", "Page_Down");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);
        key = new HashMap<>();
        key.put("click", "Page_Up");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);
        key = new HashMap<>();
        key.put("click", "BackSpace");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);
        key = new HashMap<>();
        key.put("click", "Keyboard_default");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);


        /*if (Rime.hasRight()&&Rime.hasLeft()) {
            key = new HashMap<>();
            key.put("click", "Page_Down");
            key.put("width", 25);
            key.put("height", height);
            keys.add(key);
            key = new HashMap<>();
            key.put("click", "Page_Up");
            key.put("width", 25);
            key.put("height", height);
            keys.add(key);
        } else if (Rime.hasRight()) {
            key = new HashMap<>();
            key.put("click", "Page_Down");
            key.put("width", 25);
            key.put("height", height);
            keys.add(key);
            key = new HashMap<>();
            key.put("click", "Keyboard_default");
            key.put("width", 25);
            key.put("height", height);
            keys.add(key);
        } else if (Rime.hasLeft()) {
            key = new HashMap<>();
            key.put("click", "Page_Up");
            key.put("width", 25);
            key.put("height", height);
            keys.add(key);
            key = new HashMap<>();
            key.put("click", "Keyboard_default");
            key.put("width", 25);
            key.put("height", height);
            keys.add(key);
        } else {
            key = new HashMap<>();
            key.put("functional", false);
            key.put("width", 25);
            key.put("height", height);
            keys.add(key);
            key = new HashMap<>();
            key.put("click", "Keyboard_default");
            key.put("width", 25);
            key.put("height", height);
            keys.add(key);
        }*/


        map.put("width", 25);
        map.put("height", height);
        map.put("name", "候选键盘");
        map.put("lock", false);
        map.put("keys", keys);
        map.put("vertical_gap", 1);
        map.put("horizontal_gap", 1);
        map.put("type", 1);
        return map;
    }
}
