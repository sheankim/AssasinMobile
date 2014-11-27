package cs4720.asparagus.assasinmobile;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


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
import android.widget.EditText;
import android.widget.Toast;


public class CreateGamesFragment extends Fragment{
	String webserviceURL = "http://plato.cs.virginia.edu/~cs4720f13asparagus/games/add";
	EditText titlefield;
	EditText organizationfield;
	int user_id;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_create_games, container, false);
		
		titlefield = (EditText) rootView.findViewById(R.id.game_title);
		organizationfield = (EditText) rootView.findViewById(R.id.organization_name);
		Intent login = getActivity().getIntent();
		user_id = login.getIntExtra(LogInActivity.USERID, 0);
		return rootView;
	}

	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.create_game).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new GetGamesTask().execute(webserviceURL);
				Toast.makeText(getActivity(), "Game has been created", Toast.LENGTH_LONG).show();
			}	
		});
	}
	
	private class GetGamesTask extends AsyncTask<String, Integer, String> {
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
				pairs.add(new BasicNameValuePair("title",titlefield.getText().toString()));
				pairs.add(new BasicNameValuePair("organization", organizationfield.getText().toString()));
				pairs.add(new BasicNameValuePair("admin_id", Integer.toString(user_id)));
				pairs.add(new BasicNameValuePair("game_started", "0"));
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
			titlefield.setText("");
			organizationfield.setText("");

		}
	}
}
