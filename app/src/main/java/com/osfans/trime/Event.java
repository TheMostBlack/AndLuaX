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

import android.view.KeyEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * {@link Key 按鍵}的各種事件（單擊、長按、滑動等）
 */
public class Event {
    private Integer index=0;
    private String TAG = "Event";
    private Keyboard mKeyboard;
    private int code = 0;
    private int mask = 0;
    private String text;
    private String label;
    private String description;
    private String preview;
    private List<String> states;
    private String command;
    private String option;
    private String select;
    private String toggle;
    private String commit;

    private String shiftLock;
    private boolean functional;
    private boolean repeatable;
    private boolean sticky;

    public Event(Keyboard keyboard, String s) {
        mKeyboard = keyboard;
        if (s.matches("\\{[^\\{\\}]+\\}")) { //{send|key}
            label = s.substring(1, s.length() - 1);
            int[] sends = parseSend(label); //send
            code = sends[0];
            mask = sends[1];
            if (code >= 0) return;
            s = label; //key
            label = null;
        }
        if (Key.presetKeys.containsKey(s)) {
            Map m = Key.presetKeys.get(s);
            command = Config.getString(m, "command");
            option = Config.getString(m, "option");
            select = Config.getString(m, "select");
            toggle = Config.getString(m, "toggle");
            label = Config.getString(m, "label");
            preview = Config.getString(m, "preview");
            description = Config.getString(m, "description");
            shiftLock = Config.getString(m, "shift_lock");
            commit = Config.getString(m, "commit");
            String send = Config.getString(m, "send");
            if (Function.isEmpty(send) && !Function.isEmpty(command))
                send = "function"; //command默認發function
            int[] sends = parseSend(send);
            code = sends[0];
            mask = sends[1];
            parseLabel();
            text = Config.getString(m, "text");
            if (code < 0 && Function.isEmpty(text)) text = s;
            if (m.containsKey("states")) states = (List<String>) m.get("states");
            sticky = Config.getBoolean(m, "sticky", false);
            repeatable = Config.getBoolean(m, "repeatable", false);
            functional = Config.getBoolean(m, "functional", true);
        } else if ((code = getClickCode(s)) >= 0) {
            parseLabel();
        } else if (s.endsWith(".lua")) {
            String send = "function";
            int[] sends = parseSend(send);
            code = sends[0];
            mask = sends[1];
            command=s;
            s=new File(s).getName();
            label=s.substring(0,s.length()-4);
            option="";
        } else {
            text = s;
            label = s.replaceAll("\\{[^\\{\\}]+?\\}", "");
        }
    }

    public Event(Keyboard keyboard, Map<String, Object> m) {
        mKeyboard = keyboard;
        command = Config.getString(m, "command");
        index = Config.getInt(m, "index",0);
        option = Config.getString(m, "option");
        select = Config.getString(m, "select");
        toggle = Config.getString(m, "toggle");
        label = Config.getString(m, "label");
        preview = Config.getString(m, "preview");
        description = Config.getString(m, "description");
        shiftLock = Config.getString(m, "shift_lock");
        commit = Config.getString(m, "commit");
        String send = Config.getString(m, "send");
        if (Function.isEmpty(send) && !Function.isEmpty(command))
            send = "function"; //command默認發function
        int[] sends = parseSend(send);
        code = sends[0];
        mask = sends[1];
        parseLabel();
        text = Config.getString(m, "text");

        if (m.containsKey("states")) states = (List<String>) m.get("states");
        sticky = Config.getBoolean(m, "sticky", false);
        repeatable = Config.getBoolean(m, "repeatable", false);
        functional = Config.getBoolean(m, "functional", true);
    }

    public Event(String s) {
        this(null, s);
    }
    public Event(HashMap<String, Object> s) {
        this(null, s);
    }

    public int getCode() {
        return code;
    }
    public int getIndex() {
        return index;
    }

    public int getMask() {
        return mask;
    }

    public String getCommand() {
        return command;
    }

    public String getOption() {
        return option;
    }

    public String getSelect() {
        return select;
    }

