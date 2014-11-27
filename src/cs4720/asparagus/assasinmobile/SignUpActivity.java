package cs4720.asparagus.assasinmobile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends Activity {

	String webserviceSIGNUP = "http://plato.cs.virginia.edu/~cs4720f13asparagus/users/add";
	String webserviceGetUsers = "http://plato.cs.virginia.edu/~cs4720f13asparagus/users.json";
	ArrayList<User> values;
	
	String username;
	String password;
	boolean usernameTaken = false;
	
	TextView message;
	Intent loginIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		message = (TextView) findViewById(R.id.signupmessage);
		loginIntent = new Intent(this, LogInActivity.class);
		values = new ArrayList<User>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}
	
	public void submit(View view){
		EditText usernameField = (EditText) findViewById(R.id.username_field);
		EditText passwordField = (EditText) findViewById(R.id.password_field);
		username = usernameField.getText().toString();
		password = passwordField.getText().toString();
		new GetUsersTask().execute(webserviceGetUsers);
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
				if(user.getName().equals(username)){
					usernameTaken = true;
					break;
				}
				usernameTaken = false;
			}
			
			if(usernameTaken){
				message.setText("Username has already been taken. Please enter a different username");
			}else {
				//DO THE ACTUAL POSTING HERE
				new SignUpTask().execute(webserviceSIGNUP, username, password);
			}
		}
	}

	private class SignUpTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String username = params[1];
			String password = params[2];

			
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("name", username));
				pairs.add(new BasicNameValuePair("password", password));
				httppost.setEntity(new UrlEncodedFormEntity(pairs));
				httpclient.execute(httppost);
				
			}catch (Exception e){
				Log.e("AssasinMobile", "Error in http connection " + e.toString());
			}
			
			return "Done!";
		}

		@Override
		protected void onProgressUpdate(Integer... ints) {

		}

		@Override
		protected void onPostExecute(String result) {
			startActivity(loginIntent);
		}
	}


}
