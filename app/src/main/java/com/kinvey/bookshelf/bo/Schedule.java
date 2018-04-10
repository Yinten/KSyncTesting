package com.kinvey.bookshelf.bo;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Schedule extends GenericJson{

    @Key
    public String type;

    @Key
    public List<ScheduleInternal> internalSchedules;


    public Schedule()
    {
    }  //GenericJson classes must have a public empty constructor


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ScheduleInternal> getInternalSchedules() {
        return internalSchedules;
    }

    public void setInternalSchedules(List<ScheduleInternal> internalSchedules) {
        this.internalSchedules = internalSchedules;
    }
}
