package com.kanavumnanavum.raj.sunshine.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    final String LOG_TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG,"OnCreate.");
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG_TAG,"onStart.");
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_TAG,"onResume.");
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(LOG_TAG,"onPause.");
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.e(LOG_TAG,"onStop.");
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG,"onDestroy.");
        // The activity is about to be destroyed.
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
            Intent settingActivityIntent= new Intent(this,SettingsActivity.class);
            settingActivityIntent.putExtra(Intent.EXTRA_TEXT,"Setting working for Tamil keyboard.");
            startActivity(settingActivityIntent);
            return true;
        }
        if(id == R.id.action_map)
        {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap()
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String location=sharedPreferences.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",location).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }
        else
        {
            Log.d(LOG_TAG,"Could not call "+location+",no ");
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ForcastFragment extends Fragment
    {

        private ArrayAdapter<String> mForeCastAdapter;
        public ForcastFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            List<String> weekForecast = new ArrayList<String>();
            weekForecast.add("Today Sunny - 83/63");
            weekForecast.add("Tomorrow Foggy - 76/60");
            weekForecast.add("Weds Cloudy - 73/53");
            weekForecast.add("Thurs Rainy - 70/52");
            weekForecast.add("Fri Thunderstrom - 80/70");
            weekForecast.add("Saturday Sunny - 90/73");

            mForeCastAdapter = new ArrayAdapter<String>(
            getActivity(),
            R.layout.list_item_forecast,
            R.id.list_item_forecast_textView,
                    weekForecast);

            ListView    listView = (ListView) rootView.findViewById(R.id.list_item_forecast_textView);
            listView.setAdapter(mForeCastAdapter);

            return rootView;
        }
    }
}
