package com.kinvey.bookshelf.bo;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class BasicEntity extends GenericJson {

    public BasicEntity()
    {
        //Generic Json requires empty constructor.
    }

    @Key("_id")
    private String id;

    @Key("name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
