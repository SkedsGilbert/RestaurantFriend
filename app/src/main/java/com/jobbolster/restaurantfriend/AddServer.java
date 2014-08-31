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

import java.util.List;


public class AddServer extends Activity {

    Context mContext = this;
    Button addServerBttn;
    DBAdapter serverDB;
    ListView addServerListView;
    TextView addedLocaleNameTextView;
    String addedLocaleID;
    String addedlocaleName;
    String addedRestName;
    String addedRestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);

        serverDB = new DBAdapter(this);
        addServerBttn = (Button) findViewById(R.id.addServerBttn);
        addServerListView = (ListView) findViewById(R.id.serverListView);
        addedLocaleNameTextView = (TextView) findViewById(R.id.nameLocale);

        Intent i = getIntent();
        addedRestName = i.getStringExtra("restNamePassed");
        addedRestId = i.getStringExtra("restIdPassed");
        addedlocaleName = i.getStringExtra("localeNamePassed");
        addedLocaleID = i.getStringExtra("localeIdPassed");
        addedLocaleNameTextView.setText(addedRestName + " --> " + addedlocaleName );

        setOnclick();
        populateServerListView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        populateServerListView();
    }

    private void setOnclick(){
        addServerBttn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater dialogInflater = LayoutInflater.from(mContext);
                View dialogView = dialogInflater.inflate(R.layout.layout_add_rest_name_dialog,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setView(dialogView);
                final TextView serverText = (TextView) dialogView.findViewById(R.id.dialogTextView);
                serverText.setText("Enter Server Name");
                final EditText userInput = (EditText) dialogView.findViewById(R.id.addRestDialogUserInput);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Enter",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String name = userInput.getText().toString();
                                        serverDB.openWrite();
                                        serverDB.insertServer(name);
                                        serverDB.closeDB();
//                                      startIntent(name);
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

        addServerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView temp = (TextView) view.findViewById(R.id.restNameRowTextView);
                String name = temp.getText().toString();
//                startIntent(name);
            }
        });
    }

    private void populateServerListView(){
        serverDB.openRead();
        Cursor cursor = serverDB.getAllServer(addedRestId,addedLocaleID);
        serverDB.closeDB();
        String[] fromServers = new String[]{DBAdapter.KEY_SERVER_NAME};
        int[] toViewIDs = new int[]{R.id.restNameRowTextView};
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.layout_add_rest_name_row,cursor,fromServers,toViewIDs);
        ListView serverInfoListView = (ListView) findViewById(R.id.serverListView);
        serverInfoListView.setAdapter(myCursorAdapter);
    }

//    public void startIntent(String name){
//        String ID = "";
//        String nameString = wholeName + " -> " + name;
//        Cursor cursor = serverDB.
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_server, menu);
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
