package com.jobbolster.restaurantfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.jobbolster.restaurantfriend.R;

public class AddRestaurant extends Activity {

    Context context = this;
    Button addRestNameBttn;
    DBAdapter serverDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        addRestNameBttn = (Button) findViewById(R.id.addRestaurantBttn);
        setOnClick();
        openDB();
        populateRestaurantNameListView();
    }

    private void openDB() {
        serverDB = new DBAdapter(this);
        serverDB.open();
    }

    private void setOnClick() {
        addRestNameBttn.setOnClickListener(new Button.OnClickListener(){

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
                                                serverDB.insertRestName(userInput.getText().toString());
                                                populateRestaurantNameListView();
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


        private void populateRestaurantNameListView() {
            Cursor cursor = serverDB.getAllRestName();

        //Allow activity to manage lifetime of cursor
        startManagingCursor(cursor);

        //Setup mapping from cursor to view fields
        String[] fromFieldNames = new String[]
                {DBAdapter.KEY_RESTAURANT_NAME};

        int[] toViewIDs = new int[]
                {R.id.restNameRowTextView};

        //Create adapter to my columns of the DB onto elements in the UI
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.layout_add_rest_name_row,cursor,fromFieldNames,toViewIDs);

        //Set adapter for the list view
        ListView serverInfoListView = (ListView) findViewById(R.id.RestNameListView);
        serverInfoListView.setAdapter(myCursorAdapter);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_restaurant, menu);
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
