/**    
 * @{#} BaseListAdapter.java Create on 2014-5-30 下午7:54:44    
 *          
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0    
 */
package com.e.common.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseListAdapter extends android.widget.BaseAdapter {

	public LayoutInflater inflater;
	public Activity activity;
	public ArrayList<?> list;

	public BaseListAdapter(Activity activity, ArrayList<?> list) {
		inflater = LayoutInflater.from(activity);
		this.activity = activity;
		this.list = list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
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
		return null;
	}

	public void notifyUpdate() {
		notifyDataSetChanged();
	}

	public void notifyUpdate(ArrayList<Object> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void removeObject(Object object) {
		list.remove(object);
		notifyUpdate();
	}

	public void removeIndex(int index) {
		list.remove(index);
		notifyUpdate();
	}
	
	public void clear() {
		list.clear();
		notifyDataSetChanged();
	}
}
