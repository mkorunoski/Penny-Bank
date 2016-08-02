package com.android.pennybank.data;

import android.content.Context;

import java.util.ArrayList;

public abstract class ProductDatabaseWrapper {

    private static ProductDatabaseHelper mProductDatabase;

    public static void initDatabase(Context context) {
        mProductDatabase = new ProductDatabaseHelper(context);
        // REMOVE THESE
        mProductDatabase.addProduct(new Product("Computer", "electronics", 32000.0f, Product.DEPOSIT_FREQUENCY.MONTHLY, 500.0f));
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
