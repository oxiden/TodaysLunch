package jp.oxiden.todayslunch;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.RemoteViews;

public class AsyncRetriever extends AsyncTask<String, Integer, ResponseData> {
	private Context _context;
	private AppWidgetManager _awm;
	private RemoteViews _rv;
	private int _appWidgetId;

	public AsyncRetriever(Context context, AppWidgetManager awm, RemoteViews rv, int appWidgetId) {
		_context = context;
		_awm = awm;
		_rv = rv;
		_appWidgetId = appWidgetId;
	}

	@Override
	protected void onPreExecute() {
		Util.log_d("onPreExecute------------");

		// ローディング表示
		_rv.setTextViewText(R.id.menu, _context.getResources().getString(R.string.loading));
		_awm.updateAppWidget(_appWidgetId, _rv);
		Util.log_d("update AppWidget(1).");
	}

	@Override
	protected ResponseData doInBackground(String... arg0) {
		Util.log_d("doInBackground------------");
		Util.log_d("doInBackground: uri=" + arg0[0]);

		// RESTのレスポンスをMenuインスタンスとして返却
		try {
			if (Util.isInternetConnected(_context)) {
				RestTemplate template = new RestTemplate();
				template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				ResponseEntity<Menu> res = template.exchange(arg0[0], HttpMethod.GET, null, Menu.class);
				Menu menu = res.getBody();
				Util.log_d("doInBackground: success");
				return new ResponseData(menu);
			} else {
				return new ResponseData(ResponseData.ErrorType.NETWORK_OFFLINE);
			}
		} catch (Exception e) {
			Util.log_e(e, "AsyncRetriever::doInBackground");
			return new ResponseData(ResponseData.ErrorType.UNKNOWN);
		}
	}

	@Override
	protected void onPostExecute(ResponseData result) {
		Util.log_d("onPostExecute------------");

		// RESTレスポンス(Menuオブジェクト)から結果を取得・表示
		int textId, titleId;
		textId = titleId = R.string.app_name;// dummy value
		if (result != null) {
			if (result.error == ResponseData.ErrorType.NETWORK_OFFLINE) {
				// ネットワーク接続なし
				textId = R.string.widget_title_default;
				titleId = R.string.internet_unreachable;
			} else {
				// ASSERT result.error == ResponseData.ErrorType.NO_ERROR
				if (!result.menu.getTitle().isEmpty()) {
					// 成功
					_rv.setTextViewText(R.id.text, String.format(_context.getResources().getString(R.string.widget_title), result.menu.getRelease()));
					_rv.setTextViewText(R.id.menu, result.menu.getTitle());
					textId = titleId = R.string.app_name;// dummy value
				} else {
					// 休業日などメニュー無し
					textId = R.string.widget_title_default;
					titleId = R.string.no_menudata;
				}
			}
		} else {
			// 通信エラー(ということにする)
			textId = R.string.widget_title_default;
			titleId = R.string.network_error;
		}

		if (textId != R.string.app_name) {
			_rv.setTextViewText(R.id.text, _context.getResources().getString(textId));
		}
		if (titleId != R.string.app_name) {
			_rv.setTextViewText(R.id.menu, _context.getResources().getString(titleId));
		}
		_awm.updateAppWidget(_appWidgetId, _rv);
		Util.log_d("update AppWidget(2).");
	}
}
