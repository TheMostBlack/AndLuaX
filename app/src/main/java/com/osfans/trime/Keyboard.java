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

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 從YAML中加載鍵盤配置，包含多個{@link Key 按鍵}。
 */
public class Keyboard {
    public static final int EDGE_LEFT = 0x01;
    public static final int EDGE_RIGHT = 0x02;
    public static final int EDGE_TOP = 0x04;
    public static final int EDGE_BOTTOM = 0x08;
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 5;
    private static final int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;
    private static final String TAG = Keyboard.class.getSimpleName();
    /**
     * Number of key widths from current touch point to search for nearest keys.
     */
    public static float SEARCH_DISTANCE = 1.4f;
    private final boolean SHOW_BOTTOM_KEY;
    private static int mLastTotalHeight;
    private final boolean SHOW_TOP_KEY;
    private final Context mContext;
    private float mWidthScale = 0;
    private float[] mRoundCorners;
    private int mKeyBoardTotalHeight;
    private String mName;
    private float mHeightScale = 1;
    /**
     * 按鍵默認水平間距
     */
    private int mDefaultHorizontalGap;
    /**
     * 默認鍵寬
     */
    private int mDefaultWidth;
    /**
     * 默認鍵高
     */
    private int mDefaultHeight;
    /**
     * 默認行距
     */
    private int mDefaultVerticalGap;
    /**
     * 默認按鍵圓角半徑
     */
    private float mRoundCorner;
    /**
     * 鍵盤背景
     */
    private Drawable mBackground;
    /**
     * 鍵盤的Shift鍵是否按住
     */
    private boolean mShifted;
    /**
     * 鍵盤的Shift鍵
     */
    private Key mShiftKey;
    /**
     * Total height of the keyboard, including the padding and keys
     */
    private int mTotalHeight;
    /**
     * Total width of the keyboard, including left side gaps and keys, but not any gaps on the right
     * side.
     */
    private int mTotalWidth;
    /**
     * List of keys in this keyboard
     */
    private List<Key> mKeys;

    private List<Key> mComposingKeys;
    private int mMetaState;
    /**
     * Width of the screen available to fit the keyboard
     */
    private int mDisplayWidth;
    /**
     * Keyboard mode, or zero, if none.
     */
    private int mAsciiMode;

    // Variables for pre-computing nearest keys.
    private String mLabelTransform;
    private int mCellWidth;
    private int mCellHeight;
    private int[][] mGridNeighbors;
    private int mProximityThreshold;

    private boolean mLock; //切換程序時記憶鍵盤
    private String mAsciiKeyboard; //英文鍵盤
    private String mType;
    private Integer mHeight;

    /**
     * Creates a keyboard from the given xml key layout file.
     *
     * @param context the application or service context
     */
    public Keyboard(Context context) {
        mContext = context;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mDisplayWidth = Trime.getService().getWidth();
        /* Height of the screen */
        int mDisplayHeight = Trime.getService().getHeight();
        Log.i(TAG, "keyboard's display metrics:" + dm.toString());

        Config config = Config.get();
        mHeightScale = config.getKeyboardHeight();
        mWidthScale = config.getKeyboardWidth();
        //mDisplayWidth= (int) (mDisplayWidth*mWidthScale);
        /*if(mDefaultWidth>mDisplayHeight)
            mHeightScale=mHeightScale*0.5f;*/
        mDefaultHorizontalGap = (int) (config.getPixel("horizontal_gap") * mHeightScale);
        mDefaultVerticalGap = config.getPixel("vertical_gap");
        mDefaultWidth = (int) (mDisplayWidth * config.getDouble("key_width") / 100);
        mDefaultHeight = (int) (config.getPixel("key_height") * mHeightScale);
        mProximityThreshold = (int) (mDefaultWidth * SEARCH_DISTANCE);
        mProximityThreshold = mProximityThreshold * mProximityThreshold; // Square it for comparison
        //mRoundCorner = config.getFloat("round_corner");
        Object obj = config.getValue("round_corner");
        if (obj != null) {
            if (obj instanceof List) {
                List list = (List) obj;
                mRoundCorners = new float[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    mRoundCorners[i] = Float.valueOf(list.get(i).toString());
                }
            } else {
                mRoundCorner = Float.valueOf(obj.toString());
                Float f = BackUtil.getPixel("key_round_corner");
                if (f != null)
                    mRoundCorner = f;
            }
        }


        mBackground = config.getColorDrawable("keyboard_back_color");
        SHOW_BOTTOM_KEY = config.isShowBottomKey();
        SHOW_TOP_KEY = config.isShowTopKey();

        mKeys = new ArrayList<Key>();
        mComposingKeys = new ArrayList<Key>();

    }

