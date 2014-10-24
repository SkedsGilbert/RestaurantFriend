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
import android.widget.Toast;


public class AddLocation extends Activity {

    Context mContext = this;
    Button addLocationBttn;
    DBAdapter serverDB;
    ListView addLocationListView;

    TextView addedRestName;
    String addedRestID;
    String restName;
    boolean isRestNameNew;
    boolean isLocationNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        serverDB = new DBAdapter(this);
        addedRestName = (TextView) findViewById(R.id.nameRest);
        addLocationBttn = (Button) findViewById(R.id.addLocationBttn);
        addLocationListView = (ListView) findViewById(R.id.restLocationListView);

        Intent i = getIntent();
        restName = i.getStringExtra("restNamePassed");
        addedRestID = i.getStringExtra("restIdPassed");
        isRestNameNew = i.getExtras().getBoolean("isRestNew");
        addedRestName.setText(restName + " ->");


        setOnClick();
        populateLocationListView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        populateLocationListView();
    }


    private void setOnClick(){
        addLocationBttn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {

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
                                        String localeName = userInput.getText().toString();
                                        if (localeName.isEmpty() || localeName.trim().equals("")
                                                || localeName == null) {
                                            Toast.makeText(mContext, "Location cannot be blank"
                                                    , Toast.LENGTH_SHORT).show();
                                        } else {
                                            serverDB.openWrite();
                                            serverDB.insertLocale(localeName);
                                            addRestAndLocID(localeName);
                                            serverDB.closeDB();
                                            isLocationNew = true;
                                            startIntent(localeName);
                                        }
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
                isLocationNew = false;
                startIntent(name);
        }
    });

}

    private void populateLocationListView() {
        serverDB.openRead();
        Cursor cursor = serverDB.getAllLocations();
        String[] fromLocations = new String[]{DBAdapter.KEY_RESTAURANT_LOCALE};
        int[] toViewIDs = new int[]{R.id.restNameRowTextView};
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.layout_add_rest_name_row,cursor,fromLocations,toViewIDs);
        ListView serverInfoListView = (ListView) findViewById(R.id.restLocationListView);
        serverInfoListView.setAdapter(myCursorAdapter);
        serverDB.closeDB();

    }

    public void startIntent(String localeName){
        if (isRestNameNew == true && isLocationNew == false){
            addRestAndLocID(localeName);
        }
        String locale_ID = getLocaleID(localeName);
        Intent intent = new Intent(mContext,AddServer.class);
        intent.putExtra("localeNamePassed",localeName);
        intent.putExtra("localeIdPassed",locale_ID);
        intent.putExtra("restNamePassed",restName);
        intent.putExtra("restIdPassed",addedRestID);
        startActivity(intent);
    }

    private String getLocaleID(String name){
        String locale_ID = "";
        serverDB.openRead();
        Cursor cursor = serverDB.getLocaleID(name);
        if (cursor.moveToFirst()){
            locale_ID = cursor.getString(0);
        }
        serverDB.closeDB();
        return locale_ID;
    }

    private void addRestAndLocID(String localeName){
        String localeID = getLocaleID(localeName);
        serverDB.openWrite();
        serverDB.insertRestIdLocaleID(addedRestID,localeID);
        serverDB.closeDB();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.action_settings:
                return true;

            case R.id.home_page:
                Intent i = new Intent(mContext,StarterActivity.class);
                startActivity(i);

            case R.id.tip_calculator:
                Intent iTipCal = new Intent(mContext,MainActivity.class);
                startActivity(iTipCal);
        }

        return super.onOptionsItemSelected(item);
    }
}
