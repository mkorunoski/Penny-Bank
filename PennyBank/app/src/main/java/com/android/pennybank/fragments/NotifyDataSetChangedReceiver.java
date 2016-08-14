package com.android.pennybank.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotifyDataSetChangedReceiver extends BroadcastReceiver {
    public static CustomAdapter mCustomAdapterRef;

    public NotifyDataSetChangedReceiver() {
    }

    public NotifyDataSetChangedReceiver(CustomAdapter customAdapter) {
        mCustomAdapterRef = customAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mCustomAdapterRef.notifyDataSetChanged();
    }
}
