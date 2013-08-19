package com.cloudnotes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.cloudnotes.NewNoteActivity.NewNoteAsyncTask;
import com.cloudnotes.noteendpoint.Noteendpoint;
import com.cloudnotes.noteendpoint.model.Note;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson.JacksonFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends Activity implements View.OnClickListener {

static final String PREF_ACCOUNT_NAME = "accountName";

	private static final int REQUEST_CODE_RESOLVE_ERR_SHOW_NOTE = 5000;

	GoogleAccountCredential credential;
	SharedPreferences settings;
	String accountName;
	  
	Noteendpoint endpoint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note);
		
		settings = getSharedPreferences("CloudNoteSample", 0);
		
		credential = GoogleAccountCredential.usingAudience(this, "server:client_id:"+ Ids.WEB_CLIENT_ID);
		
		setAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		Noteendpoint.Builder endpointBuilder = new Noteendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				credential);

		endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) getIntent().getSerializableExtra("noteSelected");

		String idExtra = map.get("id");
		String mailExtra = map.get("mail");
		String noteExtra = map.get("note");
		
		EditText noteId = (EditText) findViewById(R.id.noteId);
		noteId.setText(idExtra);
		
		EditText noteEmail = (EditText) findViewById(R.id.emailText);
		noteEmail.setText(mailExtra);
		
		EditText note = (EditText) findViewById(R.id.note);
		note.setText(noteExtra);
		
		if(credential.getSelectedAccountName() != null && credential.getSelectedAccountName().equalsIgnoreCase(mailExtra)) {
			Button modNoteButton = (Button) findViewById(R.id.modNoteButton);
			modNoteButton.setOnClickListener(this);
			modNoteButton.setVisibility(View.VISIBLE);
			
			Button deleteNoteButton = (Button) findViewById(R.id.deleteNoteButton);
			deleteNoteButton.setOnClickListener(this);
			deleteNoteButton.setVisibility(View.VISIBLE);
			
			note.setFocusable(true);
			note.setFocusableInTouchMode(true);
		}
	}

	private void setAccountName(String accountName) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		credential.setSelectedAccountName(accountName);
		this.accountName = accountName;	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		Note note = new Note();
		
		EditText noteId = (EditText) findViewById(R.id.noteId);
		EditText noteEmail = (EditText) findViewById(R.id.emailText);
		EditText noteText = (EditText) findViewById(R.id.note);
	
		note.setId(noteId.getText().toString());
		note.setEmailAddress(noteEmail.getText().toString());
		note.setDescription(noteText.getText().toString());
		
		switch (view.getId()) {
		case R.id.modNoteButton:
			ModifyNoteAsyncTask modifyNoteAsync = new ModifyNoteAsyncTask(this);
			modifyNoteAsync.execute(note);
			
			break;

		case R.id.deleteNoteButton:
			DeleteNoteAsyncTask deleteNoteAsync = new DeleteNoteAsyncTask(this);
			deleteNoteAsync.execute(note);
			
			break;
		}
		
	}
	
	public class ModifyNoteAsyncTask extends AsyncTask<Note, Void, Void> {

		Activity activity;
		
		public ModifyNoteAsyncTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected Void doInBackground(Note... notes) {
			for(Note note : notes) {		
				try {
					// Make the call into Noteendpoint, requesting insertion of the given Note.
					endpoint.updateNote(note).execute();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast toastAlert = Toast.makeText(activity, "Note modified", Toast.LENGTH_SHORT);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();
			
			activity.finish();
		}
	}
	
	public class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {

		Activity activity;
		
		public DeleteNoteAsyncTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected Void doInBackground(Note... notes) {
			for(Note note : notes) {		
				try {
					// Make the call into Noteendpoint, requesting insertion of the given Note.
					endpoint.removeNote(note.getId()).execute();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Toast toastAlert = Toast.makeText(activity, "Note deleted", Toast.LENGTH_SHORT);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();

			activity.finish();
		}
	}

}
