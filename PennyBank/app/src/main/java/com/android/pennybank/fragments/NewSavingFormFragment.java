package com.android.pennybank.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.pennybank.R;

public class NewSavingFormFragment extends Fragment {

    private Spinner mDepositFrequency;
    private Spinner mSavingMethod;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_saving_form_fragment_layout, container, false);
        setup(view);
        return view;
    }

    private void setup(View view) {
        mDepositFrequency = (Spinner) view.findViewById(R.id.deposit_frequency);
        mSavingMethod = (Spinner) view.findViewById(R.id.saving_method);

        ArrayAdapter<CharSequence> adapter;
        Context context = getActivity().getApplicationContext();
        adapter = ArrayAdapter.createFromResource(context, R.array.deposit_frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDepositFrequency.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(context, R.array.saving_method, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSavingMethod.setAdapter(adapter);
    }
}
