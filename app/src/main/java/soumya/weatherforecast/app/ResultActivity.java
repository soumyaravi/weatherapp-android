package soumya.weatherforecast.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class ResultActivity extends AppCompatActivity {

    ImageView fb;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String data,city,state,radio,summary,degree,img_url;
    int t;
    JSONObject obj;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_result);

        try {
            obj = new JSONObject(getIntent().getStringExtra("json"));
            city = getIntent().getStringExtra("city");
            state = getIntent().getStringExtra("state");
            radio = getIntent().getStringExtra("degree");
            data = obj.toString();
            display();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button map = (Button)findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMap(v);
            }
        });
        Button detail = (Button)findViewById(R.id.details);
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(data);
                    Intent intent = new Intent(ResultActivity.this, DetailsActivity.class);
                    intent.putExtra("json",obj.toString());
                    intent.putExtra("city",city);
                    intent.putExtra("state",state);
                    intent.putExtra("radio",radio);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        addListenerOnFBButton();
    }

    private void display() {
        String icon=null,temp1=null,low=null,high = null;
        String pnow,sp,vis,rise,set;
        String timezone = null;
        Double precip=0.0,chance=0.0,wind=0.0,dew=0.0,hum=0.0,visib=0.0;
        long time = 0,timer = 0;
        JSONObject daily = null;
        try {
            JSONObject currently = obj.getJSONObject("currently");
            daily = obj.getJSONObject("daily");
            icon = currently.getString("icon");
            summary = currently.getString("summary");
            temp1 = currently.getString("temperature");
            precip = Double.parseDouble(currently.getString("precipIntensity"));
            chance = Double.parseDouble(currently.getString("precipProbability"));
            wind = Double.parseDouble(currently.getString("windSpeed"));
            dew = Double.parseDouble(currently.getString("dewPoint"));
            hum = Double.parseDouble(currently.getString("humidity"));
            visib = Double.parseDouble(currently.getString("visibility"));
            low = daily.getJSONArray("data").getJSONObject(0).getString("temperatureMin");
            high = daily.getJSONArray("data").getJSONObject(0).getString("temperatureMax");
            timezone = obj.getString("timezone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(radio.equals("Celsius")) {
            degree = "C";
            sp="m/s";
            vis="km";
            precip/=25.4;
        }
        else {
            degree = "F";
            sp="mph";
            vis="mi";
        }
        String deg =" "+(char) 0x00B0+degree;
        ImageView img = (ImageView)findViewById(R.id.icon);
        img.setImageResource(R.drawable.clear);
        TextView sum = (TextView)findViewById(R.id.summary);
        sum.setText(summary+" in "+city+", "+state);
        TextView temp = (TextView)findViewById(R.id.temp);
        TextView lowhigh = (TextView)findViewById(R.id.lowhigh);
        TextView pnowval = (TextView)findViewById(R.id.pnow_val);
        TextView rain = (TextView)findViewById(R.id.chance_val);
        TextView winds = (TextView)findViewById(R.id.wind_val);
        TextView dews = (TextView)findViewById(R.id.dew_value);
        TextView visibility = (TextView)findViewById(R.id.visib_val);
        TextView riseval = (TextView)findViewById(R.id.rise_val);
        TextView setval = (TextView)findViewById(R.id.set_val);
        t = (int)(Math.round(Double.parseDouble(temp1)));
        int min = (int)(Math.round(Double.parseDouble(low)));
        int max = (int)(Math.round(Double.parseDouble(high)));
        int dewpt = (int)(Math.round(dew));
        temp.setText(Integer.toString(t)+""+deg);
        dews.setText(Integer.toString(dewpt)+" "+deg);
        visibility.setText(Double.toString(visib)+" "+vis);
        lowhigh.setText(Integer.toString(min) + "" + deg+" | "+Integer.toString(max) + "" + deg);
        JSONObject field = null;
        try {
            field = daily.getJSONArray("data").getJSONObject(0);
            time = field.getLong("sunriseTime");
            timer = field.getLong("sunsetTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Date date = new Date(time*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        rise = sdf.format(date);
        riseval.setText(rise);
        Date dat1e = new Date(timer*1000L);
        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
        sdf1.setTimeZone(TimeZone.getTimeZone(timezone));
        set = sdf1.format(dat1e);
        setval.setText(set);
        if(icon.equals("clear-day"))
            {img.setImageResource(R.drawable.clear);
            img_url = "clear.png";}
        else if(icon.equals("clear-night"))
            {img.setImageResource(R.drawable.clear_night);
                img_url = "clear_night.png";}
        else if(icon.equals("rain"))
            {img.setImageResource(R.drawable.rain);
                img_url = "clear.png";}
        else if(icon.equals("snow"))
            {img.setImageResource(R.drawable.snow);
                img_url = "snow.png";}
        else if(icon.equals("sleet"))
            {img.setImageResource(R.drawable.sleet);
                img_url = "sleet.png";}
        else if(icon.equals("wind"))
            {img.setImageResource(R.drawable.wind);
                img_url = "wind.png";}
        else if(icon.equals("fog"))
            {img.setImageResource(R.drawable.fog);
                img_url = "fog.png";}
        else if(icon.equals("cloudy"))
            {img.setImageResource(R.drawable.cloudy);
                img_url = "cloudy.png";}
        else if(icon.equals("partly-cloudy-day"))
            {img.setImageResource(R.drawable.cloud_day);
                img_url = "cloud_day.png";}
        else if(icon.equals("partly-cloudy-night"))
            {img.setImageResource(R.drawable.cloud_night);
                img_url = "cloud_night.png";}

        if (precip < 0.002)
            pnow = "None";
        else if (precip >= 0.002 && precip < 0.017)
            pnow = "Very Light";
        else if (precip >= 0.017 && precip < 0.1)
            pnow = "Light";
        else if (precip >= 0.1 && precip < 0.4)
            pnow = "Moderate";
        else
            pnow = "Heavy";
        pnowval.setText(pnow);
        chance = chance*100;
        hum *=100;
        int chance1 = (int) Math.round(chance);
        int wind1 = (int) Math.round(wind);
        int hum1 = (int) Math.round(hum);
        rain.setText(Integer.toString(chance1)+"%");
        TextView humid = (TextView)findViewById(R.id.humid_val);
        humid.setText(Integer.toString(hum1)+" %");
        winds.setText(Integer.toString(wind1)+" "+sp);

    }

    public void viewMap(View view){
        JSONObject obj = null;
        try {
            obj = new JSONObject(data);
            Intent intent = new Intent(ResultActivity.this, MapActivity.class);
            intent.putExtra("json",obj.toString());
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void addListenerOnFBButton() {
        //final Context context = this;
        final Activity context=this;
        fb = (ImageView) findViewById(R.id.fb);

        fb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                callbackManager = CallbackManager.Factory.create();

                LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("email", "user_photos", "public_profile"));

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {

                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Log.d("status=", "success");

                                shareDialog = new ShareDialog(context);
                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setContentTitle("Current Weather in "+city+", "+state)
                                            .setContentDescription(summary+", "+Integer.toString(t)+" "+degree)
                                            .setContentUrl(Uri.parse("http://forecast.io"))
                                            .setImageUrl(Uri.parse("http://cs-server.usc.edu:45678/hw/hw8/images/"+img_url))
                                            .build();

                                    shareDialog.show(linkContent);
                                }

                                // this part is optional
                                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                                    @Override
                                    public void onCancel() {
                                        Toast.makeText(ResultActivity.this,"Post Cancelled",Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError(FacebookException error) {
                                        //Log.d("HelloFacebook", String.format("Error: %s", error.toString()));

                                    }

                                    @Override
                                    public void onSuccess(Sharer.Result result) {
                                        Toast.makeText(ResultActivity.this,"Facebook Post Successful",Toast.LENGTH_LONG).show();
                                    }
                                    //}

                                });
                            }

                            @Override
                            public void onCancel() {
                                Log.d("status=", "cancel");
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Log.d("status", "error");
                            }
                        });


            }

        });
    }
}