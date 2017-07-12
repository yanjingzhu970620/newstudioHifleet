package com.hifleet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hifleet.plus.R;

/**
 * @{# ShipDetailsAdapter.java Create on 2015年7月14日 下午5:44:56
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShipDetailsAdapter extends BaseExpandableListAdapter {
	private Context context;
	private String[] firstDetails;
	private String[][] secondDetails;
	private String[][] tips;

	public ShipDetailsAdapter(Context context, String[] firstDetails,
			String[][] secondDetails, String[][] tips) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.firstDetails = firstDetails;
		this.secondDetails = secondDetails;
		this.tips = tips;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return firstDetails.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return secondDetails[groupPosition].length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return firstDetails[groupPosition];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return secondDetails[groupPosition][childPosition];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_ship_details, null);
		TextView nameText = (TextView) convertView
				.findViewById(R.id.text_shipdetial_name);
		ImageView img = (ImageView) convertView
				.findViewById(R.id.include_arrow_right);
		if (isExpanded) {
			img.setImageResource(R.drawable.icon_arrow_up);
		} else {
			img.setImageResource(R.drawable.icon_arrow_down);
		}
		nameText.setText(getGroup(groupPosition).toString());
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_ship_more_details, null);
		TextView nameText = (TextView) convertView
				.findViewById(R.id.text_ship_moredetails_name);
		TextView tipText = (TextView) convertView.findViewById(R.id.text_tips);
		tipText.setText(tips[groupPosition][childPosition]);
		if(getChild(groupPosition, childPosition) != null){
			nameText.setText(getChild(groupPosition, childPosition).toString());
		}else {
			nameText.setText(" ");
		}
		
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}
