package cs4720.asparagus.assasinmobile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LogInActivity extends Activity {
	public final static String USERNAME = "cs4720.asparagus.assasinmobile.USERNAME";
	public final static String USERID = "cs4720.asparagus.assasinmobile.USERID";
	
	String webserviceURL = "http://plato.cs.virginia.edu/~cs4720f13asparagus/users/view_by_name/";
	String webserviceGetUsers = "http://plato.cs.virginia.edu/~cs4720f13asparagus/users.json";
	
	int user_id;
	String username;
	String password;
	Intent intent;
	Intent signupIntent;
	
	ArrayList<User> values;
	TextView message;
	
	Boolean userExists = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		message = (TextView) findViewById(R.id.loginmessage);
		values = new ArrayList<User>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}
	
	private boolean isNetworkAvailable(){
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public void signUp(View view){
		signupIntent = new Intent(this, SignUpActivity.class);
		startActivity(signupIntent);
	}
	
	public void logIn(View view){
		intent = new Intent(this, ViewGamesActivity.class);

		EditText editText = (EditText) findViewById(R.id.username_field);
		EditText passwordField = (EditText) findViewById(R.id.password_field);
		
		username = editText.getText().toString();
		password = passwordField.getText().toString();
		

		intent.putExtra(USERNAME, username);
		
		if(isNetworkAvailable()){
			new GetUsersTask().execute(webserviceGetUsers);
		}
		else{
			message.setText("No internet connection. Please connect before continueing");
		}
	}
	
	private class GetUsersTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			InputStream is = null;
			String result = "";
			ArrayList<User> lcs = new ArrayList<User>();
			
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}catch (Exception e){
				Log.e("AssasinMobile", "Error in http connection " + e.toString());
			}
			
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while((line = reader.readLine())!= null){
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			}catch (Exception e){
				Log.e("AssasinMobile", "Error converting result " + e.toString());
			}
			
			try{
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonArray Jarray = parser.parse(result).getAsJsonArray();
				
				for(JsonElement obj: Jarray){
					JsonObject Jobject = obj.getAsJsonObject();
					JsonElement thing = Jobject.get("User");
					User user = gson.fromJson(thing, User.class);
					lcs.add(user);
				}
			
				values.clear();
				values.addAll(lcs);
				
			}catch (Exception e){
				Log.e("AssasinMobile", "JSONParse" + e.toString());
			}

			return "Done!";
		}

		@Override
		protected void onProgressUpdate(Integer... ints) {

		}

		@Override
		protected void onPostExecute(String result) {
			for(User user:values){
				if(user.getName().equals(username) && user.getPassword().equals(password)){
					userExists = true;
					break;
				}
			}
			
			if(!userExists){
				message.setText("Invalid Username/Password. Please try again");
				
			}else {
				//DO THE ACTUAL POSTING HERE
				String url = webserviceURL + username+".json";
				new GetGamesTask().execute(url);
			}
		}
	}
	
	private class GetGamesTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			InputStream is = null;
			String result = "";
			
			
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}catch (Exception e){
				Log.e("AssasinMobile", "Error in http connection " + e.toString());
			}
			
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while((line = reader.readLine())!= null){
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			}catch (Exception e){
				Log.e("AssasinMobile", "Error converting result " + e.toString());
			}
			
			try{
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonArray Jarray = parser.parse(result).getAsJsonArray();
				JsonElement obj = Jarray.get(0);
				JsonObject Jobject = obj.getAsJsonObject();
				JsonElement thing = Jobject.get("User");
				User user = gson.fromJson(thing, User.class);
				user_id = user.getId();

			}catch (Exception e){
				Log.e("AssasinMobile", "JSONParse" + e.toString());
			}

			return "Done!";
		}

		@Override
		protected void onProgressUpdate(Integer... ints) {

		}

		@Override
		protected void onPostExecute(String result) {

			intent.putExtra(USERID, user_id);
			startActivity(intent);
		}
	}

}
