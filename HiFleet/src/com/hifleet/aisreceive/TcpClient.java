//package com.hifleet.aisreceive;
//
//import android.graphics.Canvas;
//import android.os.Message;
//
//import com.hifleet.aisparser.GetAisMsg;
//import com.hifleet.bean.ShipsBean;
//import com.hifleet.map.MapActivity;
//import com.hifleet.map.MapActivity_Nav;
//import com.hifleet.map.OsmandApplication;
//import com.hifleet.map.RotatedTileBox;
//import com.hifleet.plus.R;
//
//import java.io.BufferedReader;
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class TcpClient {
//	private static MapActivity_Nav activity;
//	protected OsmandApplication mApplication;
//	public static final int FLUSH = 8001;
//	public static final int ADD = 8002;
//	public static final int ADAPTER = 8003;
//	public static final int ALTER = 8004;
//	public static final int TOAST = 8005;
//	public static final int ADDLIST = 8007;
//	public static final int SAVELIST = 8008;
//
//	static String ip;
//	static int port;
//	// public MainActivity maina = null;
//	public static boolean timerflag = false;
//	public static boolean timerflag1 = true;
//	private static Timer timer = null;
//	private static TimerTask task = null;
//	public static String Msg;
//	long lastrefreshtime = 0;
//	public GetAisMsg getaismsg;
//	public static Map<String, ShipsBean> msgList = new HashMap<String, ShipsBean>();
//	private Client c;
//
//	TcpClient() {
//
//	}
//
//	public TcpClient(String ip, int port, MapActivity_Nav mapactivity) {
//		this.ip = ip;
//		this.port = port;
//		this.activity = mapactivity;
//		mApplication = mapactivity.getMyApplication();
//
//	}
//
//	private void stoptimer() {
//
//		System.out.println("停止main timer");
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
//			System.out.println("启动client");
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
////				char[] passchararray = password.toCharArray();
////				writer = new PrintWriter(client1.getOutputStream());
////				writer.print(passchararray);
////				writer.flush();//写入信息
//				socketReader = new BufferedReader(new InputStreamReader(
//						client1.getInputStream()));
//				runflag = true;
//				System.out.println("连接成功！");
//				return runflag;
//
//			} catch (UnknownHostException ex) {
//				System.out.println("tcpUnknownHostException 连接失败！！");
//				ex.printStackTrace();
//				Message msg = activity.handlertodraw.obtainMessage();
//				msg.arg1 = R.string.unknow_host_exception;
//				activity.handlertodraw.sendMessage(msg);
//				msg.what = TOAST;
//			} catch (SocketException ex) {
//				System.out.println("tcpSocketException连接失败！！");
//				ex.printStackTrace();
//				Message msg = activity.handlertodraw.obtainMessage();
//				msg.arg1 = R.string.socket_exception;
//				msg.what = TOAST;
//				activity.handlertodraw.sendMessage(msg);
//			} catch (IOException ex) {
//				System.out.println("tcpException连接失败！！");
//				ex.printStackTrace();
//				Message msg = activity.handlertodraw.obtainMessage();
//				msg.arg1 = R.string.io_exception;
//				msg.what = TOAST;
//				activity.handlertodraw.sendMessage(msg);
//			} catch (Exception ex) {
//				System.out.println("tcpException连接失败！！");
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
//			getaismsg = new GetAisMsg(msgList);
//			try {
//
//				while (!flag) {
//					try {
//
//						//
//
////						socketReader = new BufferedReader(new InputStreamReader(
////								client1.getInputStream()));
//
//						String c = socketReader.readLine();
//						//System.out.println("readline: "+c);
//						if (c!=null && !syntaxCheck(c))
//							continue;
//
////						Message msg = activity.handlertodraw.obtainMessage();
////						msg.obj = c;
////						msg.what = ADDLIST;
////						activity.handlertodraw.sendMessage(msg);
//						getaismsg.processAisMsg(c);
//
//						Map<String, ShipsBean> msgList2 = getaismsg
//								.getMsgList();
//						if (msgList2.size() > 0 && MapActivity_Nav.inmapactivity) {
//							if (lastrefreshtime == 0) {
//								mApplication.getMapActivity_Nav().getMapView()
//										.refreshMapForceDraw();
//								lastrefreshtime = System.currentTimeMillis();
//							} else if (System.currentTimeMillis()
//									- lastrefreshtime > 5 * 1000) {
//                                MapActivity_Nav.msgList.putAll(msgList2);
//								//在刷新界面上的船舶前，先清理过期数据（12分钟没有更新的）
//                                MapActivity_Nav.clearExpiredMessageData();
//								getaismsg.clearExpiredMessageData();
//
//								Canvas canvas=mApplication.getMapActivity_Nav().getMapLayers().getShipsLayer().canvas;
//								RotatedTileBox tileBox=mApplication.getMapActivity_Nav().getMapLayers().getShipsLayer().tileBox;
//								mApplication.getMapActivity_Nav().getMapLayers().getShipsLayer().getAlertships(canvas, tileBox);
//
//								mApplication.getMapActivity_Nav().getMapView()
//										.refreshMapForceDraw();// 大于5秒才刷新
//								lastrefreshtime = System.currentTimeMillis();
//							}
//						}
//					} catch (IOException ex) {
//						// TODO 读取数据发生异常，应该提示。
//						System.out.println("读取数据异常,暂停5秒重连。");
//						Message msg = activity.handlertodraw.obtainMessage();
//						msg.arg1 = R.string.read_exception;
//						activity.handlertodraw.sendMessage(msg);
//						boolean innerflag = false;
//						try {
//							do {
//								Thread.sleep(5000);
//								innerflag = reConnect();
//							} while (!innerflag && !flag);
//						} catch (Exception ex1) {
//							ex1.printStackTrace();
//						}
//					}
//				}
//
//				if (socketReader != null) {
//					socketReader.close();
//				}
//
//			} catch (IOException ex) {
//				ex.printStackTrace();
//				// TODO 此处也应该有提示。
//			}
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