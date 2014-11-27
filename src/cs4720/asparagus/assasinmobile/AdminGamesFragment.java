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

import cs4720.asparagus.assasinmobile.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AdminGamesFragment extends Fragment{
	public final static String GAMEID = "cs4720.asparagus.assasinmobile.GAMEID";
	ListView joinGamesList;
	String webserviceURL = "http://plato.cs.virginia.edu/~cs4720f13asparagus/games/view_games_by_admin_id/";
	ArrayList<Game> values;
	ArrayAdapter<Game> adapter;
	String test = "Games are loading";
	TextView textView;
	int user_id;
	String url;
	Button refresh;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_admin_games, container, false);
		joinGamesList = (ListView) rootView.findViewById(R.id.joinGamesList);
		values = new ArrayList<Game>();
		Intent login = getActivity().getIntent();
		int user_id = login.getIntExtra(LogInActivity.USERID, 0);
		url = webserviceURL + Integer.toString(user_id) + ".json";
		new GetGamesTask().execute(url);
		
		refresh = (Button) rootView.findViewById(R.id.button1);
		refresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new GetGamesTask().execute(url);
			}
			
		});
		
		return rootView;
	}


	private class GetGamesTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			ArrayList<Game> lcs = new ArrayList<Game>();
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
					JsonObject Jobject = obj.getAsJsonObject();
					JsonElement thing = Jobject.get("Game");
					Game cse = gson.fromJson(thing, Game.class);
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
			
			adapter = new ArrayAdapter<Game> (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values);
			joinGamesList.setAdapter(adapter);
			
			joinGamesList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					boolean started = adapter.getItem(arg2).isGame_started();
					
					Intent intent;
					if(started){
						intent = new Intent(getActivity(), GameAdminActivity.class);
					}
					else{
						intent = new Intent(getActivity(), AddPlayersActivity.class);
					}
				
					intent.putExtra(GAMEID,adapter.getItem(arg2).getId());
					startActivity(intent);
				}
				
			});
		}
	}
}
