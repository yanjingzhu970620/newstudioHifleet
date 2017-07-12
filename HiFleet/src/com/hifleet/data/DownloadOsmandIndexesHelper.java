package com.hifleet.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import com.hifleet.data.DownloadActivityType;
import com.hifleet.data.DownloadEntry;
import com.hifleet.data.IndexFileList;
import com.hifleet.data.IndexItem;
import com.hifleet.map.Algorithms;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OfflineDataRegion;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.ResourceManager;


public class DownloadOsmandIndexesHelper {
	//private final static Log //log = PlatformUtil.getLog(DownloadOsmandIndexesHelper.class);
		private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		
	private static ResourceManager mgr;
		

	public static IndexFileList getIndexesListFromIneternet(Context ctx){
		PackageManager pm =ctx.getPackageManager();

		String versionUrlParam ="EasyNavigation2.0";
		IndexFileList result = downloadIndexesListFromInternet(ctx, versionUrlParam);
		
		return result;
	}

		public static IndexFileList getIndexesList(Context ctx) {
			PackageManager pm =ctx.getPackageManager();
			AssetManager amanager = ctx.getAssets();
			String versionUrlParam = "EasyNavigation2.0";
			mgr = ((OsmandApplication) ctx.getApplicationContext()).getResourceManager();
			
			//以下代码表示从internet上下载
			IndexFileList result = downloadIndexesListFromInternet(ctx, versionUrlParam);
			//以下代码表示从assets中读取
			//IndexFileList result = downloadIndexesListFromAssets(ctx,versionUrlParam);
			
			if (result == null) {
				result = new IndexFileList();
			} else {
				result.setDownloadedFromInternet(true);
			}
			
			return result;
		}
		
		
		
		private static DownloadActivityType getIndexType(String tagName){
			if("region".equals(tagName) ||
								"multiregion".equals(tagName)) {
				return DownloadActivityType.NORMAL_FILE;
			}	
			return null;
		}
		
		private static IndexFileList downloadIndexesListFromAssets(Context ctx,String versionAsUrl){
			return mgr.getIndexFileListFromAssets();
			
		}

