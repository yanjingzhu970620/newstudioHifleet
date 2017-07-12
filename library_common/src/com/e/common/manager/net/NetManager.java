package com.e.common.manager.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.e.common.utility.CommonUtility;
import com.e.library_common.R;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;

/**
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @{# NetManager.java Create on 2014年11月20日 下午2:32:50
 * @description 网络处理管理器
 */
public class NetManager {

    // 网络请求超时时间
    private final int TIMEOUTTIME = 20000;

    // 请求地址
    private static String baseRequestPath = "";

    public final String CHARSET_PREFIX = "\ufeff";

    private static NetManager mNetManager = null;

    public static final int REQUEST_TYPE_GET = 0, REQUEST_TYPE_POST = 1,
            REQUEST_TYPE_PUT = 2, REQUEST_TYPE_DELETE = 3;

    public static final String NET_EXCEPTION_BY_DEVICE = "NET_EXCEPTION_BY_DEVICE"; //网络未设置
    public static final String NET_EXCEPTION_BY_SERVER = "NET_EXCEPTION_BY_SERVER"; //服务器相应出错

    public static final String KEY_REQUEST_TYPE = "REQUEST_TYPE";
    public static final String KEY_REQUEST_CACHE = "KEY_REQUEST_CACHE";

    private HashMap<String, String> mHeader;

    OkHttpClient mOKClient;

    private Context mContext;

    private int mMaxStale = 0;
    private boolean mCache = false;

    public static NetManager getInstance(Context context) {
        if (mNetManager == null) {
            mNetManager = new NetManager(context);
            baseRequestPath = ((INet) context.getApplicationContext())
                    .getBaseRequestPath();
        }
        return mNetManager;
    }

    private NetManager() {
    }

