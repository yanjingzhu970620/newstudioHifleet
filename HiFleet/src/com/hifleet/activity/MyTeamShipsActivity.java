package com.hifleet.activity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;

import com.hifleet.plus.R;
import com.hifleet.activity.ShipInfoActivity.MyTeamThread;
import com.hifleet.adapter.MyTeamListAdapter;
import com.hifleet.adapter.TeamShipsListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.fragment.TeamFragment;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.UserLogout;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# ShowMyTeamActivity.java Create on 2015年5月28日 下午3:35:45
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class MyTeamShipsActivity extends HBaseActivity {

	ListView mMyTeam;
	OsmandApplication app;
	TeamShipsListAdapter mMyTeamListAdapter;
	ProgressBar progress;

	private List<ShipsBean> mShipsBeans = new ArrayList<ShipsBean>();
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();

	String name;
	String clickin;
	public static boolean inedit = false;
	static EditText mmsi;
	static EditText callsign;
	static EditText cname;
	static EditText aisname;
	static EditText group;
	LinearLayout parent;
	String[] mygroups;
	private ArrayAdapter<CharSequence> spmygroupsadapter = null;
	String mm = "未填写";
	String cnm = "未填写";
	String an = "未填写";
	String gr = "未填写";
	String grs = "未填写";
	String cs = "未填写";

//	private int pwidth;

	private ImageView image;

	List<MyTeamBean> mMyTeamBean = new ArrayList<MyTeamBean>();
	ArrayList<String> alertships=new ArrayList<String>();
	private DisplayMetrics dm;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_team);

		dm= MyTeamShipsActivity.this.getResources().getDisplayMetrics();
		Bundle bundle = this.getIntent().getExtras();
		// 接收name值
		name = bundle.getString("teamname");
        clickin=bundle.getString("clickin");
        mygroups=new String[TeamFragment.mMyTeamBean.size()];
        for(int i=0;i<TeamFragment.mMyTeamBean.size();i++){
        	 mygroups[i]=TeamFragment.mMyTeamBean.get(i).getName();
        }
		progress = (ProgressBar) findViewById(R.id.progress);
		mMyTeam = (ListView) findViewById(R.id.list_my_teamship);
		super.registerForContextMenu(this.mMyTeam);
		// mMyTeam.setOnItemLongClickListener(new OnItemLongClickListenerml());
		TeamShipsThread teamship = new TeamShipsThread();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			teamship.executeOnExecutor(Executors.newCachedThreadPool(),
					new String[0]);
		} else {
			teamship.execute();
		}

	}

	 public static int dip2px(float density, float dpValue) {  
	        final float scale = density;  
	        return (int) (dpValue * scale + 0.5f);  
	  } 
	// class OnItemLongClickListenerml implements OnItemLongClickListener {
	//
	// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
	// int arg2, long arg3) {
	// System.err.println("longpress    onItemLongClick:::::   team OnItemLongClickListenerml ");
	// // Map<String, String> map = (Map<String, String>) arg0.getAdapter()
	// // .getItem(arg2);
	// System.err.println("team OnItemLongClickListenerml ");
	//
	// return false;
	// }
	//
	// }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		System.err.println("team context menu ");
		// if(true){
		ShipsBean s = mMyTeamListAdapter.lteamShipsBeans.get(0);
		menu.setHeaderTitle(s.n);
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "编辑");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "删除");
		// menu.add(Menu.NONE, Menu.FIRST + 2, 2, "删除");
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			// if(mMyTeamListAdapter.lteamShipsBeans.size()>0){
			inedit = true;
			Builder builder2 = new AlertDialog.Builder(this);
			// LayoutInflater inflater = (LayoutInflater)
			// getApplicationContext()
			// .getSystemService(LAYOUT_INFLATER_SERVICE);
			// View inview = inflater.inflate(R.layout.addteam_dialog, null);
			View inview = this.getLayoutInflater().inflate(
					R.layout.addteam_dialog, null, false);
