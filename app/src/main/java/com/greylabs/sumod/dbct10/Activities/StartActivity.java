package com.greylabs.sumod.dbct10.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blunderer.materialdesignlibrary.activities.NavigationDrawerActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerAccountsHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerAccountsMenuHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerBottomHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerStyleHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerTopHandler;
import com.blunderer.materialdesignlibrary.models.Account;
import com.blunderer.materialdesignlibrary.views.Toolbar;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.greylabs.sumod.dbct10.Fragments.ChartsViewPagerFragment;
import com.greylabs.sumod.dbct10.Fragments.MapsFragment;
import com.greylabs.sumod.dbct10.PrefManager;
import com.greylabs.sumod.dbct10.R;

public class StartActivity extends NavigationDrawerActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static FragmentManager fragmentManager;
    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "StartActivity";

    @Override
    protected boolean enableActionBarShadow() {
        return true;
    }

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return null;
    }


    @Override
    public NavigationDrawerStyleHandler getNavigationDrawerStyleHandler() {
        return new NavigationDrawerStyleHandler();
    }

    @Override
    public NavigationDrawerAccountsHandler getNavigationDrawerAccountsHandler() {
        PrefManager pref = new PrefManager(this);
        return new NavigationDrawerAccountsHandler(this).addAccount(pref.getName(), pref.getEmail(),
                R.drawable.ic_image_user, R.drawable.navigation_drawer_bg);
    }

    @Override
    public NavigationDrawerAccountsMenuHandler getNavigationDrawerAccountsMenuHandler() {
        return null;
    }

    @Override
    public void onNavigationDrawerAccountChange(Account account) {

    }

    @Override
    public NavigationDrawerTopHandler getNavigationDrawerTopHandler() {
        return new NavigationDrawerTopHandler(this)
                .addItem("Charts", new ChartsViewPagerFragment())
                .addItem("Maps", new MapsFragment());
    }

    @Override
    public NavigationDrawerBottomHandler getNavigationDrawerBottomHandler() {
        return new NavigationDrawerBottomHandler(this)
                .addItem(R.string.logout, R.drawable.ic_action_logout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PrefManager pref = new PrefManager(getApplicationContext());
                        if (pref.getLoginSessionCode() == pref.GOOGLE_LOGIN_SESSION) {
                            logoutWithGoogle();
                        }
                        if (pref.getLoginSessionCode() == pref.FB_LOGIN_SESSION) {
                            logoutWithFacebook();
                        }
                        else{
                            Intent i = new Intent(StartActivity.this, ActivityLogin.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
    }


    private void logoutWithGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        PrefManager pref = new PrefManager(getApplicationContext());
                        pref.logout();
                        Intent i = new Intent(StartActivity.this, ActivityLogin.class);
                        startActivity(i);
                        finish();
                    }
                });
    }

    private void logoutWithFacebook(){
        LoginManager.getInstance().logOut();
        PrefManager pref = new PrefManager(getApplicationContext());
        pref.logout();
        Intent i = new Intent(StartActivity.this, ActivityLogin.class);
        startActivity(i);
        finish();
    }

    private void buttonShoot() {
        if (!hasCamera())
            Toast.makeText(this, "No camera on device", Toast.LENGTH_SHORT).show();
        else {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        }
    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void buttonSelectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(i, "Select Picture"),
                SELECT_PICTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            Intent i = new Intent(StartActivity.this, UserAdd.class);
            i.putExtra("Photo", photo);
            i.putExtra("resultCode", REQUEST_IMAGE_CAPTURE);
            startActivity(i);
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            System.out.println("Image Path : " + selectedImagePath);
            Intent i = new Intent(StartActivity.this, UserAdd.class);
            i.putExtra("ImageUri", selectedImageUri);
            i.putExtra("resultCode", SELECT_PICTURE);
            startActivity(i);
        }
    }

    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean overlayActionBar() {
        return true;
    }

    @Override
    public boolean replaceActionBarTitleByNavigationDrawerItemTitle() {
        return true;
    }

    @Override
    public int defaultNavigationDrawerItemSelectedPosition() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
