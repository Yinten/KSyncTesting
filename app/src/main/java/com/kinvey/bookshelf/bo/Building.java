package com.kinvey.bookshelf.bo;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


/**
 * InstallerEmail & Owner will be provided by roles.
 * In the previous iteration these were provided by properties on this collection.
 *
 *
 */
public class Building extends GenericJson {

    public Building()
    {
        //Generic JSON requires empty constructor.
    }

    @Key("_id")
    private String id;

    @Key("tuners")
    private HashMap<String, Object> tuners;

    @Key("schedules")
    public List<Schedule> schedules;

    @Key
    private String name;

    @Key
    private KAddress address;

    @Key
    private String timeZone;

    @Key("_geoloc")
    private double[] _geoloc; //Deprecates lat, lang

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KAddress getAddress() {
        return address;
    }

    public void setAddress(KAddress address) {
        this.address = address;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public double[] get_geoloc() {
        return _geoloc;
    }

    public void set_geoloc(double[] _geoloc) {
        this._geoloc = _geoloc;
    }

    public HashMap<String, Object> getTuners() {
        return tuners;
    }

    public void setTuners(HashMap<String, Object> tuners) {
        this.tuners = tuners;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
