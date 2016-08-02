package com.android.pennybank.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;

import java.util.Calendar;


public class SavingInfoDialogFragment extends DialogFragment {
    private Activity mActivity;

    private Product mProduct;

    private EditText mProductPrice;
    private Spinner mDepositFrequency;
    private EditText mSavings;
    private EditText mStartDate;
    private Spinner mSavingMethod;
    private EditText mEndDate;
    private boolean mEndDateEnabled = false;
    private EditText mDeposit;
    private boolean mDepositEnabled = false;

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
        Context context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.saving_info_dialog_fragment_layout, null);

        ImageView productImage = (ImageView) view.findViewById(R.id.product_image);
        TextView productName = (TextView) view.findViewById(R.id.product_name_label);
        mProductPrice = (EditText) view.findViewById(R.id.product_price);
        mDepositFrequency = (Spinner) view.findViewById(R.id.deposit_frequency);
        mSavings = (EditText) view.findViewById(R.id.savings);
        mStartDate = (EditText) view.findViewById(R.id.start_date);
        mStartDate.setEnabled(false);
        mSavingMethod = (Spinner) view.findViewById(R.id.saving_method);
        mEndDate = (EditText) view.findViewById(R.id.end_date);
        mDeposit = (EditText) view.findViewById(R.id.deposit);

        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(context, R.array.deposit_frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDepositFrequency.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(context, R.array.saving_method, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSavingMethod.setAdapter(adapter);

        productImage.setBackgroundResource(context.getResources().getIdentifier(mProduct.getImage(), "drawable", context.getPackageName()));
        productName.setText(mProduct.getName());

        updateEditTexts();

        setEnableEditableFields(true);

        mEdit = (Switch) view.findViewById(R.id.edit);

        setListeners();

        return view;
    }

    private void setEnableEditableFields(boolean status) {
        mProductPrice.setEnabled(status);
        mDepositFrequency.setEnabled(status);
        mSavings.setEnabled(status);
        mSavingMethod.setEnabled(status);
        if (status) {
            mEndDate.setEnabled(mEndDateEnabled);
            mDeposit.setEnabled(mDepositEnabled);
        } else {
            mEndDate.setEnabled(false);
            mDeposit.setEnabled(false);
        }
    }

    private void updateEditTexts() {
        mProductPrice.setText(String.valueOf(mProduct.getPrice()));
        mDepositFrequency.setSelection(mProduct.getDepositFrequency().getValue());
        mSavings.setText(String.valueOf(mProduct.getSavings()));
        mStartDate.setText(Product.DATE_FORMAT.format(mProduct.getStartDate().getTime()));
        mSavingMethod.setSelection(mProduct.getSavingMethod().getValue());
        mEndDate.setText(Product.DATE_FORMAT.format(mProduct.getEndDate().getTime()));
        mDeposit.setText(String.valueOf(mProduct.getDeposit()));
    }

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener datePickerDialog;

    private void setListeners() {
        calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                mEndDate.setText(Product.DATE_FORMAT.format(calendar.getTime()));
                mProduct.setEndDate(calendar);
                ProductDatabaseWrapper.updateProduct(mProduct);
                updateEditTexts();
            }
        };

        mProductPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                float oldPrice = mProduct.getPrice();
                float newPrice = Float.parseFloat(mProductPrice.getText().toString());
                if (oldPrice != newPrice) {
                    mProduct.setPrice(newPrice);
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                    mActivity.sendBroadcast(new Intent().setAction("com.android.pennybank.notifyDataSetChanged"));
                }
            }
        });

        mDepositFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProduct.setDepositFrequency(Product.DEPOSIT_FREQUENCY.fromValue(position));
                ProductDatabaseWrapper.updateProduct(mProduct);
                updateEditTexts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSavings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                float oldSavings = mProduct.getSavings();
                float newSavings = Float.parseFloat(mSavings.getText().toString());
                if (oldSavings != newSavings) {
                    mProduct.setSavings(newSavings);
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                    mActivity.sendBroadcast(new Intent().setAction("com.android.pennybank.notifyDataSetChanged"));
                }
            }
        });

        mSavingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProduct.setSavingMethod(Product.SAVING_METHOD.fromValue(position));
                ProductDatabaseWrapper.updateProduct(mProduct);
                updateEditTexts();
                changeVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mEndDate.setInputType(InputType.TYPE_NULL);
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        mActivity,
                        datePickerDialog,
                        mProduct.getEndDate().get(Calendar.YEAR),
                        mProduct.getEndDate().get(Calendar.MONTH),
                        mProduct.getEndDate().get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        mDeposit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                float oldDeposit = mProduct.getDeposit();
                float newDeposit = Float.parseFloat(mDeposit.getText().toString());
                if (oldDeposit != newDeposit) {
                    mProduct.setDeposit(newDeposit);
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                }
            }
        });

        mEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEnableEditableFields(isChecked);
            }
        });
    }

    private void changeVisibility() {
        Product.SAVING_METHOD savingMethod = mProduct.getSavingMethod();
        if (savingMethod == Product.SAVING_METHOD.BY_DEPOSIT) {
            mEndDateEnabled = false;
            mDepositEnabled = true;
        } else if (savingMethod == Product.SAVING_METHOD.BY_END_DATE) {
            mEndDateEnabled = true;
            mDepositEnabled = false;
        }
        mEndDate.setEnabled(mEndDateEnabled);
        mDeposit.setEnabled(mDepositEnabled);
    }

}
