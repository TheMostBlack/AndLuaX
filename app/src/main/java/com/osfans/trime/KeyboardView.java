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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.PopupWindow;
import android.widget.RippleHelper;
import android.widget.TextView;

import com.androlua.LoadingDrawable;
import com.androlua.LuaBitmapDrawable;
import com.osfans.trime.pro.BuildConfig;
import com.osfans.trime.pro.R;
import com.osfans.trime.enums.KeyEventType;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 顯示{@link Keyboard 鍵盤}及{@link Key 按鍵}
 */
public class KeyboardView extends View implements View.OnClickListener {

    private boolean isTouchExplorationEnabled;
    private boolean KEY_LONGPRESS;
    private boolean SHOW_BOTTOM_KEY;
    private boolean KEY_SWIPE;
    private KeyboardView mMiniKeyboard;
    private int mPopupTouchOffsetX;
    private int mPopupTouchOffsetY;
    private int mPreviewTextSizeLarge;
    private boolean colors_keyboard;
    private float[] mRoundCorners;
    private float mRoundCorner;
    private boolean mInHover;
    public static int sCandidateKeyboardTextSize;
    private boolean mFling = false;
    private boolean mIsMinKeyboard = false;
    private int key_alpha;
    private Integer mKeyborderColor;
    private int mKeyborder;
    private Integer mHilitedKeyborderColor;
    private HashMap<Key, Drawable> mKeyBackMap = new HashMap<>();
    private HashMap<int[], Drawable> mKeyBackColorMap = new HashMap<int[], Drawable>();
    private RippleHelper mRippleHelper;
    private Config config;

    public void onWindowShown() {
        if (mAccessibilityManager == null)
            mAccessibilityManager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (mAccessibilityManager != null)
            isTouchExplorationEnabled = mAccessibilityManager.isTouchExplorationEnabled();
        invalidateAllKeys();
    }

    public float getPopupOffsetX() {
        return mMiniKeyboardOffsetX;
    }

    public float getPopupOffsetY() {
        return mMiniKeyboardOffsetY;
    }

    /**
     * 處理按鍵、觸摸等輸入事件
     */
    public interface OnKeyboardActionListener {

        /**
         * Called when the user presses a key. This is sent before the {@link #onKey} is called. For
         * keys that repeat, this is only called once.
         *
         * @param primaryCode the unicode of the key being pressed. If the touch is not on a valid key,
         *                    the value will be zero.
         */
        void onPress(int primaryCode);

        void onPress(Key key);

        /**
         * Called when the user releases a key. This is sent after the {@link #onKey} is called. For
         * keys that repeat, this is only called once.
         *
         * @param primaryCode the code of the key that was released
         */
        void onRelease(int primaryCode);

        void onEvent(Event event);

        /**
         * Send a key press to the listener.
         *
         * @param primaryCode this is the key that was pressed
         * @param mask        the codes for all the possible alternative keys with the primary code being the
         *                    first. If the primary key code is a single character such as an alphabet or number or
         *                    symbol, the alternatives will include other characters that may be on the same key or
         *                    adjacent keys. These codes are useful to correct for accidental presses of a key adjacent
         *                    to the intended key.
         */
        void onKey(int primaryCode, int mask);

        /**
         * Sends a sequence of characters to the listener.
         *
         * @param text the sequence of characters to be displayed.
         */
        void onText(CharSequence text);

        /**
         * Called when the user quickly moves the finger from right to left.
         */
        void swipeLeft();

        /**
         * Called when the user quickly moves the finger from left to right.
         */
        void swipeRight();

        /**
         * Called when the user quickly moves the finger from up to down.
         */
        void swipeDown();

        /**
         * Called when the user quickly moves the finger from down to up.
         */
        void swipeUp();

        void onUp(int code);
    }

    private static final boolean DEBUG = false;
    private static final int NOT_A_KEY = -1;
    private static final int[] LONG_PRESSABLE_STATE_SET = {android.R.attr.state_long_pressable};
    private static String TAG = KeyboardView.class.getSimpleName();

    private Keyboard mKeyboard;
    private int mCurrentKeyIndex = NOT_A_KEY;
    private int mLabelTextSize;
    private int mKeyTextSize;
    private ColorStateList mKeyTextColor;
    private StateListDrawable mKeyBackColor;
    private int key_symbol_color, hilited_key_symbol_color;
    private int mSymbolSize;
    private Paint mPaintSymbol;
    private float mShadowRadius;
    private int mShadowColor;
    private float mBackgroundDimAmount;
    private Drawable mBackground;

    private TextView mPreviewText;
    private PopupWindow mPreviewPopup;
    private int mPreviewOffset;
    private int mPreviewHeight;
    // Working variable
    private final int[] mCoordinates = new int[2];

    private PopupWindow mPopupKeyboard;
    private boolean mMiniKeyboardOnScreen;
    private View mPopupParent;
    private int mMiniKeyboardOffsetX;
    private int mMiniKeyboardOffsetY;
    private Map<Key, View> mMiniKeyboardCache;
    private Key[] mKeys;

    /**
     * Listener for {@link OnKeyboardActionListener}.
     */
    private OnKeyboardActionListener mKeyboardActionListener;

    private static final int MSG_SHOW_PREVIEW = 1;
    private static final int MSG_REMOVE_PREVIEW = 2;
    private static final int MSG_REPEAT = 3;
    private static final int MSG_LONGPRESS = 4;

    private static final int DELAY_BEFORE_PREVIEW = 0;
    private static final int DELAY_AFTER_PREVIEW = 70;
    private static final int DEBOUNCE_TIME = 70;

    private int mVerticalCorrection;
    private int mProximityThreshold;

    private boolean mShowPreview = true;

    private int mLastX;
    private int mLastY;
    private int mStartX;
    private int mStartY;

    private boolean mProximityCorrectOn;

    private Paint mPaint;
    private Rect mPadding;

    private long mDownTime;
    private long mLastMoveTime;
    private int mLastKey;
    private int mLastCodeX;
    private int mLastCodeY;
    private int mCurrentKey = NOT_A_KEY;
    private int mDownKey = NOT_A_KEY;
    private long mLastKeyTime;
    private long mCurrentKeyTime;
    private int[] mKeyIndices = new int[12];
    private GestureDetector mGestureDetector;
    private int mRepeatKeyIndex = NOT_A_KEY;
    private int mPopupLayout;
    private boolean mAbortKey;
    private Key mInvalidatedKey;
    private Rect mClipRegion = new Rect(0, 0, 0, 0);
    private boolean mPossiblePoly;
    private SwipeTracker mSwipeTracker = new SwipeTracker();
    private int mSwipeThreshold;
    private boolean mDisambiguateSwipe;

    // Variables for dealing with multiple pointers
    private int mOldPointerCount = 1;
    private int[] mComboCodes = new int[10];
    private int mComboCount = 0;
    private boolean mComboMode = false;

    private static int REPEAT_INTERVAL = 50; // ~20 keys per second
    private static int REPEAT_START_DELAY = 400;
    private static int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();

    private static int MAX_NEARBY_KEYS = 12;
    private int[] mDistances = new int[MAX_NEARBY_KEYS];

    // For multi-tap
    private int mLastSentIndex;
    private long mLastTapTime;
    private static int MULTITAP_INTERVAL = 800; // milliseconds
    private StringBuilder mPreviewLabel = new StringBuilder(1);

    /**
     * Whether the keyboard bitmap needs to be redrawn before it's blitted. *
     */
    private boolean mDrawPending;
    /**
     * The dirty region in the keyboard bitmap
     */
    private Rect mDirtyRect = new Rect();
    /**
     * The keyboard bitmap for faster updates
     */
    private Bitmap mBuffer;
    /**
     * Notes if the keyboard just changed, so that we could possibly reallocate the mBuffer.
     */
    private boolean mKeyboardChanged;
    /**
     * The canvas for the above mutable keyboard bitmap
     */
    private Canvas mCanvas;
    /**
     * The accessibility manager for accessibility support
     */
    private AccessibilityManager mAccessibilityManager;
    /** The audio manager for accessibility support */
    //private AudioManager mAudioManager;
    /**
     * Whether the requirement of a headset to hear passwords if accessibility is enabled is
     * announced.
     */
    private boolean mHeadsetRequiredToHearPasswordsAnnounced;

    private boolean mShowHint = true;

    private Method getStateDrawableIndex;
    private Method getStateDrawable;

    private static class MyHandler extends Handler {
        private final WeakReference<KeyboardView> mKeyboardView;

