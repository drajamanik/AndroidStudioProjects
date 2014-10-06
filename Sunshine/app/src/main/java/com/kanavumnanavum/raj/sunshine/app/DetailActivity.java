package com.kanavumnanavum.raj.sunshine.app;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kanavumnanavum.raj.sunshine.data.WeatherContract;

public class DetailActivity extends ActionBarActivity
{
    public static final String DATE_KEY = "forecast_date";
    private static final String LOCATION_KEY = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastDetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            Intent settingActivityIntent= new Intent(this,SettingsActivity.class);
            settingActivityIntent.putExtra(Intent.EXTRA_TEXT,"Setting working for Tamil keyboard.");
            startActivity(settingActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ForecastDetailFragment extends Fragment implements  android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
    {

        private static final int DETAIL_LOADER = 0;
        private static final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        };

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            Log.v(LOG_TAG, "in onCreateOptionsMenu");
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);
            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);
            // Get the provider and hold onto it to set/change the share intent.
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            // If onLoadFinished happens before this, we can go ahead and set the share intent now.
            if (mForecast != null)
            {
                mShareActionProvider.setShareIntent(createShareForeCastIntent());
            }
        }
        private static final String LOG_TAG=ForecastDetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG=" #SunshineApp";
        private String mForeCastStr;

        public static final String DATE_KEY = "forecast_date";
        private ShareActionProvider mShareActionProvider;
        private String mLocation;
        private String mForecast;

        public ForecastDetailFragment()
        {
            setHasOptionsMenu(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            getLoaderManager().initLoader(DETAIL_LOADER,null,this);
            if (savedInstanceState != null) {
                mLocation = savedInstanceState.getString(LOCATION_KEY);
            }
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putString(LOCATION_KEY, mLocation);
            super.onSaveInstanceState(outState);
        }
        @Override
        public void onResume() {
            super.onResume();
            if (mLocation != null &&
                    !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT))
            {
                mForeCastStr= intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView)rootView.findViewById(R.id.detail_forecast_textview)).setText(mForeCastStr);
            }
            return rootView;
        }



        private Intent createShareForeCastIntent()
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,mForeCastStr+FORECAST_SHARE_HASHTAG);
            return intent;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
        {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null || !intent.hasExtra(DATE_KEY)) {
                return null;
            }
            String forecastDate = intent.getStringExtra(DATE_KEY);
            // Sort order: Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";
            mLocation = Utility.getPreferredLocation(getActivity());
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    mLocation, forecastDate);
            Log.v(LOG_TAG, weatherForLocationUri.toString());
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data)
        {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) { return; }

            String dateString = Utility.formatDate(
                    data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)));

            ((TextView) getView().findViewById(R.id.detail_date_textview))
                    .setText(dateString);
            String weatherDescription =
                    data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));

            ((TextView) getView().findViewById(R.id.detail_forecast_textview))
                    .setText(weatherDescription);

            boolean isMetric = Utility.isMetric(getActivity());
             String high = Utility.formatTemperature(
                    data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
            ((TextView) getView().findViewById(R.id.detail_high_textview)).setText(high);

            String low = Utility.formatTemperature(
                    data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), isMetric);
            ((TextView) getView().findViewById(R.id.detail_low_textview)).setText(low);

            // We still need this for the share intent
            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
            Log.v(LOG_TAG, "Forecast String: " + mForecast);
            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForeCastIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader)
        {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
