package com.android.pennybank.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.pennybank.activities.MainActivity;
import com.android.pennybank.R;
import com.android.pennybank.data.ProductDatabaseWrapper;

public class ViewSavingsFragment extends Fragment {

    private FragmentListener mFragmentListener;

    private ListView mLvSavings;
    private CustomAdapter mCustomAdapter;
    private Button mBtnBack;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_savings_fragment_layout, container, false);
        setup(view);
        return view;
    }

    private void setup(View view) {
        mLvSavings = (ListView)view.findViewById(R.id.lv_savings);
        mCustomAdapter = new CustomAdapter(getActivity().getApplicationContext());
        mLvSavings.setAdapter(mCustomAdapter);

        mBtnBack = (Button)view.findViewById(R.id.btn_back);

        setListeners();
    }

    private void setListeners() {
        mLvSavings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                SavingInfoDialogFragment savingInfoDialogFragment = new SavingInfoDialogFragment();
                savingInfoDialogFragment.setProduct(ProductDatabaseWrapper.getProductFromDatabase((int)mCustomAdapter.getItemId(position)));
                savingInfoDialogFragment.show(fragmentManager, "Product Info");
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.switchFragments(MainActivity.FRAGMENTS.START_SCREEN_FRAGMENT);
            }
        });
    }
}