    /**
     * Creates a blank keyboard from the given resource file and populates it with the specified
     * characters in left-to-right, top-to-bottom fashion, using the specified number of columns.
     * <p>
     * <p>
     * <p>
     * <p>If the specified number of columns is -1, then the keyboard will fit as many keys as
     * possible in each row.
     *
     * @param context           the application or service context
     * @param characters        the list of characters to display on the keyboard. One key will be created
     *                          for each character.
     * @param columns           the number of columns of keys to display. If this number is greater than the
     *                          number of keys that can fit in a row, it will be ignored. If this number is -1, the
     *                          keyboard will fit as many keys as possible in each row.
     * @param horizontalPadding 按鍵水平間距
     */
    public Keyboard(Context context, CharSequence characters, int columns, int horizontalPadding) {
        this(context);
        int x = 0;
        int y = 0;
        int column = 0;
        mTotalWidth = 0;

        final int maxColumns = columns == -1 ? Integer.MAX_VALUE : columns;
        for (int i = 0; i < characters.length(); i++) {
            char c = characters.charAt(i);
            if (column >= maxColumns || x + mDefaultWidth + horizontalPadding > mDisplayWidth) {
                x = 0;
                y += mDefaultVerticalGap + mDefaultHeight;
                column = 0;
            }
            final Key key = new Key(this);
            key.setX(x);
            key.setY(y);
            key.setWidth(mDefaultWidth);
            key.setHeight(mDefaultHeight);
            key.setGap(mDefaultHorizontalGap);
            key.events[0] = new Event(this, String.valueOf(c));
            column++;
            x += key.getWidth() + key.getGap();
            mKeys.add(key);
            if (x > mTotalWidth) {
                mTotalWidth = x;
            }
        }
        mTotalHeight = y + mDefaultHeight;
    }

    public Keyboard(Context context, List keys, int columns, int horizontalPadding) {
        this(context);
        int x = 0;
        int y = 0;
        int column = 0;
        mTotalWidth = 0;

        final int maxColumns = columns == -1 ? Integer.MAX_VALUE : columns;
        for (int i = 0; i < keys.size(); i++) {
            Object c = keys.get(i);
            if (column >= maxColumns || x + mDefaultWidth + horizontalPadding > mDisplayWidth) {
                x = 0;
                y += mDefaultVerticalGap + mDefaultHeight;
                column = 0;
            }
            final Key key = new Key(this);
            key.setX(x);
            key.setY(y);
            key.setWidth(mDefaultWidth);
            key.setHeight(mDefaultHeight);
            key.setGap(mDefaultHorizontalGap);
            key.events[0] = new Event(this, String.valueOf(c));
            column++;
            x += key.getWidth() + key.getGap();
            mKeys.add(key);
            if (x > mTotalWidth) {
                mTotalWidth = x;
            }
        }
        mTotalHeight = y + mDefaultHeight;
    }

    public Keyboard(Context context, String name) {
        this(context, Config.get().getKeyboard(name));
    }

    public Keyboard(Context context, Map<String, Object> m) {
        this(context);
        loadKey(m);
    }

