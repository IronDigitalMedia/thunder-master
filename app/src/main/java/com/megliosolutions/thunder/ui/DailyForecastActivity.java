package com.megliosolutions.thunder.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.megliosolutions.thunder.R;
import com.megliosolutions.thunder.adapters.DayAdapter;
import com.megliosolutions.thunder.weather.Day;
import com.megliosolutions.thunder.weather.Forecast;

import java.util.Arrays;

public class DailyForecastActivity extends ListActivity {

    private Day[] mDays;
    public Forecast mForecast;

    public RelativeLayout Rdaily;

    public static final String TAG = DailyForecastActivity.class.getSimpleName();

    public TextView CityState;
    public String City;
    public String State;
    public double Temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daily_forecast);

        Intent intent = getIntent();

        City = intent.getStringExtra("City");
        State = intent.getStringExtra("State");
        Temp = intent.getDoubleExtra("Temp",0);
        Log.d(TAG, "Daily Temp: " + Temp);
        Log.d(TAG, "Daily City: " + City);
        Log.d(TAG, "Daily State: " + State);

        Rdaily = (RelativeLayout)findViewById(R.id.relative_daily);
        CityState = (TextView)findViewById(R.id.location_CityState);
        String String_CityState = (City + ", " + State);
        CityState.setText(String_CityState);


        Parcelable[] parcelables  = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);
        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);
        ChangeBG();

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfTheWeek = mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getTemperatureMax() + "";
        String message = String.format("On %s the high will be %s and it will be %s",
                                dayOfTheWeek,
                                highTemp,
                                conditions);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();


    }

    public void ChangeBG(){

        //Current current = mForecast.getCurrent();
        //double temp = current.getTemperature();

        //Show current temp brought over
        //by intent, testing the temperature for accuracy
        //Toast.makeText(getApplicationContext(),
         //       Temp+"",Toast.LENGTH_LONG).show();

        if(Temp <= 45 && Temp > 0){
            Rdaily.setBackgroundResource(R.drawable.bg_reallycold);
            Log.i(TAG, "BG: REALLY COLD!");
        }
        else if(Temp >= 46 && Temp <= 60){
            Rdaily.setBackgroundResource(R.drawable.bg_cold);
            Log.i(TAG, "BG: COLD");
        }
        else if(Temp >= 61 && Temp <= 75){
            Rdaily.setBackgroundResource(R.drawable.bg_warm);
            Log.i(TAG, "BG: WARM");
        }
        else if(Temp >= 76 && Temp <= 99){
            Rdaily.setBackgroundResource(R.drawable.bg_reallywarm);
            Log.i(TAG, "BG: REALLY WARM!");
        }

    }
}
