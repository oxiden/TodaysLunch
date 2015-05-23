package jp.oxiden.todayslunch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Util {
	private static final String TAG = "TodaysLunch";

	public static void log_d(String s) {
		Log.d(TAG, String.format("%04d: %s", Thread.currentThread().getId(), s));
	}

	public static void log_e(Exception e, String s) {
		long threadId = Thread.currentThread().getId();
		Log.e(TAG, String.format("%04d: ERROR: %s", threadId, s));
		Log.e(TAG, String.format("%04d: ERROR: %s", threadId, e.getMessage()));
		Log.e(TAG, String.format("%04d: ERROR: %s", threadId, e.getStackTrace()));
	}

	public static String getRESTURI(Date date) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN);
		// date.setYear(2013-1900);
		// date.setMonth(7-1);
		// date.setDate(23);
		return String.format("http://tweet-lunch-bot.herokuapp.com/shops/1/menus/%s.json", sdf1.format(date));
	}

	public static boolean isInternetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null) {
			log_d("isInternetConnected: " + ni.toString());
		} else {
			log_d("isInternetConnected: network is unreachable(ni=null)");
		}
		return (ni != null && ni.isConnected());
	}
}
