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
import com.hifleet.activity.WeatherStationInfoActivity;
import com.hifleet.bean.MyTeamBean;

/**
 * @{# WeatherStationsAdapter.java Create on 2015年5月26日 下午6:50:23
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class WeatherStationsAdapter extends BaseAdapter {
	private Context context;
	private List<MyTeamBean> myTeamBean;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public WeatherStationsAdapter(Context context, List<MyTeamBean> myTeamBean) {
		this.context = context;
		this.myTeamBean = myTeamBean;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myTeamBean.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return myTeamBean.get(position);
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
				R.layout.item_my_team, null);
		TextView nameText = (TextView) convertView
				.findViewById(R.id.text_team_name);
		EffectColorRelativeLayout weatherStation = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_management);
		final MyTeamBean m = myTeamBean.get(position);
		nameText.setText(m.getName());
		weatherStation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,
						WeatherStationInfoActivity.class);
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				bundle.putString("stationId", m.getId());
				bundle.putString("stationName", m.getName());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
}
