package com.osfans.trime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by nirenr on 2019/1/12.
 */

public class JsonUtil {
    public static ArrayList<String> load(File path) {
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream stream = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String input;
            while ((input = reader.readLine()) != null) {
                stringBuilder.append(input);
            }
            stream.close();
            JSONArray letters = new JSONArray(stringBuilder.toString());
            int len = letters.length();
            for (int i = 0; i < len; i++) {
                list.add(letters.getString(i));
            }
        } catch (java.io.IOException | JSONException ignored) {
        }
        return list;
    }


    public static void save(File path, List<String> map) {
        JSONArray json = new JSONArray(map);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            writer.write(json.toString(4));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONArray loadArray(File path) {
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream stream = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String input;
            while ((input = reader.readLine()) != null) {
                stringBuilder.append(input);
            }
            stream.close();
            return new JSONArray(stringBuilder.toString());

        } catch (java.io.IOException | JSONException ignored) {
        }
        return new JSONArray();
    }

    public static void save(File path, JSONObject json) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            writer.write(json.toString(4));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save(File path, JSONArray json) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            writer.write(json.toString(4));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<HistoryData> loadHistoryData(String path) {
        ArrayList<HistoryData> list = new ArrayList<>();
        if (!new File(path).exists())
            return list;
        try {
            InputStream stream = new FileInputStream(new File(path));
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String input;
            while ((input = reader.readLine()) != null) {
                stringBuilder.append(input);
            }
            stream.close();
            try {
                JSONArray json = new JSONObject(stringBuilder.toString()).getJSONArray("history");
                int len = json.length();
                for (int i = 0; i < len; i++) {
                    list.add(new HistoryData(json.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveHistoryData(String path, ArrayList<HistoryData> history) {
        JSONObject json = new JSONObject();
        JSONArray list = new JSONArray();
        try {
            for (HistoryData data : history) {
                list.put(data.toJson());
            }
            json.put("history", list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            writer.write(json.toString(2));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> read(File file) {
        Map<String, Object> map=new HashMap<>();
        try {
            InputStream stream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String input;
            while ((input = reader.readLine()) != null) {
                stringBuilder.append(input);
            }
            stream.close();
            JSONObject json = new JSONObject(stringBuilder.toString());
            Iterator<String> ks = json.keys();
            while (ks.hasNext()){
                String k = ks.next();
                map.put(k,json.get(k));
            }
        } catch (java.io.IOException | JSONException ignored) {
        }
        return map;
    }

    public static class HistoryData {
        private String mPath;
        private int mIdx;

        public HistoryData(JSONObject json) {
            mPath = json.optString("path");
            mIdx = json.optInt("idx");
        }

        public HistoryData(String path, int idx) {
            mPath = path;
            mIdx = idx;
        }

        public String getPath() {
            return mPath;
        }


        public int getIdx() {
            return mIdx;
        }

        public JSONObject toJson() {
            JSONObject j = new JSONObject();
            try {
                j.put("path",mPath);
                j.put("idx",mIdx);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return j;
        }
    }
}
