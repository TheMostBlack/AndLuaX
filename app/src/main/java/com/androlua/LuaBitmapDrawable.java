package com.androlua;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import com.androlua.util.AsyncTaskX;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nirenr on 2018/09/05 0005.
 */

public class LuaBitmapDrawable extends Drawable implements Runnable, LuaGcable {

    private LuaContext mLuaContext;
    private int mDuration;
    private long mMovieStart;
    private int mCurrentAnimationTime;
    private Movie mMovie;
    private LoadingDrawable mLoadingDrawable;
    private Drawable mBitmapDrawable;
    private NineBitmapDrawable mNineBitmapDrawable;
    private ColorFilter mColorFilter;
    private int mFillColor;
    private int mScaleType = FIT_XY;
    private GifDecoder mGifDecoder;
    private GifDecoder mGifDecoder2;
    private Handler mHandler;
    private GifDecoder.GifFrame mGifFrame;
    private int mDelay;
    private boolean mGc;
    private int mAlpha = 255;
    private boolean mHasInvalidate;

    public static void setCacheTime(long time) {
        mCacheTime = time;
    }

    public static long getCacheTime() {
        return mCacheTime;
    }

    private static long mCacheTime = 7 * 24 * 60 * 60 * 1000;

    public LuaBitmapDrawable(LuaContext context, String path, Drawable def) {
        this(context, path);
        mBitmapDrawable = def;
    }

    public LuaBitmapDrawable(LuaContext context, String path) {
        context.regGc(this);
        mLuaContext = context;
        mLoadingDrawable = new LoadingDrawable(context.getContext());
        if (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")) {
            initHttp(context, path);
        } else {
            if (!path.startsWith("/")) {
                path = context.getLuaPath(path);
            }
            init(path);
        }
        //Log.i("rime", "backget:init gif " + mGifDecoder2 + " bmp " + mBitmapDrawable + " 9p " + mNineBitmapDrawable + path);
    }

