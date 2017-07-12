package com.hifleet.fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;

import com.hifleet.plus.R;
import com.hifleet.activity.IsLoginActivity;
import com.hifleet.activity.MyTeamShipsActivity;
import com.hifleet.adapter.ColorOptionsAdapter;
import com.hifleet.adapter.MyTeamListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.MyTeamShipsThread;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# DetailsFragment.java Create on 2015年7月8日 下午2:04:06
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
@SuppressLint("ValidFragment")
public class TeamFragment extends Fragment {

	Context context;

	ListView mTeamList;

	ListView mMyTeam;

	OsmandApplication app;

	ProgressBar progress;

	MyTeamListAdapter mMyTeamListAdapter;

	public static List<MyTeamBean> mMyTeamBean = new ArrayList<MyTeamBean>();
	ArrayList<String> alertgroup=new ArrayList<String>();
	List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
	List<HeartBeatBean> responseBean = new ArrayList<HeartBeatBean>();
	List<HeartBeatBean> delresponseBean = new ArrayList<HeartBeatBean>();
    public String groupclick=null;

	private boolean oncr;

	private MapActivity mapactivity;

	private DisplayMetrics dm;
	public TeamFragment(MapActivity mapactivity){
		this.mapactivity=mapactivity;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		oncr = true;
		View view = inflater.inflate(R.layout.fragment_team, null);
		app=mapactivity.getMyApplication();
		dm= mapactivity.getMapView().getResources().getDisplayMetrics();
		context = getActivity();

		mMyTeam = (ListView) view.findViewById(R.id.list_my_team);
		progress = (ProgressBar) view.findViewById(R.id.progress);
		super.registerForContextMenu(this.mMyTeam);
		if (!app.isIslogin()) {
			progress.setVisibility(View.GONE);
			Intent intent = new Intent(context, IsLoginActivity.class);
			context.startActivity(intent);
			((MapActivity) context).overridePendingTransition(
					R.drawable.activity_open, 0);
		} else {
			MyTeamThread mt = new MyTeamThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				mt.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				mt.execute();
			}
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (oncr) {
			oncr = false;
			return;
		} else {
			if (app.isIslogin()) {
				MyTeamThread mt = new MyTeamThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					mt.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					mt.execute();
				}
			}else {
				progress.setVisibility(View.GONE);
			}
		}
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
				mMyTeamBean.clear();
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_TEAM_NAME_URL;
				System.out.println("httpPost: " + httpPost);
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
				mMyTeamBean.clear();
				parseXMLnew(inStream);
				inStream.close();
				initPopuWindow();
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

