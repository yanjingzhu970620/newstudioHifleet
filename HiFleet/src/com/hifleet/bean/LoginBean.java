package com.hifleet.bean;

/**
 * @{# LoginBean.java Create on 2015年3月26日 下午3:36:55
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class LoginBean {
	public static String sessionid;

	/**
	 * @return the sessionid
	 */
	public static String getSessionid() {
		return sessionid;
	}

	/**
	 * @param sessionid
	 *            the sessionid to set
	 */
	public static void setSessionid(String sessionid) {
		LoginBean.sessionid = sessionid;
	}

	public String flag;
	public String role;
	public String msg;
	
	public String map = "1"; // 地图
	/**
	 * @return the map
	 */
	public String getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(String map) {
		this.map = map;
	}

	/**
	 * @return the satellitemap
	 */
	public String getSatellitemap() {
		return satellitemap;
	}

	/**
	 * @param satellitemap the satellitemap to set
	 */
	public void setSatellitemap(String satellitemap) {
		this.satellitemap = satellitemap;
	}

	/**
	 * @return the chinachart
	 */
	public String getChinachart() {
		return chinachart;
	}

	/**
	 * @param chinachart the chinachart to set
	 */
	public void setChinachart(String chinachart) {
		this.chinachart = chinachart;
	}

	/**
	 * @return the gchart
	 */
	public String getGchart() {
		return gchart;
	}

	/**
	 * @param gchart the gchart to set
	 */
	public void setGchart(String gchart) {
		this.gchart = gchart;
	}

	/**
	 * @return the gchartupdate
	 */
	public String getGchartupdate() {
		return gchartupdate;
	}

	/**
	 * @param gchartupdate the gchartupdate to set
	 */
	public void setGchartupdate(String gchartupdate) {
		this.gchartupdate = gchartupdate;
	}

	/**
	 * @return the shipdetail
	 */
	public String getShipdetail() {
		return shipdetail;
	}

	/**
	 * @param shipdetail the shipdetail to set
	 */
	public void setShipdetail(String shipdetail) {
		this.shipdetail = shipdetail;
	}

	/**
	 * @return the weather
	 */
	public String getWeather() {
		return weather;
	}

	/**
	 * @param weather the weather to set
	 */
	public void setWeather(String weather) {
		this.weather = weather;
	}

	/**
	 * @return the searchship
	 */
	public String getSearchship() {
		return searchship;
	}

	/**
	 * @param searchship the searchship to set
	 */
	public void setSearchship(String searchship) {
		this.searchship = searchship;
	}

	/**
	 * @return the traffic
	 */
	public String getTraffic() {
		return traffic;
	}

	/**
	 * @param traffic the traffic to set
	 */
	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}

	/**
	 * @return the fleets
	 */
	public String getFleets() {
		return fleets;
	}

	/**
	 * @param fleets the fleets to set
	 */
	public void setFleets(String fleets) {
		this.fleets = fleets;
	}

	/**
	 * @return the regionalert
	 */
	public String getRegionalert() {
		return regionalert;
	}

	/**
	 * @param regionalert the regionalert to set
	 */
	public void setRegionalert(String regionalert) {
		this.regionalert = regionalert;
	}

	/**
	 * @return the portship
	 */
	public String getPortship() {
		return portship;
	}

	/**
	 * @param portship the portship to set
	 */
	public void setPortship(String portship) {
		this.portship = portship;
	}

	/**
	 * @return the regionship
	 */
	public String getRegionship() {
		return regionship;
	}

	/**
	 * @param regionship the regionship to set
	 */
	public void setRegionship(String regionship) {
		this.regionship = regionship;
	}

	/**
	 * @return the route
	 */
	public String getRoute() {
		return route;
	}

	/**
	 * @param route the route to set
	 */
	public void setRoute(String route) {
		this.route = route;
	}

	/**
	 * @param serviceinfo the serviceinfo to set
	 */
	public void setServiceinfo(String serviceinfo) {
		this.serviceinfo = serviceinfo;
	}

	public String satellitemap = "1";// 卫星图
	public String chinachart = "0";// 中国海图
	public String gchart;// 全球海图
	public String gchartupdate;// 全球海图每周更新
	public String shipdetail;// 船舶档案
	public String weather;// 全球气象
	public String searchship;// 船位搜索
	public String traffic;// 船舶交通
	public String fleets;// 船队管理
	public String regionalert;// 区域个数
	public String portship;// 港口船舶
	public String regionship;// 区域船舶
	public String route;// 航线船舶
	public String observatory;// 船队管理
	
	/**
	 * @return the observatory
	 */
	public String getObservatory() {
		return observatory;
	}

	/**
	 * @param observatory the observatory to set
	 */
	public void setObservatory(String observatory) {
		this.observatory = observatory;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String name;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String serviceinfo;
	public String fleet;
	public String password;
	public String email;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String type;
	/**
	 * @return the fleet
	 */
	public String getFleet() {
		return fleet;
	}

	/**
	 * @param fleet
	 *            the fleet to set
	 */
	public void setFleet(String fleet) {
		this.fleet = fleet;
	}

	public String getFlag() {
		return flag;
	}

	public String getMsg() {
		if(msg==null){return "msgnull";}
		return msg;
	}

	public String getServiceinfo() {
		return serviceinfo;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
