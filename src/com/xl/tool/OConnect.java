package com.xl.tool;



import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * 影子编写的网络封装库 代替XConnect
 * 采用okhttp封装网络请求 更加稳定
 * 20190702
 *
 * 简单使用例子：
 * 创建get请求
 * OConnect connect = new Oconnect(url,new OConnect.PostGetInfoListener(){});
 * connect.start();
 */
public class OConnect {
    private static final String TAG = "OConnext";
    private String url;
    //private String param;
    PostGetInfoListener listener;
    //链接超时时间
    private final static int CONNENT_TIMEOUT = 2500;
    //读取超时时间
    private final static int READ_TIMEOUT = 12000;
    //ua信息 已废弃
    private static String ua = "Dalvik/1.6.0 (Linux; U; Android 4.4.4; MI 4LTE MIUI/V6.6.2.0.KXDCNCF)";
    private HashMap<String, String> fileMap;
    private HashMap<String, String> postMap;
    private String postInfo;
    private HashMap<String, String> getMap;
    private HashMap<String,String> headMap;
    private boolean isJsonPost;
    private boolean isData;
    Handler handler;


    //创建post请求
    public OConnect(String url, String param, final PostGetInfoListener listener) {
        System.out.println("OConnect url="+url+" param="+param);
        this.postMap = new HashMap<String, String>();
        this.fileMap = new HashMap<String, String>();
        this.getMap = new HashMap<String, String>();
        this.headMap = new HashMap<String, String>();
        this.url = url;
        //this.param=param;
        this.listener = listener;
        if (param != null) {
            String list[] = param.split("&");
            for (String item : list) {
                String ii[] = item.split("=");
                if (ii.length == 2)
                    postMap.put(ii[0], ii[1]);
            }
            this.postInfo = param;
        }
        this.handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                System.out.println("接收数据成功");
                if (msg.what == 1) {
                    if (listener != null)
                        listener.onPostGetText((String) msg.obj);
                }
            }
        };
    }


    //创建get请求
    public OConnect(String url, PostGetInfoListener listener) {
        this(url, null, listener);
    }

    //
    public String getUrl() {
        if (url.indexOf('?') > 0) {
            return url + "&" + getInfo();
        } else
            return url + "?" + getInfo();
    }

    //获取get内容
    public String getInfo() {
        StringBuilder builder = new StringBuilder();
        Iterator iter = getMap.entrySet().iterator();
        boolean isStart = true;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            try {
                builder.append(key + "=" + URLEncoder.encode(val, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ;
            isStart = false;
            if (!isStart) {
                builder.append("&");
            }


        }
        String re = builder.toString();
        if (re.length() != 0)
            re = re.substring(0, re.length() - 1);
        return re;
    }

    //获取post
    public String postInfo() {
        StringBuilder builder = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        Iterator iter = postMap.entrySet().iterator();
        boolean isStart = true;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            /*
            try {
                builder.append(key + "=" + URLEncoder.encode(val, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            */
            //xldebug 后台不能解码。。故修改
            if(!isJsonPost){
                builder.append(key+"="+val);
                isStart = false;
                if (!isStart) {
                    builder.append("&");
                }
            }
            else{
                jsonObject.put(key, val);
            }



        }

        if(!isJsonPost){
            String re = builder.toString();
            re = re.substring(0, re.length() - 1);
            return re;
        }
        else{
            if(isData){
                JSONObject jsonData = new JSONObject();
                jsonData.put("Data", jsonObject);
                return jsonData.toString();
            }
            else
                return jsonObject.toString();
        }

    }

    public void start() {
        boolean isPost = !postMap.isEmpty();
        //1.获取OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNENT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS).build();
        //2.获取Request对象
        final Request.Builder request = new Request.Builder();


        //提交文件
        if (!fileMap.isEmpty()) {
            Iterator iterator = fileMap.entrySet().iterator();
            MultipartBody.Builder bodyBuiler = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                File file = new File(value);

                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                bodyBuiler.addFormDataPart(key, file.getName(), fileBody);
            }
            request.post(bodyBuiler.build());

        }
        else if(postInfo != null){
            String contentType = "application/json; charset=utf-8";
            if(!isJsonPost){
contentType = "application/x-www-form-urlencoded; charset=utf-8";
            }
            RequestBody body_json = RequestBody.create(MediaType.parse(contentType), postInfo);
            request.post(body_json);
        }
        else if (!postMap.isEmpty()) { //处理post
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            RequestBody body_json = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postInfo());
            Iterator iterator = postMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (value == null) continue;
                bodyBuilder.add(key, value);
            }
            request.post(body_json);
            if(isJsonPost){
                request.post(body_json);
            }
            else
                request.post(bodyBuilder.build());
        } else {
            request.get();
        }
        request.url(getUrl());
