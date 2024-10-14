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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Scroller;

import com.androlua.LuaBitmapDrawable;

import java.util.Arrays;
import java.util.HashMap;

/**
 * 顯示候選字詞
 */
public class Candidate extends View {
    public static final int PAGE_UP = -4;
    public static final int PAGE_DOWN = -5;
    public static final int HIDE = -8;

    private final Scroller mScroller;
    private final Paint paintBackgound;
    private int width = 1080;
    private boolean mCandidateScroll;
    private int mDownX;
    private int mDownY;
    private boolean mShowWindow;
    private Rect hideRect;
    private Drawable back_color;
    private Drawable candidate_back_color;
    private String candidate_hide_button;
    private int mHideButtonWidth;
    private int mAlpha;
    private Config config;
    private boolean inHover;
    private String candidate_left_button = "◀";
    private String candidate_right_button = "▶";
    private LuaBitmapDrawable mHideButtonDraw;
    private LuaBitmapDrawable rightDraw;
    private LuaBitmapDrawable leftDraw;
    private HashMap<String, LuaBitmapDrawable> mDrawCache=new HashMap<>();

    public int getDownX() {
        return mDownX;
    }

    public int getDownY() {
        return mDownY;
    }

    /**
     * 處理候選條選字事件
     */
    public interface CandidateListener {
        void onPickCandidate(int index);

        void onSelectCandidate(String index);
    }

    private static final int MAX_CANDIDATE_COUNT = 130;
    private static final int CANDIDATE_TOUCH_OFFSET = -12;

    private CandidateListener listener;
    private int highlightIndex;
    private Rime.RimeCandidate[] candidates;
    private int num_candidates;
    private int start_num = 0;

    private Drawable candidateHighlight, candidateSeparator;
    private Paint paintCandidate, paintSymbol, paintComment;
    private Typeface tfCandidate, tfSymbol, tfComment, tfHanB, tfLatin;
    private int candidate_text_color, hilited_candidate_text_color;
    private int comment_text_color, hilited_comment_text_color;
    private int candidate_view_height, comment_height, candidate_spacing, candidate_padding;
    private boolean show_comment = true, comment_on_top, candidate_use_cursor;

    private Rect candidateRect[] = new Rect[MAX_CANDIDATE_COUNT * 2];
    private Drawable candidateDrawable[] = new Drawable[MAX_CANDIDATE_COUNT * 2];

