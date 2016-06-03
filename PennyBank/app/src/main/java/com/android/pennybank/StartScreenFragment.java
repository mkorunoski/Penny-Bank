package com.android.pennybank;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StartScreenFragment extends Fragment {

    private FragmentListener mFragmentListener;

    Button mBtnNewSaving;
    Button mBtnViewSavings;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentListener = (FragmentListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_screen_fragment_layout, container, false);
        setup(view);
        return view;
    }

    private void setup(View view) {
        mBtnNewSaving = (Button) view.findViewById(R.id.btn_new_saving);
        mBtnViewSavings = (Button) view.findViewById(R.id.btn_view_savings);

        mBtnNewSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.switchFragments(MainActivity.FRAGMENTS.NEW_SAVING_FORM_FRAGMENT);
            }
        });
        mBtnViewSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
