package com.hifleet.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import bsh.Interpreter;

public class TileSourceManager {
	// private static final Log log =
	// PlatformUtil.getLog(TileSourceManager.class);
	private static final String RULE_BEANSHELL = "beanshell";
	public static final String RULE_YANDEX_TRAFFIC = "yandex_traffic";
	private static final String RULE_WMS = "wms_tile";
	public static String mapUrl, mapName;

	public static class TileSourceTemplate implements ITileSource, Cloneable {
		private int maxZoom;
		private int minZoom;
		private String name;
		protected int tileSize;
		protected String urlToLoad;
		protected String ext;
		private int avgSize;
		private int bitDensity;
		// -1 never expires,
		private int expirationTimeMillis = -1;
		private boolean ellipticYTile;
		private String rule;

		private boolean isRuleAcceptable = true;

		public TileSourceTemplate(String name, String urlToLoad, String ext,
				int maxZoom, int minZoom, int tileSize, int bitDensity,
				int avgSize) {
			this.maxZoom = maxZoom;
			this.minZoom = minZoom;
			this.name = name;
			this.tileSize = tileSize;
			this.urlToLoad = urlToLoad;
			this.ext = ext;
			this.avgSize = avgSize;
			this.bitDensity = bitDensity;

		}

		public void setMinZoom(int minZoom) {
			this.minZoom = minZoom;
		}

