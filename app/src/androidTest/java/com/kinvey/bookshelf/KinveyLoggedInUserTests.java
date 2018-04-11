package com.kinvey.bookshelf;

import android.os.Handler;
import android.os.Looper;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.api.client.json.GenericJson;
import com.kinvey.android.Client;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.android.sync.KinveyPullResponse;
import com.kinvey.android.sync.KinveyPushResponse;
import com.kinvey.android.sync.KinveySyncCallback;
import com.kinvey.bookshelf.bo.BasicEntity;
import com.kinvey.bookshelf.bo.Building;
import com.kinvey.bookshelf.bo.Day;
import com.kinvey.bookshelf.bo.KAddress;
import com.kinvey.bookshelf.bo.Schedule;
import com.kinvey.bookshelf.bo.ScheduleInternal;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.store.StoreType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
@MediumTest

/***
 *  Objective:
 *  #First, Cycle a user between tests and clear the datastores.
 *   Create a simple object 'book' that has at least one internal object.
 *   Save the book using the cache mechanism.
 *   ##Passes
 *
 *
 *
 *   #Second, test using a more complex object and the sync mechanism with sync framework.
 *  The example project says, sync successful, but it doesn't use the 'sync' framework it uses
 *  the 'cache' framework.
 *       This is the line of code that is used for the
 *       'sync' example and it has StoreType.CACHE
 *       in the demo.
 *  bookStore = DataStore.collection(Constants.BOOK_COLLECTION_NAME,
 *      Book.class, StoreType.CACHE, client);
 *
 *   #Third, create a Basic entity test mimicking the "Building tests" using a cache store and a sync store.
 *   show that these unit tests pass.   Meaning that the issue seems isolated to using nested GenericJSON
 *   when using a StoreType.SYNC.
 *
 *
 *
 *  Per the sync documentation on Kinvey website.
 *       https://devcenter.kinvey.com/android/guides/datastore#Fetching
 *       DataStore<Book> dataStore = DataStore.collection("books", Book.class, StoreType.SYNC, client);
 *  ########Fails######### -
 *   java.lang.IllegalArgumentException: invalid value for field
 * at java.lang.reflect.Field.setField(Native Method)
 * at java.lang.reflect.Field.set(Field.java:585)
 * at com.google.api.client.util.FieldInfo.setFieldValue(FieldInfo.java:245)
 * at com.google.api.client.util.FieldInfo.setValue(FieldInfo.java:206)
 * at com.google.api.client.util.GenericData.put(GenericData.java:103)
 * at com.kinvey.android.cache.ClassHash.realmToObject(ClassHash.java:857)
 * at com.kinvey.android.cache.ClassHash.realmToObject(ClassHash.java:775)
 * at com.kinvey.android.cache.RealmCache.get(RealmCache.java:180)
 * at com.kinvey.android.async.AsyncPushRequest.executeAsync(AsyncPushRequest.java:129)
 * at com.kinvey.android.async.AsyncPushRequest.executeAsync(AsyncPushRequest.java:49)
 * at com.kinvey.android.AsyncClientRequest.run(AsyncClientRequest.java:65)
 *
 *
 *
 * Thirdly, test using a more complex object and the save mechanism with cache framework.
 *   ##Passes -
 * Building: {
 * "address": {
 *       "city": "Hudson",
 *       "country": "USA",
 *       "state": "WI",
 *       "street": "223 Rivercrest Dr",
 *       "zipCode": "54016"
 *     },
 *     "name": "RyanTestBuilding",
 *     "schedules": [
 *       {
 *         "internalSchedules": [
 *           {
 *             "days": [
 *               {
 *                 "day": 0
 *               }
 *             ],
 *             "ethh": 10,
 *             "etmm": 15,
 *             "sthh": 1,
 *             "stmm": 30,
 *             "sunrise": false,
 *             "sunset": false,
 *             "val": 75.0
 *           }
 *         ],
 *         "type": "Light"
 *       }
 *     ],
 *     "timeZone": "US/Chicago",
 *     "tuners": {},
 *     "_id": "23dd2e10-a273-423f-8cd5-3826bc6d3be3",
 *     "_acl": {
 *       "creator": "5acc605fc92757629cbdc922"
 *     },
 *     "_kmd": {
 *       "lmt": "2018-04-10T15:10:25.611Z",
 *       "ect": "2018-04-10T15:10:25.611Z"
 *     }
 *   }
 * buildingStore.sync(: onPushStarted
 * buildingStore.sync(: onPushSuccess
 * buildingStore.sync(: onPullStarted
 * buildingStore.sync(: onPullSuccess
 * buildingStore.sync(: OnSuccess
 */
