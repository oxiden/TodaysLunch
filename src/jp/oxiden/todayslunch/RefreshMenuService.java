package jp.oxiden.todayslunch;

import java.util.Date;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
		RemoteViews rv = new RemoteViews(getPackageName(), R.layout.todayslunch_widget);

		// テキスト押下イベントでインテント発行
		Intent textIntent = new Intent();
		textIntent.setAction(ACTION_NAME);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, textIntent, 0);
		rv.setOnClickPendingIntent(R.id.menu, pendingIntent);
		Util.log_d("onClickListener set.");

		// ボタン押下のインテントならテキスト更新
		// ？？処理ここでいいのか？？
		if (intent != null && ACTION_NAME.equals(intent.getAction())) {
			Util.log_d("detect onClick Event.");
			forceRefresh(rv);
		}

		// webボタン押下イベントでインテント(ブラウザ)起動
		Intent textIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tweet-lunch-bot.herokuapp.com/shops/1/menus"));
		PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, textIntent2, 0);
		rv.setOnClickPendingIntent(R.id.heroku, pendingIntent2);

		// AppWidgetの更新
		ComponentName thisWidget = new ComponentName(this, TodaysLunchWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, rv);
		Util.log_d("update AppWidget.");

		return startId;
	}

	//
	// RESTでメニューデータを取得する
	//
	public void forceRefresh(RemoteViews rv) {
		Context context = getApplicationContext();
		ComponentName thisWidget = new ComponentName(this, TodaysLunchWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		getMenu(new Date(), context, manager, thisWidget, rv);
	}

	private void getMenu(Date date, Context context, AppWidgetManager awm, ComponentName thiswidget, RemoteViews rv) {
		// メニュー情報更新(テキストタップによる手動更新)
		try {
			AsyncRetriever retr = new AsyncRetriever(context, awm, thiswidget, rv);
			retr.execute(Util.getRESTURI(date));
		} catch (Exception e) {
			Toast.makeText(context, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
			Util.log_e(e, "RefreshMenuService::getMenu");
		}
	}
}
