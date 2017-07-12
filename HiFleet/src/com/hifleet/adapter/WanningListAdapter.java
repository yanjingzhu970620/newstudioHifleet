package com.hifleet.adapter;

import java.util.List;

import org.dom4j.Text;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hifleet.plus.R;
import com.hifleet.bean.WanningBean;

/**
 * @{# WeatherStationsAdapter.java Create on 2015年5月26日 下午6:50:23
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class WanningListAdapter extends BaseAdapter {
	private Context context;
	private List<WanningBean> myTeamBean;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public WanningListAdapter(Context context, List<WanningBean> myTeamBean) {
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
				R.layout.item_wanning_message, null);
		TextView mmsi = (TextView) convertView.findViewById(R.id.text_mmsi);
		TextView name = (TextView) convertView
				.findViewById(R.id.text_ship_name);
		TextView area = (TextView) convertView.findViewById(R.id.text_area);
		TextView reason = (TextView) convertView.findViewById(R.id.text_reason);
		TextView speed = (TextView) convertView.findViewById(R.id.text_speed);
		TextView time = (TextView) convertView.findViewById(R.id.text_time);
		mmsi.setText("MMSI: " + myTeamBean.get(position).Mmsi);
		name.setText("船名:  " + myTeamBean.get(position).Shipname);
		area.setText("区域:  " + myTeamBean.get(position).PlotName);
		System.out.println("原因：===" + myTeamBean.get(position).SpeedType);
		if (myTeamBean.get(position).SpeedType.equals("less")) {
			reason.setText("原因:  " + "低速");
		}
		if (myTeamBean.get(position).SpeedType.equals("greater")) {
			reason.setText("原因:  " + "超速");
		}
		speed.setText("船速:  " + myTeamBean.get(position).getSpeed() + " 节");
		time.setText("时间:  " + myTeamBean.get(position).TriggerTime);
		return convertView;
	}
}
