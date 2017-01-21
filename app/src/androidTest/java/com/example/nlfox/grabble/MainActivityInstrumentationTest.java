package com.example.nlfox.grabble;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentationTest {
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            SplashActivity.class);
    private boolean root;
    Activity currentActivity;


    @Before
    public void setUp() {
        onView(withId(R.id.grabble)).perform(click());
        onView(withId(R.id.action_suggest)).perform(click());
    }

    public void setWord(String word) {
        ViewGroup v = (ViewGroup) getActivityInstance().findViewById(getActivityInstance().getResources().getIdentifier("topright", "id", getActivityInstance().getPackageName()));
        for (int j = 0; j < word.length(); j++) {
            ImageView imageView = (ImageView) v.getChildAt(j);
            imageView.setTag(word.charAt(j));
        }
    }

    public Activity getActivityInstance() {
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    currentActivity = (Activity) resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity;
    }

    @Test
    public void checkSnackBar() {

    }


    @Test
    public void checkSubmit() throws InterruptedException {
        setWord("yyssnnn");
        onView(withId(R.id.scrabble_submit)).perform(click());
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Wrong word. Try again")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkSuggest() throws InterruptedException {
        setWord("holi");
        onView(withId(R.id.action_suggest)).perform(click());
        Thread.sleep(5000);
        String s = "";
        for (int i = 0; i < 1; i++) {
            ViewGroup v = (ViewGroup) getActivityInstance().findViewById(getActivityInstance().getResources().getIdentifier("suggestion" + Integer.toString(i + 1), "id", getActivityInstance().getPackageName()));
            for (int j = 0; j < 7; j++) {
                s += v.getChildAt(j).getTag().toString();
            }
        }
        assertTrue(s.equals("holiday"));
    }





}