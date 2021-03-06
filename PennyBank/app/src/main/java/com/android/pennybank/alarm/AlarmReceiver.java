package com.android.pennybank.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.util.Constants;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Product product = ProductDatabaseWrapper.getProduct(intent.getExtras().getInt(Constants.KEY_PRODUCT_ID));

        Intent i = new Intent(context, NotificationButtonClickReceiver.class);
        i.putExtra(Constants.KEY_PRODUCT_ID, product.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, product.getId(), i, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.pennybank_icon)
                .setContentTitle(product.getName())
                .setContentText("Deposit " + product.getDeposit() + " " + Constants.CURRENCY_SIGN + " in your penny bank!")
                .addAction(R.drawable.deposit, "Deposit", pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(product.getId(), builder.build());
    }

}
