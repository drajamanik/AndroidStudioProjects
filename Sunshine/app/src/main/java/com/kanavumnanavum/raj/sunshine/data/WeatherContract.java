package com.kanavumnanavum.raj.sunshine.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Created by raj on 9/28/14.
 */
public class WeatherContract
{
    public static final String CONTENT_AUTHORITY = "com.kanavumnanavum.raj.sunshine.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";

    public static final String PATH_LOCATION = "location";

    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Converts unix time to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText)
    {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try
        {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e )
        {
            e.printStackTrace();
            return null;
        }
    }

    public static final class WeatherEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";

        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min_temp";
        public static final String COLUMN_MAX_TEMP = "max_temp";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity_temp";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed mph
        public static final String COLUMN_WIND_SPEED = "wind_speed";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south). Stored as floats.
        public static final String COLUMN_DEGREES = "degrees_temp";

        public static Uri buildWeatherUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting)
        {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate)
        {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, String date)
        {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri)
        {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri)
        {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }
    }

    public static final class LocationEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Table name
        public static final String TABLE_NAME = "location";
        // The location setting string is what will be sent to openweatherma
        // as the location query.
        public static final String COLUMN_LOCATION_SETTING = "location_setting";
        // Human readable location string, provided by the API. Because for styling,
        // "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_CITY_NAME = "city_name";
        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
