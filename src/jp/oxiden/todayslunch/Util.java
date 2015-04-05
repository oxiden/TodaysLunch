package jp.oxiden.todayslunch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class Util {
	private static final String TAG = "TodaysLunch";

	public static void log_d(String s) {
		Log.d(TAG, String.format("%03d: %s", Thread.currentThread().getId(), s));
	}

	public static void log_e(Exception e, String s) {
		long threadId = Thread.currentThread().getId();
		Log.e(TAG, String.format("%03d: ERROR: %s", threadId, s));
		Log.e(TAG, String.format("%03d: ERROR: %s", threadId, e.getMessage()));
		Log.e(TAG, String.format("%03d: ERROR: %s", threadId, e.getStackTrace()));
	}

	public static String getRESTURI(Date date) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN);
		// date.setYear(2013-1900);
		// date.setMonth(7-1);
		// date.setDate(23);
		return String.format("http://tweet-lunch-bot.herokuapp.com/shops/1/menus/%s.json", sdf1.format(date));
	}
}
