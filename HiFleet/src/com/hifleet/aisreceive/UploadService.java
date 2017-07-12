//package com.hifleet.aisreceive;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import com.hifleet.activities.MapActivity;
//import com.hifleet.app.OsmandApplication;
//import com.hifleet.app.OsmandSettings;
//
//import android.app.Service;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//public class UploadService extends Service {
//    private static final String TAG = "UploadService";
//    private OsmandSettings settings;
//    private OsmandApplication app;
////    MediaPlayer player;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
////        Toast.makeText(this, "My Service created", Toast.LENGTH_LONG).show();
////        Log.i(TAG, "onCreate uploadservice");
//
//        app = (OsmandApplication) getApplication();
//		settings = app.getSettings();
//
//        setTimerTask();
////        player = MediaPlayer.create(this, R.raw.braincandy);
////        player.setLooping(false);
//    }
//
//    @Override
//    public void onDestroy() {
//        Toast.makeText(this, "上传位置服务停止", Toast.LENGTH_LONG).show();
////        Log.i(TAG, "onDestroy");
////        player.stop();
//    }
//
//    @Override
//    public void onStart(Intent intent, int startid) {
////        Toast.makeText(this, "My Service Start", Toast.LENGTH_LONG).show();
////        Log.i(TAG, "onStart");
////        player.start();
//    }
//
//     private final Timer timerupload = new Timer();
//	 private TimerTask timeruploadtask;
//	 Handler uploadhandler = new Handler() {
//	     @Override
//	     public void handleMessage(Message msg) {
//	         // TODO Auto-generated method stub
//	         // 要做的事情
//	         super.handleMessage(msg);
//	         System.err.println("service handleMessage");
//	         String mmsi=settings.SHIP_MMSI.get();
//	 			String name=settings.SHIP_NAME.get();
//	 			String length=settings.SHIP_LENGTH.get();
//	 			String width=settings.SHIP_WIDTH.get();
//	 			String type=settings.SHIP_TYPE.get();
//	 			if(app!=null&&app.getMapActivity()!=null&&app.getMapActivity().lon!=null&&app.getMapActivity().lat!=null
//	 					&&app.getMapActivity().speed!=null&&app.getMapActivity().course!=null){
//	 			String lon=app.getMapActivity().lon;
//	 			String lat=app.getMapActivity().lat;
//	 			String speed=app.getMapActivity().speed;
//	 			String co=app.getMapActivity().course;
//
//	 			String con="&NSRVAIS,"+mmsi+","+name+","+lon+","+lat+","+speed+","+co+","+length+","+width+","+type
////	 					+sdf.format(new Date(System.currentTimeMillis()))
//	 					+"*END";
//
//	 			System.err.println("upload con"+con+" type "+type);
//	 			if(lon!=null&&lat!=null&&speed!=null&&co!=null){
//	 			TcpUpload tcpup=new TcpUpload("61.172.245.251",5188,con,app.getMapActivity());
//	 			tcpup.closeclient();
//	 			tcpup.start();
//	 			}
//	 			}else{System.err.println("lat lon null");}
//	     }
//	 };
//
//	 private void setTimerTask(){
//
//	 timeruploadtask = new TimerTask() {
//		    @Override
//		    public void run() {
//		        // TODO Auto-generated method stub
//		        Message message = new Message();
//		        message.what = 1;
//		        uploadhandler.sendMessage(message);
//		    }
//		};
//	 timerupload.schedule(timeruploadtask, 1000, 30*1000);
//	 }
//}