		public void setMaxZoom(int maxZoom) {
			this.maxZoom = maxZoom;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setEllipticYTile(boolean ellipticYTile) {
			this.ellipticYTile = ellipticYTile;
		}

		@Override
		public boolean isEllipticYTile() {
			return ellipticYTile;
		}

		@Override
		public int getBitDensity() {
			return bitDensity;
		}

		public int getAverageSize() {
			return avgSize;
		}

		@Override
		public int getMaximumZoomSupported() {
			// return 16;
			return maxZoom;
		}

		// 杩欓噷鍐欐浜嗘渶灏忕缉鏀惧眰绾ф槸3.
		@Override
		public int getMinimumZoomSupported() {
			// System.err.println("map.name: "+name+", map.min.zoom: "+4);
			// return 1;
			return minZoom;
		}

		@Override
		public String getName() {
			return name;
		}

		public void setExpirationTimeMillis(int timeMillis) {
			this.expirationTimeMillis = timeMillis;
		}

		public void setExpirationTimeMinutes(int minutes) {
			if (minutes < 0) {
				this.expirationTimeMillis = -1;
			} else {
				this.expirationTimeMillis = minutes * 60 * 1000;
			}
		}

		public int getExpirationTimeMinutes() {
			if (expirationTimeMillis < 0) {
				return -1;
			}
			return expirationTimeMillis / (60 * 1000);
		}

		public int getExpirationTimeMillis() {
			return expirationTimeMillis;
		}

		@Override
		public int getTileSize() {
			return tileSize;
		}

		@Override
		public String getTileFormat() {
			return ext;
		}

		public void setTileFormat(String ext) {
			this.ext = ext;
		}

		public void setUrlToLoad(String urlToLoad) {
			this.urlToLoad = urlToLoad;
		}

		public boolean isRuleAcceptable() {
			return isRuleAcceptable;
		}

		public void setRuleAcceptable(boolean isRuleAcceptable) {
			this.isRuleAcceptable = isRuleAcceptable;
		}

		public TileSourceTemplate copy() {
			try {
				return (TileSourceTemplate) this.clone();
			} catch (CloneNotSupportedException e) {
				return this;
			}
		}

		@Override
		public String getUrlToLoad(int x, int y, int zoom) {
			// use int to string not format numbers! (non-nls)
			if (urlToLoad == null) {
				return null;
			}
			return MessageFormat.format(urlToLoad, zoom + "", x + "", y + ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		public String getUrlTemplate() {
			return urlToLoad;
		}

		@Override
		public boolean couldBeDownloadedFromInternet() {
			return urlToLoad != null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TileSourceTemplate other = (TileSourceTemplate) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		public void setRule(String rule) {
			this.rule = rule;
		}

		public String getRule() {
			return rule;
		}
	}

	private static Map<String, String> readMetaInfoFile(File dir) {
		Map<String, String> keyValueMap = new LinkedHashMap<String, String>();
		try {

			File metainfo = new File(dir, ".metainfo"); //$NON-NLS-1$
			if (metainfo.exists()) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(metainfo),
								"UTF-8")); //$NON-NLS-1$
				String line;
				String key = null;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("[")) {
						key = line.substring(1, line.length() - 1)
								.toLowerCase();
					} else if (key != null && line.length() > 0) {
						keyValueMap.put(key, line);
						key = null;
					}
				}
				reader.close();
			}
		} catch (IOException e) {
			// log.error("Error reading metainfo file " + dir.getAbsolutePath(),
			// e);
		}
		return keyValueMap;
	}

	private static int parseInt(Map<String, String> attributes, String value,
			int def) {
		String val = attributes.get(value);
		if (val == null) {
			return def;
		}
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static void createMetaInfoFile(File dir, TileSourceTemplate tm,
			boolean override) throws IOException {
		File metainfo = new File(dir, ".metainfo"); //$NON-NLS-1$
		Map<String, String> properties = new LinkedHashMap<String, String>();
		if (tm.getRule() != null && tm.getRule().length() > 0) {
			properties.put("rule", tm.getRule());
		}
		if (tm.getUrlTemplate() != null) {
			properties.put("url_template", tm.getUrlTemplate());
		}

		properties.put("ext", tm.getTileFormat());
		properties.put("min_zoom", tm.getMinimumZoomSupported() + "");
		properties.put("max_zoom", tm.getMaximumZoomSupported() + "");
		properties.put("tile_size", tm.getTileSize() + "");
		properties.put("img_density", tm.getBitDensity() + "");
		properties.put("avg_img_size", tm.getAverageSize() + "");

		if (tm.isEllipticYTile()) {
			properties.put("ellipsoid", tm.isEllipticYTile() + "");
		}
		if (tm.getExpirationTimeMinutes() != -1) {
			properties.put("expiration_time_minutes",
					tm.getExpirationTimeMinutes() + "");
		}
		if (override || !metainfo.exists()) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(metainfo)));
			for (String key : properties.keySet()) {
				writer.write("[" + key + "]\n" + properties.get(key) + "\n");
			}
			writer.close();
		}
	}

	public static boolean isTileSourceMetaInfoExist(File dir) {
		return new File(dir, ".metainfo").exists()
				|| new File(dir, "url").exists();
	}

	/**
	 * @param dir
	 * @return doesn't return null
	 */
	public static TileSourceTemplate createTileSourceTemplate(File dir) {
		// read metainfo file
		Map<String, String> metaInfo = readMetaInfoFile(dir);
		boolean ruleAcceptable = true;
		if (!metaInfo.isEmpty()) {
			metaInfo.put("name", dir.getName());
			TileSourceTemplate template = createTileSourceTemplate(metaInfo);
			if (template != null) {
				return template;
			}
			ruleAcceptable = false;
		}

		// try to find url
		String ext = findOneTile(dir);
		ext = ext == null ? ".jpg" : ext;
		String url = null;
		File readUrl = new File(dir, "url"); //$NON-NLS-1$
		try {
			if (readUrl.exists()) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(readUrl),
								"UTF-8")); //$NON-NLS-1$
				url = reader.readLine();
				//
				url = url.replaceAll("\\{\\$z\\}", "{0}"); //$NON-NLS-1$ //$NON-NLS-2$
				url = url.replaceAll("\\{\\$x\\}", "{1}"); //$NON-NLS-1$//$NON-NLS-2$
				url = url.replaceAll("\\{\\$y\\}", "{2}"); //$NON-NLS-1$ //$NON-NLS-2$
				reader.close();
			}
		} catch (IOException e) {
			//log.debug("Error reading url " + dir.getName(), e); //$NON-NLS-1$
		}

		TileSourceTemplate template = new TileSourceManager.TileSourceTemplate(
				dir.getName(), url, ext, 16, 3, 256, 16, 20000); //$NON-NLS-1$
		template.setRuleAcceptable(ruleAcceptable);
		return template;
	}

	private static String findOneTile(File dir) {
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					String ext = findOneTile(file);
					if (ext != null) {
						return ext;
					}
				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".tile")) {
						String substring = fileName.substring(0,
								fileName.length() - ".tile".length());
						int extInt = substring.lastIndexOf('.');
						if (extInt != -1) {
							return substring.substring(extInt,
									substring.length());
						}
					}
				}
			}
		}
		return null;
	}

	public static java.util.List<TileSourceTemplate> getKnownSourceTemplates() {
		java.util.List<TileSourceTemplate> list = new ArrayList<TileSourceTemplate>();
		list.add(getManyShipsMapSource(MapActivity.mapKey));
		// list.add(getMapnikSource());
		// list.add(getCycleMapSource());
		// list.add(getGoogleMap());
		// list.add(getBaiDuMapSource());
		list.add(getMapABCSource());
		// list.add(getTianDiTuSource());
		// list.add(getSatMapSource());
		// list.add(getChartPlusLandMap());
		// list.add(getChartPlusSatMap());
		return list;

	}

	public static java.util.List<TileSourceTemplate> getKnownChartSourceTemplates() {
		java.util.List<TileSourceTemplate> list = new ArrayList<TileSourceTemplate>();
		list.add(getManyShipsMapSource(MapActivity.mapKey));
		// list.add(getMapnikSource());
		// list.add(getCycleMapSource());
		// list.add(getGoogleMap());
		// list.add(getBaiDuMapSource());
		list.add(getMapABCSource());
		// list.add(getTianDiTuSource());
		// list.add(getSatMapSource());
		// list.add(getChartPlusLandMap());
		// list.add(getChartPlusSatMap());
		return list;
	}

	// 鏆備笖涓嶅紑鏀綧apABC锛堥珮寰凤級鍦板浘鍔熻兘
	// Mapabc:
	// http://emap0.mapabc.com/mapabc/maptile?v=w2.61&x=13740&y=6712&z=14
	public static TileSourceTemplate getMapABCSource() {
		return new TileSourceTemplate(
				"ditu",
				"http://emap0.mapabc.com/mapabc/maptile?v=w2.61&z={0}&x={1}&y={2}",
				".png", 17, 5, 256, 8, 18000); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

	}

	// 浣跨敤Mapabc锛堥珮寰凤級鐨勫崼鐗囷紝鏆備笖涓嶅紑鏀捐鍔熻兘锟�
	// //http://ecn.t1.tiles.virtualearth.net/tiles/a133021.jpeg?g=2451
	// Map abc:
	// http://webst03.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&z=13&x=6742&y=3104
	// http://webst04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=6&x=27483&y=13429&z=15
	public static TileSourceTemplate getSatMapSource() {
		// return new TileSourceTemplate("鍗槦锟�,
		//		"http://ecn.t1.tiles.virtualearth.net/tiles/{0}/{1}/{2}.jpeg?g=2451", ".jpeg", 16, 1, 256, 8, 18000);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		return new TileSourceTemplate(
				"地图",
				"http://webst04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=6&z={0}&x={1}&y={2}",
				".jpeg", 14, 5, 256, 8, 18000);
	}

	public static TileSourceTemplate getManyShipsMapSource(int i) {
		switch (i) {
		case 1:
			mapName = "海图";
			mapUrl = "http://t1.hifleet.com/Tiles/Z{0}/{1}/{2}/{1}_{2}.png";
			break;
		case 2:
			mapName = "地图";
//			mapUrl = "http://www.hifleet.com/getHiFleetTiles?z={0}&x={1}&y={2}";
			mapUrl = "http://mt3.google.cn/vt/lyrs=t@131,r@216000000&hl=zh-CN&gl=CN&src=app&z={0}&x={1}&y={2}&s=Gal";
			break;
		case 3:
			mapName = "全球海图";
			mapUrl = "http://www.hifleet.com/getMadeSmartImages.do?z={0}&x={1}&y={2}";
			break;
		case 4:
			mapName = "green";
			mapUrl = "http://www.hifleet.com/getdotshipimage.do?z={0}&x={1}&y={2}&r=0";
//			System.out.println("i=="+i+"=============="+mapUrl);
			break;
		case 5:
			SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd");
			String time=sfd.format(MapActivity.weathertime);
			mapName = "气压图";
			mapUrl = "http://58.40.126.151:8080/HiFleetBackEndSupport/getpressurepng.do?z={0}&x={1}&y={2}&time="
					  +time+"&f="+MapActivity.weatherf;
//			System.out.println("i=="+i+"=============="+mapUrl);
			break;
		case 6:
			SimpleDateFormat sfd1 = new SimpleDateFormat("yyyyMMdd");
			String time1=sfd1.format(MapActivity.weathertime);
			mapName = "海浪图";
			mapUrl = "http://58.40.126.151:8080/HiFleetBackEndSupport/getwavepng.do?z={0}&x={1}&y={2}&time="
					  +time1+"&f="+MapActivity.weatherf;
//			System.out.println("i=="+i+"=============="+mapUrl);
			break;
		case 7:
			SimpleDateFormat sfd2 = new SimpleDateFormat("yyyyMMdd");
			String time2=sfd2.format(MapActivity.weathertime);
			mapName = "风力图";
			mapUrl = "http://58.40.126.151:8080/HiFleetBackEndSupport/getwindpng.do?z={0}&x={1}&y={2}&time="
			         +time2+"&f="+MapActivity.weatherf;
//			System.out.println("i=="+i+"=============="+mapUrl);
			break;
		case 8:
			SimpleDateFormat sfd3 = new SimpleDateFormat("yyyyMMdd");
			String time3=sfd3.format(MapActivity.weathertime);
			mapName = "洋流图";
			mapUrl = "http://58.40.126.151:8080/HiFleetBackEndSupport2/getcurrentpng.do?z={0}&x={1}&y={2}&time="
			         +time3+"&f="+MapActivity.weatherf;
//			System.out.println("i=="+i+"=============="+mapUrl);
			break;
		}
		 System.out.println("=============="+mapUrl);
		String type;
		if (i == 4) {
			type = ".gif";
		} else {
			type = ".png";
		}
		if(i==3){
		return new TileSourceTemplate(mapName,
		// "http://t1.hifleet.com/Tiles/Z{0}/{1}/{2}/{1}_{2}.png",
		// "http://www.hifleet.com/getHiFleetTiles?z={0}&x={1}&y={2}",
		// "http://www.hifleet.com/getWorldChartTiles?z={0}&x={1}&y={2}",
				mapUrl, type, 17, 2, 256, 8, 18000);
		}else{
			return new TileSourceTemplate(mapName,
					// "http://t1.hifleet.com/Tiles/Z{0}/{1}/{2}/{1}_{2}.png",
					// "http://www.hifleet.com/getHiFleetTiles?z={0}&x={1}&y={2}",
					// "http://www.hifleet.com/getWorldChartTiles?z={0}&x={1}&y={2}",
							mapUrl, type, 17, 2, 256, 8, 18000);
		}
	}

	/*
	 * 
	 * //涓嶇敤google 鍦板浘 //璋锋瓕鍦板浘
	 * //http://mt2.google.cn/vt/lyrs=m@262000000&hl=zh-
	 * CN&gl=CN&src=app&x=14&y=8&z=4&s=Ga // public static TileSourceTemplate
	 * getGoogleMap(){ // return new TileSourceTemplate("闄嗗浘", //
	 * "http://mt2.google.cn/vt/lyrs=m@262000000&hl=zh-CN&gl=CN&src=app&z={0}&x={1}&y={2}&s=Ga"
	 * , ".png", 16, 1, 256, 8, 18000); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	 * // // }
	 */
	public static List<TileSourceTemplate> downloadTileSourceTemplates(
			String versionAsUrl) {
		final List<TileSourceTemplate> templates = new ArrayList<TileSourceTemplate>();
		try {
			URLConnection connection = new URL(
					"http://download.osmand.net//tile_sources.php?"
							+ versionAsUrl).openConnection();
			XmlPullParser parser = PlatformUtil.newXMLPullParser();
			parser.setInput(connection.getInputStream(), "UTF-8");
			int tok;
			while ((tok = parser.next()) != XmlPullParser.END_DOCUMENT) {
				if (tok == XmlPullParser.START_TAG) {
					String name = parser.getName();
					if (name.equals("tile_source")) {
						Map<String, String> attrs = new LinkedHashMap<String, String>();
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							attrs.put(parser.getAttributeName(i),
									parser.getAttributeValue(i));
						}
						TileSourceTemplate template = createTileSourceTemplate(attrs);
						if (template != null) {
							templates.add(template);
						}
					}
				}
			}
		} catch (IOException e) {
			// log.error("Exception while downloading tile sources", e);
			return null;
		} catch (XmlPullParserException e) {
			// log.error("Exception while downloading tile sources", e);
			return null;
		}
		return templates;
	}

	private static TileSourceTemplate createTileSourceTemplate(
			Map<String, String> attrs) {
		TileSourceTemplate template = null;
		String rule = attrs.get("rule");
		if (rule == null) {
			template = createSimpleTileSourceTemplate(attrs, false);
		} else if (RULE_BEANSHELL.equalsIgnoreCase(rule)) {
			template = createBeanshellTileSourceTemplate(attrs);
		} else if (RULE_WMS.equalsIgnoreCase(rule)) {
			template = createWmsTileSourceTemplate(attrs);
		} else if (RULE_YANDEX_TRAFFIC.equalsIgnoreCase(rule)) {
			template = createSimpleTileSourceTemplate(attrs, true);
		} else {
			return null;
		}
		if (template != null) {
			template.setRule(rule);
		}
		return template;
	}

	private static TileSourceTemplate createWmsTileSourceTemplate(
			Map<String, String> attributes) {
		String name = attributes.get("name");
		String layer = attributes.get("layer");
		String urlTemplate = attributes.get("url_template");

		if (name == null || urlTemplate == null || layer == null) {
			return null;
		}
		int maxZoom = parseInt(attributes, "max_zoom", 16);
		int minZoom = parseInt(attributes, "min_zoom", 3);
		int tileSize = parseInt(attributes, "tile_size", 256);
		String ext = attributes.get("ext") == null ? ".jpg" : attributes
				.get("ext");
		int bitDensity = parseInt(attributes, "img_density", 16);
		int avgTileSize = parseInt(attributes, "avg_img_size", 18000);
		urlTemplate = " http://whoots.mapwarper.net/tms/{0}/{1}/{2}/" + layer
				+ "/" + urlTemplate;
		TileSourceTemplate templ = new TileSourceTemplate(name, urlTemplate,
				ext, maxZoom, minZoom, tileSize, bitDensity, avgTileSize);
		return templ;
	}

	private static TileSourceTemplate createSimpleTileSourceTemplate(
			Map<String, String> attributes, boolean ignoreTemplate) {
		String name = attributes.get("name");
		String urlTemplate = attributes.get("url_template");
		if (name == null || (urlTemplate == null && !ignoreTemplate)) {
			return null;
		}
		if (urlTemplate != null) {
			urlTemplate.replace("${x}", "{1}").replace("${y}", "{2}")
					.replace("${z}", "{0}");
		}
		int maxZoom = parseInt(attributes, "max_zoom", 16);
		int minZoom = parseInt(attributes, "min_zoom", 3);
		int tileSize = parseInt(attributes, "tile_size", 256);
		int expirationTime = parseInt(attributes, "expiration_time_minutes", -1);
		String ext = attributes.get("ext") == null ? ".jpg" : attributes
				.get("ext");
		int bitDensity = parseInt(attributes, "img_density", 16);
		int avgTileSize = parseInt(attributes, "avg_img_size", 18000);
		boolean ellipsoid = false;
		if (Boolean.parseBoolean(attributes.get("ellipsoid"))) {
			ellipsoid = true;
		}
		TileSourceTemplate templ = new TileSourceTemplate(name, urlTemplate,
				ext, maxZoom, minZoom, tileSize, bitDensity, avgTileSize);
		if (expirationTime >= 0) {
			templ.setExpirationTimeMinutes(expirationTime);
		}
		templ.setEllipticYTile(ellipsoid);
		return templ;
	}

	private static TileSourceTemplate createBeanshellTileSourceTemplate(
			Map<String, String> attributes) {
		String name = attributes.get("name");
		String urlTemplate = attributes.get("url_template");
		if (name == null || urlTemplate == null) {
			return null;
		}
		int maxZoom = parseInt(attributes, "max_zoom", 16);
		int minZoom = parseInt(attributes, "min_zoom", 3);
		int tileSize = parseInt(attributes, "tile_size", 256);
		String ext = attributes.get("ext") == null ? ".jpg" : attributes
				.get("ext");
		int bitDensity = parseInt(attributes, "img_density", 16);
		int avgTileSize = parseInt(attributes, "avg_img_size", 18000);
		int expirationTime = parseInt(attributes, "expiration_time_minutes", -1);
		boolean ellipsoid = false;
		if (Boolean.parseBoolean(attributes.get("ellipsoid"))) {
			ellipsoid = true;
		}
		TileSourceTemplate templ;
		templ = new BeanShellTileSourceTemplate(name, urlTemplate, ext,
				maxZoom, minZoom, tileSize, bitDensity, avgTileSize);
		templ.setEllipticYTile(ellipsoid);
		if (expirationTime > 0) {
			templ.setExpirationTimeMinutes(expirationTime);
		}
		return templ;
	}

	public static class BeanShellTileSourceTemplate extends TileSourceTemplate {

		Interpreter bshInterpreter;

		public BeanShellTileSourceTemplate(String name, String urlToLoad,
				String ext, int maxZoom, int minZoom, int tileSize,
				int bitDensity, int avgSize) {
			super(name, urlToLoad, ext, maxZoom, minZoom, tileSize, bitDensity,
					avgSize);
			bshInterpreter = new Interpreter();
			try {
				bshInterpreter.eval(urlToLoad);
				bshInterpreter.getClassManager().setClassLoader(
						new ClassLoader() {
							@Override
							public URL getResource(String resName) {
								return null;
							}

							@Override
							public InputStream getResourceAsStream(
									String resName) {
								return null;
							}

							@Override
							public Class<?> loadClass(String className)
									throws ClassNotFoundException {
								throw new ClassNotFoundException(
										"Error requesting " + className);
							}
						});
			} catch (bsh.EvalError e) {
				// log.error("Error executing the map init script " + urlToLoad,
				// e);
			}
		}

		@Override
		public String getUrlToLoad(int x, int y, int zoom) {
			try {
				return (String) bshInterpreter.eval("getTileUrl(" + zoom + ","
						+ x + "," + y + ");");
			} catch (bsh.EvalError e) {
				// log.error(e.getMessage(), e);
				return null;
			}
		}

	}

}
