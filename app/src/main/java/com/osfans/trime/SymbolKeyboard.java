package com.osfans.trime;

import android.content.Context;
import android.content.res.Resources;

import com.android.cglib.dx.rop.cst.CstArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nirenr on 2020/2/21.
 */

public class SymbolKeyboard extends Keyboard {

    private int width;
    private float height;
    private int currPage = 0;
    private int pageSize = 24;
    private List<String> cb=new ArrayList<>();

    public SymbolKeyboard(Context context, Map<String, Object> ks, int height) {
        super(context);
        Object k = ks.get("keys");
        if(k instanceof List)
            cb = (List<String>) k;
        else if(k instanceof Map){
            cb=new ArrayList(((Map)k).values());
        }
        width=Config.getInt(ks,"width",25);
        pageSize=100/width*6;
        this.height = height;
        int h = Math.max(Trime.getService().getHeight(), Trime.getService().getWidth());
        if (height < h / 8)
            height = h / 3;
        this.height = height / 7 / Resources.getSystem().getDisplayMetrics().scaledDensity;
        super.loadKey(getKeysMap());
    }

    @Override
    public void loadKey(Map<String, Object> ks) {
        Object k = ks.get("keys");
        if(k instanceof CstArray.List)
            cb = (List<String>) k;
        else if(k instanceof Map){
            cb=new ArrayList(((Map)k).values());
        }
        width=Config.getInt(ks,"width",25);
        pageSize=100/width*6;
        super.loadKey(getKeysMap());
    }

    @Override
    public List<Key> getKeys() {
        super.loadKey(getKeysMap());
        return super.getKeys();
    }

    private Map<String, Object> getKeysMap() {
        HashMap<String, Object> map = new HashMap<>();
        List<Map<String, Object>> keys = new ArrayList<>();
        Trime trime = Trime.getService();
        int len=0;
        if (trime != null) {
            len = cb.size() - currPage * pageSize;
            for (int i = 0; i < pageSize; i++) {
                HashMap<String, Object> key = new HashMap<>();
                if (i < len) {
                    String s = cb.get(i + currPage * pageSize);
                    if (Key.getPresetKeys().containsKey(s)) {
                        key.put("click", s);
                    } else if(s.endsWith(".lua")){
                        key.put("click", s);
                    } else {
                        if (s.length() > 6) {
                            key.put("label", s.trim());
                        } else {
                            key.put("label", s);
                        }
                        key.put("preview", s);
                        key.put("send", "3");
                        key.put("commit", s);
                        key.put("description", s);
                    }
                }
                key.put("functional", false);
                key.put("width", width);
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
        map.put("name", String.format("符号,第%d页", currPage + 1));
        map.put("lock", false);
        map.put("keys", keys);
        map.put("vertical_gap", 1);
        map.put("horizontal_gap", 1);
        map.put("type", 1);
        return map;
    }

    public boolean pageDown() {
        Trime trime = Trime.getService();
        if (trime != null) {
            int len = cb.size();
            if (len - pageSize * (currPage + 1) > 0) {
                currPage++;
            }
        }
        return true;
    }

    public boolean pageUp() {
        currPage--;
        if (currPage < 0)
            currPage = 0;
        return true;
    }

    public void reset() {
        currPage = 0;
    }
}