    public void loadKey(Map<String, Object> m) {
        mKeys.clear();
        mComposingKeys.clear();
        mGridNeighbors = null;
        Context context = mContext;
        mName = (String) m.get("name");
        Object t = m.get("type");
        mType = null;
        if (t instanceof String)
            mType = t.toString();
        boolean absolute = "absolute".equals(mType);
        mHeight = Config.getPixel(m, "keyboard_height", 0);
        mLabelTransform = Config.getString(m, "label_transform", "none");
        mAsciiMode = Config.getInt(m, "ascii_mode", 1);
        if (mAsciiMode == 0) mAsciiKeyboard = Config.getString(m, "ascii_keyboard");
        mLock = Config.getBoolean(m, "lock", false);
        int columns = Config.getInt(m, "columns", 30);
        int defaultWidth = (int) (Config.getDouble(m, "width", 0) * mDisplayWidth / 100);
        if (defaultWidth == 0) defaultWidth = mDefaultWidth;
        float height = Config.getFloatPixel(m, "height", 0) * mHeightScale;
        float defaultHeight = (height > 0) ? height : mDefaultHeight;
        float rowHeight = defaultHeight - mDefaultVerticalGap;


        if (m.containsKey("horizontal_gap"))
            mDefaultHorizontalGap = (int) (Config.getPixel(m, "horizontal_gap") * mHeightScale);
        if (m.containsKey("vertical_gap")) mDefaultVerticalGap = Config.getPixel(m, "vertical_gap");
        if (m.containsKey("round_corner")) mRoundCorner = Config.getFloat(m, "round_corner");
        if (m.containsKey("keyboard_back_color")) {
            Drawable background = Config.getColorDrawable(m, "keyboard_back_color");
            if (background != null) mBackground = background;
        }
        int x = mDefaultHorizontalGap / 2;
        float y = mDefaultVerticalGap / 2;
        int row = 0;
        int column = 0;
        mTotalWidth = 0;
        int key_text_offset_x,
                key_text_offset_y,
                key_symbol_offset_x,
                key_symbol_offset_y,
                key_hint_offset_x,
                key_hint_offset_y,
                key_press_offset_x,
                key_press_offset_y;
        key_text_offset_x = Config.getPixel(m, "key_text_offset_x", 0);
        key_text_offset_y = Config.getPixel(m, "key_text_offset_y", 0);
        key_symbol_offset_x = Config.getPixel(m, "key_symbol_offset_x", 0);
        key_symbol_offset_y = Config.getPixel(m, "key_symbol_offset_y", 0);
        key_hint_offset_x = Config.getPixel(m, "key_hint_offset_x", 0);
        key_hint_offset_y = Config.getPixel(m, "key_hint_offset_y", 0);
        key_press_offset_x = Config.getInt(m, "key_press_offset_x", 0);
        key_press_offset_y = Config.getInt(m, "key_press_offset_y", 0);

        final int maxColumns = columns == -1 ? Integer.MAX_VALUE : columns;
        Object keys = m.get("keys");
        if (keys instanceof String) {
            loadKey((String) keys, maxColumns);
            return;
        }
        if (SHOW_TOP_KEY && !m.containsKey("type")) {
            String text = Function.getPref(context).getString("custom_top_key", "1|2|3|4|5|6|7|8|9|0");
            String[] cs = text.split("\\|");
            if (cs.length == 0 || TextUtils.isEmpty(text))
                cs = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

            /*String[] ks = new String[]{"Menu", "Left", "Right", "VOICE_ASSIST"};
            String[] cs = new String[]{"Menu", "_Keyboard_phrase", "_Keyboard_edit", "VOICE_ASSIST"};*/
            float wd = 100f / cs.length;
            for (int i = 0; i < cs.length; i++) {
                HashMap<String, Object> mk = new HashMap<>();
                mk.put("height", 40);
                mk.put("width", wd);
                //mk.put("composing", ks[i]);
                mk.put("click", cs[i]);
                mk.put("functional", false);
                int gap = mDefaultHorizontalGap;
                int w = (int) (Config.getDouble(mk, "width", 0) * mDisplayWidth / 100);
                if (w == 0 && mk.containsKey("click")) w = defaultWidth;
                w -= gap;
                if (column >= maxColumns || x + w > mDisplayWidth) {
                    x = gap / 2;
                    y += mDefaultVerticalGap + rowHeight;
                    column = 0;
                    row++;
                    if (mKeys.size() > 0)
                        mKeys.get(mKeys.size() - 1).edgeFlags |= Keyboard.EDGE_RIGHT;
                }
                if (column == 0) {
                    float heightK = Config.getFloatPixel(mk, "height", 0) * mHeightScale;
                    rowHeight = ((heightK > 0) ? heightK : defaultHeight) - mDefaultVerticalGap;
                }
                if (!mk.containsKey("click")) { //無按鍵事件
                    x += w + gap;
                    continue; //縮進
                }
                final Key key = new Key(this, mk);
                key.setKey_text_offset_x(Config.getPixel(mk, "key_text_offset_x", key_text_offset_x));
                key.setKey_text_offset_y(Config.getPixel(mk, "key_text_offset_y", key_text_offset_y));
                key.setKey_symbol_offset_x(Config.getPixel(mk, "key_symbol_offset_x", key_symbol_offset_x));
                key.setKey_symbol_offset_y(Config.getPixel(mk, "key_symbol_offset_y", key_symbol_offset_y));
                key.setKey_hint_offset_x(Config.getPixel(mk, "key_hint_offset_x", key_hint_offset_x));
                key.setKey_hint_offset_y(Config.getPixel(mk, "key_hint_offset_y", key_hint_offset_y));
                key.setKey_press_offset_x(Config.getInt(mk, "key_press_offset_x", key_press_offset_x));
                key.setKey_press_offset_y(Config.getInt(mk, "key_press_offset_y", key_press_offset_y));

                key.setX(x);
                key.setY((int) y);
                int right_gap = Math.abs(mDisplayWidth - x - w - gap / 2);
                //右側不留白
                key.setWidth((right_gap <= mDisplayWidth / 100) ? mDisplayWidth - x - gap / 2 : w);
                key.setHeight((int) rowHeight);
                key.setGap(gap);
                key.setRow(row);
                key.setColumn(column);
                column++;
                x += key.getWidth() + key.getGap();
                mKeys.add(key);
            }
        }

        if (keys instanceof Map) {
            keys = new ArrayList(((Map) keys).values());
        }
        if (keys instanceof List) {
            List list = (List) keys;
            for (Object obj : list) {
                if (obj instanceof String) {
                    int w = defaultWidth - mDefaultHorizontalGap;
                    float h = defaultHeight - mDefaultVerticalGap;
                    Object c = obj;
                    if (column >= maxColumns || x + w > mDisplayWidth) {
                        x = mDefaultHorizontalGap / 2;
                        y += mDefaultVerticalGap + h;
                        column = 0;
                        row++;
                    }
                    final Key key = new Key(this);
                    key.setX(x);
                    key.setY((int) y);
                    int right_gap = Math.abs(mDisplayWidth - x - w - mDefaultHorizontalGap / 2);
                    //右側不留白
                    key.setWidth((right_gap <= mDisplayWidth / 100) ? mDisplayWidth - x - mDefaultHorizontalGap / 2 : w);
                    key.setHeight((int) h);
                    key.setGap(mDefaultHorizontalGap);
                    key.setRow(row);
                    key.setColumn(column);
                    key.events[0] = new Event(this, String.valueOf(c));
                    column++;
                    x += key.getWidth() + key.getGap();
                    mKeys.add(key);
                    if (x > mTotalWidth) {
                        mTotalWidth = x;
                    }
                } else if (obj instanceof Map) {
                    Map<String, Object> mk = (Map<String, Object>) obj;
                    int gap = mDefaultHorizontalGap;
                    int w = (int) (Config.getDouble(mk, "width", 0) * mDisplayWidth / 100);
                    if (w == 0 && mk.containsKey("click")) w = defaultWidth;
                    w -= gap;
                    if (column >= maxColumns || x + w > mDisplayWidth) {
                        x = gap / 2;
                        y += mDefaultVerticalGap + rowHeight;
                        column = 0;
                        row++;
                        if (mKeys.size() > 0)
                            mKeys.get(mKeys.size() - 1).edgeFlags |= Keyboard.EDGE_RIGHT;
                    }
                    if (column == 0) {
                        float heightK = Config.getFloatPixel(mk, "height", 0) * mHeightScale;
                        rowHeight = ((heightK > 0) ? heightK : defaultHeight) - mDefaultVerticalGap;
                    }
                    if (!mk.containsKey("click") && !mk.containsKey("commit")) { //無按鍵事件
                        x += w + gap;
                        continue; //縮進
                    }

                    final Key key = new Key(this, mk);
                    key.setKey_text_offset_x(Config.getPixel(mk, "key_text_offset_x", key_text_offset_x));
                    key.setKey_text_offset_y(Config.getPixel(mk, "key_text_offset_y", key_text_offset_y));
                    key.setKey_symbol_offset_x(Config.getPixel(mk, "key_symbol_offset_x", key_symbol_offset_x));
                    key.setKey_symbol_offset_y(Config.getPixel(mk, "key_symbol_offset_y", key_symbol_offset_y));
                    key.setKey_hint_offset_x(Config.getPixel(mk, "key_hint_offset_x", key_hint_offset_x));
                    key.setKey_hint_offset_y(Config.getPixel(mk, "key_hint_offset_y", key_hint_offset_y));
                    key.setKey_press_offset_x(Config.getInt(mk, "key_press_offset_x", key_press_offset_x));
                    key.setKey_press_offset_y(Config.getInt(mk, "key_press_offset_y", key_press_offset_y));
                    if(absolute){
                        int rx = (int) (Config.getDouble(mk, "x", 0) * mDisplayWidth / 100);
                        key.setX(rx);
                        int ry = (int) (Config.getFloatPixel(mk, "y", 0) * mHeightScale);
                        key.setY(ry);
                        key.setWidth(w);
                        float heightK = Config.getFloatPixel(mk, "height", 0) * mHeightScale;
                        float rh = ((heightK > 0) ? heightK : defaultHeight) - mDefaultVerticalGap;
                        key.setHeight((int) rh);
                    } else {
                        key.setX(x);
                        key.setY((int) y);
                        int right_gap = Math.abs(mDisplayWidth - x - w - gap / 2);
                        //右側不留白
                        key.setWidth((right_gap <= mDisplayWidth / 100) ? mDisplayWidth - x - gap / 2 : w);
                        key.setHeight((int) rowHeight);
                    }

                    key.setGap(gap);
                    key.setRow(row);
                    key.setColumn(column);
                    column++;
                    x += key.getWidth() + key.getGap();
                    mKeys.add(key);
                    if (x > mTotalWidth) {
                        mTotalWidth = x;
                    }
                }
            }

        }

        mKeyBoardTotalHeight = (int) (y + rowHeight + mDefaultVerticalGap / 2);
        if (SHOW_BOTTOM_KEY && !m.containsKey("type")) {
            mKeyBoardTotalHeight = (int) (y + rowHeight + mDefaultVerticalGap / 2);
            String text = Function.getPref(context).getString("custom_bottom_key", "Menu|_Keyboard_phrase|_Keyboard_edit|VOICE_ASSIST");
            String[] cs = text.split("\\|");
            if (cs.length == 0 || TextUtils.isEmpty(text))
                cs = new String[]{"Menu", "_Keyboard_phrase", "_Keyboard_edit", "VOICE_ASSIST"};
            /*String[] ks = new String[]{"Menu", "Left", "Right", "VOICE_ASSIST"};
            String[] cs = new String[]{"Menu", "_Keyboard_phrase", "_Keyboard_edit", "VOICE_ASSIST"};*/
            float wd = 100f / cs.length;
            for (int i = 0; i < cs.length; i++) {
                HashMap<String, Object> mk = new HashMap<>();
                mk.put("height", 40);
                mk.put("width", wd);
                //mk.put("composing", ks[i]);
                mk.put("click", cs[i]);
                mk.put("functional", false);
                int gap = mDefaultHorizontalGap;
                int w = (int) (Config.getDouble(mk, "width", 0) * mDisplayWidth / 100);
                if (w == 0 && mk.containsKey("click")) w = defaultWidth;
                w -= gap;
                if (column >= maxColumns || x + w > mDisplayWidth) {
                    x = gap / 2;
                    y += mDefaultVerticalGap + rowHeight;
                    column = 0;
                    row++;
                    if (mKeys.size() > 0)
                        mKeys.get(mKeys.size() - 1).edgeFlags |= Keyboard.EDGE_RIGHT;
                }
                if (column == 0) {
                    float heightK = Config.getFloatPixel(mk, "height", 0) * mHeightScale;
                    rowHeight = ((heightK > 0) ? heightK : defaultHeight) - mDefaultVerticalGap;
                }
                if (!mk.containsKey("click")) { //無按鍵事件
                    x += w + gap;
                    continue; //縮進
                }
                final Key key = new Key(this, mk);
                key.setKey_text_offset_x(Config.getPixel(mk, "key_text_offset_x", key_text_offset_x));
                key.setKey_text_offset_y(Config.getPixel(mk, "key_text_offset_y", key_text_offset_y));
                key.setKey_symbol_offset_x(Config.getPixel(mk, "key_symbol_offset_x", key_symbol_offset_x));
                key.setKey_symbol_offset_y(Config.getPixel(mk, "key_symbol_offset_y", key_symbol_offset_y));
                key.setKey_hint_offset_x(Config.getPixel(mk, "key_hint_offset_x", key_hint_offset_x));
                key.setKey_hint_offset_y(Config.getPixel(mk, "key_hint_offset_y", key_hint_offset_y));
                key.setKey_press_offset_x(Config.getInt(mk, "key_press_offset_x", key_press_offset_x));
                key.setKey_press_offset_y(Config.getInt(mk, "key_press_offset_y", key_press_offset_y));

                key.setX(x);
                key.setY((int) y);
                int right_gap = Math.abs(mDisplayWidth - x - w - gap / 2);
                //右側不留白
                key.setWidth((right_gap <= mDisplayWidth / 100) ? mDisplayWidth - x - gap / 2 : w);
                key.setHeight((int) rowHeight);
                key.setGap(gap);
                key.setRow(row);
                key.setColumn(column);
                column++;
                x += key.getWidth() + key.getGap();
                mKeys.add(key);
            }
        }

        if (x > mTotalWidth) {
            mTotalWidth = x;
        }
        if (mKeys.size() > 0)
            mKeys.get(mKeys.size() - 1).edgeFlags |= Keyboard.EDGE_RIGHT;
        mTotalHeight = (int) (y + rowHeight + mDefaultVerticalGap / 2);
        Log.i(TAG, "Keyboard: " + mKeyBoardTotalHeight + ";" + mTotalHeight + ";" + mLastTotalHeight);
        if (Math.abs(mTotalHeight - mLastTotalHeight) < 8) {
            mTotalHeight = mLastTotalHeight;
        }
        if("absolute".equals(mType))
            mTotalHeight=getKeyBoardHeight2();
        mLastTotalHeight = mTotalHeight;

        for (Key key : mKeys) {
            if (key.getColumn() == 0) key.edgeFlags |= Keyboard.EDGE_LEFT;
            if (key.getRow() == 0) key.edgeFlags |= Keyboard.EDGE_TOP;
            if (key.getRow() == row) key.edgeFlags |= Keyboard.EDGE_BOTTOM;
        }
    }

