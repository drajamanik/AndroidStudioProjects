package com.kanavumnanavum.raj.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by raj on 9/28/14.
 */
public class WeatherProvider extends ContentProvider
{
    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;

    // The URI Matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WeatherDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static
    {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
    }

    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";

    private static final String sLocationSettingAndDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ? ";

    private static UriMatcher buildUriMatcher()
    {
        // I know what you're thinking. Why create a UriMatcher when you can use regular
        // expressions instead? Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found. The code passed into the constructor represents the code to return for the root
        // URI. It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);

        return matcher;
    }

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new WeatherDbHelper(this.getContext(),null,null,1);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case WEATHER_WITH_LOCATION_AND_DATE:
            {
                retCursor = getWeatherByLocationSettingAndDate(uri,projection,sortOrder);
                break;
            }
            // "weather/*"
            case WEATHER_WITH_LOCATION:
            {
                retCursor = getWeatherByLocationSetting(uri,projection,sortOrder);
                break;
            }
            // "weather"
            case WEATHER:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location/*"
            case LOCATION_ID:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID +" = '" + ContentUris.parseId(uri)+ "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "location"
            case LOCATION:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder)
    {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, startDate};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder)
    {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, date},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri)
    {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match)
        {
            // "weather"
            case WEATHER:
            {
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,values);
                if(_id>0)
                    returnUri= WeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new SQLException("Failed to Insert:"+uri);
                break;
            }
            // "location"
            case LOCATION:
            {
                long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,values);
                if(_id>0)
                    returnUri= WeatherContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new SQLException("Failed to Insert:"+uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int id;
        switch (match)
        {
            // "weather"
            case WEATHER:
            {
                int _id = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                if(_id>0)
                    id=_id;
                else
                    throw new SQLException("Failed to delete:"+uri);
                break;
            }
            // "location"
            case LOCATION:
            {
                int _id = db.delete(WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                if(_id>0)
                    id=_id;
                else
                    throw new SQLException("Failed to delete:"+uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(selection==null || id!= 0)
            getContext().getContentResolver().notifyChange(uri,null);

        return id;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int id;
        switch (match) {
            // "weather"
            case WEATHER:
            {
                db.beginTransaction();
                int count=0;
                try
                {
                    for(ContentValues cv:values)
                    {
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,cv);
                        if(_id!=-1)
                            count++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return count;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int id;
        switch (match)
        {
            // "weather"
            case WEATHER:
            {
                int _id = db.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                if(_id>0)
                    id=_id;
                else
                    throw new SQLException("Failed to update:"+uri);
                break;
            }
            // "location"
            case LOCATION:
            {
                int _id = db.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                if(_id>0)
                    id=_id;
                else
                    throw new SQLException("Failed to update:"+uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(id!= 0)
            getContext().getContentResolver().notifyChange(uri,null);

        return id;
    }
}
