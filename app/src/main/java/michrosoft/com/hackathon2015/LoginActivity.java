package michrosoft.com.hackathon2015;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private GoogleApiClient googleApiClient;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestQueue = Volley.newRequestQueue(this);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, connectionFailedListener /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e("login", "error logging in");
        }
    };

    @OnClick(R.id.sign_in_button)
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public static final int RC_SIGN_IN = 0;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private String loginUrl = "http://michael.evanforbes.net:3000/login/secureUserCreate";
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("login", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String email = acct.getEmail();
            String id = acct.getId();
            String name = acct.getDisplayName();
            String photoUrl = "";
            if(acct.getPhotoUrl() != null)
                photoUrl = acct.getPhotoUrl().toString();

            Log.d("login", email + "," + id + "," + name+ "," + photoUrl);
            try {
                JSONObject jsObj = new JSONObject();
                jsObj.put("openid", id);
                jsObj.put("name", name);
                jsObj.put("email", email);
                jsObj.put("img", photoUrl);
                Log.d("login", jsObj.toString());
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, loginUrl, jsObj, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int id = response.getInt("id");
                                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                                    main.putExtra("id", id);
                                    startActivity(main);
                                }catch (Exception ex) {
                                    Log.e("login", "error getting key from response " + ex.toString());
                                    showErrorAndExit();;
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
                showErrorAndExit();
            }


        } else {
            // Signed out, show unauthenticated UI.
            showErrorAndExit();
        }
    }

    public void showErrorAndExit() {
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
