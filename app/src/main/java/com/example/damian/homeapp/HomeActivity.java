package com.example.damian.homeapp;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damian.homeapp.dodatki.VerticalProgressBar;
import com.example.damian.homeapp.dodatki.VerticalSeekBar;

public class HomeActivity extends AppCompatActivity {

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

    static Context baseContext;
    static Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        window = getWindow();
        baseContext = getBaseContext();




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        TextView tempZadana;
        TextView tempAktualna;

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
            public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

                VerticalSeekBar seekBar = (VerticalSeekBar) rootView.findViewById(R.id.temp_seekBar);
                tempZadana = (TextView) rootView.findViewById(R.id.temp_zadana);
                seekBar.setMax(16);

                tempAktualna = (TextView) rootView.findViewById(R.id.temp_aktualna);

                final VerticalProgressBar progressBar = (VerticalProgressBar)
                        rootView.findViewById(R.id.temp_aktual);
                progressBar.setMax(16);

                final android.support.v7.widget.CardView cardView1 = (android.support.v7.widget.CardView)
                        rootView.findViewById(R.id.card_1);

                SeekBar lightSeekBar = (SeekBar) rootView.findViewById(R.id.light_seekBar);
                final TextView lightTextView = (TextView) rootView.findViewById(R.id.light_text);



                switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                    //ekran 1:
                    case 1:
                        seekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            int temperatura = progress + 16;
                            tempZadana.setText(temperatura + "°C");
                            progressBar.setProgress(progress+5);
                            tempAktualna.setText(temperatura+5 + "°C");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }

                    });
                        lightSeekBar.setOnSeekBarChangeListener(new
                                                                        SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress,
                                                          boolean fromUser) {
                                lightTextView.setText(progress + "%");
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                        return rootView;
                    //ekran 2:
                    case 2:
                        cardView1.setVisibility(View.GONE);
                        return rootView;
                    //ekran 3:
                    case 3:
                        return rootView;
                }
            return null;

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
            return PlaceholderFragment.newInstance(position + 1);
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
                    return "PARTER";
                case 1:
                    return "PIETRO 1";
                case 2:
                    return "PODDASZE";
            }
            return null;
        }
    }


    static Handler UIupdater = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int numOfBytesRecived = msg.arg1;
            byte[] buffer = (byte[]) msg.obj;
            String strReceived = new String(buffer);
            strReceived = strReceived.substring(0, numOfBytesRecived);

            System.out.println(strReceived);


        }
    };


}
