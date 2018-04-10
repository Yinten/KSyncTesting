package com.kinvey.bookshelf.bo;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

public class ScheduleInternal extends GenericJson {

    public ScheduleInternal()
    {
        //Required by GenericJSON
    }


    //[0, 1, 2, 3, 4]
    @Key
    public List<Day> days;

    @Key
    boolean sunrise;

    @Key
    boolean sunset;

    @Key
    private int sthh;

    @Key
    private int stmm;

    @Key
    private int ethh;

    @Key
    private int etmm;

    @Key
    private double val;


    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public boolean isSunrise() {
        return sunrise;
    }

    public void setSunrise(boolean sunrise) {
        this.sunrise = sunrise;
    }

    public boolean isSunset() {
        return sunset;
    }

    public void setSunset(boolean sunset) {
        this.sunset = sunset;
    }

    public int getSthh() {
        return sthh;
    }

    public void setSthh(int sthh) {
        this.sthh = sthh;
    }

    public int getStmm() {
        return stmm;
    }

    public void setStmm(int stmm) {
        this.stmm = stmm;
    }

    public int getEthh() {
        return ethh;
    }

    public void setEthh(int ethh) {
        this.ethh = ethh;
    }

    public int getEtmm() {
        return etmm;
    }

    public void setEtmm(int etmm) {
        this.etmm = etmm;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }
}



