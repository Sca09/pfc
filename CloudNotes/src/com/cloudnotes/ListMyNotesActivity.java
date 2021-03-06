package com.cloudnotes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.cloudnotes.ListNotesActivity.ListNotesAsyncTask;
import com.cloudnotes.noteendpoint.Noteendpoint;
import com.cloudnotes.noteendpoint.model.CollectionResponseNote;
import com.cloudnotes.noteendpoint.model.Note;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson.JacksonFactory;

public class ListMyNotesActivity extends Activity {

	private static final int REQUEST_CODE_RESOLVE_ERR_SHOW_NOTE = 5000;
	
	Noteendpoint endpoint;
	
	GoogleAccountCredential credential;

	SharedPreferences settings;
	String accountName;

	static final String PREF_ACCOUNT_NAME = "accountName";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_notes);
		
		settings = getSharedPreferences("CloudNoteSample", 0);
		
		credential = GoogleAccountCredential.usingAudience(this, "server:client_id:"+ Ids.WEB_CLIENT_ID);
		
		setAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		Noteendpoint.Builder endpointBuilder = new Noteendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				credential);

		endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
		
		if(credential.getSelectedAccountName() == null) {
			Toast toastAlert = Toast.makeText(this, "Sign In first", Toast.LENGTH_SHORT);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();
		} else {
			new ListMyNotesAsyncTask(this).execute();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_notes, menu);

		return true;
	}

	private void setAccountName(String accountName) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		credential.setSelectedAccountName(accountName);
		this.accountName = accountName;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_CODE_RESOLVE_ERR_SHOW_NOTE:
			
			new ListMyNotesAsyncTask(this).execute();
			
			break;
		}
	}
	
	public class ListMyNotesAsyncTask extends AsyncTask<Note, Void, List<Note>> {

		Activity activity;
		
		public ListMyNotesAsyncTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected List<Note> doInBackground(Note... unused) {
			
			try {
				// Make the call into Noteendpoint
				CollectionResponseNote notesCollection = endpoint.listMyNotes().execute();
				
				List<Note> notes = notesCollection.getItems(); 
				
				return notes;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(List<Note> result) {
			if(result != null) {
			
				ListView listview = (ListView) findViewById(android.R.id.list);
				
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				
				for(Note note : result) {
					HashMap<String, String> map = new HashMap<String, String>();
					
					map.put("id", note.getId());
					map.put("mail", note.getEmailAddress());
					map.put("note", note.getDescription());
					
					fillMaps.add(map);
				}
				
			    String[] from = {"id", "mail", "note"};
			    int[] to = {R.id.item1, R.id.item2, R.id.item3};
	
			    SimpleAdapter adapter = new SimpleAdapter(activity, fillMaps, R.layout.grid_item, from, to);
			    listview.setAdapter(adapter);
			    
			    listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						@SuppressWarnings("unchecked")
						HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
						
						Intent NoteIntent = new Intent(activity, NoteActivity.class).putExtra("noteSelected", map);
						
						activity.startActivityForResult(NoteIntent, REQUEST_CODE_RESOLVE_ERR_SHOW_NOTE);
					}
				});
			}
			
		}
	}
}