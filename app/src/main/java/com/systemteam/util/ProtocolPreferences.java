package com.systemteam.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ProtocolPreferences {
    private static final String DB_PROTOCOL = "db_protocol";
    private static final String PROTOCOL_CAR_COUNT = "spf_car_count";

    public static void setCarCount(Context context, int count) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putInt(PROTOCOL_CAR_COUNT, count).apply();
    }

    public static int getCarCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getInt(PROTOCOL_CAR_COUNT, 0);
    }

}
