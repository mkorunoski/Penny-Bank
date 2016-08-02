package com.android.pennybank.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.pennybank.R;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.fragments.FragmentListener;
import com.android.pennybank.fragments.NewSavingFormFragment;
import com.android.pennybank.fragments.StartScreenFragment;
import com.android.pennybank.fragments.ViewSavingsFragment;

public class MainActivity extends AppCompatActivity implements FragmentListener {

    public static final String PREF_NAME = "Preferences";

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