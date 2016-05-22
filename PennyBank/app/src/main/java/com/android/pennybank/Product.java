package com.android.pennybank;

import android.media.Image;
import android.util.*;

import java.util.Calendar;

public class Product {

    public enum FREQUENCY {DAILY, WEEKLY, MONTHLY}

    private String m_name;
    private Image m_image;
    private float m_price;
    private FREQUENCY m_frequency;
    private float m_deposit;
    private Calendar m_startDate;
    private Calendar m_endDate;

    // Otkako ke se implementira servisot.
    // private Alarm m_alram;


    public Product(String m_name, Image m_image, float m_price, FREQUENCY m_frequency,
                   float m_deposit, Calendar m_startDate) {
        this.m_name = m_name;
        this.m_image = m_image;
        this.m_price = m_price;
        this.m_frequency = m_frequency;
        this.m_deposit = m_deposit;
        this.m_startDate = m_startDate;
        this.m_endDate = Calendar.getInstance();
        this.m_endDate.setTime(m_startDate.getTime());

        switch (m_frequency) {
            case DAILY: {
                int days = (int) (m_price / m_deposit);
                m_endDate.add(Calendar.DAY_OF_YEAR, days);
                break;
            }
            case WEEKLY: {
                int weeks = (int) (m_price / m_deposit);
                m_endDate.add(Calendar.WEEK_OF_YEAR, weeks);
                break;
            }
            case MONTHLY: {
                int months = (int) (m_price / m_deposit);
                m_endDate.add(Calendar.MONTH, months);
                break;
            }
        }

        if (Logger.s_enabled) {
            Log.i("*Product: Product()", "The end date is: " + m_endDate.getTime().toString());
        }
    }

    public Product(String m_name, Image m_image, float m_price, FREQUENCY m_frequency,
                   Calendar m_startDate, Calendar m_endDate) {
        this.m_name = m_name;
        this.m_image = m_image;
        this.m_price = m_price;
        this.m_frequency = m_frequency;
        this.m_startDate = m_startDate;
        this.m_endDate = m_endDate;

        long diff = m_endDate.getTimeInMillis() - m_startDate.getTimeInMillis();

        switch (m_frequency) {
            case DAILY: {
                int days = (int) (diff / (1000 * 60 * 60 * 24));
                m_deposit = m_price / days;
                break;
            }
            case WEEKLY: {
                int weeks = (int) (diff / (1000 * 60 * 60 * 24 * 7));
                m_deposit = m_price / weeks;
                break;
            }
            case MONTHLY: {
                int months = (int) (diff / (1000 * 60 * 60 * 24 * 7 * 12));
                m_deposit = m_price / months;
                break;
            }
        }

        if (Logger.s_enabled) {
            Log.i("*Product: Product()", "The deposit is: " + String.valueOf(m_deposit));
        }
    }
}
