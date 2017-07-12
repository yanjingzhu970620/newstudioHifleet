//package com.hifleet.aisparser;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//
//
//import com.hifleet.aisparser.*;
//import com.hifleet.bean.ShipsBean;
//
//
///**
// * !ABVDM,1,1,2,B,169<pP0P1W8bChLF3DEcwwv00<0w,0*1F
// * !ABVDM,1,1,1,B,16TqoL00268WPBPDUk44>3@00H0@,0*38
// * !ABVDM,1,1,4,A,16TSMn0P008RiERCp?eq7JIn0@16,0*7B
// * ����������1����Ϣ��169<pP0P1W8bChLF3DEcwwv00<0w�ĵ�һ���ַ���1,����1����Ϣ
// *
// * ����������Ϣ��5����Ϣ������Ӧ��������һ������������5����Ϣ
//	!ABVDM,2,1,9,A,55?54H01i07O<HmKN20mJ0E@tThF222222222216M0U<K78o0DDBCQi0,0*42
//	!ABVDM,2,2,9,A,Cp8888888888880,2*15
//
//
//	����������Ϣ��18����Ϣ��B6:AJtP0:B4mBW3@L6<acwt79P06�ĵ�һ���ַ���B��
//!ABVDM,1,1,,B,B6:AJtP0:B4mBW3@L6<acwt79P06,0*27
// *
// * @author yangchun
// *
// */
//public class GetAisMsg {
//	public static boolean myship;
//   	 static Map<String, ShipsBean> msgList=new HashMap<String, ShipsBean>();
//   	 public Map<String, String> nameList=new HashMap<String, String>();
//
//   	 static Map<String,ShipsBean> no5msgList = new HashMap<String,ShipsBean>();
//   	Map<ID, String> incompleteMessages = new HashMap<ID, String>();
////   	static ArrayList<String> shipnames=new ArrayList<String>();
//	static  ShipsBean ships=new ShipsBean();//存储获得的船舶。
//
//	public GetAisMsg(Map<String, ShipsBean> msgList){
//		this.msgList.putAll(msgList);
//	}
//	private static SimpleDateFormat sdf = new SimpleDateFormat(
//			"yyyy-MM-dd HH:mm:ss");
//	private static long lastClearTime=0;
//    public void clearExpiredMessageData() {
//
//		//1分钟清理一次。
//		if(lastClearTime==0 || (System.currentTimeMillis()-lastClearTime)<1*60*1000L){
//			if(lastClearTime==0)
//				lastClearTime =System.currentTimeMillis();
//			return;
//		}
//
////		System.out.println("准备清理过期数据。");
//
//		if (msgList.size() > 0) {
//
//			lastClearTime = System.currentTimeMillis();
//			//待删除队列
//			Map<String, ShipsBean> expired = new HashMap<String, ShipsBean>();
//			Date thistime = new Date();
//
//			Iterator<String> it = msgList.keySet().iterator();
//			while (it.hasNext()) {
//				String key = it.next();
//				ShipsBean bean = msgList.get(key);
//				if(bean==null){
//					continue;
//				}
//				try {
//					//超过12分钟没有更新的就删除掉。
//					if ((thistime.getTime() - sdf.parse(bean.getUpdatetime())
//							.getTime()) > 12 * 60 * 1000L) {
//						expired.put(key, bean);
//					}
//				} catch (ParseException ex) {
//
//				}catch (Exception ex) {
//
//				}
//			}
//			if(expired.size()>0)
//			for(String k:expired.keySet()){
//				msgList.remove(k);
//			}
//		}
//	}
//
//	public void processAisMsg(String msg/*,Map<String, ShipsBean> msgList*/){
//		try {
//			//String msg = "!ABVDM,1,1,2,B,169<pP0P1W8bChLF3DEcwwv00<0w,0*1F";
//			String[] token = msg.split("[,*]");
//
//			String originalmsgs[] = new String[2];
//
//			String originalmsg = msg;
//			 if(token[0]!=null&&token[0].equals("!AIVDO")){
//				 myship=true;
//	         // System.err.println("!ABVDO   这是本船消息！！！");
//			 }else{
//				 myship=false;
//			 }
//            if(token[1]==null
//            		||token[2]==null)
//	              return;
//			int totalPackets = Integer.parseInt(token[1]);
//			int currentPacket = Integer.parseInt(token[2]);
//			int seqNum = 0;
//			String wholeSentence="";
//
//
//			if (!token[3].isEmpty()) {
//				seqNum = Integer.parseInt(token[3]);
//			}
//
//			if (totalPackets == 1) {
//				wholeSentence = token[5];
//				originalmsgs[0] = originalmsg;
//			} else {
//			//System.out.println("总数不等于1： "+totalPackets);
//				final ID id = new ID();
//				id.setChannel(token[4]);
//				id.setSeqNum(seqNum);
//				String part1Data = incompleteMessages.get(id);
//				if (currentPacket == 1 || part1Data == null) {
//					//System.out.println("放入未完整信息列表");
//					incompleteMessages.put(id, token[5]);
//					return;
//				} else if (part1Data != null) {
//					//System.out.println("信息完整。");
//					wholeSentence = part1Data + token[5];
//					incompleteMessages.remove(id);
//				}
//			}
//			ships=null;
//			//this.msgList.putAll(msgList);
//			process(wholeSentence);
//			if(ships!=null){
//				//msgList.put(Long.toString(ships.getMmsi()),ships);
//				msgList.put(""+ships.getMmsi(),ships);
//
//				if(ships.getName()!=null&&!ships.getName().equals("")&&!ships.getName().equals(""+ships.getMmsi())){
//				nameList.put(ships.getName(), ""+ships.getMmsi());
//				}
////				if((ships.getMmsi()+"").equals("413424020")){
////				System.out.println("在msgList中放入一条船--->"+ships.getMmsi()+"，size： "+msgList.size());
////				}
//			}
////			System.err.println("getaismsg finish 未解析出结果");
//		} catch (Exception ex) {
//			//System.err.println(msg);
//			ex.printStackTrace();
//
//		}
//
//	}
//
//	public Map<String, ShipsBean> getMsgList(){
//		return msgList;
//	}
//	/*public static void main(String args[]) {
//		try {
//			String msg = "!ABVDM,1,1,2,B,169<pP0P1W8bChLF3DEcwwv00<0w,0*1F";
//
//			String[] token = msg.split("[,*]");
//
//			String originalmsgs[] = new String[2];
//
//			String originalmsg = msg;
//
//			int totalPackets = Integer.parseInt(token[1]);
//			int currentPacket = Integer.parseInt(token[2]);
//			int seqNum = 0;
//			String wholeSentence="";
//			Map<ID, String> incompleteMessages = new HashMap<ID, String>();
//
//			if (!token[3].isEmpty()) {
//				seqNum = Integer.parseInt(token[3]);
//			}
//
//			if (totalPackets == 1) {
//				wholeSentence = token[5];
//				originalmsgs[0] = originalmsg;
//			} else {
//				final ID id = new ID();
//				id.setChannel(token[4]);
//				id.setSeqNum(seqNum);
//				String part1Data = incompleteMessages.get(id);
//				if (currentPacket == 1 || part1Data == null) {
//					incompleteMessages.put(id, token[5]);
//					return;
//				} else if (part1Data != null) {
//					wholeSentence = part1Data + token[5];
//				}
//			}
//
//			process(wholeSentence);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}*/
//
//	static char msgType;
//	private static void process(String simpleSentence){
//		try{
//			msgType = simpleSentence.charAt(0);
////			System.err.println("这是msgTypemsgType：：："+msgType);
//			if (msgType == '1' || msgType == '2' || msgType == '3') {
//				//System.err.println("这是123号消息"+msgType);
//				Vdm vdm = new Vdm();
//				vdm.add(simpleSentence);
//				Message1 msg1 = new Message1();
//
//				final Class123PositionReportDTO report = msg1.parse(vdm
//						.sixbit());
//				if (report != null) {
//					update(report);
//				}
//				return;
//			}
//			if (msgType == '5') {
//				//System.out.println("这是5号消息"+msgType);
//				Vdm vdm = new Vdm();
//				vdm.add(simpleSentence);
//				Message5 msg5 = new Message5();
//				final VesselDataDTO data = msg5.parse(vdm.sixbit());
//				if (data != null) {
//					update(data);
//				}
//				return;
//			}
//			if (msgType == 'B') {
//
//				//System.err.println("这是18号消息"+msgType);
//				Vdm vdm = new Vdm();
//				vdm.add(simpleSentence);
//				Message18 msg = new Message18();
//				final ClassB18PositionReportDTO report = msg
//						.parse(vdm.sixbit());
//				if (report != null) {
//					update(report);
//				}
//				return;
//			}
//			if (msgType == 'C') {
//				//System.err.println("这是19号消息"+msgType);
//				Vdm vdm = new Vdm();
//				vdm.add(simpleSentence);
//				Message19 msg = new Message19();
//				final ClassB19PositionReportDTO report = msg
//						.parse(vdm.sixbit());
//				if (report != null) {
//					update(report);
//				}
//				return;
//			}
//			if (msgType == 'E') {
//				//System.err.println("这是21号消息"+msgType);
////				Vdm vdm = new Vdm();
////				vdm.add(simpleSentence);
////				Message21 msg = new Message21();
////				final Message21Report report = msg
////						.parse(vdm.sixbit());
////				if (report != null) {
////					update(report);
////				}//暂时不需要21
//				return;
//			}
//
//			if (msgType == 'H') {
//				//System.err.println("这是24号消息"+msgType);
//				Vdm vdm = new Vdm();
//				vdm.add(simpleSentence);
//				Message24 msg = new Message24();
//				final ClassB24ReportDTO report = msg
//						.parse(vdm.sixbit());
//				if (report != null) {
//					update(report);
//				}
//				return;
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
//
//	// ����1,2,3����Ϣ
//		private static void update(Class123PositionReportDTO report) {
//
//			long mmsi=0;
//			 double lon=0, lat=0, speed=0, course=0;
//			 String  name = null, vesselType=null;
//			mmsi = report.getMmsi();
//			if (mmsi == 123456789 || mmsi == 0) {
//				return;
//			}
//			lon = report.getLongitude() / 600000.0;
//			lat = report.getLatitude() / 600000.0;
//			if (lon < -180 || lon > 180 || lat < -90 || lat > 90)
//				return;
//
//			speed = report.getSpeedOverGround();
//			if (speed < 0 || speed > 200)
//				return;
//			course = report.getCourseOverGround();
//			if (course < 0 || course > 360)
//				return;
//
//			ShipsBean ship=null;
//			if(no5msgList.containsKey(""+mmsi)){
//				ship = no5msgList.get(""+mmsi);
//				//System.out.println("找到对应的5号消息。"+mmsi);
//			}else{
//				ship=new ShipsBean();}
////			ship.setUpdatetime(System.currentTimeMillis());
//			ship.setMyship(myship);
//			ship.setMsgType("1");
//			ship.setMmsi(mmsi);
////			ship.setName(name);
////			ship.setVesselType(vesselType);
//			ship.setLon(lon);
//			ship.setLat(lat);
//			ship.setSpeed(speed);
//			ship.setCourse(course);
//			ship.setUpdatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
//					format(System.currentTimeMillis()));
////			com.hifleet.aisreceive.TcpClient.msgList.put(Long.toString(mmsi), ship);
//			ships=ship;
////			String s=mmsi+" "+lon+" "+lat+" "+speed+" "+course+" ";
////			com.hifleet.aisreceive.TcpClient.Msg=s;
////			BluetoothActivity.Msg1=s;
//		}
//
//		// ����5����Ϣ
//		private static void update(VesselDataDTO data) {
//			long mmsi=0,imonumber;
//			 int length=0,width=0;
//			 double draught=0;
//			 String name = null, callsign=null, destination=null,eta=null,vesselType=null;
//			mmsi = data.getMmsi();
//			if (mmsi == 123456789 || mmsi == 0) {
//				return;
//			}
//			ShipsBean ship=null;
//			//if(msgList.containsKey(Long.toString(mmsi))){
//			if(msgList.containsKey(""+mmsi)){
//				ship=msgList.get(""+mmsi);
//				//System.out.println("找到！"+mmsi);
//			}else{
//				//System.out.println("这是5号消息，但是列表中不存在这艘船  没有保存: "+mmsi);
//				//return;
//				ship = new ShipsBean();
//			}
//			name = stringFilter(data.getVesselName());
//			if (name.length() <= 1 || name.compareTo("未知") == 0
//					|| name.compareTo("  ") == 0 || name.compareTo(" ") == 0
//					|| name.compareTo("") == 0) {
//				return;
//			}
//			imonumber = data.getImoNumber();
//			callsign = stringFilter(data.getCallSign());
//			destination = stringFilter(data.getDestination());
//			eta = data.getEta();
//			draught = data.getDraught();
//			length = data.getDimensionToBow()+data.getDimensionToStern();
//			width = data.getDimensionToPort()+data.getDimensionToStarboard();
//			vesselType = getVesselType(data.getShipType());
//
//
//			ship.setMyship(myship);
//			ship.setMsgType("5");
//			ship.setMmsi(mmsi);
//			if(name!=null&&!name.equals("")){
//				ship.setName(name);
////				if(shipnames.contains(name)){}else{
////					shipnames.add(name);
////				}
////				System.err.println("5hao name:::"+name+"shipnames"+shipnames.size()+"msgList"+msgList.size());
//				}else{
////					System.err.println("5hao name 不存在"+mmsi+" name"+name);
//				}
//
//			ship.setImonumber(imonumber);//未知属性还未加上      imo
//			ship.setCallsign(callsign);//未知属性还未加上     呼号
//			ship.setDestination(destination);
//			ship.setEta(eta);
//			ship.setDraught(draught);//未知属性还未加上  吃水深
//			ship.setLength(length);
//			ship.setWidth(width);
//			ship.setVesselType(vesselType);
//			ship.setUpdatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
//					format(System.currentTimeMillis()));
////			msgList.put(Long.toString(mmsi), ship);//5号消息
//			ships=ship;
//			no5msgList.put(""+mmsi, ship);
////			String s=mmsi+" "+name+" "+imonumber+" "+callsign+" "+destination+" "+eta+" "+draught+" "+length+" "+width+" "+vesselType;
////			com.hifleet.aisreceive.TcpClient.Msg=s;
////			BluetoothActivity.Msg1=s;
//		}
//
//		// ����18����Ϣ
//		private static void update(ClassB18PositionReportDTO report) {
//			long mmsi=0;
//			 double lon=0, lat=0, speed=0, course=0;
//			 String  name = null, vesselType=null;
//			mmsi = report.getMmsi();
//			if (mmsi == 123456789 || mmsi == 0) {
//				return;
//			}
//
//			lon = report.getLongitude()/ 600000.0;;
//			lat = report.getLatitude()/ 600000.0;;
//			if (lon < -180 || lon > 180 || lat < -90 || lat > 90)
//				return;
//			speed = report.getSpeedOverGround();
//			if (speed < 0 || speed > 200)
//				return;
//			course = report.getCourseOverGround();
//			if (course < 0 || course > 360)
//				return;
//			ShipsBean ship=null;
//			if(msgList.containsKey(""+mmsi)){
////				System.out.println("这是18号消息找到了: "+mmsi);
//				ship=msgList.get(""+mmsi);
//			}else{
////				System.out.println("这是18号消息，但是列表中不存在这艘船  没有保存: "+mmsi);
//				//return;
//				ship = new ShipsBean();
//			}
//			ship.setMyship(myship);
//			ship.setMsgType("18");
//			ship.setMmsi(mmsi);
//			//ship.setName(name);
//			//ship.setVesselType(vesselType);
//			ship.setLon(lon);
//			ship.setLat(lat);
//			ship.setSpeed(speed);
//			ship.setCourse(course);
//			ship.setUpdatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
//					format(System.currentTimeMillis()));
////			msgList.put(Long.toString(mmsi), ship);
//			ships=ship;
//			no5msgList.put(""+mmsi, ship);
////			String s=mmsi+" "+lon+" "+lat+" "+speed+" "+course;
////			com.hifleet.aisreceive.TcpClient.Msg=s;
////			BluetoothActivity.Msg1=s;
//		}
//
//		// ����19����Ϣ
//		private static void update(ClassB19PositionReportDTO report) {
//			long mmsi=0;
//			 double lon=0, lat=0, speed=0, course=0;
//			 int length=0,width=0;
//			 String  name = null,vesselType=null;
//			mmsi = report.getMmsi();
//			if (mmsi == 123456789 || mmsi == 0) {
//				return;
//			}
//
//			name =stringFilter(report.getVesselName());
//			vesselType = getVesselType(report.getShipType());
//
//
//			lon = report.getLongitude()/ 600000.0;;
//			lat = report.getLatitude()/ 600000.0;;
//			length = report.getDimensionToBow()+report.getDimensionToStern();
//			width = report.getDimensionToPort()+report.getDimensionToStarboard();
//
//			if (lon < -180 || lon > 180 || lat < -90 || lat > 90)
//				return;
//
//			speed = report.getSpeedOverGround();
//			if (speed < 0 || speed > 200)
//				return;
//			course = report.getCourseOverGround();
//			if (course < 0 || course > 360)
//				return;
//			ShipsBean ship=null;
//			if(no5msgList.containsKey(""+mmsi)){
//				ship=no5msgList.get(""+mmsi);
//			}else{
//				//return;
//				ship = new ShipsBean();
//			}
//			ship.setMyship(myship);
//			ship.setMsgType("19");
//			ship.setMmsi(mmsi);
//			if(name!=null&&!name.equals("")){
//				ship.setName(name);
////				if(shipnames.contains(name)){}else{
////					shipnames.add(name);
////				}
////				System.err.println("19hao name:::"+name+"shipnames"+shipnames.size()+"msgList"+msgList.size());
//				}else{
////					System.err.println("19hao name 不存在"+mmsi+" name"+name);
//				}
//			ship.setVesselType(vesselType);
//			ship.setLon(lon);
//			ship.setLat(lat);
//			ship.setSpeed(speed);
//			ship.setCourse(course);
//			if(length>0){
//				ship.setLength(length);
//			}
//			if(width>0){
//				ship.setWidth(width);
//			}
//			ship.setUpdatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
//					format(System.currentTimeMillis()));
////			msgList.put(Long.toString(mmsi), ship);//19号消息的动态更新
//			ships=ship;
//			no5msgList.put(""+mmsi, ship);
////			String s=mmsi+" "+name+" "+vesselType+" "+lon+" "+lat+" "+speed+" "+course;
////			com.hifleet.aisreceive.TcpClient.Msg=s;
////			BluetoothActivity.Msg1=s;
//		}
//		//21号消息
//		private static void update(Message21Report report) {
//			long mmsi=0;
//			 int dis_bow=0,dis_port=0,dis_startboard=0,dis_stern=0;
//			 double lon=0, lat=0;
//			 String  name = null,aton_type=null,name_ext=null;
//			mmsi = report.getMmsi();
//			if (mmsi == 123456789 || mmsi == 0) {
//				return;
//			}
//
//			name =stringFilter(report.getName());
//			int a = report.getAton_type();
//			if(a==0){
//				aton_type="不可用";
//			}else if(a>=1&&a<=15){
//				aton_type="固定助航设备";
//			}else if(a>=16&&a<=31){
//				aton_type="漂浮助航设备";
//			}
//
//			lon = report.getPos().longitude()/ 600000.0;;
//			lat = report.getPos().latitude()/ 600000.0;;
//
//			if (lon < -180 || lon > 180 || lat < -90 || lat > 90)
//				return;
//			name_ext=report.getName_ext();
//			dis_bow=report.getDim_bow();//船头
//			dis_port=report.getDim_port();//船左舷
//			dis_startboard=report.getDim_starboard();//船右舷
//			dis_stern=report.getDim_stern();//船尾
//
//			ShipsBean ship=new ShipsBean();
//			ship.setMyship(myship);
//			ship.setMsgType("21");
//			ship.setMmsi(mmsi);
//			ship.setName(name);
//			ship.setAton_type(aton_type);
//			ship.setLon(lon);
//			ship.setLat(lat);
//			ship.setName_ext(name_ext);
//			ship.setLength(dis_bow+dis_stern);
//			ship.setWidth(dis_port+dis_startboard);
//			ship.setUpdatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
//					format(System.currentTimeMillis()));
////			msgList.put(Long.toString(mmsi), ship);
//			ships=ship;
////			String s=mmsi+" "+name+" "+aton_type+" "+lon+" "+lat+" "+name_ext+" "+dis_bow;
////			com.hifleet.aisreceive.TcpClient.Msg=s;
////			BluetoothActivity.Msg1=s;
//		}
//
//		//24号消息
//				private static void update(ClassB24ReportDTO report) {
//					long mmsi=0;
//					 int dis_bow=0,dis_port=0,dis_startboard=0,dis_stern=0;
////					 double lon=0, lat=0;
//					 String  name = null,
////							 aton_type=null,
//							 callsign=null;
////							 name_ext=null;
//					mmsi = report.getMmsi();
//					if (mmsi == 123456789 || mmsi == 0) {
//						return;
//					}
//
//					ShipsBean ship=null;
//					//if(msgList.containsKey(Long.toString(mmsi))){
//						if(no5msgList.containsKey(""+mmsi)){
//						ship=no5msgList.get(""+mmsi);
//						}else{
////							System.out.println("这是24号消息，但是列表中不存在这艘船  没有保存");
//							ship = new ShipsBean();
//							//return;
//						}
//
//						if(report.getName()!=null){
//					name =stringFilter(report.getName());
//						}
////					int a = report.getAton_type();
////					if(a==0){
////						aton_type="不可用";
////					}else if(a>=1&&a<=15){
////						aton_type="固定助航设备";
////					}else if(a>=16&&a<=31){
////						aton_type="漂浮助航设备";
////					}
////
////					lon = report.getPos().longitude()/ 600000.0;;
////					lat = report.getPos().latitude()/ 600000.0;;
////
////					if (lon < -180 || lon > 180 || lat < -90 || lat > 90)
////						return;
////					name_ext=report.getName_ext();
//						if(report.getCallsign()!=null){
//					callsign=report.getCallsign();
//						}
//					dis_bow=report.getDimensionToBow();//船头
//					dis_port=report.getDimensionToPort();//船左舷
//					dis_startboard=report.getDimensionToStarboard();//船右舷
//					dis_stern=report.getDimensionToStern();//船尾
//
//
//					ship.setMyship(myship);
//					ship.setMsgType("24");
//					ship.setMmsi(mmsi);
//					if(name!=null&&!name.equals("")){
//					ship.setName(name);
////					if(shipnames.contains(name)){}else{
////						shipnames.add(name);
////					}
////					System.err.println("24hao name:::"+name+"shipnames"+shipnames.size()+"msgList"+msgList.size());
//					}else{
////						System.err.println("24hao name 不存在"+mmsi+" name"+name);
//					}
//
////					ship.setAton_type(aton_type);
////					ship.setLon(lon);
////					ship.setLat(lat);
//					if(callsign!=null){
//					ship.setCallsign(callsign);
//					}
////					ship.setName_ext(name_ext);
//					ship.setLength(dis_bow+dis_stern);
//					ship.setWidth(dis_port+dis_startboard);
//					ship.setUpdatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
//							format(System.currentTimeMillis()));
////					msgList.put(Long.toString(mmsi), ship);
//					ships=ship;
//					no5msgList.put(""+mmsi, ship);
////					String s=mmsi+" "+name+" "+aton_type+" "+lon+" "+lat+" "+name_ext+" "+dis_bow;
////					com.hifleet.aisreceive.TcpClient.Msg=s;
////					BluetoothActivity.Msg1=s;
//				}
//		private static String stringFilter(String str) {
//			str = str.replaceAll("@", "").trim();
//			str = str.replace("'", " ");
//			str = str.replace("\\", " ");
//			str = washString(str);
//			String regEx = "\\\\[-_`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//			Pattern p = Pattern.compile(regEx);
//			Matcher m = p.matcher(str);
//			String ret = m.replaceAll(" ").trim();
//			return ret = ret.replaceAll("\\s+", " ");
//
//		}
//
//		private static String washString(String string) {
//			String str = "";
//			byte[] tempbyte = string.getBytes();
//			int length = string.length();
//			for (int i = 0; i < length; i++) {
//				if ((tempbyte[i] < 48) || ((tempbyte[i] > 57) & (tempbyte[i] < 65))
//						|| (tempbyte[i] > 122)
//						|| ((tempbyte[i] > 90) & (tempbyte[i] < 97))) {
//					str += " ";
//					continue;
//				}
//				str += string.charAt(i);
//			}
//			return str;
//		}
//		private static String getVesselType(int i_type) {
//	        switch (i_type) {
//            case 0:
//                return "N/A";
//            case 20:
//                return "飞翼船";
//            case 30:
//                return "渔船";
//            case 31:
//                return "拖带船";
//            case 32:
//                return "拖带船（船长>200米，船宽>25米)";
//            case 33:
//                return "疏浚或水下作业船";
//            case 34:
//                return "潜水作业船舶";
//            case 35:
//                return "军事用途船舶";
//            case 36:
//                return "帆船";
//            case 37:
//                return "游艇";
//            case 40:
//                return "高速船";
//            case 50:
//                return "引航船";
//            case 51:
//                return "搜救船";
//            case 52:
//                return "拖轮";
//            case 53:
//                return "港区交通船";
//            case 54:
//                return "清污船";
//            case 55:
//                return "执法船";
//            case 58:
//                return "医疗船";
//            case 59:
//                return "中立国船舶";
//            case 60:
//                return "客轮";
//            case 70:
//                return "货轮";
//            case 80:
//                return "油轮";
//            case 90:
//                return "其他";
//            default:
//                return "N/A";
//        }
//
//    }
//
//
//
//}
