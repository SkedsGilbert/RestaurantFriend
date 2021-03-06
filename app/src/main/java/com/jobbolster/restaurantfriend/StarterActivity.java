package com.jobbolster.restaurantfriend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jobbolster.restaurantfriend.R;

public class StarterActivity extends Activity {

    Button startFindServerActivity;
    Button startTipCalculator;
    Context mContext = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        startFindServerActivity = (Button) findViewById(R.id.findServerBttn);
        startTipCalculator = (Button) findViewById(R.id.useTipCalBttn);
        setBttnOnClick();
    }

    private void setBttnOnClick() {
        startFindServerActivity.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext,AddRestaurant.class);
                startActivity(i);

            }
        });

        startTipCalculator.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext,MainActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.starter, menu);
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
