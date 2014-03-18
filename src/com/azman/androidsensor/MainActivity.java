package com.azman.androidsensor;





import com.azman.androidsensor.backgroundService.LocalBinder;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.app.Activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity  {
	

	
	public Handler mHandler;
	private RelativeLayout actionShower;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actionShower = (RelativeLayout) findViewById(R.id.actionShower);
		mHandler =new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			super.handleMessage(msg);
    			switch(msg.what){
    			case -1:
    				actionShower.setBackgroundColor(Color.YELLOW);
    				break;
    			case 0: // -10

    				actionShower.setBackgroundColor(Color.BLUE);

    				break;
    			
    			case 1: //0
    				actionShower.setBackgroundColor(Color.GRAY);
    				break;
    			case 2: // 10
    				actionShower.setBackgroundColor(Color.GREEN);
    				break;
    			case 3: // 30
    				actionShower.setBackgroundColor(Color.RED);
    				break;
    			default: //anything else
    				actionShower.setBackgroundColor(Color.BLACK);
    				break;
    			}
    		}
    	};
		Intent intent = new Intent(this, backgroundService.class);

		bindService(intent,mConnection,Context.BIND_AUTO_CREATE);

		
		
		
	}
	
	
	


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		


        Intent intent = new Intent(this,backgroundService.class);
        stopService(intent);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();


	}


	public void StartClick(View view) {
		if(mService != null)
        mService.subscribe(MainActivity.this);
		
		
        
	 }
	public void StopClick(View view) {
		if(mService != null)
			mService.unSubscribe();
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_LONG).show();
	 }
	public void CalibrateClick(View view) {
        Toast.makeText(getApplicationContext(), "Stand still for 3 Secs", Toast.LENGTH_SHORT).show();
        mService.calibrate();
	 }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {



		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();

			
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
    };
    private backgroundService mService = null; 
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void btnRollSensitivity_Click(View view)
	{
		EditText txt = (EditText) findViewById(R.id.edtSensitivty);
		if(mService != null)
			mService.setRollSensitivity(Float.parseFloat( txt.getText().toString()));
		
	}


	public void btnMoveSensitivity_Click(View view)
	{
		EditText txt = (EditText) findViewById(R.id.edtSensitivty);
		if(mService != null)
			mService.setMoveSensitivity(Float.parseFloat( txt.getText().toString()));
		
	}


}




