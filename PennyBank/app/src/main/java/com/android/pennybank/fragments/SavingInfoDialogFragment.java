package com.android.pennybank.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.pennybank.R;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.data.RoundImagesLoader;
import com.android.pennybank.util.RoundImage;

import java.util.Calendar;

public class SavingInfoDialogFragment extends DialogFragment {
    private Product mProduct;

    private ImageView mProductImage;
    private TextView mProductName;
    private EditText mProductPrice;
    private Spinner mDepositFrequency;
    private EditText mSavings;
    private EditText mStartDate;
    private Spinner mSavingMethod;
    private EditText mEndDate;
    private boolean mEndDateEnabled = false;
    private EditText mDeposit;
    private boolean mDepositEnabled = false;
    private EditText mReminderTime;

    private Switch mEdit;

    public void setProduct(Product product) {
        mProduct = product;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Context context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.saving_info_dialog_fragment_layout, null);

        mProductImage = (ImageView) view.findViewById(R.id.product_image);
        mProductName = (TextView) view.findViewById(R.id.product_name_label);
        mProductPrice = (EditText) view.findViewById(R.id.product_price);
        mDepositFrequency = (Spinner) view.findViewById(R.id.deposit_frequency);
        mSavings = (EditText) view.findViewById(R.id.savings);
        mStartDate = (EditText) view.findViewById(R.id.start_date);
        mStartDate.setEnabled(false);
        mSavingMethod = (Spinner) view.findViewById(R.id.saving_method);
        mEndDate = (EditText) view.findViewById(R.id.end_date);
        mDeposit = (EditText) view.findViewById(R.id.deposit);
        mReminderTime = (EditText) view.findViewById(R.id.reminder_time);
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(context, R.array.deposit_frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDepositFrequency.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(context, R.array.saving_method, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSavingMethod.setAdapter(adapter);
//        RoundImage roundImage = RoundImagesLoader.mRoundImages.get(mProduct.getId());
        Bitmap roundImage = RoundImagesLoader.mRoundImages.get(mProduct.getId());
        if (roundImage == null) {
            mProductImage.setImageResource(R.drawable.pennybank_icon);
        } else {
//            TODO: This line throws Fatal signal 11 (SIGSEGV), code 1, fault addr ... in tid ... (RenderThread)
//            mProductImage.setImageResource(R.drawable.pennybank_icon);
            mProductImage.setImageBitmap(roundImage);
        }
        mProductName.setText(mProduct.getName());
        mEdit = (Switch) view.findViewById(R.id.edit);

        updateEditTexts();
        setListeners();

        setEnabledBySavingMethod();
        setEnabledEditableFields(true);

        return view;
    }

    private void setEnabledEditableFields(boolean status) {
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
        mReminderTime.setEnabled(status);
    }

    private void updateEditTexts() {
        mProductPrice.setText(String.valueOf(mProduct.getPrice()));
        mDepositFrequency.setSelection(mProduct.getDepositFrequency().getValue());
        mSavings.setText(String.valueOf(mProduct.getSavings()));
        mStartDate.setText(Product.DATE_FORMAT.format(mProduct.getStartDate().getTime()));
        mSavingMethod.setSelection(mProduct.getSavingMethod().getValue());
        mEndDate.setText(Product.DATE_FORMAT.format(mProduct.getEndDate().getTime()));
        mDeposit.setText(String.valueOf(mProduct.getDeposit()));
        mReminderTime.setText(Product.HOUR_FORMAT.format(mProduct.getReminderTime().getTime()));
    }

    private Calendar calendar = Calendar.getInstance();

    private void setListeners() {
        mProductPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int oldPrice = mProduct.getPrice();
                int newPrice = Integer.parseInt(mProductPrice.getText().toString());
                if (oldPrice != newPrice) {
                    mProduct.setPrice(newPrice);
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                    getActivity().sendBroadcast(new Intent().setAction("com.android.pennybank.notifyDataSetChanged"));
                }
            }
        });

        mDepositFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Product.DEPOSIT_FREQUENCY.fromValue(position) != mProduct.getDepositFrequency()) {
                    mProduct.setDepositFrequency(Product.DEPOSIT_FREQUENCY.fromValue(position));
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSavings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int oldSavings = mProduct.getSavings();
                int newSavings = Integer.parseInt(mSavings.getText().toString());
                if (oldSavings != newSavings) {
                    mProduct.setSavings(newSavings);
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                    getActivity().sendBroadcast(new Intent().setAction("com.android.pennybank.notifyDataSetChanged"));
                }
            }
        });

        mSavingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Product.SAVING_METHOD.fromValue(position) != mProduct.getSavingMethod()) {
                    mProduct.setSavingMethod(Product.SAVING_METHOD.fromValue(position));
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                    setEnabledBySavingMethod();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (calendar.getTimeInMillis() != mProduct.getEndDate().getTimeInMillis()) {
                    mProduct.setEndDate(calendar);
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                }
            }
        };

        mEndDate.setInputType(InputType.TYPE_NULL);
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        getActivity(),
                        datePickerDialog,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        mDeposit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int oldDeposit = mProduct.getDeposit();
                int newDeposit = Integer.parseInt(mDeposit.getText().toString());
                if (oldDeposit != newDeposit) {
                    mProduct.setDeposit(newDeposit);
                    ProductDatabaseWrapper.updateProduct(mProduct);
                    updateEditTexts();
                }
            }
        });

        mReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        if (calendar.getTimeInMillis() != mProduct.getReminderTime().getTimeInMillis()) {
                            mProduct.setReminderTime(calendar);
                            ProductDatabaseWrapper.updateProduct(mProduct);
                            updateEditTexts();
                        }
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePicker.setTitle("Set Reminder");
                timePicker.show();
            }
        });

        mEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEnabledEditableFields(isChecked);
            }
        });
    }

    private void setEnabledBySavingMethod() {
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

