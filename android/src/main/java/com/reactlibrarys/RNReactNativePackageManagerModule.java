package com.reactlibrarys;

import android.content.Intent;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import java.text.SimpleDateFormat;

import android.util.Log;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

public class RNReactNativePackageManagerModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNReactNativePackageManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public boolean isAvailable(String id) {
        PackageManager pm = reactContext.getPackageManager();
        try {
            pm.getPackageInfo(id, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    @ReactMethod
    public void startApplication(String id) {
        Intent intent1 = reactContext.getPackageManager().getLaunchIntentForPackage(id);
        intent1.putExtra("extern", true);
        reactContext.startActivity(intent1);
    }

    @ReactMethod
    public void isExisting(String id, Promise promise) {
        PackageManager pm = reactContext.getPackageManager();
        try {
            pm.getPackageInfo(id, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            promise.resolve(false);
        }
        promise.resolve(true);
    }

    public static String joinStringList(String joiner, List items) {
        String joined = new String();

        for (int i = 0; i < items.size(); i++) {
            joined += items.get(i);
            if (i < items.size() - 1) {
                joined += joiner;
            }
        }

        return joined;
    }

    public static Map <String, UsageStats> getAggregateStatsMap(Context context, int durationInDays) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        List dates = getDates(durationInDays);
        long startTime = (long) dates.get(0);
        long endTime = (long) dates.get(1);

        Map <String, UsageStats> aggregateStatsMap = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime);
        return aggregateStatsMap;
    }

    public static List getDates(int durationInDays) {
        List dates = getDateRangeFromNow(Calendar.DATE, -(durationInDays));

        return dates;
    }

    public static List getDateRangeFromNow(int field, int amount) {
        List dates = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(field, amount);
        long startTime = calendar.getTimeInMillis();

        dates.add(startTime);
        dates.add(endTime);

        return dates;
    }

    public static String getStatsString(Map <String, UsageStats> aggregateStats) {

        List statsCollection = new ArrayList();
        List appsCollection = new ArrayList();

        for (Map.Entry < String, UsageStats > entry: aggregateStats.entrySet()) {
            appsCollection.add(entry.getValue().getPackageName());
            statsCollection.add(entry.getValue().getTotalTimeInForeground());
        }

        String stats = joinStringList(",", statsCollection);;
        String apps = joinStringList(",", appsCollection);;

        String res = apps + ";" + stats;

        return res;
    }

    @ReactMethod
    public void getStats(
        int durationInDays,
        Promise promise) {
        if (durationInDays > 0) {
            try {
                String stats = getStatsString(getAggregateStatsMap(reactContext, durationInDays));
                promise.resolve(stats);
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                promise.resolve(errorMessage);
            }
        } else {
            String noticeMessage = "Enter an integer greater than 0!";
            promise.resolve(noticeMessage);
        }
    }

    @ReactMethod
    public void queryEvents(String packageName, String startDate, String endDate, Promise promise) {
        try {
            long start = Long.parseLong(startDate);
            long end = Long.parseLong(endDate);
            String query = onQueryEvents(reactContext, packageName, start, end);
            promise.resolve(query);
        } catch (Exception e) {
            String noticeMessage = "Enter a string in milliseconds";
            promise.reject(noticeMessage);
        }
    }

    public static String onQueryEvents(Context context, String packageId, long startTime, long endTime) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        UsageEvents uEvents = usm.queryEvents(startTime, endTime);
        List games = new ArrayList();

        while (uEvents.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);

            if (e != null) {
                if (e.getPackageName().equals(packageId)) {
                    games.add("{\"eventType\": " + e.getEventType() + " , \"timeStamp\": \"" + String.valueOf(e.getTimeStamp()) + "\"}");
                }
            }
        }

        String gamesString = joinStringList("$", games);

        return gamesString;
    }

    @Override
    public String getName() {
        return "RNReactNativePackageManager";
    }
}