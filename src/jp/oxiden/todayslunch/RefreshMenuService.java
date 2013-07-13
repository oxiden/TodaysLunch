package jp.oxiden.todayslunch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

/*
 * サービスで、テキスト押下でPendingIntentが発行されるようにする。
 * ※そのPendingIntentで文字列の描画を行う
 */

public class RefreshMenuService extends Service {
	private final String TAG = "TodaysLunch";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand----------------------------------");
		RemoteViews rv = new RemoteViews(getPackageName(), R.layout.todayslunch_widget);

		// テキスト押下イベントでインテント発行
		Intent textIntent = new Intent();
		textIntent.setAction("BUTTON_CLICK_ACTION");
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, textIntent, 0);
		rv.setOnClickPendingIntent(R.id.menu, pendingIntent);
		Log.d(TAG, "onClickListener set.==========================");

		// ボタン押下のインテントならテキスト更新
		// ？？処理ここでいいのか？？
		if ("BUTTON_CLICK_ACTION".equals(intent.getAction())) {
			Log.d(TAG, "detect onClick Event.==========================");
			Context context = getApplicationContext();
			ComponentName thisWidget = new ComponentName(this, TodaysLunchWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			getMenu(new Date(), context, manager, thisWidget, rv);
		}

		// AppWidgetの更新
		ComponentName thisWidget = new ComponentName(this, TodaysLunchWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, rv);
		Log.d(TAG, "update AppWidget.==========================");

		return startId;
	}

	/*
	 * RESTでメニューデータを取得する
	 */
	private void getMenu(Date date, Context context, AppWidgetManager awm, ComponentName thiswidget, RemoteViews rv) {
		AsyncRetriever retr = new AsyncRetriever(context, awm, thiswidget, rv);
		retr.execute(getRESTURI(date));
	}

	private String getRESTURI(Date date) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN);
/*		date.setYear(2013-1900);
		date.setMonth(7-1);
		date.setDate(13);*/
		return String.format("http://tweet-lunch-bot.herokuapp.com/shops/1/menus/%s.json", sdf1.format(date));
	}
}
