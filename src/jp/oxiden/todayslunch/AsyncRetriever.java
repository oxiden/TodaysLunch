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
import android.widget.RemoteViews;

public class AsyncRetriever extends AsyncTask<String, Integer, Menu> {
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
	protected void onPreExecute() {
		Util.log_d("onPreExecute----------------------------------");

		// ローディング表示
		_rv.setTextViewText(R.id.menu, _context.getResources().getString(R.string.loading));
		_awm.updateAppWidget(_thiswidget, _rv);
		Util.log_d("update AppWidget(1).");
	}

	@Override
	protected Menu doInBackground(String... arg0) {
		Util.log_d("doInBackground----------------------------------");
		Util.log_d("doInBackground: uri=" + arg0[0]);

		// RESTのレスポンスをMenuインスタンスとして返却
		try {
			RestTemplate template = new RestTemplate();
			template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Menu> res = template.exchange(arg0[0], HttpMethod.GET, null, Menu.class);
			Menu menu = res.getBody();
			Util.log_d("doInBackground: success");
			return menu;
		} catch (Exception e) {
			Util.log_d("doInBackground: fail");
			Util.log_d("doInBackground: " + e.getStackTrace());
			return null;
		}
	}

	@Override
	protected void onPostExecute(Menu result) {
		Util.log_d("onPostExecute----------------------------------");

		// RESTレスポンス(Menuオブジェクト)から結果を取得・表示
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
		_awm.updateAppWidget(_thiswidget, _rv);
		Util.log_d("update AppWidget(2).");
	}
}
