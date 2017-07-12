//package com.hifleet.aisreceive;
//
//import java.io.*;
//import java.net.*;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.logging.Logger;
//
//import com.hifleeteasynavigetion.ais.R;
//import com.hifleet.activities.MapActivity;
//import com.hifleet.aisparser.GetAisMsg;
//import com.hifleet.app.OsmandApplication;
//import com.hifleet.base.AccessibleToast;
//import com.hifleet.bean.ShipsBean;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//public class TcpUpload {
//	private static MapActivity activity;
//	protected OsmandApplication mApplication;
//	public static final int FLUSH = 8001;
//	public static final int ADD = 8002;
//	public static final int ADAPTER = 8003;
//	public static final int ALTER = 8004;
//	public static final int TOAST = 8005;
//	public static final int ADDLIST = 8007;
//	public static final int SAVELIST = 8008;
//	static String ip;
//	static int port;
//	String con;
//	// public MainActivity maina = null;
//	public static boolean timerflag = false;
//	public static boolean timerflag1 = true;
//	private static Timer timer = null;
//	private static TimerTask task = null;
//	public static String Msg;
//	long lastrefreshtime = 0;
//	public static Map<String, ShipsBean> msgList = new HashMap<String, ShipsBean>();
//	private Client c;
//
//	TcpUpload() {
//
//	}
//
//	public TcpUpload(String ip, int port,String con, MapActivity mapactivity) {
//		if(mapactivity==null){
//			return;
//		}
//		this.ip = ip;
//		this.port = port;
//		this.con=con;
//		this.activity = mapactivity;
//		mApplication = mapactivity.getMyApplication();
//
//	}
//
//	private void stoptimer() {
//
////		System.out.println("停止main timer");
//		if (timer != null) {
//			timer.cancel();
//			timer = null;
//		}
//
//		if (task != null) {
//			task.cancel();
//			task = null;
//		}
//	}
//
//	public void closeclient() {
////		System.out.println("启动tcpupload closeclient");
//		stoptimer();
//		if (c != null) {
//			c.setTheadStopRunFlag(true);
//			c.closeClientSocket();
//		}
//	}// 关闭client
//
//	public void start() {
//		c = new Client();
//		if (c.init()) {
//			c.start();
////			System.out.println("启动tcpupload client");
//		}else{
//			closeclient();
//		}
//	}
//
//	private class Client extends Thread {
//
//		private Socket client1 = null;
//		private OutputStream os = null;
//
//		private DataInputStream input = null;
//		private boolean flag = false, runflag = false;
//
//		PrintWriter writer = null;
////		String user = "";
////		String password = "2332302067";
//		BufferedReader socketReader = null;
//
//		public void setTheadStopRunFlag(boolean f) {
//			flag = f;
//		}
//
//
//		public void closeClientSocket() {
//			runflag = false;
//			if (writer != null) {
//				writer.close();
//				writer = null;
//			}
//			if (socketReader != null) {
//				try {
//					socketReader.close();
//					socketReader = null;
//				} catch (Exception ex) {
//
//			}
//		}
//			if(client1!=null){
//				try {
//					client1.close();
//					client1=null;
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
////			if(writer==null&&socketReader==null&&client1==null){
////				System.out.println("close tcpup success");
////			}
//	}
//
//		public Client() {
//		}
//
//		public boolean init() {
//			try {
//				client1 = new Socket(ip, port);
//				client1.setSoTimeout(1 * 10 * 1000);// 1 minutes time out.
//				client1.setTcpNoDelay(true);
//
//				//socketReader = new BufferedReader(new InputStreamReader(
//				//		client1.getInputStream()));
//				runflag = true;
////				System.out.println("upload 连接成功！");
//				return runflag;
//
//			} catch (UnknownHostException ex) {
//				ex.printStackTrace();
//				Message msg = activity.handlertodraw.obtainMessage();
//				msg.arg1 = R.string.unknow_host_exception;
//				msg.what = TOAST;
//				activity.handlertodraw.sendMessage(msg);
//			} catch (SocketException ex) {
//				ex.printStackTrace();
//				Message msg = activity.handlertodraw.obtainMessage();
//				msg.arg1 = R.string.socket_exception;
//				msg.what = TOAST;
//				activity.handlertodraw.sendMessage(msg);
//
//				Message msg1 = activity.handlertodraw.obtainMessage();
//				msg1.obj = con;
//				msg1.what = ADDLIST;
//				activity.handlertodraw.sendMessage(msg1);
//			} catch (IOException ex) {
//				ex.printStackTrace();
//				Message msg = activity.handlertodraw.obtainMessage();
//				msg.arg1 = R.string.io_exception;
//				msg.what = TOAST;
//				activity.handlertodraw.sendMessage(msg);
//			} catch (Exception ex) {
//				ex.printStackTrace();
//				Message msg = activity.handlertodraw.obtainMessage();
//				msg.arg1 = R.string.other_exception;
//				msg.what = TOAST;
//				activity.handlertodraw.sendMessage(msg);
//			}
//			System.out.println("连接失败！！");
//			return runflag;
//
//		}
//
//		public void run() {
//
//			if (!runflag) {
//				Message msg = activity.handlertodraw.obtainMessage();
//				msg.arg1 = R.string.false_flag;
//				msg.what = TOAST;
//				activity.handlertodraw.sendMessage(msg);
//				return;
//			}
//
//			try {
//			writer = new PrintWriter(client1.getOutputStream());
//			writer.print(con+"\r\n");
//			writer.flush();//写入信息
//			System.out.println("tcpupload writer success"+ip+port+":::"+con+"\r\n");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			closeclient();
//		}
//
//		private boolean reConnect() {
//			closeClientSocket();
//			return init();
//		}
//
//	}
//
//
//	private static String getStringFromInputStream(InputStream a_is) {
//		BufferedReader br = null;
//		StringBuilder sb = new StringBuilder();
//		String line;
//		try {
//			br = new BufferedReader(new InputStreamReader(a_is));
//			while ((line = br.readLine()) != null) {
//				sb.append(line + ";");
//			}
//		} catch (IOException e) {
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return sb.toString();
//	}// 解析txt文档。
//
//	private boolean syntaxCheck(String nmea) {
//		return nmea
//				.matches("^!A[B,I]VD[M,O],[0-9]+,[0-9]+,[0-9]*,[A,B,1,2]{0,1},[0-9,a-z,A-Z,\\:,\\;,\\<,\\>,\\=,\\?,\\@,\\`]{1,82},[0-9]\\*[0-9,A-F]{2}");
//	}
//
//	public static long getTimel() {
//		return (new Date()).getTime();
//	}
//
//	public static String getChangeTime(long timel) {
//		SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return sfd.format(timel);
//	}
//
//	public static void Tips(int cmd, Object str) {
//		Message m = new Message();
//		m.what = cmd;
//		m.obj = str;
//		activity.handlertodraw.sendMessage(m);
//	}
//}