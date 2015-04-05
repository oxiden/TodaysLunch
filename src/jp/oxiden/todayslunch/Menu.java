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

	public String getTitle() {
		return (title != null && !title.isEmpty()) ? title : "";
	}

	// 日付を整形して文字列で返却する
	public String getRelease() {
		String result = "";
		if (release != null) {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd(E)", Locale.JAPAN);
			result = sdf1.format(release);
		}
		Util.log_d("Menu: getRelease=" + result);
		return result;
	}
}
