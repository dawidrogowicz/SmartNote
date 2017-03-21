package com.rogowiczdawid.smartnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class SettingsFragment extends PreferenceFragment {

    public final static String THEME_KEY = "pref_key_theme";
    public final static String EXTERNAL_KEY = "pref_storage_dir";
    public static boolean write_to_external = false;

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(THEME_KEY)) {
                Preference themePreference = findPreference(s);
                themePreference.setSummary(sharedPreferences.getString(s, ""));

                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), getActivity().getClass()));
            } else if (s.equals(EXTERNAL_KEY)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                write_to_external = preferences.getBoolean("pref_storage_dir", false);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}
