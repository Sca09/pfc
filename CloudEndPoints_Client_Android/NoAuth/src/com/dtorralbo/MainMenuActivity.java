package com.dtorralbo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainMenuActivity extends Activity implements  View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		findViewById(R.id.list_notes).setOnClickListener(this);
		findViewById(R.id.new_note).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.list_notes:
			Intent listNotesIntent = new Intent(this, ListNotesActivity.class);
			startActivity(listNotesIntent);
			break;

		case R.id.new_note:
			Intent newNoteIntent = new Intent(this, NewNoteActivity.class);
			startActivity(newNoteIntent);
			break;
		}
	}
}
