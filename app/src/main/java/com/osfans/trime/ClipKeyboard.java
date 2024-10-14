package com.osfans.trime;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nirenr on 2019/1/20.
 */

public class ClipKeyboard extends Keyboard {

    private static int currPage=0;
    private static final int pageSize=12;
    private static final int keyWidth =50;
    private float height;

    public ClipKeyboard(Context context, int height) {
        super(context);
        this.height=height;
        int h = Math.max(Trime.getService().getHeight(),Trime.getService().getWidth());
        if(height<h/8)
            height=h/3;
        this. height = height / 7 / Resources.getSystem().getDisplayMetrics().scaledDensity;
        loadKey(getKeysMap());
    }

    @Override
    public List<Key> getKeys() {
        loadKey(getKeysMap());
        return super.getKeys();
    }

    private  Map<String, Object> getKeysMap() {
         HashMap<String, Object> map = new HashMap<>();
        List<Map<String, Object>> keys = new ArrayList<>();
        Trime trime = Trime.getService();
        int len=0;
        if (trime != null) {
            List<String> cb = trime.getClipBoard();
            len = cb.size()-currPage*pageSize;
            for (int i = 0; i < pageSize; i++) {
                HashMap<String, Object> key = new HashMap<>();
                if (i < len) {
                    String s = cb.get(i+currPage*pageSize);
                    if (s.length() > 6) {
                        key.put("label", s.trim());
                    }else {
                        key.put("label", s);
                    }
                    key.put("preview", s);
                    key.put("option", "0");
                    key.put("commit", s);
                    key.put("description", s);
                }
                key.put("functional", false);
                key.put("width", keyWidth);
                key.put("height", height);
                keys.add(key);
            }
        }
        HashMap<String, Object> key = new HashMap<>();
        key.put("hint",Integer.toString((int) Math.max(0,Math.ceil(len*1.0/pageSize)-1)));
        key.put("click", "Page_Down");
        key.put("width", 25);
        key.put("height", height);
        keys.add(key);
        key = new HashMap<>();
        key.put("hint",String.valueOf(currPage));
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
        map.put("name", String.format("剪切板,第%d页", currPage+1));
        map.put("lock", false);
        map.put("keys", keys);
        map.put("vertical_gap",1);
        map.put("horizontal_gap",1);
        map.put("type",1);
        return map;
    }

    public boolean pageDown() {
        Trime trime = Trime.getService();
        if (trime != null) {
            List<String> cb = trime.getClipBoard();
            int len = cb.size();
            if(len-pageSize*(currPage+1)>0){
                currPage++;
            }
        }
        //loadKey(getKeysMap());
        return true;
    }

    public boolean pageUp() {
        currPage--;
        if(currPage<0)
            currPage=0;
        //loadKey(getKeysMap());
        return true;
    }

    public static void reset() {
        currPage=0;
    }
}
