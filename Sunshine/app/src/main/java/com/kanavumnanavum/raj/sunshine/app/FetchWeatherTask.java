package com.kanavumnanavum.raj.sunshine.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.kanavumnanavum.raj.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created by raj on 10/1/14.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]>
{
    private final  String LOG_TAG =FetchWeatherTask.class.getSimpleName();
    private ArrayAdapter<String> mForeCastAdapter;
    private Context mContext;
    public FetchWeatherTask(Context context)
    {
        mContext = context;
    }

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
            final String DAYS="&cnt=14";
            final int DAYS_VALUE=14;

            StringBuilder sb =new StringBuilder();
            sb.append(BASE_URL).append(MODE).append(UNITS).append(DAYS).append(QUERY).append(params[0]);

            Log.e(LOG_TAG,sb.toString());
            java.net.URL url = new URL(sb.toString());
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
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String str[])
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

    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays,
                                            String locationSetting)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        // Location information
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";
        final String OWM_COORD_LAT = "lat";
        final String OWM_COORD_LONG = "lon";
        // Weather information. Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";
        final String OWM_DATETIME = "dt";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";
        // All temperatures are children of the "temp" object.
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";

        JSONObject forecastJson = null;
        JSONArray weatherArray = null;
        JSONObject cityJson = null;
        String cityName = null;
        JSONObject coordJSON = null;
        double cityLatitude = 0.0;
        double cityLongitude =0.0;
        String[] resultStrs = new String[numDays];
        // Get and insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

        try {
            forecastJson = new JSONObject(forecastJsonStr);
            weatherArray = forecastJson.getJSONArray(OWM_LIST);
            cityJson = forecastJson.getJSONObject(OWM_CITY);
            cityName = cityJson.getString(OWM_CITY_NAME);
            coordJSON = cityJson.getJSONObject(OWM_COORD);
            cityLatitude = coordJSON.getLong(OWM_COORD_LAT);
            cityLongitude = coordJSON.getLong(OWM_COORD_LONG);

        // Insert the location into the database.
        // The function referenced here is not yet implemented, so we've commented it out for now.
         long locationID = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

        for(int i = 0; i < weatherArray.length(); i++)
        {
            long dateTime;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;
            double high;
            double low;
            String description;
            int weatherId;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long. We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            dateTime = dayForecast.getLong(OWM_DATETIME);

            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getInt(OWM_HUMIDITY);
            windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
            windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

            // Description is in a child array called "weather", which is 1 element long.
            // That element also contains a weather code.
            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);
            weatherId = weatherObject.getInt(OWM_WEATHER_ID);

            // Temperatures are in a child object called "temp". Try not to name variables
            // "temp" when working with temperature. It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);

            ContentValues weatherValues = new ContentValues();

            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationID);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                    WeatherContract.getDbDateString(new Date(dateTime * 1000L)));
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

            cVVector.add(weatherValues);

            String highAndLow = formatHighLows(high, low);
            String day = getReadableDateString(dateTime);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }
        }catch (JSONException exp)
        {
            Log.v(LOG_TAG, exp.getMessage(),exp);
        }

        return resultStrs;
    }

    private String formatHighLows(double high, double low)
    {
        boolean isMetric=Utility.isMetric(mContext);

        if(!isMetric)
        {
            high= (high *1.8 )+32;
            low= (low *1.8 )+32;
        }
        else if(isMetric)
        {
            Log.d(LOG_TAG,"Units type not found");
        }
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
   * so for convenience we're breaking it out into its own method now.
   */
    private String getReadableDateString(long time)
    {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSetting The location string used to request updates from the server.
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param lat the latitude of the city
     * @param lon the longitude of the city
     * @return the row ID of the added location.
     */
    private long addLocation(String locationSetting, String cityName, double lat, double lon)
    {
        Log.v(LOG_TAG, "inserting " + cityName + ", with coord: " + lat + ", " + lon);
        // First, check if the location with this city name exists in the db
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null);
        if (cursor.moveToFirst())
        {
            Log.v(LOG_TAG, "Found it in the database!");
            int locationIdIndex = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            return cursor.getLong(locationIdIndex);
        } else
        {
            Log.v(LOG_TAG, "Didn't find it in the database, inserting now!");
            ContentValues locationValues = new ContentValues();
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);
            Uri locationInsertUri = mContext.getContentResolver()
                    .insert(WeatherContract.LocationEntry.CONTENT_URI, locationValues);
            return ContentUris.parseId(locationInsertUri);
        }
    }
}
