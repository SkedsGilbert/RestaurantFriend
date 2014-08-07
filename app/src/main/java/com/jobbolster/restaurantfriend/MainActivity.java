package com.jobbolster.restaurantfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends Activity {

    private double billBeforeTip;
    private int tipAmount;
    private double finalBill;

    EditText billBeforeTipEt;
    EditText tipAmountEt;

    TextView finalBillTv;
    TextView tipAmountTv;
    TextView amountToTipTv;
    TextView tipPercentageTv;

    SeekBar adjustTipSb;

    Button resetBttn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setup editable text for bill amount
        billBeforeTipEt = (EditText) findViewById(R.id.billEditText);
        billBeforeTipEt.addTextChangedListener(billBeforeTipListener);
        finalBillTv = (TextView) findViewById(R.id.finalBillAmountTextView);
        tipAmountTv = (TextView) findViewById(R.id.tipAmountTextView);
        tipPercentageTv = (TextView) findViewById(R.id.tipPercentTextView);
        resetBttn = (Button) findViewById(R.id.resetAllBttn);
        //setup seekbar
        adjustTipSb = (SeekBar) findViewById(R.id.tipSeekBar);
        //setup listener
        setBttnOnClickListener();
        adjustTipSb.setOnSeekBarChangeListener(tipSeekBarListener);



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
        System.out.print("System failed here!");
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
