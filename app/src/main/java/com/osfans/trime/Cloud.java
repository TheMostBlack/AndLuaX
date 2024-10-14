package com.osfans.trime;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nirenr on 2019/1/1.
 */

public class Cloud {
    private static final String url = "https://olime.baidu.com/py?inputtype=py&bg=0&ed=20&result=hanzi&resultcoding=utf-8&ch_en=0&clientinfo=web&version=1&input=";
    private static final String url2 = "https://www.google.cn/inputtools/request?ime=pinyin&text=";
    private static final Pattern p = Pattern.compile("\\[\"([^\"]*)");
    //private static final Pattern p2 = Pattern.compile("\\[\"([^\\[\\]]*)");
    private static HttpUtil.HttpTask sTask;
    private static HttpUtil.HttpTask sTask2;
    private static HashMap<String,String> cache=new HashMap<>();

    public static void get(final String py, final CloudCallback callback) {
        if(sTask!=null)
            sTask.cancel();
        if(sTask2!=null)
            sTask2.cancel();
        sTask=null;
        sTask2=null;
        sTask = HttpUtil.get(url2 + py, new HttpUtil.HttpCallback() {
            @Override
            public void onDone(HttpUtil.HttpResult result) {
                final ArrayList<String> list = new ArrayList<>();
                if (result.code == 200) {
                    try {
                        String sText = new JSONArray(result.text).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
                        if(sText.length()>1){
                            list.add(sText);
                            callback.onDone(sText);
                        }
                     } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                sTask=null;
                sTask2 = HttpUtil.get(url + py, new HttpUtil.HttpCallback() {
                    @Override
                    public void onDone(HttpUtil.HttpResult result) {
                        Log.i("rime", "onDone: "+result.text);
                        if (result.code == 200) {
                            Matcher m = p.matcher(result.text);
                            while (m.find()) {
                                String g = m.group(1);
                                if (!list.contains(g))
                                    list.add(g);
                            }
                        }
                        callback.onDone(list);
                        sTask2=null;
                    }
                });
            }
        });
    }

    public static interface CloudCallback {
        public void onDone(ArrayList<String> list);

        public void onDone(String text);
    }
}
