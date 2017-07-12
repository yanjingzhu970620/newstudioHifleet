package com.hifleet.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.hifleet.data.IndexItem;
import com.hifleet.map.Collator;
import com.hifleet.map.OsmAndCollator;
import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;


public class IndexItemCategory implements Comparable<IndexItemCategory> {
	public final String name;
	public final List<IndexItem> items = new ArrayList<IndexItem>();
	private final int order;

	public IndexItemCategory(String name, int order) {
		this.name = name;
		this.order = order;
	}

	@Override
	public int compareTo(IndexItemCategory another) {
		return order < another.order ? -1 : 1;
	}

	public static List<IndexItemCategory> categorizeIndexItems(final OsmandApplication ctx, 
			Collection<IndexItem> indexItems) {
		//System.out.println("categorizeIndexItems function body.");
		boolean skipWiki = true;//Version.isFreeVersion(ctx);
		final Map<String, IndexItemCategory> cats = new TreeMap<String, IndexItemCategory>();
		for (IndexItem i : indexItems) {
			int nameId = R.string.index_name_other;
			int order = 0;
			String lc = i.getFileName().toLowerCase();
			if (lc.endsWith(".voice.zip")) {
				continue;
//				nameId = R.string.index_name_voice;
//				order = 1;
			} 
			else if (lc.contains(".ttsvoice.zip")) {
				continue;
//				nameId = R.string.index_name_tts_voice;
//				order = 2;
			} 
			else if (lc.contains("_wiki_")) {
				continue;
//				if(skipWiki) {
//					continue;
//				}
//				nameId = R.string.index_name_wiki;
//				order = 10;
			} 
			else if (lc.startsWith("us") || 
					(lc.contains("united states") && lc.startsWith("north-america")) ) {
				continue;
//				nameId = R.string.index_name_us;
//				order = 31;
			} 
			else if (lc.startsWith("canada")) {
				continue;
//				nameId = R.string.index_name_canada;
//				order = 32;
			} 
			else 
				if (lc.contains("openmaps")) {
					continue;
//				nameId = R.string.index_name_openmaps;
//				order = 90;
			} 
			else if (lc.contains("northamerica") || lc.contains("north-america")) {
				continue;
//				nameId = R.string.index_name_north_america;
//				order = 30;
			} 
			else if (lc.contains("centralamerica") || lc.contains("central-america")
					|| lc.contains("caribbean")) {
				continue;
//				nameId = R.string.index_name_central_america;
//				order = 40;
			} else if (lc.contains("southamerica") || lc.contains("south-america")) {
				continue;
//				nameId = R.string.index_name_south_america;
//				order = 45;
			} else if ( lc.contains("germany")) {
				continue;
//				nameId = R.string.index_name_germany;
//				order = 16;
			} else if (lc.startsWith("france_")) {
				continue;
//				nameId = R.string.index_name_france;
//				order = 17;
			} else if (lc.startsWith("italy_")) {
				continue;
//				nameId = R.string.index_name_italy;
//				order = 18;
			} else if (lc.startsWith("gb_") || lc.startsWith("british")) {
				continue;
//				nameId = R.string.index_name_gb;
//				order = 19;
			} else if (lc.contains("russia")) {
				continue;
//				nameId = R.string.index_name_russia;
//				order = 25;
			} else if (lc.contains("europe")) {
				continue;
//				nameId = R.string.index_name_europe;
//				order = 15;
			} else if (lc.contains("africa") && !lc.contains("_wiki_")) {
				continue;
//				nameId = R.string.index_name_africa;
//				order = 80;
			} else if (lc.contains("_asia")|| lc.startsWith("asia")) {
				continue;
//				nameId = R.string.index_name_asia;
//				order = 50;
			} else if (lc.contains("oceania") || lc.contains("australia")) {
				continue;
//				nameId = R.string.index_name_oceania;
//				order = 70;
			}

			String name = ctx.getString(nameId);
			if (!cats.containsKey(name)) {
				cats.put(name, new IndexItemCategory(name, order));
			}
			cats.get(name).items.add(i);
			//System.out.println("cats get name items.add i： "+name);
		}
		ArrayList<IndexItemCategory> r = new ArrayList<IndexItemCategory>(cats.values());
		final Collator collator = OsmAndCollator.primaryCollator();
		for(IndexItemCategory ct : r) {
			Collections.sort(ct.items, new Comparator<IndexItem>() {
				@Override
				public int compare(IndexItem lhs, IndexItem rhs) {
					//System.out.println("getVisibleName in compare in IndexItemCategory class.");
					return collator.compare(lhs.getVisibleName(ctx),
							rhs.getVisibleName(ctx));
				}
			});
		}
		Collections.sort(r);
		return r;
	}
}
