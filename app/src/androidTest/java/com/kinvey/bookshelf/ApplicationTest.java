package com.kinvey.bookshelf;

import android.os.Handler;
import android.os.Looper;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.kinvey.android.Client;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.bookshelf.bo.CCUUser;
import com.kinvey.bookshelf.bo.KAddress;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.store.StoreType;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ApplicationTest {

    private static final String TAG = "KINVEY_TESTS";
    private App app;

    private DataStore<Book> bookStore;
    @Rule
    public ActivityTestRule<ShelfActivity> mActivityRule =
            new ActivityTestRule<>(ShelfActivity.class);


    CountDownLatch countDownLatch;
    public void testRun()
    {
        Assert.fail();

    }

    @Before
    public void setUp() {

        Log.i(TAG, "Kinvey Setup: " + TAG);
        app = (App)mActivityRule.getActivity().getApplication();
        bookStore = DataStore.collection(Constants.BOOK_COLLECTION_NAME, Book.class, StoreType.CACHE, getClient());

    }

    private Client getClient()
    {
        return ((App)mActivityRule.getActivity().getApplication()).getSharedClient();
    }

    @Test
    public void testRegisteringNewUser() {
        countDownLatch = new CountDownLatch(1);
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {

            public void run() {


                if (((App)mActivityRule.getActivity().getApplication()).getSharedClient().isUserLoggedIn()) {
                    UserStore.logout(((App)mActivityRule.getActivity().getApplication()).getSharedClient(), new KinveyClientCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            registerUserTestExtension();
                        }


                        @Override
                        public void onFailure(Throwable throwable) {
                            Assert.fail(throwable.getMessage());
                            countDownLatch.countDown();
                        }
                    });
                } else {
                    registerUserTestExtension();
                }

            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBookWithAddress()
    {
        countDownLatch = new CountDownLatch(1);
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
                        }
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        error.printStackTrace();
                        Assert.fail(error.getMessage());
                        countDownLatch.countDown();
                    }
                });
    }


    private void registerUserTestExtension() {

        try {

            //InputStream inputStream = context.getAssets().open("User.json");
            //CCUUser user = fromJson(inputStream, CCUUser.class);

            CCUUser ccuUser = new CCUUser();
            Random r = new Random();
            ccuUser.setUsername("YintenStole22" + r.nextInt(100000));
            ccuUser.setPassword("Password");
            KAddress address = new KAddress();
            address.setStreet("131 Oak Ave N");

            ccuUser.setFirstname("Ryan");
            //ccuUser.setAddressInformation(address);
            ccuUser.setkAddress(address);
            Assert.assertNotNull(ccuUser);
            //Assert.assertNotNull(ccuUser.getAddressInformation());
            Assert.assertSame(ccuUser.getFirstname(), "Ryan");
            // Assert.assertSame(ccuUser.getAddressInformation().getAddress(), "address");

            UserStore
                    .signUp(ccuUser.getUsername(), ccuUser.getPassword(), ccuUser, ((App)mActivityRule.getActivity().getApplication()).getSharedClient(), new KinveyClientCallback<CCUUser>() {
                        @Override
                        public void onSuccess(CCUUser ccuUser) {
                            try {
                                Log.i("ApplicationTest",
                                        "Logged in: " + ccuUser.toPrettyString());
                                countDownLatch.countDown();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        @Override
                        public void onFailure(Throwable throwable) {
                            Assert.fail(throwable.getMessage());
                            countDownLatch.countDown();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            countDownLatch.countDown();
        }
    }


}