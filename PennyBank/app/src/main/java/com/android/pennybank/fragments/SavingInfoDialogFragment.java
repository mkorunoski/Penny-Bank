package com.android.pennybank.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SavingInfoDialogFragment extends DialogFragment {
    private Activity mActivity;

    private Product mProduct;

    private EditText mProductPrice;
    private EditText mSavings;
    private EditText mStartDate;
    private EditText mEndDate;
    private EditText mDeposit;

    private Switch mEdit;

    public void setProduct(Product product) {
        mProduct = product;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saving_info_dialog_fragment_layout, null);

        ImageView productImage = (ImageView) view.findViewById(R.id.product_image);
        TextView productName = (TextView) view.findViewById(R.id.product_name_label);
        mProductPrice = (EditText) view.findViewById(R.id.product_price);
        mSavings = (EditText) view.findViewById(R.id.savings);
        mStartDate = (EditText) view.findViewById(R.id.start_date);
        mEndDate = (EditText) view.findViewById(R.id.end_date);
        mDeposit = (EditText) view.findViewById(R.id.deposit);

        Context context = getActivity().getApplicationContext();

        productImage.setBackgroundResource(context.getResources().getIdentifier(mProduct.getImage(), "drawable", context.getPackageName()));
        productName.setText(mProduct.getName());
        mProductPrice.setText(String.valueOf(mProduct.getPrice()));
        mSavings.setText(String.valueOf(mProduct.getSavings()));
        mStartDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(mProduct.getStartDate().getTime()));
        mEndDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(mProduct.getEndDate().getTime()));
        mDeposit.setText(String.valueOf(mProduct.getDeposit()));

        setEnableEditableFields(false);

        mEdit = (Switch) view.findViewById(R.id.edit);

        setListeners();

        return view;
    }

    private void setEnableEditableFields(boolean status) {
        mProductPrice.setEnabled(status);
        mSavings.setEnabled(status);
        mStartDate.setEnabled(status);
        mEndDate.setEnabled(status);
        mDeposit.setEnabled(status);
    }

    private Calendar calendar;
    private EditText editTextRef;
    private DatePickerDialog.OnDateSetListener datePickerDialog;

    private void setListeners() {
        calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editTextRef.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
            }
        };

        mStartDate.setInputType(InputType.TYPE_NULL);
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextRef = mStartDate;
                new DatePickerDialog(
                        mActivity,
                        datePickerDialog,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        mEndDate.setInputType(InputType.TYPE_NULL);
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextRef = mEndDate;
                new DatePickerDialog(
                        mActivity,
                        datePickerDialog,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        mEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEnableEditableFields(isChecked);
            }
        });
    }
}
