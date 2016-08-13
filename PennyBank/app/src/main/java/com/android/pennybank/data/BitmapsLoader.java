package com.android.pennybank.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.android.pennybank.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BitmapsLoader extends AsyncTask<Void, Void, Void> {
//    This is static hash map for caching purpose.
    public static HashMap<Integer, Bitmap> mBitmaps = new HashMap<>();

    private Context mContext;
    private Product mProduct;

    /**
     * Constructor
     *
     * @param context Application's context reference
     * @param product Reference on recently added product in the database. Pass null value
     *                if you tend to load bitmaps of currently stored products, otherwise
     *                pass a reference on the recently added product.
     */
    public BitmapsLoader(Context context, Product product) {
        super();
        mContext = context;
        mProduct = product;
    }

    @Override
    protected Void doInBackground(Void... params) {
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
        Bitmap bitmap = null;
        try {
            bitmap = Util.getThumbnail(mContext, Uri.parse(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
//            Vcituvanjeto na bitmapa e dolg proces, zatoa ke gi kesirame.
            mBitmaps.put(id, bitmap);
        }
    }

}
