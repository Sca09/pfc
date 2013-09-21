package com.dtorralbo;

import java.io.IOException;
import java.util.HashMap;

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

public class NoteActivity extends Activity implements View.OnClickListener {
	
	Noteendpoint service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note);
		
		Noteendpoint.Builder builder = new Noteendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(), 
				null);

		service = CloudEndpointUtils.updateBuilder(builder).build();
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) getIntent().getSerializableExtra("noteSelected");

		String idExtra = map.get("id");
		String mailExtra = map.get("mail");
		String noteExtra = map.get("note");
		
		EditText noteId = (EditText) findViewById(R.id.noteId);
		noteId.setText(idExtra);
		
		EditText noteEmail = (EditText) findViewById(R.id.noteEmail);
		noteEmail.setText(mailExtra);
		
		EditText note = (EditText) findViewById(R.id.note);
		note.setText(noteExtra);
		
		findViewById(R.id.updateNoteButton).setOnClickListener(this);
		findViewById(R.id.removeNoteButton).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {

		Note note = new Note();
		
		EditText noteId = (EditText) findViewById(R.id.noteId);
		EditText noteEmail = (EditText) findViewById(R.id.noteEmail);
		EditText noteText = (EditText) findViewById(R.id.note);
	
		note.setId(noteId.getText().toString());
		note.setEmailAddress(noteEmail.getText().toString());
		note.setDescription(noteText.getText().toString());
		
		switch (view.getId()) {
		case R.id.updateNoteButton:
			ModifyNoteAsyncTask modifyNoteAsync = new ModifyNoteAsyncTask(this);
			modifyNoteAsync.execute(note);
			
			break;

		case R.id.removeNoteButton:
			RemoveNoteAsyncTask deleteNoteAsync = new RemoveNoteAsyncTask(this);
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
					service.note().update(note).execute();
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
	
	public class RemoveNoteAsyncTask extends AsyncTask<Note, Void, Void> {

		Activity activity;
		
		public RemoveNoteAsyncTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected Void doInBackground(Note... notes) {
			for(Note note : notes) {		
				try {
					// Make the call into Noteendpoint, requesting insertion of the given Note.
					service.note().remove(note.getId()).execute();
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
