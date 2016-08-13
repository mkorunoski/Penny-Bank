package com.android.pennybank.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    public static class Receiver extends BroadcastReceiver {
        public static CustomAdapter mCustomAdapterRef;

        public Receiver() {}

        public Receiver(CustomAdapter customAdapter) {
            mCustomAdapterRef = customAdapter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mCustomAdapterRef.notifyDataSetChanged();
        }
    }

    private Receiver mReceiver;
    private FragmentListener mFragmentListener;

    private ListView mSavings;
    private CustomAdapter mCustomAdapter;
    private Button mBack;

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
        mSavings = (ListView) view.findViewById(R.id.savings);
        mCustomAdapter = new CustomAdapter(getActivity().getApplicationContext());
        mReceiver = new Receiver(mCustomAdapter);
        mSavings.setAdapter(mCustomAdapter);

        mBack = (Button) view.findViewById(R.id.back);

        setListeners();
    }

    private void setListeners() {
        mSavings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                SavingInfoDialogFragment savingInfoDialogFragment = new SavingInfoDialogFragment();
                savingInfoDialogFragment.setProduct(ProductDatabaseWrapper.getProduct((int) mCustomAdapter.getItemId(position)));
                savingInfoDialogFragment.show(fragmentManager, "Saving Info");
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.switchFragments(MainActivity.FRAGMENTS.START_SCREEN_FRAGMENT);
            }
        });
    }
}
