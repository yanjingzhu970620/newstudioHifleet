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
import com.hifleet.adapter.AreaAlertTypeAdapter;
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
public class AreaAlertTypeActivity extends HBaseActivity {

	ListView alerttypes;
	OsmandApplication app;
	AreaAlertTypeAdapter mAreaAlertTypeAdapter;

 String zone;
 String zoneid;
 String alerttype;
// String conditonid;
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
		setContentView(R.layout.activity_alert_type);

		dm= AreaAlertTypeActivity.this.getResources().getDisplayMetrics();
		Bundle bundle = this.getIntent().getExtras();
		// 接收name值
		zone = bundle.getString("zone");
        zoneid=bundle.getString("zoneid");
        alerttype=bundle.getString("alerttype");
//        conditonid=bundle.getString("conditonid");
//        System.err.println("oncreate alerttype"+alerttype);
        alerttypes = (ListView) findViewById(R.id.list_alerttype);
//		super.registerForContextMenu(this.alerttypes);
		mAreaAlertTypeAdapter=new AreaAlertTypeAdapter(AreaAlertTypeActivity.this,alerttype,zoneid,zone);
		alerttypes.setAdapter(mAreaAlertTypeAdapter);
	}

	
	
//	 public static int dip2px(float density, float dpValue) {  
//	        final float scale = density;  
//	        return (int) (dpValue * scale + 0.5f);  
//	  } 
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {}

		return false;
	}
	
	public void onContextMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onContextMenuClosed(menu);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.alerttype_back:
			finish();
			break;
		
		}
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	


}