    private void loadKey(CharSequence characters, int columns) {
        int x = 0;
        int y = 0;
        int column = 0;
        mTotalWidth = 0;
        final int maxColumns = columns == -1 ? Integer.MAX_VALUE : columns;
        int mDefaultWidth = mDisplayWidth / maxColumns - mDefaultHorizontalGap;
        for (int i = 0; i < characters.length(); i++) {
            char c = characters.charAt(i);
            if (column >= maxColumns || x + mDefaultWidth > mDisplayWidth) {
                x = 0;
                y += mDefaultVerticalGap + mDefaultHeight;
                column = 0;
            }
            final Key key = new Key(this);
            key.setX(x);
            key.setY(y);
            key.setWidth(mDefaultWidth);
            key.setHeight(mDefaultHeight);
            key.setGap(mDefaultHorizontalGap);
            key.events[0] = new Event(this, String.valueOf(c));
            column++;
            x += key.getWidth() + key.getGap();
            mKeys.add(key);
            if (x > mTotalWidth) {
                mTotalWidth = x;
            }
        }
        mTotalHeight = y + mDefaultHeight;
    }

    private float loadKey(List keys, int columns, int width, float mDefaultHeight, float y) {
        int x = 0;
        int column = 0;
        mTotalWidth = 0;

        final int maxColumns = columns == -1 ? Integer.MAX_VALUE : columns;
        if (width == 0)
            width = mDefaultWidth;
        int w = width - mDefaultHorizontalGap;
        for (int i = 0; i < keys.size(); i++) {
            Object c = keys.get(i);
            if (column >= maxColumns || x + w > mDisplayWidth) {
                x = 0;
                y += mDefaultVerticalGap + mDefaultHeight;
                column = 0;
            }
            final Key key = new Key(this);
            key.setX(x);
            key.setY((int) y);
            int right_gap = Math.abs(mDisplayWidth - x - w - mDefaultHorizontalGap / 2);
            //右側不留白
            key.setWidth((right_gap <= mDisplayWidth / 100) ? mDisplayWidth - x - mDefaultHorizontalGap / 2 : w);
            key.setHeight((int) mDefaultHeight);
            key.setGap(mDefaultHorizontalGap);
            key.events[0] = new Event(this, String.valueOf(c));
            column++;
            x += key.getWidth() + key.getGap();
            mKeys.add(key);
            if (x > mTotalWidth) {
                mTotalWidth = x;
            }
        }
        return y + mDefaultHeight;
    }