    public boolean isFunctional() {
        return functional;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public boolean isSticky() {
        return sticky;
    }

    public String getShiftLock() {
        return shiftLock;
    }

    public static int[] parseSend(String s) {
        int[] sends = new int[2];
        if (Function.isEmpty(s)) return sends;
        String codes;
        if (!s.contains("+")) codes = s;
        else {
            String[] ss = s.split("\\+");
            int n = ss.length;
            for (int i = 0; i < n - 1; i++)
                if (masks.containsKey(ss[i])) sends[1] |= masks.get(ss[i]);
            codes = ss[n - 1];
        }
        sends[0] = Key.androidKeys.indexOf(codes);
        return sends;
    }

    private String adjustCase(String s) {
        if (Function.isEmpty(s)) return "";
        if (s.length() == 1 && mKeyboard != null && mKeyboard.isShifted())
            s = s.toUpperCase(Locale.getDefault());
        else if (s.length() == 1
                && mKeyboard != null
                && !Rime.isAsciiMode()
                && mKeyboard.isLabelUppercase()) s = s.toUpperCase(Locale.getDefault());
        return s;
    }

    public String getLabel() {
        if (!Function.isEmpty(toggle)) return states.get(Rime.getOption(toggle) ? 1 : 0);
        return adjustCase(label);
    }

    public String getUnToggleLabel() {
        if (!Function.isEmpty(toggle)) return "/"+states.get(Rime.getOption(toggle) ? 0 : 1);
        return null;
    }
    public String getText() {
        String s = "";
        if (!Function.isEmpty(text)) s = text;
        else if (mKeyboard != null
                && mKeyboard.isShifted()
                && mask == 0
                && code >= KeyEvent.KEYCODE_A
                && code <= KeyEvent.KEYCODE_Z) s = label;
        return adjustCase(s);
    }

    public String getCommit() {
        return commit;
    }

    public String getPreviewText() {
        if (!Function.isEmpty(preview)) return preview;
        return getLabel();
    }

    public String getToggle() {
        if (!Function.isEmpty(toggle)) return toggle;
        return "ascii_mode";
    }

    private void parseLabel() {
        if (!Function.isEmpty(label)) return;
        int c = code;
        if (c == KeyEvent.KEYCODE_SPACE) {
            label = Rime.getSchemaName();
        } else {
            if (c > 0) label = getDisplayLabel(c);
        }
    }

    public static String getDisplayLabel(int keyCode) {
        String s = "";
        if (keyCode < Key.getSymbolStart()) { //字母數字
            if (Key.getKcm().isPrintingKey(keyCode)) {
                char c = Key.getKcm().getDisplayLabel(keyCode);
                if (Character.isUpperCase(c)) c = Character.toLowerCase(c);
                s = String.valueOf(c);
            } else {
                s = Key.androidKeys.get(keyCode);
            }
        } else if (keyCode < Key.androidKeys.size()) { //可見符號
            keyCode -= Key.getSymbolStart();
            s = Key.getSymbols().substring(keyCode, keyCode + 1);
        }
        return s;
    }

    public static int getClickCode(String s) {
        int keyCode = -1;
        if (Function.isEmpty(s)) { //空鍵
            keyCode = 0;
        } else if (Key.androidKeys.contains(s)) { //字母數字
            keyCode = Key.androidKeys.indexOf(s);
        } else if (Key.getSymbols().contains(s)) { //可見符號
            keyCode = Key.getSymbolStart() + Key.getSymbols().indexOf(s);
        } else if (symbolAliases.containsKey(s)) {
            keyCode = symbolAliases.get(s);
        }
        return keyCode;
    }

    private static int getRimeCode(int code) {
        int i = 0;
        if (code >= 0 && code < Key.androidKeys.size()) {
            String s = Key.androidKeys.get(code);
            i = Rime.get_keycode_by_name(s);
        }
        return i;
    }

    public static boolean hasModifier(int mask, int modifier) {
        return (mask & modifier) > 0;
    }

    public static int[] getRimeEvent(int code, int mask) {
        int i = getRimeCode(code);
        int m = 0;
        if (hasModifier(mask, KeyEvent.META_SHIFT_ON)) m |= Rime.META_SHIFT_ON;
        if (hasModifier(mask, KeyEvent.META_CTRL_ON)) m |= Rime.META_CTRL_ON;
        if (hasModifier(mask, KeyEvent.META_ALT_ON)) m |= Rime.META_ALT_ON;
        if (mask == Rime.META_RELEASE_ON) m |= Rime.META_RELEASE_ON;
        return new int[]{i, m};
    }

    private static Map<String, Integer> masks =
            new HashMap<String, Integer>() {
                {
                    put("Shift", KeyEvent.META_SHIFT_ON);
                    put("Control", KeyEvent.META_CTRL_ON);
                    put("Alt", KeyEvent.META_ALT_ON);
                }
            };

    private static Map<String, Integer> symbolAliases =
            new HashMap<String, Integer>() {
                {
                    put("#", KeyEvent.KEYCODE_POUND);
                    put("'", KeyEvent.KEYCODE_APOSTROPHE);
                    put("(", KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN);
                    put(")", KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN);
                    put("*", KeyEvent.KEYCODE_STAR);
                    put("+", KeyEvent.KEYCODE_PLUS);
                    put(",", KeyEvent.KEYCODE_COMMA);
                    put("-", KeyEvent.KEYCODE_MINUS);
                    put(".", KeyEvent.KEYCODE_PERIOD);
                    put("/", KeyEvent.KEYCODE_SLASH);
                    put(";", KeyEvent.KEYCODE_SEMICOLON);
                    put("=", KeyEvent.KEYCODE_EQUALS);
                    put("@", KeyEvent.KEYCODE_AT);
                    put("\\", KeyEvent.KEYCODE_BACKSLASH);
                    put("[", KeyEvent.KEYCODE_LEFT_BRACKET);
                    put("`", KeyEvent.KEYCODE_GRAVE);
                    put("]", KeyEvent.KEYCODE_RIGHT_BRACKET);
                }
            };

    public String getDescription() {
        if (!Function.isEmpty(description))
            return description;
        return getLabel();
    }
}