		private static IndexFileList downloadIndexesListFromInternet(Context ctx, String versionAsUrl){
			try {
				IndexFileList result = new IndexFileList();
				InputStreamReader isr=null;
				//System.out.println("Start loading list of index files in function downloadIndexesListFromInternet."); //$NON-NLS-1$
				try {
					//以下是原版 yang chun
					//String strUrl = "http://"+IndexConstants.INDEX_DOWNLOAD_DOMAIN+"/get_indexes?gzip&" + versionAsUrl; //$NON-NLS-1$
					//System.out.println("strUrl: "+strUrl);
					String strUrl="http://"+IndexConstants.INDEX_DOWNLOAD_DOMAIN+"/indexes.xml";//"http://www.manyships.com/GProxy/indexes.xml";
//					Print.print("离线数据包url: "+strUrl);
					URL url = new URL("http://"+IndexConstants.INDEX_DOWNLOAD_DOMAIN+"/indexes.xml" ); 
					XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
					//parser.setInput(new GZIPInputStream(url.openStream()), "UTF-8"); //$NON-NLS-1$			
					
					
					BufferedInputStream inputStream = new BufferedInputStream(url.openStream(), 8 * 1024);
					
					FileOutputStream stream = null;
					File fileToSave = new File(mgr.getDirWithTiles(),"indexes.xml");
					stream = new FileOutputStream(fileToSave);	
					Algorithms.streamCopy(inputStream, stream);
					stream.flush();
					stream.close();
					inputStream.close();
					System.err.println("indexes.xml 文件存储成功，位于："+fileToSave.getAbsolutePath()+"  "+versionAsUrl);
					//java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(url.openStream(),"gb2312"));
					
					//java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream,"gb2312"));
					isr=new InputStreamReader(new FileInputStream(fileToSave),"gb2312");
					java.io.BufferedReader br = new java.io.BufferedReader(isr);
					
					String s;
					String str;
					StringBuffer stringbuffer = new StringBuffer();
					while( (s =br.readLine())!=null){
						stringbuffer.append(s);
					}
					str = new String(stringbuffer);
					
					//str = new String (str.getBytes("ISO8859-1"),"gb2312");
					//System.err.println("index xml content: "+str);
					
					parser.setInput(new StringReader(str));
					
					//parser.setInput(new DataInputStream(url.openStream()),"UTF-8");
					
					int next;
					while((next = parser.next()) != XmlPullParser.END_DOCUMENT) {
						if (next == XmlPullParser.START_TAG) {
							DownloadActivityType tp = getIndexType(parser.getName());
							if (tp != null) {
								String name = parser.getAttributeValue(null, "filename")+".sqlitedb"; //$NON-NLS-1$
								print("downloadIndexesListFromInternet 离线包文件名： "+new String(name.getBytes("utf-8"),"gb2312"));
								String filename1 = parser.getAttributeValue(null, "filename");
								String size = parser.getAttributeValue(null, "size"); //$NON-NLS-1$
								String date = parser.getAttributeValue(null, "date"); //$NON-NLS-1$
								String description = parser.getAttributeValue(null, "description"); //$NON-NLS-1$
								//String parts = parser.getAttributeValue(null, "parts"); //$NON-NLS-1$
								String rlon = parser.getAttributeValue(null,"rlon");
								String rlat = parser.getAttributeValue(null,"rlat");
								String llon = parser.getAttributeValue(null,"llon");
								String llat = parser.getAttributeValue(null,"llat");
								String id = parser.getAttributeValue(null,"id");
								date = reparseDate(ctx, date);
								
								IndexItem it = new IndexItem(name, description, date, size,rlon,rlat,llon,llat,filename1);
								it.setType(tp);
								it.setId(id);
								result.add(it);
								OfflineDataRegion region = new OfflineDataRegion();
								region.setMaxlat(Double.parseDouble(rlat));
								region.setMaxlon(Double.parseDouble(rlon));
								region.setMinlat(Double.parseDouble(llat));
								region.setMinlon(Double.parseDouble(llon));
								region.setId(Integer.parseInt(id));
								mgr.setOfflineDataRegionMapping(name, region);
							} else if ("osmand_regions".equals(parser.getName())) {
								String mapversion = parser.getAttributeValue(null, "mapversion");
								result.setMapVersion(mapversion);
							}
						}
					}
					result.sort();
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("error reading xml: "+e.getMessage());
					//log.error("Error while loading indexes from repository", e); //$NON-NLS-1$
					return downloadIndexesListFromAssets(ctx,versionAsUrl);
				} catch (XmlPullParserException e) {
					e.printStackTrace();
					System.err.println("error parsing xml: "+e.getMessage());
					//log.error("Error while loading indexes from repository", e); //$NON-NLS-1$
					return downloadIndexesListFromAssets(ctx,versionAsUrl);
				}finally{
					try{
						if(isr!=null){
							isr.close();
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				if (result.isAcceptable()) {
					System.err.println("从网络中获得了indexes.xml文件");
					return result;
				} else {
					return downloadIndexesListFromAssets(ctx,versionAsUrl);
				}
			} catch (RuntimeException e) {
				//log.error("Error while loading indexes from repository", e); //$NON-NLS-1$
				return downloadIndexesListFromAssets(ctx,versionAsUrl);
			}
		}
		
		private static void print(String msg){

			android.util.Log.i(TAG, msg);

	 }
	 
	private static final String TAG = "FileDownloader";


		protected static String reparseDate(Context ctx, String date) {
			try {
				Date d = simpleDateFormat.parse(date);
				return AndroidUtils.formatDate(ctx, d.getTime());
			} catch (ParseException e) {
				return date;
			}
		}

		public static class AssetIndexItem extends IndexItem {
			
			private final String assetName;
			private final String destFile;
			private final long dateModified;

			public AssetIndexItem(String fileName, String description, String date,
					long dateModified, String size, String parts, String assetName, String destFile,String filename1) {
				super(fileName, description, date, size, /*parts,*/"","","","",filename1);
				this.dateModified = dateModified;
				this.assetName = assetName;
				this.destFile = destFile;
			}
			
			public long getDateModified() {
				return dateModified;
			}

			@Override
			public boolean isAccepted(){
				return true;
			}
			
			@Override
			public List<DownloadEntry> createDownloadEntry(OsmandApplication ctx, DownloadActivityType type, List<DownloadEntry> res) {
				res.add(new DownloadEntry(this, assetName, destFile, dateModified));
				return res;
			}
		}
		
}
