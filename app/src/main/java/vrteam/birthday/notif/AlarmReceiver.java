package vrteam.birthday.notif;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;

import vrteam.birthday.MainActivity;
import vrteam.birthday.R;

import static android.R.attr.id;

public class AlarmReceiver extends BroadcastReceiver {

    private int notification_id;

    @Override
    public void onReceive(Context context, Intent intent) {


        // Notificación

         System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context,MainActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(context,notification_id,repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(intent.getStringExtra("titulo"))
                .setContentText("¡Hoy es su cumpleaños!")
                .setAutoCancel(true);
        builder.setVibrate(new long[] { 300, 300 });
        builder.setLights(Color.BLUE, 3000, 3000);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        notificationManager.notify(notification_id,builder.build());

        // Fin Notificación

    }
}
