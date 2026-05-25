package net.yuntal.autofish;

import java.util.ArrayList;
import java.util.List;

public class FishingStats {
    public static int totalCaught = 0;
    private static long startTime = -1;
    private static final List<Long> catchHistory = new ArrayList<>();

    public static void onCatch() {
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }
        totalCaught++;
        catchHistory.add(System.currentTimeMillis());

        long tenMinutesAgo = System.currentTimeMillis() - (10 * 60 * 1000);
        catchHistory.removeIf(t -> t < tenMinutesAgo);
    }

    public static double getCatchPerMinute() {
        if (catchHistory.size() < 2) return 0;
        long oldest = catchHistory.get(0);
        long newest = catchHistory.get(catchHistory.size() - 1);
        double minutes = (newest - oldest) / 60000.0;
        if (minutes < 0.01) return 0;
        return (catchHistory.size() - 1) / minutes;
    }

    public static double getCatchPerHour() {
        return getCatchPerMinute() * 60.0;
    }

    public static void reset() {
        totalCaught = 0;
        startTime = -1;
        catchHistory.clear();
    }
}