package com.greylabs.sumod.dbct10.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.greylabs.sumod.dbct10.Adapters.DBHandler;
import com.greylabs.sumod.dbct10.Model.Category;
import com.greylabs.sumod.dbct10.R;


public class CategoryAdd extends AppCompatActivity {

    DBHandler db;
    Button button_save_category;
    EditText cat_name_editTextView;
    EditText cat_desc_editTextView;

    public void buttonSaveCategory(View view){

        new AlertDialog.Builder(this)
                .setTitle("Save")
                .setMessage("Save this Category?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Category category = new Category();
                        category.setName(cat_name_editTextView.getText().toString());
                        category.setDescription(cat_desc_editTextView.getText().toString());
                        db.addCategory(category, CategoryAdd.this);
                        Toast.makeText(CategoryAdd.this, "SAVED", Toast.LENGTH_SHORT).show();
                        cat_name_editTextView.setText("");
                        cat_desc_editTextView.setText("");

                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);
        db = new DBHandler(this, null, null, 1);
        button_save_category = (Button) findViewById(R.id.button_save_category);
        cat_name_editTextView = (EditText) findViewById(R.id.cat_name_editTextView);
        cat_desc_editTextView = (EditText) findViewById(R.id.cat_desc_editTextView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_add, menu);
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
