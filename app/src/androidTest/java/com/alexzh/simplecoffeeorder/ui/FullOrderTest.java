package com.alexzh.simplecoffeeorder.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import com.alexzh.simplecoffeeorder.R;
import com.alexzh.simplecoffeeorder.ServiceIdlingResource;
import com.alexzh.simplecoffeeorder.view.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.alexzh.simplecoffeeorder.actions.RecyclerChildViewActions.checkTextViewCountForCoffee;
import static com.alexzh.simplecoffeeorder.actions.RecyclerChildViewActions.clickToViewChildItem;
import static com.alexzh.simplecoffeeorder.utils.StringUtils.getString;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FullOrderTest {
    private UiDevice mDevice;
    private ServiceIdlingResource mServiceIdlingResource;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setup() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        mServiceIdlingResource =
                new ServiceIdlingResource(mActivityRule.getActivity().getApplicationContext());
        registerIdlingResources(mServiceIdlingResource);
    }

    @Test
    public void orderThreeEspresso() {
        final String espresso = "Espresso";
        final String espressoCount = "3";
        final float totalCoffeePrice = 15.0f;
        final float coffeePrice = 5.0f;
        final String notificationTitle = "Coffee order app";
        final String notificationText = "Thank you for your payment.";

        clickToViewChildItem(R.id.recyclerView, espresso, R.id.coffee_increment);
        clickToViewChildItem(R.id.recyclerView, espresso, R.id.coffee_increment);
        clickToViewChildItem(R.id.recyclerView, espresso, R.id.coffee_increment);

        checkTextViewCountForCoffee(R.id.recyclerView, R.id.coffee_count, espresso, String.valueOf(espressoCount));
        onView(withId(R.id.total_price_toolbar)).check(matches(withText(getString(mActivityRule, R.string.price, totalCoffeePrice))));

        onView(withId(R.id.pay)).perform(click());

        onData(anything()).atPosition(0).onChildView(withId(R.id.coffee_name)).check(matches(withText(espresso)));
        onData(anything()).atPosition(0).onChildView(withId(R.id.coffee_count)).check(matches(withText(espressoCount)));
        onData(anything()).atPosition(0).onChildView(withId(R.id.coffee_price)).check(matches(withText(getString(mActivityRule, R.string.price, coffeePrice))));
        onData(anything()).atPosition(0).onChildView(withId(R.id.total_price)).check(matches(withText(getString(mActivityRule, R.string.price, totalCoffeePrice))));

        onView(withId(R.id.pay)).perform(click());

        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.text(notificationTitle)), 3000);
        UiObject2 title = mDevice.findObject(By.text(notificationTitle));
        UiObject2 text = mDevice.findObject(By.text(notificationText));

        assertEquals(notificationTitle, title.getText());
        assertEquals(notificationText, text.getText());

        title.click();

        onData(anything()).atPosition(0).onChildView(withId(R.id.coffee_name)).check(matches(withText(espresso)));
        onData(anything()).atPosition(0).onChildView(withId(R.id.coffee_count)).check(matches(withText(espressoCount)));
        onData(anything()).atPosition(0).onChildView(withId(R.id.coffee_price)).check(matches(withText(getString(mActivityRule, R.string.price, coffeePrice))));
        onData(anything()).atPosition(0).onChildView(withId(R.id.total_price)).check(matches(withText(getString(mActivityRule, R.string.price, totalCoffeePrice))));
    }

    @After
    public void tearDown() {
        unregisterIdlingResources(mServiceIdlingResource);
        mDevice.pressBack();
    }

}