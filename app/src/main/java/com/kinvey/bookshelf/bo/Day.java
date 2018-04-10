package com.kinvey.bookshelf.bo;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Day extends GenericJson {
    @Key
    private int day;

    public Day()
    {

    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
