package com.jobbolster.restaurantfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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

import com.jobbolster.restaurantfriend.R;

public class AddRestaurant extends Activity {

    Context mContext = this;
    Button addRestNameBttn;
    DBAdapter serverDB;
    ListView addRestListView;
    Boolean isNewRestName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        serverDB = new DBAdapter(this);
        addRestNameBttn = (Button) findViewById(R.id.addRestaurantBttn);
        addRestListView = (ListView) findViewById(R.id.restNameListView);
        addRestListView.setLongClickable(false);

        setOnClick();
        populateRestaurantNameListView();
    }

    protected void onRestart(){
        super.onRestart();
        populateRestaurantNameListView();
    }



    private void setOnClick() {
        addRestNameBttn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                LayoutInflater dialogInflater = LayoutInflater.from(mContext);
                View dialogView = dialogInflater.inflate(R.layout.layout_add_rest_name_dialog,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setView(dialogView);
                final EditText userInput = (EditText) dialogView.findViewById(R.id.addRestDialogUserInput);
                alertDialogBuilder.setTitle(R.string.add_restaurant_dialog_title);
                alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton(R.string.enter,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String name;
                                                name = userInput.getText().toString();
                                                if (name.isEmpty() || name.trim().equals("") ||
                                                        name == null) {
                                                        Toast.makeText(mContext,"Name cannot be blank"
                                                        ,Toast.LENGTH_SHORT).show();
                                                }else {
                                                    serverDB.openWrite();
                                                    serverDB.insertRestName(name);
                                                    serverDB.closeDB();
                                                    isNewRestName = true;
                                                    startIntent(name);
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

        addRestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView temp = (TextView) view.findViewById(R.id.restNameRowTextView);
                String name = temp.getText().toString();
                isNewRestName = false;
                startIntent(name);

            }
        });

        addRestListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String buttonClicked = Long.toString(l);
                final int intConverted = (int) (long) l;
                AlertDialog.Builder deleteAlertBuilder = new AlertDialog.Builder(mContext);
                deleteAlertBuilder.setTitle(R.string.delete_restaurant_dialog_title);
                deleteAlertBuilder.setMessage(R.string.delete_address_dialog)
                        .setPositiveButton(R.string.set_delete,
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        serverDB.openWrite();
                                        Log.d("MyApp","Going into the updateRestName.");
                                        serverDB.updateRestName(intConverted);
                                        serverDB.closeDB();
                                        populateRestaurantNameListView();
                                    }
                                })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                AlertDialog alertDialog = deleteAlertBuilder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        populateRestaurantNameListView();
    }


    private void populateRestaurantNameListView() {
            serverDB.openRead();
            Cursor cursor = serverDB.getAllRestName();

        //Setup mapping from cursor to view fields
        String[] fromFieldNames = new String[]
                {DBAdapter.KEY_RESTAURANT_NAME};

        int[] toViewIDs = new int[]
                {R.id.restNameRowTextView};

        //Create adapter to my columns of the DB onto elements in the UI
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.layout_add_rest_name_row,cursor,fromFieldNames,toViewIDs);

        //Set adapter for the list view
        ListView serverInfoListView = (ListView) findViewById(R.id.restNameListView);
        serverInfoListView.setAdapter(myCursorAdapter);
        serverDB.closeDB();

    }

    public void startIntent(String name){
        String ID = "";
        serverDB.openRead();
        Cursor cursor = serverDB.getRestID(name);
        if(cursor.moveToFirst()){
            ID = cursor.getString(0);
        }
        serverDB.closeDB();
        Intent intent = new Intent(mContext,AddLocation.class);
        intent.putExtra("restNamePassed",name);
        intent.putExtra("restIdPassed",ID);
        intent.putExtra("isRestNew",isNewRestName);
        startActivity(intent);
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

        switch (item.getItemId()){
            case R.id.action_settings:
                return true;

            case R.id.home_page:
                Log.d("MyApp","In home method");
                Intent i = new Intent(mContext,StarterActivity.class);
                startActivity(i);
                return true;

            case R.id.tip_calculator:
                Log.d("MyApp","In tipCal method");
                Intent iTipCal = new Intent(mContext,MainActivity.class);
                startActivity(iTipCal);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
