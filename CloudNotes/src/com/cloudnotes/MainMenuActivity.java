package com.cloudnotes;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.GooglePlusUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener{

	/** Google+ Sing-in */
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    /** Google+ Sing-in */
    
    static final String PREF_ACCOUNT_NAME = "accountName";
	
	GoogleAccountCredential credential;
	SharedPreferences settings;
	String accountName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		/** Google+ Sing-in **/
		mPlusClient = new PlusClient.Builder(this, this, this)
		.setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
		.build();
		// Progress bar to be displayed if the connection failure is not resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
		
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		/** Google+ Sing-in **/
		
		settings = getSharedPreferences("CloudNoteSample", 0);
		
		credential = GoogleAccountCredential.usingAudience(this, "server:client_id:"+ Ids.WEB_CLIENT_ID);
		
		setAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		findViewById(R.id.list_my_notes).setOnClickListener(this);
		findViewById(R.id.list_notes).setOnClickListener(this);
		findViewById(R.id.new_note).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		/** Google+ Sing-in **/
		mPlusClient.connect();
		/** Google+ Sing-in **/
	}
	
	@Override
    protected void onStop() {
        super.onStop();
        
        /** Google+ Sing-in **/
        // Disconnect, Clear and Remove permissions 
//        if(mPlusClient.isConnected()) {
//        	mPlusClient.clearDefaultAccount();
//        
//
//        	mPlusClient.revokeAccessAndDisconnect(new OnAccessRevokedListener() {
//        		@Override
//        		public void onAccessRevoked(ConnectionResult status) {
//        			// mPlusClient is now disconnected and access has been revoked.
//        			// Trigger app logic to comply with the developer policies
//        		}
//        	});
//        }

        
        // Disconnect and clear user
//        if(mPlusClient.isConnected()) {
//        	mPlusClient.clearDefaultAccount();
//        	mPlusClient.disconnect();
//        }
        
        
        // Disconnect
        if(mPlusClient.isConnected())
        	mPlusClient.disconnect();
        /** Google+ Sing-in **/
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Checks if Google+ app is installed
		int errorCode = GooglePlusUtil.checkGooglePlusApp(this);

		if (errorCode != GooglePlusUtil.SUCCESS) {

			GooglePlusUtil.getErrorDialog(errorCode, this, 0).show();

		} 
		
		// Checks if Google Play Services is installed
		int googlePlayAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		if(googlePlayAvailable > 0) {
			
			GooglePlayServicesUtil.getErrorDialog(googlePlayAvailable, this, requestCode);
			
		} else {
	   
			switch (requestCode) {
			case REQUEST_CODE_RESOLVE_ERR:
				if (resultCode == RESULT_OK) {
			        mConnectionResult = null;
			        mPlusClient.connect();
			    }
				break;
			}
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
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.sign_in_button:
			if (!mPlusClient.isConnected()) {
		        if (mConnectionResult == null) {
		            mConnectionProgressDialog.show();
		        } else {
		            try {
		            	mConnectionProgressDialog.show();
		                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
		            } catch (SendIntentException e) {
		                // Try connecting again.
		                mConnectionResult = null;
		                mPlusClient.connect();
		            }
		        }
		    }
			break;
			
		case R.id.list_notes:
			Intent listNotesIntent = new Intent(this, ListNotesActivity.class);
			startActivity(listNotesIntent);
			break;
		
		case R.id.list_my_notes:
			Intent listMyNotesIntent = new Intent(this, ListMyNotesActivity.class);
			startActivity(listMyNotesIntent);
			break;

		case R.id.new_note:
			Intent newNoteIntent = new Intent(this, NewNoteActivity.class);
			startActivity(newNoteIntent);
			break;

		}	
	}
	
	/** Google+ Sing-in */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
            if (result.hasResolution()) {
            	try {
            		result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            	} catch (SendIntentException e) {
            		mPlusClient.connect();
            	}
            }
		}

		// Save the intent so that we can start an activity when the user clicks
		// the sign-in button.
		mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle arg0) {
		// We've resolved any connection errors.
        if(mPlusClient.isConnected()){
        	final SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        	if(signInButton.getVisibility() == View.VISIBLE)
        		signInButton.setVisibility(View.INVISIBLE);
        	
        	final Button signOutButton = (Button) findViewById(R.id.sign_out_button);
        	
        	if(signOutButton.getVisibility() == View.INVISIBLE)
        		signOutButton.setVisibility(View.VISIBLE);
        	
        	signOutButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mPlusClient.clearDefaultAccount();
					mPlusClient.disconnect();
					mPlusClient.connect();
					
					setAccountName(null);
					
					TextView emailText = (TextView) findViewById(R.id.emailText);
					emailText.setText("");
		        	
		        	if(signInButton.getVisibility() == View.INVISIBLE)
		        		signInButton.setVisibility(View.VISIBLE);
		        	
		        	if(signOutButton.getVisibility() == View.VISIBLE)
		        		signOutButton.setVisibility(View.INVISIBLE);
		        	
				}
			});
        }
        
        if(credential.getSelectedAccountName() == null && mPlusClient.getAccountName() != null) {
        	setAccountName(mPlusClient.getAccountName());
        }
        
        TextView emailText = (TextView) findViewById(R.id.emailText);
		emailText.setText(mPlusClient.getAccountName());
        
		mConnectionProgressDialog.dismiss();
	}

	@Override
	public void onDisconnected() {
		 Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
		
	}
	/** Google+ Sing-in */

}
