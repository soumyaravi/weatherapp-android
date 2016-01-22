package soumya.weatherforecast.app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hamweather.aeris.communication.Action;
import com.hamweather.aeris.communication.AerisCallback;
import com.hamweather.aeris.communication.AerisCommunicationTask;
import com.hamweather.aeris.communication.AerisEngine;
import com.hamweather.aeris.communication.AerisRequest;
import com.hamweather.aeris.communication.Endpoint;
import com.hamweather.aeris.communication.EndpointType;
import com.hamweather.aeris.communication.loaders.ObservationsTask;
import com.hamweather.aeris.communication.loaders.ObservationsTaskCallback;
import com.hamweather.aeris.communication.parameter.LimitParameter;
import com.hamweather.aeris.communication.parameter.PlaceParameter;
import com.hamweather.aeris.model.AerisError;
import com.hamweather.aeris.model.AerisResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MapActivity extends AppCompatActivity {
    private static final int REQUEST_NTF_SERVICE = 10;
    private static final String LOG_TAG = MapActivity.class.getSimpleName();
    double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        try {
            JSONObject obj = new JSONObject(getIntent().getStringExtra("json"));
            latitude = obj.getDouble("latitude");
            longitude = obj.getDouble("longitude");
            Log.v(LOG_TAG,"lat "+latitude+" , long"+longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AerisEngine.initWithKeys(this.getString(com.android.weather.R.string.aeris_client_id), this.getString(com.android.weather.R.string.aeris_client_secret), String.valueOf(this));
        PlaceParameter place = new PlaceParameter(latitude,longitude);

        ObservationsTask task = new ObservationsTask(MapActivity.this,
                new ObservationsTaskCallback() {

                    @Override
                    public void onObservationsFailed(AerisError error) {
                        // handle fail here
                    }

                    @Override
                    public void onObservationsLoaded(List responses) {
                        // handle successful loading here.
                    }

                });
        // start the request for the task by passing in our Place Parameter.
        //task.requestClosest(place);
        /*
* This is an example of building with several parameters. Note: all of
* these parameters may not apply to every endpoint being requested.
*/
        /*ParameterBuilder builder = new ParameterBuilder()
                .withFields(ObservationFields.ICON)
                .withFilter("day")
                .withLimit(2)
                .withRadius(5)
                .withFrom("-24hours")
                .withTo("now");

        task.requestClosest(place, builder.build());
        task.withProgress(new AerisProgressListener() {

            @Override
            public void hideProgress() {
                //hide a the progress dialog
            }

            @Override
            public void showProgress() {
                //do something like show a progress dialog
            }

        });*/
        AerisRequest request = new AerisRequest(new Endpoint(EndpointType.OBSERVATIONS),
                Action.CLOSEST, new PlaceParameter(), new LimitParameter(10));
        AerisCommunicationTask task1 = new AerisCommunicationTask(MapActivity.this,
                new AerisCallback() {
                    @Override
                    public void onResult(EndpointType endpoint, AerisResponse response) {
                        //handle the response here
                    }
                }, request);
        task1.execute();
        // Fragment currentFragment;
        MapFragment fragment = new MapFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.view_map,fragment);
        fragmentTransaction.commit();
        //currentFragment = fragment;
        // AerisProgressListener listener;
        // task1.withProgress(listener);
        //AerisMapsEngine.getInstance(this).getDefaultPointParameters().setLightningParameters("dt:-1", 500, null, "-4hours");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    public double getLat(){
        return latitude;
    }
    public double getLong(){
        return longitude;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
