package jp.oxiden.todayslunch;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncHttpRequest extends AsyncTask<String, Void, String>{

	@Override
	protected String doInBackground(String... arg0) {
		return getJSON();
	}

	private String getJSON() {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		String uri = "http://tweet-lunch-bot.herokuapp.com/shops/1/menus/2013-07-11.json";
		try {
			ResponseEntity<Menu> res = template.exchange(uri, HttpMethod.GET, null, Menu.class);
			Menu menu = res.getBody();
			return menu.toString();
		} catch (Exception e) {
			Log.d("Error", e.toString());
			return "Error: REST:GET";
		}
	}
}
