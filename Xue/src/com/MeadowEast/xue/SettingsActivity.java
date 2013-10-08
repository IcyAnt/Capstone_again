package com.MeadowEast.xue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Toast;


public class SettingsActivity extends Activity {

	static Context c;
	//static SharedPreferences sp;
	
	static CheckBoxPreference sound_enable_checkboxPref;
	static EditTextPreference ce_decksizePref;
	static EditTextPreference ce_deck_targetPref;
	static EditTextPreference ec_decksizePref;
	static EditTextPreference ec_deck_targetPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
		PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, true);
		c = this;
		//sp = PreferenceManager.getDefaultSharedPreferences(this); 	
	}
	
	public static class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
					
			// set texts correctly
	        onSharedPreferenceChanged(null, "");
	        
	        final SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
	        
	        sound_enable_checkboxPref = (CheckBoxPreference) findPreference("sound_enable_checkbox");
	        ce_decksizePref = (EditTextPreference) findPreference("ce_decksize");
	        ce_deck_targetPref = (EditTextPreference) findPreference("ce_deck_target");
	        ec_decksizePref = (EditTextPreference) findPreference("ec_decksize");
	        ec_deck_targetPref = (EditTextPreference) findPreference("ec_deck_target");
	        
	        Preference cedecksize = findPreference("ce_decksize");
	        cedecksize.setTitle(getString(R.string.title_ce_deck_size) + " : " + sp.getString("ce_decksize",""));
	        cedecksize.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	            //@Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	            	try {
	        			Integer.parseInt((String) newValue);
	        			ce_decksizePref.setText((String) newValue);
	        		} catch(NumberFormatException e){
	        			warn("alert","default");   
	        			ce_decksizePref.setText(getString(R.string.default_CE_decksize));
	        			preference.setTitle(getString(R.string.title_ce_deck_size) + " : " + sp.getString("ce_decksize",""));
	        			return false;
	        		}
	            	if (Integer.parseInt((String) newValue) < Integer.parseInt(getString(R.string.default_min_CE_decksize))){
	            		warn("alert","min");
	            		ce_decksizePref.setText(getString(R.string.default_min_CE_decksize));
	            		preference.setTitle(getString(R.string.title_ce_deck_size) + " : " + sp.getString("ce_decksize",""));
	            		return false;
	            	}
	            	preference.setTitle(getString(R.string.title_ce_deck_size) + " : " + sp.getString("ce_decksize",""));
	                return true;
	            }
	        });
	        
	        Preference cedecktarget = (Preference) findPreference("ce_deck_target");
	        cedecktarget.setTitle(getString(R.string.title_ce_deck_target) + " : " + sp.getString("ce_deck_target",""));
	        cedecktarget.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	            //@Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	            	try {
	            		Integer.parseInt((String) newValue);
	            		ce_deck_targetPref.setText((String) newValue);
	        		} catch(NumberFormatException e){
	        			warn("alert","default");
	        			ce_deck_targetPref.setText(getString(R.string.default_CE_decktarget));
	        			preference.setTitle(getString(R.string.title_ce_deck_target) + " : " + sp.getString("ce_deck_target",""));
	        			return false;	        			
	        		}
	            	if (Integer.parseInt((String) newValue) < Integer.parseInt(getString(R.string.default_min_CE_decktarget))){
	            		warn("alert","min");
	            		ce_deck_targetPref.setText(getString(R.string.default_min_CE_decktarget));
	            		preference.setTitle(getString(R.string.title_ce_deck_target) + " : " + sp.getString("ce_deck_target",""));
	            		return false;
	            	}
	            	preference.setTitle(getString(R.string.title_ce_deck_target) + " : " + sp.getString("ce_deck_target",""));
	                return true;
	            }
	        });
	        
	        Preference ecdecksize = findPreference("ec_decksize");
	        ecdecksize.setTitle(getString(R.string.title_ec_deck_size) + " : " + sp.getString("ec_decksize",""));
	        ecdecksize.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	            //@Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	            	try {
	        			Integer.parseInt((String) newValue);
	        			ec_decksizePref.setText((String) newValue);
	        		} catch(NumberFormatException e){
	        			warn("alert","default");   
	        			ec_decksizePref.setText(getString(R.string.default_EC_decksize));
	        			preference.setTitle(getString(R.string.title_ec_deck_size) + " : " + sp.getString("ec_decksize",""));
	        			return false;
	        		}
	            	if (Integer.parseInt((String) newValue) < Integer.parseInt(getString(R.string.default_min_EC_decksize))){
	            		warn("alert","min");
	            		ec_decksizePref.setText(getString(R.string.default_min_EC_decksize));
	            		preference.setTitle(getString(R.string.title_ec_deck_size) + " : " + sp.getString("ec_decksize",""));
	            		return false;
	            	}
	            	preference.setTitle(getString(R.string.title_ec_deck_size) + " : " + sp.getString("ec_decksize",""));
	                return true;
	            }
	        });
	        
	        Preference ecdecktarget = (Preference) findPreference("ec_deck_target");
	        ecdecktarget.setTitle(getString(R.string.title_ec_deck_target) + " : " + sp.getString("ec_deck_target",""));
	        ecdecktarget.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	            //@Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	            	try {
	            		Integer.parseInt((String) newValue);
	            		ec_deck_targetPref.setText((String) newValue);
	        		} catch(NumberFormatException e){
	        			warn("alert","default");
	        			ec_deck_targetPref.setText(getString(R.string.default_EC_decktarget));
	        			preference.setTitle(getString(R.string.title_ec_deck_target) + " : " + sp.getString("ec_deck_target",""));
	        			return false;	        			
	        		}
	            	if (Integer.parseInt((String) newValue) < Integer.parseInt(getString(R.string.default_min_EC_decktarget))){
	            		warn("alert","min");
	            		ec_deck_targetPref.setText(getString(R.string.default_min_EC_decktarget));
	            		preference.setTitle(getString(R.string.title_ec_deck_target) + " : " + sp.getString("ec_deck_target",""));
	            		return false;
	            	}
	            	preference.setTitle(getString(R.string.title_ec_deck_target) + " : " + sp.getString("ec_deck_target",""));
	                return true;
	            }
	        });
	        
	              
		}//end of oncreate
		
		protected void warn(String i, String j) {
			if (i=="toast"){
				if (j=="default"){
					Toast.makeText(c, getString(R.string.warn_reset_default), Toast.LENGTH_LONG).show();
				}
				else if (j=="min"){
					Toast.makeText(c, getString(R.string.warn_reset_min), Toast.LENGTH_LONG).show();
				}
			}
			else if (i=="alert"){
				if (j=="default"){
					new AlertDialog.Builder(c)
	                   .setIcon(android.R.drawable.ic_dialog_alert)
	                   .setTitle(R.string.warn)
	                   .setMessage(R.string.warn_reset_default)
	                   .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int which) {
	                           dialog.cancel();  
	                       }
	                   })
	                   .show();
				}
				else if (j=="min"){
					new AlertDialog.Builder(c)
	                   .setIcon(android.R.drawable.ic_dialog_alert)
	                   .setTitle(R.string.warn)
	                   .setMessage(R.string.warn_reset_min)
	                   .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int which) {
	                           dialog.cancel();  
	                       }
	                   })
	                   .show();
				}
			}
		}
		


		@Override
	    public void onResume() {
	        super.onResume();
	        // Set up a listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        // Set up a listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    }

	    //@Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {  

	    }

	}//end of preffragment
}//end of settingsactivity