    public Key getmShiftKey() {
        return mShiftKey;
    }

    public void setmShiftKey(Key mShiftKey) {
        this.mShiftKey = mShiftKey;
    }

    public List<Key> getmComposingKeys() {
        return mComposingKeys;
    }

    public List<Key> getKeys() {
        return mKeys;
    }

    public List<Key> getComposingKeys() {
        return mComposingKeys;
    }

    protected int getHorizontalGap() {
        return mDefaultHorizontalGap;
    }

    protected void setHorizontalGap(int gap) {
        mDefaultHorizontalGap = (int) (gap * mHeightScale);
    }

    protected int getVerticalGap() {
        return mDefaultVerticalGap;
    }

    protected void setVerticalGap(int gap) {
        mDefaultVerticalGap = gap;
    }

    protected int getKeyHeight() {
        return mDefaultHeight;
    }

    protected void setKeyHeight(int height) {
        mDefaultHeight = (int) (height * mHeightScale);
    }

    protected int getKeyWidth() {
        return mDefaultWidth;
    }

    protected void setKeyWidth(int width) {
        mDefaultWidth = width;
    }

    /**
     * Returns the total height of the keyboard
     *
     * @return the total height of the keyboard
     */
    public int getHeight() {
        return mTotalHeight;
    }

    public int getKeyBoardHeight() {
        return (int) (mTotalHeight / mHeightScale);
    }

