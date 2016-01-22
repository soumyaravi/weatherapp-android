package soumya.weatherforecast.app;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editText1,editText2;
    private TextView err;
    private RadioButton degree;
    Button clear, search;
    Spinner selectState;
    private String st_address,city_name,radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clear = (Button)findViewById(R.id.clear);
        editText1 = (EditText)findViewById(R.id.street1);
        editText2 = (EditText)findViewById(R.id.city1);
        selectState = (Spinner)findViewById(R.id.states1);
        degree = (RadioButton) findViewById(R.id.Fahrenheit);
        err = (TextView)findViewById(R.id.error_msg);
        ImageView forecast = (ImageView)findViewById(R.id.forecast);
        forecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgClick(v);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(v);
            }
        });
    }

    private void imgClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://forecast.io"));
        startActivity(intent);
    }

    public void clear(){
        editText1.setText("");
        editText2.setText("");
        err.setText("");
        selectState.setSelection(0);
        degree.setChecked(true);
    }
    public void aboutMe(View view)
    {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void search(View view)
    {
        /*Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        startActivity(intent);*/

        if(degree.isChecked())
            radio="Fahrenheit";
        else
            radio="Celsius";
        st_address = editText1.getText().toString();
        city_name = editText2.getText().toString();
        final int state=selectState.getSelectedItemPosition();
        Resources res = getResources();
        String[] s = res.getStringArray(R.array.state_array);

                if (st_address.trim().equals("") || st_address.trim().equals(null))
                    err.setText("Please enter a Street");
                else if (city_name.trim().equals("") || city_name.trim().equals(null))
                    err.setText("Please enter a City ");
                else if (state==0)
                    err.setText("Please select a State");
                else {
                    //String url = "http://webtech12-env.elasticbeanstalk.com/?";
                    String state_name = s[state];
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http")
                            .authority("www.webtech12-env.elasticbeanstalk.com")
                            .appendQueryParameter("street", st_address)
                            .appendQueryParameter("city", city_name)
                            .appendQueryParameter("states", state_name.substring(0, 2))
                            .appendQueryParameter("degree", radio);
                    String link = builder.build().toString();
                    AsyncTaskRunner runner = new AsyncTaskRunner(MainActivity.this);
                    runner.execute(link,city_name,state_name.substring(0,2),radio);
                    //runner.onPostExecute();
                    }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
 class AsyncTaskRunner extends AsyncTask<String, String, String> {

     private static final String LOG_TAG = AsyncTaskRunner.class.getSimpleName();
     private String resp,city,state,radio;
     private MainActivity mainActivity;
     public AsyncTaskRunner(MainActivity mainActivity) {
         this.mainActivity=mainActivity;
     }

     @Override
     protected void onPreExecute (){}
    @Override
    protected String doInBackground(String... params) {
        city = params[1];
        state = params[2];
        radio = params[3];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        try {
            //webtech12-env.elasticbeanstalk.com/?street=720+West+27th+Street&city=Los+Angeles&states=CA&degree=Celsius
                resp = params[0];
                URL url = new URL(resp);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
               // urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
               // Log.v(LOG_TAG,"Forecast JSON Stream "+forecastJsonStr);
            } catch (IOException e) {
                Log.e("Fragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Fragment", "Error closing stream", e);
                    }
                }
            }
        return forecastJsonStr;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        try {
            JSONObject obj = new JSONObject(result);
            Intent intent = new Intent(mainActivity, ResultActivity.class);
            intent.putExtra("json",obj.toString());
            intent.putExtra("city",city);
            intent.putExtra("state",state);
            intent.putExtra("degree",radio);
            mainActivity.startActivity(intent);
           // Log.v(LOG_TAG,"post execute "+result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
 }