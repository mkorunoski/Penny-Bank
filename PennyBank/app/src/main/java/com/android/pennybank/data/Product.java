package com.android.pennybank.data;

import android.content.Context;
import android.util.*;

import com.android.pennybank.util.Logger;
import com.android.pennybank.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class represents the product that the user wants to save money for
 */
public class Product {

    public enum DEPOSIT_FREQUENCY {
        DAILY(0),   /// The user will deposit daily
        WEEKLY(1),  /// The user will deposit weekly
        MONTHLY(2); /// The user will deposit monthly

        private int value;

        DEPOSIT_FREQUENCY(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static DEPOSIT_FREQUENCY fromValue(Integer value) {
            switch (value) {
                case 0:
                    return DAILY;
                case 1:
                    return WEEKLY;
                case 2:
                    return MONTHLY;
                default:
                    return DAILY;
            }
        }
    }

    public enum SAVING_METHOD {
        BY_DEPOSIT(0),  /// Saving based upon the specified deposit value
        BY_END_DATE(1); /// Saving based upon the date when the user wants to purchase the product

        private int value;

        SAVING_METHOD(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static SAVING_METHOD fromValue(int value) {
            switch (value) {
                case 0:
                    return BY_DEPOSIT;
                case 1:
                    return BY_END_DATE;
                default:
                    return BY_DEPOSIT;
            }
        }
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("hh:mm");

    private Context context;

    private int id;                             // The id will be used as the pending intent's request code
    private String name;
    private String image;                       // Stored as path to the desired image
    private int price;
    private DEPOSIT_FREQUENCY depositFrequency;
    private SAVING_METHOD savingMethod;
    private int deposit;
    private int savings;
    private Calendar startDate;
    private Calendar endDate;
    private Calendar reminderTime;

    /**
     * Constructor that initializes based upon data stored in database
     * Acts as copy constructor
     *
     * @param context          Application context
     * @param id               Product id
     * @param name             Product name
     * @param image            Product image
     * @param price            Product price
     * @param depositFrequency Deposit frequency
     * @param savingMethod     Saving method
     * @param deposit          Deposit value
     * @param savings          Current savings
     * @param startDate        Start date
     * @param endDate          End date
     * @param reminderTime     Reminder time
     */
    public Product(Context context, int id, String name, String image, int price,
                   DEPOSIT_FREQUENCY depositFrequency, SAVING_METHOD savingMethod, int deposit,
                   int savings, Calendar startDate, Calendar endDate, Calendar reminderTime) {
        this.context = context;
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.depositFrequency = depositFrequency;
        this.savingMethod = savingMethod;
        this.deposit = deposit;
        this.savings = savings;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reminderTime = reminderTime;
    }

    /**
     * Constructor that initializes based upon the deposit value
     *
     * @param context          Application context
     * @param name             Product name
     * @param image            Product image
     * @param price            Product price
     * @param depositFrequency Deposit frequency
     * @param deposit          Deposit value
     * @param reminderTime     Reminder time
     */
    public Product(Context context, String name, String image, int price, DEPOSIT_FREQUENCY depositFrequency, int deposit, Calendar reminderTime) {
        this.context = context;
//        this.id = (name + String.valueOf(price)).hashCode();
        this.id = Util.incrementProductInstances(context);
        this.name = name;
        this.image = image;
        this.price = price;
        this.depositFrequency = depositFrequency;
        this.savingMethod = SAVING_METHOD.BY_DEPOSIT;
        this.deposit = deposit;
        this.savings = 0;
        this.startDate = Calendar.getInstance();
        this.reminderTime = (Calendar) reminderTime.clone();

        calcEndDate();
    }

    /**
     * Constructor that initializes based upon the date when the user wants to purchase the product
     *
     * @param context          Application context
     * @param name             Product name
     * @param image            Product image
     * @param price            Product price
     * @param depositFrequency Deposit frequency
     * @param endDate          Saving end date
     * @param reminderTime     Reminder time
     */
    public Product(Context context, String name, String image, int price, DEPOSIT_FREQUENCY depositFrequency, Calendar endDate, Calendar reminderTime) {
        this.context = context;
//        this.id = (name + String.valueOf(price)).hashCode();
        this.id = Util.incrementProductInstances(context);
        this.name = name;
        this.image = image;
        this.price = price;
        this.depositFrequency = depositFrequency;
        this.savingMethod = SAVING_METHOD.BY_END_DATE;
        this.savings = 0;
        this.startDate = Calendar.getInstance();
        this.endDate = endDate;
        this.reminderTime = (Calendar) reminderTime.clone();

        calcDeposit();
    }

    //region Getter and setter for id
    public int getId() {
        return id;
    }

//  public void setM_id(int id) {
//      this.id = id;
//  }
    //endregion

    //region Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //endregion

    //region Getter and setter for image
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    //endregion

    //region Getter and setter for price
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
        modify();
    }
    //endregion

