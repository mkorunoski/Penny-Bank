package com.android.pennybank.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.pennybank.util.Constants;
import com.android.pennybank.util.Util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A database of products.
 */
public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

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
    public static final String KEY_REMINDER_TIME = "reminderTime";
    public static final String KEY_ACTIVE = "active";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
                + KEY_END_DATE + " DATE,"
                + KEY_REMINDER_TIME + " DATE,"
                + KEY_ACTIVE + " INT" + ")";

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
        putValues(product, values);

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS,
                new String[]{KEY_ID, KEY_PRODUCT_NAME, KEY_PRODUCT_IMAGE, KEY_PRODUCT_PRICE,
                        KEY_DEPOSIT_FREQUENCY, KEY_SAVING_METHOD, KEY_DEPOSIT, KEY_SAVINGS,
                        KEY_START_DATE, KEY_END_DATE, KEY_REMINDER_TIME, KEY_ACTIVE},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Product product = initProduct(cursor);

        db.close();
        if (cursor != null) {
            cursor.close();
        }

        return product;
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                products.add(initProduct(cursor));
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return products;
    }

    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        putValues(product, values);

        int status = db.update(TABLE_PRODUCTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(product.getId())});

        db.close();

        return status;
    }

    private Product initProduct(Cursor cursor) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        Calendar reminderTime = Calendar.getInstance();
        try {
            startDate.setTime(Constants.DATE_FORMAT.parse(cursor.getString(8)));
            endDate.setTime(Constants.DATE_FORMAT.parse(cursor.getString(9)));
            reminderTime.setTime(Constants.HOUR_DATE_FORMAT.parse(cursor.getString(10)));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        Product product = new Product(
                context,
                Integer.parseInt(cursor.getString(0)),                                      // id
                cursor.getString(1),                                                        // name
                cursor.getString(2),                                                        // image
                Integer.parseInt(cursor.getString(3)),                                      // price
                Product.DEPOSIT_FREQUENCY.fromValue(Integer.parseInt(cursor.getString(4))), // frequency
                Product.SAVING_METHOD.fromValue(Integer.parseInt(cursor.getString(5))),     // method
                Integer.parseInt(cursor.getString(6)),                                      // deposit
                Integer.parseInt(cursor.getString(7)),                                      // savings
                startDate,                                                                  // startDate
                endDate,                                                                    // endDate
                reminderTime,                                                               // reminderTime
                Util.intToBoolean(Integer.parseInt(cursor.getString(11)))                   // active
        );

        return product;
    }

    private void putValues(Product product, ContentValues values) {
        values.put(KEY_ID, product.getId());
        values.put(KEY_PRODUCT_NAME, product.getName());
        values.put(KEY_PRODUCT_IMAGE, product.getImage());
        values.put(KEY_PRODUCT_PRICE, product.getPrice());
        values.put(KEY_DEPOSIT_FREQUENCY, product.getDepositFrequency().getValue());
        values.put(KEY_SAVING_METHOD, product.getSavingMethod().getValue());
        values.put(KEY_DEPOSIT, product.getDeposit());
        values.put(KEY_SAVINGS, product.getSavings());
        values.put(KEY_START_DATE, Constants.DATE_FORMAT.format(product.getStartDate().getTime()));
        values.put(KEY_END_DATE, Constants.DATE_FORMAT.format(product.getEndDate().getTime()));
        values.put(KEY_REMINDER_TIME, Constants.HOUR_DATE_FORMAT.format(product.getReminderTime().getTime()));
        values.put(KEY_ACTIVE, Util.booleanToInt(product.isActive()));
    }


}
