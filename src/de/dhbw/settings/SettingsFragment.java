package de.dhbw.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 18.11.13.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}