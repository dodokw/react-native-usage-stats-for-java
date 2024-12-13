package com.usagestats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.Calendar;
import java.util.Locale;

public class UsageStatsManagerUsageUtils {

    public static final long USAGE_TIME_MIX = 5000L;

    public static String humanReadableMillis(long milliSeconds) {
        long seconds = milliSeconds / 1000;
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else {
            return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m " + (seconds % 60) + "s";
        }
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static boolean openable(PackageManager packageManager, String packageName) {
        return packageManager.getLaunchIntentForPackage(packageName) != null;
    }

    public static boolean isSystemApp(PackageManager manager, String packageName) {
        boolean isSystemApp = false;
        try {
            ApplicationInfo applicationInfo = manager.getApplicationInfo(packageName, 0);
            isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
                          (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return isSystemApp;
    }

    public static boolean isInstalled(PackageManager packageManager, String packageName) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationInfo != null;
    }

    public static Drawable parsePackageIcon(String packageName, int defaultIcon, Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            return manager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return context.getResources().getDrawable(defaultIcon);
        }
    }

    public static CharSequence parsePackageName(PackageManager pckManager, String data) {
        ApplicationInfo applicationInformation;
        try {
            applicationInformation = pckManager.getApplicationInfo(data, PackageManager.GET_META_DATA);
            CharSequence appLabel = applicationInformation.loadLabel(pckManager);
            return appLabel != null && appLabel.length() > 0 ? appLabel : data;
        } catch (PackageManager.NameNotFoundException e) {
            return getAppName(data);
        }
    }

    public static String getAppName(String packageName) {
        switch (packageName) {
            case "com.brighthustle.ark": return "Ark";
            case "com.android.chrome": return "Google Chrome";
            case "com.whatsapp": return "WhatsApp";
            // Add other cases as needed
            default: return packageName;
        }
    }

    public static int getAppUid(PackageManager packageManager, String packageName) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Add methods for getTimeRange and other helper methods here
}