    public void initOptions(NetOptions options) {
        mCache = options.ismCache();
        mMaxStale = options.getmMaxStale();
        if (mCache) {
            try {
                Cache cache = new Cache(mContext.getDir("request_cache",
                        Context.MODE_PRIVATE), 10 * 1024 * 1024);
                mOKClient.setCache(cache);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private NetManager(Context context) {
        mHeader = ((INet) context.getApplicationContext()).getHeaderInfo();
        mOKClient = new OkHttpClient();
        mOKClient.setConnectTimeout(TIMEOUTTIME, TimeUnit.SECONDS);
        this.mContext = context;
    }

    public boolean checkNetworkIsValid(Handler handler) {
        if (checkNetwork())
            return true;
        else {
            if (handler != null)
                handler.sendEmptyMessage(R.string.network_invalid);
        }
        return false;
    }

    public boolean checkNetwork() {
        try {
            ConnectivityManager manager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isAvailable()) {
                // 当前网络不可用
                return false;
            } else {
                // 当前网络可用
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized String sendRequestFromHttpClientByOkHttpMultipart(String path,
                                                                          Map<String, Object> params) {
        StringBuilder pars = new StringBuilder();
        int requestType = (Integer) params.get(KEY_REQUEST_TYPE);
        boolean isPostOrPut = requestType == REQUEST_TYPE_POST
                || requestType == REQUEST_TYPE_PUT;
        MultipartBuilder multipartBuilder = null;
        if (isPostOrPut) {
            multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
            if (params.size() <= 1) { // 一定要保证Multipart
                // body有至少一组键值，不然报错
                // java.lang.IllegalStateException:
                // Multipart body must have at
                // least one
                // part.
                multipartBuilder.addFormDataPart("a", "b");
            }
        }
        Request request = null;
        Builder builder = new Builder();

        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String entryObjKey = entry.getKey();
                Object entryObjValue = entry.getValue();
                if (entryObjKey.equals(KEY_REQUEST_TYPE)
                        || entryObjKey.equals(KEY_REQUEST_CACHE)) {
                    continue;
                }
                if (isPostOrPut) {
                    if (entryObjValue instanceof ArrayList) {
                        ArrayList<Object> list = (ArrayList<Object>) entryObjValue;
                        for (Object object : list) {
                            if (object instanceof File) {
                                multipartBuilder.addFormDataPart(entryObjKey,
                                        ((File) object).getName(), RequestBody
                                                .create(MultipartBuilder.FORM,
                                                        (File) object));
                            } else {
                                multipartBuilder.addFormDataPart(entryObjKey,
                                        String.valueOf(object));
                            }
                        }
                    } else {
                        if (entryObjValue instanceof File) {
                            multipartBuilder.addFormDataPart(entryObjKey,
                                    ((File) entryObjValue).getName(),
                                    RequestBody.create(MultipartBuilder.FORM,
                                            (File) entryObjValue));
                        } else {
                            multipartBuilder.addFormDataPart(entryObjKey,
                                    String.valueOf(entryObjValue));
                        }
                    }
                }
                pars.append(entryObjKey).append("=").append(entryObjValue)
                        .append("&");
            }
        }
        RequestBody requestBody = null;
        try {
            if (!CommonUtility.Utility.isNull(multipartBuilder)) {
                requestBody = requestBodyWithContentLength(multipartBuilder
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return execute(path, isPostOrPut, pars, requestBody, requestType, params);
    }


    public synchronized String sendRequestFromHttpClientByOkHttpForm(String path,
                                                                     Map<String, Object> params) {
        StringBuilder pars = new StringBuilder();
        int requestType = (Integer) params.get(KEY_REQUEST_TYPE);
        boolean isPostOrPut = requestType == REQUEST_TYPE_POST
                || requestType == REQUEST_TYPE_PUT;
        FormEncodingBuilder formEncodingBuilder = null;
        if (isPostOrPut) {
            formEncodingBuilder = new FormEncodingBuilder();
        }

        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String entryObjKey = entry.getKey();
                Object entryObjValue = entry.getValue();
                if (entryObjKey.equals(KEY_REQUEST_TYPE)
                        || entryObjKey.equals(KEY_REQUEST_CACHE)) {
                    continue;
                }
                if (isPostOrPut) {
                    if (entryObjValue instanceof ArrayList) {
                        ArrayList<Object> list = (ArrayList<Object>) entryObjValue;
                        for (Object object : list) {
                            if (object instanceof File) {
                                formEncodingBuilder.add(entryObjKey,
                                        String.valueOf(object));
                            }
                        }
                    } else {
                        formEncodingBuilder.add(entryObjKey,
                                String.valueOf(entryObjValue));
                    }
                }
                pars.append(entryObjKey).append("=").append(entryObjValue)
                        .append("&");
            }
        }
        RequestBody requestBody = null;
        try {
            if (!CommonUtility.Utility.isNull(formEncodingBuilder)) {
                requestBody = requestBodyWithContentLength(formEncodingBuilder
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return execute(path, isPostOrPut, pars, requestBody, requestType, params);
    }

    private String execute(String path, boolean isPostOrPut, StringBuilder pars,
                           RequestBody requestBody, int requestType, Map params) {
        Request request = null;
        Builder builder = new Builder();
        StringBuilder url = new StringBuilder();
        if (!path.startsWith("http")) {
            url.append(baseRequestPath);
        }
        if (!isPostOrPut) {
            url.append(path);
            if (!CommonUtility.Utility.isNull(pars)) {
                url.append("?").append(pars);
            }
            builder.url(url.toString());
            if (requestType == REQUEST_TYPE_GET) {
                builder.get();
            } else {
                builder.delete();
            }
        } else {// post || put
            url.append(path);
            builder.url(url.toString());

            if (requestType == REQUEST_TYPE_POST) {
                builder.post(requestBody);
            } else {
                builder.put(requestBody);
            }
        }

        // 添加请求头部信息
        if (!CommonUtility.Utility.isNull(mHeader)) {
            for (Map.Entry<String, String> entry : mHeader.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        /**
         * 设置缓存控制器，默认不超过maxStale秒缓存内的数据，超过的话则重新去 see CacheControl.FORCE_CACHE
         */
        if (mCache && requestType == REQUEST_TYPE_GET) {
            Object stale = params.get(KEY_REQUEST_CACHE);
            int maxStale = mMaxStale;
            if (!CommonUtility.Utility.isNull(stale)) {
                maxStale = (Integer) stale;
            }
            CacheControl.Builder cacheBuilder = new CacheControl.Builder()
                    .onlyIfCached().maxStale(maxStale, TimeUnit.SECONDS);
            builder.cacheControl(cacheBuilder.build());
        }

        CommonUtility.DebugLog.log(url + "?" + pars);

        request = builder.build();

        try {
            Response response = mOKClient.newCall(request).execute();
            String res = removeFirstPrefix(response.body().string());
            if (CommonUtility.Utility.isNull(res) && mCache && requestType == REQUEST_TYPE_GET) {
                if (checkNetwork()) {
                    CommonUtility.DebugLog.log("net..........");
                    builder.cacheControl(CacheControl.FORCE_NETWORK);
                    request = builder.build();
                    response = mOKClient.newCall(request).execute();
                    res = removeFirstPrefix(response.body().string());
                } else {
                    return NET_EXCEPTION_BY_DEVICE;
                }
            }
            CommonUtility.DebugLog.log(res);
            if(res.contains("<html")) {
                return NET_EXCEPTION_BY_SERVER;
            }
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return NET_EXCEPTION_BY_SERVER;
        }
    }


    private RequestBody requestBodyWithContentLength(final RequestBody requestBody) throws IOException {
        final Buffer buffer = new Buffer();
        try {
            requestBody.writeTo(buffer);
        } catch (IOException e) {
            throw new IOException("Unable to copy RequestBody");
        }
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                return buffer.size();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(ByteString.read(buffer.inputStream(), (int) buffer.size()));
            }
        };
    }

    /**
     * method desc： 过滤掉服务器返回数据的特殊字符，这会影响到数据解析
     *
     * @param json
     * @return
     */
    private String removeFirstPrefix(String json) {
        if (!CommonUtility.Utility.isNull(json)) {
            if (json.startsWith(CHARSET_PREFIX)) {
                json = json.substring(1);
            }
        }
        return json;
    }
}
