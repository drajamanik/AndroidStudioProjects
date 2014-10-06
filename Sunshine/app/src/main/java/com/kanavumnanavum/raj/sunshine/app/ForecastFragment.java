package com.kanavumnanavum.raj.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kanavumnanavum.raj.sunshine.data.WeatherContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raj on 9/17/14.
 */
/**
* A placeholder fragment containing a simple view.
*/
public class ForecastFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
{
    private final  String LOG_TAG =ForecastFragment.class.getSimpleName();
    private String mLocation;
    private static final int FORECAST_LOADER=0;

    private ForecastAdapter mForeCastAdapter;

    private static final String[] FORECAST_COLUMNS=
            {
                    WeatherContract.WeatherEntry.TABLE_NAME+"." + WeatherContract.WeatherEntry._ID,
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
            };

    public static final  int COL_WEATHER_ID=0;
    public static final  int COL_WEATHER_DATE=1;
    public static final  int COL_WEATHER_DESC=2;
    public static final  int COL_WEATHER_MAX_TEMP=3;
    public static final  int COL_WEATHER_MIN_TEMP=4;
    public static final  int COL_LOCATION_SETTINGS=5;

    private  String URL="http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&units=metric&cnt=7&q=94043&";
    public ForecastFragment()
    {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        this.getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater)
    {
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if( id ==R.id.action_refresh)
        {
            updateWeather();
        }
        if (id == R.id.action_settings)
        {
            //Intent settingActivityIntent= new Intent(getActivity(),SettingsActivity.class);
            //settingActivityIntent.putExtra(Intent.EXTRA_TEXT,"Setting working for Tamil keyboard.");
            //startActivity(settingActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather()
    {
        FetchWeatherTask fetchWeatherTask =new FetchWeatherTask(getActivity());
        String defaultLocation =Utility.getPreferredLocation(getActivity());
        String returnResults=null;
        fetchWeatherTask.execute(defaultLocation, null, returnResults);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(mLocation!=null && !Utility.getPreferredLocation(getActivity()).equals(mLocation))
        {
            this.getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        List<String> weekForecast = new ArrayList<String>();

        mForeCastAdapter = new ForecastAdapter(getActivity(),null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.list_item_forecast_textView);
        listView.setAdapter(mForeCastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ForecastAdapter simpleCursorAdapter =(ForecastAdapter )parent.getAdapter();
                Cursor cursor= simpleCursorAdapter.getCursor();

                if (cursor != null && cursor.moveToPosition(position))
                {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(DetailActivity.DATE_KEY, cursor.getString(COL_WEATHER_DATE));
                    startActivity(intent);
                }
            }
        });


        return rootView;
    }

    private String formatHighLows(double high, double low)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType =sharedPreferences.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_metric));

        if(unitType.equals(getString(R.string.pref_units_imperial)))
        {
            high= (high *1.8 )+32;
            low= (low *1.8 )+32;
        }
        else if(!unitType.equals(getString(R.string.pref_units_imperial)))
        {
            Log.d(LOG_TAG,"Units type not found"+unitType);
        }
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    public  <Cursor> android.support.v4.content.Loader<Cursor> initLoader(int i, android.os.Bundle bundle, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks)
    {
        return null;
    }

    public <Cursor> android.support.v4.content.Loader<Cursor> restartLoader(int i, android.os.Bundle bundle, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks)
    {
        return null;
    }

    public void destroyLoader(int i)
    {

    }

    public <Cursor> android.support.v4.content.Loader<Cursor> getLoader(int i)
    {
        return null;
    }

    public void dump(java.lang.String s, java.io.FileDescriptor fileDescriptor, java.io.PrintWriter printWriter, java.lang.String[] strings)
    {

    }
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, android.os.Bundle bundle)
    {
        // This is called when a new Loader needs to be created. This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new java.util.Date());

        // Sort order: Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new android.support.v4.content.CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor d)
    {
        mForeCastAdapter.swapCursor(d);
    }
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader)
    {
        mForeCastAdapter.swapCursor(null);
    }

}