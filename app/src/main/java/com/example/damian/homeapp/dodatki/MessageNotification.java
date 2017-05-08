package com.example.damian.homeapp.dodatki;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.damian.homeapp.FindDevicesActivity;
import com.example.damian.homeapp.HomeActivity;
import com.example.damian.homeapp.R;

/**
 * Helper class for showing and canceling new message
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class MessageNotification {
    /**
     * The unique identifier for this type of notification.
     */

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of new message notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */

    private static NotificationCompat.Builder builder;

    private static NotificationManager nm;

    public static void notify(final Context context,
                              final String exampleString, final int number) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);



        Intent homeIntent = new Intent(context, HomeActivity.class);

        PendingIntent homePendingIntent = PendingIntent.getActivity(context, 0, homeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);



        final String ticker = exampleString;
        final String title = exampleString;

        builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setContentTitle(title)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(ticker)


                .setOnlyAlertOnce(true).setAutoCancel(false);

                // Automatically dismiss the notification when it is touched

        switch (number){
            case 0:
                Intent searchDevice = new Intent(context, FindDevicesActivity.class);

                PendingIntent searchDeviceIntent = PendingIntent.getActivity(context, 0, searchDevice,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(R.drawable.ic_action_stat_reply, "Paruj", searchDeviceIntent)
                        .setContentIntent(homePendingIntent)
                        .setSmallIcon(R.drawable.ic_bluetooth_searching);
                break;

            case 1:
                Intent requestBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                PendingIntent requestBluetoothPendingIntent =
                        PendingIntent.getActivity(context, 0, requestBluetoothIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                builder.addAction(R.drawable.ic_bluetooth_searching, "Włącz",
                        requestBluetoothPendingIntent)
                        .setSmallIcon(R.drawable.ic_bluetooth_off);
                break;

            case 2:
                builder.setSmallIcon(R.drawable.ic_bluetooth_searching);
                break;

            case 3:

                Intent brama = new Intent();
                brama.setAction("BRAMA");
                PendingIntent bramaPending = PendingIntent.getBroadcast(context, 0,
                        brama, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent garaz = new Intent();
                garaz.setAction("GARAZ");
                PendingIntent garazPending = PendingIntent.getBroadcast(context, 0,
                        garaz, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent dom = new Intent(context, HomeActivity.class);
                PendingIntent domPending =
                        PendingIntent.getActivity(context, 0, dom,
                                Intent.FILL_IN_ACTION);

                builder.addAction(R.drawable.ic_garage, "Garaż", garazPending)
                        .addAction(R.drawable.ic_gate, "Brama", bramaPending)
                        .addAction(R.drawable.ic_door, "Dom", domPending )
                        .setSmallIcon(R.drawable.ic_bluetooth_connected).setContentText("info")
                        .setOngoing(true);
                break;

        }
        nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1, builder.build());
    }


    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel("1", 0);
        } else {
            nm.cancel("1".hashCode());
        }
    }
}
