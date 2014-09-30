package com.kanavumnanavum.raj.sunshine.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.kanavumnanavum.raj.sunshine.data.WeatherContract;
import com.kanavumnanavum.raj.sunshine.data.WeatherDbHelper;

import java.util.Iterator;

/**
 * Created by raj on 9/28/14.
 */
public class TestDb extends AndroidTestCase
{
    public static String LOG_TAG=    TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable
    {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext,null,null,1).getWritableDatabase();
        assertEquals(true,db.isOpen());
        db.close();
    }

    public void testInsertReadDb()
    {
        SQLiteDatabase db = new WeatherDbHelper(this.mContext,null,null,1).getWritableDatabase();


        ContentValues cvs=getLocationValues();
        long rowId= db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,cvs);

        assertTrue(rowId!=-1);
        Log.d(LOG_TAG,"New Row Id:"+rowId);

        ContentValues readValues = new ContentValues();
        Cursor cursor=  db.query(WeatherContract.LocationEntry.TABLE_NAME,cvs.keySet().toArray(new String[cvs.size()]),null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            DatabaseUtils.cursorRowToContentValues(cursor, readValues);
            validateCursor(readValues, cvs);
        }
        else
        {
            fail("No values returned");
        }

        cvs =getWeatherValues(rowId);
        long weatherRowId= db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,cvs);

        assertTrue(weatherRowId!=-1);
        Log.d(LOG_TAG,"New Row Id:"+weatherRowId);

        readValues = new ContentValues();
        cursor=  db.query(WeatherContract.LocationEntry.TABLE_NAME,cvs.keySet().toArray(new String[cvs.size()]),null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            DatabaseUtils.cursorRowToContentValues(cursor, readValues);
            validateCursor(readValues, cvs);
        }
        else
        {
            fail("No values returned");
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

    private ContentValues getWeatherValues(long weatherRowId)
    {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, weatherRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
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
            assertEquals(readvalues.get(key),intialValues.get(key));
        }
    }

}
