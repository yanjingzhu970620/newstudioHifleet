package com.hifleet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.common.utility.CommonUtility;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.jsbridge.BridgeHandler;
import com.hifleet.jsbridge.BridgeWebView;
import com.hifleet.jsbridge.CallBackFunction;
import com.hifleet.jsbridge.MyWebViewClient;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.Location;
import com.hifleet.map.OsmAndLocationProvider;
import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;
import com.hifleet.plus.wxapi.WXEntryActivity;
import com.hifleet.utility.XmlParseUtility;
import com.hifleet.widget.ActionSheetDialog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.hifleet.map.PlatformUtil.TAG;

/**
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @{# WeatherShuiwenActivity.java Create on 2015年7月17日 下午1:20:36
 * @description
 */
public class MainwebActivity extends Activity {
    BridgeWebView webview;
    //    TextView textBack,textShipName;
//    ProgressBar progressBar;
//    private String mShipName, mShipImo, mShipLo,url;
    OsmandApplication app;
    private OsmAndLocationProvider locationProvider;
    private String temp_file_path,sharemmsi,uploadmmsi;

    private long exitTime = 0;
    private int REQ_CODE_CAMERA = 0001;
    private int REQUEST_CODE_PICK_IMAGE = 0002;
    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainweb);
        app = this.getMyApplication();
        locationProvider = app.getLocationProvider();


        webview = (BridgeWebView) findViewById(R.id.dwebView);
//        String url = "www.hifleet.com";

        webview.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
//		       //扩大比例的缩放
//		webview.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setLoadWithOverviewMode(true);

        webview.loadUrl("http://www.hifleet.com/app.html?isapp=1");
