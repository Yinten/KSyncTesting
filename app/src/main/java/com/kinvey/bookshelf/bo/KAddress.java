package com.kinvey.bookshelf.bo;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by Yinten on 3/26/2018.
 *
 *
 //    city: string
 //    postal_code: string
 //    state: string (ISO 3166-2 code)
 //    country: string (ISO 3166-1 code)
 */

public class KAddress extends GenericJson
{

    public KAddress()
    {}



    @Key
    private String street;
    @Key
    private String city;
    @Key
    private String zipCode;
    @Key
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Key
    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }




}
