// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.postit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.util.Launcher;
import org.kotemaru.android.postit.widget.PostItView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 付箋タイマーを受信するレシーバ。
 */
public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = AlarmReceiver.class.getSimpleName();

	public static void setAlarm(Context context, long time, String action) {
		Log.d(TAG, "setAlarm:" + new Date(time) + ":" + action);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.setAction(action);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		am.cancel(pi);
		am.set(AlarmManager.RTC_WAKEUP, time, pi);
	}

	/**
	 * 最新のタイマーを設定する。
	 * <li>非表示の付箋から一番最近に発火するタイマーを設定する。</li>
	 * @param context
	 */
	public static void setAlarm(Context context) {
		List<PostItData> list = PostItDataProvider.getAllPostItData(context);
		Iterator<PostItData> ite = list.iterator();
		while (ite.hasNext()) {
			PostItData data = ite.next();
			if (data.isEnabled() || data.getTimerPattern() == null) ite.remove();
		}
		// Log.d(TAG, "setAlarm:list=" + list);
		if (list.isEmpty()) return;

		Collections.sort(list, new Comparator<PostItData>() {
			@Override
			public int compare(PostItData lhs, PostItData rhs) {
				return lhs.getTimer() > rhs.getTimer() ? 1 : -1;
			}
		});
		// for (int i = 0; i < list.size(); i++) {
		// Log.d(TAG, "-->" + i + "=" + new Date(list.get(i).getTimer()) + ":" + list.get(i).getTimerPattern());
		// }

		setAlarm(context, list.get(0).getTimer(), Launcher.ACTION_CHANGE_DATA);
	}

	/**
	 * タイマーの発火を受信して付箋の状態を更新する。
	 * <li>次のタイマーを設定する。</li>
	 * <li>再起動時には次のタイマーの設定のみ行う。</li>
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "onReceive:" + action);
		if (Launcher.ACTION_CHANGE_SETTENGS.equals(action)) {
			Launcher.notifyChangeSettings(context);
		} else if (Launcher.ACTION_CHANGE_DATA.equals(action)) {
			ringtone(context);
			PostItDataProvider.updateTimer(context, System.currentTimeMillis());
			Launcher.notifyChangeData(context);
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			// nop.
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			// nop.
		}
		setAlarm(context);
	}

	private void ringtone(Context context) {
		Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone ringtone = RingtoneManager.getRingtone(context, defaultRingtoneUri);
		if (ringtone != null) ringtone.play();
	}
}