//        webview.loadUrl("http://192.168.31.62:8081/wap-leaflet/apps.html");
//        http://192.168.31.62:8081/wap-leaflet/apps.html
//            String postdata = "Request=" + mShipImo + "&Selector=IMO_No";
        webview.registerHandler("shareinfo", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                            sharemmsi=data;

                            Intent intent = new Intent(MainwebActivity.this, WXEntryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("sharemmsi",sharemmsi);
                            MainwebActivity.this.startActivity(intent);

//                System.err.println("shareinfo handler = shareinfo");
//                String str = "这是html返回给java的数据:" + data;
//                // 例如你可以对原始数据进行处理
////                makeText(MainActivity.this, str, LENGTH_SHORT).show();
//                 System.err.println("handler = shareinfo"+str);
              //  Log.i(TAG, "handler = shareinfo, data from web = " + data);
//                function.onCallBack(str + ",Java经过处理后截取了一部分：" + str.substring(0, 5));
            }
        });

        webview.registerHandler("uploadpic", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                uploadmmsi=data;
                uploadpic();
            }
        });

        webview.registerHandler("email", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                System.out.println("email:::"+data);
            }
        });

        webview.registerHandler("openqq", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=2931491585&version=1";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
            }
        });
        webview.setWebViewClient(new MyWebViewClient(webview));
        webview.setWebChromeClient(new WebChromeClient() {
            /*
             * (non-Javadoc)
             *
             * @see
             * android.webkit.WebChromeClient#onProgressChanged(android.webkit
             * .WebView, int)
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
//                        progressBar.setVisibility(View.GONE);

                    Location lastKnownLocation = locationProvider.getLastKnownLocation();
                    double last_lat=-1000 ;
                    double last_lon=-1000 ;
                    double Bearing=361 ;
                    if(lastKnownLocation!=null) {
                        last_lat = lastKnownLocation.getLatitude();
                        last_lon = lastKnownLocation.getLongitude();
                        Bearing = lastKnownLocation.getBearing();
                    }
                    if(last_lat!=-1000 && last_lon!=-1000 ) {
                        String loc = last_lon + "," + last_lat + "," + Bearing;
                        webview.callHandler("location", loc, new CallBackFunction() {

                            @Override
                            public void onCallBack(String data) {
                                // TODO Auto-generated method stub
                                Log.i(TAG, "location reponse data from js " + data);
                            }

                        });
                    }
                }

            }

        });
//        webview.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//                return false;
//
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
//                Toast.makeText(getApplicationContext(), "网络连接失败 ,请连接网络。", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//            webview.loadUrl(url);


    }

    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Bundle data = message.getData();
            // System.err.println("dianjile   popwindow  getmessage");
            switch (message.what) {

                case 3:
                    Toast.makeText((Context)MainwebActivity.this, data.getString("upload"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void uploadpic(){

        new ActionSheetDialog(MainwebActivity.this)
                .builder()
                .setTitle("请选择操作")
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {

                            @Override
                            public void onClick(int which) {
                                // TODO Auto-generated method stub
                                Intent i = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                temp_file_path = "/storage/emulated/0/hifleet/tiles"
                                        + UUID.randomUUID().toString()
                                        + ".png";
                                Uri imageUri = Uri.fromFile(new File(
                                        temp_file_path));
                                startActivityForResult(i, REQ_CODE_CAMERA);
                            }
                        })

                .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {

                            @Override
                            public void onClick(int which) {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(
                                        Intent.ACTION_PICK);
                                intent.setType("image/*");// 相片类型
                                startActivityForResult(intent,
                                        REQUEST_CODE_PICK_IMAGE);
                            }
                        }).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (resultCode == IDENTITY.ACTIVITY_CHOOSEFILE_CODE) {
        // final String path = data.getExtras().getString(
        // IDENTITY.IDENTITY_FILEPATH);
        // if (path != null) {
        // new Thread() {
        // public void run() {
        // try {
        // File file;
        // if (path.contains("@@")) {
        // String a[] = path.split("@@");
        // System.out.println("a[]===" + a);
        // for (String p : a) {
        // file = new File(p);
        // System.out.println("file===" + file);
        // uploadFile(file);
        // }
        // } else {
        // file = new File(path);
        // uploadFile(file);
        // }
        // } catch (Exception e) {
        // // TODO: handle exception
        // }
        // };
        // }.start();
        // }
        // System.out.println("path1111===" + path);
        // }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE&&data!=null) {
            Uri uri = data.getData();
            // to do find the path of pic
            String[] proj = { MediaStore.Images.Media.DATA };
            // 好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            // 按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            // 最后根据索引值获取图片路径
            final String path = cursor.getString(column_index);
//			new Thread() {
//				public void run() {
            try {
                File file;
                file = new File(path);
                if(file.exists()){
                    Bitmap bm = revitionImageSize(path);
//							LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainwebActivity.this);
                    View view = LayoutInflater.from(MainwebActivity.this).inflate(R.layout.alert_photo_upload, null);
                    builder.setView(view);
                    ImageView img = (ImageView) view.findViewById(R.id.photo_up);
                    img.setImageBitmap(bm);
                    builder.setTitle("上传照片");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread() {
                                public void run() {
                                    uploadFile(new File(path));
                                };
                            }.start();
                        }
                    });
                    builder.setNegativeButton(R.string.default_buttons_cancel, null);
                    builder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//				};
//			}.start();
            System.out.println(path);
        }
        if (resultCode == RESULT_OK && requestCode == REQ_CODE_CAMERA) {
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            CommonUtility.ImageUtility.storeImage(temp_file_path, mImageBitmap);
            System.out.println("path===" + mImageBitmap);
            if (temp_file_path != null) {
//				new Thread() {
//					public void run() {
                try {
                    File file;
                    file = new File(temp_file_path);
                    if(file.exists()){
                        Bitmap bm = revitionImageSize(temp_file_path);
//								LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainwebActivity.this);
                        View view = LayoutInflater.from(MainwebActivity.this).inflate(R.layout.alert_photo_upload, null);
                        builder.setView(view);
                        ImageView img = (ImageView) view.findViewById(R.id.photo_up);
                        img.setImageBitmap(bm);
                        builder.setTitle("上传照片");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread() {
                                    public void run() {
                                        uploadFile(new File(temp_file_path));
                                    };
                                }.start();
                            }
                        });
                        builder.setNegativeButton(R.string.default_buttons_cancel, null);
                        builder.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//					};
//				}.start();
            }
            System.out.println("path1111===" + temp_file_path);
        }
    }

    public Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 256)
                    && (options.outHeight >> i <= 256)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    /* 上传文件至Server的方法 */
    private void uploadFile(File file) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String httpPost = app.myPreferences.getString("loginserver", null)
                + IndexConstants.UPLOAD_DATA_ACTION + uploadmmsi;
        try {
            URL url = new URL(httpPost);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
			/* 设置传送的method=POST */
            con.setRequestMethod("POST");
			/* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "text/html");
			/* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            // ds.writeBytes(twoHyphens + boundary + end);
            // ds.writeBytes("Content-Disposition: form-data; "
            // + "name=\"file1\";filename=\"" + newName + "\"" + end);
            // ds.writeBytes(end);
			/* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(file);
			/* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
			/* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
            fStream.close();
            ds.flush();
			/* 取得Response内容 */
            InputStream is = con.getInputStream();
            if(is!=null){
                parseReXMLnew(is);
                is.close();
            }
//			int ch;
//			StringBuffer b = new StringBuffer();
//			while ((ch = is.read()) != -1) {
//				b.append((char) ch);
//			}
			/* 将Response显示于Dialog */
			/* 关闭DataOutputStream */
            ds.close();
            System.out.println("上传成功"+photoResultBeans.get(0).flag);

            if(photoResultBeans.size()>0&&photoResultBeans.get(0).flag.equals("1")){
                System.out.println("handler 上传成功");
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("upload", "上传成功");
                msg.setData(data);
                msg.what = 3;
                handler.sendMessage(msg);
            }else{

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("upload", "上传失败");
                msg.setData(data);
                msg.what = 3;
                handler.sendMessage(msg);
            }
//			Toast.makeText((Context)MainwebActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.out.println("上传失败" + e);
//			Toast.makeText((Context)MainwebActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
        }
    }

    List<HeartBeatBean> photoResultBeans=new ArrayList<HeartBeatBean>();
    private void parseReXMLnew(InputStream inStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inStream);
        Element root = document.getDocumentElement();
        photoResultBeans.clear();
        if (root.getNodeName().compareTo("result") == 0) {
            photoResultBeans.add(XmlParseUtility.parse(root, HeartBeatBean.class));
        }
    }

    public OsmandApplication getMyApplication() {
        return ((OsmandApplication) getApplication());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                && app.accessibilityEnabled()) {
//            if (!uiHandler.hasMessages(LONG_KEYPRESS_MSG_ID)) {
//                Message msg = Message.obtain(uiHandler, new Runnable() {
//                    @Override
//                    public void run() {
//                        // app.getLocationProvider().emitNavigationHint();
//                    }
//                });
//                msg.what = LONG_KEYPRESS_MSG_ID;
//                uiHandler.sendMessageDelayed(msg, LONG_KEYPRESS_DELAY);
//            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU
                && event.getRepeatCount() == 0) {

            // 点击菜单按钮，弹出菜单界�?
            // mapActions.openOptionsMenuAsList();// yang chun

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            System.out.println("Map activity 按两次退出。");
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), /* "再按�?���?��程序" */
                        getResources().getString(R.string.tap_twice_to_exit),
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