    public void reset() {
        mDrawCache.clear();
        config = Config.get();
        mAlpha = config.getKeyAlpha();
        candidateHighlight = config.getColorDrawable("hilited_candidate_back_color");
        if (candidateHighlight instanceof GradientDrawable) {
            GradientDrawable gd = ((GradientDrawable) candidateHighlight);
            gd.setCornerRadius(config.getFloat("hilited_candidate_round_corner"));
            Integer c = config.getColor("hilited_candidate_border_color");
            if (c == null)
                c = config.getColor("border_color");
            gd.setStroke(config.getPixel("hilited_candidate_border"), c);
        }
        candidateHighlight.setAlpha(mAlpha);

        //candidateHighlight = new PaintDrawable(config.getColor("hilited_candidate_back_color"));
        //((PaintDrawable) candidateHighlight).setCornerRadius(config.getFloat("layout/round_corner"));
        candidateSeparator = new PaintDrawable(config.getColor("candidate_separator_color"));
        candidate_spacing = config.getPixel("candidate_spacing");
        candidate_padding = (int) Math.max(config.getPixel("candidate_padding"), config.getFloat("hilited_candidate_round_corner"));
        candidate_hide_button = config.getString("candidate_hide_button");
        if (!TextUtils.isEmpty(candidate_hide_button)) {
            mHideButtonDraw = config.getDrawableObject(candidate_hide_button);
            if (mHideButtonDraw != null) {
                mHideButtonWidth = (int) mHideButtonDraw.getWidth(getHeight());
            } else {
                mHideButtonWidth = (int) (measureText(candidate_hide_button, paintCandidate, tfCandidate) * 1.5f);
                mHideButtonWidth = Math.max(mHideButtonWidth, width / 10);
            }
        } else {
            mHideButtonWidth = 0;
        }
        if (config.hasKey("candidate_left_button"))
            candidate_left_button = config.getString("candidate_left_button");
        else
            candidate_left_button = "◀";

        if (config.hasKey("candidate_right_button"))
            candidate_right_button = config.getString("candidate_right_button");
        else
            candidate_right_button = "▶";
        rightDraw = config.getDrawableObject(candidate_right_button);
        leftDraw = config.getDrawableObject(candidate_left_button);

        candidate_text_color = config.getColor("candidate_text_color");
        comment_text_color = config.getColor("comment_text_color");
        hilited_candidate_text_color = config.getColor("hilited_candidate_text_color");
        hilited_comment_text_color = config.getColor("hilited_comment_text_color");

        int candidate_text_size = config.getPixel("candidate_text_size");
        int comment_text_size = config.getPixel("comment_text_size");
        candidate_view_height = config.getPixel("candidate_view_height");
        comment_height = config.getPixel("comment_height");

        tfCandidate = config.getFont("candidate_font");
        tfLatin = config.getFont("latin_font");
        tfHanB = config.getFont("hanb_font");
        tfComment = config.getFont("comment_font");
        tfSymbol = config.getFont("symbol_font");

        paintCandidate.setTextSize(candidate_text_size);
        paintCandidate.setTypeface(tfCandidate);
        paintSymbol.setTextSize(candidate_text_size);
        paintSymbol.setTypeface(tfSymbol);
        paintComment.setTextSize(comment_text_size);
        paintComment.setTypeface(tfComment);

        comment_on_top = config.getBoolean("comment_on_top");
        candidate_use_cursor = config.getBoolean("candidate_use_cursor");
        mCandidateScroll = config.isCandidateScroll();
        mShowWindow = config.getShowWindow();
        back_color = config.getColorDrawable("back_color");
        //back_color = config.getColor("back_color");
        //paintBackgound.setColor(back_color);
        invalidate();
    }

    public void setShowComment(boolean value) {
        show_comment = value;
    }


    private final GestureDetector mGestureDetector;
    private final int mSwipeThreshold;
    private final boolean mDisambiguateSwipe;
    private boolean mShowing;

