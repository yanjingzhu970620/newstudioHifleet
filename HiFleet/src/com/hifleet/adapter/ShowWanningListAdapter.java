package com.hifleet.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hifleet.plus.R;
import com.hifleet.activity.ShowWanningActivity;
import com.hifleet.bean.WanningBean;
import com.hifleet.map.OsmandApplication;

/**
 * @{# ShipsListAdapter.java Create on 2015年4月14日 上午11:14:11
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShowWanningListAdapter extends BaseAdapter {
	private Context context;
	private List<WanningBean> wanningBean;
	OsmandApplication app;
//	public static List<WanningBean> lastbean=new ArrayList<WanningBean>();
//	public static HashMap<String, WanningBean> wanningBean1 = new HashMap<String, WanningBean>();
//	public static HashMap<String, String> whetherWan = new HashMap<String, String>();
	//public static HashMap<WanningBean, Boolean> wanningBean1 = new HashMap<WanningBean, Boolean>();
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public ShowWanningListAdapter(Context context, List<WanningBean> wanningBean) {
		this.context = context;
		this.wanningBean = wanningBean;
	}
//		for(int ai=0;ai<wanningBean.size();ai++){
//			for(int j=0;j<lastbean.size();j++){
//				haveid=0;
//			if(lastbean.get(j).LayerId.equals(wanningBean.get(ai).LayerId)){
//				haveid++;
//				return;
//			}
//			}
//			if(haveid==0){
//				lastbean.add(wanningBean.get(ai));
//			}
//        }
//for(int i=0;i<lastbean.size();i++){
//			dflag=0;
//			for(int j=0;j<wanningBean.size();j++){
//				if(wanningBean.get(j).getLayerId().equals(lastbean.get(i).getLayerId())){
//					dflag++;
//				}
//			}
//			if(dflag==0){
//				System.out.println("lastbean删除元素");
//				lastbean.remove(i);
//			}
//        }
//	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return wanningBean.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return wanningBean.get(position);
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
	/*@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 System.out.println("初始化listview");
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_show_team, null);
		 
		final TextView nameText = (TextView) convertView
				.findViewById(R.id.text_team_name);
		final CheckBox cbCheck = (CheckBox)convertView.findViewById(R.id.cb_Check);
		final WanningBean m = wanningBean.get(position);
		nameText.setText(m.getName());
		 cbCheck.setOnClickListener(new OnClickListener() {  
             
             @Override  
             public void onClick(View v) {  
            	// TODO Auto-generated method stub
                System.out.println("点击的报警名称");
                  }  
         }); 
		return convertView;
	}*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_show_team, null);
		final TextView nameText = (TextView) convertView
				.findViewById(R.id.text_team_name);
		final CheckBox wanningBox = (CheckBox)convertView.findViewById(R.id.cb_Check);
		final WanningBean m = wanningBean.get(position);
		wanningBox.setChecked(app.myPreferences.getBoolean(m.getLayerId(), false));
		if(m!=null&&!m.equals("")){
		nameText.setText(m.getName());
		}
		final com.e.common.widget.effect.layout.EffectColorRelativeLayout layout=
				(com.e.common.widget.effect.layout.EffectColorRelativeLayout)
				convertView.findViewById(R.id.effectRelativeLayout_management);
		layout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				System.out.println("点击layout");
				// TODO Auto-generated method stub  
				wanningBox.setChecked(!wanningBox.isChecked());
				app.mEditor.putBoolean(m.getLayerId(),wanningBox.isChecked());
				app.mEditor.commit();
//				lastbean.get(position).booltf=wanningBox.isChecked()+"";
//				ShowWanningActivity.lmMyTeamBeans1.get(position).booltf=wanningBox.isChecked()+"";
//				System.out.println(lastbean.get(position).getLayerId()+"layout:"+lastbean.get(position).booltf+ShowWanningActivity.lmMyTeamBeans1.get(position).booltf);
//				for(int j=0;j<ShowWanningActivity.mMyTeamBeans.size();j++){
//					if(ShowWanningActivity.mMyTeamBeans.get(j).getLayerId().equals(lastbean.get(position).getLayerId())){
//						ShowWanningActivity.mMyTeamBeans.get(j).booltf=wanningBox.isChecked()+"";
//					}
//				}
				
			}
			
		});
		wanningBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub  
				app.mEditor.putBoolean(m.getLayerId(),wanningBox.isChecked());
				app.mEditor.commit();
//				lastbean.get(position).booltf=wanningBox.isChecked()+"";
//				ShowWanningActivity.lmMyTeamBeans1.get(position).booltf=wanningBox.isChecked()+"";
//				System.out.println(lastbean.get(position).getLayerId()+":"+lastbean.get(position).booltf+ShowWanningActivity.lmMyTeamBeans1.get(position).booltf);
//				for(int j=0;j<ShowWanningActivity.mMyTeamBeans.size();j++){
//					if(ShowWanningActivity.mMyTeamBeans.get(j).getLayerId().equals(lastbean.get(position).getLayerId())){
//						ShowWanningActivity.mMyTeamBeans.get(j).booltf=wanningBox.isChecked()+"";
//					}
//				}
				
			}
		});
		return convertView;
	}
	/*@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_show_team, null);
		final TextView nameText = (TextView) convertView
				.findViewById(R.id.text_team_name);
		final CheckBox wanningBox = (CheckBox)convertView.findViewById(R.id.cb_Check);
		final WanningBean m = wanningBean.get(position);
		//if((app.myPreferences.getBoolean(m.getName(), true)+"").equals("")){
		//	wanningBox.setChecked(true);
		//}else{
		wanningBox.setChecked(false);
		//}
		//System.out.println("dedede"+app.myPreferences.getBoolean(m.getName(), true));
		if(m!=null&&!m.equals("")){
		nameText.setText(m.getName());
		}
		for(int ai=0;ai<wanningBean.size();ai++){
     	   wanningBean1.put(wanningBean.get(ai).getName(), wanningBean.get(ai));
     	   
        }
       // for(int i=0;i<wanningBean.size();i++){
      	  // if(whetherWan.containsKey(wanningBean.get(i).getName())){
      		   //wanningBean1.add(wanningBean.get(i));
			if(whetherWan.containsKey(m.getLayerId())){
				//System.out.println("进入了"+m.getName());
				whetherWan.remove(m.getLayerId());
				whetherWan.put(m.getLayerId(),app.myPreferences.getBoolean(m.getLayerId(), true)+"");
				//System.out.println("测试"+whetherWan.get(m.getName()).equals("true"));
				//System.out.println("测试2"+whetherWan.get(m.getName()).equals(true));
			}else{
				//System.out.println("进入了"+m.getName());
     	   whetherWan.put(m.getLayerId(),app.myPreferences.getBoolean(m.getLayerId(), true)+"");
     	  //System.out.println("测试"+whetherWan.get(m.getName()).equals("true"));
     	 //System.out.println("测试2"+whetherWan.get(m.getName()).equals(true));
			}
      	  // } 
     // }
		wanningBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				  
                CheckBox cb = (CheckBox)v;  
				app.mEditor.putBoolean(m.getName(), cb.isChecked());
				app.mEditor.commit();
                //System.out.println("点击的报警名称"+app.myPreferences.getBoolean(m.getName(), true)+cb.isChecked());
                if(!whetherWan.isEmpty()){
               	// for(int i=0;i<whetherWan.size();i++){
               		 if(whetherWan.containsKey(m.getLayerId())){
               			 whetherWan.remove(m.getLayerId());
               			 whetherWan.put(m.getLayerId(), cb.isChecked()+"");
               			 //System.out.print("移除"+m.getName()+whetherWan.get(m.getName()));
               		 }else{
               			 whetherWan.put(m.getLayerId(), cb.isChecked()+"");
               			// System.out.print("加入了的报警名称"+m.getName()+"状态"+whetherWan.get(m.getName()));
               		 }
               	// }
                }else{
               	 whetherWan.put(m.getLayerId(), cb.isChecked()+"");
               	 //System.out.print("加入了的报警名称"+m.getName()+"状态"+whetherWan.get(m.getName()));
                }
                for(int i=0;i<wanningBean.size();i++){
             	   if(whetherWan.containsKey(wanningBean.get(i).getLayerId())){
             		   //wanningBean1.add(wanningBean.get(i));
     			 //System.out.print(wanningBean.get(i).getName()+whetherWan.get(wanningBean.get(i).getName()));
             	   } 
             }  
			}
		});
		return convertView;
	}*/
}
