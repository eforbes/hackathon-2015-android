package michrosoft.com.hackathon2015;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {

    @Bind(R.id.start_time) TextView startTime;
    @Bind(R.id.start_date) TextView startDate;
    @Bind(R.id.set_date) Button setDate;
    @Bind(R.id.set_time) Button setTime;
    @Bind(R.id.set_deadline_date) Button setDeadlineDate;
    @Bind(R.id.set_deadline_time) Button setDeadlineTime;
    @Bind(R.id.deadline_time) TextView deadlineTime;
    @Bind(R.id.deadline_date) TextView deadlineDate;
    @Bind(R.id.minimum_people) EditText minimum_number_people_picker;
    @Bind(R.id.event_title) EditText eventTitle;
    @Bind(R.id.event_location) EditText eventLocation;
    @Bind(R.id.description) EditText description;


    String startTimePickerTag = "startTimePicker";
    String startDatePickerTag = "startDatePicker";
    String deadlineTimePickerTag = "deadlineTimePicker";
    String deadlineDatePickerTag = "deadlineDatePicker";
    boolean startTimePickerOpen = false;
    boolean deadlineTimePickerOpen = false;
    RequestQueue requestQueue;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("id")) {
            id = extras.getInt("id");
        }
        requestQueue = Volley.newRequestQueue(this);

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CreateEventActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), startDatePickerTag);

            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimePickerOpen = true;
                Calendar now = Calendar.getInstance();
                TimePickerDialog dpd = TimePickerDialog.newInstance(
                        CreateEventActivity.this,
                        now.get(Calendar.HOUR),
                        now.get(Calendar.MINUTE),
                        false
                );
                dpd.show(getFragmentManager(), startTimePickerTag);
            }
        });

        setDeadlineDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CreateEventActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), deadlineDatePickerTag);

            }
        });

        setDeadlineTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadlineTimePickerOpen = true;
                Calendar now = Calendar.getInstance();
                TimePickerDialog dpd = TimePickerDialog.newInstance(
                        CreateEventActivity.this,
                        now.get(Calendar.HOUR),
                        now.get(Calendar.MINUTE),
                        false
                );
                dpd.show(getFragmentManager(), deadlineTimePickerTag);
            }
        });

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(view.getTag().equals(deadlineDatePickerTag)) {
            deadlineDate.setText(monthOfYear + "-" + dayOfMonth + "-" + year);
        }
        else if(view.getTag().equals(startDatePickerTag)) {
            startDate.setText(monthOfYear+ "-" + dayOfMonth  + "-" + year);
        }
        Log.d("tag", "You picked the following date: " + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        if(deadlineTimePickerOpen) {
            String amPm = "AM";
            if(hourOfDay >= 12) {

                amPm = "PM";
            }
            hourOfDay = hourOfDay%12;
            if(hourOfDay == 0)
                hourOfDay = 12;
            deadlineTime.setText(hourOfDay + ":" + minute + amPm);
        }
        else if(startTimePickerOpen) {
            String amPm = "AM";

            if(hourOfDay >= 12)
                amPm = "PM";
            hourOfDay = hourOfDay%12;
            if(hourOfDay == 0)
                hourOfDay = 12;
            startTime.setText(hourOfDay + ":" + minute+amPm);
        }
        deadlineTimePickerOpen = false;
        startTimePickerOpen = false;
        Log.d("tag", "got time " + hourOfDay + ":" + minute + "." + second);

    }

    @OnClick(R.id.create_event)
    public void createEvent() {
        createEvent(id, eventTitle.getText().toString(), description.getText().toString(), eventLocation.getText().toString(), startDate.getText().toString(), startTime.getText().toString(), deadlineDate.getText().toString(), deadlineTime.getText().toString(), minimum_number_people_picker.getText().toString());
    }
    //11/15
    //m-d-yyyy
    //H:MMam/pm
    // /events/secureCreateEvent/ id:my id, title:title, description, location, startdate, starttime, responsedate, responsetime , minimumattend(optional)
    private void createEvent(int id2, String title, String description, String location, String startDate, String startTime, String responseDate, String responseTime, String minimumAttendance) {
        try {
            JSONObject jsObj = new JSONObject();
            jsObj.put("id", id2+"");
            jsObj.put("title", title);
            jsObj.put("description", description);
            jsObj.put("location", location);
            jsObj.put("start_date", startDate);
            jsObj.put("start_time", startTime);
            jsObj.put("response_date", responseDate);
            jsObj.put("response_time", responseTime);
            jsObj.put("minimum_attendance", minimumAttendance);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, "http://michael.evanforbes.net:3000/events/secureCreateEvent", jsObj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("tag", "got response");
                                Intent main = new Intent(CreateEventActivity.this, MainActivity.class);
                                main.putExtra("id", id);
                                startActivity(main);
                            }catch (Exception ex) {
                                Log.e("login", "error getting key from response " + ex.toString());
                                showError();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("login", error.toString() + ", " );
                            error.printStackTrace();
                        }
                    });
            requestQueue.add(jsObjRequest);
        }
        catch(Exception ex) {
            Log.e("login", "exception creating jsobj" + ex.toString());
            showError();
        }
    }
    public void showError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setTitle("Login Error");
        builder.setMessage("Could not login to the service. App will exit.");
        builder.create().show();
    }
}