    public int getKeyBoardHeight2() {
        if (mHeight != null)
            return (int) (mHeight * mHeightScale);
        return mTotalHeight;
    }

    public int getMinWidth() {
        return mTotalWidth;
    }

    private boolean hasModifier(int modifiers) {
        return (mMetaState & modifiers) != 0;
    }

    public boolean hasModifier() {
        return mMetaState != 0;
    }

    public boolean toggleModifier(int mask) {
        boolean value = !hasModifier(mask);
        if (value) mMetaState |= mask;
        else mMetaState &= ~mask;
        return value;
    }

    public int getModifer() {
        return mMetaState;
    }

    private boolean setModifier(int mask, boolean value) {
        boolean b = hasModifier(mask);
        if (b == value) return false;
        if (value) mMetaState |= mask;
        else mMetaState &= ~mask;
        return true;
    }

    public boolean isAlted() {
        return hasModifier(KeyEvent.META_ALT_ON);
    }

    public boolean isShifted() {
        return hasModifier(KeyEvent.META_SHIFT_ON);
    }

    public boolean isCtrled() {
        return hasModifier(KeyEvent.META_CTRL_ON);
    }

    /**
     * 設定鍵盤的Shift鍵狀態
     *
     * @param on      是否保持Shift按下狀態
     * @param shifted 是否按下Shift
     * @return Shift鍵狀態是否改變
     */
    public boolean setShifted(boolean on, boolean shifted) {
        on = on & shifted;
        if (mShiftKey != null) mShiftKey.setOn(on);
        return setModifier(KeyEvent.META_SHIFT_ON, on || shifted);
    }

