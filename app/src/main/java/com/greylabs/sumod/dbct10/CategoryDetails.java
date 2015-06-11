package com.greylabs.sumod.dbct10;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class CategoryDetails extends ActionBarActivity {

    DBHandler db;
    CategoryList list;

    public void buttonDeleteCategory(View view){
        Intent intent = getIntent();
        String category_name = intent.getStringExtra("category_name");
        db.deleteCategory(category_name);
        Toast.makeText(this, "DELETED!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);
        db = new DBHandler(this, null, null, 1);

        Intent intent = getIntent();
        String category_name = intent.getStringExtra("category_name");

        Category category = db.getCategory(category_name, this);

        TextView cat_name_view = (TextView) findViewById(R.id.cat_name_view);
        TextView cat_desc_view = (TextView) findViewById(R.id.cat_desc_view);

        cat_name_view.setText(category.getName());
        cat_desc_view.setText(category.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_details, menu);
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
}
