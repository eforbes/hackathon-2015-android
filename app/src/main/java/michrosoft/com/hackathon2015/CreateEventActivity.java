package michrosoft.com.hackathon2015;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

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
    @Bind(R.id.minimum_people)
    EditText minimum_number_people_picker;
    String startTimePickerTag = "startTimePicker";
    String startDatePickerTag = "startDatePicker";
    String deadlineTimePickerTag = "deadlineTimePicker";
    String deadlineDatePickerTag = "deadlineDatePicker";
    boolean startTimePickerOpen = false;
    boolean deadlineTimePickerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            deadlineDate.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
        }
        else if(view.getTag().equals(startDatePickerTag)) {
            startDate.setText(monthOfYear+ "/" + dayOfMonth  + "/" + year);
        }
        Log.d("tag","You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        if(deadlineTimePickerOpen) {
            deadlineTime.setText(hourOfDay + ":" + minute);
        }
        else if(startTimePickerOpen) {
            startTime.setText(hourOfDay + ":" + minute);
        }
        deadlineTimePickerOpen = false;
        startTimePickerOpen = false;
        Log.d("tag", "got time " + hourOfDay + ":" + minute + "." + second);

    }

    @OnClick(R.id.create_event)
    public void createEvent() {

    }
}
