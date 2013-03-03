package com.gideon.address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddressActivity extends Activity {
    /** Called when the activity is first created. */
	String lat="", lon="", phone_num="";
	String name="", gender="", birthday="", school="", relationship="";
    @Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) AddressActivity.this.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				lat = Double.toString(location.getLatitude());
				lon = Double.toString(location.getLongitude());
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}
			public void onProviderEnabled(String provider) {}
			public void onProviderDisabled(String provider) {}
		};
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		//GetAddress(lat,lon);
		/*try {
		    Thread.sleep(20000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}*/
		setContentView(R.layout.main);

    	//Button btnLocation = (Button)findViewById(R.id.btnLocation);
        Button btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);    
			    phone_num = tMgr.getLine1Number();    
				postData(lat,lon,phone_num);
			}
		});
	
		  
		 
		 
	}
    
    
    public void postData(String la, String lo, String phone_number) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost  httppost = new HttpPost("http://havefun.byethost15.com/index.php?lat="+la+"&long="+lo+"&num="+phone_number);
		
		try {
			// Execute HTTP Post Request
			
			//some code to initialize phone number?
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("Phone_Number",phone_number));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			
			//gets the response from the server turns it into a string from JSON, then parses it into original
			InputStream is = entity.getContent();
			
			//converts JSON into string 
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		                sb.append(line + "\n");
		        }
		        is.close();
		        String result = "";
		        String output = "";
		        String output2 = "";
		        result=sb.toString();
		    

			//parses JSON data
			try{
			JSONArray jArray = new JSONArray(result);
	        for(int i=0;i<jArray.length();i++){
	                JSONObject json_data = jArray.getJSONObject(i);
	                JSONObject json_data2 = jArray.getJSONObject(i);
	                output = output + json_data.getString("Phone_Number") + "\n";
	                Log.i("log_tag","Phone Number: "+json_data.getInt("Phone_Number"));
	                output2 = output2 + json_data2.getString("username") + "\n";
	                Log.i("log_tag","username: "+json_data.getString("username"));
	        }
			}
			catch(JSONException e){
	        Log.e("log_tag", "Error parsing data "+e.toString());
		}
			
		    //Shows the message
			
			if (output == "") {
				
				setContentView(R.layout.sign_up);
				
				Button btnSignUp = (Button)findViewById(R.id.buttonSignUp);
				btnSignUp.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					EditText userName = (EditText)findViewById(R.id.userName);				
					name = userName.getText().toString();
					EditText userGender = (EditText)findViewById(R.id.userGender);				
					gender = userGender.getText().toString();
					EditText userBirthday = (EditText)findViewById(R.id.userBirthday);				
					birthday = userBirthday.getText().toString();
					EditText userSchool = (EditText)findViewById(R.id.userSchool);
					school = userSchool.getText().toString();
					EditText userRelationship = (EditText)findViewById(R.id.userRelationship);				
					relationship = userRelationship.getText().toString();					
					signUp(phone_num, name, birthday, gender, school, relationship);
					
			
				}
			});
			}
			else {
				setContentView(R.layout.pick_opponent);
				TextView tvv = (TextView) findViewById(R.id.txtOpp);
				tvv.setText("Hello " + output2);
				
				
				
				
				
				
				
				
				
			}
			
			
		} catch (ClientProtocolException e) {
			Toast.makeText(this, "Error", 5000).show();
		} catch (IOException e) {
			Toast.makeText(this, "Error", 5000).show();
		}		
	}
  	
	public String GetAddress(String lat, String lon)
	{
		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
		String ret = "";
		try {
			List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
			if(addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
				for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				}
				ret = strReturnedAddress.toString();
			}
			else{
				ret = "No Address returned!";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = "Can't get Address!";
		}
		return ret;
	}

	public void signUp(String phone_number, String uName, String uAge, String uGender, String uSchool, String uRelationship) {
		// Create a new HttpClient and Post Header
	
		
		
		
		// Create a new HttpClient and Post Header
		HttpClient httpclient2 = new DefaultHttpClient();
		HttpPost  httppost2 = new HttpPost("http://havefun.byethost15.com/signup.php?userid=");
		try {
			HttpResponse response = httpclient2.execute(httppost2);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setContentView(R.layout.pick_opponent);

	}
}