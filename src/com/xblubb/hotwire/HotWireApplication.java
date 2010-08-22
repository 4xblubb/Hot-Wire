/**
 * Copyright
 */

package com.xblubb.hotwire;

import android.app.Application;
import android.preference.PreferenceManager;

/**
 * Base Application class.
 *
 * @author Jan
 * @version 1.1, 22.08.2010
 * @since 1.0
 */
public class HotWireApplication extends Application {

    /** Is called when the Application starts. */
    @Override
    public void onCreate() {
        // do sth.
        PreferenceManager.setDefaultValues(this, R.xml.preferences_game, true);
    }

    /** Is called when the Application is going to be terminated. */
    @Override
    public void onTerminate() {
        // do sth.
    }

}
