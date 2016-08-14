package com.android.pennybank.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;

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

        final Product product = mProducts.get(position);

        ImageView productImage = (ImageView) row.findViewById(R.id.product_image);
        TextView productName = (TextView) row.findViewById(R.id.product_name_label);
        TextView savingStatus = (TextView) row.findViewById(R.id.saving_status);
        final CheckBox pauseSaving = (CheckBox) row.findViewById(R.id.pause_saving);

        Bitmap bitmap = ProductDatabaseWrapper.mBitmaps.get(product.getId());
        if (bitmap == null) {
            productImage.setImageResource(R.drawable.pennybank_icon);
        } else {
            productImage.setImageBitmap(bitmap);
        }
        productName.setText(product.getName());
        savingStatus.setText(product.getBalance() + " ¤ left.");
        pauseSaving.setChecked(product.isActive());
        pauseSaving.setText(product.isActive() ? "Active" : "Inactive");

        pauseSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newState = !product.isActive();
                product.setActive(newState);
                ProductDatabaseWrapper.updateProduct(product);
                if (newState) {
                    Toast.makeText(mContext, "Continued saving for " + product.getName() + ".", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Paused saving for " + product.getName() + ".", Toast.LENGTH_SHORT).show();
                }
                pauseSaving.setText(newState ? "Active" : "Inactive");
            }
        });

        return row;
    }

}

