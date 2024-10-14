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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.luajava.LuaTable;
import com.osfans.trime.pro.BuildConfig;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 管理多個{@link Keyboard 鍵盤}
 */
class KeyboardSwitch {

    private final Trime context;

    private Keyboard[] mKeyboards;
    private List<String> mKeyboardNames;
    private int currentId, lastId, lastLockId;
    private int currentDisplayWidth;
    private int asciiKeyboardId;
    private int mKeyboardClip;
    private int mKeyboardPhrase;
    private int mKeyboardCandidate;
    private int mKeyboardCustom;
    private int mKeyboardSymbol;

    public KeyboardSwitch(Trime context) {
        this.context = context;
        currentId = -1;
        lastId = 0;
        lastLockId = 0;
        //reset();
    }

    public void reset() {
        Log.i("rime", "reset: keyboard");
        mKeyboardNames = Config.get().getKeyboardNames();
        if (BuildConfig.DEBUG) {
            try {
                throw new RuntimeException("reset: keyboard " + mKeyboardNames);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("rime", "reset: keyboard " + mKeyboardNames);
        }
        mKeyboardNames.add("_custom_board");
        mKeyboardCustom = mKeyboardNames.size() - 1;
        mKeyboardNames.add("_symbol_board");
        mKeyboardSymbol = mKeyboardNames.size() - 1;
        mKeyboardNames.add("_phrase_board");
        mKeyboardPhrase = mKeyboardNames.size() - 1;
        mKeyboardNames.add("_clip_board");
        mKeyboardClip = mKeyboardNames.size() - 1;
        mKeyboardNames.add("_candidate_board");
        int n = mKeyboardNames.size();
        mKeyboardCandidate = n - 1;
        mKeyboards = new Keyboard[n];
        /*for (int i = 0; i < n; i++) {
            mKeyboards[i] = new Keyboard(context, mKeyboardNames.get(i));
        }*/
        setKeyboard(0);
        Log.i("rime", "reset: keyboard");
        Log.i("rime", "setKeyboard " + mKeyboardNames);
    }

    public Keyboard getKeyboard(int i) {
        if (mKeyboards == null)
            reset();
        if (i < 0)
            i = 0;
        if (i > mKeyboards.length)
            i = mKeyboards.length - 1;

        Keyboard kb = mKeyboards[i];
        if (kb != null)
            return kb;
        if (i == mKeyboardCandidate)
            return new CandidateKeyboard(context, getKeyboard(0).getKeyBoardHeight());
        if (i == mKeyboardCustom) {
            kb = new Keyboard(context);
        } else if (i == mKeyboardSymbol) {
            kb = new SymbolKeyboard(context, new HashMap<String, Object>(), getKeyboard(0).getKeyBoardHeight());
        } else if (i == mKeyboardClip) {
            kb = new ClipKeyboard(context, getKeyboard(0).getKeyBoardHeight());
        } else if (i == mKeyboardPhrase) {
            kb = new PhraseKeyboard(context, getKeyboard(0).getKeyBoardHeight());
        } else {
            Map<String, Object> ks = Config.get().getKeyboard(mKeyboardNames.get(i));
            if (ks == null)
                kb = new Keyboard(context, mKeyboardNames.get(i));
            else if (ks.containsKey("type")) {
                String type = ks.get("type").toString();
                if (type.equals("long") || type.equals("page")) {
                    kb = new SymbolKeyboard(context, ks, getKeyboard(0).getKeyBoardHeight());
                } else {
                    kb = new Keyboard(context, ks);
                }
            } else {
                kb = new Keyboard(context, ks);
            }
        }
        mKeyboards[i] = kb;
        return kb;
    }

    public void setKeyboard(String name) {
        Log.i("rime", "setKeyboard " + name);
        int i = 0;
        if (context.isLandscape() && !TextUtils.isEmpty(name) && name.startsWith(".") && hasKeyboard(name + "_land"))
            name = name + "_land";
        asciiKeyboardId = -1;
        String fn = name;
        if (TextUtils.isEmpty(fn))
            fn = ".default";
        if (fn.equals(".default")) {
            if (new File(context.getLuaExtDir("keyboards"), Rime.getSchemaId() + ".lua").exists())
                fn = Rime.getSchemaId();
        }
        File path = new File(context.getLuaExtDir("keyboards"), fn + ".lua");
        if (path.exists()) {
            Object ret = context.doFile(path.getAbsolutePath());
            if (ret instanceof LuaTable) {
                Map<String, Object> map = (Map<String, Object>) ret;
                Keyboard mKeyboard;
                if (Objects.equals(map.get("type"), "long")) {
                    currentId = mKeyboardSymbol;
                } else {
                    currentId = mKeyboardCustom;
                }
                mKeyboard = getKeyboard(currentId);
                mKeyboard.loadKey(map);
                Trime.getService().setKeyboard((View) null);
                Log.i("rime", "setKeyboard lua");
                return;
            } else if(ret instanceof View){
                currentId = mKeyboardCustom;
                Trime.getService().setKeyboard((View) ret);
                return;
            }
        }
        Trime.getService().setKeyboard((View) null);

        if (isValidId(currentId)) i = currentId;
        if (Function.isEmpty(name)) {
             if (!getKeyboard(i).isLock())
                i = lastLockId; //不記憶鍵盤時使用默認鍵盤
        } else if (name.contentEquals(".default")) {
            i = 0;
            ClipKeyboard.reset();
            PhraseKeyboard.reset();
        } else if (name.contentEquals(".default_land")) {
            i = 1;
            ClipKeyboard.reset();
            PhraseKeyboard.reset();
        } else if (name.contentEquals(".prior")) { //前一個
            i = currentId - 1;
        } else if (name.contentEquals(".next")) { //下一個
            i = currentId + 1;
        } else if (name.contentEquals(".last")) { //最近一個
            i = lastId;
        } else if (name.contentEquals(".last_lock")) { //最近一個Lock鍵盤
            i = lastLockId;
        } else if (name.contentEquals(".ascii")) { //英文鍵盤
            String asciiKeyboard = getKeyboard(i).getAsciiKeyboard();
            if (!Function.isEmpty(asciiKeyboard)) {
                i = mKeyboardNames.indexOf(asciiKeyboard);
                asciiKeyboardId = i;
            }
        } else {
            i = mKeyboardNames.indexOf(name); //指定鍵盤
        }
        if (context.isLandscape() && i == 0)
            i = 1;
        Log.i("rime", "setKeyboard " + i);
        setKeyboard(i);
    }

    private boolean isValidId(int i) {
        return i >= 0 && i < mKeyboards.length;
    }

    private void setKeyboard(int i) {
        if (!isValidId(i)) i = 0;
        lastId = currentId;
        if (isValidId(lastId)) {
            if (getKeyboard(lastId).isLock()) lastLockId = lastId;
        }
        currentId = i;
    }

    public void init(int displayWidth) {
        if ((currentId >= 0) && (displayWidth == currentDisplayWidth)) {
            return;
        }
        currentDisplayWidth = displayWidth;
        reset();
    }

    public String getCurrentKeyboardName() {
        return mKeyboardNames.get(currentId);
    }

    public int getCurrentKeyboardId() {
        return currentId;
    }

    public int getCurrentAsciiKeyboardId() {
        return asciiKeyboardId;
    }

    public Keyboard getCurrentKeyboard() {
        return getKeyboard(currentId);
    }

    public boolean getAsciiMode() {
        return getCurrentKeyboard().getAsciiMode();
    }

    public boolean hasKeyboard(String name) {
        return mKeyboardNames.contains(name);
    }
}
