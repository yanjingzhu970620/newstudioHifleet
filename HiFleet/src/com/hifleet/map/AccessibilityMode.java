package com.hifleet.map;

import android.content.Context;

import com.hifleet.plus.R;

public enum AccessibilityMode {

    ON(R.string.accessibility_on),
    OFF(R.string.accessibility_off),
    DEFAULT(R.string.accessibility_default);

    private final int key;

    AccessibilityMode(int key) {
        this.key = key;
    }

    public String toHumanString(Context ctx) {
        return ctx.getString(key);
    }

}
