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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.osfans.trime.Candidate.PAGE_DOWN;
import static com.osfans.trime.Candidate.PAGE_UP;

/**
 * 編碼區，顯示已輸入的按鍵編碼，可使用方向鍵或觸屏移動光標位置
 */
public class Composition extends TextView {
    private int key_text_size, text_size, label_text_size, candidate_text_size, comment_text_size;
    private int key_text_color, text_color, label_color, candidate_text_color, comment_text_color;
    private int hilited_text_color, hilited_candidate_text_color, hilited_comment_text_color;
    private int back_color, hilited_back_color, hilited_candidate_back_color;
    private Integer key_back_color;
    private Typeface tfText, tfLabel, tfCandidate, tfComment;
    private int composition_pos[] = new int[2];
    private int max_length, sticky_lines;
    private int max_entries = Candidate.getMaxCandidateCount();
    private int cloud_max_entries = 0;
    private boolean candidate_use_cursor, show_comment;
    private int highlightIndex;
    private List<Map<String, Object>> components;
    private SpannableStringBuilder ss;
    private int span = 0;
    private String movable;
    private int move_pos[] = new int[2];
    private boolean first_move = true;
    private float mDx, mDy;
    private int mCurrentX, mCurrentY;
    private int candidate_num;
    private boolean all_phrases;
    private int cloud_num = 1;
    private int cloud_line_length;
    private String cloudSep = " ";
    private int min_length;
    private int mAlpha;
    private Integer hilited_label_color;
    private boolean mSingle;
    private boolean end_top;

    public Composition(Context context, AttributeSet attrs) {
        super(context, attrs);
        reset();
    }


    private final String TAG = "rime";


