package com.android.pennybank;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

/**
 * A database of products.
 */
public class ProductDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "productsContainer";

    public static final String TABLE_PRODUCTS = "products";

    public static final String KEY_ID = "id";
    public static final String KEY_PRODUCT_NAME = "productName";
    public static final String KEY_PRODUCT_IMAGE = "productImage";
    public static final String KEY_PRODUCT_PRICE = "productPrice";
    public static final String KEY_DEPOSIT_FREQUENCY = "depositFrequency";
    public static final String KEY_SAVING_METHOD = "savingMethod";
    public static final String KEY_DEPOSIT = "deposit";
    public static final String KEY_SAVINGS = "savings";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_PRODUCT_NAME + " TEXT,"
                + KEY_PRODUCT_IMAGE + " TEXT,"
                + KEY_PRODUCT_PRICE + " REAL,"
                + KEY_DEPOSIT_FREQUENCY + " INT,"
                + KEY_SAVING_METHOD + " INT,"
                + KEY_DEPOSIT + " REAL,"
                + KEY_SAVINGS + " REAL,"
                + KEY_START_DATE + " DATE,"
                + KEY_END_DATE + " DATE" + ")";

        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getId());
        values.put(KEY_PRODUCT_NAME, product.getName());
        values.put(KEY_PRODUCT_IMAGE, product.getImage());
        values.put(KEY_PRODUCT_PRICE, product.getPrice());
        values.put(KEY_DEPOSIT_FREQUENCY, product.getFrequency().getValue());
        values.put(KEY_SAVING_METHOD, product.getMethod().getValue());
        values.put(KEY_DEPOSIT, product.getDeposit());
        values.put(KEY_SAVINGS, product.getSavings());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = dateFormat.format(product.getStartDate().getTime());
        values.put(KEY_START_DATE, startDate);
        String endDate = dateFormat.format(product.getStartDate().getTime());
        values.put(KEY_END_DATE, endDate);

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

}
