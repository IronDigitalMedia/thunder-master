package com.megliosolutions.thunder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.megliosolutions.thunder.R;
import com.megliosolutions.thunder.adapters.HourAdapter;
import com.megliosolutions.thunder.weather.Hour;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends AppCompatActivity {

    private Hour[] mHours;
    public static final String TAG = HourlyForecastActivity.class.getSimpleName();

    public RelativeLayout Rhourly;
    public double Temp;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Temp = intent.getDoubleExtra("Temp",0);
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        HourAdapter adapter = new HourAdapter(this, mHours);
        mRecyclerView.setAdapter(adapter);

        Rhourly = (RelativeLayout)findViewById(R.id.relative_hourly);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
        ChangeBG();

    }


    public void ChangeBG(){
        //Toast.makeText(getApplicationContext(), Temp + "", Toast.LENGTH_LONG).show();
        if(Temp <= 45 && Temp > 0){
            Rhourly.setBackgroundResource(R.drawable.bg_reallycold);
            Log.i(TAG, "BG: REALLY COLD!");
        }
        else if(Temp >= 46 && Temp <= 60){
            Rhourly.setBackgroundResource(R.drawable.bg_cold);
            Log.i(TAG, "BG: COLD");
        }
        else if(Temp >= 61 && Temp <= 75){
            Rhourly.setBackgroundResource(R.drawable.bg_warm);
            Log.i(TAG, "BG: WARM");
        }
        else if(Temp >= 76 && Temp <= 99){
            Rhourly.setBackgroundResource(R.drawable.bg_reallywarm);
            Log.i(TAG, "BG: REALLY WARM!");
        }

    }


}



