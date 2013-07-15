package jp.oxiden.todayslunch;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class AsyncRetriever extends AsyncTask<String, Integer, Menu> {
	private final String TAG = "TodaysLunch";
	private Context _context;
	private AppWidgetManager _awm;
	private ComponentName _thiswidget;
	private RemoteViews _rv;

	public AsyncRetriever(Context context, AppWidgetManager awm, ComponentName thiswidget, RemoteViews rv) {
		_context = context;
		_awm = awm;
		_thiswidget = thiswidget;
		_rv = rv;
	}

	@Override
	protected Menu doInBackground(String... arg0) {
		Log.d(TAG, "doInBackground----------------------------------");
		Log.d(TAG, "doInBackground: uri=" + arg0[0]);
		try {
			RestTemplate template = new RestTemplate();
			template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Menu> res = template.exchange(arg0[0], HttpMethod.GET, null, Menu.class);
			Menu menu = res.getBody();
			Log.d(TAG, "doInBackground: success");
			return menu;
		} catch (Exception e) {
			Log.d(TAG, "doInBackground: fail", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Menu result) {
		Log.d(TAG, "onPostExecute----------------------------------");

		if (result.release != null && !TextUtils.isEmpty(result.title)) {
			String s = String.format(_context.getResources().getString(R.string.widget_title), result.getRelease());
			_rv.setTextViewText(R.id.text, s);
			_rv.setTextViewText(R.id.menu, result.title);
		} else {
			String s = _context.getResources().getString(R.string.widget_title_default);
			_rv.setTextViewText(R.id.text, s);
			s = TextUtils.isEmpty(result.title) ? _context.getResources().getString(R.string.no_menudata) : result.title;
			_rv.setTextViewText(R.id.menu, s);
		}

		// AppWidgetの更新
		_awm.updateAppWidget(_thiswidget, _rv);
		Log.d(TAG, "update AppWidget(2).==========================");
	}
}
