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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MyTargetFragment extends Fragment{
	String webserviceURL = "http://plato.cs.virginia.edu/~cs4720f13asparagus/games/view_players/";
	String webserviceKilled = "http://plato.cs.virginia.edu/~cs4720f13asparagus/players/edit/";
	String webserviceGetTarget = "http://plato.cs.virginia.edu/~cs4720f13asparagus/players/view/";
	
	String url2;
	
	int game_id;
	int user_id;
	int player_id;
	int target_id;
	int killer_id;
	
	String target_name;
	TextView textview;
	ArrayList<Player> values2;
	
	int isGameStarted;
	Button killed;
	boolean isAlive;
	
	Button update;
	String text;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_my_target, container, false);
		Intent intent = getActivity().getIntent();
		game_id = intent.getIntExtra(MyGamesFragment.GAMEID, 0);
		user_id = intent.getIntExtra(MyGamesFragment.USERID, 0);
		isGameStarted = intent.getIntExtra(MyGamesFragment.GAMESTATUS, 0);
		textview = (TextView)rootView.findViewById(R.id.textView1);

		values2= new ArrayList<Player>();
	
		url2 = webserviceURL + Integer.toString(game_id) + ".json";
		
	

		new GetPlayersTask().execute(url2);
		
		killed = (Button) rootView.findViewById(R.id.killed_button);
		update = (Button) rootView.findViewById(R.id.update_target);
		
		update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new GetPlayersTask().execute(url2);
			}
			
		});
		
		killed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = webserviceKilled + Integer.toString(player_id);
				String url2 = webserviceKilled + Integer.toString(killer_id);
				new PlayerKilledTask().execute(url);
				new TargetUpdateTask().execute(url2, Integer.toString(killer_id), Integer.toString(target_id));
			}
			
		});
		
		return rootView;
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
			
			values2.clear();
			values2.addAll(lcs);

			
			return "Done!";
		}

		@Override
		protected void onProgressUpdate(Integer... ints) {

		}

		@Override
		protected void onPostExecute(String result) {

			for(Player obj:values2){
				if(obj.getGameId()==game_id && obj.getUserId()==user_id){
					player_id = obj.getPlayerId();
					isAlive = obj.getis_alive();
					killer_id = obj.getKiller();
					break;
				}
				else{
					player_id = 0;
				}
			}
			
			String url = webserviceGetTarget + Integer.toString(player_id) + ".json";
			new GetTargetTask().execute(url);
		}
	}
	
	private class TargetUpdateTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String player_id = params[1];
			String target_id = params[2];
			
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("id", player_id));
				pairs.add(new BasicNameValuePair("target",target_id));
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

	
	
	private class PlayerKilledTask extends AsyncTask<String, Integer, String> {
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
				pairs.add(new BasicNameValuePair("id", Integer.toString(player_id)));
				pairs.add(new BasicNameValuePair("is_alive","0"));
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

	private class GetTargetTask extends AsyncTask<String, Integer, String> {
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
				JsonObject thing = obj.getAsJsonObject();
				JsonElement play = thing.get("Player");
				Player cse = gson.fromJson(play, Player.class);
				target_id = cse.getTarget();
				text = url;
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

			for(Player obj:values2){
				if(obj.getPlayerId() == target_id){
					target_name = obj.getAlias();
				}
			}
			if(!isAlive){
				textview.setText("You have been assasinated!");
			}
			else if(target_id == 0){
				textview.setText("Game has not been started by admin yet");
			}
			else if(target_id == player_id){
				textview.setText("You are the last man standing! You are the winner!");
			}
			else{
			textview.setText("Your target is " + target_name);
			}

		
		}
	}

}
