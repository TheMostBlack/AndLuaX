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
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static android.media.AudioManager.STREAM_SYSTEM;

/**
 * 處理按鍵聲音、震動、朗讀等效果
 */
class Effect {
    private static final int MAX_VOLUME = 101; //100%音量時只響一下，暫從100改成101
    private final Random mRandom;
    private final MediaPlayer mp;
    private int duration = 10;
    private long durationLong;
    private int volume = 100;
    private float volumeFloat;

    private final Context context;

    private boolean vibrateOn;
    private Vibrator vibrator;
    private boolean soundOn;
    private AudioManager audioManager;
    private boolean isSpeakCommit, isSpeakKey;
    private TextToSpeech mTTS;
    private SoundPool mSoundPool;
    private int mSpace;
    private int mClick;
    private int mDel;
    private int mEnter;
    private boolean soundRandom;
    private final HashMap<Integer, Integer> soundMap = new HashMap<>();

    public Effect(Context context) {
        this.context = context;
        mSoundPool = new SoundPool(4, STREAM_SYSTEM, 0);
        mRandom = new Random();
        mp = new MediaPlayer();
    }

    public void reset() {
        try {
            destory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<Integer> ks = soundMap.keySet();
        for (Integer k : ks) {
            try {
                Integer id = soundMap.get(k);
                if (id != null)
                    mSoundPool.unload(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        soundMap.clear();

        SharedPreferences pref = Function.getPref(context);
        String userDataDir = Config.get().getUserDataDir();
        duration = pref.getInt("key_vibrate_duration", duration);
        durationLong = duration * 1L;
        vibrateOn = pref.getBoolean("key_vibrate", false) && (duration > 0);
        if (vibrateOn && (vibrator == null)) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        volume = pref.getInt("key_sound_volume", volume);
        volumeFloat = (float) (1 - (Math.log(MAX_VOLUME - volume) / Math.log(MAX_VOLUME)));
        soundOn = pref.getBoolean("key_sound", false);
        soundRandom = pref.getBoolean("key_sound_random", false);
        if (soundOn && (audioManager == null)) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        if (mClick != 0)
            mSoundPool.unload(mClick);
        mClick = 0;
        if (mSpace != 0)
            mSoundPool.unload(mSpace);
        mSpace = 0;
        if (mDel != 0)
            mSoundPool.unload(mDel);
        mDel = 0;
        if (mEnter != 0)
            mSoundPool.unload(mEnter);
        mEnter = 0;

        if (soundOn) {
            String spk = pref.getString("key_sound_package", "none");
            if (!spk.equals("none")) {
                File dir = new File(new File(userDataDir, "sounds"), spk);
                loadSounds(dir);
            } else {
                File dir = new File(userDataDir, Config.get().getTheme());
                loadSounds(dir);
            }
            File dir = new File(userDataDir, "sounds");
            if (dir.exists()) {
                String[] fs = dir.list();
                if (fs != null) {
                    for (String f : fs) {
                        f = f.toLowerCase();
                        if (mClick == 0 && f.startsWith("click")) {
                            mClick = mSoundPool.load(new File(dir, f).getAbsolutePath(), 1);
                        } else if (mSpace == 0 && f.startsWith("space")) {
                            mSpace = mSoundPool.load(new File(dir, f).getAbsolutePath(), 1);
                        } else if (mDel == 0 && f.startsWith("del")) {
                            mDel = mSoundPool.load(new File(dir, f).getAbsolutePath(), 1);
                        } else if (mEnter == 0 && f.startsWith("enter")) {
                            mEnter = mSoundPool.load(new File(dir, f).getAbsolutePath(), 1);
                        }
                    }
                }
            }
        }

        isSpeakCommit = pref.getBoolean("speak_commit", false);
        isSpeakKey = pref.getBoolean("speak_key", false);
        if (mTTS == null && (isSpeakCommit || isSpeakKey)) {
            mTTS = new TextToSpeech(
                    context,
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            //初始化結果
                        }
                    });
        }
    }

    private void loadSounds(File dir) {
        if (dir.exists()) {
            File[] fs = dir.listFiles();
            if (fs != null) {
                for (File ff : fs) {
                    String f = ff.getName();
                    try {
                        int i = f.indexOf(".");
                        i = Integer.valueOf(f.substring(0, i));
                        soundMap.put(i, mSoundPool.load(ff.getAbsolutePath(), 1));
                        continue;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    f = f.toLowerCase();
                    if (f.startsWith("click")) {
                        mClick = mSoundPool.load(new File(dir, f).getAbsolutePath(), 1);
                    } else if (f.startsWith("space")) {
                        mSpace = mSoundPool.load(new File(dir, f).getAbsolutePath(), 1);
                    } else if (f.startsWith("del")) {
                        mDel = mSoundPool.load(new File(dir, f).getAbsolutePath(), 1);
                    } else if (f.startsWith("enter")) {
                        mEnter = mSoundPool.load(new File(dir, f).getAbsolutePath(), 1);
                    }
                }
            }
        }
    }

    public void vibrate() {
        if (vibrateOn && (vibrator != null)) vibrator.vibrate(durationLong);
    }

    public void playSound(final int code) {
        if (!soundOn)
            return;
        float rate = 1;
        if (soundRandom)
            rate = mRandom.nextFloat() + 0.5f;

        if (soundMap.containsKey(code)) {
            Integer id = soundMap.get(code);
            if (id != null) {
                mSoundPool.play(id, volumeFloat, volumeFloat, 0, 0, 1);
                return;
            }
        }
        switch (code) {
            case KeyEvent.KEYCODE_DEL:
                if (mDel != 0)
                    mSoundPool.play(mDel, volumeFloat, volumeFloat, 0, 0, rate);
                else if (audioManager != null)
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE, volumeFloat);
                else
                    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                break;
            case KeyEvent.KEYCODE_ENTER:
                if (mEnter != 0)
                    mSoundPool.play(mEnter, volumeFloat, volumeFloat, 0, 0, rate);
                else if (audioManager != null)
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN, volumeFloat);
                else
                    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                break;
            case KeyEvent.KEYCODE_SPACE:
                if (mSpace != 0)
                    mSoundPool.play(mSpace, volumeFloat, volumeFloat, 0, 0, rate);
                else if (audioManager != null)
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR, volumeFloat);
                else
                    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                break;
            default:
                if (mClick != 0)
                    mSoundPool.play(mClick, volumeFloat, volumeFloat, 0, 0, rate);
                else if (audioManager != null)
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volumeFloat);
                else
                    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                break;
        }
    }

    public void setLanguage(Locale loc) {
        //if (mTTS != null) mTTS.setLanguage(loc);
    }

    public void speak(final CharSequence text) {
        if (mTTS == null) {
            mTTS = new TextToSpeech(
                    context,
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            //初始化結果
                            mTTS.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, new Bundle(), "");
                        }
                    });
            return;
        }
        if (text != null && mTTS != null)
            mTTS.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, new Bundle(), "");
    }

    public void speakCommit(CharSequence text) {
        if (isSpeakCommit) speak(text);
    }

    public void speakKey(CharSequence text) {
        if (isSpeakKey) speak(text);
    }

    public void speakKey(int code) {
        if (code <= 0) return;
        String text =
                KeyEvent.keyCodeToString(code)
                        .replace("KEYCODE_", "")
                        .replace("_", " ")
                        .toLowerCase(Locale.getDefault());
        switch (code) {
            case KeyEvent.KEYCODE_DEL:
                text = "删除";
                break;
            case KeyEvent.KEYCODE_ENTER:
                text = "回车";
                break;
            case KeyEvent.KEYCODE_SPACE:
                text = "空格";
                break;
            default:
                Log.i("rime", "speakKey: " + text + ";" + code);
                break;
        }

        speakKey(text);
    }

    public void destory() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
            mTTS = null;
        }
    }
}
