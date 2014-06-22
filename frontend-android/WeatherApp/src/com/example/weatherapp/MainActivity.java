package com.example.weatherapp;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;





import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {



	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions","publish_stream");

	private MainActivity activity;
	private JSONObject objJSON = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_main);
		makeInvisible();
		TextView temp =(TextView) findViewById(R.id.textView6);
		temp.setOnClickListener(new FacebookCurrentWeatherListner());
		temp =(TextView) findViewById(R.id.textView7);
		temp.setOnClickListener(new FacebookForecastListner());
	}


	public void validate(View clickedButton) {
		validateInput();

	}

	public void postCurrentWeather(View clickedButton) {

	}

	private void showMessage(String msg){
		Toast tempMessage = Toast.makeText(this, msg,
				Toast.LENGTH_SHORT);
		tempMessage.show();
	}

	private void validateInput(){
		EditText editText = null;
		String input = null;
		final String zipcode_pattern = ".*[0-9]+.*";
		final String city_pattern1 = "(^[A-Za-z '.]+), ([A-Z][A-Z])$";
		final String city_pattern2 = "(^[A-Za-z '.]+), ([A-Z][A-Z][A-Z])$";
		final String city_pattern3 = "(^[A-Za-z '.]+), ([A-Z][A-Z]), ([A-Z][A-Z][A-Z])$";
		try{
			editText = (EditText) this.findViewById(R.id.editText1);
			input = editText.getEditableText().toString();

			if(input.matches(zipcode_pattern)){
				if(input.length() !=5){
					showMessage("Please enter a valid zip code, must be 5 digits");
					makeInvisible();
				}
				else{
					try{
						Integer.parseInt(input);
						//valid zip code
						makeRequest(input,"zip");
					}
					catch(NumberFormatException ex){
						showMessage("Please enter a valid zip code, must be 5 digits");
						makeInvisible();
					}
				}
			}
			else{
				if(input.matches(city_pattern1) || input.matches(city_pattern2) || input.matches(city_pattern3)){
					//valid city
					makeRequest(input,"city");
				}
				else{
					showMessage("Please enter a valid location, must include state and country seperated by comma");
					makeInvisible();
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void makeRequest(String location,String locationType ){
		char temperatureUnit = 'f';
		String url="http://cs-server.usc.edu:34067/examples/servlet/weathersearch";
		try{
			temperatureUnit = fetchTemperatureUnit();
			url = url +"?location="+URLEncoder.encode(location, "UTF-8")+"&location_type="+locationType+"&temperature_unit="+temperatureUnit;
			System.out.println("URL to hit:" + url);
			FetchRequest obj = new FetchRequest();
			obj.execute(url);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private char fetchTemperatureUnit(){
		char temp ='f';
		RadioGroup radioGroup =(RadioGroup) findViewById(R.id.radioTemp);
		RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
		System.out.println("radio output" + radioButton.getText());
		if(radioButton.getText().equals("\u2109")){
			temp='f';
		}
		else{
			temp='c';
		}
		return temp;
	}

	private void parseResponseAndUpdateWidgets(String jsonString){

		try{
			objJSON = new JSONObject(jsonString);
			JSONObject jsonWeather = objJSON.getJSONObject("weather");
			JSONArray jsonForecast = jsonWeather.getJSONArray("forecast");
			String html = null;
			if(jsonForecast.length() !=0){
			html = "<html> " +
					"<body style='margin:0; padding:0;'>" +
					"<table border='1' style='width:100%;border-collapse:collapse;border-color:#D4DADB;'>" +
					"<tr bgcolor='#C3CACB'><td>Day</td><td>Weather</td><td>High</td><td>Low</td></tr>" +
					"<tr><td>" + jsonForecast.getJSONObject(0).getString("day") +"</td><td>" + jsonForecast.getJSONObject(0).getString("text") +"</td><td style='color:orange;'>" + jsonForecast.getJSONObject(0).getString("high") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td><td style='color:#1E90FF;'>" + jsonForecast.getJSONObject(0).getString("low") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td></tr>" +
					"<tr bgcolor='#C9F3F8'><td>" + jsonForecast.getJSONObject(1).getString("day") +"</td><td>" + jsonForecast.getJSONObject(1).getString("text") +"</td><td style='color:orange;'>" + jsonForecast.getJSONObject(1).getString("high") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td><td style='color:#1E90FF;'>" + jsonForecast.getJSONObject(1).getString("low") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td></tr>" +
					"<tr><td>" + jsonForecast.getJSONObject(2).getString("day") +"</td><td>" + jsonForecast.getJSONObject(2).getString("text") +"</td><td style='color:orange;'>" + jsonForecast.getJSONObject(2).getString("high") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td><td style='color:#1E90FF;'>" + jsonForecast.getJSONObject(2).getString("low") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td></tr>" +
					"<tr bgcolor='#C9F3F8'><td>" + jsonForecast.getJSONObject(3).getString("day") +"</td><td>" + jsonForecast.getJSONObject(3).getString("text") +"</td><td style='color:orange;'>" + jsonForecast.getJSONObject(3).getString("high") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td><td style='color:#1E90FF;'>" + jsonForecast.getJSONObject(3).getString("low") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td></tr>" +
					"<tr><td>" + jsonForecast.getJSONObject(4).getString("day") +"</td><td>" + jsonForecast.getJSONObject(4).getString("text") +"</td><td style='color:orange;'>" + jsonForecast.getJSONObject(4).getString("high") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td><td style='color:#1E90FF;'>" + jsonForecast.getJSONObject(4).getString("low") +"&deg;"+ jsonWeather.getJSONObject("units").getString("temperature")+"</td></tr>" +
					"</table></body></html>";
			WebView wv = (WebView)this.findViewById(R.id.webView1);
			wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
			wv.setVisibility(View.VISIBLE);

			TextView textView = (TextView)this.findViewById(R.id.textView1);
			textView.setText(jsonWeather.getJSONObject("location").getString("city"));
			textView.setVisibility(View.VISIBLE);

			textView = (TextView)this.findViewById(R.id.textView2);
			textView.setText(jsonWeather.getJSONObject("location").getString("region") + ", " + jsonWeather.getJSONObject("location").getString("country"));
			textView.setVisibility(View.VISIBLE);

			textView = (TextView)this.findViewById(R.id.textView3);
			textView.setText(jsonWeather.getJSONObject("condition").getString("text"));
			textView.setVisibility(View.VISIBLE);

			String temperatureType = null;
			if(jsonWeather.getJSONObject("units").getString("temperature").equals("F")){
				temperatureType="\u2109";
			}
			else{
				temperatureType="\u2103";
			}

			textView = (TextView)this.findViewById(R.id.textView4);
			textView.setText(jsonWeather.getJSONObject("condition").getString("temp") + temperatureType);
			textView.setVisibility(View.VISIBLE);

			textView = (TextView)this.findViewById(R.id.textView5);
			textView.setVisibility(View.VISIBLE);

			textView = (TextView)this.findViewById(R.id.textView6);
			textView.setVisibility(View.VISIBLE);

			textView = (TextView)this.findViewById(R.id.textView7);
			textView.setVisibility(View.VISIBLE);

			FetchImage obj = new FetchImage();
			obj.execute(jsonWeather.getString("img"));
			}
			else{
				makeInvisible();
				TextView textView = (TextView)this.findViewById(R.id.textView1);
				textView.setText("No results found");
				textView.setVisibility(View.VISIBLE);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void makeInvisible(){
		TextView textView = (TextView)this.findViewById(R.id.textView1);
		textView.setVisibility(View.INVISIBLE);

		textView = (TextView)this.findViewById(R.id.textView2);
		textView.setVisibility(View.INVISIBLE);

		textView = (TextView)this.findViewById(R.id.textView3);
		textView.setVisibility(View.INVISIBLE);

		textView = (TextView)this.findViewById(R.id.textView4);
		textView.setVisibility(View.INVISIBLE);

		textView = (TextView)this.findViewById(R.id.textView5);
		textView.setVisibility(View.INVISIBLE);

		textView = (TextView)this.findViewById(R.id.textView6);
		textView.setVisibility(View.INVISIBLE);

		textView = (TextView)this.findViewById(R.id.textView7);
		textView.setVisibility(View.INVISIBLE);

		ImageView imageView = (ImageView)this.findViewById(R.id.imageView1);
		imageView.setVisibility(View.INVISIBLE);

		WebView wv = (WebView)this.findViewById(R.id.webView1);
		wv.setVisibility(View.INVISIBLE);
	}

	private void setImage(Bitmap bitmap){
		ImageView imageView = (ImageView)this.findViewById(R.id.imageView1);
		imageView.setImageBitmap(bitmap);
		imageView.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	class FetchImage extends AsyncTask<String,Void,Bitmap>{

		@Override
		protected Bitmap doInBackground(String... arg0) {
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(arg0[0]).getContent());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			setImage(result);
		}

	}

	class FetchRequest extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... arg0) {
			String response = null;
			try {
				URL obj = new URL(arg0[0]);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				int responseCode = con.getResponseCode();
				System.out.println("Response Code : " + responseCode);
				InputStream objInputStream = con.getInputStream();
				int num = objInputStream.available();
				System.out.println("Number of bytes available to read : " + num);
				byte[] outputByte = new byte[num];
				objInputStream.read(outputByte, 0, num);
				response  = new String(outputByte);
				System.out.println("Response : " + response);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return response;
		}
		@Override
		protected void onPostExecute(String result) {
			parseResponseAndUpdateWidgets(result);
		}

	}
	class FacebookCurrentWeatherListner implements OnClickListener{

		@Override
		public void onClick(View v) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					activity);
			alertDialogBuilder.setTitle("Post to Facebook");

			alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Post Current Weather",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					System.out.println("before opening session");
					Session.openActiveSession(activity, true, new Session.StatusCallback(){

						@Override
						public void call(Session session, SessionState state,
								Exception exception) {
							System.out.println("inside call back " + session.getState().toString());
							if(exception != null){
								System.out.println("Inside exception");
								exception.printStackTrace();
							}
							if (state.isOpened()) {
								publishWeatherOnFacebook();
							}
						}
					});	
				}
			})
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.show();

		}

		private void publishWeatherOnFacebook() {
			System.out.println("inside on click");
			Session session = Session.getActiveSession();

			if (session != null) {
				List<String> permissions = session.getPermissions();
				if (!isSubsetOf(PERMISSIONS, permissions)) {
					Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, PERMISSIONS);
					session.requestNewPublishPermissions(newPermissionsRequest);
					return;
				}

				JSONObject jsonWeather = null;
				Bundle postParams = null;
				try {
					jsonWeather = objJSON.getJSONObject("weather");


					System.out.println("Session is not null");
					postParams = new Bundle();
					postParams.putString("name", jsonWeather.getJSONObject("location").getString("city") + ", " + jsonWeather.getJSONObject("location").getString("region") + ", " + jsonWeather.getJSONObject("location").getString("country"));
					postParams.putString("caption","The current condition for " + jsonWeather.getJSONObject("location").getString("city") +" is " + jsonWeather.getJSONObject("condition").getString("text"));
					postParams.putString("description","Temperature is " + jsonWeather.getJSONObject("condition").getString("temp") + "&deg;"+ jsonWeather.getJSONObject("units").getString("temperature"));
					postParams.putString("link",jsonWeather.getString("link"));
					postParams.putString("picture",jsonWeather.getString("img"));
					JSONObject temp = new JSONObject();
					JSONObject temp2 = new JSONObject();
					temp2.put("text", "here");
					temp2.put("href", jsonWeather.getString("link"));
					temp.put("Look at details", temp2);
					postParams.putString("properties", temp.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				WebDialog.FeedDialogBuilder builder = new WebDialog.FeedDialogBuilder(activity,Session.getActiveSession(),postParams);

				builder.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values, FacebookException error) {

						if (error == null) {
							final String postId = values.getString("post_id");
							if (postId != null) {
								showMessage("Posting Success, id: "+postId);
							} else {
								showMessage("Publish cancelled");
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							showMessage("Publish cancelled");
						} else {
							showMessage("Error posting story");
						}


					}
				});

				WebDialog feedDialog = builder.build();
				feedDialog.show();
			}
		}		

	}

	class FacebookForecastListner implements OnClickListener{

		@Override
		public void onClick(View v) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					activity);

			alertDialogBuilder.setTitle("Post to Facebook");

			alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Post Weather Forecast",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					System.out.println("before opening session");
					Session.openActiveSession(activity, true, new Session.StatusCallback(){

						@Override
						public void call(Session session, SessionState state,
								Exception exception) {
							System.out.println("inside call back " + session.getState().toString());
							if(exception != null){
								System.out.println("Inside exception");
								exception.printStackTrace();
							}
							if (state.isOpened()) {
								publishForecastOnFacebook();
							}
						}
					});	
				}
			})
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

		}

		private void publishForecastOnFacebook() {
			System.out.println("inside on click");
			Session session = Session.getActiveSession();

			if (session != null) {
				List<String> permissions = session.getPermissions();
				if (!isSubsetOf(PERMISSIONS, permissions)) {
					Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, PERMISSIONS);
					session.requestNewPublishPermissions(newPermissionsRequest);
					return;
				}


				Bundle postParams = null;
				try {
					JSONObject jsonWeather = objJSON.getJSONObject("weather");
					JSONArray jsonForecast = jsonWeather.getJSONArray("forecast");
					String description = jsonForecast.getJSONObject(0).getString("day") + ": " + jsonForecast.getJSONObject(0).getString("text") + ", " + jsonForecast.getJSONObject(0).getString("high") + "/" + jsonForecast.getJSONObject(0).getString("low") + "&deg;" + jsonWeather.getJSONObject("units").getString("temperature") + "; ";
					description = description + jsonForecast.getJSONObject(1).getString("day") + ": " + jsonForecast.getJSONObject(1).getString("text") + ", " + jsonForecast.getJSONObject(1).getString("high") + "/" + jsonForecast.getJSONObject(1).getString("low") + "&deg;" + jsonWeather.getJSONObject("units").getString("temperature") + "; ";
					description = description + jsonForecast.getJSONObject(2).getString("day") + ": " + jsonForecast.getJSONObject(2).getString("text") + ", " + jsonForecast.getJSONObject(2).getString("high") + "/" + jsonForecast.getJSONObject(2).getString("low") + "&deg;" + jsonWeather.getJSONObject("units").getString("temperature") + "; ";
					description = description + jsonForecast.getJSONObject(3).getString("day") + ": " + jsonForecast.getJSONObject(3).getString("text") + ", " + jsonForecast.getJSONObject(3).getString("high") + "/" + jsonForecast.getJSONObject(3).getString("low") + "&deg;" + jsonWeather.getJSONObject("units").getString("temperature") + "; ";
					description = description + jsonForecast.getJSONObject(4).getString("day") + ": " + jsonForecast.getJSONObject(4).getString("text") + ", " + jsonForecast.getJSONObject(4).getString("high") + "/" + jsonForecast.getJSONObject(4).getString("low") + "&deg;" + jsonWeather.getJSONObject("units").getString("temperature") + ";";
					System.out.println("Session is not null");
					postParams = new Bundle();
					postParams.putString("name", jsonWeather.getJSONObject("location").getString("city") + ", " + jsonWeather.getJSONObject("location").getString("region") + ", " + jsonWeather.getJSONObject("location").getString("country"));
					postParams.putString("caption","Weather Forecast for " + jsonWeather.getJSONObject("location").getString("city"));
					postParams.putString("description",description);
					postParams.putString("link",jsonWeather.getString("link"));
					postParams.putString("picture","http://cs-server.usc.edu:34067/weather.jpg");
					JSONObject temp = new JSONObject();
					JSONObject temp2 = new JSONObject();
					temp2.put("text", "here");
					temp2.put("href", jsonWeather.getString("link"));
					temp.put("Look at details", temp2);
					postParams.putString("properties", temp.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				WebDialog.FeedDialogBuilder builder = new WebDialog.FeedDialogBuilder(activity,Session.getActiveSession(),postParams);

				builder.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values, FacebookException error) {

						if (error == null) {
							final String postId = values.getString("post_id");
							if (postId != null) {
								showMessage("Posting Success, id: "+postId);
							} else {
								showMessage("Publish cancelled");
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							showMessage("Publish cancelled");
						} else {
							showMessage("Error posting story");
						}


					}
				});

				WebDialog feedDialog = builder.build();
				feedDialog.show();
			}
		}		

	}


	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}



}


