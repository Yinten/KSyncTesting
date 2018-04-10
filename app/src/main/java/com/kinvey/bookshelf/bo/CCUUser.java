package com.kinvey.bookshelf.bo;

import com.google.api.client.util.Key;
import com.kinvey.android.model.User;
import com.kinvey.java.auth.KinveyAuthResponse;
import com.kinvey.java.model.KinveyMetaData;

import java.util.Collection;

/**
 * Created by Yinten on 9/4/2017.
 */

public class CCUUser extends User {
    @Key("_id")
    private String id;

    @Key
    private String firstname;

    @Key
    private String lastname;

    @Key
    private String domain;

    @Key
    private String email;

    @Key
    private String password;

    @Key
    private KAddress kAddress;


    @Key("_geoloc")
    private float[] _geoloc; //Deprecates lat, lang


    //    @Key("_kmd")
    //    private KinveyMetaData meta;
    //
    //    @Key("_acl")
    //    private KinveyMetaData.AccessControlList acl;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public float[] get_geoloc() {
        return _geoloc;
    }

    public void set_geoloc(float[] _geoloc) {
        this._geoloc = _geoloc;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

//    @Key("_kmd")
//    private KinveyAuthResponse.KinveyUserMetadata meta;
//    @Key("_acl")
//    private KinveyMetaData.AccessControlList acl;
//
//    public KinveyAuthResponse.KinveyUserMetadata getMeta() {
//        return meta;
//    }
//
//    public void setMeta(KinveyAuthResponse.KinveyUserMetadata meta) {
//        this.meta = meta;
//    }
//
//    public KinveyMetaData.AccessControlList getAcl() {
//        return acl;
//    }
//
//    public void setAcl(KinveyMetaData.AccessControlList acl) {
//        this.acl = acl;
//    }

    public KAddress getkAddress() {
        return kAddress;
    }

    public void setkAddress(KAddress kAddress) {
        this.kAddress = kAddress;
    }
}
