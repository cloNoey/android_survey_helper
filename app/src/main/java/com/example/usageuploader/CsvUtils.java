package com.example.usageuploader;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvUtils {
    public static File generateUsageCsv(Context context) throws Exception {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long end = System.currentTimeMillis();
        long begin = end - (1000L * 60 * 60 * 24 * 7); // 1주일

        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begin, end);
        File file = new File(context.getExternalFilesDir(null), "usage.csv");

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write("package,firstTime,lastTime,totalTime\n");
            for (UsageStats us : stats) {
                writer.write(us.getPackageName() + "," +
                        us.getFirstTimeStamp() + "," +
                        us.getLastTimeStamp() + "," +
                        us.getTotalTimeInForeground() + "\n");
            }
        }
        return file;
    }
}