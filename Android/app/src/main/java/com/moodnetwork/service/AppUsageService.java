package com.moodnetwork.service;

import android.app.job.*;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;

import android.os.AsyncTask;
import android.util.Log;

import com.moodnetwork.MoodNetworkApplication;
import com.moodnetwork.database.MongoDB;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppUsageService extends JobService {
    private static final String TAG = AppUsageService.class.getCanonicalName();
    private static final long DAY_IN_MILLIS = 86400000;
    private static final long DAY_IN_SECS = 86400;

    class AppUsageTask extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(getApplicationContext().USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - DAY_IN_MILLIS;
            List<UsageStats> queryUsageStats = mUsageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime,
                            endTime);
            Log.i(TAG, "Usage size is " + queryUsageStats.size());
            //@TODO add it to database
            for (int i = 0; i < Math.min(30, queryUsageStats.size()); ++i) {
                UsageStats usage = queryUsageStats.get(i);
                Log.i(TAG, i + ") " + usage.getPackageName() +
                        ": total foreground time=" + usage.getTotalTimeInForeground());
            }
            jobFinished(params[0], true);
            if (queryUsageStats.size() != 0) {
                MongoDB.getInstance().insertAppUsageData(queryUsageStats);
            }
            return null;
        }
    }
    public static void startService() {
        Log.i(TAG, "Scheduling app usage service");
        ComponentName serviceComponent = new ComponentName(MoodNetworkApplication.getContext(), AppUsageService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(DAY_IN_SECS * 100);
        builder.setOverrideDeadline(DAY_IN_SECS * 101);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        JobScheduler jobScheduler = MoodNetworkApplication.getContext().getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "Starting App usage job");
        new AppUsageTask().execute(params, null, null);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "Finishing App usage job");
        return false;
    }
}
