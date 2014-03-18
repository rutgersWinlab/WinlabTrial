package com.azman.androidsensor;

import java.io.Serializable;

import android.bluetooth.BluetoothDevice;

@SuppressWarnings("serial")
public class ServiceEncapsulator implements Serializable {

	
	//public String message; 
	public ServiceEncapsulator(BluetoothDevice id,String name)
	{
		   this.id = id;
		    this.name = name;
		}

		public BluetoothDevice getId() {
		    return id;
		}
		public void setId(BluetoothDevice id) {
		    this.id = id;
		}
		public String getName() {
		    return this.name;
		}
		public void setName(String name) {
		    this.name = name;
		}

		public BluetoothDevice id;
		private String name;

		}
