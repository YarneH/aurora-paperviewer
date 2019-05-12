package com.aurora.paperviewerenvironment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * <p>
 *     Activity class that is shown upon clicking the settings button in the toolbar.
 *     This Activity is responsible for inflating the available preferences from an xml file.
 *     These preferences are inflated within a {@link PreferenceFragment}.
 * </p>
 * <p>
 *     The preferences are automatically saved in the {@link SharedPreferences},
 *     these can then be accessed from anywhere in the program.
 * </p>
 *
 */
public class SettingsActivity extends PreferenceActivity {

    private final static String FONT_FAMILY_KEY = "fontFamily";

    /**
     * Called upon creation of this activity. See android lifecycle for more info.
     *
     * @param savedInstanceState State to load
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MyPreferenceFragment()).commit();
    }

    /**
     * The fragment which inflates the available preferences from an XML resource.
     */
    public static class MyPreferenceFragment extends PreferenceFragment {

        /**
         * Called upon creation of the preference fragment
         *
         * @param savedInstanceState State to load
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            ListPreference fontFamily = (ListPreference) findPreference(FONT_FAMILY_KEY);
            if(fontFamily.getValue() == null){
                fontFamily.setValue(getResources().getString(R.string.default_font_family));
            }

        }
    }


}

