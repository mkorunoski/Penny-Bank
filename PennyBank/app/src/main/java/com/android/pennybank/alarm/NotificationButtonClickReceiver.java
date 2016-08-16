package com.android.pennybank.alarm;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.util.Constants;

public class NotificationButtonClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Product product = ProductDatabaseWrapper.getProduct(intent.getExtras().getInt(Constants.KEY_PRODUCT_ID));
        product.deposit();
        ProductDatabaseWrapper.updateProduct(product);
        Toast.makeText(context,
                "You deposited: " + product.getDeposit() + " " + Constants.CURRENCY_SIGN + ". " +
                        "Your balance is: " + product.getBalance() + " " + Constants.CURRENCY_SIGN + ". ",
                Toast.LENGTH_LONG).show();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(product.getId());

        if (product.getBalance() <= 0) {
            // TODO: You have enough savings to purchase the product. Notify the user.
            Toast.makeText(context, "You can now purchase " + product.getName() + ".", Toast.LENGTH_SHORT).show();
            ProductDatabaseWrapper.deleteProduct(product);
        }
    }
}