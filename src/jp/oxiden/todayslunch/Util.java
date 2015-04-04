package jp.oxiden.todayslunch;

import android.util.Log;

public class Util {
	private static final String TAG = "TodaysLunch";

	public static void log_d(String s){
		Log.d(TAG, String.format("%03d: %s", Thread.currentThread().getId(), s));
	}
}