    public boolean resetShifted() {
        if (mShiftKey != null && !mShiftKey.isOn())
            return setModifier(KeyEvent.META_SHIFT_ON, false);
        return false;
    }

    private void computeNearestNeighbors() {
        // Round-up so we don't have any pixels outside the grid
        mCellWidth = (getMinWidth() + GRID_WIDTH - 1) / GRID_WIDTH;
        mCellHeight = (getHeight() + GRID_HEIGHT - 1) / GRID_HEIGHT;
        mGridNeighbors = new int[GRID_SIZE][];
        int[] indices = new int[mKeys.size()];
        final int gridWidth = GRID_WIDTH * mCellWidth;
        final int gridHeight = GRID_HEIGHT * mCellHeight;
        for (int x = 0; x < gridWidth; x += mCellWidth) {
            for (int y = 0; y < gridHeight; y += mCellHeight) {
                int count = 0;
                for (int i = 0; i < mKeys.size(); i++) {
                    final Key key = mKeys.get(i);
                    if (key.squaredDistanceFrom(x, y) < mProximityThreshold
                            || key.squaredDistanceFrom(x + mCellWidth - 1, y) < mProximityThreshold
                            || key.squaredDistanceFrom(x + mCellWidth - 1, y + mCellHeight - 1)
                            < mProximityThreshold
                            || key.squaredDistanceFrom(x, y + mCellHeight - 1) < mProximityThreshold
                            || key.isInside(x, y)
                            || key.isInside(x + mCellWidth - 1, y)
                            || key.isInside(x + mCellWidth - 1, y + mCellHeight - 1)
                            || key.isInside(x, y + mCellHeight - 1)) {
                        indices[count++] = i;
                    }
                }
                int[] cell = new int[count];
                System.arraycopy(indices, 0, cell, 0, count);
                mGridNeighbors[(y / mCellHeight) * GRID_WIDTH + (x / mCellWidth)] = cell;
            }
        }
    }