//			handler = new Handler(); 
			mmsi = (EditText) inview.findViewById(R.id.add_mmsi);
			callsign = (EditText) inview.findViewById(R.id.add_callsign);
			cname = (EditText) inview.findViewById(R.id.add_cname);
			aisname = (EditText) inview.findViewById(R.id.add_aisname);
			group = (EditText) inview.findViewById(R.id.add_groupchoose);
			image = (ImageView)inview.findViewById(R.id.btn_select_group);
			
			MyTeamThread mt = new MyTeamThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				mt.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				mt.execute();
			}
//			parent= (LinearLayout) inview.findViewById(R.id.popselect_parent);
//			int width = group.getWidth();
//			System.err.println("group.getWidth()"+group.getWidth());
//	        pwidth = width;
//	        System.err.println("pwidth"+pwidth);
//	        image.setBackgroundColor(inview.getResources().getColor(R.color.red));
	        
//	        image.setOnClickListener(new View.OnClickListener(){
//
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					popupWindwShowing();
////					System.err.println("popupWindwShowingpopupWindwShowing  pwidth:::"+pwidth);
//				}
//				
//			});
//			 initPopuWindow();    //listview 让分组选择  未做好
			 
			// System.out.println("team edit lteamShipsBeans"+mMyTeamListAdapter.lteamShipsBeans.size());
			// if(mMyTeamListAdapter.lteamShipsBeans.size()>0){
			ShipsBean s = mMyTeamListAdapter.lteamShipsBeans.get(0);
			System.out.println("team edit ShipsBeans" + s.getM());
			mmsi.setText(s.m);
			callsign.setText(s.c);
			aisname.setText(s.n);
			cname.setText(s.cname);
			group.setText(name);
