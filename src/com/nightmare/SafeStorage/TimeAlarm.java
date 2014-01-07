package com.nightmare.SafeStorage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeAlarm extends BroadcastReceiver {

	NotificationManager nm;

	@Override
	public void onReceive(Context context, Intent intent) {
		notify(context, intent, "Safe Storage", "All new files have been stored");
//		StorageActivity sa = new StorageActivity();
//		sa.startComparison();
		// Uploading content.
	}	
	
	public void notify(Context context, Intent intent, String name, String messageToDisplay) {
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence from = name;
		CharSequence message = messageToDisplay;
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(), 0);
		Notification notif = new Notification(R.drawable.ic_launcher,
				message, System.currentTimeMillis());
		notif.setLatestEventInfo(context, from, message, contentIntent);
		nm.notify(1, notif);
	}
}