    /**
     * Returns the indices of the keys that are closest to the given point.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the array of integer indices for the nearest keys to the given point. If the given
     * point is out of range, then an array of size zero is returned.
     */
    public int[] getNearestKeys(int x, int y) {
        if (mGridNeighbors == null) computeNearestNeighbors();
        if (x >= 0 && x < getMinWidth() && y >= 0 && y < getHeight()) {
            int index = (y / mCellHeight) * GRID_WIDTH + (x / mCellWidth);
            if (index < GRID_SIZE) {
                return mGridNeighbors[index];
            }
        }
        return new int[0];
    }

    public boolean getAsciiMode() {
        return mAsciiMode != 0;
    }

    public String getAsciiKeyboard() {
        return mAsciiKeyboard;
    }

    public boolean isLabelUppercase() {
        if (TextUtils.isEmpty(mLabelTransform))
            return false;
        return mLabelTransform.contentEquals("uppercase");
    }

    public boolean isLock() {
        return mLock;
    }

    public float getRoundCorner() {
        return mRoundCorner;
    }

    public float[] getRoundCorners() {
        return mRoundCorners;
    }

    public Drawable getBackground() {
        return mBackground;
    }

    public String getName() {
        return mName;
    }

    public boolean pageUp() {
        return false;
    }

    public boolean pageDown() {
        return false;
    }

    public String getType() {
        return mType;
    }
}
