/**
 * Copyright
 */
package com.xblubb.hotwire;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Preferenceactivity
 *
 * @author Jan
 * @version 1.1, 22.08.2010
 * @since 1.0
 */
public class HotWirePreferences extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Loads the preferences from the xml
        addPreferencesFromResource(R.xml.preferences_game);
    }
}