    public Candidate(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintCandidate = new Paint();
        paintCandidate.setAntiAlias(true);
        paintCandidate.setStrokeWidth(0);
        paintSymbol = new Paint();
        paintSymbol.setAntiAlias(true);
        paintSymbol.setStrokeWidth(0);
        paintComment = new Paint();
        paintComment.setAntiAlias(true);
        paintComment.setStrokeWidth(0);
        paintBackgound = new Paint();

        reset();

        setWillNotDraw(false);

        mSwipeThreshold = (int) (10 * getResources().getDisplayMetrics().density);
        mDisambiguateSwipe = true;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public float mTouchY;
            public int mLastY;
            private float mTouchX;
            private int mLastX;

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (!mCandidateScroll || inHover)
                    return false;
                int x = (int) e.getX() + getScrollX();
                int y = (int) e.getY();
                if (updateHighlight(x, y)) {
                    performClick();
                    pickHighlighted(-1);
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                //Rime.onKey(new int[]{KeyEvent.KEYCODE_DEL, KeyEvent.META_CTRL_ON});
            }

            @Override
            public void onShowPress(MotionEvent e) {
                if (!mCandidateScroll || inHover)
                    return;
                int x = (int) e.getX() + getScrollX();
                int y = (int) e.getY();
                updateHighlight(x, y);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Trime trime = Trime.getService();
                if (trime == null)
                    return false;
                if (trime.isKeyBoardFloat()) {
                    Window win = trime.getWindow().getWindow();
                    WindowManager.LayoutParams attr = win.getAttributes();
                    mLastY = attr.y;
                    mTouchY = e.getRawY();
                    mLastX = attr.x;
                    mTouchX = e.getRawX();
                }

                return super.onDown(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Trime trime = Trime.getService();
                if (trime == null)
                    return false;

                if (trime.isKeyBoardFloat() && highlightIndex < 0) {
                    /*Log.i(TAG, "onScroll: "
                            +"\n RawY "+e2.getRawY()
                            +"；\n TouchY "+mTouchY
                            +"；\n RawX "+e2.getRawX()
                            +"；\n TouchX "+mTouchX
                            +"；\n distanceY "+distanceY
                            +"；\n distanceX "+distanceX
                            +"；\n LastY "+mLastY
                            +"；\n LastX "+mLastX
                            +"；\n attr.y "+(mLastY+(e2.getRawY()-mTouchY))
                            +"；\n attr.x "+(mLastX+(e2.getRawX()-mTouchX))

                    );*/
                    /*if((distanceY>0&&mLastD1<0)||(distanceY<0&&mLastD1>0)){
                        mLastD1=distanceY;
                        return true;
                    }
                    if(distanceY==0)
                        distanceY=10;
                    mLastD1=distanceY;*/
                    if (distanceY == 0)
                        distanceY = 10;
                    else
                        distanceY = 0;
                    if (distanceX == 0)
                        distanceX = 10;
                    else
                        distanceX = 0;

                    Window win = trime.getWindow().getWindow();
                    WindowManager.LayoutParams attr = win.getAttributes();
                    attr.y = (int) (mLastY + (e2.getRawY() - mTouchY - distanceY));
                    attr.x = (int) (mLastX + (e2.getRawX() - mTouchX - distanceX));
                    win.setAttributes(attr);

                    return true;
                }

                if (!mCandidateScroll || inHover)
                    return false;
                int x = (int) (getScrollX() + distanceX);
                if (x < 0)
                    return true;
                if (x > getWidth() - width)
                    return true;
                scrollTo(x, 0);
                /*int x = (int) e2.getX() + getScrollX();
                int y = (int) e2.getY();
                updateHighlight(x, y);*/
                return true;
            }

            @Override
            public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                final float absX = Math.abs(velocityX);
                final float absY = Math.abs(velocityY);
                float deltaX = me2.getX() - me1.getX();
                float deltaY = me2.getY() - me1.getY();
                int w = Math.min(getWidth(), getHeight());
                int travelX = w / 8; // Half the keyboard width
                int travelY = w / 8; // Half the keyboard height
                if (velocityX > mSwipeThreshold && absY < absX && deltaX > travelX) {
                    swipeRight();
                    return true;
                } else if (velocityX < -mSwipeThreshold && absY < absX && deltaX < -travelX) {
                    swipeLeft();
                    return true;
                } else if (velocityY < -mSwipeThreshold && absX < absY && deltaY < -travelY) {
                    swipeUp();
                    return true;
                } else if (velocityY > mSwipeThreshold && absX < absY / 2 && deltaY > travelY) {
                    swipeDown();
                    return true;
                }
                return false;
            }
        });
        mGestureDetector.setIsLongpressEnabled(true);
        mScroller = new Scroller(context);
    }

    public void smoothScrollBy(int x, int y) {
        mScroller.startScroll(getScrollX(), getScrollY(), x, y);
        postInvalidateOnAnimation();
    }

    public void smoothScrollTo(int x, int y) {
        mScroller.startScroll(getScrollX(), getScrollY(), x - getScrollX(), y - getScrollY());
        postInvalidateOnAnimation();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getStartY());
            postInvalidateOnAnimation();
        }
    }

    private void swipeRight() {
        if (getScrollX() == 0 && Rime.hasLeft()) {
            pickHighlighted(PAGE_UP);
            //scrollTo(0,0);
            return;
        }
        if (getScrollX() < width) {
            smoothScrollTo(0, 0);
            return;
        }
        int x = Math.min(width, getScrollX());
        for (Rect rect : candidateRect) {
            if (rect != null && rect.contains(getScrollX(), 0)) {
                x = getScrollX() + width - rect.right;
                break;
            }
        }
        smoothScrollBy(0 - x - (getScrollX() - getDownX()), 0);
    }

    private final String TAG = "rime";

    private void swipeLeft() {
        if (getScrollX() + width == getWidth() && Rime.hasRight()) {
            pickHighlighted(PAGE_DOWN);
            //scrollTo(0,0);
            return;
        }
        if (getScrollX() + width * 2 >= getWidth()) {
            smoothScrollTo(getWidth() - width, 0);
            return;
        }
        int x = Math.min(width, getWidth() - getScrollX() - width);
        for (Rect rect : candidateRect) {
            if (rect != null && rect.contains(getScrollX() + width, 0)) {
                x = rect.left - getScrollX();
                break;
            }
        }
        smoothScrollBy(x - (getScrollX() - getDownX()), 0);
    }

    private void swipeUp() {
        Trime trime = Trime.getService();
        if (trime != null)
            trime.onKey(KeyEvent.KEYCODE_BACK, 0);
    }

    private void swipeDown() {
        Trime trime = Trime.getService();
        if (trime != null && !Rime.isComposing())
            trime.onKey(KeyEvent.KEYCODE_BACK, 0);
    }

    public static int getMaxCandidateCount() {
        return MAX_CANDIDATE_COUNT;
    }

    public void setCandidateListener(CandidateListener listener) {
        this.listener = listener;
    }

    /**
     * 刷新候選列表
     *
     * @param start 候選的起始編號
     */
    public void setText(int start) {
        start_num = start;
        removeHighlight();
        updateCandidateWidth();
        if (getCandNum() > 0) {
            invalidate();
        }
    }

    /**
     * 選取候選項
     *
     * @param index 候選項序號（從0開始），{@code -1}表示選擇當前高亮候選項
     * @return 是否成功選字
     */
    private boolean pickHighlighted(int index) {
        if ((highlightIndex != -1) && (listener != null)) {
            if (index == -1) index = highlightIndex;
            if (index >= 0) index += start_num;
            listener.onPickCandidate(index);
            return true;
        }
        return false;
    }

    private int lastHighlightIndex = -1;

    private boolean updateHighlight(int x, int y) {
        int index = getCandidateIndex(x, y);
        if (index != -1) {
            highlightIndex = index;
            if (lastHighlightIndex == highlightIndex)
                return true;
            lastHighlightIndex = highlightIndex;
            invalidate();
            if (index > -1) {
                listener.onSelectCandidate(getCandidate(index));
                Trime.getService().onPress(0);
            } else if (highlightIndex == PAGE_UP) {
                Trime.getService().speakPro("上页");
            } else if (highlightIndex == PAGE_DOWN) {
                Trime.getService().speakPro("下页");
            } else if (highlightIndex == HIDE) {
                if (Rime.isComposing())
                    Trime.getService().speakPro("更多候选");
                else
                    Trime.getService().speakPro("隐藏");
            }
            return true;
        }
        return false;
    }

    private void removeHighlight() {
        highlightIndex = -1;
        invalidate();
        requestLayout();
    }

    private boolean isHighlighted(int i) {
        return candidate_use_cursor && i >= 0 && i == highlightIndex;
    }

    private void drawHighlight(Canvas canvas) {
        if (isHighlighted(highlightIndex) && candidateRect[highlightIndex] != null) {
            candidateHighlight.setBounds(candidateRect[highlightIndex]);
            candidateHighlight.draw(canvas);
        }
    }

    private Typeface getFont(int codepoint, Typeface font) {
        if (tfHanB != Typeface.DEFAULT && Character.isSupplementaryCodePoint(codepoint))
            return tfHanB;
        if (tfLatin != Typeface.DEFAULT && codepoint < 0x2e80) return tfLatin;
        return font;
    }

    private void drawText(
            String s, Canvas canvas, Paint paint, Typeface font, float center, float y) {
        if (s == null) return;
        int length = s.length();
        if (length == 0) return;
        int points = s.codePointCount(0, length);
        float x = center - measureText(s, paint, font) / 2;
        if (tfLatin != Typeface.DEFAULT || (tfHanB != Typeface.DEFAULT && length > points)) {
            int offset = 0;
            while (offset < length) {
                int codepoint = s.codePointAt(offset);
                int charCount = Character.charCount(codepoint);
                int end = offset + charCount;
                paint.setTypeface(getFont(codepoint, font));
                canvas.drawText(s, offset, end, x, y, paint);
                x += paint.measureText(s, offset, end);
                offset = end;
            }
        } else {
            paint.setTypeface(font);
            canvas.drawText(s, x, y, paint);
        }
    }

    private boolean hasScrollLeft() {
        return getScrollX() > 0;
    }

    private boolean hasScrollRight() {
        return getScrollX() + width < getWidth();
    }

    private void drawCandidates(final Canvas canvas) {
        if (candidates == null || candidates.length == 0) return;
        canvas.save();
        if (mHideButtonWidth > 0) {
            int x = getScrollX() + width - mHideButtonWidth;
            canvas.clipRect(new Rect(0, 0, x, getHeight()));
        }
        float x = 0;
        float y = 0;
        int i = 0;
        float comment_x, comment_y;
        float comment_width;
        String candidate, comment;

        y = candidateRect[0].centerY() - (paintCandidate.ascent() + paintCandidate.descent()) / 2;
        if (show_comment && comment_on_top) y += comment_height / 2;
        comment_y = comment_height / 2 - (paintComment.ascent() + paintComment.descent()) / 2;
        if (show_comment && !comment_on_top) comment_y += candidateRect[0].bottom - comment_height;

        while (i < num_candidates) {
            // Calculate a position where the text could be centered in the rectangle.
            if (candidateRect[i] == null) {
                i++;
                continue;
            }
            x = candidateRect[i].centerX();
            if (show_comment && candidateDrawable[i] == null) {
                comment = getComment(i);
                if (!Function.isEmpty(comment)) {
                    comment_width = measureText(comment, paintComment, tfComment);
                    if (comment_on_top) {
                        comment_x = candidateRect[i].centerX();
                    } else {
                        x -= comment_width / 2;
                        comment_x = candidateRect[i].right - comment_width / 2;
                    }
                    paintComment.setColor(isHighlighted(i) ? hilited_comment_text_color : comment_text_color);
                    drawText(comment, canvas, paintComment, tfComment, comment_x, comment_y);
                }
            }
            paintCandidate.setColor(
                    isHighlighted(i) ? hilited_candidate_text_color : candidate_text_color);
            if (!Rime.isComposing()) {
                final Drawable bmp = candidateDrawable[i];
                //Log.i(TAG, "drawCandidates: " + getCandidate(i) + ";" + bmp + ";" + x + ";" + y);
                if (bmp != null) {
                    bmp.draw(canvas);
                } else {
                    drawText(getCandidate(i), canvas, paintCandidate, tfCandidate, x, y);
                }
            } else {
                drawText(getCandidate(i), canvas, paintCandidate, tfCandidate, x, y);
            }
            // Draw the separator at the right edge of each candidate.
            candidateSeparator.setBounds(
                    candidateRect[i].right - candidateSeparator.getIntrinsicWidth(),
                    candidateRect[i].top,
                    candidateRect[i].right + candidate_spacing,
                    candidateRect[i].bottom);
            candidateSeparator.draw(canvas);
            i++;
        }
        for (int j = PAGE_UP; j >= PAGE_DOWN; j--) { // -4: left, -5: right
            candidate = getCandidate(j);
            if (candidate == null) continue;
            if (candidateRect[i] == null) {
                i++;
                continue;
            }
            paintSymbol.setColor(isHighlighted(i) ? hilited_comment_text_color : comment_text_color);
            x = candidateRect[i].centerX() - measureText(candidate, paintSymbol, tfSymbol) / 2;
            final Drawable bmp = candidateDrawable[i];
            //Log.i(TAG, "drawCandidates: " + getCandidate(i) + ";" + bmp + ";" + x + ";" + y);
            if (bmp != null) {
                bmp.draw(canvas);
            } else {
                canvas.drawText(candidate, x, y, paintSymbol);
            }
            candidateSeparator.setBounds(
                    candidateRect[i].right - candidateSeparator.getIntrinsicWidth(),
                    candidateRect[i].top,
                    candidateRect[i].right + candidate_spacing,
                    candidateRect[i].bottom);
            candidateSeparator.draw(canvas);
            i++;
        }
        canvas.restore();
        if (!TextUtils.isEmpty(candidate_hide_button)) {
            x = getScrollX() + width - mHideButtonWidth / 2f - measureText(candidate_hide_button, paintCandidate, tfSymbol) / 2;
            hideRect = new Rect((int) (x - mHideButtonWidth / 6), 0, getScrollX() + width, getHeight());
            candidateSeparator.setBounds(
                    (int) (getScrollX() + width - mHideButtonWidth),
                    0,
                    (int) (getScrollX() + width - mHideButtonWidth + candidate_spacing),
                    getHeight());
            candidateSeparator.draw(canvas);
            if (mHideButtonDraw != null) {
                //Log.i(TAG, "drawCandidates: " + hideRect);
                //Log.i(TAG, "drawCandidates: " + mHideButtonDraw);
                mHideButtonDraw.setBounds(hideRect);
                mHideButtonDraw.draw(canvas);

            } else {
                canvas.drawText(candidate_hide_button, x, y, paintCandidate);
            }
        } else {
            hideRect = null;
        }
        /*if(hasScrollLeft()){
           drawText("◀", canvas, paintComment, tfComment, getScrollX(), comment_y);
        }
        if(hasScrollRight()){
            drawText("▶", canvas, paintComment, tfComment, getScrollX()+width-measureText("▶", paintComment, tfComment), comment_y);
        }*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        super.onDraw(canvas);

        drawHighlight(canvas);
        drawCandidates(canvas);
    }

    private void updateCandidateWidth() {
        final int top = 0;
        final int bottom = getHeight();
        width = Trime.getService().getWidth();
        int i = 0;
        int x = 0;
        int sx = getScrollX();
        int w = width;

        Arrays.fill(candidateDrawable, null);
        if (!TextUtils.isEmpty(candidate_hide_button)) {
            if (mHideButtonDraw != null) {
                mHideButtonWidth = (int) mHideButtonDraw.getWidth(getHeight());
            } else {
                mHideButtonWidth = (int) (measureText(candidate_hide_button, paintCandidate, tfCandidate) * 1.5f);
                mHideButtonWidth = Math.max(mHideButtonWidth, width / 10);
            }
        } else {
            mHideButtonWidth = 0;
        }
        if (hideRect != null)
            w -= hideRect.right - hideRect.left;
        if (Rime.hasLeft()) x += getCandidateWidth(PAGE_UP) + candidate_spacing;
        getCandNum();
        for (i = 0; i < num_candidates; i++) {
            if (!Rime.isComposing()) {
                LuaBitmapDrawable draw = getDrawableObject(getCandidate(i));
                //Log.i(TAG, "getWidth: "+getCandidate(i)+draw);
                if (draw != null) {
                    candidateDrawable[i] = draw;
                    final Rect rect = new Rect(x, top, x += draw.getWidth(bottom - top), bottom);
                    candidateRect[i] = rect;
                    draw.setBounds(rect);
                    draw.setCallback(new Drawable.Callback() {
                        @Override
                        public void invalidateDrawable(Drawable who) {
                            invalidate();
                        }

                        @Override
                        public void scheduleDrawable(Drawable who, Runnable what, long when) {

                        }

                        @Override
                        public void unscheduleDrawable(Drawable who, Runnable what) {

                        }
                    });
                    continue;
                }
            }
            candidateRect[i] = new Rect(x, top, x += getCandidateWidth(i), bottom);
            x += candidate_spacing;
        }
        if (Rime.hasLeft()) {
            if (leftDraw != null) {
                candidateDrawable[i] = leftDraw;
                final Rect rect = new Rect(0, top, leftDraw.getWidth(bottom - top), bottom);
                candidateRect[i++] = rect;
                leftDraw.setBounds(rect);
                leftDraw.setCallback(new Drawable.Callback() {
                    @Override
                    public void invalidateDrawable(Drawable who) {
                        invalidate();
                    }

                    @Override
                    public void scheduleDrawable(Drawable who, Runnable what, long when) {

                    }

                    @Override
                    public void unscheduleDrawable(Drawable who, Runnable what) {

                    }
                });
            } else {
                candidateRect[i++] = new Rect(0, top, (int) getCandidateWidth(PAGE_UP), bottom);
            }
        }
        if (Rime.hasRight()) {
            if (rightDraw != null) {
                candidateDrawable[i] = rightDraw;
                final Rect rect = new Rect(x, top, x += rightDraw.getWidth(bottom - top), bottom);
                candidateRect[i++] = rect;
                rightDraw.setBounds(rect);
                rightDraw.setCallback(new Drawable.Callback() {
                    @Override
                    public void invalidateDrawable(Drawable who) {
                        invalidate();
                    }

                    @Override
                    public void scheduleDrawable(Drawable who, Runnable what, long when) {

                    }

                    @Override
                    public void unscheduleDrawable(Drawable who, Runnable what) {

                    }
                });
            } else {
                candidateRect[i++] = new Rect(x, top, x += getCandidateWidth(PAGE_DOWN), bottom);
            }
        }
        x += mHideButtonWidth;
        setRight(Math.max(x, width));
        LayoutParams params = getLayoutParams();
        params.width = width;//Math.max(x, width);
        params.height = candidate_view_height;
        if (show_comment && comment_on_top) params.height += comment_height;
        setLayoutParams(params);
        if (highlightIndex >= 1) {
            if (candidateRect[highlightIndex].left < sx)
                smoothScrollBy(candidateRect[highlightIndex].left - sx, 0);
            if (candidateRect[highlightIndex].right > w + sx)
                smoothScrollBy(candidateRect[highlightIndex].right - (w + sx), 0);
        } else {
            scrollTo(0, 0);
        }

    }

    private LuaBitmapDrawable getDrawableObject(String candidate) {
        if(mDrawCache.containsKey(candidate))
            return mDrawCache.get(candidate);
        LuaBitmapDrawable draw = config.getDrawableObject(candidate);
        mDrawCache.put(candidate,draw);
        return draw;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateCandidateWidth();
    }

    /*@Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int[] r = new int[2];
        getLocationOnScreen(r);
        //Log.i(TAG, "onLayout: "+ Arrays.toString(r)+left+";"+top +";"+getWidth()+";"+ getHeight());
    }*/

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        inHover = true;
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_HOVER_ENTER: {
                event.setAction(MotionEvent.ACTION_DOWN);
            }
            break;
            case MotionEvent.ACTION_HOVER_MOVE: {
                event.setAction(MotionEvent.ACTION_MOVE);
            }
            break;
            case MotionEvent.ACTION_HOVER_EXIT: {
                if (event.getY() < 4 || event.getY() > getHeight() - 4)
                    event.setAction(MotionEvent.ACTION_CANCEL);
                else
                    event.setAction(MotionEvent.ACTION_UP);
            }
            break;
        }
        boolean ret = onTouchEvent(event);
        inHover = false;
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = getScrollX();
            mDownY = getScrollY();
            lastHighlightIndex = -1;
        }
        if (mGestureDetector.onTouchEvent(me))
            return true;
        if (!inHover && mCandidateScroll)
            return true;
        int action = me.getAction();
        int x = (int) me.getX() + getScrollX();
        int y = (int) me.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                updateHighlight(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (y > getHeight() * 0.8)
                    break;
                //Log.i(TAG, "onTouchEvent: " + me);
                if (updateHighlight(x, y)) {
                    //Log.i(TAG, "onTouchEvent: " + highlightIndex);
                    //performClick();
                    pickHighlighted(-1);
                }
                break;
        }
        return true;
    }

    /**
     * 獲得觸摸處候選項序號
     *
     * @param x 觸摸點橫座標
     * @param y 觸摸點縱座標
     * @return {@code >=0}: 觸摸點 (x, y) 處候選項序號，從0開始編號； {@code -1}: 觸摸點 (x, y) 處無候選項； {@code -4}: 觸摸點
     * (x, y) 處爲{@code Page_Up}； {@code -5}: 觸摸點 (x, y) 處爲{@code Page_Down}
     */
    private int getCandidateIndex(int x, int y) {
        Rect r = new Rect();
        if (hideRect != null) {
            r.set(hideRect);
            r.inset(0, CANDIDATE_TOUCH_OFFSET);
            if (r.contains(x, y)) {
                return HIDE;
            }
        }
        int j = 0;
        for (int i = 0; i < num_candidates; i++) {
            // Enlarge the rectangle to be more responsive to user clicks.
            r.set(candidateRect[j++]);
            r.inset(0, CANDIDATE_TOUCH_OFFSET);
            if (r.contains(x, y)) {
                // Returns -1 if there is no candidate in the hitting rectangle.
                return (i < num_candidates) ? i : -1;
            }
        }

        if (Rime.hasLeft()) { //Page Up
            r.set(candidateRect[j++]);
            r.inset(0, CANDIDATE_TOUCH_OFFSET);
            if (r.contains(x, y)) {
                return PAGE_UP;
            }
        }

        if (Rime.hasRight()) { //Page Down
            r.set(candidateRect[j++]);
            r.inset(0, CANDIDATE_TOUCH_OFFSET);
            if (r.contains(x, y)) {
                return PAGE_DOWN;
            }
        }

        return -1;
    }

    private int getCandNum() {
        candidates = Rime.getCandidates();
        if (candidates != null && candidates.length > 0 && !Rime.hasLeft()) {
            if (!Rime.isComposing()) {
                long speed = Trime.getService().getSpeed();
                if (speed > 0)
                    candidates[0].comment = speed + "/min";
            }
        }

        highlightIndex = Rime.getCandHighlightIndex() - start_num + Rime.getCandidatesFix();
        lastHighlightIndex = highlightIndex;
        if (highlightIndex > -1) {
            listener.onSelectCandidate(getCandidate(highlightIndex));
        }

        num_candidates = candidates == null ? 0 : candidates.length - start_num;
        return num_candidates;
    }

    public String getCandidate(int i) {
        if (candidates == null || candidates.length == 0)
            return Rime.get_input();
        String s = null;
        if (candidates != null && i >= 0 && candidates.length > 0)
            s = candidates[i + start_num].text;
        else if (i == PAGE_UP && Rime.hasLeft()) s = candidate_left_button;
        else if (i == PAGE_DOWN && Rime.hasRight()) s = candidate_right_button;
        return s;
    }

    private String getComment(int i) {
        String s = null;
        if (candidates != null && i >= 0) s = candidates[i + start_num].comment;
        return s;
    }

    private float measureText(String s, Paint paint, Typeface font) {
        float x = 0;
        if (s == null) return x;
        int length = s.length();
        if (length == 0) return x;
        int points = s.codePointCount(0, length);
        if (tfLatin != Typeface.DEFAULT || (tfHanB != Typeface.DEFAULT && length > points)) {
            int offset = 0;
            while (offset < length) {
                int codepoint = s.codePointAt(offset);
                int charCount = Character.charCount(codepoint);
                int end = offset + charCount;
                paint.setTypeface(getFont(codepoint, font));
                x += paint.measureText(s, offset, end);
                offset = end;
            }
            paint.setTypeface(font);
        } else {
            paint.setTypeface(font);
            x += paint.measureText(s);
        }
        return x;
    }

    private float getCandidateWidth(int i) {
        String s = getCandidate(i);
        float n = (s == null ? 0 : s.codePointCount(0, s.length()));
        float x = 2 * candidate_padding;
        if (s != null) x += measureText(s, paintCandidate, tfCandidate);
        if (i >= 0 && show_comment) {
            String comment = getComment(i);
            if (comment != null) {
                float x2 = measureText(comment, paintComment, tfComment);
                if (comment_on_top) {
                    if (x2 > x) x = x2 + candidate_padding;
                } //提示在上方
                else x += x2; //提示在右方
            }
        }

        return Math.max(x, width / 10f);
    }
}
