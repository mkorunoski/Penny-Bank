package com.android.pennybank.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Product> products;

    public CustomAdapter(Context context) {
        mContext = context;
        products = ProductDatabaseWrapper.getProductsFromDatabase();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_adapter_row_layout, parent, false);

        Product product = products.get(position);

        ImageView ivProductImage = (ImageView)row.findViewById(R.id.product_image);
        TextView tvProductName = (TextView)row.findViewById(R.id.product_name_label);
        TextView tvSavingStatus = (TextView)row.findViewById(R.id.saving_status);

        tvProductName.setText(product.getName());
        tvSavingStatus.setText((product.getPrice() - product.getSavings()) + " ¤ left.");
        ivProductImage.setBackgroundResource(mContext.getResources().getIdentifier(product.getImage(), "drawable", mContext.getPackageName()));

        return row;
    }

}
