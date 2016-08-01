package com.android.pennybank.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;

public class ProductInfoDialogFragment extends DialogFragment {

    private Product mProduct;

    public void setProduct(Product product) {
        mProduct = product;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_info_dialog_fragment_layout, null);

        ImageView ivProductImage = (ImageView)view.findViewById(R.id.product_info_dialog_iv_product_image);

        ivProductImage.setBackgroundResource(getContext().getResources().getIdentifier(mProduct.getImage(), "drawable", getContext().getPackageName()));

        return view;
    }
}
