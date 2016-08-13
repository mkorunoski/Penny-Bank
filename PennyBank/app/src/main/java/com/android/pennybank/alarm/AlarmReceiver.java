package com.android.pennybank.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.util.Constants;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Product product = ProductDatabaseWrapper.getProduct(intent.getExtras().getInt(Constants.KEY_PRODUCT_ID));

        Intent i = new Intent(context, NotificationButtonClickReceiver.class);
        i.putExtra(Constants.KEY_PRODUCT_ID, product.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, product.getId(), i, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.pennybank_icon)
                .setContentTitle(product.getName())
                .setContentText("Deposit " + product.getDeposit() + " ¤ in your penny bank!")
                .addAction(R.drawable.pennybank_icon, "Deposit", pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(product.getId(), builder.build());
    }

}
