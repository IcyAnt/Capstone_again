package com.MeadowEast.xue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends Activity {

//	public static SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
		PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, true);
		
//		sp = PreferenceManager.getDefaultSharedPreferences(this); 
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
	        
	        Preference cedecksize = (Preference) findPreference("ce_decksize");
	        cedecksize.setTitle(getString(R.string.title_ce_deck_size) + " : " + sp.getString("ce_decksize",""));
	        cedecksize.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

	            //@Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	            	int val;
	            	try {
	        			val = Integer.parseInt((String) newValue);
	        		} catch(NumberFormatException e){
	        			val = Integer.parseInt(getString(R.string.default_CE_decksize));
	        			/*
	        			SharedPreferences.Editor editor = sp.edit();
	        			editor.putString("ce_decksize",getString(R.string.default_CE_decksize));
	        			editor.commit();
	        			System.out.println("ce_decksize ::" + 
	                            sp.getString("ce_decksize", ""));*/
	        		}
	            	if (val < Integer.parseInt(getString(R.string.default_min_CE_decksize))){
	            		val = Integer.parseInt(getString(R.string.default_min_CE_decksize));
	            	/*	SharedPreferences.Editor editor = sp.edit();
	        			editor.putString("ce_decksize", getString(R.string.default_min_CE_decksize));
	        			editor.commit();
	        			System.out.println("ce_decksize ::" + 
	                            sp.getString("ce_decksize", ""));*/
	            	}

	                preference.setTitle(getString(R.string.title_ce_deck_size) + " : " + Integer.toString(val));
	            	//preference.setTitle(getString(R.string.title_ce_deck_size) + " : " + sp.getString("ce_decksize",""));
	            	//preference.setTitle(getString(R.string.title_ce_deck_size) + " : " + sp.getString("ce_decksize",""));
	                return true;
	            }

	        });
	        
	        Preference cedecktarget = (Preference) findPreference("ce_deck_target");
	        cedecktarget.setTitle(getString(R.string.title_ce_deck_target) + " : " + sp.getString("ce_deck_target",""));
	        cedecktarget.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	            //@Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	            	int val;
	            	try {
	        			val = Integer.parseInt((String) newValue);
	        		} catch(NumberFormatException e){
	        			val = Integer.parseInt(getString(R.string.default_CE_decktarget));
	        		}
	            	if (val < Integer.parseInt(getString(R.string.default_min_CE_decktarget))){
	            		val = Integer.parseInt(getString(R.string.default_min_CE_decktarget));
	            	}
	                preference.setTitle(getString(R.string.title_ce_deck_target) + " : " + Integer.toString(val));
	            	//preference.setTitle(getString(R.string.title_ce_deck_target) + " : " + sp.getString("ce_deck_target",""));
	                return true;
	            }
	        });
	        
	        Preference ecdecksize = (Preference) findPreference("ec_decksize");
	        ecdecksize.setTitle(getString(R.string.title_ec_deck_size) + " : " + sp.getString("ec_decksize",""));
	        ecdecksize.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	            //@Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	            	int val;
	            	try {
	        			val = Integer.parseInt((String) newValue);
	        		} catch(NumberFormatException e){
	        			val = Integer.parseInt(getString(R.string.default_EC_decksize));
	        		}
	            	if (val < Integer.parseInt(getString(R.string.default_min_EC_decksize))){
	            		val = Integer.parseInt(getString(R.string.default_min_EC_decksize));
	            	}
	                preference.setTitle(getString(R.string.title_ec_deck_size) + " : " + Integer.toString(val));
	            	//preference.setTitle(getString(R.string.title_ec_deck_size) + " : " + sp.getString("ec_decksize",""));
	                return true;
	            }
	        });
	        
	        Preference ecdecktarget = (Preference) findPreference("ec_deck_target");
	        ecdecktarget.setTitle(getString(R.string.title_ec_deck_target) + " : " + sp.getString("ec_deck_target",""));
	        ecdecktarget.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	            //@Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) {
	            	int val;
	            	try {
	        			val = Integer.parseInt((String) newValue);
	        		} catch(NumberFormatException e){
	        			val = Integer.parseInt(getString(R.string.default_EC_decktarget));
	        		}
	            	if (val < Integer.parseInt(getString(R.string.default_min_EC_decktarget))){
	            		val = Integer.parseInt(getString(R.string.default_min_EC_decktarget));
	            	}
	                preference.setTitle(getString(R.string.title_ec_deck_target) + " : " + Integer.toString(val));
	                //preference.setTitle(getString(R.string.title_ec_deck_target) + " : " + sp.getString("ec_deck_target",""));
	                return true;
	            }
	        });
	                
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
	    //	EditTextPreference cedecksize = (EditTextPreference) findPreference("ce_decksize");
	    //	String newValue = sharedPreferences.getString("ce_decksize", "");
	    //	cedecksize.setTitle("dummy");
	    //	cedecksize.setTitle(newValue);
	    //	EditTextPreference cedecktarget = (EditTextPreference) findPreference("ce_deck_target");
	    //	EditTextPreference ecdecksize = (EditTextPreference) findPreference("ec_decksize");
	    //	EditTextPreference ecdecktarget = (EditTextPreference) findPreference("ec_deck_target");
	    	
	    //	Preference cedecksize = (Preference) findPreference("ce_decksize");
		//    cedecksize.setTitle(getString(R.string.title_ce_deck_size) + " : " + sp.getString("ce_decksize",""));
	    }
		
	}
}