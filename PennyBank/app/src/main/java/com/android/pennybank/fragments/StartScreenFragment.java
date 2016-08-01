package com.android.pennybank.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.pennybank.activities.GameActivity;
import com.android.pennybank.activities.MainActivity;
import com.android.pennybank.R;

public class StartScreenFragment extends Fragment {

    private FragmentListener mFragmentListener;

    Button mNewSaving;
    Button mViewSavings;
    Button mBored;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentListener = (FragmentListener) context;
    }

    // For older API level
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentListener = (FragmentListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_screen_fragment_layout, container, false);
        setup(view);
        return view;
    }

    private void setup(View view) {
        mNewSaving = (Button) view.findViewById(R.id.new_saving);
        mViewSavings = (Button) view.findViewById(R.id.view_savings);
        mBored = (Button) view.findViewById(R.id.bored);

        mNewSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.switchFragments(MainActivity.FRAGMENTS.NEW_SAVING_FORM_FRAGMENT);
            }
        });
        mViewSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.switchFragments(MainActivity.FRAGMENTS.VIEW_SAVINGS_FRAGMENT);
            }
        });
        mBored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), GameActivity.class);
                startActivity(intent);
            }
        });
    }
}
