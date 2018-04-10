package com.kinvey.bookshelf;

import android.support.multidex.MultiDexApplication;

import com.kinvey.android.Client;
import com.kinvey.bookshelf.bo.CCUUser;

/**
 * Created by Prots on 3/15/16.
 */
public class App extends MultiDexApplication {

    private Client sharedClient;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedClient = new Client.Builder(this).setUserClass(CCUUser.class).build();
    }

    public Client getSharedClient(){
        return sharedClient;
    }
}
