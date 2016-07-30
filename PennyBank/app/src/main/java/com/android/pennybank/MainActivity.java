package com.android.pennybank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements FragmentListener {

    // Tommy was here
    public enum FRAGMENTS
    {
        NULL,
        START_SCREEN_FRAGMENT,
        NEW_SAVING_FORM_FRAGMENT
    }

    protected FRAGMENTS mLastFragment;
    protected FRAGMENTS mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchFragments(FRAGMENTS.START_SCREEN_FRAGMENT);
        mLastFragment = FRAGMENTS.NULL;
        mCurrentFragment = FRAGMENTS.START_SCREEN_FRAGMENT;
    }

    @Override
    public void switchFragments(FRAGMENTS fragment)
    {
        switch (fragment)
        {
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
        }
    }
}