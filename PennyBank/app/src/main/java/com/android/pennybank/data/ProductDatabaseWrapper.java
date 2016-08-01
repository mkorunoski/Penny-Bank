package com.android.pennybank.data;

import android.content.Context;

import java.util.ArrayList;

public abstract class ProductDatabaseWrapper {

    private static ProductDatabaseHelper mProducDatabase;

    public static void initDatabase(Context context)
    {
        mProducDatabase = new ProductDatabaseHelper(context);
        // REMOVE THESE
        mProducDatabase.addProduct(new Product("Computer", "electronics", 32000.0f, Product.FREQUENCY.MONTHLY, 500.0f));
        mProducDatabase.addProduct(new Product("Phone", "electronics", 15000.0f, Product.FREQUENCY.DAILY, 80.0f));
        mProducDatabase.addProduct(new Product("Shirt", "fashion", 1500.0f, Product.FREQUENCY.DAILY, 100.0f));
        mProducDatabase.addProduct(new Product("Vocation", "travel", 18500.0f, Product.FREQUENCY.MONTHLY, 450.0f));
        mProducDatabase.addProduct(new Product("Shoes", "fashion", 2800.0f, Product.FREQUENCY.DAILY, 120.0f));
        mProducDatabase.addProduct(new Product("Head Phones", "electronics", 600.0f, Product.FREQUENCY.DAILY, 65.0f));
    }

    public static ArrayList<Product> getProductsFromDatabase() {
        return mProducDatabase.getAllProducts();
    }

    public static Product getProductFromDatabase(int id) {
        return mProducDatabase.getProduct(id);
    }
}
