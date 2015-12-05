package com.greylabs.sumod.dbct10.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;


public class ActivityStart extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "ActivityStart";
    private GoogleApiClient mGoogleApiClient;
    private PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_start);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = new PrefManager(this);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (!item.isChecked()) item.setChecked(true);


                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.charts:
                        ChartsViewPagerFragment chartsViewPagerFragment = new ChartsViewPagerFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, chartsViewPagerFragment);
                        fragmentTransaction.commit();
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.map:
                        MapsFragment mapsFragment = new MapsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.frame, mapsFragment);
                        fragmentTransaction1.commit();

                        return true;

                    default:
                        return true;
                }

            }
        });

        navigationView.setCheckedItem(R.id.charts);


        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.app_name, R.string.app_name){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        View header = navigationView.getHeaderView(0);

        ((TextView) header.findViewById(R.id.username)).setText(pref.getName());
        ((TextView) header.findViewById(R.id.email)).setText(pref.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        int id = item.getItemId();

        if (id == R.id.shoot) {
            buttonShoot();
        }

        if (id == R.id.photo){
            buttonSelectImage();
        }

        if (id == R.id.admin){
            Intent i = new Intent(this, AdminMenu.class);
            startActivity(i);
        }

        if (id == R.id.btn_logout){
            PrefManager pref = new PrefManager(getApplicationContext());
            if (pref.getLoginSessionCode() == pref.GOOGLE_LOGIN_SESSION)
                logoutWithGoogle();
            if (pref.getLoginSessionCode() == pref.FB_LOGIN_SESSION)
                logoutWithFacebook();

            else {
                Intent i = new Intent(ActivityStart.this, ActivityLogin.class);
                startActivity(i);
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutWithGoogle(){

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        PrefManager pref = new PrefManager(getApplicationContext());
                        pref.logout();
                        Intent i = new Intent(ActivityStart.this, ActivityLogin.class);
                        startActivity(i);
                        finish();
                    }
                });
    }

    private void logoutWithFacebook(){
        LoginManager.getInstance().logOut();
        PrefManager pref = new PrefManager(getApplicationContext());
        pref.logout();
        Intent i = new Intent(ActivityStart.this, ActivityLogin.class);
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
            Intent i = new Intent(ActivityStart.this, UserAdd.class);
            i.putExtra("Photo", photo);
            i.putExtra("resultCode", REQUEST_IMAGE_CAPTURE);
            startActivity(i);
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            System.out.println("Image Path : " + selectedImagePath);
            Intent i = new Intent(ActivityStart.this, UserAdd.class);
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
}
