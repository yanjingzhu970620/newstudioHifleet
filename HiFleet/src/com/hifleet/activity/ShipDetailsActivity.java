package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.hifleet.plus.R;
import com.hifleet.adapter.ShipDetailsAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.ShipDetailsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# ShipDetailsActivity.java Create on 2015年7月14日 下午7:28:34
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShipDetailsActivity extends Activity {

	private Context context;
	public ArrayList<ShipDetailsBean> mShipDetailsBean = new ArrayList<ShipDetailsBean>();
	private List<HeartBeatBean> outBeans = new ArrayList<HeartBeatBean>();
	public ShipDetailsBean obj = new ShipDetailsBean();
	public ShipDetailsAdapter mShipDetailsAdapter;
	public ExpandableListView mShipDetailsListView;
	private String shipMMSI, mIsTimeOut = " ";
	private ProgressBar progressBar;
	OsmandApplication app;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ships_details);
		context = this;
        app = getMyApplication();
		Bundle bundle = this.getIntent().getExtras();
		shipMMSI = bundle.getString("shipmmsi");

		mShipDetailsListView = (ExpandableListView) findViewById(R.id.expandable_ship_details);
		progressBar = (ProgressBar) findViewById(R.id.progress);

		ShipDetailsThread searchship = new ShipDetailsThread();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			searchship.executeOnExecutor(Executors.newCachedThreadPool(),
					new String[0]);
		} else {
			searchship.execute();
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.text_close:
			for (int i = 0; i < 8; i++) {
				if (mShipDetailsListView.isGroupExpanded(i)) {
					mShipDetailsListView.collapseGroup(i);
				}
			}
			break;
		}
	}

	class ShipDetailsThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = null;
				if (app.isIslogin()
						&& !app.getMyrole().equals(
								"ship")
						&& !app.getMyrole().equals(
								"fleet")
						&& !app.getMyrole().equals(
										"coastal")) {
					httpPost = app.myPreferences.getString("loginserver", null)
							+ IndexConstants.GET_SHIPS_DETAIL_XML + shipMMSI;
				} else {
					httpPost = app.myPreferences.getString("loginserver", null)
							+ IndexConstants.GET_SHIPS_DETAIL_XML_FREE
							+ shipMMSI + "&deviceid="
							+ app.myPreferences.getString("DEVICE_ID", null);
				}
				System.out.println("船舶档案"+httpPost);
				URL shipsUrl = new URL(httpPost);
				HttpURLConnection conn = (HttpURLConnection) shipsUrl
						.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				conn.setConnectTimeout(10000);
				InputStream inStream = conn.getInputStream();
				parseXMLNew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println("mShipDetailsBean"+mShipDetailsBean.size()+mIsTimeOut);
			if (mIsTimeOut.equals("timeout")) {
				progressBar.setVisibility(View.GONE);
				new AlertDialog.Builder(context)
						.setTitle("提示")
						.setCancelable(false)
						.setMessage("每日只能查询2艘船舶，权限不足或未登录")
						.setNegativeButton("取消", null)
						.setPositiveButton(
								"登录",
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(context,
												IsLoginActivity.class);
										context.startActivity(intent);
										((ShipDetailsActivity) context)
												.overridePendingTransition(
														R.drawable.activity_open,
														0);
									}
								}).show();
				return;
			}
			String[] firstDetail = new String[] { "基本信息", "船舶尺寸", "船舶容积",
					"管理信息", "船级社", "建造信息", "动力设备", "船舶设备" };
			String[][] secondDetail = new String[][] {
					{ obj.vessel_name_original, obj.mmsi, obj.callsign_ogrinal,
							obj.imo, obj.flag_normalized, obj.my_vessel_type,
							obj.hull_material, obj.port_of_registry,
							obj.service_speed, obj.status_date_normalized,
							obj.status_ship_normalized, obj.teu,
							obj.trial_speed },
					{ obj.air_draught, obj.breadth_extreme,
							obj.breadth_moulded, obj.breadth_registered,
							obj.depth_moulded, obj.draught,
							obj.freeboard_lightship,
							obj.freeboard_normal_ballast,
							obj.freeboard_segregated_ballast,
							obj.freeboard_summer, obj.freeboard_tropical,
							obj.freeboard_winter, obj.lane_meters,
							obj.length_bp, obj.length_overall,
							obj.length_registered, obj.l_p_p },
					{ obj.b_pull_max, obj.bale, obj.ballast, obj.ballast_water,
							obj.bunker, obj.cargo_holds, obj.cars,
							obj.crude_capacity, obj.deadweight, obj.diesel_oil,
							obj.freshwater, obj.fuel, obj.fuel_oil,
							obj.gas_oil, obj.grain, obj.gross_tonnage,
							obj.liquid_cap, obj.lube_oil, obj.net_tonnage,
							obj.passengers },
					{ obj.ism_manager_address, obj.ism_manager_email,
							obj.ism_manager_fax, obj.ism_manager_location,
							obj.ism_manager_name, obj.ism_manager_phone,
							obj.ism_manager_telex, obj.ism_manager_website,
							obj.manager_address, obj.manager_email,
							obj.manager_fax, obj.manager_location,
							obj.manager_name, obj.manager_phone,
							obj.manager_telex, obj.manager_website,
							obj.operator_address, obj.operator_email,
							obj.operator_fax, obj.operator_location,
							obj.operator_name, obj.operator_phone,
							obj.operator_telex, obj.operator_website,
							obj.owner_address, obj.owner_email, obj.owner_fax,
							obj.owner_location, obj.owner_name,
							obj.owner_phone, obj.owner_telex,
							obj.owner_website, obj.reefer_plant,
							obj.reefer_plant_company,
							obj.reefer_plant_description,
							obj.reefer_plant_make, obj.reefer_pts,
							obj.rsw_plant, obj.rsw_plant_company,
							obj.rsw_plant_description, obj.rsw_plant_make,
							obj.tech_man, obj.tech_man_address,
							obj.tech_man_e_mail, obj.tech_man_internet,
							obj.tech_man_mob_phone, obj.tech_man_nation,
							obj.tech_man_post_no, obj.tech_man_telefax,
							obj.tech_man_telephone },
					{ obj.classed_by_1, obj.classed_by_1_date_change,
							obj.classed_by_2, obj.classed_by_2_date_change,
							obj.p_i_insure_1, obj.p_i_insure_2,
							obj.p_i_insure_date_1, obj.p_i_insure_date_2,
							obj.survey_1, obj.survey_1_date,
							obj.survey_1_next_date, obj.survey_2,
							obj.survey_2_date, obj.survey_2_next_date },
					{ obj.builder, obj.displacement, obj.shipyard,
							obj.yard_number, obj.year_of_built },
					{ obj.aux_engine_1, obj.aux_engine_1_bhp,
							obj.aux_engine_1_b_year, obj.aux_engine_1_kw,
							obj.aux_engine_1_lic_builder, obj.aux_engine_1_no,
							obj.aux_engine_1_rpm, obj.aux_engine_1_shp,
							obj.aux_engine_2, obj.aux_engine_2_bhp,
							obj.aux_engine_2_b_year, obj.aux_engine_2_kw,
							obj.aux_engine_2_lic_builder, obj.aux_engine_2_no,
							obj.aux_engine_2_rpm, obj.aux_engine_2_shp,
							obj.aux_engine_3, obj.aux_engine_3_bhp,
							obj.aux_engine_3_b_year, obj.aux_engine_3_kw,
							obj.aux_engine_3_lic_builder, obj.aux_engine_3_no,
							obj.aux_engine_3_rpm, obj.aux_engine_3_shp,
							obj.aux_engine_4, obj.aux_engine_4_bhp,
							obj.aux_engine_4_b_year, obj.aux_engine_4_kw,
							obj.aux_engine_4_lic_builder, obj.aux_engine_4_no,
							obj.aux_engine_4_rpm, obj.aux_engine_4_shp,
							obj.aux_engine_5, obj.aux_engine_5_bhp,
							obj.aux_engine_5_b_year, obj.aux_engine_5_kw,
							obj.aux_engine_5_lic_builder, obj.aux_engine_5_no,
							obj.aux_engine_5_rpm, obj.aux_engine_5_shp,
							obj.aux_engine_6, obj.aux_engine_6_bhp,
							obj.aux_engine_6_b_year, obj.aux_engine_6_kw,
							obj.aux_engine_6_lic_builder, obj.aux_engine_6_no,
							obj.aux_engine_6_rpm, obj.aux_engine_6_shp,
							obj.engine_builder, obj.eng_total_bhp,
							obj.eng_total_kw, obj.main_engine,
							obj.main_engine_bhp, obj.main_engine_builder,
							obj.main_engine_b_year, obj.main_engine_kw,
							obj.main_engine_lic_builder, obj.main_engine_model,
							obj.main_engine_no, obj.main_engine_power,
							obj.main_engine_rpm, obj.main_engine_shp,
							obj.main_engine_type },
					{ obj.gear, obj.gear_company, obj.gear_description,
							obj.generator, obj.generator_company,
							obj.generator_description, obj.lifting_equipment,
							obj.propeller, obj.propeller_company,
							obj.propeller_description, obj.propeller_type } };
			String[][] tips = new String[][] {
					{ "船名", "MMSI编号", "呼号", "IMO注册号", "国籍", "船舶类型", "船体", "登记港",
							"营运速度", "船舶状态更新时间", "船舶状态", "标准箱", "试航速度" },
					{ "净空高度", "最大船宽", "型宽", "登记船宽", "型深", "吃水", "空船干舷",
							"正常压载干舷", "专用压载干舷", "夏季干舷", "热带干舷", "冬季干舷",
							"船舱车道长度", "垂线间长", "总长", "登记长度", "两柱间长" },
					{ "最大系桩拉力", "包装舱容", "压载", "压载水", "燃油", "货舱数量", "汽车装载容量",
							"原油舱容", "载重吨", "轻油", "淡水", "燃料", "燃油", "柴油",
							"散装舱容", "总重", "液货舱容", "滑油", "净吨", "旅客容量" },
					{ "管理公司地址", "ISM船舶管理人email", "ISM船舶管理人传真", "经营场所",
							"ISM船舶管理人名称", "ISM船舶管理人电话", "ISM船舶管理人电传", "网址",
							"船舶管理人地址", "船舶管理人email", "船舶管理人传真", "船舶管理人位置",
							"船舶管理人名称", "船舶管理人电话", "船舶管理人电传", "船舶管理人网站",
							"船舶经营人", "船舶运营人email", "船舶运营人传真", "船舶运营人位置",
							"船舶运营人名称", "船舶运营人电话", "船舶运营人电传", "船舶运营人网站", "船东",
							"船舶所有人email", "船舶所有人传真", "船舶所有人位置", "船舶所有人名称",
							"船舶所有人电话", "船舶所有人电传", "船舶所有人网站", "冷藏设备", "冷藏设备制造商",
							"冷藏设备描述", "冷藏设备制造商", "pts", "制冷设备", "油泵设备制造商",
							"油泵设备描述", "油泵设备make", "设备维修公司", "船舶检验商地址",
							"船舶检验商email", "船舶检验商网页", "船舶检验商移动电话", "船舶检验商国籍",
							"船舶检验商邮编", "船舶检验商电传", "船舶检验商电话" },
					{ "认证船级社（1）", "认证时间（1）", "认证船级社（2）", "认证时间（2）",
							"入保的互保协会（1）", "入保的互保协会（2）", "投保时间", "入保时间（2）",
							"船舶检验（1）", "船舶检验时间（1）", "下次检验时间（1）", "船舶检验（2）",
							"船舶检验时间（2）", "下次检验时间（2）" },
					{ "造船厂", "排水量", "船坞", "船坞编号", "建造日期" },
					{ "辅机（1）", "辅机马力（1）", "辅机建造日期（1）", "辅机功率（1）",
							"辅机licBuilder（1）", "出厂编号", "转速", "额定功率", "辅机（2）",
							"有效功率", "辅机建造日期（2）", "辅机功率（2）", "辅机licBuilder（2）",
							"辅机号（2）", "辅机最大转速（2）", "额定功率", "辅机（3）", "辅机马力（3）",
							"辅机建造日期（3）", "辅机功率（3）", "辅机licBuilder（3）",
							"辅机号（3）", "辅机最大转速（3）", "辅机轴马力（3）", "辅机（4）",
							"辅机马力（4）", "辅机建造日期（4）", "辅机功率（4）",
							"辅机licBuilder（4）", "辅机号（4）", "辅机最大转速（4）",
							"辅机轴马力（4）", "辅机（5）", "辅机马力（5）", "辅机建造日期（5）",
							"辅机功率（5）", "辅机licBuilder（5）", "辅机号（5）",
							"辅机最大转速（5）", "辅机轴马力（5）", "辅机（6）", "辅机马力（6）",
							"辅机建造日期（6）", "辅机功率（6）", "辅机licBuilder（6）",
							"辅机号（6）", "辅机最大转速（6）", "辅机轴马力（6）", "机器制造商",
							"机器总马力", "机器总功率", "主机", "主机马力", "主机建造商", "主机建造日期",
							"主机功率", "特许制造商", "主机型号", "主机编号", "主机功率", "主机最大转速",
							"主机轴马力", "主机类型" },
					{ "齿轮", "齿轮建造商", "齿轮描述", "发电机", "发电机建造商", "发电机描述", "起重设备",
							"推进器", "推进器建造商", "推进器描述", "推进器类型" } };
			progressBar.setVisibility(View.GONE);
			mShipDetailsAdapter = new ShipDetailsAdapter(context, firstDetail,
					secondDetail, tips);
			mShipDetailsListView.setAdapter(mShipDetailsAdapter);
		}
	}

	private void parseXMLNew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			mIsTimeOut = childNode.getNodeValue();
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("b_pull_max") == 0) {
					obj.setB_pull_max(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("bale") == 0) {
					obj.setBale(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ballast") == 0) {
					obj.setBallast(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ballast_water") == 0) {
					obj.setBallast_water(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("bunker") == 0) {
					obj.setBunker(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("cargo_holds") == 0) {
					obj.setCargo_holds(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("cars") == 0) {
					obj.setCars(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("crude_capacity") == 0) {
					obj.setCrude_capacity(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("deadweight") == 0) {
					obj.setDeadweight(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("diesel_oil") == 0) {
					obj.setDiesel_oil(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("freshwater") == 0) {
					obj.setFreshwater(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("fuel") == 0) {
					obj.setFuel(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("fuel_oil") == 0) {
					obj.setFuel_oil(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("gas_oil") == 0) {
					obj.setGas_oil(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("grain") == 0) {
					obj.setGrain(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("gross_tonnage") == 0) {
					obj.setGross_tonnage(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("liquid_cap") == 0) {
					obj.setLiquid_cap(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("lube_oil") == 0) {
					obj.setLube_oil(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("net_tonnage") == 0) {
					obj.setNet_tonnage(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("passengers") == 0) {
					obj.setPassengers(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("classed_by_1") == 0) {
					obj.setClassed_by_1(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"classed_by_1_date_change") == 0) {
					obj.setClassed_by_1_date_change(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("classed_by_2") == 0) {
					obj.setClassed_by_2(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"classed_by_2_date_change") == 0) {
					obj.setClassed_by_2_date_change(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("p_i_insure_1") == 0) {
					obj.setP_i_insure_1(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("p_i_insure_2") == 0) {
					obj.setP_i_insure_2(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("p_i_insure_date_1") == 0) {
					obj.setP_i_insure_date_1(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("p_i_insure_date_2") == 0) {
					obj.setP_i_insure_date_2(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("survey_1") == 0) {
					obj.setSurvey_1(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("survey_1_date") == 0) {
					obj.setSurvey_1_date(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("survey_1_next_date") == 0) {
					obj.setSurvey_1_next_date(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("survey_2") == 0) {
					obj.setSurvey_2(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("survey_2_date") == 0) {
					obj.setSurvey_2_date(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("survey_2_next_date") == 0) {
					obj.setSurvey_2_next_date(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("air_draught") == 0) {
					obj.setAir_draught(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("breadth_extreme") == 0) {
					obj.setBreadth_extreme(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("breadth_moulded") == 0) {
					obj.setBreadth_moulded(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("breadth_registered") == 0) {
					obj.setBreadth_registered(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("depth_moulded") == 0) {
					obj.setDepth_moulded(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("draught") == 0) {
					obj.setDraught(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("freeboard_lightship") == 0) {
					obj.setFreeboard_lightship(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"freeboard_normal_ballast") == 0) {
					obj.setFreeboard_normal_ballast(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"freeboard_segregated_ballast") == 0) {
					obj.setFreeboard_segregated_ballast(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("freeboard_summer") == 0) {
					obj.setFreeboard_summer(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("freeboard_tropical") == 0) {
					obj.setFreeboard_tropical(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("freeboard_winter") == 0) {
					obj.setFreeboard_winter(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("lane_meters") == 0) {
					obj.setLane_meters(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("length_bp") == 0) {
					obj.setLength_bp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("length_overall") == 0) {
					obj.setLength_overall(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("length_registered") == 0) {
					obj.setLength_registered(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("l_p_p") == 0) {
					obj.setL_p_p(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_1") == 0) {
					obj.setAux_engine_1(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_1_bhp") == 0) {
					obj.setAux_engine_1_bhp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_1_b_year") == 0) {
					obj.setAux_engine_1_b_year(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_1_kw") == 0) {
					obj.setAux_engine_1_kw(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"aux_engine_1_lic_builder") == 0) {
					obj.setAux_engine_1_lic_builder(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_1_no") == 0) {
					obj.setAux_engine_1_no(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_1_rpm") == 0) {
					obj.setAux_engine_1_rpm(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_1_shp") == 0) {
					obj.setAux_engine_1_shp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_2") == 0) {
					obj.setAux_engine_2(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_2_bhp") == 0) {
					obj.setAux_engine_2_bhp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_2_b_year") == 0) {
					obj.setAux_engine_2_b_year(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_2_kw") == 0) {
					obj.setAux_engine_2_kw(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"aux_engine_2_lic_builder") == 0) {
					obj.setAux_engine_2_lic_builder(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_2_no") == 0) {
					obj.setAux_engine_2_no(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_2_rpm") == 0) {
					obj.setAux_engine_2_rpm(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_2_shp") == 0) {
					obj.setAux_engine_2_shp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_3") == 0) {
					obj.setAux_engine_3(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_3_bhp") == 0) {
					obj.setAux_engine_3_bhp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_3_b_year") == 0) {
					obj.setAux_engine_3_b_year(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_3_kw") == 0) {
					obj.setAux_engine_3_kw(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"aux_engine_3_lic_builder") == 0) {
					obj.setAux_engine_3_lic_builder(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_3_no") == 0) {
					obj.setAux_engine_3_no(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_3_rpm") == 0) {
					obj.setAux_engine_3_rpm(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_3_shp") == 0) {
					obj.setAux_engine_3_shp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_4") == 0) {
					obj.setAux_engine_4(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_4_bhp") == 0) {
					obj.setAux_engine_4_bhp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_4_b_year") == 0) {
					obj.setAux_engine_4_b_year(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_4_kw") == 0) {
					obj.setAux_engine_4_kw(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"aux_engine_4_lic_builder") == 0) {
					obj.setAux_engine_4_lic_builder(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_4_no") == 0) {
					obj.setAux_engine_4_no(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_4_rpm") == 0) {
					obj.setAux_engine_4_rpm(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_4_shp") == 0) {
					obj.setAux_engine_4_shp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_5") == 0) {
					obj.setAux_engine_5(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_5_bhp") == 0) {
					obj.setAux_engine_5_bhp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_5_b_year") == 0) {
					obj.setAux_engine_5_b_year(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_5_kw") == 0) {
					obj.setAux_engine_5_kw(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"aux_engine_5_lic_builder") == 0) {
					obj.setAux_engine_5_lic_builder(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_5_no") == 0) {
					obj.setAux_engine_5_no(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_5_rpm") == 0) {
					obj.setAux_engine_5_rpm(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_5_shp") == 0) {
					obj.setAux_engine_5_shp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_6") == 0) {
					obj.setAux_engine_6(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_6_bhp") == 0) {
					obj.setAux_engine_6_bhp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_6_b_year") == 0) {
					obj.setAux_engine_6_b_year(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_6_kw") == 0) {
					obj.setAux_engine_6_kw(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"aux_engine_6_lic_builder") == 0) {
					obj.setAux_engine_6_lic_builder(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_6_no") == 0) {
					obj.setAux_engine_6_no(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_6_rpm") == 0) {
					obj.setAux_engine_6_rpm(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("aux_engine_6_shp") == 0) {
					obj.setAux_engine_6_shp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("engine_builder") == 0) {
					obj.setEngine_builder(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("eng_total_bhp") == 0) {
					obj.setEng_total_bhp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("eng_total_kw") == 0) {
					obj.setEng_total_kw(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine") == 0) {
					obj.setMain_engine(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_bhp") == 0) {
					obj.setMain_engine_bhp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_builder") == 0) {
					obj.setMain_engine_builder(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_b_year") == 0) {
					obj.setMain_engine_b_year(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_kw") == 0) {
					obj.setMain_engine_kw(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"main_engine_lic_builder") == 0) {
					obj.setMain_engine_lic_builder(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_model") == 0) {
					obj.setMain_engine_model(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_no") == 0) {
					obj.setMain_engine_no(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_power") == 0) {
					obj.setMain_engine_power(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_rpm") == 0) {
					obj.setMain_engine_rpm(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_shp") == 0) {
					obj.setMain_engine_shp(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("main_engine_type") == 0) {
					obj.setMain_engine_type(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("gear") == 0) {
					obj.setGear(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("gear_company") == 0) {
					obj.setGear_company(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("gear_description") == 0) {
					obj.setGear_description(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("generator") == 0) {
					obj.setGenerator(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("generator_company") == 0) {
					obj.setGenerator_company(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"generator_description") == 0) {
					obj.setGenerator_description(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("lifting_equipment") == 0) {
					obj.setLifting_equipment(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("propeller") == 0) {
					obj.setPropeller(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("propeller_company") == 0) {
					obj.setPropeller_company(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"propeller_description") == 0) {
					obj.setPropeller_description(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("propeller_type") == 0) {
					obj.setPropeller_type(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("callsign_ogrinal") == 0) {
					obj.setCallsign_ogrinal(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("flag_normalized") == 0) {
					obj.setFlag_normalized(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("hull_material") == 0) {
					obj.setHull_material(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("imo") == 0) {
					obj.setImo(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("mmsi") == 0) {
					obj.setMmsi(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("my_vessel_type") == 0) {
					obj.setMy_vessel_type(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("port_of_registry") == 0) {
					obj.setPort_of_registry(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("service_speed") == 0) {
					obj.setService_speed(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"status_date_normalized") == 0) {
					obj.setStatus_date_normalized(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"status_ship_normalized") == 0) {
					obj.setStatus_ship_normalized(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("teu") == 0) {
					obj.setTeu(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("trial_speed") == 0) {
					obj.setTrial_speed(childElement.getTextContent());
				}
				if (childElement.getNodeName()
						.compareTo("vessel_name_original") == 0) {
					obj.setVessel_name_original(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("updateTime") == 0) {
					obj.setUpdatetime(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ism_manager_address") == 0) {
					obj.setIsm_manager_address(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ism_manager_email") == 0) {
					obj.setIsm_manager_email(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ism_manager_fax") == 0) {
					obj.setIsm_manager_fax(childElement.getTextContent());
				}
				if (childElement.getNodeName()
						.compareTo("ism_manager_location") == 0) {
					obj.setIsm_manager_location(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ism_manager_name") == 0) {
					obj.setIsm_manager_name(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ism_manager_phone") == 0) {
					obj.setIsm_manager_phone(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ism_manager_telex") == 0) {
					obj.setIsm_manager_telex(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("ism_manager_website") == 0) {
					obj.setIsm_manager_website(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("manager_address") == 0) {
					obj.setManager_address(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("manager_email") == 0) {
					obj.setManager_email(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("manager_fax") == 0) {
					obj.setManager_fax(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("manager_location") == 0) {
					obj.setManager_location(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("manager_name") == 0) {
					obj.setManager_name(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("manager_phone") == 0) {
					obj.setManager_phone(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("manager_telex") == 0) {
					obj.setManager_telex(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("manager_website") == 0) {
					obj.setManager_website(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("operator_address") == 0) {
					obj.setOperator_address(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("operator_email") == 0) {
					obj.setOperator_email(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("operator_fax") == 0) {
					obj.setOperator_fax(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("operator_location") == 0) {
					obj.setOperator_location(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("operator_name") == 0) {
					obj.setOperator_name(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("operator_phone") == 0) {
					obj.setOperator_phone(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("operator_telex") == 0) {
					obj.setOperator_telex(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("operator_website") == 0) {
					obj.setOperator_website(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("owner_address") == 0) {
					obj.setOwner_address(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("owner_email") == 0) {
					obj.setOwner_email(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("owner_fax") == 0) {
					obj.setOwner_fax(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("owner_location") == 0) {
					obj.setOwner_location(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("owner_name") == 0) {
					obj.setOwner_name(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("owner_phone") == 0) {
					obj.setOwner_phone(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("owner_telex") == 0) {
					obj.setOwner_telex(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("owner_website") == 0) {
					obj.setOwner_website(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("reefer_plant") == 0) {
					obj.setReefer_plant(childElement.getTextContent());
				}
				if (childElement.getNodeName()
						.compareTo("reefer_plant_company") == 0) {
					obj.setReefer_plant_company(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"reefer_plant_description") == 0) {
					obj.setReefer_plant_description(childElement
							.getTextContent());
				}
				if (childElement.getNodeName().compareTo("reefer_plant_make") == 0) {
					obj.setReefer_plant_make(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("reefer_pts") == 0) {
					obj.setReefer_pts(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("rsw_plant") == 0) {
					obj.setRsw_plant(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("rsw_plant_company") == 0) {
					obj.setRsw_plant_company(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo(
						"rsw_plant_description") == 0) {
					obj.setRsw_plant_description(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("rsw_plant_make") == 0) {
					obj.setRsw_plant_make(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man") == 0) {
					obj.setTech_man(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man_address") == 0) {
					obj.setTech_man_address(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man_e_mail") == 0) {
					obj.setTech_man_e_mail(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man_internet") == 0) {
					obj.setTech_man_internet(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man_mob_phone") == 0) {
					obj.setTech_man_mob_phone(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man_nation") == 0) {
					obj.setTech_man_nation(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man_post_no") == 0) {
					obj.setTech_man_post_no(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man_telefax") == 0) {
					obj.setTech_man_telefax(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("tech_man_telephone") == 0) {
					obj.setTech_man_telephone(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("builder") == 0) {
					obj.setBuilder(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("displacement") == 0) {
					obj.setDisplacement(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("shipyard") == 0) {
					obj.setShipyard(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("yard_number") == 0) {
					obj.setYard_number(childElement.getTextContent());
				}
				if (childElement.getNodeName().compareTo("year_of_built") == 0) {
					obj.setYear_of_built(childElement.getTextContent());
				}
				mShipDetailsBean.add(obj);
			}
		}
	}
	public OsmandApplication getMyApplication() {
		return ((OsmandApplication) getApplication());
	}
}
