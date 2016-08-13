package com.android.pennybank.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.android.pennybank.util.RoundImage;
import com.android.pennybank.util.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class RoundImagesLoader extends AsyncTask<Void, Void, Void> {
    // This is static hash map for caching purpose.
//    public static HashMap<Integer, RoundImage> mRoundImages = new HashMap<>();
    public static HashMap<Integer, Bitmap> mRoundImages = new HashMap<>();

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
    public RoundImagesLoader(Context context, Product product) {
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
//            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(imagePath));
            bitmap = getThumbnail(Uri.parse(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            // =====================================================================================
            // Vcituvanjeto na bitmapa e dolg proces, zatoa ke gi kesirame.
            // =====================================================================================
//            mRoundImages.put(id, new RoundImage(bitmap));
            mRoundImages.put(id, bitmap);
//            bitmap.recycle();
        }
    }

    public Bitmap getThumbnail(Uri uri) throws IOException {
        final int THUMBNAIL_SIZE = 128;
        InputStream input = mContext.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        input = mContext.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return getCroppedBitmap(bitmap);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

}
