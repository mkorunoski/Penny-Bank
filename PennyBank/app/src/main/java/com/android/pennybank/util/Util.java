package com.android.pennybank.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.android.pennybank.alarm.AlarmReceiver;
import com.android.pennybank.data.Product;

import java.util.Calendar;

public abstract class Util {

    public static String getPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static void startAlarm(Context context, Product product) {
        long intervalMillis = AlarmManager.INTERVAL_DAY;
        switch (product.getDepositFrequency()) {
            case WEEKLY: {
                intervalMillis *= 7;
                break;
            }
            case MONTHLY: {
                intervalMillis *= Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
                break;
            }
        }
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.KEY_PRODUCT_ID, product.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, product.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, product.getReminderTime().getTimeInMillis(), intervalMillis, pendingIntent);
        if (Logger.ENABLED) {
            Log.i(Logger.TAG, "Reminder set to: " + product.getReminderTime().getTime());
        }
    }

    /*
    * Increments the number of product instances and returns it.
     */
    public static int incrementProductInstances(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Product Instances Counter", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int productInstances = sharedPreferences.getInt("Product Instances", -1);
        if (productInstances == -1) {
            productInstances = 0;
        } else {
            productInstances++;
        }
        editor.putInt("Product Instances", productInstances);
        editor.commit();
        return productInstances;
    }
}
