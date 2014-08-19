package com.jobbolster.restaurantfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class AddLocation extends Activity {

    Context mContext = this;
    Button addLocationBttn;
    DBAdapter serverDB;
    ListView addLocationListView;

    TextView addedRestName;
    String addedRestID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        addedRestName = (TextView) findViewById(R.id.nameRest);
        addLocationBttn = (Button) findViewById(R.id.addLocationBttn);
        addLocationListView = (ListView) findViewById(R.id.restLocationListView);

        Intent i = getIntent();
        String restName = i.getStringExtra("name");
        addedRestID = i.getStringExtra("ID");
        addedRestName.setText(restName + " ->");


        setOnClick();
        openDB();
        populateLocationListView();
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

                LayoutInflater dialogInflater = LayoutInflater.from(mContext);
                View dialogView = dialogInflater.inflate(R.layout.layout_add_rest_name_dialog,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setView(dialogView);
                final TextView locationText = (TextView) dialogView.findViewById(R.id.dialogTextView);
                System.out.println(locationText.getText().toString());
                locationText.setText("Enter Restaurant Location");
                final EditText userInput = (EditText) dialogView.findViewById(R.id.addRestDialogUserInput);
                
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Enter",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        serverDB.insertLocale(userInput.getText().toString());
                                        String name = userInput.getText().toString();
                                        startIntent(name);
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

        addLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView temp = (TextView) view.findViewById(R.id.restNameRowTextView);
                String name = temp.getText().toString();
                startIntent(name);
            }
        });

    }

    private void populateLocationListView() {
        Cursor cursor = serverDB.getAllLocations();
        startManagingCursor(cursor);
        String[] fromLocations = new String[]{DBAdapter.KEY_RESTAURANT_LOCALE};
        int[] toViewIDs = new int[]{R.id.restNameRowTextView};
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.layout_add_rest_name_row,cursor,fromLocations,toViewIDs);
        ListView serverInfoListView = (ListView) findViewById(R.id.restLocationListView);
        serverInfoListView.setAdapter(myCursorAdapter);

    }

    public void startIntent(String name){
        String ID = "";
        Cursor cursor = serverDB.getRestID(name);
        if(cursor.moveToFirst()){
            ID = cursor.getString(0);
        }

        Intent intent = new Intent(mContext,AddLocation.class);
        intent.putExtra("name",name);
        intent.putExtra("ID",ID);
        startActivity(intent);
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
