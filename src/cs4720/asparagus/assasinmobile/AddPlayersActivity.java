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
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AddPlayersActivity extends Activity {
	String webserviceURL = "http://plato.cs.virginia.edu/~cs4720f13asparagus/players/add";
	String webserviceUSER = "http://plato.cs.virginia.edu/~cs4720f13asparagus/users/view_by_name/";
	String webserviceVIEWPLAYER = "http://plato.cs.virginia.edu/~cs4720f13asparagus/games/view_players/";
	String webserviceGAMESTART = "http://cs4720asparagus.appspot.com/targets/";
	String webserviceSTARTCHANGE = "http://plato.cs.virginia.edu/~cs4720f13asparagus/games/edit/";
	String webserviceGETTARGET = "http://cs4720asparagus.appspot.com/games/view_targets/";
	String webserviceEDITPLAYER = "http://plato.cs.virginia.edu/~cs4720f13asparagus/players/edit/";
	String showplayers;
	
	EditText username_field;
	EditText name_field;
	int game_id;
	int user_id;
	Button add;
	Button start;
	ArrayList<Player> values;
	ArrayAdapter<Player> adapter;
	ListView myPlayersList;
	Activity addplayers;
	String changeStart;
	

	ArrayList<Target> targets;
	String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_players);
		Intent adminGames = getIntent();
		game_id = adminGames.getIntExtra(AdminGamesFragment.GAMEID, 0);
		username_field = (EditText)findViewById(R.id.ap_username);
		name_field = (EditText)findViewById(R.id.ap_alias);
		myPlayersList = (ListView)findViewById(R.id.addedplayerslist);
		values = new ArrayList<Player>();
		targets = new ArrayList<Target>();
		addplayers = this;
		
		add = (Button) findViewById(R.id.add_player_button);
		start = (Button) findViewById(R.id.start_game_buton);
		
		adapter = new ArrayAdapter<Player> (this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		showplayers = webserviceVIEWPLAYER + Integer.toString(game_id) + ".json";
		myPlayersList.setAdapter(adapter);
		
		
		url = webserviceGETTARGET + Integer.toString(game_id);
		changeStart = webserviceSTARTCHANGE+Integer.toString(game_id); 
		new GetPlayersTask().execute(showplayers);
		

		
		add.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String useridservice = webserviceUSER + username_field.getText().toString()+".json";
				
				new GetGamesTask().execute(webserviceURL,useridservice);
				new GetPlayersTask().execute(showplayers);
				
			}
			
		});
		
		start.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String serializedPost = serializePost(game_id, values);
				new GameStartTask().execute(webserviceGAMESTART, serializedPost);
				new GameChangeStart().execute(changeStart);
			}
			
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.add_players, menu);
		return true;
	}
	
	public String serializePost(int game_id, ArrayList<Player> players){
		String serializable = Integer.toString(game_id);
		for(Player obj:players){
			serializable = serializable+","+Integer.toString(obj.getPlayerId());
		}
		return serializable;	
	}
	
	private class GetGamesTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String user_url = params[1];
			
			InputStream is = null;
			String result = "";
			
			
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(user_url);
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

			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("game_id",Integer.toString(game_id)));
				pairs.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
				pairs.add(new BasicNameValuePair("alias", name_field.getText().toString()));
				pairs.add(new BasicNameValuePair("is_alive", "1"));
				pairs.add(new BasicNameValuePair("target", "0"));
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
			username_field.setText("");
			name_field.setText("");
			Toast.makeText(getApplicationContext(), "Player added", Toast.LENGTH_LONG).show();
			myPlayersList.setAdapter(adapter);
			
		}
	}
	
	private class GetPlayersTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			ArrayList<Player> lcs = new ArrayList<Player>();
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
				for(JsonElement obj : Jarray){
					Player cse = gson.fromJson(obj, Player.class);
					lcs.add(cse);
				}
			}catch (Exception e){
				Log.e("AssasinMobile", "JSONParse" + e.toString());
			}
			
			values.clear();
			values.addAll(lcs);
					
			return "Done!";
		}

		@Override
		protected void onProgressUpdate(Integer... ints) {

		}

		@Override
		protected void onPostExecute(String result) {
	
			adapter.notifyDataSetChanged();
			
		}
	}
	
	private class GameStartTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String serializedData = params[1];
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("players",serializedData));
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
			Toast.makeText(getApplicationContext(), "Game Has Been Started", Toast.LENGTH_LONG).show();

		}
	}

	private class GameChangeStart extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];

			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("id", Integer.toString(game_id)));
				pairs.add(new BasicNameValuePair("game_started","1"));
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
			new GetTargetTask().execute(url);
			
		}
		
		
	}

	private class GetTargetTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			ArrayList<Target> lcs = new ArrayList<Target>();
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
				JsonArray Jarray = parser.parse(result.trim()).getAsJsonArray();
			
				for(JsonElement obj : Jarray){
					Target cse = gson.fromJson(obj, Target.class);
					lcs.add(cse);
				}
			}catch (Exception e){
				Log.e("AssasinMobile", "JSONParse" + e.toString());
			}
			
			targets.clear();
			targets.addAll(lcs);
			
			return "Done!";
		}

		@Override
		protected void onProgressUpdate(Integer... ints) {

		}

		@Override
		protected void onPostExecute(String result) {
			for(Target obj: targets){
				String player_id = Integer.toString(obj.getPlayerId());
				String target = Integer.toString(obj.getTargetId());
				String url = webserviceEDITPLAYER + player_id;
				String killer_url = webserviceEDITPLAYER + target;
				new AssignTargetTask().execute(url,player_id,target);
				new AssignKillerTask().execute(killer_url,target, player_id);
			}
		}
	}
	
	private class AssignTargetTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String player_id = params[1];
			String target = params[2];
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("id", player_id));
				pairs.add(new BasicNameValuePair("target", target));
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
		

		}
	}

	private class AssignKillerTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String player_id = params[1];
			String killer = params[2];
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("id", player_id));
				pairs.add(new BasicNameValuePair("killer", killer));
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
		

		}
	}
}
