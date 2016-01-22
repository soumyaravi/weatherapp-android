package soumya.weatherforecast.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    JSONObject obj;
    String timezone,city,state,radio,degree;
    int flag =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        try {
            obj = new JSONObject(getIntent().getStringExtra("json"));
            city = getIntent().getStringExtra("city");
            state = getIntent().getStringExtra("state");
            radio = getIntent().getStringExtra("radio");
            timezone = obj.getString("timezone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (radio.equals("Celsius"))
            degree = "C";
        else
        degree = "F";
        final RelativeLayout hours = (RelativeLayout)findViewById(R.id.hrs);
        final RelativeLayout days = (RelativeLayout)findViewById(R.id.days);
        final Button day = (Button)findViewById(R.id.next7);
        final  Button hr = (Button)findViewById(R.id.next24);
        hours.setVisibility(RelativeLayout.VISIBLE);
        days.setVisibility(RelativeLayout.GONE);
        hr.setBackgroundResource(R.drawable.n24hb);
        day.setBackgroundResource(R.drawable.b2);
        init();
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hours.setVisibility(RelativeLayout.GONE);
                days.setVisibility(RelativeLayout.VISIBLE);
                day.setBackgroundResource(R.drawable.bg1);
                hr.setBackgroundResource(R.drawable.n24hg);
                daysClick(v);
            }
        });

        hr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hours.setVisibility(RelativeLayout.VISIBLE);
                days.setVisibility(RelativeLayout.GONE);
                hr.setBackgroundResource(R.drawable.n24hb);
                day.setBackgroundResource(R.drawable.b2);
                init();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }
    public void init() {
        TextView heading = (TextView)findViewById(R.id.heading);
        String head = "More Details for "+city+", "+state;
        heading.setText(head);
        final TableLayout stk = (TableLayout) findViewById(R.id.tableLayout);
        TableRow th = (TableRow) findViewById(R.id.tableheader);
        th.setBackgroundColor(Color.CYAN);
        TableRow t = new TableRow(this);
        t.setMinimumHeight(10);
        stk.addView(t);
        createTable(stk);
        final ImageView view = new ImageView(this);
        view.setImageResource(R.drawable.plus);
        view.isClickable();
        final TableRow plus1 = new TableRow(this);
        plus1.addView(view);
        plus1.layout(50, 0, 0, 0);
        plus1.setGravity(Gravity.CENTER);

        plus1.setBackgroundColor(Color.LTGRAY);
        stk.addView(plus1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=24;
                plus1.removeView(view);
                createTable(stk);
            }
        });

    }

    private void createTable(TableLayout stk) {
        JSONObject currently = null;
        JSONArray data = null;
        String icon=null,counter="";
        TextView tview2 = (TextView)findViewById(R.id.textView2);
        tview2.setText("Temp ("+(char) 0x00B0+degree+")");
        long time;
        int img = R.drawable.clear;
        try {
            currently = obj.getJSONObject("hourly");
            data = currently.getJSONArray("data");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 1; i <= 24; i++) {
            TableRow tbrow = new TableRow(this);
            int temp1 = 0;
            try {

                JSONObject field = null;
                if (data != null) {
                    field = data.getJSONObject(i + flag);
                }
                icon = field.getString("icon");
                time = field.getLong("time");
                Date date = new Date(time*1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                sdf.setTimeZone(TimeZone.getTimeZone(timezone));
                counter = sdf.format(date);
                double temp = Double.parseDouble(field.getString("temperature"));
                temp1 = (int) Math.round(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

             if(i%2!=0)
                tbrow.setBackgroundColor(Color.LTGRAY);
            TextView t1v = new TextView(this);
            t1v.setTextSize(16);
            t1v.setText(counter);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            ImageView t2v = new ImageView(this);
            //t2v.setImageResource(R.drawable.clear);

            if(icon.equals("clear-day"))
                img = R.drawable.clear;
            else if(icon.equals("clear-night"))
                img = R.drawable.clear_night;
            else if(icon.equals("rain"))
                img = R.drawable.rain;
            else if(icon.equals("snow"))
                img = R.drawable.snow;
            else if(icon.equals("sleet"))
                img = R.drawable.sleet;
            else if(icon.equals("wind"))
                img = R.drawable.wind;
            else if(icon.equals("fog"))
                img = R.drawable.fog;
            else if(icon.equals("cloudy"))
                img = R.drawable.cloudy;
            else if(icon.equals("partly-cloudy-day"))
                img = R.drawable.cloud_day;
            else if(icon.equals("partly-cloudy-night"))
                img = R.drawable.cloud_night;
            Bitmap bmp= BitmapFactory.decodeResource(getResources(), img);//image is your image
            bmp= Bitmap.createScaledBitmap(bmp, 100, 100, true);
            t2v.setImageBitmap(bmp);

            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setTextSize(16);
            t3v.setText(Integer.toString(temp1));
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.RIGHT);
            t3v.layout(0, 0, 150, 0);
            tbrow.addView(t3v);
            stk.addView(tbrow);
            TableRow tb = new TableRow(this);
            tb.setMinimumHeight(10);
            stk.addView(tb);
        }
    }
    public void daysClick(View v){
            String row_color[]={"#FFDB6A","#A0E7FF","#FFC4EA","#C4FFA5","#FFBDB7","#EFFFB5","#BCBEFF"};
        TableLayout lay = (TableLayout)findViewById(R.id.table24);
        
        //lay.removeView(t);
        String counter,icon=null;
        int rowid[]={R.id.day_row1,R.id.day_row2,R.id.day_row3,R.id.day_row4,R.id.day_row5,R.id.day_row6,R.id.day_row7};
        int dayid[]={R.id.day1,R.id.day2,R.id.day3,R.id.day4,R.id.day5,R.id.day6,R.id.day7};
        int imgid[]={R.id.imgDay1,R.id.imgDay2,R.id.imgDay3,R.id.imgDay4,R.id.imgDay5,R.id.imgDay6,R.id.imgDay7};
        int minid[]={R.id.min1,R.id.min2,R.id.min3,R.id.min4,R.id.min5,R.id.min6,R.id.min7};
        long time=0;
        int img = R.drawable.clear,low=0,high=0;
        double min=0.0,max=0.0;
        for(int i=1;i<=7;i++){
            TableRow t = (TableRow)findViewById(rowid[i-1]);
            t.setBackgroundColor(Color.parseColor(row_color[i - 1]));
            try {
                JSONObject field = obj.getJSONObject("daily").getJSONArray("data").getJSONObject(i);
                icon = field.getString("icon");
                time = field.getLong("time");
                min = field.getDouble("temperatureMin");
                max = field.getDouble("temperatureMax");
                low = (int) Math.round(min);
                high = (int) Math.round(max);
            }catch (JSONException e){}


                TableRow tr = new TableRow(this);
                tr.setMinimumHeight(10);
            Date date = new Date(time*1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d");
            sdf.setTimeZone(TimeZone.getTimeZone(timezone));
            counter = sdf.format(date);
            lay.addView(tr);
            TextView tv1 = (TextView)findViewById(dayid[i-1]);
            tv1.setTextSize(20);
            tv1.setText(counter);
//            tr.addView(tv1);
            ImageView iv = (ImageView)findViewById(imgid[i-1]);
            if(icon.equals("clear-day"))
                img = R.drawable.clear;
            else if(icon.equals("clear-night"))
                img = R.drawable.clear_night;
            else if(icon.equals("rain"))
                img = R.drawable.rain;
            else if(icon.equals("snow"))
                img = R.drawable.snow;
            else if(icon.equals("sleet"))
                img = R.drawable.sleet;
            else if(icon.equals("wind"))
                img = R.drawable.wind;
            else if(icon.equals("fog"))
                img = R.drawable.fog;
            else if(icon.equals("cloudy"))
                img = R.drawable.cloudy;
            else if(icon.equals("partly-cloudy-day"))
                img = R.drawable.cloud_day;
            else if(icon.equals("partly-cloudy-night"))
                img = R.drawable.cloud_night;
            Bitmap bmp= BitmapFactory.decodeResource(getResources(), img);//image is your image
            bmp= Bitmap.createScaledBitmap(bmp, 75, 75, true);
            iv.setImageBitmap(bmp);
            //iv.layout(170, -10, 0, 0);
          //  tr.addView(iv);
            TextView tv3 = (TextView)findViewById(minid[i-1]);
            tv3.setTextSize(16);
            tv3.setText("Min: " + Integer.toString(low) + " " + (char) 0x00B0 + degree + " | Max:" + Integer.toString(high) + " " + (char) 0x00B0 + degree);
            //tv3.layout(-240, 60, 0, 0);
            //tr.addView(tv3);

            /*TableRow tb = new TableRow(this);
            tb.setMinimumHeight(40);*/
            //lay.addView(tb);
        }
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
