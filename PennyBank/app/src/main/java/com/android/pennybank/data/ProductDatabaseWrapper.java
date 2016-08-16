package com.android.pennybank.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.android.pennybank.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ProductDatabaseWrapper {

    private static Context mContext;
    private static ProductDatabaseHelper mProductDatabase;
    //    This is static hash map for caching purpose.
    public static HashMap<Integer, Bitmap> mBitmaps;

    public static void initDatabase(Context context) {
        mContext = context;
        mProductDatabase = new ProductDatabaseHelper(mContext);
        mBitmaps = new HashMap<>();
        new BitmapsLoader(mContext, null).execute();
    }

    public static void addProduct(Product product) {
        mProductDatabase.addProduct(product);
        new BitmapsLoader(mContext, product).execute();
        Util.startAlarm(mContext, product);
    }

    public static Product getProduct(int id) {
        return mProductDatabase.getProduct(id);
    }

    public static ArrayList<Product> getAllProducts() {
        return mProductDatabase.getAllProducts();
    }

    public static void deleteProduct(Product product) {
        mProductDatabase.deleteProduct(product.getId());
        mBitmaps.remove(product.getId());
        Util.cancelAlarm(mContext, product);
        mContext.sendBroadcast(new Intent().setAction("com.android.pennybank.notifyDataSetChanged"));
    }

    public static void updateProduct(Product product) {
        mProductDatabase.updateProduct(product);
        mContext.sendBroadcast(new Intent().setAction("com.android.pennybank.notifyDataSetChanged"));
    }

}