        public MyHandler(KeyboardView view) {
            mKeyboardView = new WeakReference<KeyboardView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            KeyboardView view = mKeyboardView.get();
            switch (msg.what) {
                case MSG_SHOW_PREVIEW:
                    view.showKey(msg.arg1, msg.arg2);
                    break;
                case MSG_REMOVE_PREVIEW:
                    view.mPreviewText.setVisibility(INVISIBLE);
                    break;
                case MSG_REPEAT:
                    if (view.repeatKey()) {
                        Message repeat = Message.obtain(this, MSG_REPEAT);
                        sendMessageDelayed(repeat, REPEAT_INTERVAL);
                    }
                    break;
                case MSG_LONGPRESS:
                    view.openPopupIfRequired((MotionEvent) msg.obj);
                    break;
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    public void setShowHint(boolean value) {
        mShowHint = value;
    }

    public void reset() {
        cache.clear();
        config = Config.get();
        colors_keyboard = Function.getPref(getContext()).getBoolean("pref_colors_keyboard", false);
        mShowHint = config.getShowHint();
        key_alpha = config.getKeyAlpha();
        /*if (!config.isKeyboardFloat())
            key_alpha = 255;*/
        key_symbol_color = config.getColor("key_symbol_color");
        hilited_key_symbol_color = config.getColor("hilited_key_symbol_color");
        mShadowColor = config.getColor("shadow_color");

        mKeyborderColor = config.getColor("key_border_color");
        if (mKeyborderColor == null)
            mKeyborderColor = config.getColor("border_color");
        mHilitedKeyborderColor = config.getColor("hilited_key_border_color");
        if (mHilitedKeyborderColor == null)
            mHilitedKeyborderColor = mKeyborderColor;

        mKeyborder = config.getPixel("key_border");
        mSymbolSize = config.getPixel("symbol_text_size");
        mKeyTextSize = config.getPixel("key_text_size");
        mVerticalCorrection = config.getPixel("vertical_correction");
        setProximityCorrectionEnabled(config.getBoolean("proximity_correction"));
        mPreviewOffset = config.getPixel("preview_offset");
        mPreviewHeight = config.getPixel("preview_height");
        mLabelTextSize = config.getPixel("key_long_text_size");
        if (mLabelTextSize == 0) mLabelTextSize = mKeyTextSize;

        mBackgroundDimAmount = config.getFloat("background_dim_amount");
        mShadowRadius = config.getFloat("shadow_radius");
        Object obj = config.getValue("round_corner");
        mRoundCorners = null;
        if (obj != null) {
            if (obj instanceof List) {
                List list = (List) obj;
                mRoundCorners = new float[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    mRoundCorners[i] = Float.valueOf(list.get(i).toString());
                }
            } else {
                mRoundCorner = Float.valueOf(obj.toString());
            }
        }
        Float f = BackUtil.getPixel("key_round_corner");
        if (f != null)
            mRoundCorner = f;

        mKeyBackColor = new StateListDrawable();
        mKeyBackColorMap.clear();
        mKeyBackColorMap.put(
                Key.KEY_STATE_PRESSED_ON, getDrawable(config.getColorDrawable("hilited_on_key_back_color"), mRoundCorner, mHilitedKeyborderColor));
        mKeyBackColorMap.put(
                Key.KEY_STATE_PRESSED_OFF, getDrawable(config.getColorDrawable("hilited_off_key_back_color"), mRoundCorner, mHilitedKeyborderColor));
        mKeyBackColorMap.put(Key.KEY_STATE_NORMAL_ON, getDrawable(config.getColorDrawable("on_key_back_color"), mRoundCorner, mKeyborderColor));
        mKeyBackColorMap.put(Key.KEY_STATE_NORMAL_OFF, getDrawable(config.getColorDrawable("off_key_back_color"), mRoundCorner, mKeyborderColor));
        mKeyBackColorMap.put(
                Key.KEY_STATE_PRESSED, getDrawable(config.getColorDrawable("hilited_key_back_color"), mRoundCorner, mHilitedKeyborderColor));
        mKeyBackColorMap.put(Key.KEY_STATE_NORMAL, getDrawable(config.getColorDrawable("key_back_color"), mRoundCorner, mKeyborderColor));

        mKeyBackColor.addState(
                Key.KEY_STATE_PRESSED_ON, getDrawable(config.getColorDrawable("hilited_on_key_back_color"), mRoundCorner, mHilitedKeyborderColor));
        mKeyBackColor.addState(
                Key.KEY_STATE_PRESSED_OFF, getDrawable(config.getColorDrawable("hilited_off_key_back_color"), mRoundCorner, mHilitedKeyborderColor));
        mKeyBackColor.addState(Key.KEY_STATE_NORMAL_ON, getDrawable(config.getColorDrawable("on_key_back_color"), mRoundCorner, mKeyborderColor));
        mKeyBackColor.addState(Key.KEY_STATE_NORMAL_OFF, getDrawable(config.getColorDrawable("off_key_back_color"), mRoundCorner, mKeyborderColor));
        mKeyBackColor.addState(
                Key.KEY_STATE_PRESSED, getDrawable(config.getColorDrawable("hilited_key_back_color"), mRoundCorner, mHilitedKeyborderColor));
        mKeyBackColor.addState(Key.KEY_STATE_NORMAL, getDrawable(config.getColorDrawable("key_back_color"), mRoundCorner, mKeyborderColor));

        mKeyTextColor =
                new ColorStateList(
                        Key.KEY_STATES,
                        new int[]{
                                config.getColor("hilited_on_key_text_color"),
                                config.getColor("hilited_off_key_text_color"),
                                config.getColor("on_key_text_color"),
                                config.getColor("off_key_text_color"),
                                config.getColor("hilited_key_text_color"),
                                config.getColor("key_text_color")
                        });

        Integer color = config.getColor("preview_text_color");
        if (color != null) mPreviewText.setTextColor(color);
        Drawable previewBackColor = config.getColorDrawable("preview_back_color");
        if (previewBackColor != null) {
            /*GradientDrawable background = new GradientDrawable();
            background.setColor(previewBackColor);*/
            if (previewBackColor instanceof GradientDrawable) {
                if (mRoundCorners != null)
                    ((GradientDrawable) previewBackColor).setCornerRadii(mRoundCorners);
                else
                    ((GradientDrawable) previewBackColor).setCornerRadius(mRoundCorner);
                if (mKeyborderColor != null)
                    ((GradientDrawable) previewBackColor).setStroke(mKeyborder, mKeyborderColor);
            }
            mPreviewText.setBackground(previewBackColor);
        } else {
            mPreviewText.setBackground(mKeyBackColor);
        }

        mPreviewTextSizeLarge = config.getInt("preview_text_size");
        mPreviewText.setTextSize(mPreviewTextSizeLarge);
        mShowPreview = config.getShowPreview();

        mPaint.setTypeface(config.getFont("key_font"));
        mPaintSymbol.setTypeface(config.getFont("symbol_font"));
        mPaintSymbol.setColor(key_symbol_color);
        mPaintSymbol.setTextSize(mSymbolSize);
        mPreviewText.setTypeface(config.getFont("preview_font"));

        REPEAT_INTERVAL = config.getRepeatInterval();
        REPEAT_START_DELAY = config.getLongTimeout() + 1;
        LONGPRESS_TIMEOUT = config.getLongTimeout();
        KEY_LONGPRESS = config.isKeyLongPress();
        KEY_SWIPE = config.isKeySwipe();
        MULTITAP_INTERVAL = config.getLongTimeout();
        invalidateAllKeys();
        mAccessibilityManager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (mAccessibilityManager != null)
            isTouchExplorationEnabled = mAccessibilityManager.isTouchExplorationEnabled();
        int width = Math.min(Trime.getService().getWidth(), Trime.getService().getHeight());
        for (int size = mKeyTextSize; size > 0; size--) {
            mPaint.setTextSize(size);
            if (width > mPaint.measureText("口口口口口口口口口口口口口口口口口口")) {

                sCandidateKeyboardTextSize = size / Config.getPixel(1f);
                return;
            }
        }
    }

    private Drawable getDrawable(Drawable drawable, float mRoundCorner, Integer borderColor) {
        if (drawable instanceof GradientDrawable) {
            if (mRoundCorners != null)
                ((GradientDrawable) drawable).setCornerRadii(mRoundCorners);
            else
                ((GradientDrawable) drawable).setCornerRadius(mRoundCorner);
            if (borderColor != null)
                ((GradientDrawable) drawable).setStroke(mKeyborder, borderColor);
        }
        return drawable;
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        try {
            getStateDrawableIndex =
                    StateListDrawable.class.getMethod("getStateDrawableIndex", int[].class);
            getStateDrawable = StateListDrawable.class.getMethod("getStateDrawable", int.class);
        } catch (Exception ex) {
            Log.e(TAG, "Get Drawable Exception" + ex);
        }

        LayoutInflater inflate =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPreviewText = (TextView) inflate.inflate(R.layout.keyboard_key_preview, (ViewGroup) null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Align.CENTER);
        mPaintSymbol = new Paint();
        mPaintSymbol.setAntiAlias(true);
        mPaintSymbol.setTextAlign(Align.CENTER);
        reset();

        mPreviewPopup = new PopupWindow(context);
        mPreviewPopup.setContentView(mPreviewText);
        mPreviewPopup.setBackgroundDrawable(null);
        mPreviewPopup.setTouchable(false);

        mPopupLayout = R.layout.keyboard_popup_keyboard;
        mPopupKeyboard = new PopupWindow(context);
        mPopupKeyboard.setBackgroundDrawable(null);
        //mPopupKeyboard.setClippingEnabled(false);

        mPopupParent = this;
        //mPredicting = true;

        mPadding = new Rect(0, 0, 0, 0);
        mMiniKeyboardCache = new HashMap<Key, View>();

        mSwipeThreshold = (int) (10 * getResources().getDisplayMetrics().density);
        mDisambiguateSwipe = true;

        resetMultiTap();
        initGestureDetector();
    }

    private void initGestureDetector() {
        mGestureDetector =
                new GestureDetector(
                        getContext(),
                        new GestureDetector.OnGestureListener() {
                            @Override
                            public boolean onDown(MotionEvent e) {
                                return false;
                            }

                            @Override
                            public void onShowPress(MotionEvent e) {

                            }

                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                return false;
                            }

                            @Override
                            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                                if (!mFling)
                                    mFling = e2.getEventTime() - e1.getEventTime() < 64;
                                return false;
                            }

                            @Override
                            public void onLongPress(MotionEvent e) {

                            }

                            @Override
                            public boolean onFling(
                                    MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                                if (!KEY_SWIPE)
                                    return false;
                                if (mPossiblePoly) return false;
                                mFling = true;
                                final float absX = Math.abs(velocityX);
                                final float absY = Math.abs(velocityY);
                                float deltaX = me2.getX() - me1.getX();
                                float deltaY = me2.getY() - me1.getY();
                                int w = Math.min(getWidth(), getHeight());
                                int travelX = w / 8; // Half the keyboard width
                                int travelY = w / 8; // Half the keyboard height
                                mSwipeTracker.computeCurrentVelocity(10);
                                final float endingVelocityX = mSwipeTracker.getXVelocity();
                                final float endingVelocityY = mSwipeTracker.getYVelocity();
                                boolean sendDownKey = false;
                                int type = 0;
                                if (velocityX > mSwipeThreshold && absY < absX && deltaX > travelX) {
                                    if (mDisambiguateSwipe && endingVelocityX < velocityX / 4) {
                                        sendDownKey = true;
                                        type = KeyEventType.SWIPE_RIGHT.ordinal();
                                    } else {
                                        swipeRight();
                                        return true;
                                    }
                                } else if (velocityX < -mSwipeThreshold && absY < absX && deltaX < -travelX) {
                                    if (mDisambiguateSwipe && endingVelocityX > velocityX / 4) {
                                        sendDownKey = true;
                                        type = KeyEventType.SWIPE_LEFT.ordinal();
                                    } else {
                                        swipeLeft();
                                        return true;
                                    }
                                } else if (velocityY < -mSwipeThreshold && absX < absY && deltaY < -travelY) {
                                    if (mDisambiguateSwipe && endingVelocityY > velocityY / 4) {
                                        sendDownKey = true;
                                        type = KeyEventType.SWIPE_UP.ordinal();
                                    } else {
                                        swipeUp();
                                        return true;
                                    }
                                } else if (velocityY > mSwipeThreshold && absX < absY / 2 && deltaY > travelY) {
                                    if (mDisambiguateSwipe && endingVelocityY < velocityY / 4) {
                                        sendDownKey = true;
                                        type = KeyEventType.SWIPE_DOWN.ordinal();
                                    } else {
                                        swipeDown();
                                        return true;
                                    }
                                }

                                if (sendDownKey) {
                                    showPreview(NOT_A_KEY);
                                    showPreview(mDownKey, type);
                                    detectAndSendKey(mDownKey, mStartX, mStartY, me1.getEventTime(), type);
                                    return true;
                                }
                                return false;
                            }
                        });

        mGestureDetector.setIsLongpressEnabled(false);
    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        mKeyboardActionListener = listener;
    }

