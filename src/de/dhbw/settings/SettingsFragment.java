package de.dhbw.settings;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Preference voteNotification = findPreference(getString(R.string.pref_vote_notification_key));
        if (voteNotification != null) {
            voteNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    Context context = getActivity();
                    Intent intent = new Intent(context, VoteNotification.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    // Returns state before it is changed
                    if (((CheckBoxPreference) preference).isChecked() == true)
                    {
                        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
                    }
                    else
                    {
                        Calendar calendar = Calendar.getInstance();
                        if (calendar.get(Calendar.HOUR_OF_DAY) >= 17)
                            calendar.add(Calendar.DATE, 1);
                        calendar.set(Calendar.HOUR_OF_DAY, 17);
                        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                    return true;
                }
            });
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

