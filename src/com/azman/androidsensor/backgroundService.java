/**
 * 
 */
package com.azman.androidsensor;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Binder;
import android.os.IBinder;

import android.util.Log;
import android.widget.Toast;


/**
 * @author Cagdas
 *
 */
public class backgroundService extends Service implements SensorEventListener{


	private boolean isRunning = false;
	private boolean isInCalibration = false;

	
//	private int SEND_THRESHOLD = 5;
//	private int[] send_Counters = new int[20];
	private float[][] buffer = new float[3][25];
	private float[] calibrationValues = new float[3];
	private int ptrBuffer = 0;
	private float sensitivity= 1.5f;
	private int[] thresholds = {-3, 0, 3, 8};
//	private float moveSensitivity = 10;

	public final static String EXTRA_MESSAGE = "com.azman.MotionVisualizer.SERVICE";
	
	
	
	protected SensorManager mSensor;
	protected Sensor sSensor;
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		
		
		Log.i(EXTRA_MESSAGE,"onCreate");
    	mSensor = (SensorManager) getSystemService(SENSOR_SERVICE);
    	sSensor = mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	mSensor.registerListener(this, sSensor,SensorManager.SENSOR_DELAY_UI);
    	
//    	sSensor = mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//    	mSensor.registerListener(this, sSensor,SensorManager.SENSOR_DELAY_NORMAL);
    	
 //   	sSensor = mSensor.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
  //  	mSensor.registerListener(this, sSensor,SensorManager.SENSOR_DELAY_UI);


	}

	public class LocalBinder extends Binder{
		backgroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return backgroundService.this;
        }
	}
	public LocalBinder mBinder = new LocalBinder();
	private MainActivity mActivity = null;
	private File file;
	


	
	public void subscribe(MainActivity a)
	{ 
		Log.i(EXTRA_MESSAGE,"Subscribed");
		mActivity = a;
		File root = android.os.Environment.getExternalStorageDirectory(); 
	    File dir = new File (root.getAbsolutePath() + "/asd");
	    dir.mkdirs();
	    DateFormat s = SimpleDateFormat.getDateTimeInstance();
	    file = new File(dir,  s.format(new Date()) +".csv");}
	public void unSubscribe()
	{
		Log.i(EXTRA_MESSAGE,"unSubscribed");
		mActivity = null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(EXTRA_MESSAGE,"onDestroy");
		isRunning = false;
		
	}

	@Override
	public synchronized int onStartCommand(Intent intent, int flags, int startId) {
		
		
		Log.i(EXTRA_MESSAGE,"onStartCommand");
		Toast.makeText(getApplicationContext(), "Start Ready", Toast.LENGTH_LONG).show();

		if(isRunning)
		{	Log.e(EXTRA_MESSAGE, "I am already running");

		}

			isRunning = true;





    	return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(EXTRA_MESSAGE, "Binded");
		return mBinder;
	}
	

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO will this part implemented?
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		//Log.i(EXTRA_MESSAGE, "Sensor data arrived");
		float[] e = new float[3];
		if(mActivity != null)
		{
			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
			{	


						float varx, vary, varz;
						if(ptrBuffer == buffer[0].length) ptrBuffer = 0;
						// We want our data to be between 0 and + 360
						buffer[0][ptrBuffer] = (event.values[0] - calibrationValues[0]) ;//buffer[0][ptrBuffer] = event.values[0];
						buffer[1][ptrBuffer] = (event.values[1] - calibrationValues[1]);//buffer[1][ptrBuffer] = event.values[1];
						buffer[2][ptrBuffer++] = (event.values[2] - calibrationValues[2]);//buffer[2][ptrBuffer++] = event.values[2];
//						varx = angularVariance(buffer[0]);
//						vary = angularVariance(buffer[1]);
//						varz = angularVariance(buffer[2]);
						writeToSDFile("accelerometer," +Long.toString(System.currentTimeMillis())+ ","+ Float.toString(buffer[0][ptrBuffer-1]) +","+Float.toString(buffer[1][ptrBuffer-1]) +","+Float.toString(buffer[2][ptrBuffer-1]));
						if(isInCalibration)
							mActivity.mHandler.obtainMessage(-1).sendToTarget();
						else{
//						else if(varx+vary+varz > moveSensitivity)
//						{	
//
//							mActivity.mHandler.obtainMessage(0).sendToTarget();
//							
//							
//						}
//						else
//						{
//							float[] orientation = new float[3];
//							orientation[0] = angularMean(buffer[0]);
//							orientation[1] = angularMean(buffer[1]);
//							orientation[2] = angularMean(buffer[2]);
							float mean = angularMean(buffer[1]);
							// my values are between 0,360 I need to make them between -180, + 180
							//if(mean>180) mean -=360;
							
							if( Math.abs(mean - thresholds[0]) <sensitivity ) 
								mActivity.mHandler.obtainMessage(0).sendToTarget();
							else if( Math.abs(mean - thresholds[1]) <sensitivity ) 
								mActivity.mHandler.obtainMessage(1).sendToTarget();
							else if( Math.abs(mean - thresholds[2]) <sensitivity ) 
								mActivity.mHandler.obtainMessage(2).sendToTarget();
							else if( Math.abs(mean - thresholds[3]) <sensitivity ) 
								mActivity.mHandler.obtainMessage(3).sendToTarget();
							else 
								mActivity.mHandler.obtainMessage(4).sendToTarget();

						}
					
					

			}
		}

		//Log.i("ANGULAR MEAN",Float.toString(angularVariance(buffer[0])));

		
	}
	
	  private void writeToSDFile(String s){




		    try {
		        FileOutputStream f = new FileOutputStream(file,true);
		        PrintWriter pw = new PrintWriter(f,true);
		        pw.println(s);
		        pw.close();
		        f.close();
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    }   

		}
	
	
	public void calibrate()
	{
			isInCalibration = true;
			calibrationValues[0] = 0;calibrationValues[1] = 0;calibrationValues[2] = 0;
			
				
			Timer timer = new Timer();
			class getTheCalibrationTask extends TimerTask {
				   public void run() {
						calibrationValues[0] = angularMean(buffer[0]);
						calibrationValues[1] = angularMean(buffer[1]);
						calibrationValues[2] = angularMean(buffer[2]);
						isInCalibration = false;
				   }
				}
			TimerTask getTheCalibration = new getTheCalibrationTask();
			timer.schedule(getTheCalibration, 3000);

	}

	private float angularMean(float[] buf)
	{
		float retVal = buf[0],diff = 0;
		for(int i=1;i<buf.length;i++)
		{
			diff = angularDifference(buf[i],retVal);
			retVal = (diff/(i+1) + retVal +360) % 360;

		}
		return retVal;
		
	}
	private float angularVariance(float[] buf)
	{
		float mean = angularMean(buf), diff = 0;
		float temp = 0;
		for(int i=0 ; i <buf.length; i++)
		{
			// there is a better way of doing this but I think mod operation takes more than comparison
			diff = angularDifference(buf[i],mean);
        	temp += diff*diff;
		}
        return temp/buf.length;
	}
	
	private float angularDifference(float ang1,float ang2)
	{
		float diff = ang1 - ang2;
		return (diff >180)?diff-360: diff < -180 ?diff+360 : diff;

	}
	public void setRollSensitivity(float newSensitivity) {
		sensitivity = newSensitivity;
		
	}
	public void setMoveSensitivity(float newSensitivity) {
		
//		moveSensitivity = newSensitivity;
	}
	


}
