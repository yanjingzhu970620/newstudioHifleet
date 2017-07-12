package com.hifleet.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hifleet.plus.R;


public class Update {
	private static final int DOWNLOAD = 1;  
    private static final int DOWNLOAD_FINISH = 2;  
    private static final int CONNECT_FAILED = 0;  
    private static final int CONNECT_SUCCESS = 1;  
    HashMap<String, String> mHashMap;  
    private String mSavePath;  
    private int progress;  
    private boolean cancelUpdate = false;  
    private Context mContext;  
    private ProgressBar mProgress;  
    private Dialog mDownloadDialog;  
    private String mXmlPath; // 服务器更新xml存放地址
    
    public Update(Context context, String xmlPath, String savePath) {  
        this.mContext = context;  
        this.mXmlPath = xmlPath;  
        this.mSavePath = savePath;  
    }  
    
    private Handler mHandler = new Handler() {  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case DOWNLOAD:  
                mProgress.setProgress(progress);  
                break;  
            case DOWNLOAD_FINISH:  
                installApk();  
                break;  
            default:  
                break;  
            }  
        };  
    }; 
    
    /**
     * 程序刚开始就执行该函数，检查更新，除非有新版本，不弹出其他的信息。
     */
    public void checkUpdateWithoutNotification(){
        new Thread(new Runnable() { 
            @Override  
            public void run() {  
                try {  
                    URL url = new URL(mXmlPath);  
                    HttpURLConnection conn = (HttpURLConnection) url  
                            .openConnection();  
                    conn.setConnectTimeout(5000);  
                    InputStream inStream = conn.getInputStream();  
                    mHashMap = parseXml(inStream);  
                    Message msg = new Message();  
                    msg.what = CONNECT_SUCCESS;  
                    handlerOnInit.sendMessage(msg);  
                } catch (Exception e) { 
                	System.out.println(mXmlPath);
                	e.printStackTrace();
                    Message msg = new Message();  
                    msg.what = CONNECT_FAILED;  
                    handlerOnInit.sendMessage(msg);  
                }  
            }  
        }).run();
    }
    
    /** 
     * 检查更新 
     */  
    public void checkUpdate() {  
        new Thread(new Runnable() { 
            @Override  
            public void run() {  
                try {  
                    URL url = new URL(mXmlPath);  
                    HttpURLConnection conn = (HttpURLConnection) url  
                            .openConnection();  
                    conn.setConnectTimeout(5000);  
                    InputStream inStream = conn.getInputStream();  
                    mHashMap = parseXml(inStream);  
                    Message msg = new Message();  
                    msg.what = CONNECT_SUCCESS;  
                    handler.sendMessage(msg);  
                } catch (Exception e) {                	
                    Message msg = new Message();  
                    msg.what = CONNECT_FAILED;  
                    handler.sendMessage(msg);  
                }  
            }  
        }).run();  
    }  
    
    /**
     * 初始化时访问服务器更新XML 
     */
    
    Handler handlerOnInit = new Handler(){
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case CONNECT_FAILED:  
//                Toast.makeText(mContext, "访问服务器失败！", Toast.LENGTH_SHORT).show();  
                break;  
            case CONNECT_SUCCESS:  
                if (null != mHashMap) {  
                    int serviceCode = Integer.valueOf(mHashMap.get("version"));  
//                    System.err.println("serviceCode: "+serviceCode+", localCode: "+getVersionCode(mContext));
                    if (serviceCode > getVersionCode(mContext)) {  
                        showNoticeDialog();  
                    } else{
                    	//没有新版本，也要通知一下
//                    	System.err.println("没有新版本。");
                    	//showNoNewVersionDialog();
                    }
                }  
                break;  
            }  
        } 
    };
    /** 
     * 访问服务器更新XML 
     */  
    Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case CONNECT_FAILED:  
                Toast.makeText(mContext, "访问服务器失败！", Toast.LENGTH_SHORT).show();  
                break;  
            case CONNECT_SUCCESS:  
                if (null != mHashMap) {  
                    int serviceCode = Integer.valueOf(mHashMap.get("version"));  
                    System.err.println("serviceCode: "+serviceCode+", localCode: "+getVersionCode(mContext));
                    if (serviceCode > getVersionCode(mContext)) {  
                        showNoticeDialog();  
                    } else{
                    	//没有新版本，也要通知一下
                    	showNoNewVersionDialog();
                    }
                }  
                break;  
            }  
        }  
    };  
    
    /** 
     * 获取程序版本号 
     */  
    private int getVersionCode(Context context) {  
        int versionCode = 0;  
        try {  
            versionCode = context.getPackageManager().getPackageInfo(  
                    mContext.getPackageName(), 0).versionCode;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
        return versionCode;  
    } 
    
    /** 
     * 是否更新提示窗口 
     */  
    private void showNoticeDialog() {  
        AlertDialog.Builder builder = new Builder(mContext);  
        builder.setTitle(R.string.upgrade_notification_dialog_title);  
        builder.setMessage(R.string.find_new_app_version_info);       
        builder.setPositiveButton(R.string.upgrade_butoon,  
                new OnClickListener() {
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                        showDownloadDialog();  
                    }  
                });  
  
        builder.setNegativeButton(R.string.default_buttons_cancel,  
                new OnClickListener() {  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                    }  
                });  
        Dialog noticeDialog = builder.create();  
        noticeDialog.show();  
    } 
    
    private void showNoNewVersionDialog(){
    	AlertDialog.Builder builder = new Builder(mContext);  
    	builder.setTitle(R.string.check_new_version);
    	builder.setMessage(R.string.no_new_version_info);
    	builder.setPositiveButton(R.string.defalut_button_queding, null);
    	Dialog noticeDialog = builder.create(); 
    	noticeDialog.show(); 
    }
    
    /** 
     * 下载等待窗口 
     */  
    private void showDownloadDialog() {  
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  
        builder.setTitle(R.string.downloading_now);  
        final LayoutInflater inflater = LayoutInflater.from(mContext);  
        View v = inflater.inflate(R.layout.softupdate_progress, null);  
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);  
        builder.setView(v);  
        builder.setNegativeButton(R.string.cancle_downloading,  
                new OnClickListener() {  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                        cancelUpdate = true;  
                    }  
                });  
        mDownloadDialog = builder.create();  
        mDownloadDialog.show();  
        downloadApk();  
    }  
    
    private void downloadApk() {  
        new downloadApkThread().start();  
    }  
    
    /** 
     * 下载程序 
     */  
    private class downloadApkThread extends Thread {  
        @Override  
        public void run() {  
            try {  
                if (Environment.getExternalStorageState().equals(  
                        Environment.MEDIA_MOUNTED)) {  
  
                    URL url = new URL(mHashMap.get("url"));  
                    HttpURLConnection conn = (HttpURLConnection) url  
                            .openConnection();  
                    conn.connect();  
                    int length = conn.getContentLength();  
                    InputStream is = conn.getInputStream();  
  
                    File file = new File(mSavePath);  
                    if (!file.exists()) {  
                        file.mkdir();  
                    }  
                    File apkFile = new File(mSavePath, mHashMap.get("name"));  
                    FileOutputStream fos = new FileOutputStream(apkFile);  
                    int count = 0;  
                    byte buf[] = new byte[1024];  
                    do {  
                        int numread = is.read(buf);  
                        count += numread;  
                        progress = (int) (((float) count / length) * 100);  
                        mHandler.sendEmptyMessage(DOWNLOAD);  
                        if (numread <= 0) {  
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);  
                            break;  
                        }  
  
                        fos.write(buf, 0, numread);  
                    } while (!cancelUpdate);  
                    fos.close();  
                    is.close();  
                }  
            } catch (MalformedURLException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
  
            mDownloadDialog.dismiss();  
        }  
    };  
    
    /** 
     * 安装apk 
     */  
    private void installApk() {  
        File apkfile = new File(mSavePath, mHashMap.get("name"));  
        if (!apkfile.exists()) {  
            return;  
        }  
  
        Intent i = new Intent(Intent.ACTION_VIEW);  
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),  
                "application/vnd.android.package-archive");  
        mContext.startActivity(i);  
    } 
    
    private HashMap<String, String> parseXml(InputStream inStream)  
            throws Exception {  
        HashMap<String, String> hashMap = new HashMap<String, String>();  
        // 实例化一个文档构建器工厂  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        // 通过文档构建器工厂获取一个文档构建器  
        DocumentBuilder builder = factory.newDocumentBuilder();  
        // 通过文档通过文档构建器构建一个文档实例  
        Document document = builder.parse(inStream);  
        // 获取XML文件根节点  
        Element root = document.getDocumentElement();  
        // 获得所有子节点  
        NodeList childNodes = root.getChildNodes();  
        for (int j = 0; j < childNodes.getLength(); j++) {  
            // 遍历子节点  
            Node childNode = (Node) childNodes.item(j);  
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {  
                Element childElement = (Element) childNode;  
                // 版本号  
                if ("version".equals(childElement.getNodeName())) {  
                    hashMap.put("version", childElement.getFirstChild()  
                            .getNodeValue());  
                }  
                // 软件名称  
                else if (("name".equals(childElement.getNodeName()))) {  
                    hashMap.put("name", childElement.getFirstChild()  
                            .getNodeValue());  
                }  
                // 下载地址  
                else if (("url".equals(childElement.getNodeName()))) {  
                    hashMap.put("url", childElement.getFirstChild()  
                            .getNodeValue());  
                }  
            }  
        }  
        return hashMap;  
    } 
}
