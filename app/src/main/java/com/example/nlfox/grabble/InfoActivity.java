package com.example.nlfox.grabble;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.preference.PreferenceFragment;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.nlfox.grabble.dummy.ScoreboardContent;

public class InfoActivity extends AppCompatActivity implements ScoreboardFragment.OnListFragmentInteractionListener, PlayerInfoFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListFragmentInteraction(ScoreboardContent.ScoreItem item) {
        return;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new PlayerInfoFragment();
                case 1:
                    return new ScoreboardFragment();
                case 2:
                    return (new PrefsFragment());
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PlayerInfo";
                case 1:
                    return "ScoreBoard";
                case 2:
                    return "Settings";
            }
            return null;
        }
    }

    public static class PrefsFragment extends PreferenceFragmentCompat implements android.support.v7.preference.Preference.OnPreferenceClickListener {


        private static final String TAG = "PF";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.preferences, rootKey);
            android.support.v7.preference.Preference logout = findPreference("logout");
            logout.setOnPreferenceClickListener(this);

        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("logout")) {
                GrabbleApplication.getAppContext(getActivity().getApplicationContext()).logout();
                doRestart(getActivity().getApplicationContext());
            }
            return true;
        }
        public static void doRestart(Context c) {
            try {
                //check if the context is given
                if (c != null) {
                    //fetch the packagemanager so we can get the default launch activity
                    // (you can replace this intent with any other activity if you want
                    PackageManager pm = c.getPackageManager();
                    //check if we got the PackageManager
                    if (pm != null) {
                        //create the intent with the default start activity for your application
                        Intent mStartActivity = pm.getLaunchIntentForPackage(
                                c.getPackageName()
                        );
                        if (mStartActivity != null) {
                            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //create a pending intent so the application is restarted after System.exit(0) was called.
                            // We use an AlarmManager to call this intent in 100ms
                            int mPendingIntentId = 223344;
                            PendingIntent mPendingIntent = PendingIntent
                                    .getActivity(c, mPendingIntentId, mStartActivity,
                                            PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            //kill the application
                            System.exit(0);
                        } else {
                            Log.e(TAG, "Was not able to restart application, mStartActivity null");
                        }
                    } else {
                        Log.e(TAG, "Was not able to restart application, PM null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, Context null");
                }
            } catch (Exception ex) {
                Log.e(TAG, "Was not able to restart application");
            }
        }

    }
}
