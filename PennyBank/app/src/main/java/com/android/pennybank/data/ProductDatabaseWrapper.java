package com.android.pennybank.data;

import android.content.Context;

import java.util.ArrayList;

public abstract class ProductDatabaseWrapper {

    private static ProductDatabaseHelper mProductDatabase;

    public static void initDatabase(Context context) {
        mProductDatabase = new ProductDatabaseHelper(context);
    }

    public static void addProduct(Product product) {
        mProductDatabase.addProduct(product);
    }

    public static Product getProduct(int id) {
        return mProductDatabase.getProduct(id);
    }

    public static ArrayList<Product> getAllProducts() {
        return mProductDatabase.getAllProducts();
    }

    public static void updateProduct(Product product) {
        mProductDatabase.updateProduct(product);
    }

}
