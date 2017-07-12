package com.hifleet.adapter;

import android.view.View;
import android.widget.BaseExpandableListAdapter;

public abstract class OsmandBaseExpandableListAdapter2 extends BaseExpandableListAdapter{
	protected void adjustIndicator(int groupPosition, boolean isExpanded, View row) {
		if (!isExpanded) {
			if (getChildrenCount(groupPosition) == 0) {
			} else {
			}
		} else {
		}
	}
}
