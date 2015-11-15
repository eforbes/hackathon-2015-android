package michrosoft.com.hackathon2015;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class EditEventActivity extends AppCompatActivity {
    Invitation invitation;
    @Bind(R.id.attendee_list) ListView attendeeList;
    @Bind(R.id.accept_invitation) Switch acceptSwitch;
    private int id;
    private RequestQueue requestQueue;
    private ArrayList<Attendee> attendees = new ArrayList<Attendee>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("invitation")) {
            invitation  = extras.getParcelable("invitation");
        }
        else {
            finish();
        }
        if(extras.containsKey("id")) {
            id = extras.getInt("id");
        }
        requestQueue = Volley.newRequestQueue(this);


        getOtherAttendees(id,invitation.getEvent_id());
    }

    @OnCheckedChanged(R.id.accept_invitation)
    public void OnInvitationAccepted() {
        acceptEvent(acceptSwitch.isChecked(), invitation);
    }

    //events/secureGetAttendingUsers id eventId
    private void getOtherAttendees(int id, String eventId) {
        try {
            JSONObject jsObj = new JSONObject();
            jsObj.put("id", id+"");
            jsObj.put("eventId", invitation.getEvent_id());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, "http://michael.evanforbes.net:3000/events/secureGetAttendingUsers", jsObj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("tag", "got response");
                                JSONArray jsonArray = response.getJSONArray("rows");
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    Attendee attendee = new Attendee(((JSONObject)jsonArray.get(i)).getString("name"), ((JSONObject)jsonArray.get(i)).getString("email"));
                                    attendees.add(attendee);
                                }
                                ArrayAdapter<Attendee> adapter = new ArrayAdapter<Attendee>(EditEventActivity.this,
                                        android.R.layout.simple_list_item_1, attendees);
                                attendeeList.setAdapter(adapter);
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


    private void acceptEvent(boolean accepted, Invitation invitation) {
        try {
            JSONObject jsObj = new JSONObject();
            if(accepted)
                jsObj.put("status", 1+"");
            else
                jsObj.put("status", 2+"");
            jsObj.put("id", id+"");
            jsObj.put("eventId", invitation.getEvent_id());
            Log.d("mainactivity", jsObj.toString());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, "http://michael.evanforbes.net:3000/events/secureRespond", jsObj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("tag", "got response");
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

            }
        });
        builder.setTitle("Connection Error");
        builder.setMessage("Error getting data from server.");
        builder.create().show();
    }

}