public class KinveyLoggedInUserTests {

    private static final String TAG = "KINVEY_TESTS";

    /* The application context */
    private App app;

    /* A simple bookstore cache */
    private DataStore<Book> bookStore;

    /* A simple building store sync*/
    //StoreType.SYNC
    private DataStore<Building> buildingStoreSync;

    /* A building store based on a cache */
    //StoreType.CACHE
    private DataStore<Building> buildingStoreCache;

    /* A basic entity store sync*/
    private DataStore<BasicEntity> basicEntityStoreSync;

    /* A basic entity store cache */
    private DataStore<BasicEntity> basicEntityStoreCache;


    /* Load the shelf activity */
    @Rule
    public ActivityTestRule<ShelfActivity> mActivityRule =
            new ActivityTestRule<>(ShelfActivity.class);


    /*  A user must be logged in before a latch is released to run tests*/
    CountDownLatch loginLatch;


    /*  The setup logs out the user, clears the datastores, and logs in a test user.
        Based on the Kinvey documentation a new datastore must be used when a user is
        logged out.   When a user is logged out, it also clears the datastore
        to secure the information for the user.

        Just in case the bugs reside here, I manually clear the datastore's as well.

        Since the login & logout process is asynchronous and long running process,
        I hold a login latch.   This login latch pauses the other tests until
        they are notified that the login latch is released.

        If a failure occurs logging in and logging out I abort the tests using quit method.

     */
    @Before
    public void setUp() {
        loginLatch = new CountDownLatch(1);
        app = (App) mActivityRule.getActivity().getApplication();

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {

            public void run() {
                try {

                    /* If the user is logged in, log them out. */
                    if (getClient().isUserLoggedIn()) {
                        Log.i(TAG, "Kinvey User was logged in, logging out");
                        logout();

                    } else {
                        /* If the user was never logged in, log in*/
                        Log.i(TAG, "Kinvey User was not logged in, logging in now");
                        login();

                    }


                } catch (IOException e) {

                    quit(e);
                }
            }
        });
        Log.i(TAG, "Setup underway to logout / login user");
        waitForLoginLatch();
        Log.i(TAG, "Setup finished, login latch released");

    }

    private void logout() {
        UserStore.logout(getClient(), new KinveyClientCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Successfully logged out Kinvey User, logging in test user");
                try {
                    login();
                } catch (IOException e) {
                    quit(e);
                }
            }


            @Override
            public void onFailure(Throwable e) {
                quit(e);
            }
        });
    }

    /***
     * Login the user and clear the datastores.
     * @throws IOException
     */
    private void login() throws IOException {
        Log.d(TAG, "login()");
        UserStore.login(TestConstants.testUserName, TestConstants.testPassword, getClient(), new KinveyClientCallback() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Kinvey: Login Success");

                /* User is logged in, reinitialize the data stores.   I don't entirely follow why this is needed, but I'm
                trying my best to follow the documentation.
                 */

                //Cache based bookstore
                bookStore = DataStore.collection(Constants.BOOK_COLLECTION_NAME, Book.class, StoreType.CACHE, getClient());

                //Cache based building store
                buildingStoreCache = DataStore.collection(Constants.BUILDING_COLLECTION_NAME, Building.class, StoreType.CACHE, getClient());

                //Sync based building store
                buildingStoreSync = DataStore.collection(Constants.BUILDING_COLLECTION_NAME, Building.class, StoreType.SYNC, getClient());


                //Cache based basic entity with no nested GenericJSON, building has nested GenericJSON.
                basicEntityStoreCache = DataStore.collection("BasicEntityCache", BasicEntity.class, StoreType.CACHE, getClient());

                //Sync based basic entity with no nested GenericJSON, building has nested GenericJSON.
                basicEntityStoreSync = DataStore.collection("BasicEntitySync", BasicEntity.class, StoreType.SYNC, getClient());




                //Just in case
                bookStore.clear();
                buildingStoreCache.clear();
                buildingStoreSync.clear();
                basicEntityStoreCache.clear();
                basicEntityStoreSync.clear();

                //Release the latch to allow tests to run.
                loginLatch.countDown();
            }


            @Override
            public void onFailure(Throwable throwable) {
                quit(throwable);

            }
        });


    }


    /* The user was never logged in
       as a  precondition, so fire nukes at system and tear it down.

       Tests shouldn't run without user.
    */
    private void quit(Throwable throwable) {
        throwable.printStackTrace();
        Assert.assertNull(throwable);
    }

    /* The Kinvey client */
    private Client getClient() {
        return app.getSharedClient();
    }


    /***
     * Test book with internal object Address against a cache datastore.
     * @throws IOException
     */
    @Test
    public void testBookWithAddress() {
        final CountDownLatch testBookWithAddressLatch = new CountDownLatch(1);

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {

            public void run() {


                Book book = new Book();
                book.setName("Test Book");
                CAddress cAddress = new CAddress();
                cAddress.setAddress("131 Oak Avenue N");
                book.setAddress(cAddress);
                bookStore.save(book,
                        new KinveyClientCallback<Book>() {
                            @Override
                            public void onSuccess(Book result) {
                                try {
                                    Log.d(TAG, result.toPrettyString());
                                } catch (IOException e) {
                                    Assert.fail(e.getMessage());
                                } finally {
                                    testBookWithAddressLatch.countDown();
                                }
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                error.printStackTrace();
                                Assert.fail(error.getMessage());

                            }
                        });
            }
        });
        try {
            testBookWithAddressLatch.await();
        } catch (InterruptedException e) {
            quit(e);
        }
    }


    /***
     * Test a building with a lot of complex objects against the sync framework.
     */
    @Test
    public void testSyncBuilding() {
        CountDownLatch latch = new CountDownLatch(1);
        testBuilding(buildingStoreSync, latch);
        try {
            latch.await();
        } catch (InterruptedException e) {
            quit(e);
        }
    }


    /***
     * Sync the locally stored buildings in the
     * buildingStoreCache to Kinvey.   This differs from buildStoreSync in the StoreType parameter,
     * the remainder of the test is the same.
     *
     * Currently fails looking for help to diagnose.
     */
    @Test
    public void testCacheBuilding() {
        CountDownLatch latch = new CountDownLatch(1);
        testBuilding(buildingStoreCache, latch);
        try {
            latch.await();
        } catch (InterruptedException e) {
            quit(e);
        }
    }


    /***
     * Sync the locally stored basic entities in the
     * @basicEntityStoreCache to Kinvey.   This differs from basicEntityStoreSync in the StoreType parameter,
     * the remainder of the test is the same.
     *
     * Currently fails, whatever my issue here is, have spent a lot of time trying to diagnose.
     */
    @Test
    public void testCacheBasicEntity() {
        CountDownLatch latch = new CountDownLatch(1);
        testBasicEntity(basicEntityStoreCache, latch);
        try {
            latch.await();
        } catch (InterruptedException e) {
            quit(e);
        }
    }


    /***
     * Test a building with a lot of complex objects against the sync framework.
     */
    @Test
    public void testSyncBasicEntity() {
        CountDownLatch latch = new CountDownLatch(1);
        testBasicEntity(basicEntityStoreSync, latch);
        try {
            latch.await();
        } catch (InterruptedException e) {
            quit(e);
        }
    }


    /***
     * Sync the locally stored buildings in the
     * buildingStoreSync to Kinvey
     *
     * Currently fails, whatever my issue here is, I think will resolve all my issues for a good long time.
     */
    public void testStoreSync(DataStore datastore, final CountDownLatch latch) {

        datastore.sync(new KinveySyncCallback()

        {

            @Override
            public void onSuccess(KinveyPushResponse kinveyPushResponse, KinveyPullResponse kinveyPullResponse) {
                Log.d(TAG, "buildingStore.sync(: OnSuccess");
                latch.countDown();
            }

            @Override
            public void onPullStarted() {
                Log.d(TAG, "buildingStore.sync(: onPullStarted");
            }

            @Override
            public void onPushStarted() {
                Log.d(TAG, "buildingStore.sync(: onPushStarted");
            }

            @Override
            public void onPullSuccess(KinveyPullResponse kinveyPullResponse) {
                Log.d(TAG, "buildingStore.sync(: onPullSuccess");
            }

            @Override
            public void onPushSuccess(KinveyPushResponse kinveyPushResponse) {
                Log.d(TAG, "buildingStore.sync(: onPushSuccess");
            }

            @Override
            public void onFailure(final Throwable throwable) {
                Log.d(TAG, "buildingStore.sync(: onFailure");
                throwable.printStackTrace();
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {

                    public void run() {
                        Assert.assertNull(getThrowableStringFull(throwable));
                    }
                });


            }
        });
    }


    private void waitForLoginLatch() {
        try {
            loginLatch.await();
        } catch (InterruptedException e) {
            quit(e);
        }
    }

    /***
     * Save and sync a building to the Kinvey datastore.   It'll use the Datastores StoreType.
     *
     * This is used to test the differences between sync & cache etc.
     */
    private void testBuilding(final DataStore<Building> buildingStore, final CountDownLatch latch) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {

            public void run() {

                Building building = createNewBuilding();


                buildingStore.save(building, new KinveyClientCallback<Building>() {
                    @Override
                    public void onSuccess(Building building) {
                        Log.d(TAG, "buildingStore.save(: OnSuccess");
                        try {
                            Log.d(TAG, "Building: " + building.toPrettyString());
                            testStoreSync(buildingStore, latch);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Assert.fail(e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        throwable.printStackTrace();
                        Assert.fail(throwable.getMessage());


                    }
                });

            }
        });

    }



    /***
     * Save and sync a BasicEntity to the Kinvey datastore.   It'll use the Datastores StoreType.
     *
     * This is used to test the differences between sync & cache etc.
     */
    private void testBasicEntity(final DataStore<BasicEntity> dataStore, final CountDownLatch latch) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {

            public void run() {

                BasicEntity basicEntity = createNewBasicEntity();


                dataStore.save(basicEntity, new KinveyClientCallback<BasicEntity>() {
                    @Override
                    public void onSuccess(BasicEntity basicEntity1) {
                        Log.d(TAG, "Basic Entity.save(: OnSuccess");
                        try {
                            Log.d(TAG, "Basic Entity: " + basicEntity1.toPrettyString());
                            testStoreSync(dataStore, latch);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Assert.fail(e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        throwable.printStackTrace();
                        Assert.fail(throwable.getMessage());


                    }
                });

            }
        });

    }


    private BasicEntity createNewBasicEntity()
    {
        BasicEntity basicEntity = new BasicEntity();
        Random r = new Random();
        basicEntity.setName("Name " + r.nextInt());
        return basicEntity;
    }


    /***
     * Returns the full stack trace as a readable String for printing in asserts
     * to make debugging slightly less Logcat based.
     * @param throwable
     * @return throwable's stack trace as String
     */
    private String getThrowableStringFull(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.flush();

        return writer.toString();
    }

    public Building createNewBuilding() {
        Building building = new Building();

        KAddress kAddress = new KAddress();
        kAddress.setStreet("223 Rivercrest Dr");
        kAddress.setCity("Hudson");
        kAddress.setCountry("USA");
        kAddress.setState("WI");
        kAddress.setZipCode("54016");
        building.setAddress(kAddress);

        //building.set_geoloc(new double[]{12.22F, 11.11F});

        building.setName("RyanTestBuilding");

        building.setTimeZone("US/Chicago");
        HashMap tunerHashmap = new HashMap<String, Object>();

        //tunerHashmap.put("Tuner1", 5);
        building.setTuners(tunerHashmap);

        ArrayList<Schedule> scheduleArrayList = new ArrayList<>();
        Schedule newSchedule = new Schedule();

        newSchedule.type = "Light";
        ArrayList<ScheduleInternal> scheduleInternalArrayList = new ArrayList<>();
        ScheduleInternal scheduleInternal = new ScheduleInternal();
        scheduleInternal.days = new ArrayList<Day>();
        Day one = new Day();
        scheduleInternal.days.add(one);
        scheduleInternal.setSthh(1);
        scheduleInternal.setEthh(10);
        scheduleInternal.setStmm(30);
        scheduleInternal.setEtmm(15);
        scheduleInternal.setVal(75);
        scheduleInternalArrayList.add(scheduleInternal);
        newSchedule.setInternalSchedules(scheduleInternalArrayList);
        scheduleArrayList.add(newSchedule);


        building.setSchedules(scheduleArrayList);


        return building;
    }
}
