package com.jobbolster.restaurantfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private double billBeforeTip;
    private int tipAmount;
    private double finalBill;
    String serverName = "";
    String serverID = "";
    Context mContext = this;

    EditText billBeforeTipEt;
    EditText serverNotesEt;
    EditText serverNotesDialogET;
    TextView finalBillTv;
    TextView tipAmountTv;
    TextView tipPercentageTv;
    TextView serverNameTv;
    TextView serverScoreTv;
    TextView scoreLabelTv;
    SeekBar adjustTipSb;
    Button resetBttn;
    Button addNotesBttn;
    DBAdapter serverDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverDB = new DBAdapter(this);
        //extract intent info
        Intent i = getIntent();
        serverName = i.getStringExtra("serverName");
        serverID = i.getStringExtra("serverID");

        //setup edit text
        billBeforeTipEt = (EditText) findViewById(R.id.billEditText);
        billBeforeTipEt.addTextChangedListener(billBeforeTipListener);
        serverNotesEt = (EditText) findViewById(R.id.serverNotesDisplayEditText);
        serverNotesEt.setKeyListener(null);
        serverNotesDialogET = (EditText) findViewById(R.id.serverNotesEditText);
        //setup Textview
        finalBillTv = (TextView) findViewById(R.id.finalBillAmountTextView);
        tipAmountTv = (TextView) findViewById(R.id.tipAmountTextView);
        tipPercentageTv = (TextView) findViewById(R.id.tipPercentTextView);
        serverNameTv = (TextView) findViewById(R.id.serverNameTextView);
        serverScoreTv = (TextView) findViewById(R.id.serverScoreTextView);
        scoreLabelTv = (TextView) findViewById(R.id.scoreLabelTextView);
        serverNameTv.setText(serverName);
        serverScoreTv.setText(getServerScore());

        //setup buttons
        resetBttn = (Button) findViewById(R.id.resetAllBttn);
        addNotesBttn = (Button) findViewById(R.id.addNotesBttn);
        //setup seekbar
        adjustTipSb = (SeekBar) findViewById(R.id.tipSeekBar);

        adjustTipSb.requestFocus();
        //setup listener
        setBttnOnClickListener();
        adjustTipSb.setOnSeekBarChangeListener(tipSeekBarListener);
        //Check for empty server field
        checkMissingServer();

    }

    private void checkMissingServer(){
        if(serverName == null || serverName.length() == 0 || serverName.isEmpty()){
            serverNameTv.setVisibility(View.GONE);
            serverScoreTv.setVisibility(View.GONE);
            scoreLabelTv.setVisibility(View.GONE);
            serverNotesEt.setVisibility(View.GONE);
            addNotesBttn.setVisibility(View.GONE);
        }else{
            serverDB.openRead();
            String notes = "";
            Cursor cursor = serverDB.getServerNotes(serverID);
            if(cursor.moveToFirst()){
                notes = cursor.getString(0);
            }
            serverDB.closeDB();
            serverNotesEt.setText(notes);
        }
    }

    private String getServerScore(){
        String serverScore ="";
        serverDB.openRead();
        Cursor cursor = serverDB.getServerScore(serverID);
        if(cursor.moveToFirst()){
            serverScore = cursor.getString(0);
        }
        return serverScore;
    }

    private TextWatcher billBeforeTipListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try{
                billBeforeTip = Double.parseDouble(charSequence.toString());
            }catch (NumberFormatException nfe){
                billBeforeTip = 0.00;
            }
            updateTipFinalBill();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private SeekBar.OnSeekBarChangeListener tipSeekBarListener = new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            tipAmount = (adjustTipSb.getProgress());
            System.out.println(tipAmount);
            tipPercentageTv.setText(Integer.toString(tipAmount));
            updateTipFinalBill();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };



    private void updateTipFinalBill() {
        double tipFromText = Double.parseDouble(tipPercentageTv.getText().toString()) *.01;
        finalBill = billBeforeTip + (billBeforeTip * tipFromText);
        double amountToTip = finalBill - billBeforeTip;
        tipAmountTv.setText(String.format("%.02f", amountToTip));
        finalBillTv.setText(String.format("%.02f",finalBill));
    }

    private void setBttnOnClickListener(){
        resetBttn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                billBeforeTipEt.setText(String.format("%.02f", 0.00));
                adjustTipSb.setProgress(15);
            }
        });

        addNotesBttn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {

                LayoutInflater dialogInflater = LayoutInflater.from(mContext);
                View dialogView = dialogInflater.inflate(R.layout.layout_server_info_dialog,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setView(dialogView);

            //Setup Textbox and rating bar
                    final EditText userInput = (EditText) dialogView.findViewById(R.id.serverNotesEditText);
                    serverDB.openRead();
                    String notes = "";
                    Cursor cursor = serverDB.getServerNotes(serverID);
                        if(cursor.moveToFirst()){
                            notes = cursor.getString(0);
                        }
                        if(notes != null) {
                            userInput.setText(notes);
                        }

                    final RatingBar serverRating = (RatingBar) dialogView.findViewById(R.id.serverRatingBar);
                        float stars = 0;
                        Cursor getStarsCursor = serverDB.getServerScore(serverID);
                            if(getStarsCursor.moveToFirst()){
                                stars = Float.parseFloat(getStarsCursor.getString(0));
                            }
                    serverDB.closeDB();
                    serverRating.setRating(stars);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        serverDB.openWrite();
                                        serverDB.updateServerNotes(serverID, userInput.getText().toString());
                                        serverDB.updateServerScore(serverID,serverRating.getRating());
                                        serverDB.closeDB();
                                        checkMissingServer();
                                        Toast.makeText(mContext,"Notes Saved",Toast.LENGTH_SHORT).show();
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
