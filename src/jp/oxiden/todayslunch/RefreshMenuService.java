package jp.oxiden.todayslunch;

import java.util.Date;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

//
// サービスで、テキスト押下でPendingIntentが発行されるようにする。
// ※そのPendingIntentで文字列の描画を行う
// 

public class RefreshMenuService extends Service {
	private final String ACTION_NAME = "FORCE_REFRESH_MENU";
	private final int DEFAULT_WIDGET_ID = 0;
	private int _appWidgetId = DEFAULT_WIDGET_ID;

	//
	// 一番はじめのService起動時のみ呼ばれる
	// ※startServiceを複数回呼んだ場合、初回のみ呼ばれる
	//
	@Override
	public void onCreate() {
		super.onCreate();
		Util.log_d("onCreate------------");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	//
	// startServiceのたび呼ばれる
	//
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Util.log_d("onStartCommand------------");

		if (intent != null) {
			_appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, DEFAULT_WIDGET_ID);
		} else {
			_appWidgetId = DEFAULT_WIDGET_ID;
		}
		Util.log_d("appWidgetId:" + _appWidgetId);

		RemoteViews rv = new RemoteViews(getPackageName(), R.layout.todayslunch_widget);

		// テキスト押下イベントでインテント発行
		Intent textIntent = new Intent();
		textIntent.setAction(ACTION_NAME);
		textIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
		PendingIntent textPendingIntent = PendingIntent.getService(this, _appWidgetId, textIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.text, textPendingIntent);
		Util.log_d("onClickListener set.");

		// ボタン押下のインテントならテキスト更新
		// ？？処理ここでいいのか？？
		if (intent != null && ACTION_NAME.equals(intent.getAction())) {
			Util.log_d("detect onClick Event.");
			forceRefresh(rv);
		}

		// webボタン押下イベントでインテント(ブラウザ)起動
		Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tweet-lunch-bot.herokuapp.com/shops/1/menus"));
		PendingIntent webPendingIntent = PendingIntent.getActivity(this, _appWidgetId, webIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.web, webPendingIntent);

		// AppWidgetの更新
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(_appWidgetId, rv);
		Util.log_d("update AppWidget.");

		return startId;
	}

	//
	// RESTでメニューデータを取得する
	//
	public void forceRefresh(RemoteViews rv) {
		Context context = getApplicationContext();
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		getMenu(new Date(), context, manager, rv);
	}

	private void getMenu(Date date, Context context, AppWidgetManager awm, RemoteViews rv) {
		// メニュー情報更新(テキストタップによる手動更新)
		try {
			AsyncRetriever retr = new AsyncRetriever(context, awm, rv, _appWidgetId);
			retr.execute(Util.getRESTURI(date));
		} catch (Exception e) {
			Toast.makeText(context, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
			Util.log_e(e, "RefreshMenuService::getMenu");
		}
	}
}
