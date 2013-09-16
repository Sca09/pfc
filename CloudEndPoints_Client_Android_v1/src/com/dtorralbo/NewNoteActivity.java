package com.dtorralbo;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appspot.cloudendpointsservergae.noteendpoint.Noteendpoint;
import com.appspot.cloudendpointsservergae.noteendpoint.model.Note;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson.JacksonFactory;

public class NewNoteActivity extends Activity implements View.OnClickListener {

	Noteendpoint service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_note);
			
		Noteendpoint.Builder builder = new Noteendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(), 
				null);
		
//		final boolean enableGZip = builder.getRootUrl().startsWith("https:");
//
//	    builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//	      public void initialize(AbstractGoogleClientRequest<?> request)
//	          throws IOException {
//	        if (!enableGZip) {
//	          request.setDisableGZipContent(true);
//	        }
//	      }
//	    });
//		
//		service = builder.setRootUrl("http://10.0.2.2:8888/_ah/api/").build();
		
		service = CloudEndpointUtils.updateBuilder(builder).build();
		
		findViewById(R.id.newNoteButton).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
				
		return true;
	}
	
	@Override
	public void onClick(View view) {
	    switch(view.getId()) {
	    	case R.id.newNoteButton:
	    		Note newNote = new Note();
	    		
    			EditText noteId = (EditText) findViewById(R.id.noteId);
    			EditText noteEmail = (EditText) findViewById(R.id.noteEmail);
    			EditText note = (EditText) findViewById(R.id.note);
    		
    			newNote.setId(noteId.getText().toString());
    			newNote.setDescription(note.getText().toString());
    			newNote.setEmailAddress(noteEmail.getText().toString());

    			NewNoteAsyncTask noteAsync = new NewNoteAsyncTask(this);
    			noteAsync.execute(newNote);
    		
    			noteId.setText("");
    			noteEmail.setText("");
    			note.setText("");
	    	break;
	    }
	}
	
	public class NewNoteAsyncTask extends AsyncTask<Note, Void, Void> {

		Activity activity;
		
		public NewNoteAsyncTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected Void doInBackground(Note... notes) {
			for(Note note : notes) {		
				try {
					service.note().insert(note).execute();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Toast toastAlert = Toast.makeText(activity, "New note added", Toast.LENGTH_SHORT);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();
			
			activity.finish();
		}
	}
}