    /**
     * Returns the {@link OnKeyboardActionListener} object.
     *
     * @return the listener attached to this keyboard
     */
    protected OnKeyboardActionListener getOnKeyboardActionListener() {
        return mKeyboardActionListener;
    }

    private void setKeyboardBackground() {
        if (mKeyboard == null)
            return;
        Drawable d = mPreviewText.getBackground();
        if (d instanceof GradientDrawable) {
            if (mKeyboard.getRoundCorners() != null)
                ((GradientDrawable) d).setCornerRadii(mKeyboard.getRoundCorners());
            else
                ((GradientDrawable) d).setCornerRadius(mKeyboard.getRoundCorner());
            mPreviewText.setBackground(d);
        }
        d = mKeyboard.getBackground();
        if (d != null)
            d.setAlpha(key_alpha);
        setBackgroundDrawable(d);
        d = BackUtil.get(Trime.getService(), "keyboard");
        if (d != null) {
            d.setAlpha(key_alpha);
            setBackgroundDrawable(d);
        }
        /*mRippleHelper = new RippleHelper(this);
        mRippleHelper.setCircle(false);
        mRippleHelper.setSingle(false);
        mRippleHelper.setRippleLineColor(mHilitedKeyborderColor);*/
    }

    /**
     * Attaches a keyboard to this view. The keyboard can be switched at any time and the view will
     * re-layout itself to accommodate the keyboard.
     *
     * @param keyboard the keyboard to display in this view
     * @see Keyboard
     * @see #getKeyboard()
     */
    public void setKeyboard(Keyboard keyboard) {
        if (mKeyboard != null) {
            showPreview(NOT_A_KEY);
        }
        // Remove any pending messages
        removeMessages();
        mRepeatKeyIndex = NOT_A_KEY;
        mKeyboard = keyboard;
        List<Key> keys = mKeyboard.getKeys();
        mKeys = keys.toArray(new Key[keys.size()]);
        /*mKeyBackMap.clear();
        for (Key key : mKeys) {
            Drawable b = BackUtil.get(Trime.getService(), key.getLabel());
            if(b==null)
                b = BackUtil.get(Trime.getService(), key.getCode());
            if(b!=null)
                mKeyBackMap.put(key,b);
        }*/
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = mKeyboard.getHeight();
        setLayoutParams(lp);
        setKeyboardBackground();
        requestLayout();
        // Hint to reallocate the buffer if the size changed
        mKeyboardChanged = true;
        invalidateAllKeys();
        computeProximityThreshold(keyboard);
        mMiniKeyboardCache.clear(); // Not really necessary to do every time, but will free up views
        // Switching to a different keyboard should abort any pending keys so that the key up
        // doesn't get delivered to the old or new keyboard
        mAbortKey = true; // Until the next ACTION_DOWN
        if ("absolute".equals(keyboard.getType()))
            setProximityCorrectionEnabled(false);
        else
            setProximityCorrectionEnabled(config.getBoolean("proximity_correction"));
    }

    /**
     * Returns the current keyboard being displayed by this view.
     *
     * @return the currently attached keyboard
     * @see #setKeyboard(Keyboard)
     */
    public Keyboard getKeyboard() {
        return mKeyboard;
    }

    /**
     * 設定鍵盤的Shift鍵狀態
     *
     * @param on      是否保持Shift按下狀態
     * @param shifted 是否按下Shift
     * @return Shift鍵狀態是否改變
     * @see Keyboard#setShifted(boolean, boolean) KeyboardView#isShifted()
     */
    public boolean setShifted(boolean on, boolean shifted) {
        if (mKeyboard != null) {
            if (mKeyboard.setShifted(on, shifted)) {
                // The whole keyboard probably needs to be redrawn
                Trime trime = Trime.getService();
                if (trime != null ) {
                    String text = shifted ? "大写" : "小写";
                    if (shifted && on)
                        text += "锁定";
                    trime.speak(text);
                }
                invalidateAllKeys();
                return true;
            }
        }
        return false;
    }

    private boolean resetShifted() {
        if (mKeyboard != null) {
            if (mKeyboard.resetShifted()) {
                // The whole keyboard probably needs to be redrawn
                invalidateAllKeys();
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the state of the shift key of the keyboard, if any.
     *
     * @return true if the shift is in a pressed state, false otherwise. If there is no shift key on
     * the keyboard or there is no keyboard attached, it returns false.
     * @see KeyboardView#setShifted(boolean, boolean)
     */
    public boolean isShifted() {
        if (mKeyboard != null) {
            return mKeyboard.isShifted();
        }
        return false;
    }

    /**
     * 返回鍵盤是否爲大寫狀態
     *
     * @return true 如果大寫
     */
    public boolean isCapsOn() {
        if (mKeyboard != null && mKeyboard.getmShiftKey() != null)
            return mKeyboard.getmShiftKey().isOn();
        return false;
    }

    /**
     * Enables or disables the key feedback popup. This is a popup that shows a magnified version of
     * the depressed key. By default the preview is enabled.
     *
     * @param previewEnabled whether or not to enable the key feedback popup
     * @see #isPreviewEnabled()
     */
    public void setPreviewEnabled(boolean previewEnabled) {
        mShowPreview = previewEnabled;
    }

    /**
     * Returns the enabled state of the key feedback popup.
     *
     * @return whether or not the key feedback popup is enabled
     * @see #setPreviewEnabled(boolean)
     */
    public boolean isPreviewEnabled() {
        return mShowPreview;
    }

    public void setVerticalCorrection(int verticalOffset) {
    }

    private void setPopupParent(View v) {
        mPopupParent = v;
    }

    private void setPopupOffset(int x, int y) {
        mMiniKeyboardOffsetX = x;
        mMiniKeyboardOffsetY = y;
        mIsMinKeyboard = true;
        if (mPreviewPopup.isShowing()) {
            mPreviewPopup.dismiss();
        }
    }

    /**
     * When enabled, calls to {@link OnKeyboardActionListener#onKey} will include key codes for
     * adjacent keys. When disabled, only the primary key code will be reported.
     *
     * @param enabled whether or not the proximity correction is enabled
     */
    private void setProximityCorrectionEnabled(boolean enabled) {
        mProximityCorrectOn = enabled;
    }

    /**
     * 檢查是否允許距離校正
     *
     * @return 是否允許距離校正
     */
    public boolean isProximityCorrectionEnabled() {
        return mProximityCorrectOn;
    }

    /**
     * 關閉彈出鍵盤
     *
     * @param v 鍵盤視圖
     */
    @Override
    public void onClick(View v) {
        dismissPopupKeyboard();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Round up a little
        if (mKeyboard == null) {
            setMeasuredDimension(
                    getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
        } else {
            int width = mKeyboard.getMinWidth() + getPaddingLeft() + getPaddingRight();
            if (MeasureSpec.getSize(widthMeasureSpec) < width + 10) {
                width = MeasureSpec.getSize(widthMeasureSpec);
            }
            setMeasuredDimension(width, mKeyboard.getHeight() + getPaddingTop() + getPaddingBottom());
        }
    }

    /**
     * 計算水平和豎直方向的相鄰按鍵中心的平均距離的平方，這樣不需要做開方運算
     *
     * @param keyboard 鍵盤
     */
    private void computeProximityThreshold(Keyboard keyboard) {
        if (keyboard == null) return;
        final Key[] keys = mKeys;
        if (keys == null) return;
        int length = keys.length;
        int dimensionSum = 0;
        for (int i = 0; i < length; i++) {
            Key key = keys[i];
            dimensionSum += Math.min(key.getWidth(), key.getHeight()) + key.getGap();
        }
        if (dimensionSum < 0 || length == 0) return;
        mProximityThreshold = (int) (dimensionSum * Keyboard.SEARCH_DISTANCE / length);
        mProximityThreshold *= mProximityThreshold; // Square it
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawPending || mBuffer == null || mKeyboardChanged) {
            onBufferDraw();
        }
        canvas.drawBitmap(mBuffer, 0, 0, null);
    }

    private static int rgb(double red, double green, double blue) {
        return 0xff000000 |
                ((int) (red * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) << 8) |
                (int) (blue * 255.0f + 0.5f);
    }

    private Drawable getDrawable(Key key, int[] drawableState) {
        Drawable d = null;
        String l = key.getLabel();
        String s = null;
        if (mKeyboard.isShifted())
            s = "s";
        else if (mKeyboard.isCtrled())
            s = "c";
        else if (mKeyboard.isAlted())
            s = "a";
        /*if (isShift()) {
            if (isNormal(drawableState)) {
                if (on) {
                    d = BackUtil.get(Trime.getService(), "key_shift_on");
                } else {
                    d = BackUtil.get(Trime.getService(), "key_shift_off");
                }
            } else {
                if (on) {
                    d = BackUtil.get(Trime.getService(), "hkey_shift_on");
                } else {
                    d = BackUtil.get(Trime.getService(), "hkey_shift_off");
                }
            }
            if (d != null)
                return d;
        }*/
        boolean nl = key.isNormal(drawableState);
        String h = nl ? null : "h";
        String o = key.isOn() ? "_on" : null;
        d = getDrawable(s, h, l, o);
        if (d != null)
            return d;
        return null;
    }

    private Drawable getDrawable(String s, String h, String l, String o) {
        Drawable d = null;
        Trime trime = Trime.getService();

        if (s != null && h != null && o != null) {
            d = BackUtil.get(trime, String.format("%s%skey_%s%s", s, h, l, o));
            if (d != null)
                return d;
        }

        if (h != null && o != null) {
            d = BackUtil.get(trime, String.format("%skey_%s%s", h, l, o));
            if (d != null)
                return d;
        }

        if (s != null && o != null) {
            d = BackUtil.get(trime, String.format("%skey_%s%s", s, l, o));
            if (d != null)
                return d;
        }

        if (o != null) {
            d = BackUtil.get(trime, String.format("key_%s%s", l, o));
            if (d != null)
                return d;
        }

        if (s != null && h != null) {
            d = BackUtil.get(trime, String.format("%s%skey_%s", s, h, l));
            if (d != null)
                return d;
        }

        if (s != null) {
            d = BackUtil.get(trime, String.format("%skey_%s", s, l));
            if (d != null)
                return d;
        }

        if (h != null) {
            d = BackUtil.get(trime, String.format("%skey_%s", h, l));
            if (d != null)
                return d;
        }

        d = BackUtil.get(trime, String.format("key_%s", l));
        if (d != null)
            return d;
        return null;
    }

    private ArrayList<Key> mHasInvalidateKeys = new ArrayList<>();
    private Runnable mHasInvalidateKeysRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "InvalidateKeysRunnable: " + System.currentTimeMillis());
            invalidateKeys(mHasInvalidateKeys);
        }
    };

