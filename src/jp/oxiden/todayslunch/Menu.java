package jp.oxiden.todayslunch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class Menu {
	private final String TAG = "TodaysLunch";
	public Integer shop_id;
	public Integer id;
	public Date release;
	public String title;
	public String memo;
	public Boolean curry;
	public Date created_at;
	public Date updated_at;

	public String getRelease() {
		if (release == null) {
			return "";
		} else {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd(E)", Locale.JAPAN);
			return sdf1.format(release) + "";
		}
	}
}
