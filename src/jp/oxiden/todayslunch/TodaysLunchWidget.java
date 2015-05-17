package jp.oxiden.todayslunch;

import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class TodaysLunchWidget extends AppWidgetProvider {

	//
	// 一番はじめのWidget設置時のみ呼ばれる
	//
	@Override
	public void onEnabled(Context context) {
		Util.log_d("onEnabled----------------------------------");
		super.onEnabled(context);
	}

	//
	// Widget設置のたび呼ばれる
	//
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Util.log_d("onUpdate----------------------------------");

		// 対象ウィジェットに対する再描画＆サービス起動
		Util.log_d("appWidgetIds:" + appWidgetIds.length);
		for (int appWidgetId : appWidgetIds) {
			Util.log_d("appWidgetId:" + appWidgetId);

			if (isRunningRefreshMenuService(context)) {
				Intent intent = new Intent(context, RefreshMenuService.class);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				context.stopService(intent);
				Util.log_d("service stopped.");
			}
			if (!isRunningRefreshMenuService(context)) {
				Intent intent = new Intent(context, RefreshMenuService.class);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				context.startService(intent);
				Util.log_d("service started.");
			}

			// メニュー情報更新(一定間隔の自動更新)
			drawWidget(context, appWidgetId);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	//
	// Widget削除のたび呼ばれる
	//
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Util.log_d("onDeleted----------------------------------");

		// 対象ウィジェットに対するサービス停止
		Util.log_d("appWidgetIds:" + appWidgetIds.length);
		for (int appWidgetId : appWidgetIds) {
			Util.log_d("appWidgetId:" + appWidgetId);

			if (isRunningRefreshMenuService(context)) {
				Intent intent = new Intent(context, RefreshMenuService.class);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				context.stopService(intent);
				Util.log_d("service stopped.");
			}
		}

		super.onDeleted(context, appWidgetIds);
	}

	//
	// 一番さいごのWidget削除時のみ呼ばれる
	//
	@Override
	public void onDisabled(Context context) {
		Util.log_d("onDisabled----------------------------------");
		super.onDisabled(context);
	}

	//
	// 上記コールバック関数の直後、及びupdatePeriodMillis設定時間ごと(※0以外)呼ばれる
	//
	@Override
	public void onReceive(Context context, Intent intent) {
		Util.log_d("onReceive----------------------------------");
		super.onReceive(context, intent);
	}

	// ウィジェットの再描画
	private void drawWidget(Context context, int appWidgetId) {
		try {
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.todayslunch_widget);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			AsyncRetriever retr = new AsyncRetriever(context, manager, rv, appWidgetId);
			retr.execute(Util.getRESTURI(new Date()));
		} catch (Exception e) {
			Toast.makeText(context, "ERROR:" + e.getMessage(), Toast.LENGTH_LONG).show();
			Util.log_e(e, "TodaysLunchWidget::onReceive");
		}
	}

	// テキストタップ応答用サービスがRunning状態か？
	private boolean isRunningRefreshMenuService(Context context) {
		boolean result = false;
		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo si: manager.getRunningServices(Integer.MAX_VALUE)) {
			result = result || RefreshMenuService.class.getName().equals(si.service.getClassName());
		}
		return result;
	}
}
