/**
 * Copyright
 */

package com.xblubb.hotwire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Main Activity Class. This Activity is created on program startup. It displays
 * the main menu of the game containing
 *
 * @author Jan
 * @version 1.1, 22.08.2010
 * @since 1.0
 */
public class HotWire extends Activity {

    /**
     * Requestcode for the Preference Activity (onActivityResult)
     */
    private static final int REQUEST_CODE_PREFERENCES = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Setting up layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    /** Called when the options menu is first created. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    /** Called when a button of the optionmenu is clicked. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gets the clicked menu button and its action
        switch (item.getItemId()) {
            case R.id.btn_menu_quit:
                finish();
                break;
            case R.id.btn_menu_about:
                return true;
            case R.id.btn_menu_preferences:
                Intent prefIntent = new Intent(HotWire.this, HotWirePreferences.class);
                startActivityForResult(prefIntent, REQUEST_CODE_PREFERENCES);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    /** Called when an Activity sends back an result maybe when its finised */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Deciding what to do depending on the request code
        switch (requestCode) {
            case REQUEST_CODE_PREFERENCES:
                PreferenceManager.setDefaultValues(this, R.xml.preferences_game, true);
                break;
            default:
                break;
        }
    }
}
