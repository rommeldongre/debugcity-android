package com.greylabs.sumod.dbct10;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CategoryDetails extends AppCompatActivity{

    DBHandler db;
    CategoryList list;

    public void buttonEditCategory(View view){
        EditText cat_name_view = (EditText) findViewById(R.id.cat_name_view);
        EditText cat_desc_view = (EditText) findViewById(R.id.cat_desc_view);

        Button button_edit_category = (Button) findViewById(R.id.button_edit_category);

        String text = String.valueOf(button_edit_category.getText());

        switch(text){
            case "EDIT":
                cat_name_view.setCursorVisible(true);
                cat_desc_view.setCursorVisible(true);

                cat_name_view.setEnabled(true);
                cat_desc_view.setEnabled(true);
                button_edit_category.setText("SAVE");

                break;

            case "SAVE":
                Category category = new Category();

                Intent intent = getIntent();
                String category_name = intent.getStringExtra("category_name");

                category.setName(String.valueOf(cat_name_view.getText()));
                category.setDescription(String.valueOf(cat_desc_view.getText()));

                cat_name_view.setCursorVisible(false);
                cat_desc_view.setCursorVisible(false);

                cat_name_view.setEnabled(false);
                cat_desc_view.setEnabled(false);

                db.editCategory(category, category_name);
                db.close();
                Toast.makeText(this, "UPDATED", Toast.LENGTH_LONG).show();

                button_edit_category.setText("EDIT");
                break;
        }
    }

    public void buttonDeleteCategory(View view){


        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = getIntent();
                        String category_name = intent.getStringExtra("category_name");
                        db.deleteCategory(category_name);
                        db.close();
                        Toast.makeText(CategoryDetails.this, "DELETED!", Toast.LENGTH_LONG).show();

                        EditText cat_name_view = (EditText) findViewById(R.id.cat_name_view);
                        EditText cat_desc_view = (EditText) findViewById(R.id.cat_desc_view);

                        cat_name_view.setText("");
                        cat_desc_view.setText("");

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
        setContentView(R.layout.activity_category_details);
        db = new DBHandler(this, null, null, 1);

        Intent intent = getIntent();
        String category_name = intent.getStringExtra("category_name");

        Category category = db.getCategory(category_name, this);

        EditText cat_name_view = (EditText) findViewById(R.id.cat_name_view);
        EditText cat_desc_view = (EditText) findViewById(R.id.cat_desc_view);

        cat_name_view.setCursorVisible(false);
        cat_desc_view.setCursorVisible(false);

        cat_name_view.setEnabled(false);
        cat_desc_view.setEnabled(false);

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
