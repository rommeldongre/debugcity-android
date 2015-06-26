package com.greylabs.sumod.dbct10;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    DBHandler db;
    ListView mainListView;
    ViewFlipper viewFlipper;
    BarChart chart1;
    BarChart chart2;

    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public void buttonSelectImage(View view){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(i, "Select Picture"),
                SELECT_PICTURE);
    }

    public void buttonShoot(View view){
        if(!hasCamera())
            Toast.makeText(this, "No camera on device", Toast.LENGTH_SHORT).show();
        else{
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void adminButtonClicked(View view){
        Intent i = new Intent(this, AdminMenu.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        db = new DBHandler(this, null, null, 1);
        chart1 = (BarChart) findViewById(R.id.chart1);
        chart2 = (BarChart) findViewById(R.id.chart2);

        ((ViewGroup)chart1.getParent()).removeView(chart1);
        ((ViewGroup)chart2.getParent()).removeView(chart2);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        viewFlipper.addView(chart1); viewFlipper.addView(chart2);
        populateChart1();
        populateChart2();
        viewFlipper.startFlipping();
        viewFlipper.setFlipInterval(1000);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            Intent i = new Intent(MainActivity.this, UserAdd.class);
            i.putExtra("Photo", photo);
            i.putExtra("resultCode", REQUEST_IMAGE_CAPTURE);
            startActivity(i);
        }
        if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            System.out.println("Image Path : " + selectedImagePath);
            Intent i = new Intent(MainActivity.this, UserAdd.class);
            i.putExtra("ImageUri", selectedImageUri);
            i.putExtra("resultCode", SELECT_PICTURE);
            startActivity(i);
        }
    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    //converting bitmap to byte[] and vice-versa:
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getByteArrayAsBitmap(byte[] imgByte){
        return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }

    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void ShowAlert(String title, String message, String button){
        AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });
        alertDialog.setIcon(R.drawable.abc_dialog_material_background_dark);
        alertDialog.show();
    }

    public void populateListView(){
        Cursor cursor = db.getCursorByRawQuery("SELECT ID, PINCODE as _id, COUNT(*) as C FROM INCIDENTS GROUP BY PINCODE ORDER BY C DESC");

        String[] fromFeildNames = new String[] {"_id", "C"};
        int[] toViewIDs = new int[] {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
                cursor, fromFeildNames, toViewIDs, 0);
        mainListView.setAdapter(myCursorAdapter);
    }

    public void populateChart1(){

        Cursor cursor = db.getCursorByRawQuery("SELECT ID, PINCODE as _id, COUNT(*) as C FROM INCIDENTS GROUP BY PINCODE ORDER BY C DESC");
        int count = cursor.getCount();
        cursor.moveToFirst();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i<count; i++){
            barEntries.add(new BarEntry(cursor.getInt(cursor.getColumnIndex("C")), i));
            cursor.moveToNext();
        }

        /*ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(4f, 0));
        entries.add(new BarEntry(8f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(12f, 3));
        entries.add(new BarEntry(18f, 4));
        entries.add(new BarEntry(9f, 5));*/

        BarDataSet barDataset = new BarDataSet(barEntries, "# of Incidents");



        ArrayList<String> labels = new ArrayList<String>();
        //labels.add("test");labels.add("test2");labels.add("test3");
        cursor.moveToFirst();
        for(int i = 0; i<count; i++){
            labels.add(cursor.getString(cursor.getColumnIndex("_id")));
            cursor.moveToNext();
        }

        BarData data = new BarData(labels, barDataset);
        chart1.setData(data);

        chart1.setDescription("# of Incidents vs PinCode");

    }

    public void populateChart2(){
        Cursor cursor = db.getCursorByRawQuery("SELECT ID, CATEGORY as _id, COUNT(*) as C FROM INCIDENTS GROUP BY CATEGORY ORDER BY C DESC");
        int count = cursor.getCount();

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i<count; i++){
            barEntries.add(new BarEntry(cursor.getInt(cursor.getColumnIndex("C")), i));
            cursor.moveToNext();
        }

        BarDataSet barDataset = new BarDataSet(barEntries, "# of Incidents");

        ArrayList<String> labels = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i<count; i++){
            labels.add(cursor.getString(cursor.getColumnIndex("_id")));
            cursor.moveToNext();
        }

        chart2 = (BarChart) findViewById(R.id.chart2);
        BarData data = new BarData(labels, barDataset);
        chart2.setData(data);

        chart2.setDescription("# of Incidents vs Category");
    }
}
