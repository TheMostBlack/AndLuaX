package com.osfans.trime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * Created by nirenr on 2019/1/5.
 */

public class CloudCandidate extends View {

    private final Scroller mScroller;
    private int width = 1080;
    private int mDownX;
    private int mDownY;

    public int getDownX() {
        return mDownX;
    }

    public int getDownY() {
        return mDownY;
    }

    public void setCandidates(ArrayList<String> clip, ArrayList<String> list) {
        int size = clip.size();
        candidates = new Rime.RimeCandidate[size];
        for (int i = 0; i < size; i++) {
            Rime.RimeCandidate cd = new Rime.RimeCandidate();
            cd.text = clip.get(i);
            cd.comment = list.get(i);
            candidates[i] = cd;
        }
    }

    /**
     * 處理候選條選字事件
     */
    public interface CandidateListener {
        void onPickCandidate(String text);

        void onSelectCandidate(String text);
    }

    private static final int MAX_CANDIDATE_COUNT = 30;
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
    private boolean show_comment = false, comment_on_top, candidate_use_cursor;

    private Rect candidateRect[] = new Rect[MAX_CANDIDATE_COUNT + 2];

    public void reset() {
        Config config = Config.get();
        candidateHighlight = config.getColorDrawable("hilited_candidate_back_color");
        if(candidateHighlight instanceof GradientDrawable)
            ((GradientDrawable) candidateHighlight).setCornerRadius(config.getFloat("layout/round_corner"));

        //candidateHighlight = new PaintDrawable(config.getColor("hilited_candidate_back_color"));
        //((PaintDrawable) candidateHighlight).setCornerRadius(config.getFloat("layout/round_corner"));
        candidateSeparator = new PaintDrawable(config.getColor("candidate_separator_color"));
        candidate_spacing = config.getPixel("candidate_spacing");
        candidate_padding = config.getPixel("candidate_padding");

        candidate_text_color = config.getColor("candidate_text_color");
        comment_text_color = config.getColor("comment_text_color");
        hilited_candidate_text_color = config.getColor("hilited_candidate_text_color");
        hilited_comment_text_color = config.getColor("hilited_comment_text_color");

        int candidate_text_size = config.getPixel("candidate_text_size");
        int comment_text_size = config.getPixel("comment_text_size");
        comment_height = config.getPixel("comment_height");
        candidate_view_height = config.getPixel("candidate_view_height");

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
        invalidate();
    }

    public void setShowComment(boolean value) {
        show_comment = value;
    }


    private final GestureDetector mGestureDetector;
    private final int mSwipeThreshold;
    private final boolean mDisambiguateSwipe;
    private boolean mShowing;

    public CloudCandidate(Context context) {
        this(context, null);
    }

    public CloudCandidate(Context context, AttributeSet attrs) {
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
        reset();

        setWillNotDraw(false);

        mSwipeThreshold = (int) (10 * getResources().getDisplayMetrics().density);
        mDisambiguateSwipe = true;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                int x = (int) e.getX() + getScrollX();
                int y = (int) e.getY();
                if (updateHighlight(x, y)) {
                    performClick();
                    pickHighlighted(-1);
                }
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                if (Trime.getService().isTouchExplorationEnabled())
                    return;
                int x = (int) e.getX() + getScrollX();
                int y = (int) e.getY();
                updateHighlight(x, y);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (Trime.getService().isTouchExplorationEnabled())
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
                int w=Math.min(getWidth(),getHeight());
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
        mGestureDetector.setIsLongpressEnabled(false);
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
            pickHighlighted(-4);
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
            pickHighlighted(-5);
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

    public void setCandidateListener(CloudCandidate.CandidateListener listener) {
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
            if(getComment(highlightIndex)!=null)
                listener.onPickCandidate(getComment(highlightIndex));
            else
                listener.onPickCandidate(getCandidate(highlightIndex));
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
            listener.onSelectCandidate(getCandidate(highlightIndex));
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
        if (isHighlighted(highlightIndex)) {
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

    private void drawCandidates(Canvas canvas) {
        if (candidates == null) return;

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
            x = candidateRect[i].centerX();
            if (show_comment) {
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
            drawText(getCandidate(i), canvas, paintCandidate, tfCandidate, x, y);
            // Draw the separator at the right edge of each candidate.
            candidateSeparator.setBounds(
                    candidateRect[i].right - candidateSeparator.getIntrinsicWidth(),
                    candidateRect[i].top,
                    candidateRect[i].right + candidate_spacing,
                    candidateRect[i].bottom);
            candidateSeparator.draw(canvas);
            i++;
        }
        for (int j = -4; j >= -5; j--) { // -4: left, -5: right
            candidate = getCandidate(j);
            if (candidate == null) continue;
            paintSymbol.setColor(isHighlighted(i) ? hilited_comment_text_color : comment_text_color);
            x = candidateRect[i].centerX() - measureText(candidate, paintSymbol, tfSymbol) / 2;
            canvas.drawText(candidate, x, y, paintSymbol);
            candidateSeparator.setBounds(
                    candidateRect[i].right - candidateSeparator.getIntrinsicWidth(),
                    candidateRect[i].top,
                    candidateRect[i].right + candidate_spacing,
                    candidateRect[i].bottom);
            candidateSeparator.draw(canvas);
            i++;
        }
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
        width = getResources().getDisplayMetrics().widthPixels;
        int i = 0;
        int x = 0;
        int sx = getScrollX();
        int w = width;
        getCandNum();
        for (i = 0; i < num_candidates; i++) {
            candidateRect[i] = new Rect(x, top, x += getCandidateWidth(i), bottom);
            x += candidate_spacing;
        }
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = Math.max(x, width);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateCandidateWidth();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = getScrollX();
            mDownY = getScrollY();
        }

        if (mGestureDetector.onTouchEvent(me))
            return true;
        if (!Trime.getService().isTouchExplorationEnabled())
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
                if (updateHighlight(x, y)) {
                    performClick();
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

        return -1;
    }

    public void setCandidates(ArrayList<String> list) {
        int size = list.size();
        candidates = new Rime.RimeCandidate[size];
        for (int i = 0; i < size; i++) {
            Rime.RimeCandidate cd = new Rime.RimeCandidate();
            cd.text = list.get(i);
            //cd.comment = "";
            candidates[i] = cd;
        }
    }

    private int getCandNum() {
        //candidates = candidates;
        highlightIndex = -1;
        lastHighlightIndex = highlightIndex;
        if (highlightIndex > -1)
            listener.onSelectCandidate(getCandidate(highlightIndex));
        num_candidates = candidates == null ? 0 : candidates.length - start_num;
        return num_candidates;
    }

    private String getCandidate(int i) {
        String s = null;
        if (candidates != null && i >= 0) s = candidates[i + start_num].text;
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
                    if (x2 > x) x = x2;
                } //提示在上方
                else x += x2; //提示在右方
            }
        }
        return x;
    }

}
