package com.dtorralbo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.appspot.cloudendpointsservergae.noteendpoint.Noteendpoint;
import com.appspot.cloudendpointsservergae.noteendpoint.model.CollectionResponseNote;
import com.appspot.cloudendpointsservergae.noteendpoint.model.Note;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson.JacksonFactory;

public class ListNotesActivity extends ListActivity {
	
	private static final int REQUEST_CODE_RESOLVE_ERR_SHOW_NOTE = 5000;
	
	Noteendpoint service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_notes);
		
		Noteendpoint.Builder builder = new Noteendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(), 
				null);
			    
		service = CloudEndpointUtils.updateBuilder(builder).build();
		
		new ListNotesAsyncTask(this).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
	
	public class ListNotesAsyncTask extends AsyncTask<Note, Void, List<Note>> {

		Activity activity;
		
		public ListNotesAsyncTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected List<Note> doInBackground(Note... unused) {
			
			try {
				// Make the call into Noteendpoint
				CollectionResponseNote notesCollection = service.note().list().execute();
				
				List<Note> notes = notesCollection.getItems(); 
				
				return notes;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(List<com.appspot.cloudendpointsservergae.noteendpoint.model.Note> result) {

			if(result != null) {
			
				ListView listview = (ListView) findViewById(android.R.id.list);
				
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				
				for(com.appspot.cloudendpointsservergae.noteendpoint.model.Note note : result) {
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
