package com.android.pennybank;

import android.media.Image;
import android.util.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class represents the product that the user wants to save money for
 */
public class Product {

    public enum FREQUENCY {
        DAILY(1),   /// The user will deposit daily
        WEEKLY(2),  /// The user will deposit weekly
        MONTHLY(3); /// The user will deposit monthly

        private int value;

        FREQUENCY(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static FREQUENCY fromValue(Integer value) {
            switch (value) {
                case 1:
                    return DAILY;
                case 2:
                    return WEEKLY;
                case 3:
                    return MONTHLY;
                default:
                    return DAILY;
            }
        }
    }

    public enum METHOD {
        BY_DEPOSIT(1),  /// Saving based upon the specified deposit value
        BY_END_DATE(2); /// Saving based upon the date when the user wants to purchase the product

        private int value;

        METHOD(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static METHOD fromValue(int value) {
            switch (value) {
                case 1:
                    return BY_DEPOSIT;
                case 2:
                    return BY_END_DATE;
                default:
                    return BY_DEPOSIT;
            }
        }
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private int id;
    private String name;
    private String image; // Stored as location to the desired image
    private float price;
    private FREQUENCY frequency;
    private METHOD method;
    private float deposit;
    private float savings;
    private Calendar startDate;
    private Calendar endDate;

//  Otkako ke se implementira servisot.
//  private Alarm m_alram;

    /**
     * Constructor that initializes based upon data stored in database
     *
     * @param id        Product id
     * @param name      Product name
     * @param image     Product image
     * @param price     Product price
     * @param frequency Deposit frequency
     * @param method    Saving method
     * @param deposit   Deposit value
     * @param savings   Current savings
     * @param startDate Start date
     * @param endDate   End date
     */
    public Product(int id, String name, String image, float price,
                   FREQUENCY frequency, METHOD method, float deposit,
                   float savings, Calendar startDate, Calendar endDate) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.frequency = frequency;
        this.method = method;
        this.deposit = deposit;
        this.savings = savings;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Constructor that initializes based upon the deposit value
     *
     * @param name      Product name
     * @param image     Product image
     * @param price     Product price
     * @param frequency Deposit frequency
     * @param deposit   Deposit value
     */
    public Product(String name, String image, float price, FREQUENCY frequency, float deposit) {
        this.id = (name + String.valueOf(price)).hashCode();
        this.name = name;
        this.image = image;
        this.price = price;
        this.frequency = frequency;
        this.method = METHOD.BY_DEPOSIT;
        this.deposit = deposit;
        this.savings = 0.0f;

        calcEndDate();
    }

    /**
     * Constructor that initializes based upon the date when the user wants to purchase the product
     *
     * @param name      Product name
     * @param image     Product image
     * @param price     Product price
     * @param frequency Deposit frequency
     * @param endDate   Saving end date
     */
    public Product(String name, String image, float price, FREQUENCY frequency, Calendar endDate) {
        this.id = (name + String.valueOf(price)).hashCode();
        this.name = name;
        this.image = image;
        this.price = price;
        this.frequency = frequency;
        this.method = METHOD.BY_END_DATE;
        this.savings = 0.0f;
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
        calcDeposit();
    }
    //endregion

    //region Getter and setter for frequency
    public FREQUENCY getFrequency() {
        return frequency;
    }

    public void setFrequency(FREQUENCY frequency) {
        this.frequency = frequency;
        calcDeposit();
    }
    //endregion

    //region Getter and setter for method
    public METHOD getMethod() {
        return method;
    }

    public void setMethod(METHOD method) {
        this.method = method;
    }
    //endregion

    //region Getter and setter for deposit
    public float getDeposit() {
        return deposit;
    }

    public void setDeposit(float deposit) {
        this.deposit = deposit;
        calcEndDate();
    }
    //endregion

    //region Getter and setter for savings
    public float getSavings() {
        return savings;
    }

    public void setSavings(float savings) {
        this.savings = savings;
        switch (method) {
            case BY_DEPOSIT: {
                calcDeposit();
                break;
            }
            case BY_END_DATE: {
                calcEndDate();
                break;
            }
        }
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

    //region Additional methods
    public void deposit() {
        savings += deposit;

//      Tuka ke se postavuva alarmot za datumot na sledeniot depozit.
    }
    //endregion

    //region Helper methods
    private void calcDeposit() {
        startDate = Calendar.getInstance();
        long timeDifference = endDate.getTimeInMillis() - startDate.getTimeInMillis();
        float balance = price - savings;

        switch (frequency) {
            case DAILY: {
                int days = (int) (timeDifference / (1000 * 60 * 60 * 24));
                deposit = balance / days;
                break;
            }
            case WEEKLY: {
                int weeks = (int) (timeDifference / (1000 * 60 * 60 * 24 * 7));
                deposit = balance / weeks;
                break;
            }
            case MONTHLY: {
                int months = (int) (timeDifference / (1000 * 60 * 60 * 24 * 7 * 12));
                deposit = balance / months;
                break;
            }
        }

        if (Logger.s_enabled) {
            Log.i("*Product: Product()", "The deposit is: " + String.valueOf(deposit));
        }
    }

    private void calcEndDate() {
        this.startDate = Calendar.getInstance();
        this.endDate = Calendar.getInstance();
        float balance = (price - savings);

        switch (frequency) {
            case DAILY: {
                int days = (int) (balance / deposit);
                endDate.add(Calendar.DAY_OF_YEAR, days);
                break;
            }
            case WEEKLY: {
                int weeks = (int) (balance / deposit);
                endDate.add(Calendar.WEEK_OF_YEAR, weeks);
                break;
            }
            case MONTHLY: {
                int months = (int) (balance / deposit);
                endDate.add(Calendar.MONTH, months);
                break;
            }
        }

        if (Logger.s_enabled) {
            Log.i("*Product: Product()", "The end date is: " + endDate.getTime().toString());
        }
    }
    //endregion
}
