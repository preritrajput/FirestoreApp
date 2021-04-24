package com.preritrajput.firestoreapp;

import android.graphics.drawable.Drawable;

public class AppUsageInfo {

        Drawable appIcon;
        String appName, packageName;
        long timeInForeground;
        int launchCount;

        AppUsageInfo(String pName) {
            this.packageName=pName;
        }
}