//设置header
        Iterator iterator = headMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            request.addHeader(key,value);
        }


        //处理get

        //3.将Request封装为Call对象
        Call call = okHttpClient.newCall(request.build());
        //4.执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e);
                Message m = new Message();
                m.what = 1;
                m.obj = e.toString();
                handler.sendMessage(m);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.i(TAG, "onResponse: " + result);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新JavaFX的主线程的代码放在此处
//                        p.cancelProgressBar();
                        if (listener != null)
                            listener.onPostGetText((String)result);
                    }
                });
                Message m = new Message();
                m.what = 1;
                m.obj = result;
                handler.sendMessage(m);
            }
        });

    }

    private void get(String url) {
        //1.获取OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNENT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS).build();
        //2.获取Request对象
        Request request = new Request.Builder().get().url(url).build();
        //3.将Request封装为Call对象
        Call call = okHttpClient.newCall(request);
        //4.执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
            }
        });
    }


    private void post(String url, String format) {
        //1.获取OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造RequestBody
        FormBody body = new FormBody.Builder().add("id", "26").build();
        Request request = new Request.Builder().url(url).post(body).build();
        RequestBody body2 = RequestBody.create(MediaType.parse("text/plain;chaset=utf-8"),
                "轻轻的我走了，\n" +
                        "正如我轻轻的来；\n" +
                        "我轻轻的招手，\n" +
                        "作别西天的云彩。\n" +
                        "\n" +
                        "那河畔的金柳，\n" +
                        "是夕阳中的新娘；\n" +
                        "波光里的艳影，\n" +
                        "在我的心头荡漾。\n");
        //3.将Request封装为Call对象
        Call call = okHttpClient.newCall(request);
        //4.执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e(TAG, "onResponse: " + result);

            }
        });
    }

//    //传输文件
//    private void doPostFile(String url) {
//        File file = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/iv_500x400.png");
//        //1.获取OkHttpClient对象
//        OkHttpClient okHttpClient = new OkHttpClient();
//        //2.构造Request--任意二进制流：application/octet-stream
//        Request request = new Request.Builder()
//                .url(url)
//                .post(RequestBody.create(MediaType.parse("application/octet-stream"), file)).build();
//        //3.将Request封装为Call对象
//        Call call = okHttpClient.newCall(request);
//        //4.执行Call
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "onFailure: " + e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String result = response.body().string();
//                Log.e(TAG, "onResponse: " + result);
////                runOnUiThread(() -> ToastUtil.showAtOnce(MainActivity.this, result));
//            }
//        });
//    }


    //    模拟表单上传文件
//    private void doUpload(String url) {
//        File file = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/iv_500x400.png");
//        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//        //1.获取OkHttpClient对象
//        OkHttpClient okHttpClient = new OkHttpClient();
//        //2.获取Request对象
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("img", "test.jpg", fileBody)
//                .addFormDataPart("test2", "")
//
//                .build();
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody).build();
//        //3.将Request封装为Call对象
//        Call call = okHttpClient.newCall(request);
//        //4.执行Call
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "onFailure: " + e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String result = response.body().string();
//                Log.e(TAG, "onResponse: " + result);
////            runOnUiThread(() -> ToastUtil.showAtOnce(MainActivity.this, result));
//            }
//        });
//    }


    //下载文件
//    private void doDownload(String url) {
//        //1.获取OkHttpClient对象
//        OkHttpClient okHttpClient = new OkHttpClient();
//        //2.获取Request对象
//        Request request = new Request.Builder().get().url(url).build();
//        //3.将Request封装为Call对象
//        Call call = okHttpClient.newCall(request);
//        //4.执行Call
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "onFailure: " + e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                File file = new File(Environment.getExternalStorageDirectory(), "download.jpg");
//                InputStream is = response.body().byteStream();
//                FileOutputStream fos = new FileOutputStream(file);
//                byte[] buf = new byte[102];
//                int len = 0;
//                while ((len = is.read(buf)) != -1) {
//                    fos.write(buf, 0, len);
//                }
//                fos.close();
//                is.close();
//            }
//        });
//    }

    //添加一个post参数
    public void addPostParmeter(String key, String value) {
        postMap.put(key,value);
    }

    //    添加一个int类型的post参数
    public void addPostParmeter(String key,int value){
        postMap.put(key,""+value);
    }

    //    添加一个get参数
    public void addGetParmeter(String key,String value){
        getMap.put(key,value);
    }

    //    添加一个int类型的get参数
    public void addGetParmeter(String key,int value){
        getMap.put(key,""+value);
    }

    //    添加一个文件
    public void addPostParmeter(String name,File file){
        fileMap.put(name,file.getPath());
    }

    //    添加一个文件上传
    public void addFileParmeter(String name, String value){
        fileMap.put(name,value);
    }

    //    添加一个header头
    public void addHeader(String name,String value){
        headMap.put(name,value);
    }

    /*
    监听器
    */
    public interface PostGetInfoListener {
        public void onPostGetText(String text);
    }

    public void setJSONPost(boolean selected,boolean isData) {
        // TODO Auto-generated method stub
        this.isJsonPost = selected;
        this.isData = isData;
    }

}
