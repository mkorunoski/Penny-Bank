package com.android.pennybank.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.fragments.FragmentListener;
import com.android.pennybank.fragments.NewSavingFormFragment;
import com.android.pennybank.fragments.StartScreenFragment;
import com.android.pennybank.fragments.ViewSavingsFragment;
import com.android.pennybank.util.Constants;
import com.android.pennybank.util.Logger;

public class MainActivity extends AppCompatActivity implements FragmentListener {

    public enum FRAGMENTS {
        NULL,
        START_SCREEN_FRAGMENT,
        NEW_SAVING_FORM_FRAGMENT,
        VIEW_SAVINGS_FRAGMENT
    }

    protected FRAGMENTS mLastFragment;
    protected FRAGMENTS mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProductDatabaseWrapper.initDatabase(this);

        switchFragments(FRAGMENTS.START_SCREEN_FRAGMENT);
        mLastFragment = FRAGMENTS.NULL;
        mCurrentFragment = FRAGMENTS.START_SCREEN_FRAGMENT;

        Intent intent = this.getIntent();
        String success = intent.getStringExtra(Constants.SUCCESS);
        if (Logger.ENABLED) {
            Log.i(Logger.TAG, "Success value: " + success);
        }
        if (success != null && success.equals(Constants.TRUE)) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_saving_success, null);
            ImageView anim = (ImageView) view.findViewById(R.id.anim);
            anim.setBackgroundResource(R.drawable.animation);
            AnimationDrawable animation = (AnimationDrawable) anim.getBackground();
            animation.start();

            final Product product = ProductDatabaseWrapper.getProduct(intent.getExtras().getInt(Constants.KEY_PRODUCT_ID));
            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Congratulations");
            alertDialog.setMessage("You have enough mooney to purchase " + product.getName() + "!");
            alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Hooray", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    ProductDatabaseWrapper.deleteProduct(product);
                }
            });
            alertDialog.setView(view);
            alertDialog.show();
        }
    }

    @Override
    public void switchFragments(FRAGMENTS fragment) {
        switch (fragment) {
            case START_SCREEN_FRAGMENT: {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new StartScreenFragment()).commit();
                mLastFragment = mCurrentFragment;
                mCurrentFragment = FRAGMENTS.START_SCREEN_FRAGMENT;
                break;
            }
            case NEW_SAVING_FORM_FRAGMENT: {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewSavingFormFragment()).commit();
                mLastFragment = mCurrentFragment;
                mCurrentFragment = FRAGMENTS.NEW_SAVING_FORM_FRAGMENT;
                break;
            }
            case VIEW_SAVINGS_FRAGMENT: {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewSavingsFragment()).commit();
                mLastFragment = mCurrentFragment;
                mCurrentFragment = FRAGMENTS.VIEW_SAVINGS_FRAGMENT;
                break;
            }
        }
    }
}