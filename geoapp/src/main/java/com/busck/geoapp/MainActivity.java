package com.busck.geoapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity implements ActionBar.TabListener {

    private static Context mMainActivity;
    private ArrayList <Geofence> mGeofenceList;
    private HashMap<String, Circle> mCircleDatas;
    private static final float GEOFENCE_RADIUS = 25.0f;
    private ArrayAdapter<Geofence> mListAdapter;
    public GoogleMap mMap;
    private LocationClient mLocationClient;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private String TAG = "GEOAPP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        mMainActivity = this;
        mGeofenceList = new ArrayList <Geofence>();
        mCircleDatas = new HashMap<String, Circle>();


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

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

            if(position == 0){
                //return PlaceholderFragment.newInstance(R.layout.fragment_map);
                return new FragmentMap();
            } else if (position == 1){
                //return PlaceholderFragment.newInstance(R.layout.fragment_list);
                return new FragmentList();
            }else{
                return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);

                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(sectionNumber, container, false);

            return rootView;
        }
    }

    public class FragmentMap extends Fragment implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<Cursor> {



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View mapView = inflater.inflate(R.layout.fragment_map, container, false);

            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setOnMapLongClickListener(this);
            mMap.setOnMapClickListener(this);
            mMap.setMyLocationEnabled(true);

            mLocationClient = new LocationClient(mMainActivity, this, this);

            return mapView;

        }


        @Override
        public void onResume() {
            super.onResume();
            mLocationClient.connect();

        }



        @Override
        public void onPause() {
            super.onPause();
            mCircleDatas.clear();
            mGeofenceList.clear();
            mListAdapter.notifyDataSetChanged();
            mLocationClient.disconnect();
        }



        @Override
        public void onMapLongClick(LatLng latLng) {
            //String requestId = Long.toString(SystemClock.elapsedRealtime());
            //addGeofenceAndCircles(latLng, requestId);
            String requestId = insertDataInDatabase(latLng);
            //addGeofenceAndCircles(latLng, requestId);
        }

        private void addGeofenceAndCircles(LatLng latLng, String requestId) {

            Geofence.Builder builder = new Geofence.Builder();
            Geofence geofence = builder
                    .setRequestId(requestId)
                    .setCircularRegion(latLng.latitude,
                            latLng.longitude, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(1000 * 60 * 10)
                    .build();

            mGeofenceList.add(geofence);

            Intent intent =
                    new Intent(GeofenceReciver.ACTION_GEOFENCE_RECIVED);
            PendingIntent pendingIntent
                    = PendingIntent
                    .getBroadcast(mMainActivity, 0, intent, 0);

            mLocationClient.addGeofences(mGeofenceList, pendingIntent,
                    new LocationClient.OnAddGeofencesResultListener() {
                        @Override
                        public void onAddGeofencesResult(int i, String[] strings) {
                            Log.e(TAG, "Geofences added!");
                        }
                    });

            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng).radius(GEOFENCE_RADIUS).fillColor(0x55F70010).strokeColor(android.R.color.transparent);
            Circle circle = mMap.addCircle(circleOptions);
            mCircleDatas.put(requestId, circle);
            mListAdapter.notifyDataSetChanged();
        }


        private String insertDataInDatabase(LatLng latLng) {
            ContentValues values = new ContentValues();
            values.put(DataStore.Contract.LATITUDE, latLng.latitude);
            values.put(DataStore.Contract.LONGITUDE, latLng.longitude);
            return getContentResolver()
                    .insert(DataStore.Contract.GEOFENCES, values)
                    .getLastPathSegment();
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this, DataStore.Contract.GEOFENCES,
                    new String[]{
                            DataStore.Contract.ID,
                            DataStore.Contract.LATITUDE,
                            DataStore.Contract.LONGITUDE},
                    null, null, "");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mCircleDatas.clear();
            mGeofenceList.clear();
            mListAdapter.notifyDataSetChanged();
            mMap.clear();

            while (cursor != null && cursor.moveToNext()) {
                String requestId = cursor
                        .getString(cursor
                                .getColumnIndex(DataStore.Contract.ID));
                float latitude = cursor
                        .getFloat(cursor
                                .getColumnIndex(DataStore.Contract.LATITUDE));
                float longitude = cursor
                        .getFloat(cursor
                                .getColumnIndex(DataStore.Contract.LONGITUDE));
                addGeofenceAndCircles(new LatLng(latitude, longitude), requestId);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        @Override
        public void onMapClick(LatLng latLng) {

        }

        @Override
        public void onConnected(Bundle bundle) {
            getLoaderManager().restartLoader(0, null, this);
        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    }

    public class FragmentList extends ListFragment implements LocationClient.OnRemoveGeofencesResultListener,  LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            mListAdapter = new ArrayAdapter<Geofence>(inflater.getContext(), android.R.layout.simple_list_item_1, mGeofenceList);
            setListAdapter(mListAdapter);

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            String requestId = mGeofenceList.get(position).getRequestId();
            removeGeofence(mGeofenceList.get(position));
            deleteDataFromDatabase(requestId);
            mGeofenceList.remove(position);
            mListAdapter.notifyDataSetChanged();
            Circle circle = mCircleDatas.get(requestId);
            circle.remove();
            mCircleDatas.remove(requestId);

            Log.d(TAG, "mCircleDatas " + Integer.toString(mCircleDatas.size()));


            Log.d(TAG, Long.toString(position));

        }

        public void deleteDataFromDatabase(String requestId){
            Uri geofenceUri = Uri.withAppendedPath(DataStore.Contract.GEOFENCES,
                    requestId);
            int numberOfRowsChanged = getContentResolver().delete(geofenceUri, null, null);
            Log.d(TAG, "numberOfRowsChanged" + Integer.toString(numberOfRowsChanged));
            getLoaderManager().restartLoader(0, null, this);
        }

        public void removeGeofence(Geofence geofence){
            String requestId = geofence.getRequestId();
            ArrayList<String> geofenceToRemove = new ArrayList<String>();
            geofenceToRemove.add(requestId);
            mLocationClient.removeGeofences(geofenceToRemove, this);
        }

        @Override
        public void onRemoveGeofencesByRequestIdsResult(int i, String[] strings) {

        }

        @Override
        public void onRemoveGeofencesByPendingIntentResult(int i, PendingIntent pendingIntent) {

        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this, DataStore.Contract.GEOFENCES,
                    new String[]{
                            DataStore.Contract.ID,
                            DataStore.Contract.LATITUDE,
                            DataStore.Contract.LONGITUDE},
                    null, null, "");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }


}
