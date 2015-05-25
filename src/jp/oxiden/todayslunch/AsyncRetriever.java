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
		_rv.setTextViewText(R.id.text, _context.getResources().getString(R.string.text_loading));
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
		int titleId, textId;
		titleId = textId = R.string.app_name;// dummy value
		if (result != null) {
			if (result.error == ResponseData.ErrorType.NETWORK_OFFLINE) {
				// ネットワーク接続なし
				titleId = R.string.title_default;
				textId = R.string.text_internet_unreachable;
			} else if (result.error == ResponseData.ErrorType.UNKNOWN) {
				// 通信エラー(ということにする)
				titleId = R.string.title_default;
				textId = R.string.text_network_error;
			} else {
				// ASSERT result.error == ResponseData.ErrorType.NO_ERROR
				if (!result.menu.getText().isEmpty()) {
					// 成功
					_rv.setTextViewText(R.id.title, String.format(_context.getResources().getString(R.string.title_template), result.menu.getRelease()));
					_rv.setTextViewText(R.id.text, result.menu.getText());
					titleId = textId = R.string.app_name;// dummy value
				} else {
					// 休業日などメニュー無し
					titleId = R.string.title_default;
					textId = R.string.text_no_menudata;
				}
			}
		} else {
			// 通信エラー(ということにする)
			titleId = R.string.title_default;
			textId = R.string.text_network_error;
		}

		if (titleId != R.string.app_name) {
			_rv.setTextViewText(R.id.title, _context.getResources().getString(titleId));
		}
		if (textId != R.string.app_name) {
			_rv.setTextViewText(R.id.text, _context.getResources().getString(textId));
		}
		_awm.updateAppWidget(_appWidgetId, _rv);
		Util.log_d("update AppWidget(2).");
	}
}
