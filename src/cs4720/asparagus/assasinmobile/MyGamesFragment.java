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

import cs4720.asparagus.assasinmobile.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class MyGamesFragment extends Fragment{
	public final static String GAMEID = "cs4720.asparagus.assasinmobile.GAMEID";
	public final static String USERID = "cs4720.asparagus.assasinmobile.USERID";
	public final static String GAMESTATUS = "cs4720.asparagus.assasinmobile.GAMESTATUS";
	ListView myGamesList;
	String webserviceURL = "http://plato.cs.virginia.edu/~cs4720f13asparagus/users/view_games_by_name/";
	ArrayList<Game> values;
	ArrayAdapter<Game> adapter;
	Button refresh;
	Button twitter;
	Button logout;
	
	String url;
	Intent twitterIntent; 
	Intent logoutIntent;
	
	int user_id;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_my_games, container, false);
	
		Intent intent = getActivity().getIntent();
		String message = intent.getStringExtra(LogInActivity.USERNAME);
		user_id = intent.getIntExtra(LogInActivity.USERID, 0);

		myGamesList = (ListView) rootView.findViewById(R.id.myGamesList);
		refresh = (Button) rootView.findViewById(R.id.refresh_button);
		twitter = (Button) rootView.findViewById(R.id.twitter_feed);
		logout = (Button) rootView.findViewById(R.id.logoutbutton);
		
		values = new ArrayList<Game>();
		url = webserviceURL+message+".json";
		adapter = new ArrayAdapter<Game> (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values);
		myGamesList.setAdapter(adapter);
		new GetGamesTask().execute(url);
		
		twitter.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				twitterIntent = new Intent(getActivity(), TwitterActivity.class);
				startActivity(twitterIntent);
			}
			
		});
		
		refresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new GetGamesTask().execute(url);
			}
			
		});
		
		logout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logoutIntent = new Intent(getActivity(), LogInActivity.class);
				startActivity(logoutIntent);
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
					Game cse = gson.fromJson(obj, Game.class);
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
			myGamesList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), PlayerActivity.class);
					intent.putExtra(GAMEID,adapter.getItem(arg2).getId());
					intent.putExtra(USERID, user_id);
					intent.putExtra(GAMESTATUS, adapter.getItem(arg2).isGame_started());
					startActivity(intent);
				}
				
			});

		}
	}
}
