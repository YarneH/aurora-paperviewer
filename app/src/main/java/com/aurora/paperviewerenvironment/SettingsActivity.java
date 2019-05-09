package com.aurora.paperviewerenvironment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {

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

            ListPreference fontFamily = (ListPreference) findPreference("fontFamily");
            if(fontFamily.getValue() == null){
                fontFamily.setValue(getResources().getString(R.string.default_font_family));
            }

        }
    }


}

