package pl.wieczorekp.lifeishard.network.database;

import java.util.Calendar;

public class Utils {
    public static int getCurrentTime() {
        return (int) (Calendar.getInstance().getTimeInMillis() / 1000);
    }
}