    private void onBufferDraw() {
        removeCallbacks(mHasInvalidateKeysRunnable);
        if (mBuffer == null || mKeyboardChanged) {
            if (mBuffer == null
                    || mKeyboardChanged
                    && (mBuffer.getWidth() != getWidth() || mBuffer.getHeight() != getHeight())) {
                // Make sure our bitmap is at least 1x1
                final int width = Math.max(1, getWidth());
                final int height = Math.max(1, getHeight());
                mBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBuffer);
            }
            invalidateAllKeys();
            mKeyboardChanged = false;
        }

        if (mKeyboard == null) return;

        mCanvas.save();
        final Canvas canvas = mCanvas;
        canvas.clipRect(mDirtyRect);

        final Paint paint = mPaint;
        Drawable keyBackground;
        final Rect clipRegion = mClipRegion;
        final Rect padding = mPadding;
        final int kbdPaddingLeft = getPaddingLeft();
        final int kbdPaddingTop = getPaddingTop();
        final Key[] keys = mKeys;
        final Key invalidKey = mInvalidatedKey;

        boolean drawSingleKey = false;
        if (invalidKey != null && canvas.getClipBounds(clipRegion)) {
            // Is clipRegion completely contained within the invalidated key?
            if (invalidKey.getX() + kbdPaddingLeft - 1 <= clipRegion.left
                    && invalidKey.getY() + kbdPaddingTop - 1 <= clipRegion.top
                    && invalidKey.getX() + invalidKey.getWidth() + kbdPaddingLeft + 1 >= clipRegion.right
                    && invalidKey.getY() + invalidKey.getHeight() + kbdPaddingTop + 1 >= clipRegion.bottom) {
                drawSingleKey = true;
            }
        }
        canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
        final int keyCount = keys.length;
        final float symbolBase = padding.top - mPaintSymbol.getFontMetrics().top;
        final float hintBase = -padding.bottom - mPaintSymbol.getFontMetrics().bottom;
        for (int i = 0; i < keyCount; i++) {
            final Key key = keys[i];
            if (drawSingleKey && invalidKey != key) {
                continue;
            }
            int[] drawableState = key.getCurrentDrawableState();
            /*keyBackground = getDrawable(key, drawableState);
            if (keyBackground == null)*/
            keyBackground = key.getBackColorForState(drawableState);
            //Log.i(TAG, "onBufferDraw: 1"+keyBackground);
            /*if (keyBackground == null) {
                try {
                    int index = (int) getStateDrawableIndex.invoke(mKeyBackColor, drawableState);
                    keyBackground = (Drawable) getStateDrawable.invoke(mKeyBackColor, index);
                } catch (Exception ex) {
                    Log.e(TAG, "Get Drawable Exception" + ex);
                }
            }
            Log.i(TAG, "onBufferDraw: 2"+keyBackground);*/
            if (keyBackground == null) {
                keyBackground = mKeyBackColorMap.get(drawableState);
            }
            if (keyBackground == null) {
                mKeyBackColor.setState(drawableState);
                Drawable.ConstantState state = mKeyBackColor.getConstantState();
                if(state!=null)
                    keyBackground = state.newDrawable();
                else
                    keyBackground=mKeyBackColor;
            }
            float keyRoundCorner = mRoundCorner;
            if (keyBackground instanceof GradientDrawable) {
                GradientDrawable gd = (GradientDrawable) keyBackground;
                float[] rs = key.getRound_corners() != null ? key.getRound_corners() : mKeyboard.getRoundCorners();
                if (rs != null) {
                    gd.setCornerRadii(rs);
                } else {
                    //Log.i(TAG, "onBufferDraw: m " + mRoundCorner + " k " + key.getRound_corner() + " b " + mKeyboard.getRoundCorner());
                    keyRoundCorner = key.getRound_corner() != null ? key.getRound_corner() : mKeyboard.getRoundCorner();
                    gd.setCornerRadius(keyRoundCorner);
                }
                /*if (mKeyborderColor != null && key.getborderColor() == null)
                    gd.setStroke(mKeyborder, mKeyborderColor);*/
            }
            Integer color = key.getTextColorForState(drawableState);
            mPaint.setColor(color != null ? color : mKeyTextColor.getColorForState(drawableState, 0));
            color = key.getSymbolColorForState(drawableState);
            mPaintSymbol.setColor(
                    color != null ? color : (key.isPressed() ? hilited_key_symbol_color : key_symbol_color));

            // Switch the character to uppercase if shift is pressed
            String label = key.getLabel();
            String hint = key.getHint();
            int left = (key.getWidth() - padding.left - padding.right) / 2 + padding.left;
            int top = padding.top;
            if (colors_keyboard) {
                int clr = rgb(Math.random(), Math.random(), Math.random());
                mPreviewText.getBackground().setFilterBitmap(true);
                mPreviewText.getBackground().setColorFilter(clr, PorterDuff.Mode.SRC_IN);
                //keyBackground.setColorFilter(clr, PorterDuff.Mode.OVERLAY);
                if (drawableState.length > 0 && drawableState[0] == android.R.attr.state_pressed) {
                    //keyBackground.setBounds(4, 4, key.getWidth() - 2, key.getHeight() - 2);
                    //canvas.translate(0,10);
                    keyBackground.setFilterBitmap(true);
                    keyBackground.setColorFilter(clr, PorterDuff.Mode.SRC_IN);
                    /*if (keyBackground instanceof GradientDrawable) {
                        keyBackground = new ColorDrawable(clr);
                    }*/
                }
            }

            final Rect bounds = keyBackground.getBounds();
            if (key.getWidth() != bounds.right || key.getHeight() != bounds.bottom) {
                keyBackground.setBounds(0, 0, key.getWidth(), key.getHeight());
            }
            canvas.translate(key.getX() + kbdPaddingLeft, key.getY() + kbdPaddingTop);
            keyBackground.setAlpha(key_alpha);
            keyBackground.draw(canvas);
            keyBackground.setColorFilter(null);
            if (keyBackground instanceof LuaBitmapDrawable) {
                LuaBitmapDrawable kb = (LuaBitmapDrawable) keyBackground;
                if (kb.isHasInvalidate()) {
                    if (!mHasInvalidateKeys.contains(key))
                        mHasInvalidateKeys.add(key);
                } else {
                    if (mHasInvalidateKeys.contains(key))
                        mHasInvalidateKeys.remove(key);
                }
            }
            if (!Function.isEmpty(label)) {
                // For characters, use large font. For labels like "Done", use small font.
                int len = label.length();
                if (key.getKey_text_size() != null) {
                    paint.setTextSize(key.getKey_text_size());
                } else {
                    paint.setTextSize(label.codePointCount(0, label.length()) > 1 ? mLabelTextSize : mKeyTextSize);
                }
                // Draw a drop shadow for the text
                paint.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);
                boolean doubleLine = false;
                if (len > 2 && paint.measureText(label, 0, len) > key.getWidth()) {
                    int size = (int) paint.getTextSize();
                    int min = Config.getPixel(16f);
                    for (int size1 = size - 1; size1 > min; size1--) {
                        paint.setTextSize(size1);
                        if (paint.measureText(label, 0, len) < key.getWidth()) {
                            paint.setTextSize(size1 - 1);
                            break;
                        }
                    }
                    float l = paint.measureText("..", 0, 2);
                    boolean hasLong = !mShowHint || (Function.isEmpty(hint) && key.getLongClick() == null);
                    for (int i1 = 2; i1 < len; i1++) {
                        if (paint.measureText(label, 0, i1) + l > key.getWidth()) {
                            if (hasLong && paint.getTextSize() < key.getHeight() / 2) {
                                canvas.drawText(
                                        label, 0, i1,
                                        left + key.getKey_text_offset_x(),
                                        (key.getHeight() - padding.top - padding.bottom) / 2
                                                //+ (paint.getTextSize() - paint.descent()) / 2
                                                + top
                                                + key.getKey_text_offset_y(),
                                        paint);
                                label = label.substring(i1);
                                len = label.length();
                                for (int i2 = 2; i2 < len; i2++) {
                                    if (paint.measureText(label, 0, i2) + l > key.getWidth()) {
                                        if (i2 < 4)
                                            label = label.substring(0, i2);
                                        else
                                            label = label.substring(0, i2 - 2) + "..";
                                        break;
                                    }
                                }
                                canvas.drawText(
                                        label,
                                        left + key.getKey_text_offset_x(),
                                        (key.getHeight() - padding.top - padding.bottom) / 2
                                                + (paint.getTextSize() - paint.descent())
                                                + (paint.getTextSize() - paint.descent()) / 2
                                                + top
                                                + key.getKey_text_offset_y(),
                                        paint);
                                doubleLine = true;
                                break;
                            }
                            label = label.substring(0, i1 - 2) + "..";
                            break;
                        }
                    }
                }
                // Draw the text
                if (!doubleLine) {

                    final Drawable bmp = getDrawableObject(key, label);
                    //Log.i(TAG, "drawCandidates: " + label + ";" + bmp);
                    if (bmp != null) {
                        bmp.setBounds(key.getKey_text_offset_x(),
                                (key.getHeight() - padding.top - padding.bottom) / 8 + key.getKey_text_offset_y(), key.getWidth(), key.getHeight());
                        bmp.draw(canvas);
                    } else {
                        canvas.drawText(
                                label,
                                left + key.getKey_text_offset_x(),
                                (key.getHeight() - padding.top - padding.bottom) / 2
                                        + (paint.getTextSize() - paint.descent()) / 2
                                        + top
                                        + key.getKey_text_offset_y(),
                                paint);
                    }
                }
                if (mShowHint) {
                    if (KEY_LONGPRESS && key.getLongClick() != null) {
                        mPaintSymbol.setTextSize(
                                key.getSymbol_text_size() != null ? key.getSymbol_text_size() : mSymbolSize);
                        mPaintSymbol.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);
                        canvas.drawText(
                                key.getSymbolLabel(),
                                left + key.getKey_symbol_offset_x(),
                                symbolBase + key.getKey_symbol_offset_y(),
                                mPaintSymbol);
                    }

                    if (!Function.isEmpty(hint) /*&& !Rime.isAsciiMode()*/) {
                        mPaintSymbol.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);
                        canvas.drawText(
                                hint,
                                left + key.getKey_hint_offset_x(),
                                key.getHeight() + hintBase + key.getKey_hint_offset_y(),
                                mPaintSymbol);
                    }
                }

