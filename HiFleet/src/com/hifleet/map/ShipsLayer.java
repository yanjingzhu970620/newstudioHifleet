package com.hifleet.map;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.hifleet.ship.ShipObject;

public class ShipsLayer extends BaseMapLayer{

	protected static final int emptyTileDivisor = 16;
	public static final int OVERZOOM_IN = 0;//2;
	
	private final boolean mainMap;
	protected ITileSource map = null;
	protected MapTileAdapter mapTileAdapter = null;
	
	Paint paintBitmap;
	
	protected RectF bitmapToDraw = new RectF();
	
	protected Rect bitmapToZoom = new Rect();
	

	protected OsmandMapTileView view;
	protected ResourceManager resourceManager;
	private OsmandSettings settings;
	private boolean visible = true;
	
	public ShipsLayer(boolean mainMap){
		this.mainMap=mainMap;
	}
	
	@Override
	public boolean drawInScreenPixels() {
		return false;
	}

	@Override
	public void initLayer(OsmandMapTileView view) {
		
		this.view = view;
		settings = view.getSettings();
		resourceManager = view.getApplication().getResourceManager();

		paintBitmap = new Paint();
		paintBitmap.setFilterBitmap(true);
		paintBitmap.setAlpha(getAlpha());
		
//		if(mapTileAdapter != null && view != null){
//			mapTileAdapter.initLayerAdapter(this, view);
//		}		
	}
	
	@Override
	public void setAlpha(int alpha) {
		super.setAlpha(alpha);
		if (paintBitmap != null) {
			paintBitmap.setAlpha(alpha);
		}
	}
	@Override
	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {
		
		//drawTileMap(canvas, tileBox);
		
		if(tileBox.getZoom()<=9){
			//print("�����߽�С������ʾ������"+tileBox.getZoom());
			return;
		}
		
		QuadRect rec = tileBox.getLatLonBounds();
		LatLon p3 = new LatLon(rec.top,rec.right);
		LatLon p2 = new LatLon(rec.top,rec.left);
		LatLon p1 = new LatLon(rec.bottom,rec.left);
		LatLon p4 = new LatLon(rec.bottom,rec.right);
		
//		String polygon="Polygon(("+p1.getLongitude()+"%20"+p1.getLatitude()+","+
//		p2.getLongitude()+"%20"+p2.getLatitude()+","+p3.getLongitude()+"%20"+p3.getLatitude()+
//		","+p4.getLongitude()+"%20"+p4.getLatitude()+","+p1.getLongitude()+"%20"+p1.getLatitude()+"))";
		
		//String bboxurl = "http://58.40.126.151:8080/cnooc/MobileBBoxSearchVessel.do?bbox="+polygon;
		//print(bboxurl);
		//download(bboxurl);
		
	}
	
	private void download(final String urlstr){
		//print("����download");
		new Thread(new Runnable(){
			public void run(){
				try{
					URL url = new URL(urlstr);
					HttpURLConnection conn = (HttpURLConnection) url  
                            .openConnection();  
                    conn.setConnectTimeout(5000);  
                    InputStream inStream = conn.getInputStream(); 
                   // print("ִ�����غͽ�����");
                    parseXML(inStream);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}).start();
	}
	
	private void parseXML(InputStream inStream) throws Exception {
		// ʵ����һ���ĵ�����������  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        // ͨ���ĵ�������������ȡһ���ĵ�������  
        DocumentBuilder builder = factory.newDocumentBuilder();  
        // ͨ���ĵ�ͨ���ĵ�����������һ���ĵ�ʵ��  
        Document document = builder.parse(inStream);  
        // ��ȡXML�ļ����ڵ�  
        Element root = document.getDocumentElement();  
        // ������е��ӽڵ�
        NodeList childNodes = root.getChildNodes();  
        for (int j = 0; j < childNodes.getLength(); j++) {  
        	// �����ӽڵ�  
            Node childNode = (Node) childNodes.item(j); 
            if (childNode.getNodeType() == Node.ELEMENT_NODE) { 
            	Element childElement = (Element) childNode;  
            	if(childElement.getNodeName().compareTo("ship")==0){
            		String mmsi = childElement.getAttribute("m");
            		String name = childElement.getAttribute("n");
            		String lo = childElement.getAttribute("lo");
            		String la = childElement.getAttribute("la");
            		String sp = childElement.getAttribute("sp");
            		String co = childElement.getAttribute("co");
            		String imo = childElement.getAttribute("i");
            		String dn = childElement.getAttribute("dn");
            		String type = childElement.getAttribute("t");
            		String length = childElement.getAttribute("l");
            		String width = childElement.getAttribute("b");
            		String updatetime = childElement.getAttribute("ti");
            		String navStatus = childElement.getAttribute("s");
            		String destination = childElement.getAttribute("d");
            		String eta = childElement.getAttribute("e");
            		String draught = childElement.getAttribute("dr");
            		String heading = childElement.getAttribute("h");
            		String rot  = childElement.getAttribute("rot");
            		String flagname = childElement.getAttribute("an");
            		String fleet = childElement.getAttribute("fle");
            		String callsign = childElement.getAttribute("c");
            		
            		//print(mmsi+" "+name+" "+lo+" "+la);
            		
            		ShipObject shipObject = new ShipObject();
            		shipObject.setCallsign(callsign);
            		shipObject.setCourse(Double.parseDouble(co));
            		shipObject.setDestination(destination);
            		shipObject.setDisplayed(true);
            		shipObject.setDraught(Double.parseDouble(draught));
            		shipObject.setEta(eta);
            		shipObject.setFlag(dn);
            		shipObject.setFlagname(flagname);
            		shipObject.setHeading(Double.parseDouble(heading));
            		shipObject.setImo(imo);
            		shipObject.setIsfleetship(false);
            		shipObject.setLat(Double.parseDouble(la));
            		shipObject.setLength(Double.parseDouble(length));
            		shipObject.setLon(Double.parseDouble(lo));
            		shipObject.setMmsi(mmsi);
            		shipObject.setName(name);
            		shipObject.setNamedisplayed(true);
            		shipObject.setNavStatus(navStatus);
            		shipObject.setRot(Double.parseDouble(rot));
            		shipObject.setSpeed(Double.parseDouble(sp));
            		shipObject.setType(type);
            		shipObject.setUpdatetime(updatetime);
            		shipObject.setWidth(Double.parseDouble(width));
            	}
            }
        }
       
	}

	private static void print(String msg){
        Log.i(TAG, msg);
    }
 public static final String TAG = "FileDownloader";
	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings drawSettings) {
		
	}
	
	@Override
	public int getMaximumShownMapZoom(){
		if(map == null){
			return 15;
		} else {
			//System.err.println("MapTileLayer max zoom: "+map.getMaximumZoomSupported()+", overzoom_in: "+OVERZOOM_IN);
			return map.getMaximumZoomSupported() + OVERZOOM_IN;
		}
	}
	
	@Override
	public int getMinimumShownMapZoom(){
		if(map == null){
			return 2;
		} else {
			//System.err.println("MapTileLayer min zoom: "+map.getMinimumZoomSupported()+", map name: "+map.getName());
			return map.getMinimumZoomSupported();
		}
	}
		
	@Override
	public void destroyLayer() {
		// TODO clear map cache
		//setMapTileAdapter(null);
	}
}
