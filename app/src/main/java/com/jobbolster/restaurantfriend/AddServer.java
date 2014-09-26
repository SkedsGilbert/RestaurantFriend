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

import java.util.List;


public class AddServer extends Activity {

    Context mContext = this;
    Button addServerBttn;
    DBAdapter serverDB;
    ListView addServerListView;
    TextView addedLocaleNameTextView;
    String addedLocaleID;
    String addedLocaleName;
    String addedRestName;
    String addedRestId;
    String restLocID;
    long serverID;

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
        addedLocaleName = i.getStringExtra("localeNamePassed");
        addedLocaleID = i.getStringExtra("localeIdPassed");
        addedLocaleNameTextView.setText(addedRestName + " --> " + addedLocaleName );
        restLocID = getRestLocaleID();

        setOnclick();
        populateServerListView();
    }

    private String getRestLocaleID(){
        String restLocID = "";
        serverDB.openRead();
        Cursor cursor = serverDB.getRestLocID(addedRestId,addedLocaleID);
        if(cursor.moveToFirst()){
            restLocID = cursor.getString(0);
        }
        serverDB.closeDB();
        return restLocID;
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
                                        if (name.isEmpty() || name.trim().equals("") ||
                                                name == null) {
                                            Toast.makeText(mContext, "Server name cannot be blank"
                                                    , Toast.LENGTH_SHORT).show();
                                        } else {
                                            serverDB.openWrite();
                                            serverID = serverDB.insertServer(name, restLocID);
                                            serverDB.closeDB();
                                            String newText = String.valueOf(serverID);
                                            populateServerListView();
                                            startIntent(name, newText);
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

        addServerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String text = String.valueOf(l);
        TextView temp = (TextView) view.findViewById(R.id.serverNameRowTextView);
        String name = temp.getText().toString();
        startIntent(name,text);
        }
        });
        }

private void populateServerListView(){
        serverDB.openRead();
        Cursor cursor = serverDB.getAllServer(restLocID);
        serverDB.closeDB();
        String[] fromServers = new String[]{DBAdapter.KEY_SERVER_NAME,DBAdapter.KEY_SERVER_SCORE };
        int[] toViewIDs = new int[]{R.id.serverNameRowTextView,R.id.serverScoreRowTextView};
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.layout_add_server_row,cursor,fromServers,toViewIDs);
        ListView serverInfoListView = (ListView) findViewById(R.id.serverListView);
        serverInfoListView.setAdapter(myCursorAdapter);
    }

    public void startIntent(String serverName, String serverID){
        Intent intent = new Intent(mContext,MainActivity.class);
        intent.putExtra("serverName",serverName);
        intent.putExtra("serverID",serverID);
        startActivity(intent);
    }


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
