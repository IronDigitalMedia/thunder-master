package com.megliosolutions.thunder.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.megliosolutions.thunder.R;
import com.megliosolutions.thunder.ui.AlertDialogs.AlertDialogFragment;
import com.megliosolutions.thunder.ui.AlertDialogs.ChangeZipError;
import com.megliosolutions.thunder.ui.AlertDialogs.NoNetworkAlertDialogFragment;
import com.megliosolutions.thunder.weather.Current;
import com.megliosolutions.thunder.weather.Day;
import com.megliosolutions.thunder.weather.Forecast;
import com.megliosolutions.thunder.weather.Hour;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";

    private Forecast mForecast;
    public Context mContext;
    public Locale mLocale;
    public double lat = 33.3222865;
    public double lon = -111.9061562;
    public String ChangeZip; //ChangeZip String
    public String City = "Chandler"; //City from geocoding from zip
    public String State = "Arizona"; //State from geocoding from zip
    public double Temp = 0.0;


    public RelativeLayout Rmain;

    public double mTemp;



    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.location_CityState) TextView mCityState;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.ET_zipcode) EditText mEditText;
    @BindView(R.id.button_changezip) Button mButtonChange;
    @BindView(R.id.dailybutton) Button mDailyButton;
    @BindView(R.id.swipe_refresh_main) SwipeRefreshLayout mSwipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mLocale = Locale.getDefault();
        Rmain = (RelativeLayout)findViewById(R.id.relative_main);

        LoadData();

        // swipe refresh layout
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getForecast();
                mSwipe.setRefreshing(false);
            }
        });

        mButtonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateLocation();
                ClearText();
                hideKeyboardFrom();
            }
        });


        Log.d(TAG, "MAIN UI code is running!");

    }

    public void LoadData(){
        mSwipe.setRefreshing(true);
        getForecast();
        mSwipe.setRefreshing(false);
    }


    public void ClearText(){
        mEditText.setText("");
    }

    public void hideKeyboardFrom() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /*This method gets the information using an API Key
    OkHttpClient allows for us to connect to a network and grab info
    from api.forcast.io
    checks to see if the network is available first
    then makes a request for the information
    then makes the call to the url to get the information from the jsonData object
    Checks if it fails, then if it responds it does certain methods
    */
    private void getForecast() {


        String APIkey = "baab1393443251119f2e1e8fdf55f46d";
        String forecastUrl = "https://api.forecast.io/forecast/" + APIkey +
                "/" + lat + "," + lon;

        if (isNetworkAvailable()) {
            //toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {

                            mForecast = parseForcastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });


                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception caught: ", e);
                    }
                }
            });
        }
        else {
            alertUserNoNetwork();
        }
    }

    //Updates the display with the new information
    private void updateDisplay() {
        Current current = mForecast.getCurrent();

        mTemperatureLabel.setText(current.getTemperature() + "");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mTemp = current.getTemperature();
        Log.d(TAG, "CURRENT TEMP: " + mTemp);
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipChance() + "%");
        mSummaryLabel.setText(current.getSummary());
        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
        mCityState.setText(City + ", " + State);
        ChangeBG();
    }
    //Gathers parsed date
    private Forecast parseForcastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }
    //Gets info from Dialy Class
    private Day[] getDailyForecast(String jsonData)  throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for(int i = 0; i < data.length(); i++){
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            days[i] = day;


        }

        return days;
    }
    //Gets info from Hourly Class
    private Hour[] getHourlyForecast(String jsonData)  throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        for(int i = 0; i < data.length(); i++){
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;



        }
        return hours;
    }
    //Fills variables with data to then set to the layout items.
    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);


        //Logs
        Log.d(TAG, "TEST: " + current.getTimeZone());
        Log.d(TAG, "TEST: " + current.getFormattedTime());
        Log.d(TAG, "TEST-CITY: " + City);
        Log.d(TAG, "TEST-STATE: " + State);

        return current;

    }

    //Checks to see if the network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
            return isAvailable;

    }
