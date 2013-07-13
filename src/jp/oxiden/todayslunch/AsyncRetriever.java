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

public class AsyncRetriever extends AsyncTask<String, Integer, String> {
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
	protected String doInBackground(String... arg0) {
		Log.d(TAG, "doInBackground----------------------------------");
		Log.d(TAG, "doInBackground: uri=" + arg0[0]);
		try {
			RestTemplate template = new RestTemplate();
			template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Menu> res = template.exchange(arg0[0], HttpMethod.GET, null, Menu.class);
			Menu menu = res.getBody();
			Log.d(TAG, "doInBackground: success");
			String result = menu.toString();
			return TextUtils.isEmpty(result) ? _context.getResources().getString(R.string.no_menudata) : result;
		} catch (Exception e) {
			Log.d(TAG, "doInBackground: fail", e);
			return "ERROR:" + e.toString();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		Log.d(TAG, "onPostExecute----------------------------------");
		Log.d(TAG, "result=" + result);
		_rv.setTextViewText(R.id.menu, result);

		// AppWidgetの更新
		_awm.updateAppWidget(_thiswidget, _rv);
		Log.d(TAG, "update AppWidget(2).==========================");
	}
}