//			System.err.println("group after textchanged"+group.getWidth());
//			spmygroupsadapter=new ArrayAdapter<CharSequence>(this,
//		    			android.R.layout.simple_dropdown_item_1line,mygroups);
//		            group.setAdapter(spmygroupsadapter);
//		            group.setOnItemSelectedListener(new OnItemSelectedListener(){
//
//		        		@Override
//		        		public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
//		        				long arg3) {
//		        			// TODO Auto-generated method stub
//		        			TextView v1 = (TextView)v;
//		        			 v1.setTextColor(Color.WHITE);
//
//		        			gr = group.getSelectedItem().toString().replace(" ", "%20");
//		        		}
//
//		        		@Override
//		        		public void onNothingSelected(AdapterView<?> arg0) {
//		        			// TODO Auto-generated method stub
//		        			
//		        		}
//		               });
			// }
		
			builder2.setView(inview);
			builder2.setCancelable(false);
			builder2.setTitle("编辑" + s.n + "信息").setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mm = mmsi.getText().toString().replace(" ", "%20");
							if (mm == null || mm.equals("")) {
								Toast.makeText(MyTeamShipsActivity.this,
										"mmsi 不能为空", Toast.LENGTH_LONG).show();
								return;
							}
							cnm = cname.getText().toString()
									.replace(" ", "%20");
							an = aisname.getText().toString()
									.replace(" ", "%20");
							cs = callsign.getText().toString()
									.replace(" ", "%20");
							gr = group.getText().toString()
									.replace(" ", "%20");
							System.out.println("team editddddd111" + mm + cnm
									+ an + gr + cs);
							SaveTeamShipsThread saveship = new SaveTeamShipsThread();
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								System.out.println("add team thread start");
								saveship.executeOnExecutor(
										Executors.newCachedThreadPool(),
										new String[0]);
							} else {
								saveship.execute();
							}
							// inedit=false;
							dialog.dismiss();
						}
					});
			builder2.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface cdialog, int which) {
							// inedit=false;
							cdialog.dismiss();
						}
					}).create().show();// 选中
			break;
		case Menu.FIRST + 2:
			DeleteTeamShipsThread deleteship = new DeleteTeamShipsThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				System.out.println("delete teamship thread start");
				deleteship.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				deleteship.execute();
			}
			break;
		}

		return false;
	}
	
	class MyTeamThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_TEAM_NAME_URL;
				System.out.println("my team group httpPost: " + httpPost);
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
				parseTeamGroupXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println("看看mMyTeamBean的内容===" + mMyTeamBean.size());
			if (mMyTeamBean.size() > 0) {
				
				image.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						popupWindwShowing();
//						System.err.println("popupWindwShowingpopupWindwShowing  pwidth:::"+pwidth);
					}
					
				});
				 initPopuWindow();    //listview 让分组选择  未做好
				 
			}
		}

		private void parseTeamGroupXMLnew(InputStream inStream) throws Exception {
			mMyTeamBean.clear();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inStream);

			Element root = document.getDocumentElement();
			// System.out.println(root.getTextContent());
			NodeList childNodes = root.getChildNodes();

			if (root.getNodeName().compareTo("session__timeout") == 0) {
				heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			}

			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = (Node) childNodes.item(j);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) childNode;
					if (childElement.getNodeName().compareTo("fleet") == 0) {
						mMyTeamBean.add(XmlParseUtility.parse(childElement,
								MyTeamBean.class));
					}
				}
			}
		}
	}
	private ArrayList<String> datas = new ArrayList<String>();

	private ListView listView;

	private com.hifleet.adapter.OptionsAdapter optionsAdapter;

	private PopupWindow selectPopupWindow;; 
	/**
	 * 初始化填充Adapter所用List数据  暂时测试。。
	 */
	private void initDatas(){
		
		 datas.clear();
		 
		 for(MyTeamBean t:mMyTeamBean){
			 datas.add(t.getName());
		 }
//		
	}

	 /**
     * 初始化PopupWindow
     */ 
    private void initPopuWindow(){ 
    	
    	initDatas();
    	
    	//PopupWindow浮动下拉框布局
        View loginwindow = (View)MyTeamShipsActivity.this.getLayoutInflater().inflate(R.layout.popupwindow_select, null); 
        listView = (ListView) loginwindow.findViewById(R.id.pop_sel_list); 
        
        //设置自定义Adapter
        optionsAdapter = new com.hifleet.adapter.OptionsAdapter(this, handler,datas); 
        listView.setAdapter(optionsAdapter); 
//        System.err.println("initPopuWindow"+datas.toArray().length+group.getWidth());
        selectPopupWindow = new PopupWindow(loginwindow, dip2px(dm.density,250),LayoutParams.WRAP_CONTENT, true); 
        
        selectPopupWindow.setOutsideTouchable(true); 
        
        //这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失，
        //没有这一句则效果不能出来，但并不会影响背景
        //本人能力极其有限，不明白其原因，还望高手、知情者指点一下
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());  
    } //让分组可以选择的list

	
	
	/**
     * 显示PopupWindow窗口
     * 
     * @param popupwindow
     */ 
    public void popupWindwShowing() { 
       //将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
       //这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
       //（是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
       selectPopupWindow.showAsDropDown(group,0,-3); 
//       selectPopupWindow.set
    } 
     
    /**
     * PopupWindow消失
     */ 
    public void dismiss(){ 
        selectPopupWindow.dismiss(); 
    }
    
    public Handler handler = new Handler() {
    @Override
	public void handleMessage(Message message) {
    	Bundle data=message.getData();
    	System.err.println("dianjile   popwindow  getmessage");
		switch(message.what){
			case 1:
				//选中下拉项，下拉框消失
				int selIndex = data.getInt("selIndex");
				group.setText(datas.get(selIndex));
				System.err.println("dianjile   popwindow  ");
				dismiss();
				break;
			case 2:
				//移除下拉项数据
				int delIndex = data.getInt("delIndex");
				datas.remove(delIndex);
				//刷新下拉列表
				optionsAdapter.notifyDataSetChanged();
				break;
		}
	} 
    };
	// public static Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case TeamShipsListAdapter.ADAPTER:
	// System.out.println("刷新edit");
	// // Message m = new Message();
	// // m.what = cmd;
	// // m.obj = str;
	// // MainActivity.handler.sendMessage(m);
	// if(TeamShipsListAdapter.lteamShipsBeans.size()>0){
	// ShipsBean s=TeamShipsListAdapter.lteamShipsBeans.get(0);
	// System.out.println("teamshipsbean :"+s.getM());
	// mmsi.setText(s.m);
	// callsign.setText(s.c);
	// aisname.setText(s.n);
	// cname.setText(s.dn);
	// group.setText(s.fle);
	// }
	// break;
	// }
	// }
	// };


	public boolean firsttoast=true;	
	public void onContextMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onContextMenuClosed(menu);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.team_back:
			if(clickin.equals("false")){
				MapActivity.isTeamShipMove = true;
			}
			finish();
			break;
		case R.id.team_add:
			// System.out.println("team addddddddd");
			Builder builder2 = new AlertDialog.Builder(this);

			View inview = this.getLayoutInflater().inflate(
					R.layout.addteam_dialog, null, false);
			mmsi = (EditText) inview.findViewById(R.id.add_mmsi);
			callsign = (EditText) inview.findViewById(R.id.add_callsign);
			cname = (EditText) inview.findViewById(R.id.add_cname);
			aisname = (EditText) inview.findViewById(R.id.add_aisname);
			group = (EditText) inview.findViewById(R.id.add_groupchoose);
            image = (ImageView)inview.findViewById(R.id.btn_select_group);
			
			MyTeamThread mt = new MyTeamThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				mt.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				mt.execute();
			}
