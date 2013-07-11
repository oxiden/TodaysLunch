package jp.oxiden.todayslunch;

import android.os.AsyncTask;

public class AsyncHttpRequest extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... arg0) {
		return getJSON();
	}

	private String getJSON() {
		return "";
	}
}
