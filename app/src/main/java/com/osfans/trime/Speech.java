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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.osfans.trime.pro.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.media.AudioManager.STREAM_MUSIC;

/**
 * {@link RecognitionListener 語音輸入}
 */
class Speech implements RecognitionListener {
    private final SoundPool mSoundPool;
    private  int mSoundCancel;
    private  int mSoundError;
    private  int mSoundStart;
    private  int mSoundSuccess;
    private  int mSoundEnd;
    private final Vibrator mVibrator;
    private int duration=10;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String TAG = "Speech";
    private Context context;
    private int state = STATE_CANCEL;
    public static final int STATE_DONE = -1;
    public static final int STATE_ERROR = -2;
    public static final int STATE_READY = 0;
    public static final int STATE_BEGIN = 1;
    public static final int STATE_END = 2;
    public static final int STATE_START = 3;
    public static final int STATE_CANCEL = -3;

    public Speech(Context context) {
        this.context = context;
        String[] name = Function.getPref(context).getString("recognition_service", context.getString(R.string.value_default)).split("/");
        if (name.length == 1) {
            if (SpeechRecognizer.isRecognitionAvailable(context))
                speech = SpeechRecognizer.createSpeechRecognizer(context);
        } else {
            try {
                speech = SpeechRecognizer.createSpeechRecognizer(context, new ComponentName(name[1], name[2]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mVibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        SharedPreferences pref = Function.getPref(context);
        duration = (int) (pref.getInt("key_vibrate_duration", duration)*1.5);

        /*if(!SpeechRecognizer.isRecognitionAvailable(context)){
            HashMap<String, ComponentName> list = getService(context);
            Log.i(TAG, "Speech: "+list);
        //}
        if(!list.isEmpty()){
            speech =  SpeechRecognizer.createSpeechRecognizer(context,list.values().iterator().next());
        }*/
        if(speech!=null)
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "開始語音");
        mSoundPool = new SoundPool(4, STREAM_MUSIC, 0);
        mSoundCancel = mSoundPool.load(context, R.raw.speech_recognition_cancel, 1);
        mSoundError = mSoundPool.load(context, R.raw.speech_recognition_error, 1);
        mSoundStart = mSoundPool.load(context, R.raw.speech_recognition_start, 1);
        mSoundSuccess = mSoundPool.load(context, R.raw.speech_recognition_success, 1);
        mSoundEnd = mSoundPool.load(context, R.raw.speech_speech_end, 1);
    }

    private HashMap<String, ComponentName> getService(Context context) {
        HashMap<String, ComponentName> appMap = new HashMap<>();
        ArrayList<String> list = new ArrayList<String>();
        PackageManager manager = context.getPackageManager();
        Intent mainIntent = new Intent(RecognitionService.SERVICE_INTERFACE); //取出Intent 为Action_Main的程序
        List<ResolveInfo> apps = manager.queryIntentServices(mainIntent, 0); //利用包管理器将起取出来
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        int count = apps.size();
        for (int i = 0; i < count; i++) {
            //ApplicationInfo application = new ApplicationInfo();
            ResolveInfo info = apps.get(i);

            CharSequence title = info.loadLabel(manager);
            ComponentName componentName = new ComponentName(
                    info.serviceInfo.applicationInfo.packageName,
                    info.serviceInfo.name);
            appMap.put(title.toString(), componentName);
        }
        return appMap;
    }

    private void playSound(int id) {
        mSoundPool.play(id, 0.5f, 0.5f, 0, 0, 1);
        vibrate();
    }

    public void vibrate() {
        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator.vibrate(duration);
        }
    }

    private void alert(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void start() {
        if (speech==null) {
            alert("未正确设置识别引擎");
            playSound(mSoundError);
            return;
        }
        Log.i(TAG, "start");
        switch (state) {
            case STATE_BEGIN:
                stop();
                break;
            case STATE_START:
            case STATE_READY:
            case STATE_END:
                speech.cancel();
                playSound(mSoundCancel);
                state = STATE_CANCEL;
                break;
            default:
                state = STATE_START;
                speech.startListening(recognizerIntent);
        }
    }

    public void stop() {
        Log.i(TAG, "stop");
        vibrate();
        speech.stopListening();
    }

    public void destroy() {
        if (speech != null) {
            speech.destroy();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        state = STATE_BEGIN;
        Log.i(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        //Log.i(TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        state = STATE_END;
        Log.i(TAG, "onEndOfSpeech");
        playSound(mSoundEnd);
        //alert("正在识别...");
    }

    @Override
    public void onError(int errorCode) {
        state = STATE_ERROR;
        //speech.stopListening();
        //speech.destroy();
        String errorMessage = getErrorText(errorCode);
        alert(errorMessage);
        playSound(mSoundError);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        state = STATE_READY;
        Log.i(TAG, "onReadyForSpeech");
        //alert("开始说话：");
        playSound(mSoundStart);
    }

    @Override
    public void onResults(Bundle results) {
        //stop();
        playSound(mSoundSuccess);
        state = STATE_DONE;
        Log.i(TAG, "onResults");
        Trime trime = Trime.getService();
        if (trime == null) return;
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        //String opencc_config = Function.getPref(context).getString("voice_input_s2t_config","none");
        boolean s2t2 = Function.getPref(context).getBoolean("voice_input_s2t", false);
        boolean s2t = !Rime.getOption("simplification");
        if (matches != null) {
            for (String result : matches) {
                if (s2t2) {
                    if (s2t)
                        result = Rime.openccConvert(result, "s2t.json");
                    else
                        result = Rime.openccConvert(result, "t2s.json");
                }
                String path = trime.getLuaExtPath("speech.lua");
                if (new File(path).exists()) {
                    Object ret = trime.doFile(path, result);
                    if (ret == null)
                        return;
                    if (ret instanceof String)
                        result = ret.toString();
                }
                trime.commitText(result);
            }
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(TAG, "onRmsChanged: " + rmsdB);
    }

    private static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "錄音錯誤";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "客戶端錯誤";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "权限不足";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "網絡錯誤";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "網絡超時";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "未能識別";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "識別服務忙";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "服務器錯誤";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "無語音輸入";
                break;
            default:
                message = "未知錯誤";
                break;
        }
        return message;
    }

    public int getState() {
        return state;
    }
}
