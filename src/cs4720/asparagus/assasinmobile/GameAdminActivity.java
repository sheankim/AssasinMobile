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
import android.widget.ListView;

public class GameAdminActivity extends Activity {
	int game_id;
	String webserviceURL = "http://plato.cs.virginia.edu/~cs4720f13asparagus/games/view_players/";
	ArrayList<Player> values;
	ArrayAdapter<Player> adapter;
	ListView gameplayers;
	Button refresh;
	String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_admin);
		Intent intent = getIntent();
		game_id = intent.getIntExtra(AdminGamesFragment.GAMEID, 0);
		gameplayers = (ListView) findViewById(R.id.myGamesStats);
		values = new ArrayList<Player>();
		refresh = (Button) findViewById(R.id.refresh_admin);
		url = webserviceURL + Integer.toString(game_id) + ".json";
		adapter = new ArrayAdapter<Player> (this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		gameplayers.setAdapter(adapter);
		
		new GetGamesTask().execute(url);
		
		refresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new GetGamesTask().execute(url);
			}
			
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.game_admin, menu);
		return true;
	}
	
	private class GetGamesTask extends AsyncTask<String, Integer, String> {
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

}