			MyTeamAlertThread mat = new MyTeamAlertThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				mat.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				mat.execute();
			}

			if(mMyTeamBean.size()==1){
				Intent intent = new Intent(context, MyTeamShipsActivity.class);
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				bundle.putString("teamname", mMyTeamBean.get(0).getName());
				bundle.putString("clickin", "false");
				intent.putExtras(bundle);
				context.startActivity(intent);
				return;
			}
			mMyTeamListAdapter = new MyTeamListAdapter(TeamFragment.this,context, mMyTeamBean,alertgroup);
			mMyTeam.setAdapter(mMyTeamListAdapter);
			progress.setVisibility(View.GONE);
		}

	}

	class MyTeamAlertThread extends AsyncTask<String, Void, Void> {
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
//				alertgroup.clear();
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_TEAM_ALERT_URL+st+"&EndTime="+et;
				URL shipsUrl = new URL(httpPost);
				HttpURLConnection conn = (HttpURLConnection) shipsUrl
						.openConnection();
				System.out.println("team alert: " + httpPost);
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				conn.setConnectTimeout(10000);
				if(conn==null){
					System.err.println("conn = = null");
				}
				InputStream inStream = conn.getInputStream();
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.err.println("MyTeamAlertThread alertgroup onPostExecute");
			System.err.println("MyTeamAlertThread alertgroup"+alertgroup.size());
			mMyTeamListAdapter = new MyTeamListAdapter(TeamFragment.this,context, mMyTeamBean,alertgroup);
			mMyTeam.setAdapter(mMyTeamListAdapter);
		}

	}
	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);

		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}
		if (root.getNodeName().compareTo("changemygroupcolor") == 0) {
			responseBean.clear();
			responseBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}
		if (root.getNodeName().compareTo("delmygroup") == 0) {
			delresponseBean.clear();
			delresponseBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("fleet") == 0) {
					mMyTeamBean.add(XmlParseUtility.parse(childElement,
							MyTeamBean.class));
//					alertgroup.add(childElement.getAttribute("name"));
//					System.err.println("group alert name::"+childElement.getAttribute("name"));
				}
				if (childElement.getNodeName().compareTo("AlertGroupTitle") == 0) {
					alertgroup.add(childElement.getAttribute("GroupName"));
					System.err.println("group alert::"+childElement.getAttribute("GroupName"));
				}
			}
		}
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		System.err.println("team context menu ");
		// if(true){
		MyTeamBean s = mMyTeamListAdapter.lteamBeans.get(0);
		menu.setHeaderTitle(s.getName());
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "删除船队");
//		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "编辑颜色");
		// menu.add(Menu.NONE, Menu.FIRST + 2, 2, "");
		// }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			DeletGroupDialog();
			break;
		}
		return false;
	}
	
	int i;
	private void DeletGroupDialog() {
		i = 0;
		Builder builder = new AlertDialog.Builder(getActivity());
		final MyTeamBean s = mMyTeamListAdapter.lteamBeans.get(0);
		// final String[] items = { "wifi连接", "蓝牙连接" };
		final String[] items = { "保留船队名仅清空","不保留船队名删除" };
		// builder.setTitle("连接ais");
		builder.setTitle("删除 "+s.getName());
		builder.setSingleChoiceItems(items, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						i = which;
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				DeletGroupThread dg=new DeletGroupThread(s.getName(),i+"");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					dg.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					dg.execute();
				}
			}
		});
		builder.setNegativeButton(R.string.default_buttons_cancel, null);
		builder.create().show();
	}
	
	  public static int dip2px(float density, float dpValue) {  
	        final float scale = density;  
	        return (int) (dpValue * scale + 0.5f);  
	  } 
	private ArrayList<String> datas = new ArrayList<String>();

	private ListView listView;

	private ColorOptionsAdapter optionsAdapter;
	private PopupWindow selectPopupWindow;; 
	/**
	 * 初始化填充Adapter所用List数据  暂时测试。。
	 */
	private void initDatas(){
		
		 datas.clear();
		 
		 for(int i=0;i<app.colorarray.length;i++){
			 datas.add(app.colorarray[i]);
		 }
//		
	}

	 /**
     * 初始化PopupWindow
     */ 
    private void initPopuWindow(){ 
    	
    	initDatas();
    	
    	//PopupWindow浮动下拉框布局
        View loginwindow = (View)getActivity().getLayoutInflater().inflate(R.layout.popupwindow_select, null); 
        listView = (ListView) loginwindow.findViewById(R.id.pop_sel_list); 
        
        //设置自定义Adapter
        optionsAdapter = new ColorOptionsAdapter(getActivity(), handler,datas); 
        listView.setAdapter(optionsAdapter); 
//        System.err.println("initPopuWindow"+datas.toArray().length+group.getWidth());
        selectPopupWindow = new PopupWindow(loginwindow, dip2px(dm.density,50),LayoutParams.WRAP_CONTENT, true); 
        
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
    public void popupWindwShowing(View v) { 
       //将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
       //这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
       //（是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
       selectPopupWindow.showAsDropDown(v,0,-3); 
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
			switch(message.what){
				case 1:
					//选中下拉项，下拉框消失
					int selIndex = data.getInt("selIndex");
					SetColorThread sc=new SetColorThread(groupclick,app.colorarray[selIndex]);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
						sc.executeOnExecutor(Executors.newCachedThreadPool(),
								new String[0]);
					} else {
						sc.execute();
					}
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
	  
	    class DeletGroupThread extends AsyncTask<String, Void, Void> {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 * 
			 */
	    	String group;
	    	String isgroupreserved;
	    	DeletGroupThread(String g,String r){
	    		this.group=g;
	    		this.isgroupreserved=r;
	    	}
			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					String httpPost = app.myPreferences.getString("loginserver",
							null) + IndexConstants.DELET_MYGROUP_URL+"groupname="+group+"&isgroupreserved="+isgroupreserved;
					System.out.println("DELET_MYGROUP_URL: " + httpPost);
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
				System.out.println("看看delresponseBean的内容===" + delresponseBean.size());
				if(delresponseBean.size()>0&&delresponseBean.get(0).flag.equals("1")){
				MyTeamThread mt = new MyTeamThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					mt.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					mt.execute();
				}
				}
			}

		}
	    class SetColorThread extends AsyncTask<String, Void, Void> {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 * 
			 */
	    	String group;
	    	String color;
	    	SetColorThread(String g,String c){
	    		this.group=g;
	    		this.color=c;
	    	}
			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					String httpPost = app.myPreferences.getString("loginserver",
							null) + IndexConstants.SET_TEAM_COLOR_URL+"group="+group+"&color="+color;
					System.out.println("SET_TEAM_COLOR_URL: " + httpPost);
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
				System.out.println("看看responseBean的内容===" + responseBean.size());
				if(responseBean.size()>0&&responseBean.get(0).flag.equals("1")){
				MyTeamShipsThread.getTeamgroup().put(group, color);
				MyTeamThread mt = new MyTeamThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					mt.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					mt.execute();
				}
				}
			}

		}
}