//			spmygroupsadapter=new ArrayAdapter<CharSequence>(this,
//	    			android.R.layout.simple_dropdown_item_1line,mygroups);
//	            group.setAdapter(spmygroupsadapter);
//	            group.setOnItemSelectedListener(new OnItemSelectedListener(){
//
//	        		@Override
//	        		public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
//	        				long arg3) {
//	        			// TODO Auto-generated method stub
//	        			TextView v1 = (TextView)v;
//	        			 v1.setTextColor(Color.WHITE);
//
//	        			gr = group.getSelectedItem().toString().replace(" ", "%20");
//	        		}
//
//	        		@Override
//	        		public void onNothingSelected(AdapterView<?> arg0) {
//	        			// TODO Auto-generated method stub
//	        			
//	        		}
//	               });
			builder2.setView(inview);
			builder2.setCancelable(false);
			builder2.setTitle("添加船队船舶").setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mm = mmsi.getText().toString().replace(" ", "%20");
							if (mm == null || mm.equals("")) {

								Toast.makeText(MyTeamShipsActivity.this,
										"mmsi 不能为空", Toast.LENGTH_LONG).show();
								return;
							}
							cnm = cname.getText().toString()
									.replace(" ", "%20");
							an = aisname.getText().toString()
									.replace(" ", "%20");
							cs = callsign.getText().toString()
									.replace(" ", "%20");
							gr = group.getText().toString()
									.replace(" ", "%20");
							// System.out.println("team addddddddd111"+mm+cnm+an+gr+cs);
							SaveTeamShipsThread saveship = new SaveTeamShipsThread();
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								// System.out.println("add team thread start");
								saveship.executeOnExecutor(
										Executors.newCachedThreadPool(),
										new String[0]);
							} else {
								saveship.execute();
							}
							dialog.dismiss();
						}
					});
			builder2.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface cdialog, int which) {

							cdialog.dismiss();
						}
					}).create().show();// 选中

			break;
		}
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if(clickin.equals("false")){
				MapActivity.isTeamShipMove = true;
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	class SaveTeamShipsThread extends AsyncTask<String, Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				// String xmString = new
				// String(name.toString().getBytes("UTF-8"));
				// String xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
				String mmString = new String(mm.toString().getBytes("UTF-8"));
//				String mmUTF8 = URLEncoder.encode(mmString, "UTF-8");
				String anString = new String(an.toString().getBytes("UTF-8"));
//				String anUTF8 = URLEncoder.encode(anString, "UTF-8");
				String cnmString = new String(cnm.toString().getBytes("UTF-8"));
//				String cnmUTF8 = URLEncoder.encode(cnmString, "UTF-8");
				String csString = new String(cs.toString().getBytes("UTF-8"));
//				String csUTF8 = URLEncoder.encode(csString, "UTF-8");
				String grString = new String(gr.toString().getBytes("UTF-8"));
//				String grUTF8 = URLEncoder.encode(grString, "UTF-8");

				System.out.println("savemyteam ship "+anString);
				String httpPost = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.SAVE_MYFLEET_SHIPS
						+ "mmsi="
						+ mm
						+ "&callsign="
						+ cs
						+ "&name="
						+ an
						+ "&dname=" + cnm + "&group=" + gr;
				// String
				// httpPost="http://www.hifleet.com/savemyship.do?mmsi=441736000&callsign=&name=moon&dname=moon&groups=&group=moon";
				System.out.println("saveteamship "+httpPost);
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
				// System.out.println("add finish......");
				InputStream inStream = conn.getInputStream();
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				Toast.makeText(MyTeamShipsActivity.this, "网络超时 添加失败",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// System.err.println("add finish already");

			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					if (h.message != null && !h.message.equals("")) {
						Toast.makeText(MyTeamShipsActivity.this,
								"添加失败: " + h.message, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					}
					return;
				} else if (Integer.valueOf(h.flag).intValue() == 1) {
					Toast.makeText(MyTeamShipsActivity.this, "添加成功 ",
							Toast.LENGTH_LONG).show();
				}
			}
			if (name.equals(gr)) {
				TeamShipsThread teamship = new TeamShipsThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					teamship.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					teamship.execute();
				}// 再次请求刷新列表
			} else {

				MyTeamShipsActivity.this.finish();
			}
			// mMyTeamListAdapter = new TeamShipsListAdapter(activity,
			// mShipsBeans);
			// mMyTeam.setAdapter(mMyTeamListAdapter);
			progress.setVisibility(View.GONE);
			// MyTeamShipsActivity.this.finish();
		}
	}

	class DeleteTeamShipsThread extends AsyncTask<String, Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {

				String httpPost = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.DELETE_MYFLEET_SHIP
						+ "mmsi="
						+ mMyTeamListAdapter.mShipName;
				// String
				// httpPost="http://www.hifleet.com/savemyship.do?mmsi=441736000&callsign=&name=moon&dname=moon&groups=&group=moon";
				System.out.println("deletemyteamship "+httpPost);
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
				System.out.println("delete finish......");
				InputStream inStream = conn.getInputStream();
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				Toast.makeText(MyTeamShipsActivity.this, "网络超时 删除失败 ",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			System.err.println("delete finish already");

			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					if (h.message != null && !h.message.equals("")) {
						Toast.makeText(MyTeamShipsActivity.this,
								"删除失败: " + h.message, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					}
					return;
				} else if (Integer.valueOf(h.flag).intValue() == 1) {
					{
						Toast.makeText(MyTeamShipsActivity.this, "删除成功 ",
								Toast.LENGTH_LONG).show();
					}
				}
				TeamShipsThread teamship = new TeamShipsThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					teamship.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					teamship.execute();
				}// 再次请求刷新列表
					// mMyTeamListAdapter = new TeamShipsListAdapter(activity,
					// mShipsBeans);
				// mMyTeam.setAdapter(mMyTeamListAdapter);
				progress.setVisibility(View.GONE);
				// MyTeamShipsActivity.this.finish();
			}
		}
	}

	class TeamShipsThread extends AsyncTask<String, Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				mShipsBeans.clear();
				String xmString = new String(name.toString().getBytes("UTF-8"));
				String xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_QUERY_MY_FLEET + xmlUTF8;
				System.out.println(httpPost);
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
				parseXMLnew(inStream);
				inStream.close();
				
				TeamShipsAlertMmsiThread teamshipalert = new TeamShipsAlertMmsiThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					teamshipalert.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					teamshipalert.execute();
				}// 再次请求刷新列表
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
			mMyTeamListAdapter = new TeamShipsListAdapter(activity, mShipsBeans,alertships);
			mMyTeam.setAdapter(mMyTeamListAdapter);
			progress.setVisibility(View.GONE);
			if(firsttoast){
			Toast.makeText(MyTeamShipsActivity.this, "长按船名 进行操作",
					Toast.LENGTH_LONG).show();
			firsttoast=false;
		}
		}
	}

	class TeamShipsAlertMmsiThread extends AsyncTask<String, Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				SimpleDateFormat  dfm=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d=new Date();
				Date ldate=(Date) d.clone();
				ldate.setDate(d.getDate()-7);
				
				String st=URLEncoder.encode(new String(dfm.format(ldate).getBytes("UTF-8")), "UTF-8");;
				String et=URLEncoder.encode(new String(dfm.format(d).getBytes("UTF-8")), "UTF-8");;
				
				
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_SHIPS_ALERT_URL +st+"&EndTime="+et;
				System.out.println("ships alert::"+httpPost);
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
				alertships.clear();
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
			mMyTeamListAdapter = new TeamShipsListAdapter(activity, mShipsBeans,alertships);
			mMyTeam.setAdapter(mMyTeamListAdapter);
			progress.setVisibility(View.GONE);
			if(firsttoast){
				Toast.makeText(MyTeamShipsActivity.this, "长按船名 进行操作",
						Toast.LENGTH_LONG).show();
				firsttoast=false;
			}
		}
	}
	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		if (root.getNodeName().compareTo("delmyship") == 0) {
			heartBeatBean.clear();
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			System.err.println("添加得到了回应:" + heartBeatBean.get(0).flag
					+ heartBeatBean.get(0).message);
		}
		if (root.getNodeName().compareTo("savemyship") == 0) {
			heartBeatBean.clear();
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			System.err.println("添加得到了回应:" + heartBeatBean.get(0).flag
					+ heartBeatBean.get(0).message);
		}
		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.clear();
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}

		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("ship") == 0) {
					mShipsBeans.add(XmlParseUtility.parse(childElement,
							ShipsBean.class));
				}
				if (childElement.getNodeName().compareTo("AlertRsShip") == 0) {
					alertships.add(childElement.getAttribute("Mmsi"));
				}
			}
		}

		for (int i = 0; i < mShipsBeans.size(); i++) {
			// boolean h=false;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			for (int j = 0; j < mShipsBeans.size(); j++) {
				if (mShipsBeans.get(i).m.equals(mShipsBeans.get(j).m)) {
					// h=true;
					Date ti = df.parse(mShipsBeans.get(i).ti.substring(0,
							mShipsBeans.get(i).ti.length() - 2));
					Date tj = df.parse(mShipsBeans.get(j).ti.substring(0,
							mShipsBeans.get(j).ti.length() - 2));
					if (ti.getTime() < tj.getTime()) {
						mShipsBeans.remove(i);
						// System.out.println("删除"+shipsBeans.get(i).m+"save:"+shipsBeans.get(j).m+"ti小 "+ti.getTime()+"tj"+tj.getTime());
					} else if (ti.getTime() > tj.getTime()) {
						mShipsBeans.remove(j);
						// System.out.println("删除"+shipsBeans.get(j).m+"保留"+shipsBeans.get(i).m+"ti "+ti.getTime()+"tj小"+tj.getTime());
					} else if (ti.getTime() == tj.getTime() && i != j) {
						mShipsBeans.remove(j);
					}
				}
			}
			// if(h==false){
			// shipsBeans.add(tempshipsBeans.get(i));
			// }
		}// 选最近时间的
	}
}
