package com.android.pennybank.util;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RoundImagesLoader extends AsyncTask {

    public interface TaskCompleted {
        void onTaskComplete(Integer id, Bitmap bitmap);
    }

    // This is static hash map for caching purpose.
    public static HashMap<Integer, RoundImage> mRoundImages = new HashMap<>();

    private Context mContext;
    private TaskCompleted mCallback;

    private Product mProduct;
    private Bitmap mBitmap;
    private Integer mId;

    /**
     * Constructor
     *
     * @param context Application's context reference
     * @param product Reference on recently added product in the database. Pass null value
     *                if you tend to load bitmaps of currently stored products, otherwise
     *                pass a reference on the recently added product.
     */
    public RoundImagesLoader(Context context, Product product) {
        super();
        mContext = context;
        mCallback = (TaskCompleted) context;
        mProduct = product;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if (mProduct == null) {
            ArrayList<Product> products = ProductDatabaseWrapper.getAllProducts();
            for (Product product : products) {
                loadBitmap(product.getId(), product.getImage());
            }
        } else {
            loadBitmap(mProduct.getId(), mProduct.getImage());
        }
        return null;
    }

    private void loadBitmap(Integer id, String imagePath) {
        mId = id;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            mBitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(imagePath));
        } catch (IOException e) {
            if (Logger.ENABLED) {
                Log.i(Logger.TAG, "Rounded image not initialized.");
            }
            e.printStackTrace();
        }
        publishProgress();
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        mCallback.onTaskComplete(mId, mBitmap);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
