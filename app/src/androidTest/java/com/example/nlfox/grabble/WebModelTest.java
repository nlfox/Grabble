package com.example.nlfox.grabble;

import android.content.Context;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class WebModelTest {

    WebModel w;

    @Before
    public void setUp() {
        w = new WebModel(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        //GrabbleApplication c = GrabbleApplication.getAppContext(appContext);

        assertEquals("com.example.nlfox.grabble", appContext.getPackageName());
    }


    @Test
    public void testScoreboard() throws Exception {
        List<Pair<String, Integer>> a = w.getScoreboard();
        System.out.println(a);
    }
}
