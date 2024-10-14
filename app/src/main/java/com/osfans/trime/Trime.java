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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RoundRectDrawable;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androlua.LuaService;
import com.osfans.trime.pro.R;
import com.osfans.trime.enums.InlineModeType;
import com.osfans.trime.enums.WindowsPositionType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link InputMethodService 輸入法}主程序
 */
public class Trime extends LuaService
        implements KeyboardView.OnKeyboardActionListener, Candidate.CandidateListener {
    private static Logger Log = Logger.getLogger(Trime.class.getSimpleName());
    private static Trime self;
    private KeyboardView mKeyboardView; //軟鍵盤
    private KeyboardSwitch mKeyboardSwitch;
    private Config mConfig; //配置
    private Effect mEffect; //音效
    private Candidate mCandidate; //候選
    private Composition mComposition; //編碼
    private LinearLayout mCompositionContainer;
    private LinearLayout mCandidateContainer;
    private PopupWindow mFloatingWindow;
    private PopupTimer mFloatingWindowTimer = new PopupTimer();
    private RectF mPopupRectF = new RectF();
    private AlertDialog mOptionsDialog; //對話框

    private int orientation;
    private boolean canCompose;
    private boolean enterAsLineBreak;
    private boolean mShowWindow = true; //顯示懸浮窗口
    private String movable; //候選窗口是否可移動
    private int winX, winY; //候選窗座標
    private int candSpacing; //候選窗與邊緣間距
    private boolean cursorUpdated = false; //光標是否移動
    private int min_length;
    private boolean mTempAsciiMode; //臨時中英文狀態
    private boolean mAsciiMode; //默認中英文狀態
    private boolean reset_ascii_mode; //重置中英文狀態
    private String auto_caps; //句首自動大寫
    private Locale[] locales = new Locale[2];
    private boolean keyUpNeeded; //RIME是否需要處理keyUp事件
    private boolean mNeedUpdateRimeOption = true;
    private String lastCommittedText;

    private WindowsPositionType winPos; //候選窗口彈出位置
    private InlineModeType inlinePreedit; //嵌入模式

    private IntentReceiver mIntentReceiver;
    private TextFormatter mTextFormatter;
    private PopupWindow mCloudWindow;
    private CloudCandidate mCloudView;
    private Speech mSpeech;
    private boolean mCloudInput;
    private int mCloudY;
    private int mCurrSelEnd;
    private int mCurrSelStart;
    private boolean noStatusBar;
    private CloudInputRunnable mCloudInputRunnable;
    private TextView mHide;
    private boolean mSystemSpeak;
    private File mCloundPath;
    private ArrayList<String> mCloudCache;
    private boolean mUpdateCursorAnchor;
    private int mLastCurrSelEnd;
    private ArrayList<String> mCandidateLists;
    private int mLastOrientation;
    private String mLastKeyboardName;
    private Window mWindow;
    private int mDefaultType;
    private boolean mFloat;
    private boolean keyBoardFloat;
    private int mGravity;
    private LinearLayout mKeyboardLayout;
    private LinearLayout mRootLayout;
    private boolean mInputViewShown;
    private boolean mPhraseSort;
    private int mCompositionWidth;
    private boolean isTouchExplorationEnabled;
    private ScrollView mScrollView;
    private LinearLayout noScrollView;
    private boolean mAsyncKey;

    private boolean isWinFixed() {
        return VERSION.SDK_INT < VERSION_CODES.LOLLIPOP
                || (winPos != WindowsPositionType.LEFT
                && winPos != WindowsPositionType.RIGHT
                && winPos != WindowsPositionType.LEFT_UP
                && winPos != WindowsPositionType.RIGHT_UP);
    }

    public void updateWindow(int offsetX, int offsetY) {
        winPos = WindowsPositionType.DRAG;
        winX = offsetX;
        winY = offsetY;
        mFloatingWindow.update(winX, winY, -1, -1, true);
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = self.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = self.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int[] getLocationOnScreen(View v) {
        final int[] position = new int[2];
        v.getLocationOnScreen(position);
        return position;
    }

    public void resetCloudInput() {
        mCloudInput = mConfig.isCloudInput();
    }


    public void onHover() {

    }

    public void addPhrase() {
        CharSequence text = getCurrentInputConnection().getSelectedText(0);
        if (!TextUtils.isEmpty(text)) {
            addPhrase(text.toString());
            return;
        }
        startActivity(new Intent(this, DialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT).addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK));
        /*final EditText edit = new EditText(this);
        edit.setText(text);
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.ime_name)
                .setView(edit)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addPhrase(edit.getText().toString());
                    }
                })
                .setNeutralButton(android.R.string.cancel, null).create();
        Window window = dlg.getWindow();
        if(window!=null){
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
            dlg.show();
        }
        showDialog(dlg);*/
    }

    public void loadClipboard() {
        mClipboard = JsonUtil.load(new File(Config.get().getUserDataDir(), "clipboard.json"));
    }

    public void loadPhrase() {
        mPhrase = JsonUtil.load(new File(Config.get().getUserDataDir(), "phrase.json"));
    }

    public void setKeyboard(HashMap<String, Object> keys) {
        mKeyboardSwitch.setKeyboard("_custom_board");
        try {
            mKeyboardSwitch.getCurrentKeyboard().loadKey(keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTempAsciiMode = mKeyboardSwitch.getAsciiMode();
        bindKeyboardToInputView();
    }

    public void setKeyboard(String name) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mKeyboardSwitch.hasKeyboard(name + "_land"))
                name = name + "_land";
        } else {
            if (name.endsWith("_land"))
                name = name.replaceAll("_land$", "");
        }
        mKeyboardSwitch.setKeyboard(name);
        mTempAsciiMode = mKeyboardSwitch.getAsciiMode();
        bindKeyboardToInputView();
    }

    public void speech() {
        if (mSpeech != null)
            mSpeech.start();
    }


    public boolean isKeyBoardFloat() {
        return keyBoardFloat;
    }

    public void setSmallMode(boolean smallMode) {
        SharedPreferences.Editor edit = Function.getPref(this).edit();
        edit.putBoolean("keyboard_small", smallMode);
        edit.commit();
        updateKeyboard();
    }

    private void updateKeyboard() {
        if (mKeyboardSwitch != null) mKeyboardSwitch.reset();
        resetKeyboard();
        bindKeyboardToInputView();
    }

    public List<String> getSymbols() {
        ArrayList<String> symbols = new ArrayList<>();

        return symbols;
    }

    public boolean isTouchExplorationEnabled() {
        return isTouchExplorationEnabled;
    }

    private class PopupTimer extends Handler implements Runnable {
        private int mParentLocation[] = new int[2];

        void postShowFloatingWindow() {
            if (Function.isEmpty(Rime.getCompositionText())) {
                hideComposition();
                return;
            }
            /*if (Function.isEmpty(mComposition.getText())) {
                hideComposition();
                return;
            }*/
            mComposition.measure(0, 0);
            int w = Math.min(mCompositionWidth, mComposition.getMeasuredWidth());
            int h = Math.min(mComposition.getMeasuredHeight(), getHeight() / 2);
            mFloatingWindow.setWidth(w);
            mFloatingWindow.setHeight(h);
            //mComposition.setSize(w,h);
            post(this);
        }

        void postShowFloatingWindow2() {
            if (Function.isEmpty(mComposition.getText())) {
                hideComposition();
                return;
            }
            mCompositionContainer.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mFloatingWindow.setWidth(mCompositionContainer.getMeasuredWidth());
            mFloatingWindow.setHeight(mCompositionContainer.getMeasuredHeight());
            post(this);
        }

        void cancelShowing() {
            if (null != mFloatingWindow && mFloatingWindow.isShowing()) mFloatingWindow.dismiss();
            removeCallbacks(this);
        }

        @Override
        public void run() {
            if (mCandidateContainer == null || mCandidateContainer.getWindowToken() == null) return;
            if (!mShowWindow) return;
            int x = 0, y = 0;
            mParentLocation = getLocationOnScreen(mCandidateContainer);
            if (isWinFixed() || !cursorUpdated) {
                //setCandidatesViewShown(true);
                switch (winPos) {
                    case TOP_RIGHT:
                        x = mCandidateContainer.getWidth() - mFloatingWindow.getWidth();
                        y = candSpacing;
                        break;
                    case TOP_LEFT:
                        x = 0;
                        y = candSpacing;
                        break;
                    case BOTTOM_RIGHT:
                        x = mCandidateContainer.getWidth() - mFloatingWindow.getWidth();
                        y = mParentLocation[1] - mFloatingWindow.getHeight() - candSpacing;
                        break;
                    case DRAG:
                        x = winX;
                        y = winY;
                        break;
                    case FIXED:
                    case BOTTOM_LEFT:
                    default:
                        x = 0;
                        y = mParentLocation[1] - mFloatingWindow.getHeight() - candSpacing;
                        break;
                }
            } else {
                //setCandidatesViewShown(false);
                x = (int) mPopupRectF.left;
                if (winPos == WindowsPositionType.RIGHT
                        || winPos == WindowsPositionType.RIGHT_UP) {
                    x = (int) mPopupRectF.right;
                }
                y = (int) mPopupRectF.bottom + candSpacing;
                if (winPos == WindowsPositionType.LEFT_UP
                        || winPos == WindowsPositionType.RIGHT_UP) {
                    y =
                            (int) mPopupRectF.top
                                    - mFloatingWindow.getHeight()
                                    - candSpacing;
                }
            }
            if (x > mCandidateContainer.getWidth() - mFloatingWindow.getWidth()) {
                x = mCandidateContainer.getWidth() - mFloatingWindow.getWidth();
            }
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (y > mParentLocation[1] - mFloatingWindow.getHeight() - candSpacing) { //candSpacing爲負時，可覆蓋部分鍵盤
                y = mParentLocation[1] - mFloatingWindow.getHeight() - candSpacing;
            }
            if (!noStatusBar)
                y -= getStatusBarHeight(); //不包含狀態欄
            if (!mFloatingWindow.isShowing()) {
                mFloatingWindow.showAtLocation(mCandidateContainer, Gravity.START | Gravity.TOP, x, y);
                ViewGroup s = (ViewGroup) mCompositionContainer.getChildAt(0);
                s.scrollTo(0,0);
            } else {
                mFloatingWindow.update(x, y, mFloatingWindow.getWidth(), mFloatingWindow.getHeight());
            }
        }
    }

    public View getCandidateView() {
        return mCandidateContainer;
    }

    public ViewGroup getRoorView() {
        return mRootLayout;
    }

    public void loadConfig() {
        inlinePreedit = mConfig.getInlinePreedit();
        winPos = mConfig.getWinPos();
        movable = mConfig.getMovable();
        candSpacing = mConfig.getPixel("layout/spacing");
        min_length = mConfig.getMinLength();
        reset_ascii_mode = mConfig.getBoolean("reset_ascii_mode");
        auto_caps = mConfig.getString("auto_caps");
        mShowWindow = mConfig.getShowWindow();
        mNeedUpdateRimeOption = true;
        mCloudInput = mConfig.isCloudInput();
        mSystemSpeak = mConfig.isSystemSpeak();
        mPhraseSort = mConfig.isPhraseSort();
        mAsyncKey = mConfig.isAsyncKey();

        mCompositionWidth = (int) (getResources().getDisplayMetrics().widthPixels * mConfig.getCompositionWidth());
    }

    private boolean updateRimeOption() {
        if (mNeedUpdateRimeOption) {
            String soft_cursor_key = "soft_cursor";
            Rime.setOption(soft_cursor_key, mConfig.getSoftCursor()); //軟光標
            String horizontal_key = "horizontal";
            Rime.setOption("_" + horizontal_key, mConfig.getBoolean(horizontal_key)); //水平模式
            mNeedUpdateRimeOption = false;
        }
        return true;
    }

    public void resetEffect() {
        if (mEffect != null) mEffect.reset();
    }

    public void vibrateEffect() {
        if (mEffect != null) mEffect.vibrate();
    }

    public void soundEffect() {
        if (mEffect != null) mEffect.playSound(0);
    }

    @Override
    public void onCreate() {
        //setTheme(android.R.style.Theme_Material_Light);
        super.onCreate();
        mAsyncThread.start();
        // android.util.Log.i(TAG, "onCreate: ");
        self = this;
        mIntentReceiver = new IntentReceiver();
        mIntentReceiver.registerReceiver(this);
        mTextFormatter = new TextFormatter(this);
        mTextFormatter.loadLetter();
        String sc1 = Function.getPref(self).getString("select_schema_id", "");

        mEffect = new Effect(this);
        mConfig = Config.get(this);
        Rime.resetSchema();
        mNeedUpdateRimeOption = true;
        loadConfig();
        resetEffect();
        BackUtil.reset(this);
        mKeyboardSwitch = new KeyboardSwitch(this);

        String s;
        String[] ss;
        s = mConfig.getString("locale");
        if (Function.isEmpty(s)) s = "";
        ss = s.split("[-_]");
        if (ss.length == 2) locales[0] = new Locale(ss[0], ss[1]);
        else if (ss.length == 3) locales[0] = new Locale(ss[0], ss[1], ss[2]);
        else locales[0] = Locale.getDefault();
        s = mConfig.getString("latin_locale");
        if (Function.isEmpty(s)) s = "en_US";
        ss = s.split("[-_]");
        if (ss.length == 1) locales[1] = new Locale(ss[0]);
        else if (ss.length == 2) locales[1] = new Locale(ss[0], ss[1]);
        else if (ss.length == 3) locales[1] = new Locale(ss[0], ss[1], ss[2]);
        else locales[0] = Locale.ENGLISH;

        orientation = getResources().getConfiguration().orientation;
        // Use the following line to debug IME service.
        //android.os.Debug.waitForDebugger();
        String sc = Function.getPref(self).getString("select_schema_id", "");
        if (!TextUtils.isEmpty(sc) && !sc.equals(Rime.getSchemaId()))
            Rime.selectSchemaId(sc);

        registerClipEvents();
        initCloud();
    }

    private void initRime() {

    }


    private void initCloud() {
        mCloundPath = new File(mConfig.getUserDataDir(), "cloud.dict.yaml");
        if (!mCloundPath.exists()) {
            try {
                FileUtil.assetsToSD(this, "cloud.dict.yaml", mCloundPath.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mCloudCache = new ArrayList<>(Arrays.asList(new String(FileUtil.readAll(new FileInputStream(mCloundPath))).split("\n")));
        } catch (IOException e) {
            e.printStackTrace();
            mCloudCache = new ArrayList<>();
        }
    }

    public void addCloud(String text) {
        if (mCloudCache.contains(text))
            return;
        mCloudCache.add(text);
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(mCloundPath, true));
            buf.write(text);
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> mClipboard = new ArrayList<>();
    private ArrayList<String> mPhrase = new ArrayList<>();
    private ClipboardManager manager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;

    private void registerClipEvents() {
        loadClipboard();
        loadPhrase();
        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (manager == null)
            return;
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                    CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
                    if (!TextUtils.isEmpty(addedText)) {
                        String text = addedText.toString();
                        addClipboard(text);
                    }
                }
            }
        };
        manager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    private void unregisterClipEvents() {
        if (manager == null)
            return;
        manager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    private void addClipboard(String text) {
        if (mClipboard.contains(text))
            mClipboard.remove(text);
        mClipboard.add(0, text);
        for (int size = mClipboard.size() - 1; size >= 120; size--) {
            mClipboard.remove(size);
        }
        JsonUtil.save(new File(mConfig.getUserDataDir(), "clipboard.json"), mClipboard);
    }

    public List<String> getClipBoard() {
        return mClipboard;
    }

    public void addPhrase(String text) {
        if (mPhrase.contains(text))
            mPhrase.remove(text);
        mPhrase.add(0, text);
        for (int size = mPhrase.size() - 1; size >= 120; size--) {
            mPhrase.remove(size);
        }
        JsonUtil.save(new File(mConfig.getUserDataDir(), "phrase.json"), mPhrase);
    }

    public List<String> getPhrase() {
        return mPhrase;
    }

    private String TAG = "rime";

    public void onOptionChanged(String option, boolean value) {
        // android.util.Log.i(TAG, "onOptionChanged: " + option + ";" + value);
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("option", option);
        data.putBoolean("value", value);
        msg.setData(data);
        msg.what = 10;
        mHandler.sendMessage(msg);
    }

    public void onOptionChanged2(String option, boolean value) {
        // android.util.Log.i(TAG, "onOptionChanged: " + option + ";" + value);
        switch (option) {
            case "small_mode":
                setSmallMode(value);
                break;
            case "float_mode":
                setFloat();
                break;
            case "ascii_mode":
                if (!mTempAsciiMode)
                    mAsciiMode = value; //切換中西文時保存狀態
                mEffect.setLanguage(locales[value ? 1 : 0]);
                if (value) {
                    if (mKeyboardSwitch.getCurrentKeyboardId() == 0)
                        mKeyboardSwitch.setKeyboard(".ascii");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Rime.setOption("full_shape", false);
                            updateCandidate();
                        }
                    }, 32);
                } else {
                    if (mKeyboardSwitch.getCurrentKeyboardId() == mKeyboardSwitch.getCurrentAsciiKeyboardId())
                        mKeyboardSwitch.setKeyboard(".default");
                }
                bindKeyboardToInputView();
                break;
            case "_hide_comment":
                setShowComment(!value);
                break;
            case "_hide_candidate":
                if (mCandidateContainer != null)
                    mCandidate.setVisibility(!value ? View.VISIBLE : View.GONE);
                setCandidatesViewShown(canCompose && !value);
                break;
            case "_hide_key_hint":
                if (mKeyboardView != null) mKeyboardView.setShowHint(!value);
                break;
            default:
                if (option.startsWith("_keyboard_") && option.length() > 10 && value && mKeyboardSwitch != null) {
                    String keyboard = option.substring(10);
                    mKeyboardSwitch.setKeyboard(keyboard);
                    mTempAsciiMode = mKeyboardSwitch.getAsciiMode();
                    bindKeyboardToInputView();
                } else if (option.startsWith("_key_") && option.length() > 5 && value) {
                    boolean bNeedUpdate = mNeedUpdateRimeOption;
                    if (bNeedUpdate) mNeedUpdateRimeOption = false; //防止在onMessage中setOption
                    String key = option.substring(5);
                    // android.util.Log.i(TAG, "onOptionChanged: " + key + ";" + new Event(key).getLabel());
                    onEvent(new Event(key));
                    if (bNeedUpdate) mNeedUpdateRimeOption = true;
                }
        }
        if (mKeyboardView != null) mKeyboardView.invalidateAllKeys();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    commitText();
                    keyUpNeeded = true;
                    break;
                case 1:
                    commitText();
                    int keyCode = msg.arg1;
                    int mask = msg.arg2;
                    if (handleAciton(keyCode, mask)
                            || handleOption(keyCode)
                            || handleEnter(keyCode)
                            || handleBack(keyCode)) {
                        // android.util.Log.i(TAG, "Trime onAsyncKey");
                    } else if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                            && Function.openCategory(Trime.this, keyCode)) {
                        // android.util.Log.i(TAG, "onAsyncOpen category");
                    } else {
                        keyUpNeeded = true;
                        if (keyCode >= Key.getSymbolStart()) { //符號
                            keyUpNeeded = false;
                            commitText(Event.getDisplayLabel(keyCode));
                            return;
                        }
                        keyUpNeeded = false;
                        sendDownUpKeyEvents(keyCode, mask);
                    }
                    break;
                case 10:
                    Bundle data = msg.getData();
                    if (data == null)
                        return;
                    String option = data.getString("option");
                    boolean value = data.getBoolean("value");
                    onOptionChanged2(option, value);
                    break;
                case 100:
                    break;
                case 101:
                    commitText();
                    break;
            }

        }
    };

    public void invalidate() {
        Rime.get();
        if (mConfig != null) mConfig.destroy();
        mConfig = new Config(this);
        reset();
        mNeedUpdateRimeOption = true;
    }

    private void hideComposition() {
        if (movable.contentEquals("once")) winPos = mConfig.getWinPos();
        mFloatingWindowTimer.cancelShowing();
    }

    private void loadBackground() {
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(mConfig.getPixel("layout/border"), mConfig.getColor("border_color"));
        gd.setCornerRadius(mConfig.getFloat("layout/round_corner"));
        Drawable d = mConfig.getColorDrawable("composition_back_color");
        if (d == null) {
            d = mConfig.getDrawable("layout/background");
        }
        if (d == null) {
            gd.setColor(mConfig.getColor("text_back_color"));
            d = gd;
        }
        if (mConfig.hasKey("layout/alpha")) {
            int alpha = mConfig.getInt("layout/alpha");
            if (alpha <= 0) alpha = 0;
            else if (alpha >= 255) alpha = 255;
            d.setAlpha(alpha);
        } else if (mConfig.isKeyboardFloat())
            d.setAlpha(mConfig.getKeyAlpha());
        mFloatingWindow.setBackgroundDrawable(d);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
            mFloatingWindow.setElevation(mConfig.getPixel("layout/elevation"));
        Drawable bkg = mConfig.getColorDrawable("candidate_back_color");
        if (bkg == null)
            bkg = mConfig.getColorDrawable("back_color");
        if (bkg != null) {
            bkg.setAlpha(mConfig.getKeyAlpha());
            if (bkg instanceof GradientDrawable) {
                Integer b = mConfig.getColor("candidate_border_color");
                if (b == null)
                    b = mConfig.getColor("border_color");
                ((GradientDrawable) bkg).setStroke(mConfig.getPixel("candidate_border"), b);
                ((GradientDrawable) bkg).setCornerRadius(mConfig.getFloat("candidate_round_corner"));
            }
        }

        mCandidateContainer.setBackground(bkg);

        Drawable bk = mConfig.getColorDrawable("background_color");
        if (bk == null)
            bk = mConfig.getColorDrawable("back_color");
        if (mConfig.isKeyboardFloat() && bk != null)
            bk.setAlpha(mConfig.getKeyAlpha());
        mRootLayout.setBackground(bk);
    }

    private void loadBackground(PopupWindow mFloatingWindow) {
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(mConfig.getPixel("layout/border"), mConfig.getColor("border_color"));
        gd.setCornerRadius(mConfig.getFloat("layout/round_corner"));
        Drawable d = mConfig.getColorDrawable("candidate_back_color");
        if (d == null)
            d = mConfig.getColorDrawable("back_color");
        if (mConfig.isKeyboardFloat())
            d.setAlpha(mConfig.getKeyAlpha());
        if (d == null) {
            gd.setColor(mConfig.getColor("text_back_color"));
            d = gd;
        }
        if (d instanceof GradientDrawable) {
            GradientDrawable g = (GradientDrawable) d;
            g.setStroke(mConfig.getPixel("layout/border"), mConfig.getColor("border_color"));
            g.setCornerRadius(Config.getPixel(8f));
        }
        if (mConfig.hasKey("layout/alpha")) {
            int alpha = mConfig.getInt("layout/alpha");
            if (alpha <= 0) alpha = 0;
            else if (alpha >= 255) alpha = 255;
            d.setAlpha(alpha);
        }
        mFloatingWindow.setBackgroundDrawable(d);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
            mFloatingWindow.setElevation(Config.getPixel(4f));
        mCloudY = mConfig.getPixel("candidate_view_height") * 4;
    }

    public void resetKeyboard() {
        if (mKeyboardView != null) {
            //mKeyboardView.setShowHint(!Rime.getOption("_hide_key_hint"));
            LayoutParams lp = mKeyboardLayout.getLayoutParams();
            lp.width = getWidth();
            mKeyboardLayout.setLayoutParams(lp);
            lp = mCandidateContainer.getLayoutParams();
            lp.width = getWidth();
            mCandidateContainer.setLayoutParams(lp);
            mKeyboardView.reset(); //實體鍵盤無軟鍵盤
        }
    }

    public void resetCandidate() {
        if (mCandidateContainer != null) {
            loadBackground();
            setShowComment(!Rime.getOption("_hide_comment"));
            mCandidate.setVisibility(!Rime.getOption("_hide_candidate") ? View.VISIBLE : View.GONE);
            mCandidate.reset();
            mShowWindow = mConfig.getShowWindow();
            mComposition.setVisibility(mShowWindow ? View.VISIBLE : View.GONE);
            mComposition.reset();
            setCompositionSingleLine(mConfig.isCompositionSingleLine());
            loadBackground(mCloudWindow);
            mCloudView.reset();
        }
    }

    /**
     * 重置鍵盤、候選條、狀態欄等 !!注意，如果其中調用Rime.setOption，切換方案會卡住
     */
    private void reset() {
        allGc();
        mConfig.reset();
        loadConfig();
        BackUtil.reset(this);
        if (mKeyboardSwitch != null)
            mKeyboardSwitch.reset();
        resetCandidate();
        hideComposition();
        resetKeyboard();
        resetEffect();
        resetBackground();
    }

    private void resetBackground() {
        if (mKeyboardView != null) {
            Drawable b = BackUtil.get(this, "keyboard");
            if (b != null) {
                if (mConfig.isKeyboardFloat())
                    b.setAlpha(mConfig.getKeyAlpha());
                mKeyboardView.setBackgroundDrawable(b);
            }
        }
        if (mKeyboardLayout != null) {
            Drawable b = BackUtil.get(this, "background");
            if (b != null) {
                if (mConfig.isKeyboardFloat())
                    b.setAlpha(mConfig.getKeyAlpha());
                mRootLayout.setBackgroundDrawable(b);
            }
        }
        if (mCompositionContainer != null) {
            Drawable b = BackUtil.get(this, "composition");
            if (b != null) {
                if (mConfig.hasKey("layout/alpha")) {
                    int alpha = mConfig.getInt("layout/alpha");
                    if (alpha <= 0) alpha = 0;
                    else if (alpha >= 255) alpha = 255;
                    b.setAlpha(alpha);
                } else if (mConfig.isKeyboardFloat())
                    b.setAlpha(mConfig.getKeyAlpha());
                mCompositionContainer.setBackgroundDrawable(b);
            } else {
                mCompositionContainer.setBackground(null);
            }
        }
        if (mCandidateContainer != null) {
            Drawable b = BackUtil.get(this, "candidate");
            if (b != null) {
                if (mConfig.isKeyboardFloat())
                    b.setAlpha(mConfig.getKeyAlpha());
                mCandidateContainer.setBackgroundDrawable(b);
            }
        }
    }

    public void initKeyboard() {
        reset();
        mNeedUpdateRimeOption = true; //不能在Rime.onMessage中調用set_option，會卡死
        bindKeyboardToInputView();
        updateComposing(); //切換主題時刷新候選
    }


    @Override
    public void onDestroy() {
        onWindowHidden();
        super.onDestroy();
        mIntentReceiver.unregisterReceiver(this);
        self = null;
        if (mEffect != null)
            mEffect.destory();
        if (mConfig.isDestroyOnQuit()) {
            Rime.destroy();
            mConfig.destroy();
            mConfig = null;
            System.exit(0); //清理內存
        }
        unregisterClipEvents();
    }

    public static Trime getService() {
        return self;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (orientation != newConfig.orientation) {
            // Clear composing text and candidates for orientation change.
            escape();
            orientation = newConfig.orientation;
            mKeyboardSwitch.reset();
            resetKeyboard();
            resetCandidate();
        }
        super.onConfigurationChanged(newConfig);
    }

    public boolean isLandscape() {
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onUpdateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !isTouchExplorationEnabled()) {
            int i = cursorAnchorInfo.getComposingTextStart();
            if ((winPos == WindowsPositionType.LEFT || winPos == WindowsPositionType.LEFT_UP) && i >= 0) {
                mPopupRectF = cursorAnchorInfo.getCharacterBounds(i);
            } else {
                mPopupRectF.left = cursorAnchorInfo.getInsertionMarkerHorizontal();
                mPopupRectF.top = cursorAnchorInfo.getInsertionMarkerTop();
                mPopupRectF.right = mPopupRectF.left;
                mPopupRectF.bottom = cursorAnchorInfo.getInsertionMarkerBottom();
            }
            cursorAnchorInfo.getMatrix().mapRect(mPopupRectF);
            if (mCandidateContainer != null) {
                mFloatingWindowTimer.postShowFloatingWindow();
            }
        }
        mUpdateCursorAnchor = true;
    }

    @Override
    public void onUpdateSelection(
            int oldSelStart,
            int oldSelEnd,
            int newSelStart,
            int newSelEnd,
            int candidatesStart,
            int candidatesEnd) {
        super.onUpdateSelection(
                oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        if ((candidatesEnd != -1) && ((newSelStart != candidatesEnd) || (newSelEnd != candidatesEnd))) {
            //移動光標時，更新候選區
            if ((newSelEnd < candidatesEnd) && (newSelEnd >= candidatesStart)) {
                int n = newSelEnd - candidatesStart;
                Rime.RimeSetCaretPos(n);
                updateComposing();
            }
        }
        if ((candidatesStart == -1 && candidatesEnd == -1) && (newSelStart == 0 && newSelEnd == 0)) {
            //上屏後，清除候選區
            escape();
        }
        // Update the caps-lock status for the current cursor position.
        updateCursorCapsToInputView();
        mCurrSelEnd = newSelEnd;
        mCurrSelStart = newSelStart;
    }

    @Override
    public void onComputeInsets(InputMethodService.Insets outInsets) {
        super.onComputeInsets(outInsets);
        outInsets.contentTopInsets = outInsets.visibleTopInsets;
    }

    @Override
    public View onCreateInputView() {
        // android.util.Log.i(TAG, "onCreateInputView: ");
        mRootLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.input, (ViewGroup) null);
        mGravity = Gravity.LEFT;
        mRootLayout.getChildAt(0).setVisibility(View.GONE);

        final LinearLayout left = (LinearLayout) mRootLayout.getChildAt(0);
        final LinearLayout right = (LinearLayout) mRootLayout.getChildAt(2);
        left.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left.setVisibility(View.GONE);
                right.setVisibility(View.VISIBLE);
            }
        });
        right.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.GONE);
            }
        });

        right.getChildAt(0).setBackground(new RoundRectDrawable(0x11888888, 1000));
        left.getChildAt(0).setBackground(new RoundRectDrawable(0x11888888, 1000));
        mRootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //setSmallMode(false);
                onOptionChanged("small_mode", false);
                return true;
            }
        });
        mKeyboardLayout = (LinearLayout) mRootLayout.getChildAt(1);
        //mKeyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.input, (ViewGroup) null);
        mScrollView = (ScrollView) mRootLayout.findViewById(R.id.scroll);//mKeyboardLayout.getChildAt(1);
        noScrollView = (LinearLayout) mRootLayout.findViewById(R.id.noscroll);//mKeyboardLayout.getChildAt(2);
        mKeyboardView = (KeyboardView) mRootLayout.findViewById(R.id.keyboard);//noScrollView.getChildAt(0);
        createCandidatesView((LinearLayout) mRootLayout.findViewById(R.id.candidate_container));
        //lp=mKeyboardLayout.getLayoutParams();
        mKeyboardView.setOnKeyboardActionListener(this);
        //mKeyboardView.setShowHint(!Rime.getOption("_hide_key_hint"));
        resetBackground();
        setScroll(false);
        return mRootLayout;
    }

    private boolean mScroll = false;
    private boolean mCurrView = false;

    public int getLastKeyboardHeight() {
        return Math.max(mScrollView.getHeight(), noScrollView.getHeight());
    }

    public int getDefaultKeyboardHeight() {
        return mKeyboardSwitch.getKeyboard(0).getKeyBoardHeight2();
    }

    public void setKeyboard(View view) {
        if (view == null && !mCurrView)
            return;
        int h = Math.max(mScrollView.getHeight(), noScrollView.getHeight());
        mScrollView.removeAllViews();
        noScrollView.removeAllViews();
        if (view != null) {
            LayoutParams lp = view.getLayoutParams();
            if (lp.height == LayoutParams.MATCH_PARENT) {
                lp.height = h;
                view.setLayoutParams(lp);
            }
            noScrollView.addView(view);
            mScrollView.setVisibility(View.GONE);
            noScrollView.setVisibility(View.VISIBLE);
            mCurrView = true;
        } else {
            if (mScroll) {
                noScrollView.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
                mScrollView.addView(mKeyboardView);
            } else {
                mScrollView.setVisibility(View.GONE);
                noScrollView.setVisibility(View.VISIBLE);
                noScrollView.addView(mKeyboardView);
            }
            mCurrView = false;
        }
        android.util.Log.i(TAG, "setKeyboard: mCurrView " + view);
    }

    public void setScroll(boolean b) {
        android.util.Log.i(TAG, "setKeyboard: setScroll " + b);
        if (mScroll == b)
            return;
        if (mCurrView)
            return;
        mScroll = b;
        mScrollView.removeAllViews();
        noScrollView.removeAllViews();
        if (b) {
            noScrollView.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            mScrollView.addView(mKeyboardView);
        } else {
            mScrollView.setVisibility(View.GONE);
            noScrollView.setVisibility(View.VISIBLE);
            noScrollView.addView(mKeyboardView);
        }
    }

    @Override
    public int getWidth() {
        if (mConfig.isKeyboardSmall())
            return (int) (getResources().getDisplayMetrics().widthPixels * mConfig.getKeyboardWidth());
        else
            return getResources().getDisplayMetrics().widthPixels;
    }

    public void setFloat() {
        SharedPreferences.Editor edit = Function.getPref(this).edit();
        edit.putBoolean("keyboard_float", !mConfig.isKeyboardFloat());
        edit.commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
                //onDestroy();
                /*getWindow().dismiss();
                onCreate();
                onCreateInputView();
                initKeyboard();
                //invalidate();
                //onCreateCandidatesView();

               /* WindowManager.LayoutParams attr = mWindow.getAttributes();
                if(flt)
                    mWindow.setType(getDialogType());
                else
                    mWindow.setType(mDefaultType);
                //attr.alpha=0.7f;
                attr.gravity=Gravity.CENTER;
                mWindow.setAttributes(attr);*/

            }
        }, 100);
    }

    void setShowComment(boolean show_comment) {
        if (mCandidateContainer != null) mCandidate.setShowComment(show_comment);
        mComposition.setShowComment(show_comment);
    }

    //@Override
    public View onCreateCandidatesView2() {

        LayoutInflater inflater = getLayoutInflater();
        mCompositionContainer =
                (LinearLayout) inflater.inflate(R.layout.composition_container, (ViewGroup) null);
        hideComposition();

        mCloudView = new CloudCandidate(this);
        mCloudView.setCandidateListener(new CloudCandidate.CandidateListener() {
            @Override
            public void onPickCandidate(String text) {
                mCloudWindow.dismiss();
                commitText(text);
                Rime.clearComposition();
                updateComposing();
                addCloud(text);
            }

            @Override
            public void onSelectCandidate(String text) {
                try {
                    String text1 = null;
                    StringBuilder mList = new StringBuilder(100);
                    mList.append(text).append("，");
                    for (int i = 0; i < text.length(); i++)
                        mList.append(mTextFormatter.format(String.valueOf(text.charAt(i)))).append("，");
                    text1 = mList.toString();
                    if (isTouchExplorationEnabled())
                        speakPro(text1);
                    else
                        mEffect.speakCommit(text1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        FrameLayout layout = new FrameLayout(this);
        layout.addView(mCloudView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layout.setPadding(Config.getPixel(4f), Config.getPixel(2f), Config.getPixel(4f), Config.getPixel(2f));
        mCloudWindow = new PopupWindow(this);
        mCloudWindow.setClippingEnabled(false);
        mCloudWindow.setContentView(layout);
        mCloudWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            mCloudWindow.setWindowLayoutType(getDialogType());
        }
        mCloudWindow.setWidth(LayoutParams.MATCH_PARENT);
        mCloudWindow.setHeight(LayoutParams.WRAP_CONTENT);
        loadBackground(mCloudWindow);

        mFloatingWindow = new PopupWindow(this);
        mFloatingWindow.setClippingEnabled(false);
        try {
            Method method = PopupWindow.class.getMethod("setLayoutInScreenEnabled", boolean.class);
            method.invoke(mFloatingWindow, true);
            noStatusBar = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            mFloatingWindow.setWindowLayoutType(getDialogType());
        }
        mFloatingWindow.setContentView(mCompositionContainer);
        mFloatingWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            //mFloatingWindow.setWindowLayoutType(getDialogType());
        }

        mComposition = (Composition) mCompositionContainer.getChildAt(0);

        mCandidateContainer =
                (LinearLayout) inflater.inflate(R.layout.candidate_container, (ViewGroup) null);
        mCandidate = (Candidate) mCandidateContainer.findViewById(R.id.candidate);

        mCandidate.setCandidateListener(this);
        setShowComment(!Rime.getOption("_hide_comment"));
        mCandidate.setVisibility(!Rime.getOption("_hide_candidate") ? View.VISIBLE : View.GONE);
        loadBackground();
        return mCandidateContainer;
    }

    private void createCompositionView() {
        LayoutInflater inflater = getLayoutInflater();
        mCompositionContainer =
                (LinearLayout) inflater.inflate(R.layout.composition_container, (ViewGroup) null);
        hideComposition();

        mFloatingWindow = new PopupWindow(this);
        mFloatingWindow.setClippingEnabled(false);
        try {
            Method method = PopupWindow.class.getMethod("setLayoutInScreenEnabled", boolean.class);
            method.invoke(mFloatingWindow, true);
            noStatusBar = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            mFloatingWindow.setWindowLayoutType(getDialogType());
        }
        mFloatingWindow.setContentView(mCompositionContainer);
        mFloatingWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            //mFloatingWindow.setWindowLayoutType(getDialogType());
        }
        mComposition = (Composition) (((ScrollView) (mCompositionContainer.getChildAt(0))).getChildAt(0));
        setCompositionSingleLine(mConfig.isCompositionSingleLine());
        //mComposition = (Composition) (mCompositionContainer.getChildAt(0));
    }

    public void setCompositionSingleLine(boolean single) {
        if (mConfig.isCompositionEndTop()) {
            single = false;
            mComposition.setCompositionEndTop(true);
        } else {
            mComposition.setCompositionEndTop(false);
        }

        mComposition.setCompositionSingleLine(single);
        if (single) {
            ViewGroup s = (ViewGroup) mCompositionContainer.getChildAt(0);
            s.removeAllViews();
            mCompositionContainer.removeAllViews();
            s = new HorizontalScrollView(this);
            s.setScrollBarSize(Config.getPixel(2f));
            s.addView(mComposition);
            mCompositionContainer.addView(s);
        } else {
            ViewGroup s = (ViewGroup) mCompositionContainer.getChildAt(0);
            s.removeAllViews();
            mCompositionContainer.removeAllViews();
            s = new ScrollView(this);
            s.setScrollBarSize(Config.getPixel(2f));
            s.addView(mComposition);
            mCompositionContainer.addView(s);
        }
    }

    private void createCloudView() {
        mCloudView = new CloudCandidate(this);
        mCloudView.setCandidateListener(new CloudCandidate.CandidateListener() {
            @Override
            public void onPickCandidate(String text) {
                mCloudWindow.dismiss();
                commitText(text);
                Rime.clearComposition();
                updateComposing();
                addCloud(text);
            }

            @Override
            public void onSelectCandidate(String text) {
                try {
                    String text1 = null;
                    StringBuilder mList = new StringBuilder(100);
                    mList.append(text).append("，");
                    for (int i = 0; i < text.length(); i++)
                        mList.append(mTextFormatter.format(String.valueOf(text.charAt(i)))).append("，");
                    text1 = mList.toString();
                    if (isTouchExplorationEnabled())
                        speakPro(text1);
                    else
                        mEffect.speakCommit(text1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        FrameLayout layout = new FrameLayout(this);
        layout.addView(mCloudView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layout.setPadding(Config.getPixel(4f), Config.getPixel(2f), Config.getPixel(4f), Config.getPixel(2f));
        mCloudWindow = new PopupWindow(this);
        mCloudWindow.setClippingEnabled(false);
        mCloudWindow.setContentView(layout);
        mCloudWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            mCloudWindow.setWindowLayoutType(getDialogType());
        }
        mCloudWindow.setWidth(LayoutParams.MATCH_PARENT);
        mCloudWindow.setHeight(LayoutParams.WRAP_CONTENT);
        loadBackground(mCloudWindow);

    }

    private void createCandidatesView(LinearLayout candidateLayout) {
        createCompositionView();
        createCloudView();

        mCandidateContainer = candidateLayout;
        mCandidate = (Candidate) candidateLayout.getChildAt(0);
        mCandidate.setCandidateListener(this);
        setShowComment(!Rime.getOption("_hide_comment"));
        mCandidate.setVisibility(!Rime.getOption("_hide_candidate") ? View.VISIBLE : View.GONE);
        /*mHide = (TextView) mCandidateContainer.findViewById(R.id.hide);
        mHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKey(KeyEvent.KEYCODE_BACK, 0);
            }
        });*/
        loadBackground();
    }

    /**
     * 重置鍵盤、候選條、狀態欄等，進入文本框時通常會調用。
     *
     * @param attribute  文本框的{@link EditorInfo 屬性}
     * @param restarting 是否重啓
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        canCompose = false;
        enterAsLineBreak = false;
        mTempAsciiMode = false;
        int inputType = attribute.inputType;
        int inputClass = inputType & InputType.TYPE_MASK_CLASS;
        int variation = inputType & InputType.TYPE_MASK_VARIATION;
        String keyboard = null;
        switch (inputClass) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_PHONE:
            case InputType.TYPE_CLASS_DATETIME:
                mTempAsciiMode = true;
                keyboard = "number";
                break;
            case InputType.TYPE_CLASS_TEXT:
                if (variation == InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE) {
                    // Make enter-key as line-breaks for messaging.
                    enterAsLineBreak = true;
                }
                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_PASSWORD
                        || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        || variation == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) {
                    mTempAsciiMode = true;
                    keyboard = ".ascii";
                } else {
                    canCompose = true;
                }
                break;
            default: //0
                canCompose = (inputType > 0); //0x80000 FX重命名文本框
                if (canCompose) break;
                return;
        }
        //Rime.get();
        if (reset_ascii_mode) mAsciiMode = false;
        // Select a keyboard based on the input type of the editing field.
        mKeyboardSwitch.init(getMaxWidth()); //橫豎屏切換時重置鍵盤
        mKeyboardSwitch.setKeyboard(keyboard);
        updateAsciiMode();
        canCompose = canCompose && !Rime.isEmpty();
        if (!onEvaluateInputViewShown()) setCandidatesViewShown(canCompose); //實體鍵盤進入文本框時顯示候選欄
        if (mConfig.isShowStatusIcon()) showStatusIcon(R.drawable.status); //狀態欄圖標
    }

    @Override
    public void showWindow(boolean showInput) {
        super.showWindow(showInput);
        updateCandidate();
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        if (mConfig.isKeyboardFloat()) {
            keyBoardFloat = true;
            mWindow = getWindow().getWindow();
            WindowManager.LayoutParams attr = mWindow.getAttributes();
            mDefaultType = attr.type;
            mWindow.setType(getDialogType());
            //attr.alpha=0.7f;
            attr.gravity = Gravity.CENTER;
            attr.width = getWidth();
            mWindow.setAttributes(attr);
        }

        super.onStartInputView(attribute, restarting);
        bindKeyboardToInputView();
        setCandidatesViewShown(!Rime.isEmpty()); //軟鍵盤出現時顯示候選欄
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        mInputViewShown = isInputViewShown();
        mLastKeyboardName = null;
        setKeyboard(".default");
        if (getSpeed() > 0)
            speakPro("键盘已隐藏，每分钟" + getSpeed() + "字");
        else
            speakPro("键盘已隐藏");

        if (mCloudWindow != null && mCloudWindow.isShowing())
            mCloudWindow.dismiss();
        if (mSpeech != null)
            mSpeech.destroy();
        mSpeech = null;
    }


    @Override
    public void onWindowShown() {
        super.onWindowShown();
        if (mInputViewShown)
            return;
        AccessibilityManager mAccessibilityManager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (mAccessibilityManager != null)
            isTouchExplorationEnabled = mAccessibilityManager.isTouchExplorationEnabled();

        mInputViewShown = isInputViewShown();

        File path = new File(mConfig.getSharedDataDir(), Rime.getSchemaId() + ".lua");
        if (path.exists())
            Trime.getService().doMainFile(path.getAbsolutePath());
        //Function.printStackTrace("onWindowShown");
        if (mKeyboardView != null)
            mKeyboardView.onWindowShown();
        mSpeech = new Speech(this);
    }

    private int errors = 0;

    public int getSelection() {
        if (isComposing()) {
            return -1;
        } else {
            return getBeforeText().length();
        }
    }

    public void setSelection(boolean bool) {
        if (isComposing()) {
            return;
        } else {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                if (bool) {
                    ic.setSelection(0, 0);
                    speakPro("开头");
                } else {
                    String text = getAfterText();
                    ic.setSelection(text.length(), text.length());
                    speakPro("结尾");
                }
            }
        }
    }

    public void next() {
        if (isComposing()) {
            onKey(KeyEvent.KEYCODE_DPAD_RIGHT, 0);
        } else {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                boolean noNext = TextUtils.isEmpty(getAfterText());
                if (noNext && mLastCurrSelEnd == -1) {
                    speakPro("文字尾，" + getActiveText(4));
                    return;
                } else if (noNext) {
                    mLastCurrSelEnd = -1;
                } else {
                    mLastCurrSelEnd = 0;
                }
                if (!ic.setSelection(mCurrSelStart + 1, mCurrSelEnd + 1)) {
                    speakPro(getActiveText(4));
                }
            }
        }
    }

    public void previous() {
        if (isComposing() && !Rime.hasLeft() && Rime.getCandHighlightIndex() == 0) {
            return;
        }
        if (isComposing()) {
            onKey(KeyEvent.KEYCODE_DPAD_LEFT, 0);
        } else {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                if (!ic.setSelection(mCurrSelStart - 1, mCurrSelEnd - 1)) {
                    speakPro(getActiveText(3));
                }
                if (mCurrSelStart - 1 < 0) {
                    speakPro("文字头，" + getActiveText(3));
                }
            }
        }
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        if (isComposing())
            commitTextAndClearComposition("");
        super.onFinishInputView(finishingInput);
        // Dismiss any pop-ups when the input-view is being finished and hidden.
        mKeyboardView.closing();
        escape();
        try {
            hideComposition();
        } catch (Exception e) {
            Log.info("Fail to show the PopupWindow.");
        }
    }

    public void speakPro(CharSequence text) {
        if (isTouchExplorationEnabled())
            mEffect.speak(text);
    }

    public void speak(String text) {
        if (mEffect != null)
            mEffect.speak(text);
    }

    private void bindKeyboardToInputView() {
        if (mKeyboardView != null) {
            // Bind the selected keyboard to the input view.
            Keyboard sk = (Keyboard) mKeyboardSwitch.getCurrentKeyboard();
            if ("scroll".equals(sk.getType())) {
                setScroll(true);
                LayoutParams lp = mScrollView.getLayoutParams();
                int sh = sk.getKeyBoardHeight2();
                if (sh == 0)
                    sh = Math.max(mScrollView.getHeight(), noScrollView.getHeight());
                lp.height = sh;
                mScrollView.setLayoutParams(lp);
            } else {
                setScroll(false);
            }
            mKeyboardView.setKeyboard(sk);
            updateCursorCapsToInputView();
            String name = sk.getName();
            if (!name.equals(mLastKeyboardName)) {
                if (mKeyboardSwitch.getCurrentKeyboardId() == 0) {
                    if (Rime.isAsciiMode())
                        speakPro("英文" + name);
                    else
                        speakPro("中文" + name);
                } else {
                    speakPro(name);
                }
            }
            mLastKeyboardName = name;
        }
    }

    //句首自動大小寫
    private void updateCursorCapsToInputView() {
        if (auto_caps.contentEquals("false") || Function.isEmpty(auto_caps)) return;
        if ((auto_caps.contentEquals("true") || Rime.isAsciiMode())
                && (mKeyboardView != null && !mKeyboardView.isCapsOn())) {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                int caps = 0;
                EditorInfo ei = getCurrentInputEditorInfo();
                if ((ei != null) && (ei.inputType != EditorInfo.TYPE_NULL)) {
                    caps = ic.getCursorCapsMode(ei.inputType);
                }
                mKeyboardView.setShifted(false, caps != 0);
            }
        }
    }

    private boolean isComposing() {
        return Rime.isComposing();
    }

    private long lastCommittedTime = 0;
    private long committedTime = 0;
    private long committedText = 0;
    private long committedTextCount = 0;
    private long committedCount = 0;

    /**
     * 指定字符串上屏
     *
     * @param text 要上屏的字符串
     */
    public void commitText(CharSequence text) {
        mLastCurrSelEnd = 0;
        if (text == null) return;
        runFunc("commitText", text);
        if (isTouchExplorationEnabled())
            speakPro(text);
        else
            mEffect.speakCommit(text);
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.commitText(text, 1);
            lastCommittedText = text.toString();
        }
        if (!isComposing()) Rime.commitComposition(); //自動上屏
        ic.clearMetaKeyStates(KeyEvent.getModifierMetaStateMask()); //黑莓刪除鍵清空文本框問題
    }

    public int getSpeed() {
        if (committedCount < 4 || committedText < 4 || committedTextCount < 32)
            return 0;
        try {
            return (int) (60 * 1000 / (Long.valueOf(committedTime).doubleValue() / Long.valueOf(committedText).doubleValue()));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 從Rime獲得字符串並上屏
     *
     * @return 是否成功上屏
     */
    private boolean commitText() {
        boolean r = Rime.getCommit();
        if (r) {
            String text = Rime.getCommitText();
            commitText(text);
            long time = System.currentTimeMillis();
            if (time - lastCommittedTime > 30 * 1000 && committedText > 0) {
                lastCommittedTime = time - committedTime / committedText;
                committedTime = committedTime / committedText;
                committedText = 1;
            }
            if (time - lastCommittedTime < 5 * 1000 && !TextUtils.isEmpty(text) && text.length() < 6) {
                committedTime += time - lastCommittedTime;
                committedText += text.length();
                committedTextCount += text.length();
                committedCount++;
            }
            lastCommittedTime = time;
        }
        updateComposing();
        return r;
    }

    public void commitTextAndClearComposition(String text) {
        commitText(text);
        Rime.clearComposition();
        updateComposing();
    }

    public void clearComposition() {
        Rime.clearComposition();
        updateComposing();
    }

    /**
     * 獲取光標處的字符
     *
     * @return 光標處的字符
     */
    private CharSequence getLastText() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            return ic.getTextBeforeCursor(1, 0);
        }
        return "";
    }

    private boolean handleAciton(int code, int mask) { //編輯操作
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return false;
        if (Event.hasModifier(mask, KeyEvent.META_CTRL_ON)) {
            // android.R.id. + selectAll, startSelectingText, stopSelectingText, cut, copy, paste, copyUrl, or switchInputMethod
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                if (code == KeyEvent.KEYCODE_V
                        && Event.hasModifier(mask, KeyEvent.META_ALT_ON)
                        && Event.hasModifier(mask, KeyEvent.META_SHIFT_ON)) {
                    return ic.performContextMenuAction(android.R.id.pasteAsPlainText);
                }
                if (code == KeyEvent.KEYCODE_S && Event.hasModifier(mask, KeyEvent.META_ALT_ON)) {
                    CharSequence cs = ic.getSelectedText(0);
                    if (cs == null) ic.performContextMenuAction(android.R.id.selectAll);
                    return ic.performContextMenuAction(android.R.id.shareText);
                }
                if (code == KeyEvent.KEYCODE_Y)
                    return ic.performContextMenuAction(android.R.id.redo);
                if (code == KeyEvent.KEYCODE_Z)
                    return ic.performContextMenuAction(android.R.id.undo);
            }
            if (code == KeyEvent.KEYCODE_V
                    && Event.hasModifier(mask, KeyEvent.META_SHIFT_ON)) {
                setCandidates(mClipboard, 4);
                return true;
            }
            if (code == KeyEvent.KEYCODE_F
                    && Event.hasModifier(mask, KeyEvent.META_SHIFT_ON)) {
                setFloat();
                return true;
            }
            if (code == KeyEvent.KEYCODE_DEL
                    && Event.hasModifier(mask, KeyEvent.META_SHIFT_ON)) {
                backToSentence();
                return true;
            }

            if (code == KeyEvent.KEYCODE_A)
                return ic.performContextMenuAction(android.R.id.selectAll);
            if (code == KeyEvent.KEYCODE_X) return ic.performContextMenuAction(android.R.id.cut);
            if (code == KeyEvent.KEYCODE_C) return ic.performContextMenuAction(android.R.id.copy);
            if (code == KeyEvent.KEYCODE_V) return ic.performContextMenuAction(android.R.id.paste);
        }
        return false;
    }

    private void backToSentence() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null)
            return;
        CharSequence text = ic.getTextBeforeCursor(128, 0);
        if (TextUtils.isEmpty(text))
            return;
        for (int i = text.length() - 1; i > 0; i--) {
            switch (text.charAt(i)) {
                case ',':
                case '.':
                case '!':
                case '?':
                case '\n':
                case '，':
                case '。':
                case '！':
                case '？':
                    if (text.length() - i > 1) {
                        ic.deleteSurroundingText(text.length() - i - 1, 0);
                        return;
                    }
            }
        }
        if (text.length() < 128)
            ic.deleteSurroundingText(text.length(), 0);
    }

    /**
     * 如果爲{@link KeyEvent#KEYCODE_BACK Back鍵}，則隱藏鍵盤
     *
     * @param keyCode {@link KeyEvent#getKeyCode() 鍵碼}
     * @return 是否處理了Back鍵事件
     */
    private boolean handleBack(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
            requestHideSelf(0);
            return true;
        }
        return false;
    }

    private boolean onRimeKey(int[] event) {
        updateRimeOption();
        boolean ret = Rime.onKey(event);
        commitText();
        return ret;
    }

    private boolean composeEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_MENU) return false; //不處理Menu鍵
        if (keyCode >= Key.getSymbolStart()) return false; //只處理安卓標準按鍵
        if (event.getRepeatCount() == 0 && KeyEvent.isModifierKey(keyCode)) {
            boolean ret =
                    onRimeKey(
                            Event.getRimeEvent(
                                    keyCode, event.getAction() == KeyEvent.ACTION_DOWN ? 0 : Rime.META_RELEASE_ON));
            if (isComposing()) setCandidatesViewShown(canCompose); //藍牙鍵盤打字時顯示候選欄
            return ret;
        }
        if (!canCompose || Rime.isVoidKeycode(keyCode)) return false;
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.info("onKeyDown=" + event);
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            try {
                if (Rime.isComposing()) {
                    onPickCandidate(Rime.getCandHighlightIndex());
                    return true;
                } else {
                    onKey(KeyEvent.KEYCODE_ENTER, 0);
                    onPress(KeyEvent.KEYCODE_ENTER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (composeEvent(event) && onKeyEvent(event)) return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //Log.info("onKeyUp=" + event);
        if (composeEvent(event) && keyUpNeeded) {
            onRelease(keyCode);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 處理實體鍵盤事件
     *
     * @param event {@link KeyEvent 按鍵事件}
     * @return 是否成功處理
     */
    private boolean onKeyEvent(KeyEvent event) {
        //Log.info("onKeyEvent=" + event);
        int keyCode = event.getKeyCode();

        boolean ret = true;
        keyUpNeeded = isComposing();

        if (!isComposing()) {
            if (keyCode == KeyEvent.KEYCODE_DEL
                    || keyCode == KeyEvent.KEYCODE_ENTER
                    || keyCode == KeyEvent.KEYCODE_ESCAPE
                    || keyCode == KeyEvent.KEYCODE_BACK) {
                return false;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            keyCode = KeyEvent.KEYCODE_ESCAPE; //返回鍵清屏
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.isCtrlPressed()
                && event.getRepeatCount() == 0
                && !KeyEvent.isModifierKey(keyCode)) {
            if (handleAciton(keyCode, event.getMetaState())) return true;
        }

        int c = event.getUnicodeChar();
        String s = String.valueOf((char) c);
        int mask = 0;
        int i = Event.getClickCode(s);
        if (i > 0) {
            keyCode = i;
        } else { //空格、回車等
            mask = event.getMetaState();
        }
        ret = handleKey(keyCode, mask);
        if (isComposing()) setCandidatesViewShown(canCompose); //藍牙鍵盤打字時顯示候選欄
        return ret;
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    public void sendEvent(String s) {
        onEvent(new Event(s));
    }

    public void sendEvent(HashMap<String, Object> s) {
        onEvent(new Event(s));
    }

    @Override
    public void onEvent(Event event) {
        String c = event.getCommit();
        if (!Function.isEmpty(c)) {
            // android.util.Log.i(TAG, "onEvent: " + event.getOption() + ";" + event.getIndex() + ";" + c);
            if (event.getOption().equals("2")) {
                onPickCandidate(event.getIndex());
                if (!isComposing())
                    setKeyboard(".default");
                //updateComposing();
                return;
            } else if (event.getOption().equals("0")) {
                if (mPhraseSort) {
                    mClipboard.remove(c);
                    mClipboard.add(0, c);
                }
            } else if (event.getOption().equals("1")) {
                if (mPhraseSort) {
                    mPhrase.remove(c);
                    mPhrase.add(0, c);
                }
            }
            commitTextAndClearComposition(c);
            ClipKeyboard.reset();
            PhraseKeyboard.reset();
            return;
        }

        String s = event.getText();
        if (!Function.isEmpty(s)) {
            onText(s);
        } else if (event.getCode() > 0) {
            int code = event.getCode();
            if (code == KeyEvent.KEYCODE_PAGE_DOWN) {
                if (mKeyboardSwitch.getCurrentKeyboard().pageDown()) {
                    setKeyboard(mKeyboardSwitch.getCurrentKeyboardName());
                    return;
                }
                /*switch (mKeyboardSwitch.getCurrentKeyboardName()) {
                    case "_clip_board":
                        ClipKeyboard.pageDown();
                        setKeyboard("_clip_board");
                        return;
                    case "_phrase_board":
                        PhraseKeyboard.pageDown();
                        setKeyboard("_phrase_board");
                        return;
                }*/
            } else if (code == KeyEvent.KEYCODE_PAGE_UP) {
                if (mKeyboardSwitch.getCurrentKeyboard().pageUp()) {
                    setKeyboard(mKeyboardSwitch.getCurrentKeyboardName());
                    return;
                }
                /*switch (mKeyboardSwitch.getCurrentKeyboardName()) {
                    case "_clip_board":
                        ClipKeyboard.pageUp();
                        setKeyboard("_clip_board");
                        return;
                    case "_phrase_board":
                        PhraseKeyboard.pageUp();
                        setKeyboard("_phrase_board");
                        return;
                }*/
            }

            if (code == KeyEvent.KEYCODE_SWITCH_CHARSET) { //切換狀態
                Rime.toggleOption(event.getToggle());
                commitText();
            } else if (code == KeyEvent.KEYCODE_EISU) { //切換鍵盤
                mKeyboardSwitch.setKeyboard(event.getSelect());
                //根據鍵盤設定中英文狀態，不能放在Rime.onMessage中做
                mTempAsciiMode = mKeyboardSwitch.getAsciiMode(); //切換到西文鍵盤時不保存狀態
                if (!event.getSelect().startsWith("_"))
                    updateAsciiMode();
                bindKeyboardToInputView();
                updateComposing();
            } else if (code == KeyEvent.KEYCODE_LANGUAGE_SWITCH) { //切換輸入法
                IBinder imeToken = getToken();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (event.getSelect().contentEquals(".next")
                        && VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                    imm.switchToNextInputMethod(imeToken, false);
                } else if (!Function.isEmpty(event.getSelect())) {
                    imm.switchToLastInputMethod(imeToken);
                } else {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker();
                }
            } else if (code == KeyEvent.KEYCODE_FUNCTION) { //命令直通車
                String cmd = event.getCommand();
                String opt = event.getOption();
                if (cmd.endsWith(".lua") && Function.isEmpty(opt)) {
                    s = Function.handle(this, cmd,
                            getActiveText(1),
                            getActiveText(2),
                            getActiveText(3),
                            getActiveText(4));
                } else {
                    String arg =
                            String.format(
                                    event.getOption(),
                                    getActiveText(1),
                                    getActiveText(2),
                                    getActiveText(3),
                                    getActiveText(4));
                    s = Function.handle(this, event.getCommand(), arg);
                }
                if (s != null) {
                    commitText(s);
                    updateComposing();
                }
            } else if (code == KeyEvent.KEYCODE_VOICE_ASSIST) { //語音輸入
                if (mSpeech != null)
                    mSpeech.start();
            } else if (code == KeyEvent.KEYCODE_SETTINGS) { //設定
                switch (event.getOption()) {
                    case "theme":
                        showThemeDialog();
                        break;
                    case "color":
                        showColorDialog();
                        break;
                    case "schema":
                        showSchemaDialog();
                        break;
                    default:
                        Function.showPrefDialog(this);
                        break;
                }
            } else if (code == KeyEvent.KEYCODE_PROG_RED) { //配色方案
                showColorDialog();
            } else {
                int keyCode = event.getCode();
                int mask = event.getMask();
                // android.util.Log.i(TAG, "Rime onKey " + keyCode+":"+mask);
                if (mAsyncKey && mask == 0 && isNumOrAlpha(keyCode))
                    onAsyncKey(keyCode, mask);
                else
                    onKey(keyCode, mask);
            }

        }
    }


    private boolean isNumOrAlpha(int keyCode) {
        return (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) || (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z);
    }

    public void onEvent2(Event event) {
        String c = event.getCommit();
        if (!Function.isEmpty(c)) {
            // android.util.Log.i(TAG, "onEvent: " + event.getOption() + ";" + event.getIndex() + ";" + c);
            if (event.getOption().equals("2")) {
                onPickCandidate(event.getIndex());
                if (!isComposing())
                    setKeyboard(".default");
                //updateComposing();
                return;
            } else if (event.getOption().equals("0")) {
                if (mPhraseSort) {
                    mClipboard.remove(c);
                    mClipboard.add(0, c);
                }
            } else if (event.getOption().equals("1")) {
                if (mPhraseSort) {
                    mPhrase.remove(c);
                    mPhrase.add(0, c);
                }
            }
            commitTextAndClearComposition(c);
            ClipKeyboard.reset();
            PhraseKeyboard.reset();
            return;
        }

        String s = event.getText();
        if (!Function.isEmpty(s)) {
            onText(s);
        } else if (event.getCode() > 0) {
            int code = event.getCode();
            if (code == KeyEvent.KEYCODE_PAGE_DOWN) {
                if (mKeyboardSwitch.getCurrentKeyboard().pageDown()) {
                    setKeyboard(mKeyboardSwitch.getCurrentKeyboardName());
                    return;
                }
            } else if (code == KeyEvent.KEYCODE_PAGE_UP) {
                if (mKeyboardSwitch.getCurrentKeyboard().pageUp()) {
                    setKeyboard(mKeyboardSwitch.getCurrentKeyboardName());
                    return;
                }
                /*switch (mKeyboardSwitch.getCurrentKeyboardName()) {
                    case "_clip_board":
                        ClipKeyboard.pageUp();
                        setKeyboard("_clip_board");
                        return;
                    case "_phrase_board":
                        PhraseKeyboard.pageUp();
                        setKeyboard("_phrase_board");
                        return;
                }*/
            }

            if (code == KeyEvent.KEYCODE_SWITCH_CHARSET) { //切換狀態
                Rime.toggleOption(event.getToggle());
                commitText();
            } else if (code == KeyEvent.KEYCODE_EISU) { //切換鍵盤
                mKeyboardSwitch.setKeyboard(event.getSelect());
                //根據鍵盤設定中英文狀態，不能放在Rime.onMessage中做
                mTempAsciiMode = mKeyboardSwitch.getAsciiMode(); //切換到西文鍵盤時不保存狀態
                if (!event.getSelect().startsWith("_"))
                    updateAsciiMode();
                bindKeyboardToInputView();
                updateComposing();
            } else if (code == KeyEvent.KEYCODE_LANGUAGE_SWITCH) { //切換輸入法
                IBinder imeToken = getToken();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (event.getSelect().contentEquals(".next")
                        && VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                    imm.switchToNextInputMethod(imeToken, false);
                } else if (!Function.isEmpty(event.getSelect())) {
                    imm.switchToLastInputMethod(imeToken);
                } else {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker();
                }
            } else if (code == KeyEvent.KEYCODE_FUNCTION) { //命令直通車
                String arg =
                        String.format(
                                event.getOption(),
                                getActiveText(1),
                                getActiveText(2),
                                getActiveText(3),
                                getActiveText(4));
                s = Function.handle(this, event.getCommand(), arg);
                if (s != null) {
                    commitText(s);
                    updateComposing();
                }
            } else if (code == KeyEvent.KEYCODE_VOICE_ASSIST) { //語音輸入
                if (mSpeech != null)
                    mSpeech.start();
            } else if (code == KeyEvent.KEYCODE_SETTINGS) { //設定
                switch (event.getOption()) {
                    case "theme":
                        showThemeDialog();
                        break;
                    case "color":
                        showColorDialog();
                        break;
                    case "schema":
                        showSchemaDialog();
                        break;
                    default:
                        Function.showPrefDialog(this);
                        break;
                }
            } else if (code == KeyEvent.KEYCODE_PROG_RED) { //配色方案
                showColorDialog();
            } else {
                int keyCode = event.getCode();
                int mask = event.getMask();
                // android.util.Log.i(TAG, "Rime onKey " + keyCode+":"+mask);
                onKey(keyCode, mask);
            }
        }
    }

    private boolean handleKey(int keyCode, int mask) { //軟鍵盤
        keyUpNeeded = false;
        long ms = System.currentTimeMillis();
        if (onRimeKey(Event.getRimeEvent(keyCode, mask))) {
            keyUpNeeded = true;
            // android.util.Log.i(TAG, "Rime onKey " + (System.currentTimeMillis() - ms));
        } else if (handleAciton(keyCode, mask)
                || handleOption(keyCode)
                || handleEnter(keyCode)
                || handleBack(keyCode)) {
            // android.util.Log.i(TAG, "Trime onKey");
        } else if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && Function.openCategory(this, keyCode)) {
            // android.util.Log.i(TAG, "open category");
        } else {
            keyUpNeeded = true;
            return false;
        }
        return true;
    }

    private void sendKey(InputConnection ic, int key, int meta, int action) {
        long now = System.currentTimeMillis();
        if (ic != null) ic.sendKeyEvent(new KeyEvent(now, now, action, key, 0, meta));
    }

    private void sendKeyDown(InputConnection ic, int key, int meta) {
        sendKey(ic, key, meta, KeyEvent.ACTION_DOWN);
    }

    private void sendKeyUp(InputConnection ic, int key, int meta) {
        sendKey(ic, key, meta, KeyEvent.ACTION_UP);
    }

    private void sendDownUpKeyEvents(int keyCode, int mask) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        int states =
                KeyEvent.META_FUNCTION_ON
                        | KeyEvent.META_SHIFT_MASK
                        | KeyEvent.META_ALT_MASK
                        | KeyEvent.META_CTRL_MASK
                        | KeyEvent.META_META_MASK
                        | KeyEvent.META_SYM_ON;
        ic.clearMetaKeyStates(states);
        if (mKeyboardView != null && mKeyboardView.isShifted()) {
            if (keyCode == KeyEvent.KEYCODE_MOVE_HOME
                    || keyCode == KeyEvent.KEYCODE_MOVE_END
                    || keyCode == KeyEvent.KEYCODE_PAGE_UP
                    || keyCode == KeyEvent.KEYCODE_PAGE_DOWN
                    || (keyCode >= KeyEvent.KEYCODE_DPAD_UP && keyCode <= KeyEvent.KEYCODE_DPAD_RIGHT)) {
                mask |= KeyEvent.META_SHIFT_ON;
            }
        }

        if (Event.hasModifier(mask, KeyEvent.META_SHIFT_ON)) {
            sendKeyDown(
                    ic, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.META_SHIFT_ON | KeyEvent.META_SHIFT_LEFT_ON);
        }
        if (Event.hasModifier(mask, KeyEvent.META_CTRL_ON)) {
            sendKeyDown(
                    ic, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
        }
        if (Event.hasModifier(mask, KeyEvent.META_ALT_ON)) {
            sendKeyDown(ic, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.META_ALT_ON | KeyEvent.META_ALT_LEFT_ON);
        }
        sendKeyDown(ic, keyCode, mask);
        sendKeyUp(ic, keyCode, mask);
        if (Event.hasModifier(mask, KeyEvent.META_ALT_ON)) {
            sendKeyUp(ic, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.META_ALT_ON | KeyEvent.META_ALT_LEFT_ON);
        }
        if (Event.hasModifier(mask, KeyEvent.META_CTRL_ON)) {
            sendKeyUp(ic, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
        }
        if (Event.hasModifier(mask, KeyEvent.META_SHIFT_ON)) {
            sendKeyUp(
                    ic, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.META_SHIFT_ON | KeyEvent.META_SHIFT_LEFT_ON);
        }
    }

    private int currKeyIdx = 0;
    private AsyncThread mAsyncThread = new AsyncThread();

    private final class AsyncThread extends Thread {
        private AsyncHandler mAsyncHandler;

        @Override
        public void run() {
            Looper.prepare();
            mAsyncHandler = new AsyncHandler();
            Looper.loop();
        }

        public void asyncKey(final int keyCode, final int mask) {
            Message msg = new Message();
            msg.arg1 = keyCode;
            msg.arg2 = mask;
            msg.what = 0;
            mAsyncHandler.sendMessage(msg);
        }

        public void asyncSelectCandidate(int i) {
            Message msg = new Message();
            msg.arg1 = i;
            msg.what = 100;
            mAsyncHandler.sendMessage(msg);

        }
    }

    @SuppressLint("HandlerLeak")
    private class AsyncHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            android.util.Log.i(TAG, "handleMessage: " + msg);
            switch (msg.what) {
                case 0:
                    if (Rime.onKey(Event.getRimeEvent(msg.arg1, msg.arg2))) {
                        mHandler.sendEmptyMessage(0);
                    } else {
                        Message msg1 = new Message();
                        msg1.arg1 = msg.arg1;
                        msg1.arg2 = msg.arg2;
                        msg1.what = 1;
                        mHandler.sendMessage(msg1);
                    }
                    break;
                case 100:
                    if (Rime.selectCandidate(msg.arg1)) {
                        mHandler.sendEmptyMessage(101);
                    } else {
                        mHandler.sendEmptyMessage(100);
                    }
                    break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void onAsyncKey(final int keyCode, final int mask) {
        final int idx = ++currKeyIdx;
        keyUpNeeded = false;
        final long ms = System.currentTimeMillis();
        try {
            updateRimeOption();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAsyncThread.asyncKey(keyCode, mask);
        android.util.Log.i(TAG, "Rime onAsyncKey end " + (System.currentTimeMillis() - ms));
    }


    @Override
    public void onKey(int keyCode, int mask) { //軟鍵盤
        if (handleKey(keyCode, mask)) return;
        if (keyCode >= Key.getSymbolStart()) { //符號
            keyUpNeeded = false;
            commitText(Event.getDisplayLabel(keyCode));
            return;
        }
        keyUpNeeded = false;
        sendDownUpKeyEvents(keyCode, mask);
    }

    @Override
    public void onText(CharSequence text) { //軟鍵盤
        //Log.info("onText=" + text);
        //mEffect.speakKey(text);
        String s = text.toString();
        String t;
        Pattern p = Pattern.compile("^(\\{[^{}]+\\}).*$");
        Pattern pText = Pattern.compile("^((\\{Escape\\})?[^{}]+).*$");
        Matcher m;
        while (s.length() > 0) {
            m = pText.matcher(s);
            if (m.matches()) {
                t = m.group(1);
                Rime.onText(t);
                if (!commitText() && !isComposing()) commitText(t);
                updateComposing();
            } else {
                m = p.matcher(s);
                t = m.matches() ? m.group(1) : s.substring(0, 1);
                onEvent2(new Event(t));
            }
            s = s.substring(t.length());
        }
        keyUpNeeded = false;
    }

    @Override
    public void onPress(int keyCode) {
        mEffect.vibrate();
        mEffect.playSound(keyCode);
        //mEffect.speakKey(keyCode);
    }

    @Override
    public void onPress(Key key) {
        if (key == null)
            return;

        if (isTouchExplorationEnabled())
            mEffect.speak(mTextFormatter.formatSymbols(key.getDescription()));
        else
            mEffect.speakKey(mTextFormatter.formatSymbols(key.getDescription()));
    }


    @Override
    public void onRelease(int keyCode) {
        if (keyUpNeeded) {
            onRimeKey(Event.getRimeEvent(keyCode, Rime.META_RELEASE_ON));
        }
        if (mSpeech != null) {
            if (mSpeech.getState() == Speech.STATE_BEGIN)
                mSpeech.stop();
        }
    }

    @Override
    public void onUp(int keyCode) {
        //android.util.Log.i(TAG, "onUp: "+keyCode+":"+mSpeech.getState());
        if (mSpeech != null) {
            if (mSpeech.getState() == Speech.STATE_BEGIN)
                mSpeech.stop();
        }
    }

    @Override
    public void swipeLeft() {
        // no-op
    }

    @Override
    public void swipeRight() {
        // no-op
    }

    @Override
    public void swipeUp() {
        // no-op
    }

    /**
     * 在鍵盤視圖中從上往下滑動，隱藏鍵盤
     */
    @Override
    public void swipeDown() {
        //requestHideSelf(0);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onPickCandidate(int i) {
        // Commit the picked candidate and suggest its following words.
        //onPress(0);
        final long ms = System.currentTimeMillis();
        if (i == Candidate.HIDE) {
            if (!isComposing())
                onKey(KeyEvent.KEYCODE_BACK, 0);
            else if (mKeyboardSwitch.getCurrentKeyboardName().equals("_candidate_board"))
                setKeyboard(".default");
            else
                setKeyboard("_candidate_board");
            return;
        }

        if (i >= 0 && Rime.hasCandidates()) {
            if (!Rime.hasMenu()) {
                commitTextAndClearComposition(Rime.getCandidates()[i].text);
                return;
            }
            if (mCandidateLists.size() > i) {
                String text = mCandidateLists.get(i);
                commitTextAndClearComposition(text);
                return;
            } else {
                i = i - mCandidateLists.size();
            }
        }
        if (!isComposing()) {
            if (i >= 0) {
                String name = Rime.getOptionName(i);
                if (Key.getPresetKeys().containsKey(name)) {
                    onEvent(new Event(name));
                    return;
                }
                Rime.toggleOption(i);
                updateComposing();
            }
        } else if (i == Candidate.PAGE_UP) {
            onKey(KeyEvent.KEYCODE_PAGE_UP, 0);
        } else if (i == Candidate.PAGE_DOWN) {
            onKey(KeyEvent.KEYCODE_PAGE_DOWN, 0);
        }/* else if (Rime.selectCandidate(i)) {
            commitText();
        }*/
        if (mAsyncKey) {
            mAsyncThread.asyncSelectCandidate(i);
            /*new AsyncTask<Integer, Boolean, Boolean>() {
                @Override
                protected Boolean doInBackground(Integer... is) {
                    return Rime.selectCandidate(is[0]);
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    // android.util.Log.i(TAG, "Rime onAsyncPickCandidate " + (System.currentTimeMillis()-ms));
                    if (aBoolean)
                        commitText();
                }
            }.execute(i);*/
        } else if (Rime.selectCandidate(i)) {
            commitText();
        }
        // android.util.Log.i(TAG, "Rime onPickCandidate " + (System.currentTimeMillis()-ms));
    }

    @Override
    public void onSelectCandidate(String text) {
        if (TextUtils.isEmpty(text))
            return;
        try {
            String text1;
            if (isComposing()) {
                StringBuilder mList = new StringBuilder(100);
                if (text.length() > 1)
                    mList.append(text).append("，");
                text1 = mTextFormatter.formatSymbols2(text);
                if (text1 == null) {
                    for (int i = 0; i < text.length(); i++) {
                        mList.append(mTextFormatter.format(String.valueOf(text.charAt(i)))).append("，");
                    }
                    text1 = mList.toString();
                }
            } else {
                text1 = text;
            }
            if (isTouchExplorationEnabled())
                mEffect.speak(text1);
            else
                mEffect.speakCommit(text1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCompositions(ArrayList<String> list) {
        mComposition.addCompositions(list);
        mFloatingWindowTimer.postShowFloatingWindow2();
    }

    public void addCandidates(ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "剪切板为空", Toast.LENGTH_SHORT).show();
        } else {
            Rime.RimeCandidate[] cs = Rime.getCandidates();
            if (cs == null)
                cs = new Rime.RimeCandidate[0];
            mCandidateLists = list;
            int size = list.size();
            size = Math.min(size, 30);
            Rime.RimeCandidate[] candidates = new Rime.RimeCandidate[size + cs.length];
            for (int i = 0; i < size; i++) {
                String s = list.get(i);
                s = s.trim();
                Rime.RimeCandidate cd = new Rime.RimeCandidate();
                cd.text = s;
                candidates[i] = cd;
            }
            for (int i = 0; i < cs.length; i++) {
                candidates[i + size] = cs[i];
                //mCandidateLists.add(cs[i].text);
            }

            Rime.setCandidates(candidates);
            mCandidate.setText(0);
        }
    }

    public void setCandidates(ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "剪切板为空", Toast.LENGTH_SHORT).show();
        } else {
            mCandidateLists = list;
            int size = list.size();
            size = Math.min(size, 30);
            Rime.RimeCandidate[] candidates = new Rime.RimeCandidate[size];
            for (int i = 0; i < size; i++) {
                String s = list.get(i);
                s = s.trim();
                Rime.RimeCandidate cd = new Rime.RimeCandidate();
                cd.text = s;
                candidates[i] = cd;
            }
            Rime.setCandidates(candidates);
            mCandidate.setText(0);
        }
    }

    public void setCandidates(ArrayList<String> list, int len) {
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "剪切板为空", Toast.LENGTH_SHORT).show();
        } else {
            mCandidateLists = list;
            int size = list.size();
            size = Math.min(size, 30);
            Rime.RimeCandidate[] candidates = new Rime.RimeCandidate[size];
            for (int i = 0; i < size; i++) {
                String s = list.get(i);
                s = s.trim();
                Rime.RimeCandidate cd = new Rime.RimeCandidate();
                if (s.length() > len) {
                    cd.text = s.substring(0, len) + "...";
                } else {
                    cd.text = s;
                }
                candidates[i] = cd;
            }
            Rime.setCandidates(candidates);
            mCandidate.setText(0);
        }
    }

    /**
     * 獲得當前漢字：1,候選字、2,選中字、3,剛上屏字/光標前字/光標前所有字、4,光標後所有字
     */
    private String getActiveText(int type) {
        if (type == 2)
            return Rime.RimeGetInput(); //當前編碼
        String s = Rime.getComposingText(); //當前候選
        if (Function.isEmpty(s)) {
            InputConnection ic = getCurrentInputConnection();
            CharSequence cs = ic.getSelectedText(0); //選中字
            if (type == 1 && Function.isEmpty(cs))
                cs = lastCommittedText; //剛上屏字
            if (Function.isEmpty(cs)) {
                cs = ic.getTextBeforeCursor(type == 4 ? 1024 : 1, 0); //光標前字
            }
            if (Function.isEmpty(cs))
                cs = ic.getTextAfterCursor(1024, 0); //光標後面所有字
            if (cs != null) s = cs.toString();
        }
        return s;
    }

    private String getAfterText() {
        String s = "";
        InputConnection ic = getCurrentInputConnection();
        CharSequence cs = ic.getTextAfterCursor(1024 * 10, 0); //光標後面所有字
        if (cs != null) s = cs.toString();
        return s;
    }

    private String getBeforeText() {
        String s = "";
        InputConnection ic = getCurrentInputConnection();
        CharSequence cs = ic.getTextBeforeCursor(1024 * 10, 0); //光標前面所有字
        if (cs != null) s = cs.toString();
        return s;
    }

    private int _h = 0;

    private int getItemHeight() {
        if (_h != 0)
            return _h;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView item = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
        item.measure(0, 0);
        _h = item.getMeasuredHeight();
        return _h;
    }

    /**
     * 更新Rime的中西文狀態、編輯區文本
     */
    public void updateComposing() {
        String py = Rime.getCompositionText();
        Rime.setCandidates(null);
        showCloud(null);
        if (!TextUtils.isEmpty(py)) {
            py = py.replace("‸", "");
        }
        if (mCloudInput && !isTouchExplorationEnabled() && !TextUtils.isEmpty(py)) {
            if (!py.equals(mLastPinyin)) {
                cancelCloudInput();
                mLastPinyin = py;
                if (Rime.get_input().length() > 1) {
                    mCloudInputRunnable = new CloudInputRunnable(py);
                    mHandler.postDelayed(mCloudInputRunnable, 500);

                }
            }
        }

        InputConnection ic = getCurrentInputConnection();
        if (!isTouchExplorationEnabled() && inlinePreedit != InlineModeType.INLINE_NONE) { //嵌入模式
            String s = null;
            switch (inlinePreedit) {
                case INLINE_PREVIEW:
                    s = Rime.getComposingText();
                    break;
                case INLINE_COMPOSITION:
                    s = Rime.getCompositionText();
                    break;
                case INLINE_INPUT:
                    s = Rime.RimeGetInput();
                    break;
            }

            if (s == null) s = "";
            if (ic != null) {
                CharSequence cs = ic.getSelectedText(0);
                if (cs == null || !Function.isEmpty(s)) {
                    // 無選中文本或編碼不爲空時更新編輯區
                    ic.setComposingText(s, 1);
                }
            }
        }
        if (ic != null && !isWinFixed() && VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
            cursorUpdated = ic.requestCursorUpdates(1);
        if (mCandidateContainer != null) {
            if (mShowWindow && !isTouchExplorationEnabled()) {
                int start_num = mComposition.setWindow(min_length);
                mCandidate.setText(start_num);

                if (isWinFixed() || !cursorUpdated || !mUpdateCursorAnchor)
                    mFloatingWindowTimer.postShowFloatingWindow();
            } else {
                mCandidate.setText(0);
            }
        }
        if (mKeyboardView != null)
            mKeyboardView.invalidateComposingKeys();
        if (!onEvaluateInputViewShown())
            setCandidatesViewShown(canCompose); //實體鍵盤打字時顯示候選欄
        if (mKeyboardSwitch.getCurrentKeyboardName().equals("_candidate_board")) {
            if (isComposing())
                setKeyboard("_candidate_board");
            else
                setKeyboard(".default");
        }
        if (!TextUtils.isEmpty(py))
            runFunc("updateComposing", Rime.get_input(), py, Rime.getComposingText());
    }

    private void updateCandidate() {
        if (mCandidateContainer != null) {
            mCandidate.setText(0);
        }
        if (mKeyboardView != null)
            mKeyboardView.invalidateComposingKeys();
        if (!onEvaluateInputViewShown())
            setCandidatesViewShown(canCompose); //實體鍵盤打字時顯示候選欄
    }

    private String mLastPinyin;

    private void cancelCloudInput() {
        if (mCloudInputRunnable == null)
            return;
        mHandler.removeCallbacks(mCloudInputRunnable);
        mCloudInputRunnable = null;
    }

    private class CloudInputRunnable implements Runnable {
        private final String mPy;

        public CloudInputRunnable(String py) {
            mPy = py;
        }

        @Override
        public void run() {
            cloudInput(mPy);
            mCloudInputRunnable = null;
        }
    }

    private void cloudInput(String py) {
        if (!TextUtils.isEmpty(py) && !py.startsWith("`")) {
            py = py.replaceAll("ee", "e").replaceAll("aa", "a").replaceAll("oo", "o").replaceAll("ü", "v").replaceAll(" ", "");
            if (!Pattern.matches("^[a-zA-Z]+$", py)) {
                showCloud(null);
                return;
            }
            Cloud.get(py, new Cloud.CloudCallback() {
                @Override
                public void onDone(ArrayList<String> list) {
                    if (mShowWindow) {
                        mComposition.setCloud(list);
                        mFloatingWindowTimer.postShowFloatingWindow();
                    } else {
                        showCloud(list);
                    }
                }

                @Override
                public void onDone(String text) {
                    if (mShowWindow) {
                        mComposition.setCloud(text);
                        mFloatingWindowTimer.postShowFloatingWindow();
                    }
                }
            });
        } else {
            showCloud(null);
        }
    }

    public void showCloud(ArrayList<String> list) {
        if (mCloudWindow == null)
            return;
        if (list == null || list.isEmpty() || !Rime.isComposing()) {
            if (mCloudWindow.isShowing())
                mCloudWindow.dismiss();
        } else {
            mCloudView.setCandidates(list);
            mCloudView.setText(0);
            int[] mParentLocation = getLocationOnScreen(mCandidateContainer);
            if (mFloatingWindow.isShowing())
                mParentLocation = getLocationOnScreen(mCompositionContainer);
            mCloudWindow.showAtLocation(mCandidateContainer, Gravity.START | Gravity.TOP, 0, mParentLocation[1] - mCloudY);
        }
    }

    public static int getDialogType() {
        int dialogType = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            dialogType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY; //Android P中AlertDialog要顯示在最上層
        }
        return dialogType;
        /*if(VERSION.SDK_INT >= VERSION_CODES.M && Settings.canDrawOverlays(getService())){
            return dialogType;
        }
        return WindowManager.LayoutParams.FIRST_SUB_WINDOW;*/
    }

    private void showDialog(AlertDialog dialog) {

        Window window = dialog.getWindow();
        mFloatingWindow.setClippingEnabled(false);

        WindowManager.LayoutParams lp = window.getAttributes();
        if (mCandidateContainer != null)
            lp.token = mCandidateContainer.getWindowToken();
        lp.type = getDialogType();
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.show();
    }

    public void editFile(String path) {
        startActivity(new Intent(this, OpenActivity.class).setData(Uri.fromFile(new File(path))));
    }

    /**
     * 彈出{@link ColorDialog 配色對話框}
     */
    public void showColorDialog() {
        AlertDialog dialog = new ColorDialog(this).getDialog();
        showDialog(dialog);
    }

    /**
     * 彈出{@link SchemaDialog 輸入法方案對話框}
     */
    private void showSchemaDialog() {
        new SchemaDialog(this, mCandidateContainer.getWindowToken());
    }

    /**
     * 彈出{@link ThemeDlg 配色對話框}
     */
    private void showThemeDialog() {
        new ThemeDlg(this, mCandidateContainer.getWindowToken());
    }

    private boolean handleOption(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mOptionsDialog != null && mOptionsDialog.isShowing()) return true; //對話框單例
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.ime_name)
                            //.setCancelable(true)
                            .setPositiveButton(
                                    R.string.set_ime,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface di, int id) {
                                            Function.showPrefDialog(Trime.this); //全局設置
                                            di.dismiss();
                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel, null);
            if (Rime.isEmpty()) {
                builder.setMessage(R.string.no_schemas); //提示安裝碼表
            } else {
                builder.setNeutralButton(
                        R.string.pref_schemas,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int id) {
                                showSchemaDialog(); //部署方案
                                di.dismiss();
                            }
                        });
                /*builder.setNeutralButton(
                        R.string.pref_themes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int id) {
                                showThemeDialog(); //选择主题
                                di.dismiss();
                            }
                        });*/
                builder.setSingleChoiceItems(
                        Rime.getSchemaNames(),
                        Rime.getSchemaIndex(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int id) {
                                di.dismiss();
                                Rime.selectSchema(id); //切換方案
                                mNeedUpdateRimeOption = true;
                                Function.getPref(self).edit().putString("select_schema_id", Rime.getSchemaId()).apply();
                            }
                        });
            }
            mOptionsDialog = builder.create();
            showDialog(mOptionsDialog);
            return true;
        }
        return false;
    }

    /**
     * 如果爲{@link KeyEvent#KEYCODE_ENTER 回車鍵}，則換行
     *
     * @param keyCode {@link KeyEvent#getKeyCode() 鍵碼}
     * @return 是否處理了回車事件
     */
    private boolean handleEnter(int keyCode) { //回車
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (enterAsLineBreak) {
                commitText("\n");
            } else {
                sendKeyChar('\n');
            }
            return true;
        }
        return false;
    }

    /**
     * 模擬PC鍵盤中Esc鍵的功能：清除輸入的編碼和候選項
     */
    private void escape() {
        if (isComposing()) onKey(KeyEvent.KEYCODE_ESCAPE, 0);
    }

    /**
     * 更新Rime的中西文狀態
     */
    private void updateAsciiMode() {
        Rime.setOption("ascii_mode", mTempAsciiMode || mAsciiMode);
    }

}
