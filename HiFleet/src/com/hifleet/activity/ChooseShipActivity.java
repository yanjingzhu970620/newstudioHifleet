package com.hifleet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.hifleet.plus.R;
import com.hifleet.adapter.ChooseShipAdapter;
import com.hifleet.adapter.ShipsListAdapter;
import com.hifleet.map.OsmandApplication;

/**
 * @{# ShipSimpleDetailActivity.java Create on 2015年7月15日 下午4:22:41
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ChooseShipActivity extends Activity {
	private ListView mSimpleDetailList;
	private ChooseShipAdapter mAdapter;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("ChooseShipActivity oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_ship);

		mSimpleDetailList = (ListView) findViewById(R.id.list_ship_simple_detail);
		mAdapter = new ChooseShipAdapter(this, ShipsListAdapter.shipsBeans);
		mSimpleDetailList.setAdapter(mAdapter);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		}
	}

	public OsmandApplication getMyApplication() {
		return ((OsmandApplication) getApplication());
	}
}
