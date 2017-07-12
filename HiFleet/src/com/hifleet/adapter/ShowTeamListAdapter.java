package com.hifleet.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.hifleet.plus.R;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.map.OsmandApplication;

/**
 * @{# ShipsListAdapter.java Create on 2015年4月14日 上午11:14:11
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShowTeamListAdapter extends BaseAdapter {
	private Context context;
	private List<MyTeamBean> myTeamBean;
	OsmandApplication app;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public ShowTeamListAdapter(Context context, List<MyTeamBean> myTeamBean) {
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
				R.layout.item_show_team, null);
		TextView nameText = (TextView) convertView
				.findViewById(R.id.text_team_name);
		CheckBox checkBox = (CheckBox) convertView
				.findViewById(R.id.cb_Check);
		final MyTeamBean m = myTeamBean.get(position);
		Boolean isChecked = app.myPreferences.getBoolean(m.getName(), true);
		checkBox.setChecked(isChecked);
		nameText.setText(m.getName());
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				app.mEditor.putBoolean(m.getName(), isChecked);
				app.mEditor.commit();
			}
		});
		return convertView;
	}
}