    //region Getter and setter for depositFrequency
    public DEPOSIT_FREQUENCY getDepositFrequency() {
        return depositFrequency;
    }

    public void setDepositFrequency(DEPOSIT_FREQUENCY depositFrequency) {
        this.depositFrequency = depositFrequency;
        modify();
        Util.startAlarm(context, this);
    }
    //endregion

    //region Getter and setter for savingMethod
    public SAVING_METHOD getSavingMethod() {
        return savingMethod;
    }

    public void setSavingMethod(SAVING_METHOD savingMethod) {
        this.savingMethod = savingMethod;
    }
    //endregion

    //region Getter and setter for deposit
    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
        calcEndDate();
    }
    //endregion

    //region Getter and setter for savings
    public int getSavings() {
        return savings;
    }

    public void setSavings(int savings) {
        this.savings = savings;
        modify();
    }
    //endregion

    //region Getter and setter for startDate
    public Calendar getStartDate() {
        return startDate;
    }

//  public void setM_startDate(Calendar startDate) {
//      this.startDate = startDate;
//  }
    //endregion

    //region Getter and setter for endDate
    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
        calcDeposit();
    }
    //endregion

    //region Getter and setter for reminderTime
    public Calendar getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Calendar reminderTime) {
        this.reminderTime = reminderTime;
        Util.startAlarm(context, this);
    }
    //endregion

    //region Additional methods
    public void deposit() {
        savings += deposit;
    }

    public int getBalance() {
        return price - savings;
    }
    //endregion

    //region Helper methods
    private void modify() {
        if (getSavingMethod() == SAVING_METHOD.BY_DEPOSIT) {
            calcEndDate();
        } else if (getSavingMethod() == SAVING_METHOD.BY_END_DATE) {
            calcDeposit();
        }
    }

    // "MS" suffix stands for milliseconds.
    private final long TO_DAYS_MS = 1000L * 60 * 60 * 24;
    private final long TO_WEEKS_MS = TO_DAYS_MS * 7;
    private final long TO_MONTHS_MS = TO_WEEKS_MS * 12;

    private void calcDeposit() {
        long timeDifferenceMS = endDate.getTimeInMillis() - startDate.getTimeInMillis();

        int balance = getBalance();

        switch (depositFrequency) {
            case DAILY: {
                if (timeDifferenceMS <= TO_DAYS_MS) {
                    deposit = balance;
                } else {
                    int days = (int) (timeDifferenceMS / TO_DAYS_MS);
                    deposit = balance / days;
                }
                break;
            }
            case WEEKLY: {
                if (timeDifferenceMS <= TO_WEEKS_MS) {
                    deposit = balance;
                } else {
                    int weeks = (int) (timeDifferenceMS / TO_WEEKS_MS);
                    deposit = balance / weeks;
                }
                break;
            }
            case MONTHLY: {
                if (timeDifferenceMS <= TO_MONTHS_MS) {
                    deposit = balance;
                } else {
                    int months = (int) (timeDifferenceMS / TO_MONTHS_MS);
                    deposit = balance / months;
                }
                break;
            }
        }

        if (Logger.ENABLED) {
            Log.i(Logger.TAG, "The deposit is: " + String.valueOf(deposit));
        }
    }

    private void calcEndDate() {
        this.endDate = (Calendar) startDate.clone();

        int balance = getBalance();

        int increment = balance / deposit;
        if (increment == 0) {
            increment = 1;
        }

        switch (depositFrequency) {
            case DAILY: {
                endDate.add(Calendar.DAY_OF_YEAR, increment);
                break;
            }
            case WEEKLY: {
                endDate.add(Calendar.WEEK_OF_YEAR, increment);
                break;
            }
            case MONTHLY: {
                endDate.add(Calendar.MONTH, increment);
                break;
            }
        }

        if (Logger.ENABLED) {
            Log.i(Logger.TAG, "The end date is: " + endDate.getTime().toString());
        }
    }
    //endregion
}
