package com.android.pennybank.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.util.Logger;
import com.android.pennybank.util.RoundImage;
import com.android.pennybank.data.RoundImagesLoader;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Product> mProducts;

    public CustomAdapter(Context context) {
        mContext = context;
        mProducts = ProductDatabaseWrapper.getAllProducts();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mProducts = ProductDatabaseWrapper.getAllProducts();
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return mProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mProducts.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_adapter_row_layout, parent, false);

        Product product = mProducts.get(position);

        ImageView productImage = (ImageView) row.findViewById(R.id.product_image);
        TextView productName = (TextView) row.findViewById(R.id.product_name_label);
        TextView savingStatus = (TextView) row.findViewById(R.id.saving_status);

//        RoundImage roundImage = RoundImagesLoader.mRoundImages.get(product.getId());
        Bitmap roundImage = RoundImagesLoader.mRoundImages.get(product.getId());
        if (roundImage == null) {
            productImage.setImageResource(R.drawable.pennybank_icon);
        } else {
//            TODO: This line throws Fatal signal 11 (SIGSEGV), code 1, fault addr ... in tid ... (RenderThread)
//            productImage.setImageDrawable(roundImage);
            productImage.setImageBitmap(roundImage);
        }
        productName.setText(product.getName());
        savingStatus.setText(product.getBalance() + " ¤ left.");

        return row;
    }

}

