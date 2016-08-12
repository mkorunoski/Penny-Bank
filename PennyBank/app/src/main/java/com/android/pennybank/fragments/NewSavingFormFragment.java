package com.android.pennybank.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.pennybank.R;
import com.android.pennybank.activities.MainActivity;
import com.android.pennybank.data.Product;
import com.android.pennybank.data.Product.DEPOSIT_FREQUENCY;
import com.android.pennybank.data.Product.SAVING_METHOD;
import com.android.pennybank.data.ProductDatabaseWrapper;
import com.android.pennybank.util.Logger;
import com.android.pennybank.util.RoundImagesLoader;
import com.android.pennybank.util.Util;

import java.text.ParseException;
import java.util.Calendar;

public class NewSavingFormFragment extends Fragment {
    private static final int CHOOSE_PICTURE = 100;
    private FragmentListener mFragmentListener;

    private Product mProduct;

    private EditText mProductName;
    private String mProductImage;
    private Button mChoosePicture;
    private EditText mProductPrice;
    private Spinner mDepositFrequency;
    private Spinner mSavingMethod;
    private EditText mEndDate;
    private TextView mEndDateLabel;
    private EditText mDeposit;
    private TextView mDepositLabel;
    private Button mSave;

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
        View view = inflater.inflate(R.layout.new_saving_form_fragment_layout, container, false);
        setup(view);
        return view;
    }

    private void setup(View view) {
        Context context = getActivity().getApplicationContext();

        mProductName = (EditText) view.findViewById(R.id.product_name);
        mProductImage = "";
        mChoosePicture = (Button) view.findViewById(R.id.choose_picture);
        mProductPrice = (EditText) view.findViewById(R.id.product_price);
        mDepositFrequency = (Spinner) view.findViewById(R.id.deposit_frequency);
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(context, R.array.deposit_frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDepositFrequency.setAdapter(adapter);
        mSavingMethod = (Spinner) view.findViewById(R.id.saving_method);
        adapter = ArrayAdapter.createFromResource(context, R.array.saving_method, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSavingMethod.setAdapter(adapter);
        mEndDate = (EditText) view.findViewById(R.id.end_date);
        mEndDateLabel = (TextView) view.findViewById(R.id.end_date_label);
        mDeposit = (EditText) view.findViewById(R.id.deposit);
        mDepositLabel = (TextView) view.findViewById(R.id.deposit_label);
        mSave = (Button) view.findViewById(R.id.save);

        mEndDate.setEnabled(false);
        changeVisibility(Product.SAVING_METHOD.BY_DEPOSIT);

        setListeners();
    }

    private void setListeners() {
        mChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        mSavingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeVisibility(Product.SAVING_METHOD.fromValue(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mEndDate.setText(Product.DATE_FORMAT.format(calendar.getTime()));
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

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = mProductName.getText().toString();
                String productPrice = mProductPrice.getText().toString();
                DEPOSIT_FREQUENCY depositFrequency = DEPOSIT_FREQUENCY.fromValue(mDepositFrequency.getSelectedItemPosition());
                SAVING_METHOD savingMethod = SAVING_METHOD.fromValue(mSavingMethod.getSelectedItemPosition());
                String deposit = mDeposit.getText().toString();
                Calendar endDate = Calendar.getInstance();
                if (savingMethod == SAVING_METHOD.BY_END_DATE && !mEndDate.getText().toString().equals("")) {
                    try {
                        endDate.setTime(Product.DATE_FORMAT.parse(mEndDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

//                Do the checkup
                if (productName.equals("")) {
                    mProductName.requestFocus();
                    alertEmptyFields();
                    return;
                }
                if (mProductImage.equals("")) {
                    alertProductImageNotChosen();
                    return;
                }
                if (productPrice.equals("")) {
                    mProductPrice.requestFocus();
                    alertEmptyFields();
                    return;
                }
                if (savingMethod == SAVING_METHOD.BY_END_DATE && endDate == Calendar.getInstance()) {
                    mEndDate.requestFocus();
                    alertEmptyFields();
                    return;
                }
                if (savingMethod == SAVING_METHOD.BY_DEPOSIT && deposit.equals("")) {
                    mDeposit.requestFocus();
                    alertEmptyFields();
                    return;
                }

//                If everything went OK init product object...
                if (savingMethod == SAVING_METHOD.BY_DEPOSIT) {
                    mProduct = new Product(getActivity(), productName, mProductImage, Integer.parseInt(productPrice),
                            depositFrequency, Integer.parseInt(deposit), Calendar.getInstance());
                }
                if (savingMethod == SAVING_METHOD.BY_END_DATE) {
                    mProduct = new Product(getActivity(), productName, mProductImage, Integer.parseInt(productPrice),
                            depositFrequency, endDate, Calendar.getInstance());
                }

                setReminderAndAddToDatabase();
            }
        });
    }

    private Calendar calendar = Calendar.getInstance();

    private void setReminderAndAddToDatabase() {
        TimePickerDialog timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);
                mProduct.setReminderTime(calendar);

                ProductDatabaseWrapper.addProduct(mProduct);
                new RoundImagesLoader(getActivity(), mProduct).execute();

                notifySuccess();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePicker.setTitle("Set Reminder");
        timePicker.show();
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    mProductImage = "file://" + Util.getPathFromURI(getActivity().getApplicationContext(), selectedImageUri);
                    if (Logger.ENABLED) {
                        Log.i(Logger.TAG, "Image Path : " + mProductImage);
                    }
                }
            }
        }
    }

    private void alertEmptyFields() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Please fill required fields!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void alertProductImageNotChosen() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Product picture not chosen.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Open gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                openImageChooser();
            }
        });
        alertDialog.show();
    }

    private void notifySuccess() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Wonderful");
        alertDialog.setMessage("You've successfully created new saving!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Great", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                mFragmentListener.switchFragments(MainActivity.FRAGMENTS.START_SCREEN_FRAGMENT);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View savings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                mFragmentListener.switchFragments(MainActivity.FRAGMENTS.VIEW_SAVINGS_FRAGMENT);
            }
        });
        alertDialog.show();
    }

    private void changeVisibility(SAVING_METHOD savingMethod) {
        if (savingMethod == SAVING_METHOD.BY_DEPOSIT) {
            mEndDate.setEnabled(false);
            mEndDateLabel.setEnabled(false);
            mDeposit.setEnabled(true);
            mDepositLabel.setEnabled(true);
        } else if (savingMethod == SAVING_METHOD.BY_END_DATE) {
            mEndDate.setEnabled(true);
            mEndDateLabel.setEnabled(true);
            mDeposit.setEnabled(false);
            mDepositLabel.setEnabled(false);
        }
    }
}
