package com.systemteam.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ProtocolPreferences {
    private static final String DB_PROTOCOL = "db_protocol";
    private static final String PROTOCOL_CAR_COUNT = "spf_car_count";
    private static final String PROTOCOL_WITHDRAW_BALANCE = "spf_withdraw_balance";

    public static void setCarCount(Context context, int count) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putInt(PROTOCOL_CAR_COUNT, count).apply();
    }

    public static int getCarCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getInt(PROTOCOL_CAR_COUNT, 0);
    }

    public static void setIsWithdrawBalance(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putBoolean(PROTOCOL_WITHDRAW_BALANCE, value).apply();
    }

    public static boolean getIsWithdrawBalance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getBoolean(PROTOCOL_WITHDRAW_BALANCE, false);
    }

}
