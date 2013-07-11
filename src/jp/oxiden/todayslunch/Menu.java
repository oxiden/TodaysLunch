package jp.oxiden.todayslunch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Menu {
	public Integer shop_id;
	public Integer id;
	public Date release;
	public String title;
	public String memo;
	public Boolean curry;
	public Date created_at;
	public Date updated_at;
	
	@Override
	public String toString() {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN);
		return sdf1.format(release) + ": " + title;
	}
}
