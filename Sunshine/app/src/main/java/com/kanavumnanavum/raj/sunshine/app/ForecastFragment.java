package com.kanavumnanavum.raj.sunshine.app;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raj on 9/17/14.
 */
/**
* A placeholder fragment containing a simple view.
*/
public class ForecastFragment extends Fragment
{
    private final  String LOG_TAG =ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> mForeCastAdapter;
    private  String URL="http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&units=metric&cnt=7&q=94043&";
    public ForecastFragment()
    {
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
        FetchWeatherTask fetchWeatherTask =new FetchWeatherTask();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultLocation = getResources().getString(R.string.pref_location_key);
        String returnResults=null;
        fetchWeatherTask.execute(defaultLocation, null, returnResults);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        List<String> weekForecast = new ArrayList<String>();

        mForeCastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textView,
                weekForecast);

        ListView listView = (ListView) rootView.findViewById(R.id.list_item_forecast_textView);
        listView.setAdapter(mForeCastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext().getApplicationContext();
                String text=mForeCastAdapter.getItem(position);
                //Toast.makeText(context, mForeCastAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                Intent detailActivityIntent= new Intent(getActivity(),DetailActivity.class);
                detailActivityIntent.putExtra(Intent.EXTRA_TEXT,text);
                startActivity(detailActivityIntent);
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

    public class FetchWeatherTask extends AsyncTask<String,Void,String[]>
    {
        private final  String LOG_TAG =FetchWeatherTask.class.getSimpleName();
        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY="&q=";
                final String QUERY_VALUE="&q=";
                final String MODE="&mode=json";
                final String UNITS ="&units=metric";
                final String DAYS="&cnt=7";
                final int DAYS_VALUE=7;

                StringBuilder sb =new StringBuilder();
                sb.append(BASE_URL).append(MODE).append(UNITS).append(DAYS).append(QUERY).append(params[0]);

                Log.e(LOG_TAG,sb.toString());
                URL url = new URL(sb.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                WeatherDataProcessor wdp =new WeatherDataProcessor();

                Log.e (LOG_TAG,"JSON Obj:"+forecastJsonStr );
                try {
                    return wdp.getWeatherDataFromJson(forecastJsonStr,DAYS_VALUE );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return new String[]{};
        }

        public FetchWeatherTask() {
            super();
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] str)
        {
            if(str!=null)
            {
                mForeCastAdapter.clear();
                mForeCastAdapter.addAll(str);
            }
            super.onPostExecute(str);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String[] str) {
            super.onCancelled(str);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}