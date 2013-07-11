package jp.oxiden.todayslunch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;

public class TodaysLunchWidget extends AppWidgetProvider {
	private final String TAG = "TodaysLunch";

	/*
	 * 一番はじめのWidget設置時のみ呼ばれる
	 */
	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled----------------------------------");
		super.onEnabled(context);
		// メインスレッドでREST通信するための暫定対応
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

	}

	/*
	 * Widget設置のたび呼ばれる
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		Log.d(TAG, "onUpdate----------------------------------");
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.todayslunch_widget);
		ComponentName cn = new ComponentName(context, TodaysLunchWidget.class);
		appWidgetManager.updateAppWidget(cn, rv);

		String s = getMenu(new Date()).toString();
		rv.setTextViewText(R.id.menu, s);

		appWidgetManager.updateAppWidget(cn, rv);

		super.onUpdate(context, appWidgetManager, appWidgetIds);

	}

	/*
	 * Widget削除のたび呼ばれる
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted----------------------------------");
		super.onDeleted(context, appWidgetIds);
	}

	/*
	 * 一番さいごのWidget削除時のみ呼ばれる
	 */
	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled----------------------------------");
		super.onDisabled(context);
	}

	/*
	 * 上記コールバック関数の直後、及びupdatePeriodMillis設定時間ごと(※0以外)呼ばれる
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive====================================");
		super.onReceive(context, intent);
	}

	/*
	 * RESTでメニューデータを取得する
	 */
	private String getMenu(Date date) {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		String uri = getRESTURI(date);
		try {
			ResponseEntity<Menu> res = template.exchange(uri, HttpMethod.GET, null, Menu.class);
			Menu menu = res.getBody();
			return menu.toString();
		} catch (Exception e) {
			Log.d("Error", e.toString());
			return "Error: Internet communication.";
		}
	}

	private String getRESTURI(Date date) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN);
		return String.format("http://tweet-lunch-bot.herokuapp.com/shops/1/menus/%s.json", sdf1.format(date));
	}
}
