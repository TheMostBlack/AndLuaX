package com.osfans.trime;

import android.util.Log;

import com.androlua.LuaUtil;
import com.androlua.util.AsyncTaskX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class HttpUtil {

    private static HashMap<String, String> sHeader;

    public static void setHeader(HashMap<String, String> header) {
        sHeader = header;
    }

    public static HashMap<String, String> getHeader() {
        return sHeader;
    }


    public static HttpTask get(String url, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "GET", null, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask get(String url, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "GET", null, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask get(String url, String cookie, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = cookie.matches("[\\w\\-]+") && Charset.isSupported(cookie) ? new HttpTask(url, "GET", null, cookie, header, callback) : new HttpTask(url, "GET", cookie, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask get(String url, String cookie, HttpCallback callback) {
        HttpTask task = cookie.matches("[\\w\\-]+") && Charset.isSupported(cookie) ? new HttpTask(url, "GET", null, cookie, null, callback) : new HttpTask(url, "GET", cookie, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask get(String url, String cookie, String charset, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "GET", cookie, charset, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask get(String url, String cookie, String charset, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "GET", cookie, charset, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask download(String url, String data, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "GET", null, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask download(String url, String data, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "GET", null, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask download(String url, String data, String cookie, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "GET", cookie, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask download(String url, String data, String cookie, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "GET", cookie, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }


    public static HttpTask delete(String url, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "DELETE", null, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask delete(String url, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "DELETE", null, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask delete(String url, String cookie, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = cookie.matches("[\\w\\-]+") && Charset.isSupported(cookie) ? new HttpTask(url, "DELETE", null, cookie, header, callback) : new HttpTask(url, "DELETE", cookie, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask delete(String url, String cookie, HttpCallback callback) {
        HttpTask task = cookie.matches("[\\w\\-]+") && Charset.isSupported(cookie) ? new HttpTask(url, "DELETE", null, cookie, null, callback) : new HttpTask(url, "DELETE", cookie, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask delete(String url, String cookie, String charset, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "DELETE", cookie, charset, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask delete(String url, String cookie, String charset, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "DELETE", cookie, charset, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR);
        return task;
    }


    public static HttpTask post(String url, String data, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "POST", null, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask post(String url, String data, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "POST", null, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask post(String url, String data, String cookie, HttpCallback callback) {
        HttpTask task = cookie.matches("[\\w\\-]+") && Charset.isSupported(cookie) ? new HttpTask(url, "POST", null, cookie, null, callback) : new HttpTask(url, "POST", cookie, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask post(String url, String data, String cookie, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = cookie.matches("[\\w\\-]+") && Charset.isSupported(cookie) ? new HttpTask(url, "POST", null, cookie, header, callback) : new HttpTask(url, "POST", cookie, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask post(String url, String data, String cookie, String charset, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "POST", cookie, charset, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask post(String url, String data, String cookie, String charset, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "POST", cookie, charset, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }


    public static HttpTask put(String url, String data, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "PUT", null, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask put(String url, String data, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "PUT", null, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask put(String url, String data, String cookie, HttpCallback callback) {
        HttpTask task = cookie.matches("[\\w\\-]+") && Charset.isSupported(cookie) ? new HttpTask(url, "PUT", null, cookie, null, callback) : new HttpTask(url, "PUT", cookie, null, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask put(String url, String data, String cookie, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = cookie.matches("[\\w\\-]+") && Charset.isSupported(cookie) ? new HttpTask(url, "PUT", null, cookie, header, callback) : new HttpTask(url, "PUT", cookie, null, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask put(String url, String data, String cookie, String charset, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "PUT", cookie, charset, null, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask put(String url, String data, String cookie, String charset, HashMap<String, String> header, HttpCallback callback) {
        HttpTask task = new HttpTask(url, "PUT", cookie, charset, header, callback);
        task.executeOnExecutor(AsyncTaskX.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static interface UpdateListener {
        public void onUpdate(String[] values);
    }

    public static class HttpTask extends AsyncTaskX<Object, String, HttpResult> {

        private String mUrl;

        private HttpCallback mCallback;

        private byte[] mData;

        private String mCharset;

        private String mCookie;

        private HashMap<String, String> mHeader;

        private String mMethod;
        private UpdateListener mUpdateListener;


        public HttpTask(String url, String method, String cookie, String charset, HashMap<String, String> header, HttpCallback callback) {
            mUrl = url;
            mMethod = method;
            mCookie = cookie;
            mCharset = charset;
            mHeader = header;
            mCallback = callback;
        }

        public void setUpdateListerer(UpdateListener listener) {
            mUpdateListener = listener;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mUpdateListener.onUpdate(values);
        }

        @Override
        protected HttpResult doInBackground(Object[] p1) {
            // TODO: Implement this method
            try {
                URL url = new URL(mUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6000);
                HttpURLConnection.setFollowRedirects(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");

                if (mCharset == null)
                    mCharset = "UTF-8";
                conn.setRequestProperty("Accept-Charset", mCharset);

                if (mCookie != null)
                    conn.setRequestProperty("Cookie", mCookie);

                if (sHeader != null) {
                    Set<Map.Entry<String, String>> entries = sHeader.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }

                if (mHeader != null) {
                    Set<Map.Entry<String, String>> entries = mHeader.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }

                if (mMethod != null)
                    conn.setRequestMethod(mMethod);

                if (!"GET".equals(mMethod) && p1.length != 0) {
                    mData = formatData(p1);

                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-length", "" + mData.length);
                }

                conn.connect();

                //download
                if ("GET".equals(mMethod) && p1.length != 0) {
                    File f = new File((String) p1[0]);
                    if (!f.getParentFile().exists())
                        f.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(f);
                    InputStream in = conn.getInputStream();
                    long len = conn.getContentLength();
                    long off=0;
                    try {
                        int byteread = 0;
                        byte[] buffer = new byte[8 * 1024];
                        while ((byteread = in.read(buffer)) != -1) {
                            out.write(buffer, 0, byteread);
                            off+=byteread;
                            publishProgress(String.format(Locale.getDefault(),"%d%%",off*100/len));
                        }
                        //in.close();
                        //out.close();
                    } catch (Exception e) {
                    }
                    return new HttpResult(conn.getResponseCode(), f.getAbsolutePath(), null, conn.getHeaderFields());
                }

                //post upload
                if (p1.length != 0) {
                    OutputStream os = conn.getOutputStream();
                    os.write(mData);
                }

                int code = conn.getResponseCode();
                Map<String, List<String>> hs = conn.getHeaderFields();
                String encoding = conn.getContentEncoding();
                List<String> cs = hs.get("Set-Cookie");
                StringBuilder cok = new StringBuilder();
                if (cs != null)
                    for (String s : cs) {
                        cok.append(s).append(";");
                    }

                StringBuilder buf = new StringBuilder();
                try {
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, mCharset));
                    String line;
                    while ((line = reader.readLine()) != null && !isCancelled())
                        buf.append(line).append('\n');
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                InputStream is = conn.getErrorStream();
                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, mCharset));
                    String line;
                    while ((line = reader.readLine()) != null && !isCancelled())
                        buf.append(line).append('\n');
                    is.close();
                }
                return new HttpResult(code, new String(buf), cok.toString(), hs);
            } catch (Exception e) {
                e.printStackTrace();
                return new HttpResult(-1, e.getMessage(), null, null);
            }

        }

        private byte[] formatData(Object[] p1) throws UnsupportedEncodingException, IOException {
            // TODO: Implement this method
            byte[] bs = null;
            if (p1.length == 1) {
                Object obj = p1[0];
                if (obj instanceof String)
                    bs = ((String) obj).getBytes(mCharset);
                else if (obj.getClass().getComponentType() == byte.class)
                    bs = (byte[]) obj;
                else if (obj instanceof File)
                    bs = LuaUtil.readAll(new FileInputStream((File) obj));
                else if (obj instanceof Map)
                    bs = formatData((Map) obj);
            }
            return bs;
        }

        private byte[] formatData(Map obj) throws UnsupportedEncodingException {
            // TODO: Implement this method
            StringBuilder buf = new StringBuilder();
            Set<Map.Entry<String, String>> entries = mHeader.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                buf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            return buf.toString().getBytes(mCharset);
        }


        public boolean cancel() {
            // TODO: Implement this method
            return super.cancel(true);
        }


        @Override
        protected void onPostExecute(HttpResult result) {
            // TODO: Implement this method
            if (isCancelled())
                return;
            mCallback.onDone(result);
        }
    }

    public interface HttpCallback {
        public void onDone(HttpResult result);
    }

    public static class HttpResult {
        public int code;
        public String text;
        public String cookie;
        public Map<String, List<String>> header;

        public HttpResult(int code,
                          String text,
                          String cookie,
                          Map<String, List<String>> header) {

            this.code = code;
            this.text = text;
            this.cookie = cookie;
            this.header = header;
        }
    }
}
