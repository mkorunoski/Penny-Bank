package com.android.pennybank;

import android.media.Image;
import android.util.*;

import java.util.Calendar;

/**
 * This class represents the product that the user wants to save money for.
 */
public class Product {

    public enum FREQUENCY {
        DAILY,  /// The user will deposit daily
        WEEKLY, /// The user will deposit weekly
        MONTHLY /// The user will deposit monthly
    }

    public enum METHOD {
        BY_DEPOSIT, /// Saving based upon the specified deposit value
        BY_END_DATE /// Saving based upon the date when the user wants to purchase the product
    }

    private String m_name;
    private Image m_image;
    private float m_price;
    private FREQUENCY m_frequency;
    private METHOD m_method;
    private float m_deposit;
    private float m_saving;
    private Calendar m_startDate;
    private Calendar m_endDate;

//     Otkako ke se implementira servisot.
//     private Alarm m_alram;

    /**
     * Constructor that initializes based upon the deposit value
     *
     * @param m_name      Product name
     * @param m_image     Product image
     * @param m_price     Product price
     * @param m_frequency Deposit frequency
     * @param m_deposit   Deposit value
     */
    public Product(String m_name, Image m_image, float m_price, FREQUENCY m_frequency, float m_deposit) {
        this.m_name = m_name;
        this.m_image = m_image;
        this.m_price = m_price;
        this.m_frequency = m_frequency;
        this.m_method = METHOD.BY_DEPOSIT;
        this.m_deposit = m_deposit;
        this.m_saving = 0.0f;

        calcEndDate();
    }

    /**
     * Constructor that initializes based upon the date when the user wants to purchase the product
     *
     * @param m_name      Product name
     * @param m_image     Product image
     * @param m_price     Product price
     * @param m_frequency Deposit frequency
     * @param m_endDate   Saving end date
     */
    public Product(String m_name, Image m_image, float m_price, FREQUENCY m_frequency, Calendar m_endDate) {
        this.m_name = m_name;
        this.m_image = m_image;
        this.m_price = m_price;
        this.m_frequency = m_frequency;
        this.m_method = METHOD.BY_END_DATE;
        this.m_saving = 0.0f;
        this.m_endDate = m_endDate;

        calcDeposit();
    }

    //region Getter and setter for m_name
    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }
    //endregion

    //region Getter and setter for m_image
    public Image getM_image() {
        return m_image;
    }

    public void setM_image(Image m_image) {
        this.m_image = m_image;
    }
    //endregion

    //region Getter and setter for m_price
    public float getM_price() {
        return m_price;
    }

    public void setM_price(float m_price) {
        this.m_price = m_price;
        calcDeposit();
    }
    //endregion

    //region Getter and setter for m_frequency
    public FREQUENCY getM_frequency() {
        return m_frequency;
    }

    public void setM_frequency(FREQUENCY m_frequency) {
        this.m_frequency = m_frequency;
        calcDeposit();
    }
    //endregion

    //region Getter and setter for m_deposit
    public float getM_deposit() {
        return m_deposit;
    }

    public void setM_deposit(float m_deposit) {
        this.m_deposit = m_deposit;
        calcEndDate();
    }
    //endregion

    //region Getter and setter for m_saving
    public float getM_saving() {
        return m_saving;
    }

    public void setM_saving(float m_saving) {
        this.m_saving = m_saving;
        switch (m_method) {
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

    //region Getter and setter for m_startDate
    public Calendar getM_startDate() {
        return m_startDate;
    }

//  public void setM_startDate(Calendar m_startDate) {
//      this.m_startDate = m_startDate;
//  }
    //endregion

    //region Getter and setter for m_endDate
    public Calendar getM_endDate() {
        return m_endDate;
    }

    public void setM_endDate(Calendar m_endDate) {
        this.m_endDate = m_endDate;
        calcDeposit();
    }
    //endregion

    //region Additional methods
    public void deposit() {
        m_saving += m_deposit;

//      Tuka ke se postavuva alarmot za datumot na sledeniot depozit.
    }
    //endregion

    //region Helper methods
    private void calcDeposit() {
        m_startDate = Calendar.getInstance();
        long timeDifference = m_endDate.getTimeInMillis() - m_startDate.getTimeInMillis();
        float balance = m_price - m_saving;

        switch (m_frequency) {
            case DAILY: {
                int days = (int) (timeDifference / (1000 * 60 * 60 * 24));
                m_deposit = balance / days;
                break;
            }
            case WEEKLY: {
                int weeks = (int) (timeDifference / (1000 * 60 * 60 * 24 * 7));
                m_deposit = balance / weeks;
                break;
            }
            case MONTHLY: {
                int months = (int) (timeDifference / (1000 * 60 * 60 * 24 * 7 * 12));
                m_deposit = balance / months;
                break;
            }
        }

        if (Logger.s_enabled) {
            Log.i("*Product: Product()", "The deposit is: " + String.valueOf(m_deposit));
        }
    }

    private void calcEndDate() {
        this.m_startDate = Calendar.getInstance();
        this.m_endDate = Calendar.getInstance();
        float balance = (m_price - m_saving);

        switch (m_frequency) {
            case DAILY: {
                int days = (int) (balance / m_deposit);
                m_endDate.add(Calendar.DAY_OF_YEAR, days);
                break;
            }
            case WEEKLY: {
                int weeks = (int) (balance / m_deposit);
                m_endDate.add(Calendar.WEEK_OF_YEAR, weeks);
                break;
            }
            case MONTHLY: {
                int months = (int) (balance / m_deposit);
                m_endDate.add(Calendar.MONTH, months);
                break;
            }
        }

        if (Logger.s_enabled) {
            Log.i("*Product: Product()", "The end date is: " + m_endDate.getTime().toString());
        }
    }
    //endregion
}
