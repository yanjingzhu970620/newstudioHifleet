package com.hifleet.map;

import java.util.Locale;

public class OsmAndCollator {

	public static com.hifleet.map.Collator primaryCollator() {
		// romanian locale encounters diacritics as differnet symbols
		final java.text.Collator instance = Locale.getDefault().getLanguage().equals("ro")  ||
				Locale.getDefault().getLanguage().equals("sk")? java.text.Collator.getInstance(Locale.US)
				: java.text.Collator.getInstance();
		instance.setStrength(java.text.Collator.PRIMARY);
		return wrapCollator(instance);
	}
	
	public static com.hifleet.map.Collator wrapCollator(final java.text.Collator instance) {
		return new com.hifleet.map.Collator() {
			
			@Override
			public int compare(Object o1, Object o2) {
				return instance.compare(o1, o2);
			}
			
			@Override
			public boolean equals(Object obj) {
				return instance.equals(obj);
			}

			@Override
			public boolean equals(String source, String target) {
				return instance.equals(source, target);
			}

			@Override
			public int compare(String source, String target) {
				return instance.compare(source, target);
			}
		};
	}
}
