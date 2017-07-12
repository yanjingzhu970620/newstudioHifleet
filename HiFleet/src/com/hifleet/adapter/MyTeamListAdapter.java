package com.hifleet.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e.common.widget.effect.layout.EffectColorRelativeLayout;
import com.hifleet.activity.MyTeamShipsActivity;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.fragment.TeamFragment;
import com.hifleet.plus.R;
import com.hifleet.thread.MyTeamShipsThread;

import java.util.ArrayList;
import java.util.List;

/**
 * @{# ShipsListAdapter.java Create on 2015年4月14日 上午11:14:11
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class MyTeamListAdapter extends BaseAdapter {
	private Context context;
	private List<MyTeamBean> myTeamBean;
	TeamFragment teamfragment;
	public List<MyTeamBean> lteamBeans=new ArrayList<MyTeamBean>();
	ArrayList<String> alertgroup=new ArrayList<String>();
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public MyTeamListAdapter(TeamFragment teamfragment,Context context, List<MyTeamBean> myTeamBean,ArrayList<String> alertgroup) {
		this.context = context;
		this.myTeamBean = myTeamBean;
		this.teamfragment=teamfragment;
		this.alertgroup=alertgroup;
		System.out.println("MyTeamListAdapter new");
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_my_team, null);
		TextView nameText = (TextView) convertView
				.findViewById(R.id.text_team_name);
		final View cl=(View) convertView.findViewById(R.id.v_colorchoose);
		
		EffectColorRelativeLayout myFleet = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_myteam_item);
		LinearLayout alerticon=(LinearLayout)convertView
				.findViewById(R.id.alert_png);
		final MyTeamBean m = myTeamBean.get(position);
//		System.err.println("alertgroup size::"+alertgroup.size());
		if(alertgroup.contains(m.getName())){
			alerticon.setVisibility(View.VISIBLE);
//			alerticon.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					System.err.println("have alert message!!!");
//				}
//			});
		}
		nameText.setText(m.getName());
		int color=m.getColor();
		if(m.getName()!=null&&color==-1){
			String tcolor="#"+MyTeamShipsThread.getTeamgroup().get(m.getName());
//			System.err.println("color groupname "+groupname+tcolor);
			color=Color.parseColor(tcolor);
			}
		cl.setBackgroundColor(color);
		cl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				teamfragment.popupWindwShowing(cl);
				teamfragment.groupclick=m.getName();
			}
		});
		
		myFleet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, MyTeamShipsActivity.class);
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				bundle.putString("teamname", m.getName());
				bundle.putString("clickin", "true");
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		myFleet.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
//				longclick=true;
//				System.out.println("longclick =true fuzhi");
//				String mShipName = myTeamBean.get(position).getName();
				lteamBeans.clear();
				lteamBeans.add(myTeamBean.get(position));
				// TODO Auto-generated method stub
//				ShipChoosedThread shc = new ShipChoosedThread();
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					shc.executeOnExecutor(
//							Executors.newCachedThreadPool(),
//							new String[0]);
//				} else {
//					shc.execute();
//				}
				
				return false;
			}
		});
		return convertView;
	}
}
