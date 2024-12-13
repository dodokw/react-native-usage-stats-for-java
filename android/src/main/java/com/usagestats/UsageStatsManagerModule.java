package com.usagestats;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.EventStats;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;

import java.util.List;
import java.util.Map;

public class UsageStatsManagerModule extends ReactContextBaseJavaModule {

    private final Context reactContext;

    @RequiresApi(Build.VERSION_CODES.M)
    private NetworkStatsManager networkStatsManager;

    public UsageStatsManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.networkStatsManager = (NetworkStatsManager) reactContext.getSystemService(Context.NETWORK_STATS_SERVICE);
    }

    @Override
    public String getName() {
        return "UsageStatsManagerModule";
    }

    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = MapBuilder.newHashMap();
        constants.put("INTERVAL_WEEKLY", UsageStatsManager.INTERVAL_WEEKLY);
        constants.put("INTERVAL_MONTHLY", UsageStatsManager.INTERVAL_MONTHLY);
        constants.put("INTERVAL_YEARLY", UsageStatsManager.INTERVAL_YEARLY);
        constants.put("INTERVAL_DAILY", UsageStatsManager.INTERVAL_DAILY);
        constants.put("INTERVAL_BEST", UsageStatsManager.INTERVAL_BEST);
        constants.put("TYPE_WIFI", ConnectivityManager.TYPE_WIFI);
        constants.put("TYPE_MOBILE", ConnectivityManager.TYPE_MOBILE);
        constants.put("TYPE_MOBILE_AND_WIFI", Integer.MAX_VALUE);
        return constants;
    }

    private boolean packageExists(String packageName) {
        PackageManager packageManager = reactContext.getPackageManager();
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void showUsageAccessSettings(String packageName) {
        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        if (packageExists(packageName)) {
            intent.setData(Uri.fromParts("package", packageName, null));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            reactContext.startActivity(intent);
        }
    }

    @ReactMethod
    public void queryUsageStats(int interval, double startTime, double endTime, Promise promise) {
        PackageManager packageManager = reactContext.getPackageManager();
        WritableMap result = new WritableNativeMap();
        UsageStatsManager usageStatsManager = (UsageStatsManager) reactContext.getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(interval, (long) startTime, (long) endTime);
        
        for (UsageStats us : queryUsageStats) {
            if (us.getTotalTimeInForeground() > 0) {
                WritableMap usageStats = new WritableNativeMap();
                usageStats.putString("packageName", us.getPackageName());
                double totalTimeInSeconds = us.getTotalTimeInForeground() / 1000.0;
                usageStats.putDouble("totalTimeInForeground", totalTimeInSeconds);
                usageStats.putDouble("firstTimeStamp", us.getFirstTimeStamp());
                usageStats.putDouble("lastTimeStamp", us.getLastTimeStamp());
                usageStats.putDouble("lastTimeUsed", us.getLastTimeUsed());
                usageStats.putBoolean("isSystem", isSystemApp(us.getPackageName()));
                usageStats.putString("appName", UsageStatsManagerUsageUtils.parsePackageName(packageManager, us.getPackageName()).toString());

                result.putMap(us.getPackageName(), usageStats);
            }
        }
        
        promise.resolve(result);
    }

private boolean isSystemApp(String packageName) {
    PackageManager packageManager = reactContext.getPackageManager();
    return UsageStatsManagerUsageUtils.isSystemApp(packageManager, packageName);
}
}