    private void initHttp(final LuaContext context, final String path) {
        new AsyncTaskX<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    return getHttpBitmap(context, path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                init(s);
            }
        }.execute();
    }

    private void init(final String path) {
        //Log.i("rime", "backget:init1 gif " + mGifDecoder2 + " bmp " + mBitmapDrawable + " 9p " + mNineBitmapDrawable + path);
        if (path.endsWith("png") || path.endsWith("jpg")) {
            init2(path);
            return;
        }
        try {
            mGifDecoder = new GifDecoder(new FileInputStream(path), new GifDecoder.GifAction() {
                @Override
                public void parseOk(boolean parseStatus, int frameIndex) {
                    if (!parseStatus && frameIndex < 0) {
                        init2(path);
                    } else if (parseStatus && mGifDecoder2 == null && mGifDecoder.getFrameCount() > 1) {     //当帧数大于1时，启动动画线程
                        mGifDecoder2 = mGifDecoder;
                    }

                }
            });
            mGifDecoder.start();
        } catch (Exception e) {
            e.printStackTrace();
            init2(path);
        }
        //Log.i("rime", "backget:init11 gif " + mGifDecoder2+" bmp " + mBitmapDrawable+" 9p " + mNineBitmapDrawable+path);

    }


    private void init2(String path) {
        //Log.i("rime", "backget:init2 gif " + mGifDecoder2+" bmp " + mBitmapDrawable+" 9p " + mNineBitmapDrawable+path);
        if (path.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLoadingDrawable.setState(-1);
                }
            }, 1000);
            invalidateSelf();
            return;
        }


        try {
            mNineBitmapDrawable = new NineBitmapDrawable(path);
        } catch (Exception e) {
            if (path.endsWith(".9.png")) {
                try {
                    Bitmap bmp = LuaBitmap.getLocalBitmap(path);
                    int w = bmp.getWidth();
                    int h = bmp.getHeight();
                    mNineBitmapDrawable = new NineBitmapDrawable(bmp, w / 4, h / 4, w / 4 * 3, h / 4 * 3);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    mBitmapDrawable = new BitmapDrawable(LuaBitmap.getLocalBitmap(mLuaContext, path));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        }
        if (mBitmapDrawable == null && mNineBitmapDrawable == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLoadingDrawable.setState(-1);
                }
            }, 1000);
        }
        invalidateSelf();
        //Log.i("rime", "backget:init22 gif " + mGifDecoder2+" bmp " + mBitmapDrawable+" 9p " + mNineBitmapDrawable+path);

    }

    public int getWidth() {
        if (mMovie != null) {
            return mMovie.width();
        } else if (mBitmapDrawable != null) {
            return mBitmapDrawable.getIntrinsicWidth();
        } else if (mNineBitmapDrawable != null) {
            return mNineBitmapDrawable.getIntrinsicWidth();
        } else if (mGifDecoder != null) {
            return mGifDecoder.width;
        }
        return super.getIntrinsicWidth();
    }

    public int getHeight() {
        if (mMovie != null) {
            return mMovie.height();
        } else if (mBitmapDrawable != null) {
            return mBitmapDrawable.getIntrinsicHeight();
        } else if (mNineBitmapDrawable != null) {
            return mNineBitmapDrawable.getIntrinsicHeight();
        } else if (mGifDecoder != null) {
            return mGifDecoder.height;
        }
        return super.getIntrinsicHeight();
    }

    public int getWidth(int i) {
        int w = getWidth();
        if (w <= 0)
            return i;
        int h = getHeight();
        //Log.i("rime", "getWidth: " + w + ";" + h + ";" + i + ";" + ";" + (i / h * w));
        return i / h * w;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(mFillColor);
        //Log.i("rime", "backget: gif " + mGifDecoder2 + " bmp " + mBitmapDrawable + " 9p " + mNineBitmapDrawable);
        if (mGifDecoder2 != null) {
            long now = System.currentTimeMillis();
            if (mMovieStart == 0 || mGifFrame == null) {
                mGifFrame = mGifDecoder2.next();
                mDelay = mGifFrame.delay;
                mMovieStart = now;
            } else {
                while (now - mMovieStart > mDelay) {
                    mGifFrame = mGifDecoder2.next();
                    mDelay = mGifFrame.delay;
                    mMovieStart += mDelay;
                }
            }
            if (mGifFrame != null) {
                Rect bound = getBounds();
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(mGifFrame.image);
                int width = mBitmapDrawable.getIntrinsicWidth();
                int height = mBitmapDrawable.getIntrinsicHeight();
                float mScale = 1;
                if (mScaleType == FIT_XY) {
                    float mScaleX = (float) (bound.right - bound.left) / (float) width;
                    float mScaleY = (float) (bound.bottom - bound.top) / (float) height;
                    width = (int) (width * mScaleX);
                    height = (int) (height * mScaleY);
                } else if (mScaleType != MATRIX) {
                    mScale = Math.min((float) (bound.bottom - bound.top) / (float) height, (float) (bound.right - bound.left) / (float) width);
                    width = (int) (width * mScale);
                    height = (int) (height * mScale);
                }
                int left = bound.left;
                int top = bound.top;
                switch (mScaleType) {
                    case FIT_CENTER:
                        left = (int) (((bound.right - bound.left) - width) / 2);
                        top = (int) (((bound.bottom - bound.top) - height) / 2);
                        break;
                    case FIT_END:
                        top = (int) ((bound.bottom - bound.top) - height);
                        break;
                }
                //float mScale = Math.min((float) (bound.bottom - bound.top) / (float) mBitmapDrawable.getIntrinsicHeight(), (float) (bound.right - bound.left) / (float) mBitmapDrawable.getIntrinsicWidth());
                mBitmapDrawable.setBounds(new Rect(left, top, left + width, top + height));
                mBitmapDrawable.setAlpha(mAlpha);
                mBitmapDrawable.setColorFilter(mColorFilter);
                mBitmapDrawable.draw(canvas);
                // canvas.drawBitmap(mGifFrame.image, null, getBounds(), null);
            }
            invalidateSelf();
        } else if (mBitmapDrawable != null) {
            //Log.i("rime", "backget: " + getBounds() + mBitmapDrawable);
            Rect bound = getBounds();
            int width = mBitmapDrawable.getIntrinsicWidth();
            int height = mBitmapDrawable.getIntrinsicHeight();
            float mScale = 1;
            if (mScaleType == FIT_XY) {
                float mScaleX = (float) (bound.right - bound.left) / (float) width;
                float mScaleY = (float) (bound.bottom - bound.top) / (float) height;
                width = (int) (width * mScaleX);
                height = (int) (height * mScaleY);
            } else if (mScaleType != MATRIX) {
                mScale = Math.min((float) (bound.bottom - bound.top) / (float) height, (float) (bound.right - bound.left) / (float) width);
                width = (int) (width * mScale);
                height = (int) (height * mScale);
            }
            int left = bound.left;
            int top = bound.top;
            switch (mScaleType) {
                case FIT_CENTER:
                    left = (int) (((bound.right - bound.left) - width) / 2);
                    top = (int) (((bound.bottom - bound.top) - height) / 2);
                    break;
                case FIT_END:
                    top = (int) ((bound.bottom - bound.top) - height);
                    break;
            }
            //float mScale = Math.min((float) (bound.bottom - bound.top) / (float) mBitmapDrawable.getIntrinsicHeight(), (float) (bound.right - bound.left) / (float) mBitmapDrawable.getIntrinsicWidth());
            mBitmapDrawable.setBounds(new Rect(left, top, left + width, top + height));
            mBitmapDrawable.setAlpha(mAlpha);
            mBitmapDrawable.setColorFilter(mColorFilter);
            mBitmapDrawable.draw(canvas);
            //canvas.drawBitmap(mBitmapDrawable.getBitmap(),getBounds(),getBounds(),new Paint());
            mHasInvalidate = false;
        } else if (mNineBitmapDrawable != null) {
            mNineBitmapDrawable.setBounds(getBounds());
            mNineBitmapDrawable.setAlpha(mAlpha);
            mNineBitmapDrawable.setColorFilter(mColorFilter);
            mNineBitmapDrawable.draw(canvas);
            mHasInvalidate = false;
        } else if (mLoadingDrawable != null) {
            mLoadingDrawable.setBounds(getBounds());
            mLoadingDrawable.draw(canvas);
            mLoadingDrawable.setAlpha(mAlpha);
            mLoadingDrawable.setColorFilter(mColorFilter);
            invalidateSelf();
        }
    }

    @Override
    public void invalidateSelf() {
        try {
            mHasInvalidate = true;
            Rect rect = getBounds();
            if (rect.right - rect.left <= 0)
                return;
            super.invalidateSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (mGifDecoder2 != null)
            mGifDecoder2.free();
    }

    public void setScaleType(int scaleType) {

        if (mScaleType != scaleType) {
            mScaleType = scaleType;
            invalidateSelf();
        }
    }

    public void setFillColor(int fillColor) {
        if (fillColor == mFillColor) {
            return;
        }
        mFillColor = fillColor;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mColorFilter = colorFilter;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public static String getHttpBitmap(LuaContext context, String url) throws IOException {
        //Log.d(TAG, url);
        String path = context.getLuaExtDir("cache") + "/" + url.hashCode();
        File f = new File(path);
        if (f.exists() && System.currentTimeMillis() - f.lastModified() < mCacheTime) {
            return path;
        }
        new File(path).delete();
        URL myFileUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
        conn.setConnectTimeout(120000);
        conn.setDoInput(true);
        conn.connect();
        InputStream is = conn.getInputStream();
        FileOutputStream out = new FileOutputStream(path);
        if (!LuaUtil.copyFile(is, out)) {
            out.close();
            is.close();
            new File(path).delete();
            throw new RuntimeException("LoadHttpBitmap Error.");
        }
        out.close();
        is.close();
        return path;
    }

    public static final int MATRIX = (0);
    public static final int FIT_XY = (1);
    public static final int FIT_START = (2);
    public static final int FIT_CENTER = (3);
    public static final int FIT_END = (4);
    public static final int CENTER = (5);
    public static final int CENTER_CROP = (6);
    public static final int CENTER_INSIDE = (7);

    @Override
    public void run() {
        invalidateSelf();
    }

    public boolean isHasInvalidate() {
        return mHasInvalidate;
    }

    @Override
    public void gc() {
        mHasInvalidate = false;
        if (isGc())
            return;
        try {
            if (mGifDecoder2 != null)
                mGifDecoder2.free();
            if (mBitmapDrawable != null && mBitmapDrawable instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) mBitmapDrawable).getBitmap();
                if (bmp == null)
                    return;
                LuaBitmap.removeBitmap(bmp);
                if (bmp.isRecycled())
                    return;
                bmp.recycle();
            }
            if (mNineBitmapDrawable != null)
                mNineBitmapDrawable.gc();
            mGifDecoder2 = null;
            mBitmapDrawable = null;
            mNineBitmapDrawable = null;
            mLoadingDrawable.setState(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mGc = true;
    }

    @Override
    public boolean isGc() {
        return mGc;
    }
}