                // Turn off drop shadow
                paint.setShadowLayer(0, 0, 0, 0);
            }
            canvas.translate(-key.getX() - kbdPaddingLeft, -key.getY() - kbdPaddingTop);
        }
        mInvalidatedKey = null;
        // Overlay a dark rectangle to dim the keyboard
        if (mMiniKeyboardOnScreen) {
            paint.setColor((int) (mBackgroundDimAmount * 0xFF) << 24);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }

        boolean mShowTouchPoints = true;
        if (DEBUG && mShowTouchPoints) {
            paint.setAlpha(128);
            paint.setColor(0xFFFF0000);
            canvas.drawCircle(mStartX, mStartY, 3, paint);
            canvas.drawLine(mStartX, mStartY, mLastX, mLastY, paint);
            paint.setColor(0xFF0000FF);
            canvas.drawCircle(mLastX, mLastY, 3, paint);
            paint.setColor(0xFF00FF00);
            canvas.drawCircle((mStartX + mLastX) / 2, (mStartY + mLastY) / 2, 2, paint);
        }
        mCanvas.restore();
        mDrawPending = false;
        mDirtyRect.setEmpty();
        if (!mHasInvalidateKeys.isEmpty()) {
            postDelayed(mHasInvalidateKeysRunnable, 50);
        }
    }

    private HashMap<String, Drawable> cache = new HashMap<>();

    private Drawable getDrawableObject(Key key, String label) {
        if (key.isPressed())
            label = "hlabel_" + label;
        else
            label = "label_" + label;

        if (cache.containsKey(label))
            return cache.get(label);
        Drawable bmp = config.getDrawableObject(label);
        cache.put(label, bmp);
        return bmp;
    }

    private int getKeyIndices(int x, int y, int[] allKeys) {
        //if (mMiniKeyboardOffsetX != 0) {
        x = Math.max(0, Math.min(x, getWidth()));
        y = Math.max(0, Math.min(y, getHeight()));
        //}
        final Key[] keys = mKeys;
        int primaryIndex = NOT_A_KEY;
        int closestKey = NOT_A_KEY;
        int closestKeyDist = mProximityThreshold + 1;
        java.util.Arrays.fill(mDistances, Integer.MAX_VALUE);
        int[] nearestKeyIndices = mKeyboard.getNearestKeys(x, y);
        final int keyCount = nearestKeyIndices.length;
        for (int i = 0; i < keyCount; i++) {
            final Key key = keys[nearestKeyIndices[i]];
            int dist = 0;
            boolean isInside = key.isInside(x, y);
            if (isInside) {
                primaryIndex = nearestKeyIndices[i];
            }

            if ((mProximityCorrectOn && (dist = key.squaredDistanceFrom(x, y)) < mProximityThreshold)
                    || isInside) {
                // Find insertion point
                final int nCodes = 1;
                if (dist < closestKeyDist) {
                    closestKeyDist = dist;
                    closestKey = nearestKeyIndices[i];
                }

                if (allKeys == null) continue;

                for (int j = 0; j < mDistances.length; j++) {
                    if (mDistances[j] > dist) {
                        // Make space for nCodes codes
                        System.arraycopy(mDistances, j, mDistances, j + nCodes, mDistances.length - j - nCodes);
                        System.arraycopy(allKeys, j, allKeys, j + nCodes, allKeys.length - j - nCodes);
                        allKeys[j] = key.getCode();
                        mDistances[j] = dist;
                        break;
                    }
                }
            }
        }
        if (primaryIndex == NOT_A_KEY) {
            primaryIndex = closestKey;
        }
        return primaryIndex;
    }

    private void releaseKey(int code) {
        if (mComboMode) {
            if (mComboCount > 9) mComboCount = 9;
            mComboCodes[mComboCount++] = code;
        } else {
            mKeyboardActionListener.onRelease(code);
            if (mComboCount > 0) {
                for (int i = 0; i < mComboCount; i++) {
                    mKeyboardActionListener.onRelease(mComboCodes[i]);
                }
                mComboCount = 0;
            }
        }
    }

    private void detectAndSendKey(int index, int x, int y, long eventTime, int type) {
        if (index != NOT_A_KEY && index < mKeys.length) {
            final Key key = mKeys[index];
            if (key.isShift() && !key.sendBindings(type)) {
                setShifted(key.isShiftLock(), !isShifted());
            } else {
                if (key.getClick().isRepeatable()) {
                    if (type > 0) mAbortKey = true;
                    if (!key.hasEvent(type)) return;
                }
                int code = key.getCode(type);
                //TextEntryState.keyPressedAt(key, x, y);
                int[] codes = new int[MAX_NEARBY_KEYS];
                Arrays.fill(codes, NOT_A_KEY);
                getKeyIndices(x, y, codes);
                mKeyboardActionListener.onEvent(key.getEvent(type));
                releaseKey(code);
                resetShifted();
            }
            mLastSentIndex = index;
            mLastTapTime = eventTime;
        }
    }

    private void detectAndSendKey(int index, int x, int y, long eventTime) {
        detectAndSendKey(index, x, y, eventTime, 0);
    }

    private void showPreview(int keyIndex, int type) {
        int oldKeyIndex = mCurrentKeyIndex;
        final PopupWindow previewPopup = mPreviewPopup;

        mCurrentKeyIndex = keyIndex;
        // Release the old key and press the new key
        final Key[] keys = mKeys;
        if (oldKeyIndex != mCurrentKeyIndex) {
            if (oldKeyIndex != NOT_A_KEY && keys.length > oldKeyIndex) {
                Key oldKey = keys[oldKeyIndex];
                oldKey.onReleased(mCurrentKeyIndex == NOT_A_KEY);
                invalidateKey(oldKeyIndex);
            }
            if (mCurrentKeyIndex != NOT_A_KEY && keys.length > mCurrentKeyIndex) {
                Key newKey = keys[mCurrentKeyIndex];
                if (type == KeyEventType.CLICK.ordinal())
                    mKeyboardActionListener.onPress(newKey.getCode());
                if (mInHover)
                    mKeyboardActionListener.onPress(newKey);
                newKey.onPressed();
                invalidateKey(mCurrentKeyIndex);
            }
        }
        // If key changed and preview is on ...
        if (oldKeyIndex != mCurrentKeyIndex && mShowPreview) {
            mHandler.removeMessages(MSG_SHOW_PREVIEW);
            if (previewPopup.isShowing()) {
                if (keyIndex == NOT_A_KEY) {
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(MSG_REMOVE_PREVIEW), DELAY_AFTER_PREVIEW);
                }
            }
            if (keyIndex != NOT_A_KEY) {
                if (previewPopup.isShowing() && mPreviewText.getVisibility() == VISIBLE) {
                    // Show right away, if it's already visible and finger is moving around
                    showKey(keyIndex, type);
                } else {
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(MSG_SHOW_PREVIEW, keyIndex, type), DELAY_BEFORE_PREVIEW);
                }
            }
        } /*else if (mShowPreview) {
            if (keyIndex != NOT_A_KEY) {
                if (previewPopup.isShowing() && mPreviewText.getVisibility() == VISIBLE) {
                    // Show right away, if it's already visible and finger is moving around
                    if (keyIndex < 0 || keyIndex >= mKeys.length) return;
                    Key key = keys[keyIndex];
                    mPreviewText.setText(key.getPreviewText(type));
                }
            }
        }*/
    }

    private void showPreview(int keyIndex) {
        showPreview(keyIndex, 0);
    }

    private void showKey(final int keyIndex, int type) {
        if (isTouchExplorationEnabled)
            return;
        final PopupWindow previewPopup = mPreviewPopup;
        final Key[] keys = mKeys;
        if (keyIndex < 0 || keyIndex >= mKeys.length) return;
        Key key = keys[keyIndex];
        //mKeyboardActionListener.onPress(key);
        mPreviewText.setCompoundDrawables(null, null, null, null);
        mPreviewText.setText(key.getPreviewText(type));
        if (mPreviewText.getText().length() > 10)
            mPreviewText.setTextSize(16);
        else
            mPreviewText.setTextSize(mPreviewTextSizeLarge);
        mPreviewText.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int popupWidth =
                Math.min(Math.max(
                        mPreviewText.getMeasuredWidth(),
                        key.getWidth() + mPreviewText.getPaddingLeft() + mPreviewText.getPaddingRight()), getWidth() / 3 * 2);
        final int popupHeight = Math.min(getHeight() / 3 * 2, mPreviewText.getMeasuredHeight());
        ViewGroup.LayoutParams lp = mPreviewText.getLayoutParams();
        if (lp != null) {
            lp.width = popupWidth;
            lp.height = popupHeight;
        }
        int mPopupPreviewY;
        int mPopupPreviewX;
        boolean mPreviewCentered = false;
        if (!mPreviewCentered) {
            mPopupPreviewX = key.getX() - mPreviewText.getPaddingLeft() + getPaddingLeft();
            mPopupPreviewY = key.getY() - popupHeight + mPreviewOffset;
        } else {
            // TODO: Fix this if centering is brought back
            mPopupPreviewX = 160 - mPreviewText.getMeasuredWidth() / 2;
            mPopupPreviewY = -mPreviewText.getMeasuredHeight();
        }
        mHandler.removeMessages(MSG_REMOVE_PREVIEW);
        getLocationInWindow(mCoordinates);
        mCoordinates[0] += mMiniKeyboardOffsetX; // Offset may be zero
        mCoordinates[1] += mMiniKeyboardOffsetY; // Offset may be zero

        // Set the preview background state
        mPreviewText
                .getBackground()
                .setState(key.getPopupResId() != 0 ? LONG_PRESSABLE_STATE_SET : EMPTY_STATE_SET);
        mPopupPreviewX += mCoordinates[0];
        mPopupPreviewY += mCoordinates[1];

        // If the popup cannot be shown above the key, put it on the side
        getLocationOnScreen(mCoordinates);
        if (mPopupPreviewY + mCoordinates[1] < 0) {
            // If the key you're pressing is on the left side of the keyboard, show the popup on
            // the right, offset by enough to see at least one key to the left/right.
            if (key.getX() + key.getWidth() <= getWidth() / 2) {
                mPopupPreviewX += (int) (key.getWidth() * 2.5);
            } else {
                mPopupPreviewX -= (int) (key.getWidth() * 2.5);
            }
            mPopupPreviewY += popupHeight;
        }

        if (previewPopup.isShowing()) {
            //previewPopup.update(mPopupPreviewX, mPopupPreviewY, popupWidth, popupHeight);
            previewPopup.dismiss(); //禁止窗口動畫
        }
        previewPopup.setAnimationStyle(R.style.popupAnim);
        previewPopup.setWidth(popupWidth);
        previewPopup.setHeight(popupHeight);
        previewPopup.showAtLocation(mPopupParent, Gravity.NO_GRAVITY, mPopupPreviewX, mPopupPreviewY);
        mPreviewText.setVisibility(VISIBLE);

    }

    /**
     * Requests a redraw of the entire keyboard. Calling {@link #invalidate} is not sufficient because
     * the keyboard renders the keys to an off-screen buffer and an invalidate() only draws the cached
     * buffer.
     *
     * @see #invalidateKey(int)
     */
    public void invalidateAllKeys() {
        mHasInvalidateKeys.clear();
        mDirtyRect.union(0, 0, getWidth(), getHeight());
        mDrawPending = true;
        postInvalidate();
    }

    /**
     * Invalidates a key so that it will be redrawn on the next repaint. Use this method if only one
     * key is changing it's content. Any changes that affect the position or size of the key may not
     * be honored.
     *
     * @param keyIndex the index of the key in the attached {@link Keyboard}.
     * @see #invalidateAllKeys
     */
    private void invalidateKey(int keyIndex) {
        if (mKeys == null) return;
        if (keyIndex < 0 || keyIndex >= mKeys.length) {
            return;
        }
        final Key key = mKeys[keyIndex];
        mInvalidatedKey = key;
        mDirtyRect.union(
                key.getX() + getPaddingLeft(),
                key.getY() + getPaddingTop(),
                key.getX() + key.getWidth() + getPaddingLeft(),
                key.getY() + key.getHeight() + getPaddingTop());
        onBufferDraw();
        invalidate(
                key.getX() + getPaddingLeft(),
                key.getY() + getPaddingTop(),
                key.getX() + key.getWidth() + getPaddingLeft(),
                key.getY() + key.getHeight() + getPaddingTop());
    }

    private void invalidateKeys(List<Key> keys) {
        if (keys == null || keys.size() == 0) return;
        for (Key key : keys) {
            mDirtyRect.union(
                    key.getX() + getPaddingLeft(),
                    key.getY() + getPaddingTop(),
                    key.getX() + key.getWidth() + getPaddingLeft(),
                    key.getY() + key.getHeight() + getPaddingTop());
        }
        onBufferDraw();
        postInvalidate();
    }

    public void invalidateComposingKeys() {
        List<Key> keys = mKeyboard.getComposingKeys();
        if (keys != null && keys.size() > 5) invalidateAllKeys();
        else invalidateKeys(keys);
    }

    private boolean openPopupIfRequired(MotionEvent me) {
        // Check if we have a popup layout specified first.
        if (mPopupLayout == 0) {
            return false;
        }
        if (mCurrentKey < 0 || mCurrentKey >= mKeys.length) {
            return false;
        }
        if (!KEY_LONGPRESS)
            return false;
        int code = mKeys[mCurrentKey].getCode();
        if (isTouchExplorationEnabled)
            return false;
        showPreview(NOT_A_KEY);
        showPreview(mCurrentKey, KeyEventType.LONG_CLICK.ordinal());
        Key popupKey = mKeys[mCurrentKey];
        boolean result = onLongPress(popupKey);
        if (result) {
            mAbortKey = true;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPreview(NOT_A_KEY);
                }
            }, 100);
        }
        return result;
    }

    /**
     * Called when a key is long pressed. By default this will open any popup keyboard associated with
     * this key through the attributes popupLayout and popupCharacters.
     *
     * @param popupKey the key that was long pressed
     * @return true if the long press is handled, false otherwise. Subclasses should call the method
     * on the base class if the subclass doesn't wish to handle the call.
     */
    private boolean onLongPress(Key popupKey) {
        int popupKeyboardId = popupKey.getPopupResId();
        if (popupKeyboardId != 0) {
            View mMiniKeyboardContainer = mMiniKeyboardCache.get(popupKey);
            if (mMiniKeyboardContainer == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mMiniKeyboardContainer = inflater.inflate(mPopupLayout, null);
                mMiniKeyboard =
                        (KeyboardView) mMiniKeyboardContainer.findViewById(android.R.id.keyboardView);
                View closeButton = mMiniKeyboardContainer.findViewById(android.R.id.closeButton);
                if (closeButton != null) closeButton.setOnClickListener(this);
                mMiniKeyboard.setOnKeyboardActionListener(
                        new OnKeyboardActionListener() {
                            @Override
                            public void onEvent(Event event) {
                                mKeyboardActionListener.onEvent(event);
                                dismissPopupKeyboard();
                            }

                            @Override
                            public void onKey(int primaryCode, int mask) {
                                mKeyboardActionListener.onKey(primaryCode, mask);
                                dismissPopupKeyboard();
                            }

                            @Override
                            public void onText(CharSequence text) {
                                mKeyboardActionListener.onText(text);
                                dismissPopupKeyboard();
                            }

                            @Override
                            public void swipeLeft() {
                            }

                            @Override
                            public void swipeRight() {
                            }

                            @Override
                            public void swipeUp() {
                            }

                            @Override
                            public void onUp(int code) {

                            }

                            @Override
                            public void swipeDown() {
                            }

                            @Override
                            public void onPress(int primaryCode) {
                                mKeyboardActionListener.onPress(primaryCode);
                            }

                            @Override
                            public void onPress(Key primaryCode) {
                                mKeyboardActionListener.onPress(primaryCode);
                            }

                            @Override
                            public void onRelease(int primaryCode) {
                                mKeyboardActionListener.onRelease(primaryCode);
                            }
                        });
                //mInputView.setSuggest(mSuggest);
                Keyboard keyboard;
                if (popupKey.getPopupKeys() != null) {
                    keyboard =
                            new Keyboard(
                                    getContext(),
                                    popupKey.getPopupKeys(),
                                    -1,
                                    getPaddingLeft() + getPaddingRight());
                } else if (popupKey.getPopupCharacters() != null) {
                    keyboard =
                            new Keyboard(
                                    getContext(),
                                    popupKey.getPopupCharacters(),
                                    -1,
                                    getPaddingLeft() + getPaddingRight());
                } else {
                    keyboard = new Keyboard(getContext());
                }
                mMiniKeyboard.setKeyboard(keyboard);
                mMiniKeyboard.setPopupParent(this);
                mMiniKeyboard.setPreviewEnabled(false);
                mMiniKeyboardContainer.measure(
                        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));

                mMiniKeyboardCache.put(popupKey, mMiniKeyboardContainer);
            } else {
                mMiniKeyboard =
                        (KeyboardView) mMiniKeyboardContainer.findViewById(android.R.id.keyboardView);
            }
            getLocationInWindow(mCoordinates);
            int mPopupX = popupKey.getX() + getPaddingLeft();
            int mPopupY = popupKey.getY() + getPaddingTop();
            mPopupX = mPopupX + popupKey.getWidth() / 2 - mMiniKeyboardContainer.getMeasuredWidth() / 2;
            if (mPopupX < 0)
                mPopupX = 0;
            else if (mPopupX > getWidth() - mMiniKeyboardContainer.getMeasuredWidth())
                mPopupX = getWidth() - mMiniKeyboardContainer.getMeasuredWidth();

            mPopupY = mPopupY - mMiniKeyboardContainer.getMeasuredHeight();
            final int x = mPopupX + mMiniKeyboardContainer.getPaddingRight() + mCoordinates[0];
            final int y = mPopupY + mMiniKeyboardContainer.getPaddingBottom() + mCoordinates[1];
            mMiniKeyboard.setPopupOffset(x < 0 ? 0 : x, y);
            mMiniKeyboard.setShifted(false, isShifted());
            mPopupKeyboard.setContentView(mMiniKeyboardContainer);
            mPopupKeyboard.setWidth(mMiniKeyboardContainer.getMeasuredWidth());
            mPopupKeyboard.setHeight(mMiniKeyboardContainer.getMeasuredHeight());
            mPopupKeyboard.setAnimationStyle(R.style.popupAnim);
            mPopupKeyboard.showAtLocation(this, Gravity.LEFT | Gravity.TOP, x, y);
            mMiniKeyboardOnScreen = true;
            mPopupTouchOffsetX = 0 - x;
            mPopupTouchOffsetY = -popupKey.getY();
            invalidateAllKeys();
            return true;
        } else {
            Key key = popupKey;
            if (key.getLongClick() != null) {
                removeMessages();
                mAbortKey = true;
                Event e = key.getLongClick();
                mKeyboardActionListener.onEvent(e);
                releaseKey(e.getCode());
                resetShifted();
                return true;
            }
            if (key.isShift() && !key.sendBindings(KeyEventType.LONG_CLICK.ordinal())) {
                setShifted(!key.isOn(), !key.isOn());
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onHoverEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_HOVER_ENTER: {
                    mInHover = true;
                    Trime.getService().onHover();
                    event.setAction(MotionEvent.ACTION_DOWN);
                }
                break;
                case MotionEvent.ACTION_HOVER_MOVE: {
                    event.setAction(MotionEvent.ACTION_MOVE);
                }
                break;
                case MotionEvent.ACTION_HOVER_EXIT: {
                    mInHover = false;
                    if (event.getY() < 8)
                        event.setAction(MotionEvent.ACTION_CANCEL);
                    else
                        event.setAction(MotionEvent.ACTION_UP);
                }
                break;
            }
            return onTouchEvent(event);
        }
        return true;
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private boolean popupDown = false;

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        //mRippleHelper.onTouchEvent(me);
        if (mMiniKeyboardOnScreen) {
            if (!popupDown)
                me.setAction(MotionEvent.ACTION_DOWN);
            popupDown = true;
            me.offsetLocation(mPopupTouchOffsetX, mPopupTouchOffsetY);
            //Log.i(TAG, "onTouchEvent: " + me.toString());
            mMiniKeyboard.onTouchEvent(me);
            if(me.getAction()==MotionEvent.ACTION_UP)
                dismissPopupKeyboard();
            return true;
        }
        popupDown = false;

        // Convert multi-pointer up/down events to single up/down events to
        // deal with the typical multi-pointer behavior of two-thumb typing
        final int index = me.getActionIndex();
        final int pointerCount = me.getPointerCount();
        final int action = me.getActionMasked();
        boolean result = false;
        final long now = me.getEventTime();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
            mInHover = false;

        mComboMode = false;
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_CANCEL) {
            //Log.i(TAG, "onTouchEvent: down");
            mComboCount = 0;
            mFling = false;
        } else if (pointerCount > 1
                || action == MotionEvent.ACTION_POINTER_DOWN
                || action == MotionEvent.ACTION_POINTER_UP) {
            mComboMode = true;
        }

        if (action == MotionEvent.ACTION_POINTER_UP
                || (mOldPointerCount > 1 && action == MotionEvent.ACTION_UP)) {
            //並擊鬆開前的虛擬按鍵事件
            MotionEvent ev =
                    MotionEvent.obtain(
                            now,
                            now,
                            MotionEvent.ACTION_POINTER_DOWN,
                            me.getX(index),
                            me.getY(index),
                            me.getMetaState());
            result = onModifiedTouchEvent(ev, false);
            ev.recycle();
        }

        if (action == MotionEvent.ACTION_POINTER_DOWN) {
            //並擊中的按鍵事件，需要按鍵提示
            MotionEvent ev =
                    MotionEvent.obtain(
                            now, now, MotionEvent.ACTION_DOWN, me.getX(index), me.getY(index), me.getMetaState());
            result = onModifiedTouchEvent(ev, false);
            ev.recycle();
        } else {
            result = onModifiedTouchEvent(me, false);
        }

        if (action != MotionEvent.ACTION_MOVE)
            mOldPointerCount = pointerCount;
        performClick();
        return result;
    }

    private boolean onModifiedTouchEvent(MotionEvent me, boolean possiblePoly) {
        final int pointerCount = me.getPointerCount();
        final int index = me.getActionIndex();
        int touchX = (int) me.getX(index) - getPaddingLeft();
        int touchY = (int) me.getY(index) - getPaddingTop();
        if (touchY >= -mVerticalCorrection) touchY += mVerticalCorrection;
        final int action = me.getActionMasked();
        final long eventTime = me.getEventTime();
        int keyIndex = getKeyIndices(touchX, touchY, null);
        if (touchY < 0 && mInHover) {
            keyIndex = NOT_A_KEY;
            mCurrentKey = keyIndex;
        }

        mPossiblePoly = possiblePoly;

        // Track the last few movements to look for spurious swipes.
        if (action == MotionEvent.ACTION_DOWN) mSwipeTracker.clear();
        mSwipeTracker.addMovement(me);

        // Ignore all motion events until a DOWN.
        if (mAbortKey && action == MotionEvent.ACTION_UP && keyIndex != NOT_A_KEY) {
            mKeyboardActionListener.onUp(mKeys[keyIndex].getCode());
            return true;
        }

        if (mAbortKey && action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_CANCEL) {
            return true;
        }

        if (KEY_SWIPE && !mInHover && mGestureDetector.onTouchEvent(me)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPreview(NOT_A_KEY);
                }
            }, 150);
            mHandler.removeMessages(MSG_REPEAT);
            mLastRepeatKeyIndex = NOT_A_KEY;
            mHandler.removeMessages(MSG_LONGPRESS);
            return true;
        }

        // Needs to be called after the gesture detector gets a turn, as it may have
        // displayed the mini keyboard
        if (mMiniKeyboardOnScreen && action != MotionEvent.ACTION_CANCEL) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mLastRepeatKeyIndex=NOT_A_KEY;
                mAbortKey = false;
                mStartX = touchX;
                mStartY = touchY;
                mLastCodeX = touchX;
                mLastCodeY = touchY;
                mLastKeyTime = 0;
                mCurrentKeyTime = 0;
                mLastKey = NOT_A_KEY;
                mCurrentKey = keyIndex;
                mDownKey = keyIndex;
                mDownTime = me.getEventTime();
                mLastMoveTime = mDownTime;
                if (action == MotionEvent.ACTION_POINTER_DOWN) break; //並擊鬆開前的虛擬按鍵事件
                checkMultiTap(eventTime, keyIndex);
                if (!mInHover)
                    mKeyboardActionListener.onPress(keyIndex != NOT_A_KEY ? mKeys[keyIndex] : null);
                if (mCurrentKey >= 0 && mKeys[mCurrentKey].getClick().isRepeatable()) {
                    mRepeatKeyIndex = mCurrentKey;
                    Message msg = mHandler.obtainMessage(MSG_REPEAT);
                    mHandler.sendMessageDelayed(msg, REPEAT_START_DELAY);
                    // Delivering the key could have caused an abort
                    if (mAbortKey) {
                        mRepeatKeyIndex = NOT_A_KEY;
                        break;
                    }
                }
                if (mCurrentKey != NOT_A_KEY && (mKeys[keyIndex].hasEvent(KeyEventType.LONG_CLICK.ordinal()) || mKeys[keyIndex].isShift())) {
                    Message msg = mHandler.obtainMessage(MSG_LONGPRESS, me);
                    mHandler.sendMessageDelayed(msg, LONGPRESS_TIMEOUT);
                }
                showPreview(keyIndex, 0);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mFling && !mIsMinKeyboard) {
                    keyIndex = mCurrentKeyIndex;
                }
                boolean continueLongPress = false;
                if (keyIndex != NOT_A_KEY) {
                    if (mCurrentKey == NOT_A_KEY) {
                        mCurrentKey = keyIndex;
                        mCurrentKeyTime = eventTime - mDownTime;
                    } else {
                        if (keyIndex == mCurrentKey) {
                            mCurrentKeyTime += eventTime - mLastMoveTime;
                            continueLongPress = true;
                        } else if (mRepeatKeyIndex == NOT_A_KEY) {
                            resetMultiTap();
                            mLastKey = mCurrentKey;
                            mLastCodeX = mLastX;
                            mLastCodeY = mLastY;
                            mLastKeyTime = mCurrentKeyTime + eventTime - mLastMoveTime;
                            mCurrentKey = keyIndex;
                            mCurrentKeyTime = 0;
                        } else if (mRepeatKeyIndex != keyIndex) {
                            mLastRepeatKeyIndex=NOT_A_KEY;
                            resetMultiTap();
                            mHandler.removeMessages(MSG_REPEAT);
                            mRepeatKeyIndex = NOT_A_KEY;
                            mLastKey = mCurrentKey;
                            mLastCodeX = mLastX;
                            mLastCodeY = mLastY;
                            mLastKeyTime = mCurrentKeyTime + eventTime - mLastMoveTime;
                            mCurrentKey = keyIndex;
                            mCurrentKeyTime = 0;
                        }
                    }
                }
                if (!mComboMode && !continueLongPress) {
                    // Cancel old longpress
                    mHandler.removeMessages(MSG_LONGPRESS);
                    // Start new longpress if key has changed
                    if (keyIndex != NOT_A_KEY && mKeys[keyIndex].hasEvent(KeyEventType.LONG_CLICK.ordinal())) {
                        Message msg = mHandler.obtainMessage(MSG_LONGPRESS, me);
                        mHandler.sendMessageDelayed(msg, LONGPRESS_TIMEOUT);
                    }
                }
                showPreview(mCurrentKey);
                mLastMoveTime = eventTime;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                removeMessages();
                if (keyIndex == mCurrentKey) {
                    mCurrentKeyTime += eventTime - mLastMoveTime;
                } else {
                    resetMultiTap();
                    mLastKey = mCurrentKey;
                    mLastKeyTime = mCurrentKeyTime + eventTime - mLastMoveTime;
                    mCurrentKey = keyIndex;
                    mCurrentKeyTime = 0;
                }
                if (mCurrentKeyTime < mLastKeyTime
                        && mCurrentKeyTime < DEBOUNCE_TIME
                        && mLastKey != NOT_A_KEY) {
                    mCurrentKey = mLastKey;
                    touchX = mLastCodeX;
                    touchY = mLastCodeY;
                }
                showPreview(NOT_A_KEY);
                Arrays.fill(mKeyIndices, NOT_A_KEY);
                // If we're not on a repeating key (which sends on a DOWN event)
                mLastRepeatKeyIndex=mRepeatKeyIndex;
                if (mRepeatKeyIndex != NOT_A_KEY && !mAbortKey)
                    repeatKey();
                if (mRepeatKeyIndex == NOT_A_KEY && !mMiniKeyboardOnScreen && !mAbortKey) {
                    detectAndSendKey(
                            mCurrentKey,
                            touchX,
                            touchY,
                            eventTime,
                            (mOldPointerCount > 1 || mComboMode) ? KeyEventType.COMBO.ordinal() : 0);
                }
                invalidateKey(keyIndex);
                mRepeatKeyIndex = NOT_A_KEY;
                break;
            case MotionEvent.ACTION_CANCEL:
                removeMessages();
                dismissPopupKeyboard();
                mAbortKey = true;
                showPreview(NOT_A_KEY);
                invalidateKey(mCurrentKey);
                break;
        }
        mLastX = touchX;
        mLastY = touchY;
        return true;
    }

    private int mLastRepeatKeyIndex = NOT_A_KEY;

    private boolean repeatKey() {
        Key key = mKeys[mRepeatKeyIndex];
        detectAndSendKey(mCurrentKey, key.getX(), key.getY(), mLastTapTime);
        if (mLastRepeatKeyIndex != mRepeatKeyIndex)
            Trime.getService().onPress(key.getCode());
        mLastRepeatKeyIndex = mRepeatKeyIndex;
        return true;
    }

    private void swipeRight() {
        mKeyboardActionListener.swipeRight();
    }

    private void swipeLeft() {
        mKeyboardActionListener.swipeLeft();
    }

    private void swipeUp() {
        mKeyboardActionListener.swipeUp();
    }

    private void swipeDown() {
        mKeyboardActionListener.swipeDown();
    }

    public void closing() {
        if (mPreviewPopup.isShowing()) {
            mPreviewPopup.dismiss();
        }
        removeMessages();

        dismissPopupKeyboard();
        mBuffer = null;
        mCanvas = null;
        mMiniKeyboardCache.clear();
    }

    private void removeMessages() {
        mHandler.removeMessages(MSG_REPEAT);
        mHandler.removeMessages(MSG_LONGPRESS);
        mHandler.removeMessages(MSG_SHOW_PREVIEW);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closing();
    }

    private void dismissPopupKeyboard() {
        if (mPopupKeyboard.isShowing()) {
            mPopupKeyboard.dismiss();
            mMiniKeyboardOnScreen = false;
            invalidateAllKeys();
        }
    }

    public boolean handleBack() {
        if (mPopupKeyboard.isShowing()) {
            dismissPopupKeyboard();
            return true;
        }
        return false;
    }

    private void resetMultiTap() {
        mLastSentIndex = NOT_A_KEY;
        int mTapCount = 0;
        mLastTapTime = -1;
        boolean mInMultiTap = false;
    }

    private void checkMultiTap(long eventTime, int keyIndex) {
        if (keyIndex == NOT_A_KEY) return;
        Key key = mKeys[keyIndex];
        if (eventTime > mLastTapTime + MULTITAP_INTERVAL || keyIndex != mLastSentIndex) {
            resetMultiTap();
        }
    }

    /**
     * 識別滑動手勢
     */
    private static class SwipeTracker {

        static final int NUM_PAST = 4;
        static final int LONGEST_PAST_TIME = 200;

        final float mPastX[] = new float[NUM_PAST];
        final float mPastY[] = new float[NUM_PAST];
        final long mPastTime[] = new long[NUM_PAST];

        float mYVelocity;
        float mXVelocity;

        public void clear() {
            mPastTime[0] = 0;
        }

        public void addMovement(MotionEvent ev) {
            long time = ev.getEventTime();
            final int N = ev.getHistorySize();
            for (int i = 0; i < N; i++) {
                addPoint(ev.getHistoricalX(i), ev.getHistoricalY(i), ev.getHistoricalEventTime(i));
            }
            addPoint(ev.getX(), ev.getY(), time);
        }

        private void addPoint(float x, float y, long time) {
            int drop = -1;
            int i;
            final long[] pastTime = mPastTime;
            for (i = 0; i < NUM_PAST; i++) {
                if (pastTime[i] == 0) {
                    break;
                } else if (pastTime[i] < time - LONGEST_PAST_TIME) {
                    drop = i;
                }
            }
            if (i == NUM_PAST && drop < 0) {
                drop = 0;
            }
            if (drop == i) drop--;
            final float[] pastX = mPastX;
            final float[] pastY = mPastY;
            if (drop >= 0) {
                final int start = drop + 1;
                final int count = NUM_PAST - drop - 1;
                System.arraycopy(pastX, start, pastX, 0, count);
                System.arraycopy(pastY, start, pastY, 0, count);
                System.arraycopy(pastTime, start, pastTime, 0, count);
                i -= (drop + 1);
            }
            pastX[i] = x;
            pastY[i] = y;
            pastTime[i] = time;
            i++;
            if (i < NUM_PAST) {
                pastTime[i] = 0;
            }
        }

        public void computeCurrentVelocity(int units) {
            computeCurrentVelocity(units, Float.MAX_VALUE);
        }

        public void computeCurrentVelocity(int units, float maxVelocity) {
            final float[] pastX = mPastX;
            final float[] pastY = mPastY;
            final long[] pastTime = mPastTime;

            final float oldestX = pastX[0];
            final float oldestY = pastY[0];
            final long oldestTime = pastTime[0];
            float accumX = 0;
            float accumY = 0;
            int N = 0;
            while (N < NUM_PAST) {
                if (pastTime[N] == 0) {
                    break;
                }
                N++;
            }

            for (int i = 1; i < N; i++) {
                final int dur = (int) (pastTime[i] - oldestTime);
                if (dur == 0) continue;
                float dist = pastX[i] - oldestX;
                float vel = (dist / dur) * units; // pixels/frame.
                if (accumX == 0) accumX = vel;
                else accumX = (accumX + vel) * .5f;

                dist = pastY[i] - oldestY;
                vel = (dist / dur) * units; // pixels/frame.
                if (accumY == 0) accumY = vel;
                else accumY = (accumY + vel) * .5f;
            }
            mXVelocity = accumX < 0.0f ? Math.max(accumX, -maxVelocity) : Math.min(accumX, maxVelocity);
            mYVelocity = accumY < 0.0f ? Math.max(accumY, -maxVelocity) : Math.min(accumY, maxVelocity);
        }

        public float getXVelocity() {
            return mXVelocity;
        }

        public float getYVelocity() {
            return mYVelocity;
        }
    }
}
