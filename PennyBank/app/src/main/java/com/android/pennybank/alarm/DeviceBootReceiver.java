package com.android.pennybank.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.util.Util;

import java.util.ArrayList;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ArrayList<Product> products = ProductDatabaseWrapper.getAllProducts();
            for (Product product : products) {
                Util.startAlarm(context, product);
            }
        }
    }

}
