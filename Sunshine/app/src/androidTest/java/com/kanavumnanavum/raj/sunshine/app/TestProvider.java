package com.kanavumnanavum.raj.sunshine.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.kanavumnanavum.raj.sunshine.data.WeatherContract;
import com.kanavumnanavum.raj.sunshine.data.WeatherDbHelper;

import java.util.Iterator;

/**
 * Created by raj on 9/28/14.
 */
public class TestProvider extends AndroidTestCase
{
    public static String LOG_TAG=    TestProvider.class.getSimpleName();

    public void testDeleteDb() throws Throwable
    {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void testGetType()
    {
         // content://com.example.android.sunshine.app/weather/
         String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
         // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
         assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

         String testLocation = "94074";
         // content://com.example.android.sunshine.app/weather/94074
         type = mContext.getContentResolver().getType(
                 WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
         // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
         assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

         String testDate = "20140612";
         // content://com.example.android.sunshine.app/weather/94074/20140612
         type = mContext.getContentResolver().getType(
                 WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
         // vnd.android.cursor.item/com.example.android.sunshine.app/weather
         assertEquals(WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.CONTENT_URI);
         // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(WeatherContract.LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.buildLocationUri(1L));
         // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(WeatherContract.LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testUpdateLocation() {
        // Create a new map of values, where column names are the keys
        ContentValues values = getLocationValues();

        Uri locationUri = mContext.getContentResolver().
                insert(WeatherContract.LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(WeatherContract.LocationEntry._ID, locationRowId);
        updatedValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int count = mContext.getContentResolver().update(
                WeatherContract.LocationEntry.CONTENT_URI, updatedValues, WeatherContract.LocationEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        if(cursor.moveToFirst()) {
            ContentValues readValues = new ContentValues();
            cursorRowToContentValues(cursor, readValues);
            assertEquals(readValues.size(), 4);
        }
    }

    public void te1stInsertReadProivder()
    {
        ContentValues cvs=getLocationValues();
        //long rowId= db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,cvs);

        Uri locationUri = mContext.getContentResolver()
                .insert(WeatherContract.LocationEntry.CONTENT_URI, cvs);
        assertTrue(locationUri != null);

        long rowId = ContentUris.parseId(locationUri);

        assertTrue(rowId!=-1);
        Log.d(LOG_TAG,"New Row Id:"+rowId);

        ContentValues readValues=null;
        //Cursor cursor=  db.query(WeatherContract.LocationEntry.TABLE_NAME,cvs.keySet().toArray(new String[cvs.size()]),null,null,null,null,null);
        Cursor cursor = mContext.getContentResolver().query(WeatherContract.LocationEntry.CONTENT_URI,null,null,null,null);

        if(cursor.moveToFirst())
        {
            readValues = new ContentValues();
            cursorRowToContentValues(cursor, readValues);
            validateCursor(readValues, cvs);
        }
        else
        {
            fail("No values returned");
        }
        cursor.close();
        cvs =getWeatherValues((int)rowId);
        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(WeatherContract.WeatherEntry.CONTENT_URI, cvs);
        long weatherRowId = ContentUris.parseId(weatherInsertUri);
        //long weatherRowId= db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,cvs);

        assertTrue(weatherRowId!=-1);
        Log.d(LOG_TAG,"New Row Id:"+weatherRowId);

        //cursor=  db.query(WeatherContract.WeatherEntry.TABLE_NAME,cvs.keySet().toArray(new String[cvs.size()]),null,null,null,null,null);
        cursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI,null,null,null,null);
        //cursor = mContext.getContentResolver().query(WeatherContract.LocationEntry.buildLocationUri(weatherRowId),null,null,null,null);

        if(cursor.moveToFirst())
        {
            readValues = new ContentValues();
            cursorRowToContentValues(cursor, readValues);
            validateCursor(readValues, cvs);
        }
        else
        {
            fail("No values returned");
        }
        cursor.close();
    }

    public static void cursorRowToContentValues(Cursor cursor, ContentValues values)
    {
        AbstractWindowedCursor awc =
                (cursor instanceof AbstractWindowedCursor) ? (AbstractWindowedCursor) cursor : null;

        String[] columns = cursor.getColumnNames();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            if (awc != null && awc.isBlob(i)) {
                values.put(columns[i], cursor.getBlob(i));
            }
            else if(awc!=null && awc.getType(i)==Cursor.FIELD_TYPE_INTEGER)
            {
                values.put(columns[i], cursor.getInt(i));
            }
            else if(awc!=null && awc.getType(i)==Cursor.FIELD_TYPE_FLOAT)
            {
                values.put(columns[i], cursor.getDouble(i));
            }
            else {
                values.put(columns[i], cursor.getString(i));
            }
        }
    }
    private ContentValues getLocationValues()
    {
        ContentValues cvs = new ContentValues();
        cvs.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,"New Jersey");
        cvs.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,"08536");
        cvs.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,40.5);
        cvs.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,74.5);
        return cvs;
    }

    private ContentValues getWeatherValues(int weatherRowId)
    {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, weatherRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75.0);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65.0);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

    public void validateCursor(ContentValues readvalues, ContentValues intialValues)
    {
        Iterator<String> iterator= readvalues.keySet().iterator();
        String key;
        while(iterator.hasNext())
        {
            key=iterator.next();
            Object read=readvalues.get(key);
            Object initial =intialValues.get(key);
            if(read!=null & initial!=null)
              assertEquals(read.toString(), initial.toString());
        }
    }

    // brings our database to an empty state
    public void te1stDeleteAllRecords()
    {
        mContext.getContentResolver().delete(
                WeatherContract.WeatherEntry.CONTENT_URI,
                "1",
                null
        );

        mContext.getContentResolver().delete(
                WeatherContract.LocationEntry.CONTENT_URI,
                "1",
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract. WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }
    // Since we want each test to start with a clean slate, run deleteAllRecords
// in setUp (called by the test runner before each test).


}
