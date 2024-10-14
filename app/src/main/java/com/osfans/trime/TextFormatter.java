package com.osfans.trime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.osfans.trime.pro.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/04/18 0018.
 */

public class TextFormatter {
    private final Context mService;
    private static final String emoji = "(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE32-\uD83C\uDE3A]\uFE0F?)";
    private static final Pattern emoji2 = Pattern.compile("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)");

    private Map<String, String> mEmojisMap = new HashMap<String, String>();
    ;

    private Map<String, String> mSymbolsMap = new HashMap<String, String>();
    ;

    private Map<String, String> mLetterMap = new HashMap<String, String>();
    ;

    public Map<String, String> getLetterMap() {
        return mLetterMap;
    }

    public Map<String, String> getSymbolsMap() {
        return mSymbolsMap;
    }

    public TextFormatter(final Context service) {
        mService = service;
    }

    @SuppressLint("StaticFieldLeak")
    public void loadLetter() {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String[] params) {
                mLetterMap = getLetterMap(R.raw.dict);
                mSymbolsMap = getLetterMap(R.raw.symbols);
                mEmojisMap = getLetterMap(R.raw.emoji);

                return null;
            }
        }.execute("");
    }


    public String formatSymbols(String text) {
        if (text.length() > 3)
            return text;
        String letter = mSymbolsMap.get(text);
        if (letter != null)
            return letter;
        letter = mEmojisMap.get(text);
        if (letter != null)
            return "表情" + letter;
        return text;
    }
    public String formatSymbols2(String text) {
        if (text.length() > 3)
            return null;
        String letter = mSymbolsMap.get(text);
        if (letter != null)
            return letter;
        letter = mEmojisMap.get(text);
        if (letter != null)
            return "表情" + letter;
        return null;
    }
    public String formatEmojis(String text) {
        if (!emoji2.matcher(text).matches())
            return text;

        for (Object o : mEmojisMap.entrySet()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) o;
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }
        return text;
    }

    @SuppressLint("DefaultLocale")
    public String formatText(String text) {
        if(TextUtils.isEmpty(text))
            return "";
        String letter = mSymbolsMap.get(text);
        if (letter != null)
            return letter;
        letter = mEmojisMap.get(text);
        if (letter != null)
            return "表情" + letter;
        char key = text.charAt(0);

        letter = mLetterMap.get(String.valueOf(key));
        if (letter == null) {
            return text;
        }
        return letter;
    }

    public String format(String text) {
        return formatText(text);
    }

    private Map<String, String> getLetterMap(int rawId) {
        Map<String, String> map = new HashMap<String, String>();
        InputStream stream =
                mService.getResources().openRawResource(rawId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder(8196);
            String input;
            while ((input = reader.readLine()) != null) {
                stringBuilder.append(input);
            }
            stream.close();

            JSONObject letters = new JSONObject(stringBuilder.toString());
            Iterator<?> keys = letters.keys();
            while (keys.hasNext()) {
                String letter = (String) keys.next();
                map.put(letter, letters.getString(letter));
            }
        } catch (java.io.IOException | JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Map<String, String> getLetterMap(String path, Map<String, String> map) {
        try {
            InputStream stream =
                    new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder(8196);
            String input;
            while ((input = reader.readLine()) != null) {
                stringBuilder.append(input);
            }
            stream.close();

            JSONObject letters = new JSONObject(stringBuilder.toString());
            Iterator<?> keys = letters.keys();
            while (keys.hasNext()) {
                String letter = (String) keys.next();
                map.put(letter, letters.getString(letter));
            }
        } catch (java.io.IOException | JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String format(int c) {
        return format(String.valueOf(Character.toChars(c)));
    }
}
