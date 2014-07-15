package de.dhbw.settings;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import de.dhbw.navigation.NavigationActivity;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 02.04.2014.
 */
public class VoteNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_kadcon_logo_alternate)
                        .setContentTitle(context.getString(R.string.notification_title_vote))
                        .setContentText(context.getString(R.string.notification_text_vote));
        Intent resultIntent = new Intent(context, NavigationActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NavigationActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Integer.parseInt(context.getString(R.string.notification_id_vote)), mBuilder.build());
    }
}
