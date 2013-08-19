package com.cloudnotes;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudnotes.noteendpoint.Noteendpoint;
import com.cloudnotes.noteendpoint.model.Note;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson.JacksonFactory;

public class NewNoteActivity extends Activity implements View.OnClickListener {
	
	static final String PREF_ACCOUNT_NAME = "accountName";
	
	GoogleAccountCredential credential;
	SharedPreferences settings;
	String accountName;
	  
	Noteendpoint endpoint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_note);
		
		settings = getSharedPreferences("CloudNoteSample", 0);
		
		credential = GoogleAccountCredential.usingAudience(this, "server:client_id:"+ Ids.WEB_CLIENT_ID);
		
		setAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		Noteendpoint.Builder endpointBuilder = new Noteendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				credential);

		endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
		
		findViewById(R.id.newNoteButton).setOnClickListener(this);
		
		if(credential.getSelectedAccountName() == null) {
			Toast toastAlert = Toast.makeText(this, "Sign In first", Toast.LENGTH_SHORT);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();
		}
	}

	private void setAccountName(String accountName) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		credential.setSelectedAccountName(accountName);
		this.accountName = accountName;
		
		EditText email = (EditText) findViewById(R.id.emailText);
		
		if(credential.getSelectedAccountName() != null) {	
			email.setText(credential.getSelectedAccountName());
		} else {
			email.setText("");
		}
		
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
	    		if(credential.getSelectedAccountName() == null) {
	    			Toast toastAlert = Toast.makeText(this, "Sign In to save", Toast.LENGTH_SHORT);
	    			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
	    			toastAlert.show();
	    		} else {
	    			Note newNote = new Note();
	    		
	    			EditText noteId = (EditText) findViewById(R.id.noteId);
	    			EditText note = (EditText) findViewById(R.id.note);
	    		
	    			newNote.setId(noteId.getText().toString());
	    			newNote.setDescription(note.getText().toString());
	    			newNote.setEmailAddress(credential.getSelectedAccountName());

	    			NewNoteAsyncTask noteAsync = new NewNoteAsyncTask(this);
	    			noteAsync.execute(newNote);
	    		
	    			noteId.setText("");
	    			note.setText("");
	    		}
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
					// Make the call into Noteendpoint, requesting insertion of the given Note.
					endpoint.insertNote(note).execute();
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