    public void addCloud(String cloud) {
        if (cloud_num > cloud_max_entries && cloud_max_entries != 0)
            return;
        if (cloud_max_entries < 5 && cloud.length() == 1)
            return;
        int start = ss.length();
        if (cloud_num == 1) {
            if (max_entries < 1 || min_length < 1)
                ss.append("\n").append(String.valueOf(cloud_num)).append(".");
            else
                ss.append("\n☁").append(String.valueOf(cloud_num)).append(".");
        } else if (max_length > 1 && cloud_line_length + cloud.length() > max_length) {
            ss.append("\n").append(String.valueOf(cloud_num)).append(".");
            cloud_line_length = 0;
        } else {
            ss.append(cloudSep).append(String.valueOf(cloud_num)).append(".");
        }
        cloud_line_length += cloud.length();
        int end = ss.length();
        ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, span);
        ss.setSpan(new AbsoluteSizeSpan(candidate_text_size), start, end, span);
        start = ss.length();
        ss.append(cloud);
        end = ss.length();
        ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, span);
        ss.setSpan(
                new CloudSpan(
                        cloud,
                        tfLabel,
                        hilited_candidate_text_color,
                        hilited_candidate_back_color,
                        label_color),
                start,
                end,
                span);
        ss.setSpan(new AbsoluteSizeSpan(candidate_text_size), start, end, span);
    }

    public void setCloud(String cloud) {
        if (cloud_max_entries == 0) {
            return;
        }
        cloud_num = 1;
        cloud_line_length = 0;
        if (ss == null)
            ss = new SpannableStringBuilder();
        if (ss.toString().contains(cloud))
            return;
        addCloud(cloud);
        cloud_num++;
        if (ss.length() > max_length) {
            setSingleLine(false); //設置單行
        }/*else {
            measure(0,0);
            if(getMeasuredWidth()/2>getMaxWidth())
                setSingleLine(false);
        }*/
        setText(ss);
    }

    public void setCloud(ArrayList<String> cloud) {
        if (cloud_max_entries == 0) {
            Trime.getService().showCloud(cloud);
            return;
        }
        if (cloud_max_entries == 1) {
            return;
        }
        String text = ss.toString();
        for (String s : cloud) {
            if (text.contains(s))
                continue;
            addCloud(s);
            cloud_num++;
            if (cloud_max_entries > 0 && cloud_num > 5)
                break;
        }
        if (ss.length() > max_length)
            setSingleLine(false); //設置單行
        setText(ss);
    }

    private void addComposition(String cloud) {
        int start = ss.length();
        if (cloud_num == 1) {
            ss.append("\n").append(String.valueOf(cloud_num)).append(".");
        } else if (max_length > 1 && cloud_line_length + cloud.length() > max_length) {
            ss.append("\n").append(String.valueOf(cloud_num)).append(".");
            cloud_line_length = 0;
        } else {
            ss.append(cloudSep).append(String.valueOf(cloud_num)).append(".");
        }
        cloud_line_length += cloud.length();
        int end = ss.length();
        ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, span);
        ss.setSpan(new AbsoluteSizeSpan(candidate_text_size), start, end, span);
        start = ss.length();
        ss.append(cloud);
        end = ss.length();
        ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, span);
        ss.setSpan(
                new CloudSpan2(
                        cloud,
                        tfLabel,
                        hilited_candidate_text_color,
                        hilited_candidate_back_color,
                        label_color),
                start,
                end,
                span);
        ss.setSpan(new AbsoluteSizeSpan(candidate_text_size), start, end, span);
    }

    public void addCompositions(ArrayList<String> list) {
        if (Function.isEmpty(Rime.getCompositionText()) || ss == null) {
            ss = new SpannableStringBuilder();
            for (Map<String, Object> m : components) {
                if (m.containsKey("move")) appendMove(m);
            }
        }

        cloud_num = 1;
        for (String s : list) {
            addComposition(s);
            cloud_num++;
        }
        if (ss.length() > max_length)
            setSingleLine(false); //設置單行
        setText(ss);
    }

    public void setCompositionSingleLine(boolean single) {
        mSingle = single;
    }

    public void setCompositionEndTop(boolean b) {
        end_top = b;
    }

    private class CloudSpan2 extends ClickableSpan {
        String index;
        Typeface tf;
        int hi_text, hi_back, text;

        public CloudSpan2(String i, Typeface _tf, int _hi_text, int _hi_back, int _text) {
            super();
            index = i;
            tf = _tf;
            hi_text = _hi_text;
            hi_back = _hi_back;
            text = _text;
        }

        @Override
        public void onClick(View tv) {
            Trime.getService().commitTextAndClearComposition(index);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setTypeface(tf);
            ds.setColor(text);
        }
    }

    private class CloudSpan extends ClickableSpan {
        String index;
        Typeface tf;
        int hi_text, hi_back, text;

        public CloudSpan(String i, Typeface _tf, int _hi_text, int _hi_back, int _text) {
            super();
            index = i;
            tf = _tf;
            hi_text = _hi_text;
            hi_back = _hi_back;
            text = _text;
        }

        @Override
        public void onClick(View tv) {
            Trime.getService().commitTextAndClearComposition(index);
            Trime.getService().addCloud(index);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setTypeface(tf);
            ds.setColor(text);
        }
    }

    private class CompositionSpan extends UnderlineSpan {
        public CompositionSpan() {
            super();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setTypeface(tfText);
            ds.setColor(text_color);
            ds.bgColor = back_color;
        }
    }

    private class CandidateSpan extends ClickableSpan {
        int index;
        Typeface tf;
        int hi_text, hi_back, text;

        public CandidateSpan(int i, Typeface _tf, int _hi_text, int _hi_back, int _text) {
            super();
            index = i;
            tf = _tf;
            hi_text = _hi_text;
            hi_back = _hi_back;
            text = _text;
        }

        @Override
        public void onClick(View tv) {
            Trime.getService().onPickCandidate(index);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setTypeface(tf);
            if (index == highlightIndex) {
                ds.setColor(hi_text);
                ds.bgColor = hi_back;
            } else {
                ds.setColor(text);
            }
        }
    }

    private class EventSpan extends ClickableSpan {
        Event event;

        public EventSpan(Event e) {
            super();
            event = e;
        }

        @Override
        public void onClick(View tv) {
            Trime.getService().onPress(event.getCode());
            Trime.getService().onEvent(event);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(key_text_color);
            if (key_back_color != null) ds.bgColor = key_back_color;
        }
    }

    @TargetApi(21)
    public class LetterSpacingSpan extends UnderlineSpan {
        private float letterSpacing;

        /**
         * @param letterSpacing 字符間距
         */
        public LetterSpacingSpan(float letterSpacing) {
            this.letterSpacing = letterSpacing;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setLetterSpacing(letterSpacing);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int n = getOffsetForPosition(event.getX(), event.getY());
            if (composition_pos[0] <= n && n <= composition_pos[1]) {
                String s =
                        getText().toString().substring(n, composition_pos[1]).replace(" ", "").replace("‸", "");
                n = Rime.RimeGetInput().length() - s.length(); //從右側定位
                Rime.RimeSetCaretPos(n);
                Trime.getService().updateComposing();
                return true;
            }
        } else if (!movable.contentEquals("false")
                && (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN)) {
            int n = getOffsetForPosition(event.getX(), event.getY());
            if (move_pos[0] <= n && n <= move_pos[1]) {
                if (action == MotionEvent.ACTION_DOWN) {
                    if (first_move || movable.contentEquals("once")) {
                        first_move = false;
                        int location[] = Trime.getLocationOnScreen(this);
                        mCurrentX = location[0];
                        mCurrentY = location[1];
                    }
                    mDx = mCurrentX - event.getRawX();
                    mDy = mCurrentY - event.getRawY();
                } else { //MotionEvent.ACTION_MOVE
                    mCurrentX = (int) (event.getRawX() + mDx);
                    mCurrentY = (int) (event.getRawY() + mDy);
                    Trime.getService().updateWindow(mCurrentX, mCurrentY);
                }
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    public void setShowComment(boolean value) {
        show_comment = value;
    }

    public void reset() {

        Config config = Config.get();
        mAlpha = config.getKeyAlpha();
        components = (List<Map<String, Object>>) config.getValue("window");
        max_entries = config.getMaxEntries();
        cloud_max_entries = config.getCloudMaxEntries();
        min_length = config.getMinLength();
        candidate_use_cursor = config.getBoolean("candidate_use_cursor");
        text_size = config.getPixel("text_size");
        candidate_text_size = config.getPixel("candidate_text_size");
        comment_text_size = config.getPixel("comment_text_size");
        label_text_size = config.getPixel("label_text_size");

        text_color = config.getColor("text_color");
        candidate_text_color = config.getColor("candidate_text_color");
        comment_text_color = config.getColor("comment_text_color");
        hilited_text_color = config.getColor("hilited_text_color");
        hilited_candidate_text_color = config.getColor("hilited_candidate_text_color");
        hilited_comment_text_color = config.getColor("hilited_comment_text_color");
        label_color = config.getColor("label_color");
        hilited_label_color = config.getColor("hilited_label_color");
        if (hilited_label_color == null)
            hilited_label_color = hilited_candidate_text_color;

        hilited_back_color = config.getColor("hilited_back_color");
        hilited_candidate_back_color = getColor(config, "hilited_candidate_back_color");
        back_color = getColor(config, "back_color");

        key_text_size = config.getPixel("key_text_size");
        key_text_color = config.getColor("key_text_color");
        key_back_color = config.getColor("key_back_color");

        float line_spacing_multiplier = config.getFloat("layout/line_spacing_multiplier");
        if (line_spacing_multiplier == 0f) line_spacing_multiplier = 1f;
        setLineSpacing(config.getFloat("layout/line_spacing"), line_spacing_multiplier);
        setMinWidth(config.getPixel("layout/min_width"));
        setMinHeight(config.getPixel("layout/min_height"));
        setMaxWidth(Math.min(config.getPixel("layout/max_width"), Trime.getService().getMaxWidth()));
        setMaxHeight(config.getPixel("layout/max_height"));
        setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * config.getCompositionWidth()));
        int margin_x, margin_y;
        margin_x = config.getPixel("layout/margin_x");
        margin_y = config.getPixel("layout/margin_y");
        setPadding(margin_x, margin_y, margin_x, margin_y);
        max_length = config.getLineMaxLength();
        sticky_lines = config.getInt("layout/sticky_lines");
        //movable = config.getString("layout/movable");
        movable = config.getMovable();
        all_phrases = config.getAllPhrases();
        tfLabel = config.getFont("label_font");
        tfText = config.getFont("text_font");
        tfCandidate = config.getFont("candidate_font");
        tfComment = config.getFont("comment_font");
        setText("");
    }

    private int getColor(Config config, String s) {
        Integer clr = config.getColor(s);
        if (clr == null) {
            Drawable cd = config.getDrawable(s);
            if (cd instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) cd).getBitmap();
                clr = bmp.getPixel(bmp.getWidth() / 2, bmp.getHeight() / 2);
            } else {
                clr = 0;
            }
        }
        return clr;
    }

    private Object getAlign(Map m) {
        Layout.Alignment i = Layout.Alignment.ALIGN_NORMAL;
        if (m.containsKey("align")) {
            String align = Config.getString(m, "align");
            switch (align) {
                case "left":
                case "normal":
                    i = Layout.Alignment.ALIGN_NORMAL;
                    break;
                case "right":
                case "opposite":
                    i = Layout.Alignment.ALIGN_OPPOSITE;
                    break;
                case "center":
                    i = Layout.Alignment.ALIGN_CENTER;
                    break;
            }
        }
        return new AlignmentSpan.Standard(i);
    }

    private void appendComposition(Map m) {
        Rime.RimeComposition r = Rime.getComposition();
        String s = r.getText();
        int start, end;
        String sep = Config.getString(m, "start");
        if (!Function.isEmpty(sep)) {
            start = ss.length();
            ss.append(sep);
            end = ss.length();
            ss.setSpan(getAlign(m), start, end, span);
        }
        start = ss.length();
        ss.append(s);
        end = ss.length();
        ss.setSpan(getAlign(m), start, end, span);
        composition_pos[0] = start;
        composition_pos[1] = end;
        ss.setSpan(new CompositionSpan(), start, end, span);
        ss.setSpan(new AbsoluteSizeSpan(text_size), start, end, span);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && m.containsKey("letter_spacing")) {
            Float size = Config.getFloat(m, "letter_spacing");
            if (size != null && size != 0f)
                ss.setSpan(new LetterSpacingSpan(size), start, end, span);
        }
        start = composition_pos[0] + r.getStart();
        end = composition_pos[0] + r.getEnd();
        ss.setSpan(new ForegroundColorSpan(hilited_text_color), start, end, span);
        ss.setSpan(new BackgroundColorSpan(hilited_back_color), start, end, span);
        sep = Config.getString(m, "end");
        if (!Function.isEmpty(sep)) ss.append(sep);
    }

    private int appendCandidates(Map m, int length) {
        int start, end;
        int start_num = 0;
        Rime.RimeCandidate[] candidates = Rime.getCandidates();
        if (candidates == null || candidates.length == 0) return start_num;
        ArrayList tmp = new ArrayList();
        String sep = Config.getString(m, "start");
        highlightIndex = candidate_use_cursor ? Rime.getCandHighlightIndex() : -1;
        String label_format = Config.getString(m, "label");
        String candidate_format = Config.getString(m, "candidate");
        String comment_format = Config.getString(m, "comment");
        String line = Config.getString(m, "sep");
        cloudSep = line;
        if (length < 1)
            return 0;
        int last_cand_length = 0;
        int line_length = 0;
        String[] labels = Rime.getSelectLabels();
        if (!end_top) {
            int i = -1;
            candidate_num = 0;
            int n = -1;
            for (Rime.RimeCandidate o : candidates) {
                n++;
                String cand = o.text;
                if (Function.isEmpty(cand)) cand = "";
                i++;
                if (candidate_num >= max_entries && max_entries > -1) {
                    if (start_num == 0 && candidate_num == i)
                        start_num = candidate_num;
                    break;
                }
                if (cand.length() < length) {
                    if (start_num == 0 && candidate_num == i)
                        start_num = candidate_num;
                    if (all_phrases)
                        continue;
                    else
                        break;
                }
                cand = String.format(candidate_format, cand);
                String line_sep;
                if (candidate_num == 0) {
                    line_sep = sep;
                } /*else if (n % 5 == 0) {
                    line_sep = "\n";
                    line_length = 0;
                }*/ else if ((sticky_lines > 0 && sticky_lines >= i)
                        || (max_length > 0 && line_length + cand.length() > max_length)) {
                    line_sep = "\n";
                    line_length = 0;
                } else {
                    line_sep = line;
                }
                if (!Function.isEmpty(line_sep)) {
                    start = ss.length();
                    ss.append(line_sep);
                    end = ss.length();
                    ss.setSpan(getAlign(m), start, end, span);
                }
                if (!Function.isEmpty(label_format) && labels != null) {
                    String label = String.format(label_format, labels[i]);
                    start = ss.length();
                    ss.append(label);
                    end = ss.length();
                    ss.setSpan(
                            new CandidateSpan(
                                    i,
                                    tfLabel,
                                    hilited_label_color,
                                    hilited_candidate_back_color,
                                    label_color),
                            start,
                            end,
                            span);
                    ss.setSpan(new AbsoluteSizeSpan(label_text_size), start, end, span);
                }
                start = ss.length();
                ss.append(cand);
                end = ss.length();
                line_length += cand.length();
                ss.setSpan(getAlign(m), start, end, span);
                ss.setSpan(
                        new CandidateSpan(
                                i,
                                tfCandidate,
                                hilited_candidate_text_color,
                                hilited_candidate_back_color,
                                candidate_text_color),
                        start,
                        end,
                        span);
                ss.setSpan(new AbsoluteSizeSpan(candidate_text_size), start, end, span);
                String comment = o.comment;
                if (show_comment && !Function.isEmpty(comment_format) && !Function.isEmpty(comment)) {
                    comment = String.format(comment_format, comment);
                    start = ss.length();
                    ss.append(comment);
                    end = ss.length();
                    ss.setSpan(getAlign(m), start, end, span);
                    ss.setSpan(
                            new CandidateSpan(
                                    i,
                                    tfComment,
                                    hilited_comment_text_color,
                                    hilited_candidate_back_color,
                                    comment_text_color),
                            start,
                            end,
                            span);
                    ss.setSpan(new AbsoluteSizeSpan(comment_text_size), start, end, span);
                    //line_length += comment.length();
                }
                candidate_num++;
            }

            if (start_num == 0 && candidate_num == i + 1) start_num = candidate_num;
        } else {
            candidate_num = 0;
            int n = -1;
            int i = -1;
            int max = max_entries;
            if (max == -1)
                max = candidates.length;
            if (max > candidates.length)
                max = candidates.length;
            for (i = max - 1; i >= 0; i--) {
                Rime.RimeCandidate o = candidates[i];
                n++;
                String cand = o.text;
                if (Function.isEmpty(cand)) cand = "";
                /*if (candidate_num >= max_entries && max_entries > -1) {
                    if (start_num == 0 && candidate_num == i)
                        start_num = candidate_num;
                    break;
                }
                if (cand.length() < length) {
                    if (start_num == 0 && candidate_num == i)
                        start_num = candidate_num;
                    continue;
                }*/
                cand = String.format(candidate_format, cand);
                String line_sep;
                if (candidate_num == 0) {
                    line_sep = sep;
                } else {
                    line_sep = "\n";
                    line_length = 0;
                }
                if (!Function.isEmpty(line_sep)) {
                    start = ss.length();
                    ss.append(line_sep);
                    end = ss.length();
                    ss.setSpan(getAlign(m), start, end, span);
                }
                if (!Function.isEmpty(label_format) && labels != null) {
                    String label = String.format(label_format, labels[i]);
                    start = ss.length();
                    ss.append(label);
                    end = ss.length();
                    ss.setSpan(
                            new CandidateSpan(
                                    i,
                                    tfLabel,
                                    hilited_label_color,
                                    hilited_candidate_back_color,
                                    label_color),
                            start,
                            end,
                            span);
                    ss.setSpan(new AbsoluteSizeSpan(label_text_size), start, end, span);
                }
                start = ss.length();
                ss.append(cand);
                end = ss.length();
                line_length += cand.length();
                ss.setSpan(getAlign(m), start, end, span);
                ss.setSpan(
                        new CandidateSpan(
                                i,
                                tfCandidate,
                                hilited_candidate_text_color,
                                hilited_candidate_back_color,
                                candidate_text_color),
                        start,
                        end,
                        span);
                ss.setSpan(new AbsoluteSizeSpan(candidate_text_size), start, end, span);
                String comment = o.comment;
                if (show_comment && !Function.isEmpty(comment_format) && !Function.isEmpty(comment)) {
                    comment = String.format(comment_format, comment);
                    start = ss.length();
                    ss.append(comment);
                    end = ss.length();
                    ss.setSpan(getAlign(m), start, end, span);
                    ss.setSpan(
                            new CandidateSpan(
                                    i,
                                    tfComment,
                                    hilited_comment_text_color,
                                    hilited_candidate_back_color,
                                    comment_text_color),
                            start,
                            end,
                            span);
                    ss.setSpan(new AbsoluteSizeSpan(comment_text_size), start, end, span);
                    //line_length += comment.length();
                }
                candidate_num++;
            }

            start_num = max + 1;
        }

        sep = Config.getString(m, "end");
        if (!Function.isEmpty(sep)) ss.append(sep);
        return start_num;
    }

    private void appendButton(Map m) {
        if (m.containsKey("when")) {
            String when = Config.getString(m, "when");
            if (when.contentEquals("paging") && !Rime.isPaging()) return;
            if (when.contentEquals("has_menu") && !Rime.hasMenu()) return;
        }
        String label;
        Event e = new Event(Config.getString(m, "click"));
        if (m.containsKey("label")) label = Config.getString(m, "label");
        else label = e.getLabel();
        int start, end;
        String sep = null;
        if (m.containsKey("start")) sep = Config.getString(m, "start");
        if (!Function.isEmpty(sep)) {
            start = ss.length();
            ss.append(sep);
            end = ss.length();
            ss.setSpan(getAlign(m), start, end, span);
        }
        start = ss.length();
        ss.append(label);
        end = ss.length();
        ss.setSpan(getAlign(m), start, end, span);
        ss.setSpan(new EventSpan(e), start, end, span);
        ss.setSpan(new AbsoluteSizeSpan(key_text_size), start, end, span);
        sep = Config.getString(m, "end");
        if (!Function.isEmpty(sep)) ss.append(sep);
    }

    private void appendMove(Map m) {
        String s = Config.getString(m, "move");
        int start, end;
        String sep = Config.getString(m, "start");
        if (!Function.isEmpty(sep)) {
            start = ss.length();
            ss.append(sep);
            end = ss.length();
            ss.setSpan(getAlign(m), start, end, span);
        }
        start = ss.length();
        ss.append(s);
        end = ss.length();
        ss.setSpan(getAlign(m), start, end, span);
        move_pos[0] = start;
        move_pos[1] = end;
        ss.setSpan(new AbsoluteSizeSpan(key_text_size), start, end, span);
        ss.setSpan(new ForegroundColorSpan(key_text_color), start, end, span);
        sep = Config.getString(m, "end");
        if (!Function.isEmpty(sep)) ss.append(sep);
    }

    public int setWindow(int length) {
        if (getVisibility() != View.VISIBLE) return 0;
        Rime.RimeComposition r = Rime.getComposition();
        if (r == null) return 0;
        String s = r.getText();
        if (Function.isEmpty(s)) return 0;
        setSingleLine(true); //設置單行
        ss = new SpannableStringBuilder();
        int start_num = 0;
        for (Map<String, Object> m : components) {
            if (m.containsKey("composition")) appendComposition(m);
            else if (m.containsKey("candidate")) start_num = appendCandidates(m, length);
            else if (m.containsKey("click")) appendButton(m);
            else if (m.containsKey("move")) appendMove(m);
        }

        setText(ss);
        if (mSingle) {
            setSingleLine();
            scrollTo(0, 0);
            setMaxWidth(Trime.getService().getWidth() * 10);
        } else {
            if (ss.length() > 8) {
                measure(0, 0);
                //Log.i(TAG, "setWindow: "+getMeasuredWidth());
                if (getMeasuredWidth() > getMaxWidth())
                    setSingleLine(false);
            }
            if (start_num > 0)
                setSingleLine(false);
        }
        setMovementMethod(LinkMovementMethod.getInstance());
        return start_num;
    }


}
