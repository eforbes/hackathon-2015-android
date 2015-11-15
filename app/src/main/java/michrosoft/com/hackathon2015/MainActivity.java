package michrosoft.com.hackathon2015;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

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

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.invitation_recycler)
    RecyclerView invitationRecycler;

    public final static String TAG = "MainActivity";
    private LinearLayoutManager linearLayout;
    private int id;
    private RequestQueue requestQueue;
    private  ArrayList<Invitation> invitations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewInvitationEvent = new Intent(getBaseContext(), CreateEventActivity.class);
                viewInvitationEvent.putExtra("id", id);
                startActivity(viewInvitationEvent);
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("id")) {
            id = extras.getInt("id");
        }
        requestQueue = Volley.newRequestQueue(this);
        ///events/secureListEvents id

        invitations = new ArrayList<Invitation>();
        invitationRecycler.setHasFixedSize(true);

        linearLayout = new LinearLayoutManager(this);
        invitationRecycler.setLayoutManager(linearLayout);
        getEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    private OnInvitationAccepted invitationAccepted = new OnInvitationAccepted() {
        @Override
        public void onCheckedChanged(Invitation invitation, CompoundButton button, int position) {
            boolean invitationAccepted = invitation.getStatus() == 1 ? true : false;
            if(invitationAccepted != button.isChecked() ) {
                Log.d(TAG,"checked? " + button.isChecked() + ", " + invitation.getDescription());
                //events/secureRespond status id  eventId
                acceptEvent(button.isChecked(), invitation);
                if(button.isChecked())
                    invitation.setStatus(1);
                else
                    invitation.setStatus(2);
            }
        }
    };

    private OnInvitationClickedEvent invitationClicked = new OnInvitationClickedEvent() {
        @Override
        public void onInvitationClicked(Invitation invitation) {
            Intent viewInvitationEvent = new Intent(getBaseContext(), EditEventActivity.class);
            viewInvitationEvent.putExtra("invitation", invitation);
            viewInvitationEvent.putExtra("id", id);
            startActivity(viewInvitationEvent);
        }
    };

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
                                Log.d(TAG, "got response");
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

    private void getEvents() {
        try {
            JSONObject jsObj = new JSONObject();
            jsObj.put("id", id);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, "http://michael.evanforbes.net:3000/events/secureListEvents", jsObj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONArray jsonArray = response.getJSONArray("rows");
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    Invitation newInvite = new Invitation(((JSONObject) jsonArray.get(i)));
                                    invitations.add(newInvite);
                                }
                                InvitationAdapter invitationAdapter = new InvitationAdapter(invitations, invitationAccepted, invitationClicked);
                                invitationRecycler.setAdapter(invitationAdapter);

                                Log.d(TAG, "got response");
                            }catch (Exception ex) {
                                Log.e("login", "error getting key from response " + ex.toString());
                                showError();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("login", error.toString());
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


