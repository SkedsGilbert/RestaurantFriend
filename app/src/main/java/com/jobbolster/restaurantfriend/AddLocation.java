package com.jobbolster.restaurantfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AddLocation extends Activity {

    Context context = this;
    Button addLocationBttn;
    DBAdapter serverDB;

    TextView newText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        newText = (TextView) findViewById(R.id.nameRest);
        addLocationBttn = (Button) findViewById(R.id.addLocationBttn);

        Intent i = getIntent();
        String product = i.getStringExtra("name");
        newText.setText(product);

        setOnClick();
        openDB();
    }

    private void openDB(){
        serverDB = new DBAdapter(this);
        serverDB.open();
    }

    private void setOnClick(){
        addLocationBttn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                openDB();
                LayoutInflater dialogInflater = LayoutInflater.from(context);
                View dialogView = dialogInflater.inflate(R.layout.layout_add_rest_name_dialog,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(dialogView);
                final EditText userInput = (EditText) dialogView.findViewById(R.id.addRestDialogUserInput);
                
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Enter",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        serverDB.insertLocale(userInput.getText().toString());
                                        populateLocationListView();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        });
    }

    private void populateLocationListView() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