////////////////////////////////////////////////////////////////////////
    //Alerts user of No Network issue
    private void alertUserNoNetwork() {
        NoNetworkAlertDialogFragment NoNetworkdialog = new NoNetworkAlertDialogFragment();
        NoNetworkdialog.show(getFragmentManager(), "no_network_dialog");
    }
    //Alerts user of incorrect zip code
    private void alertUserZipCodeError(){
        ChangeZipError zipError = new ChangeZipError();
        zipError.show(getFragmentManager(), "zip_code_error");
    }
    //Alerts user of issue
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
    ////////////////////////////////////////////////////////////////////////
    ///  Button Click Listeners
    @OnClick(R.id.dailybutton)
    public void startDailyActivity(View view){
        //Get newly updated current temperature
        if (isNetworkAvailable()){
            Current current = mForecast.getCurrent();
            mTemp = current.getTemperature();

            Intent intent = new Intent(getApplicationContext(),DailyForecastActivity.class);
            intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
            intent.putExtra("City", City);
            intent.putExtra("State", State);
            intent.putExtra("Temp", mTemp);
            startActivity(intent);
        }else{
            alertUserNoNetwork();
        }

    }

    @OnClick(R.id.hourlybutton)
    public void startHourlyActivity(View view){

        if (isNetworkAvailable()){
            //Get newly updated current temperature
            Current current = mForecast.getCurrent();
            mTemp = current.getTemperature();

            Intent intent = new Intent(this, HourlyForecastActivity.class);
            intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
            intent.putExtra("Temp", mTemp);
            startActivity(intent);
        }else{
            alertUserNoNetwork();
        }

    }

    public void SendWidgetInfo(){
        Current current = mForecast.getCurrent();
        mTemp = current.getTemperature();

        Intent intent = new Intent(this, ScreenWidget.class);
        intent.putExtra("Temp",mTemp);
        startActivity(intent);
    }

    //Gathers new long, lat from geocoder by zip code
    public void UpdateLocation(){

        if (isNetworkAvailable()){
            // Take user input numerical in
            ChangeZip = mEditText.getText().toString();
            Geocoder geo = new Geocoder(mContext, mLocale);
            try {

                Current current = mForecast.getCurrent();
                double mTemp = current.getTemperature();
                List<Address> addresses = geo.getFromLocationName(ChangeZip, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    // get Longitude, and Latitude
                    lat = address.getLatitude();
                    lon = address.getLongitude();
                    City = address.getLocality();
                    State = address.getAdminArea();

                    //Gather new data from forecast API
                    LoadData();

                    Log.d(TAG, "State: " + State);
                    Log.d(TAG, "City: " + City);
                    Log.d(TAG,"New Lat: " + lat);
                    Log.d(TAG, "New Long: " + lon);
                    Log.d(TAG, "Your location: " + City + ", "
                            + State + " | Temp: " + mTemp);

                } else {
                    // Display appropriate message when Geocoder services are not available
                    alertUserZipCodeError();
                }
            } catch (IOException e) {
                // handle exception
                Log.e(TAG,e.getMessage());
            }
        }else{
            alertUserNoNetwork();
        }

    }





    public void ChangeBG(){
        Current current = mForecast.getCurrent();
        double temp = current.getTemperature();
        //Toast.makeText(getApplicationContext(),temp+"",Toast.LENGTH_LONG).show();
        if(temp <= 45 && temp > 0){
            Rmain.setBackgroundResource(R.drawable.bg_reallycold);
            Log.i(TAG, "BG: REALLY COLD!");
        }
        else if(temp >= 46 && temp <= 60){
            Rmain.setBackgroundResource(R.drawable.bg_cold);
            Log.i(TAG, "BG: COLD");
        }
        else if(temp >= 61 && temp <= 75){
            Rmain.setBackgroundResource(R.drawable.bg_warm);
            Log.i(TAG, "BG: WARM");
        }
        else if(temp >= 76 && temp <= 99){
            Rmain.setBackgroundResource(R.drawable.bg_reallywarm);
            Log.i(TAG, "BG: REALLY WARM!");
        }


    }





















}
