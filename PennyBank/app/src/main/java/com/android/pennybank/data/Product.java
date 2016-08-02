package com.android.pennybank.data;

import android.util.*;

import com.android.pennybank.util.Logger;

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

    private int id;
    private String name;
    private String image; // Stored as location to the desired image
    private float price;
    private DEPOSIT_FREQUENCY depositFrequency;
    private SAVING_METHOD savingMethod;
    private float deposit;
    private float savings;
    private Calendar startDate;
    private Calendar endDate;

//  Otkako ke se implementira servisot.
//  private Alarm m_alram;

    /**
     * Constructor that initializes based upon data stored in database
     *
     * @param id               Product id
     * @param name             Product name
     * @param image            Product image
     * @param price            Product price
     * @param depositFrequency Deposit depositFrequency
     * @param savingMethod     Saving savingMethod
     * @param deposit          Deposit value
     * @param savings          Current savings
     * @param startDate        Start date
     * @param endDate          End date
     */
    public Product(int id, String name, String image, float price,
                   DEPOSIT_FREQUENCY depositFrequency, SAVING_METHOD savingMethod, float deposit,
                   float savings, Calendar startDate, Calendar endDate) {
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
    }

    /**
     * Constructor that initializes based upon the deposit value
     *
     * @param name             Product name
     * @param image            Product image
     * @param price            Product price
     * @param depositFrequency Deposit depositFrequency
     * @param deposit          Deposit value
     */
    public Product(String name, String image, float price, DEPOSIT_FREQUENCY depositFrequency, float deposit) {
        this.id = (name + String.valueOf(price)).hashCode();
        this.name = name;
        this.image = image;
        this.price = price;
        this.depositFrequency = depositFrequency;
        this.savingMethod = SAVING_METHOD.BY_DEPOSIT;
        this.deposit = deposit;
        this.savings = 0.0f;
        this.startDate = Calendar.getInstance();

        calcEndDate();
    }

    /**
     * Constructor that initializes based upon the date when the user wants to purchase the product
     *
     * @param name             Product name
     * @param image            Product image
     * @param price            Product price
     * @param depositFrequency Deposit depositFrequency
     * @param endDate          Saving end date
     */
    public Product(String name, String image, float price, DEPOSIT_FREQUENCY depositFrequency, Calendar endDate) {
        this.id = (name + String.valueOf(price)).hashCode();
        this.name = name;
        this.image = image;
        this.price = price;
        this.depositFrequency = depositFrequency;
        this.savingMethod = SAVING_METHOD.BY_END_DATE;
        this.savings = 0.0f;
        this.startDate = Calendar.getInstance();
        this.endDate = endDate;

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
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
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
    public float getDeposit() {
        return deposit;
    }

    public void setDeposit(float deposit) {
        this.deposit = deposit;
        modify();
    }
    //endregion

    //region Getter and setter for savings
    public float getSavings() {
        return savings;
    }

    public void setSavings(float savings) {
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
        modify();
    }
    //endregion

    //region Additional methods
    public void deposit() {
        savings += deposit;

//      Tuka ke se postavuva alarmot za datumot na sledeniot depozit.
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

    private void calcDeposit() {
        long timeDifference = endDate.getTimeInMillis() - startDate.getTimeInMillis();
        float balance = price - savings;

        switch (depositFrequency) {
            case DAILY: {
                if (timeDifference == 0) {
                    deposit = 0.0f;
                } else {
                    int days = (int) (timeDifference / (1000 * 60 * 60 * 24));
                    deposit = balance / days;
                }
                break;
            }
            case WEEKLY: {
                if (timeDifference == 0) {
                    deposit = 0.0f;
                } else {
                    int weeks = (int) (timeDifference / (1000 * 60 * 60 * 24 * 7));
                    deposit = balance / weeks;
                }
                break;
            }
            case MONTHLY: {
                if (timeDifference == 0) {
                    deposit = 0.0f;
                } else {
                    long div = 1000L * 60 * 60 * 24 * 7 * 12;
                    int months = (int) (timeDifference / div);
                    deposit = balance / months;
                }
                break;
            }
        }

        if (Logger.s_enabled) {
            Log.i("*Product: Product()", "The deposit is: " + String.valueOf(deposit));
        }
    }

    private void calcEndDate() {
        this.endDate = (Calendar) startDate.clone();
        float balance = price - savings;
        int increment = (int) (balance / deposit);

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

        if (Logger.s_enabled) {
            Log.i("*Product: Product()", "The end date is: " + endDate.getTime().toString());
        }
    }
    //endregion
}
