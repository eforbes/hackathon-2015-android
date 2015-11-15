package michrosoft.com.hackathon2015;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

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
import butterknife.OnClick;

public class EditEventActivity extends AppCompatActivity {
    Invitation invitation;
    @Bind(R.id.attendee_list) ListView attendeeList;
    @Bind(R.id.accept_invitation) Switch acceptSwitch;
    @Bind(R.id.invitation_time_left) TextView timeLeft;
    @Bind(R.id.invitation_time_start) TextView timeStart;
    private int id;
    private RequestQueue requestQueue;
    private ArrayList<Attendee> attendees = new ArrayList<Attendee>();
    private ArrayList<Attendee> friends = new ArrayList<>();
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

        timeLeft.setText( "RSVP by " + invitation.getResponse_deadline());
        timeStart.setText("Starts at " + invitation.getStart_time());
        acceptSwitch.setChecked((invitation.getStatus() == 1 ? true : false));
        setTitle(invitation.getDescription());

        requestQueue = Volley.newRequestQueue(this);

        getOtherAttendees(id,invitation.getEvent_id());
        getFriends(id);
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
                                    Attendee attendee = new Attendee(((JSONObject)jsonArray.get(i)).getString("name"), ((JSONObject)jsonArray.get(i)).getString("email"), ((JSONObject)jsonArray.get(i)).getInt("user_id"));
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

    //login/getFavoriteUsers  post id
    private void getFriends(int id) {
        try {
            JSONObject jsObj = new JSONObject();

            jsObj.put("id", id + "");
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, "http://michael.evanforbes.net:3000/login/getFavoriteUsers", jsObj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("rows");
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    Attendee attendee = new Attendee(((JSONObject)jsonArray.get(i)).getString("name"), ((JSONObject)jsonArray.get(i)).getString("email"), ((JSONObject)jsonArray.get(i)).getInt("id"));
                                    friends.add(attendee);
                                }

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
    ArrayList<Attendee> friendsNotAttending = new ArrayList<Attendee>();
    @OnClick(R.id.add_attendee)
    public void showFriendList() {

        friendsNotAttending = new ArrayList<Attendee>();
        for(int i = 0; i < friends.size(); i++) {
            if(!attendees.contains(friends.get(i))) {
                friendsNotAttending.add(friends.get(i));
            }
        }
        if(friendsNotAttending.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setTitle("Everyone's Here");
            builder.setMessage("All your friend have been added to this event!");
            builder.create().show();
        }
        String[] friendsNotAttendingArr = new String[friendsNotAttending.size()];
        for(int i = 0; i < friendsNotAttending.size(); i++) {
            if(!attendees.contains(friendsNotAttending.get(i))) {
                friendsNotAttendingArr[i] = friendsNotAttending.get(i).toString();
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
        builder.setTitle("Select a friend to add")
                .setItems(friendsNotAttendingArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addToEvent(invitation.getEvent_id(), friendsNotAttending.get(which).getId());
                    }
                });
         builder.create().show();
    }

    // invitations/secureInvite eventId id type="user"
    //login/getFavoriteUsers  post id
    private void addToEvent(String eventId, final int userId) {
        try {
            JSONObject jsObj = new JSONObject();

            jsObj.put("eventId", eventId);
            jsObj.put("id", userId + "");
            jsObj.put("type", "user");
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, "http://michael.evanforbes.net:3000/invitations/secureInvite", jsObj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                               Log.d("edit event", "sucessfully added");
                                for(Attendee friend : friends) {
                                    if(friend.getId() == userId)
                                        attendees.add(friend);
                                }
                            } catch (Exception ex) {
                                Log.e("login", "error getting key from response " + ex.toString());
                                showError();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("login", error.toString() + ", ");
                            error.printStackTrace();
                        }
                    });
            requestQueue.add(jsObjRequest);
        } catch (Exception ex) {
            Log.e("login", "exception creating jsobj" + ex.toString());
            showError();
        }
    }

    @OnClick(R.id.done_editing)
    public void doneEditing() {
        Intent main = new Intent(EditEventActivity.this, MainActivity.class);
        main.putExtra("id", id);
        startActivity(main);
    }
}
