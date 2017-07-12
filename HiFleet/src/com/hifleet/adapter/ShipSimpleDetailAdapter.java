package com.hifleet.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.e.common.widget.effect.layout.EffectColorRelativeLayout;
import com.hifleet.plus.R;
import com.hifleet.activity.ShipDetailsActivity;
import com.hifleet.activity.WeatherStationInfoActivity;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.bean.ShipsBean;

/**
 * @{# WeatherStationsAdapter.java Create on 2015年5月26日 下午6:50:23
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShipSimpleDetailAdapter extends BaseAdapter {
	private Context context;
	private List<ShipsBean> mShipBean;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public ShipSimpleDetailAdapter(Context context, List<ShipsBean> mShipBean) {
		this.context = context;
		this.mShipBean = mShipBean;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mShipBean.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mShipBean.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_simple_detail, null);
		TextView nameText = (TextView) convertView
				.findViewById(R.id.text_ship_name);
		TextView mmsiText = (TextView) convertView
				.findViewById(R.id.text_ship_mmsi);
		TextView imoText = (TextView) convertView
				.findViewById(R.id.text_ship_imo);
		TextView callNumberText = (TextView) convertView
				.findViewById(R.id.text_ship_call_number);
		EffectColorRelativeLayout weatherStation = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_details);
		final ShipsBean m = mShipBean.get(position);
		nameText.setText("船名：" + m.n);
		mmsiText.setText("MMSI：" + m.m);
		imoText.setText("IMO：" + m.i);
		callNumberText.setText("呼号：" + m.c);
		weatherStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, ShipDetailsActivity.class);
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				bundle.putString("shipmmsi", m.m);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
}
