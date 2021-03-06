package com.android.pennybank.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.android.pennybank.alarm.AlarmReceiver;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public abstract class Util {

    public static Bitmap getThumbnail(Context context, Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        final int THUMBNAIL_SIZE = 128;
        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return getCroppedBitmap(bitmap);
    }

    private static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) {
            return 1;
        } else {
            return k;
        }
    }

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
        Calendar reminderTime = product.getReminderTime();
//      If the reminder time is smaller then the current time, the alarm manager will broadcast
//      message to the receivers. This is why this condition is needed, so we can avoid notifying
//      the user by adding the proper amount (based on deposit frequency) to the current reminder time.
        if (reminderTime.getTimeInMillis() < System.currentTimeMillis()) {
            switch (product.getDepositFrequency()) {
                case DAILY: {
                    reminderTime.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                }
                case WEEKLY: {
                    reminderTime.add(Calendar.WEEK_OF_MONTH, 1);
                    break;
                }
                case MONTHLY: {
                    reminderTime.add(Calendar.MONTH, 1);
                    break;
                }
            }
            ProductDatabaseWrapper.updateProduct(product);
        }
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                product.getReminderTime().getTimeInMillis(),
                intervalMillis,
                pendingIntent);
        if (Logger.ENABLED) {
            Log.i(Logger.TAG, "Reminder set to: " + product.getReminderTime().getTime());
        }
    }

    public static void cancelAlarm(Context context, Product product) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, product.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
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

    public static boolean intToBoolean(int i) {
        if (i == 0) {
            return false;
        }
        return true;
    }

    public static int booleanToInt(boolean b) {
        if (b == false) {
            return 0;
        }
        return 1;
    }

}
