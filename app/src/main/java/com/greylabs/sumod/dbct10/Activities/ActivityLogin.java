package com.greylabs.sumod.dbct10.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.FacebookSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.greylabs.sumod.dbct10.Adapters.DBHandler;
import com.greylabs.sumod.dbct10.Model.User;
import com.greylabs.sumod.dbct10.PrefManager;
import com.greylabs.sumod.dbct10.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import butterknife.ButterKnife;
//import butterknife.InjectView;

public class ActivityLogin extends AppCompatActivity implements OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "LoginActivity";

    Button btn_logIn;
    EditText input_email;
    EditText input_password;
    TextView signUp_link;
    DBHandler db;
    PrefManager pref;
    Snackbar snackbar;
    LinearLayout linearLayout;
    GoogleApiClient mGoogleApiClient;
    SignInButton googleSignInButton;
    LoginButton fbLoginButton;
    CallbackManager callbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        pref = new PrefManager(this);
        if (pref.isLoggedIn()){
            Intent i = new Intent(ActivityLogin.this, StartActivity.class);
            startActivity(i);
            finish();
        }

        db = new DBHandler(this, null, null, 1);

        btn_logIn = (Button) findViewById(R.id.button_logIn);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        signUp_link = (TextView) findViewById(R.id.link_signup);
        linearLayout = (LinearLayout) findViewById(R.id.activity_login_linearLayout);

        btn_logIn.setOnClickListener(this);
        signUp_link.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSignInButton = (SignInButton) findViewById(R.id.btn_google_signIn);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleSignInButton.setScopes(gso.getScopeArray());
        googleSignInButton.setOnClickListener(this);



        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) findViewById(R.id.btn_facebook_signIn);
        ArrayList<String> fbPermissions = new ArrayList<>();
        fbPermissions.add("email"); fbPermissions.add("public_profile"); fbPermissions.add("user_location");
        fbLoginButton.setReadPermissions(fbPermissions);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    User user = new User();
                                    if (object.has("email")){
                                        String email = object.getString("email");
                                        user.setEmail_ID(email);
                                        pref.createLoginSession(object.getString("name"),email, pref.FB_LOGIN_SESSION);
                                        user.setFull_name(object.getString("name"));
                                        user.setLocation(object.getJSONObject("location").getString("name"));
                                        db.addUser(user);

                                        Intent i = new Intent(ActivityLogin.this, StartActivity.class);
                                        startActivity(i);
                                        finish();
                                    }else {
                                        Snackbar snackbar = Snackbar
                                                .make(linearLayout, "Cannot Log in without email!", Snackbar.LENGTH_LONG);
                                        snackbar.show();                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,location");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_signIn:
                signInwithGoogle();
                break;

            case R.id.button_logIn:
                LoginwithEmail();
                break;

            case R.id.link_signup:
                SignUpLink();
                break;
        }
    }

    //OnClick methods:
    public void LoginwithEmail(){
        final String EMAIL_PATTERN = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$";

        if (!isNetworkConnected()) {

            Snackbar snackbar = Snackbar
                    .make(linearLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LoginwithEmail();
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();

        } else if (!TextUtils.isEmpty(input_email.getText())) {
            input_password.setError("Enter a password");
        } else if(input_password.getText().toString().length() < 8){
            input_password.setError("Password must contatain minimum 8 characters");
        } else if (!TextUtils.isEmpty(input_email.getText()) && android.util.Patterns.EMAIL_ADDRESS.matcher(input_email.getText()).matches()) {
            input_email.setError("Enter a valid email");
        } else {
            login();
        }

    }

    public void login(){
        String email = input_email.getText().toString();
        String password = input_password.getText().toString();
        btn_logIn.setEnabled(false);

        if(db.ifUSerExists(email)){
            User user = db.getUser(email);
            if (user.getPassword().equals(password)) {
                pref.createLoginSession(user.getFull_name(), email, pref.EMAIL_LOGIN_SESSION);
                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Welcome to DebugCity!", Snackbar.LENGTH_LONG);
                snackbar.show();
                Intent intent = new Intent(ActivityLogin.this, StartActivity.class);
                intent.putExtra("user_name", user.getFull_name());
                startActivity(intent);
                finish();
            }
            else{
                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Email or Password is incorrect", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
                btn_logIn.setEnabled(true);
            }
        }
        else {
            Snackbar snackbar = Snackbar
                    .make(linearLayout, "Email does not exist!", Snackbar.LENGTH_LONG)
                    .setAction("Sign Up", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ActivityLogin.this, SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.GREEN);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.BLUE);
            snackbar.show();

            btn_logIn.setEnabled(true);

        }
    }

    public void SignUpLink(){
        Intent intent = new Intent(ActivityLogin.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void signInwithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean isNetworkConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo info : networkInfo) {
                if (info.getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }
        }

        return false;

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
                pref.createLoginSession(acct.getDisplayName(), acct.getEmail(), pref.GOOGLE_LOGIN_SESSION);
                User user = new User(acct.getEmail(), acct.getDisplayName());
                db.addUser(user);
                Intent i = new Intent(ActivityLogin.this, StartActivity.class);
                startActivity(i);
                finish();

            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}