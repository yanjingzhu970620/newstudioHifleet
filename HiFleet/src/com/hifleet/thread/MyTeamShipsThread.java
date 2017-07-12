package com.hifleet.thread;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.lnfo.layer.ShipsInfoLayer;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.LatLon;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.QuadRect;
import com.hifleet.map.RotatedTileBox;
import com.hifleet.utility.Cell;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# LoginHandler.java Create on 2015年3月30日 下午1:52:39
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class MyTeamShipsThread extends AsyncTask<String, Void, String> {

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";
//	public static ArrayList<ShipsBean> tempshipsBeans = new ArrayList<ShipsBean>();
//	public static ArrayList<ShipsBean> shipsBeans = new ArrayList<ShipsBean>();
//	public static ArrayList<ShipsBean> currentshipsBeans = new ArrayList<ShipsBean>();// 当前屏幕内的船。
//	public static ArrayList<ShipsBean> currentLableshipsBeans = new ArrayList<ShipsBean>();// 当前屏幕内选取较少的船。
	
	public static HashMap<String, ShipsBean> tempshipsBeans = new HashMap<String, ShipsBean>();
	public static HashMap<String, ShipsBean> shipsBeans = new HashMap<String, ShipsBean>();
	public static HashMap<String, ShipsBean> currentshipsBeans = new HashMap<String, ShipsBean>();// 当前屏幕内的船。
	public static HashMap<String, ShipsBean> currentLableshipsBeans = new HashMap<String, ShipsBean>();// 当前屏幕内选取较少的船。
																							// 暂未用到
	public static List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();

	private static HashMap<String, Cell> _cellHashMap = new HashMap<String, Cell>();// 保存cellid和cell的对应关�?
	private static HashMap<String, String> _shipCellMap = new HashMap<String, String>();// 保存mmsi和该船所在的cell的对应关�?
	
//	public static ArrayList<String> teamgroup = new ArrayList<String>();;
	private static HashMap<String, String> teamgroup = new HashMap<String, String>();
	/**
	 * @return the teamgroup
	 */
	public static HashMap<String, String> getTeamgroup() {
		return teamgroup;
	}

	/**
	 * @param teamgroup the teamgroup to set
	 */
	public static void setTeamgroup(HashMap<String, String> teamgroup) {
		MyTeamShipsThread.teamgroup = teamgroup;
	}

	/**
	 * @return the teammmsigroup
	 */
	public static HashMap<String, String> getTeammmsigroup() {
		return teammmsigroup;
	}

	/**
	 * @param teammmsigroup the teammmsigroup to set
	 */
	public static void setTeammmsigroup(HashMap<String, String> teammmsigroup) {
		MyTeamShipsThread.teammmsigroup = teammmsigroup;
	}

	private static HashMap<String, String> teammmsigroup = new HashMap<String, String>();
	
    OsmandApplication app;
	public static boolean flag = true;
	public static boolean teamshipfirst = true;// 初始化 是否是第一次加载。

	public static boolean teamfresh = false;// 船队加载完毕是否需要刷新。

	protected void onPreExecute() {

	}

	protected void onProgressUpdate(String... values) {

	}

	protected void updateProgress(boolean updateOnlyProgress) {

	}

	private static Cell getCellIndex(double lon, double lat) {
		double _x = Math.floor((lon + 180) * 10);
		double _y = Math.floor((lat + 90) * 10);
		return new Cell(_x, _y);
	}

	public static void deleAllCurrentCellShips() {
		for (String cellkey : _cellHashMap.keySet()) {
			if (_cellHashMap.containsKey(cellkey)) {
				(_cellHashMap.get(cellkey)).clearMMSITeamShipInfoMap();
			}
		}
	}

	// 将当前窗口所有cells里面的船舶删�?
	public static void deleAllCurrentWindowCellShips(RotatedTileBox tileBox) {
		QuadRect rect = tileBox.getLatLonBounds();
		LatLon p3 = new LatLon(rect.top, rect.right);
		LatLon p1 = new LatLon(rect.bottom, rect.left);

		Cell blIndex = getCellIndex(p1.getLongitude(), p1.getLatitude());
		Cell trIndex = getCellIndex(p3.getLongitude(), p3.getLatitude());

		double rd = 0;

		for (double i = blIndex.get_x(); i <= trIndex.get_x() + rd; i++) {
			for (double j = blIndex.get_y(); j <= trIndex.get_y() + rd; j++) {
				String cellkey = "x" + i + "y" + j;
				if (_cellHashMap.containsKey(cellkey)) {
					(_cellHashMap.get(cellkey)).clearMMSIShipInfoMap();
				} else {
					continue;
				}
			}
		}
	}
	
	public static void addLableCellShips() {
		currentLableshipsBeans.clear();
		QuadRect rect = ShipsInfoLayer.tileBox.getLatLonBounds();
		LatLon p3 = new LatLon(rect.top, rect.right);
		LatLon p1 = new LatLon(rect.bottom, rect.left);

		Cell blIndex = getCellIndex(p1.getLongitude(), p1.getLatitude());
		Cell trIndex = getCellIndex(p3.getLongitude(), p3.getLatitude());
		// System.out.println(blIndex.get_x()+" "+blIndex.get_y()+"----�?"+trIndex.get_x()+" "+trIndex.get_y());
		double rd = 0;

		int celling = 1;

		// if (ShipsInfoLayer.tileBox.getZoom() <= 11) {
		// celling = 4;
		// } else if (ShipsInfoLayer.tileBox.getZoom() >= 12 &&
		// ShipsInfoLayer.tileBox.getZoom() < 13) {
		// celling = 10;
		// } else if (ShipsInfoLayer.tileBox.getZoom() >= 13) {
		// celling = 100;
		// }

		// int cellcount=0;

		for (double i = blIndex.get_x(); i <= trIndex.get_x() + rd; i++) {
			for (double j = blIndex.get_y(); j <= trIndex.get_y() + rd; j++) {
				String cellkey = "x" + i + "y" + j;
				// cellcount++;
				if (_cellHashMap.containsKey(cellkey)) {
					addLableShipsLimited(_cellHashMap.get(cellkey), celling);

				}
			}
			// System.out.println("第一层for结束�?");
		}
		// print("cell数目："+cellcount);

	}

	public static void addAllCellShips() {
		currentshipsBeans.clear();
		QuadRect rect = ShipsInfoLayer.tileBox.getLatLonBounds();
		LatLon p3 = new LatLon(rect.top, rect.right);
		LatLon p1 = new LatLon(rect.bottom, rect.left);

		Cell blIndex = getCellIndex(p1.getLongitude(), p1.getLatitude());
		Cell trIndex = getCellIndex(p3.getLongitude(), p3.getLatitude());
		// System.out.println(blIndex.get_x()+" "+blIndex.get_y()+"----�?"+trIndex.get_x()+" "+trIndex.get_y());
		double rd = 0;

		int celling = 100;

		// if (ShipsInfoLayer.tileBox.getZoom() <= 11) {
		// celling = 4;
		// } else if (ShipsInfoLayer.tileBox.getZoom() >= 12 &&
		// ShipsInfoLayer.tileBox.getZoom() < 13) {
		// celling = 10;
		// } else if (ShipsInfoLayer.tileBox.getZoom() >= 13) {
		// celling = 100;
		// }

		// int cellcount=0;

		for (double i = blIndex.get_x(); i <= trIndex.get_x() + rd; i++) {
			for (double j = blIndex.get_y(); j <= trIndex.get_y() + rd; j++) {
				String cellkey = "x" + i + "y" + j;
				// cellcount++;
				if (_cellHashMap.containsKey(cellkey)) {
					addCellShipsLimited(_cellHashMap.get(cellkey), celling);

				}
			}
			// System.out.println("第一层for结束�?");
		}
		// ShipsInfoLayer.teamCallLable(currentshipsBeans);
		// print("cell数目："+cellcount);
	}

	private static void addCellShipsLimited(Cell cell, int celling) {

		HashMap<String, ShipsBean> iMMSIHashMap = cell.mmsiTeamShipInfo();

		if (iMMSIHashMap != null) {

			try {
				int ceilingInner = celling > iMMSIHashMap.size() ? iMMSIHashMap
						.keySet().toArray().length : celling;

//				 print("ceilingInner: "+ceilingInner+" iMMSIHashMap"+iMMSIHashMap
//							.size()+" celling"+celling);

				String keysarray[] = (String[]) iMMSIHashMap.keySet().toArray(
						new String[0]); //

				for (int i = 0; i < ceilingInner; i++) {
					String key = keysarray[i];
					ShipsBean ship = iMMSIHashMap.get(key);
//					 System.out.println("添加到了显示队列！： "+ship.getM()+currentshipsBeans.size());
					currentshipsBeans.put(ship.getM(), ship);
				}
			} catch (java.util.ConcurrentModificationException ex) {
				System.out.println("ConcurrentModificationException shipsinfo");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		// System.out.println("待显示队列： "+_ships2Draw.size());
	}// zoom较小情况下缓慢的原因

	private static void addLableShipsLimited(Cell cell, int celling) {

		HashMap<String, ShipsBean> iMMSIHashMap = cell.mmsiTeamShipInfo();

		if (iMMSIHashMap != null) {

			try {
				int ceilingInner = celling > iMMSIHashMap.size() ? iMMSIHashMap
						.size() : celling;

				// print("ceilingInner: "+ceilingInner);

				String keysarray[] = (String[]) iMMSIHashMap.keySet().toArray(
						new String[0]); //

				for (int i = 0; i < ceilingInner; i++) {
					String key = keysarray[i];
					ShipsBean ship = iMMSIHashMap.get(key);
					// System.out.println("添加到了显示队列！： "+ship.getM());
					currentLableshipsBeans.put(ship.getM(), ship);
				}
			} catch (java.util.ConcurrentModificationException ex) {
				System.out.println("ConcurrentModificationException shipsinfo");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		// System.out.println("待显示队列： "+_ships2Draw.size());
	}// 得到需要标签的船舶。暂时没使用

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */

	protected void onPostExecute(String result) {

		for (HeartBeatBean h : heartBeatBean) {
			if (Integer.valueOf(h.flag).intValue() == 0) {
				Message message = new Message();
				message.obj = 33;
				message.what = 44;
				MapActivity.teamShipsHandler.sendMessage(message);
//				System.out.println("看看问题是不是出在这里=====" + h.message);
			}
		}
		// print("船队船舶数量" + shipsBeans.size());
		// ShipLableLayer.lablePost = true;
		// ShipsInfoLayer.clearLayer();
	}

	protected String doInBackground(String... arg0) {

		// TODO Auto-generated method stub
		try {
			String httpPost = app.myPreferences.getString("loginserver", null)
					+ IndexConstants.GET_MY_TEAM_URL;
			 System.out.println("看看teamURL===" + httpPost);
			URL shipsUrl = new URL(httpPost);
			if (this.isCancelled()) {
				return null;
			}
			HttpURLConnection conn = (HttpURLConnection) shipsUrl
					.openConnection();
			// System.out.print("myteam session"+app.myPreferences.getString("sessionid",
			// ""));
			try {
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
			} catch (java.lang.RuntimeException re) {
				re.printStackTrace();
				// System.out.println("RuntimeException myteam doinbackground");
				return null;
			}
			conn.setConnectTimeout(10000);
			if (this.isCancelled()) {
				return null;
			}
			InputStream inStream = conn.getInputStream();
			if (this.isCancelled()) {
				inStream.close();
				return null;
			}
			parseXMLnew(inStream);
			if (this.isCancelled()) {
				return null;
			}
			inStream.close();
//			print("准备执行异步刷新船队");
//			if(teamshipfirst){
//			teamshipfirst = false;
//			}
			// teamfresh = true;
			// ShipLableLayer.teamlable = true;
			ShipsInfoLayer.callbuffer(teamshipfirst);
			
			if(teamshipfirst){
				teamshipfirst = false;
				}
//			ShipsInfoLayer.lableadd = app.myPreferences.getBoolean(
//					"isShowMyTeamName", true);
			// currentshipsBeans.clear();
			// ShipsInfoLayer.clearLayer();

			// ShipsInfoLayer.callbuffer();
			// ShipsInfoLayer.addFleetVesselsInCurrentWindow(currentshipsBeans,
			// MyTeamShipsThread.shipsBeans);
		} catch (Exception e) {
			// TODO: handle exception
			// System.out.println("未能获取网络数据");
			e.printStackTrace();
		}

		return null;
	}

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		
		if (tempshipsBeans != null) {
			tempshipsBeans = new HashMap<String, ShipsBean>();
		}
		
		heartBeatBean.clear();
		if (root.getNodeName().compareTo("session__timeout") == 0) {
//			System.err.println("session__timeoutsession__timeout  heartBeatBean add");
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}

		int id=0;
		HashMap<String, String> ttg = new HashMap<String, String>();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String groupname = "缺省分组";
				String groupcolor= "N/A";
				if (childElement.getNodeName().compareTo("fleet") == 0) {
//					tempshipsBeans.add(XmlParseUtility.parse(childElement,
//							ShipsBean.class));
					
					groupname=childElement.getAttribute("groupname");
					groupcolor=childElement.getAttribute("groupcolor");
					if(groupcolor.equals("N/A")||groupcolor.equals("-")){
						groupcolor=app.colorarray[id];
					}
//					System.err.println(" parse xml groupname:"+groupname);
					ttg.put(groupname, groupcolor);
					if(id<=app.colorarray.length){
					id++;
					}
					
				}
				
				NodeList childNodes1 = childElement.getChildNodes();
				for (int k = 0; k < childNodes1.getLength(); k++) {
					Node childNode1 = (Node) childNodes1.item(k);
					if (childNode1.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement1 = (Element) childNode1;
						
						NodeList childNodes2 = childElement1.getChildNodes();
						for (int p = 0; p < childNodes2.getLength(); p++) {
							Node childNode2 = (Node) childNodes2.item(p);
							if (childNode2.getNodeType() == Node.ELEMENT_NODE) {
								Element childElement2 = (Element) childNode2;
								if (childElement2.getNodeName().compareTo("ship") == 0) {
//									System.err.println(" parse xml :"+tempshipsBeans.size());
									ShipsBean s=XmlParseUtility.parse(childElement2,
											ShipsBean.class);
									
										// boolean h=false;
										SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											if (tempshipsBeans.containsKey(s.getM())) {
												// h=true;
												Date ti = df.parse(tempshipsBeans.get(s.getM()).ti);
												Date ts = df.parse(s.ti);
												if (ti.getTime() < ts.getTime()) {
//													array[i]=i;
													// System.out.println("删除"+shipsBeans.get(i).m+"save:"+shipsBeans.get(j).m+"ti小 "+ti.getTime()+"tj"+tj.getTime());
												} else if (ti.getTime() > ts.getTime()) {
//													array[i]=j;
													tempshipsBeans.remove(s.getM());
													tempshipsBeans.put(s.getM(), s);
//													tempshipsBeans.remove(j);
													// System.out.println("删除"+shipsBeans.get(j).m+"保留"+shipsBeans.get(i).m+"ti "+ti.getTime()+"tj小"+tj.getTime());
												} else if (ti.getTime() == ts.getTime()) {
//													array[i]=j;
//													tempshipsBeans.remove(j);
												}
												
											}else{tempshipsBeans.put(s.getM(), s);}
										// if(h==false){
										// shipsBeans.add(tempshipsBeans.get(i));
										// }
									}// 选最近时间的
									
									String m=childElement2.getAttribute("m");
									teammmsigroup.put(m, groupname);
								}
							}
							}
					}
					}
			}
		teamgroup.putAll(ttg);
		
//		 print("teamgroup数量" + teamgroup.size());
		// int i=0;
		// for (ShipsBean bean : shipsBeans) {
		// i++;
		// System.out.println("mmsi" + bean.m+"这是第"+i);
		// }
		// print("船队船舶筛选前的数量" + shipsBeans.size());
//		int[] array=new int[tempshipsBeans.size()*2];//扩大容量  防止报错
//		for (int i = 0; i < tempshipsBeans.size(); i++) {
//			// boolean h=false;
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//			for (int j = 0; j < tempshipsBeans.size(); j++) {
//				if (tempshipsBeans.get(i).m.equals(tempshipsBeans.get(j).m)) {
//					// h=true;
//					Date ti = df.parse(tempshipsBeans.get(i).ti.substring(0,
//							tempshipsBeans.get(i).ti.length()));
//					Date tj = df.parse(tempshipsBeans.get(j).ti.substring(0,
//							tempshipsBeans.get(j).ti.length()));
//					if (ti.getTime() < tj.getTime()) {
//						array[i]=i;
////						tempshipsBeans.remove(i);
//						// System.out.println("删除"+shipsBeans.get(i).m+"save:"+shipsBeans.get(j).m+"ti小 "+ti.getTime()+"tj"+tj.getTime());
//					} else if (ti.getTime() > tj.getTime()) {
//						array[i]=j;
////						tempshipsBeans.remove(j);
//						// System.out.println("删除"+shipsBeans.get(j).m+"保留"+shipsBeans.get(i).m+"ti "+ti.getTime()+"tj小"+tj.getTime());
//					} else if (ti.getTime() == tj.getTime() && i != j) {
//						array[i]=j;
////						tempshipsBeans.remove(j);
//					}
//					
//					break;
//				}
//			}
//			// if(h==false){
//			// shipsBeans.add(tempshipsBeans.get(i));
//			// }
//		}// 选最近时间的
//		if(array.length>0&&tempshipsBeans.size()>0){
//		for(int i=0;i<array.length;i++){
//			tempshipsBeans.remove(array[array.length-i-1]);
//		}
//		}
			// print("船队船舶筛选的数量" + shipsBeans.size());
		shipsBeans= new HashMap<String, ShipsBean>();
//		for (int i = 0; i < tempshipsBeans.size(); i++) {
//			shipsBeans.put(tempshipsBeans.get(i).getM(), tempshipsBeans.get(i));
//		}
		shipsBeans.putAll(tempshipsBeans);
		deleAllCurrentCellShips();
		updateCellships(shipsBeans);
		
	}

	public static void updateCellships(HashMap<String, ShipsBean> shipsBeans){
//		for (ShipsBean ship : shipsBeans) {
			Iterator<Entry<String, ShipsBean>> ite = shipsBeans.entrySet().iterator();
			// System.out.println("MMSI: " + ship.m);
			// System.out.println("航向:" + "======" + ship.co);

			while(ite.hasNext()){
				Map.Entry entry = (Map.Entry) ite.next();
				String key=entry.getKey().toString();
				ShipsBean value=shipsBeans.get(key);
			Cell c = getCellIndex(value.getLo(), value.getLa());// null
//			System.out.println("MMSI: " + value.m);
			if (_shipCellMap.containsKey(value.getM())) {
				// 如果shipcellmap中包括该船舶的mmsi，先删除在该cell中船舶队列中该船舶的信息
				Cell ccell = _cellHashMap.get(_shipCellMap.get(value.getM()));
				ccell.mmsiTeamShipInfo().remove(value.getM());
			}

			if (!_cellHashMap.containsKey(c.get_id())) {
				// 如果cellmap队列中没有这个cell，那么要把这个cell添加到cellmap进去�?
				// 同时把该船舶也添加到这个cell中船舶队列中去�??
				c.mmsiTeamShipInfo().put(value.getM(), value);
				_cellHashMap.put(c.get_id(), c);
			} else {
				// cellmap中有这个cell了，那么要更新这个cell中船舶队列中该船舶的信息�?
				_cellHashMap.get(c.get_id()).mmsiTeamShipInfo()
						.put(value.getM(), value);
			}

			// 更新ship-cell 索引
			_shipCellMap.put(value.getM(), c.get_id());
		}
	}
	public MyTeamShipsThread(OsmandApplication app) {
		// long l=System.currentTimeMillis();
		// System.out.println("启动船队刷新线程");
		// System.out.println("启动船队刷新线程     传参数完成"+(System.currentTimeMillis()-l));
          this.app=app;
	}
}
