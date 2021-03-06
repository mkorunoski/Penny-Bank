package com.android.pennybank.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
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
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;

public class ViewSavingsFragment extends Fragment {

    private NotifyDataSetChangedReceiver mReceiver;
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
        View view = inflater.inflate(R.layout.fragment_view_savings, container, false);
        setup(view);
        return view;
    }

    private void setup(View view) {
        mSavings = (ListView) view.findViewById(R.id.savings);
        mCustomAdapter = new CustomAdapter(getActivity().getApplicationContext());
        mSavings.setAdapter(mCustomAdapter);
        mReceiver = new NotifyDataSetChangedReceiver(mCustomAdapter);

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

        mSavings.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                alertProductRemove(position);
                return true;
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.switchFragments(MainActivity.FRAGMENTS.START_SCREEN_FRAGMENT);
            }
        });
    }

    private void alertProductRemove(final int productId) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to remove this saving?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Positive", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                Product product = ProductDatabaseWrapper.getProduct((int) mCustomAdapter.getItemId(productId));
                ProductDatabaseWrapper.deleteProduct(product);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Get me back!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
