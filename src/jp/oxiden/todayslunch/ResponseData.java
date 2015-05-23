package jp.oxiden.todayslunch;

public class ResponseData {
	public Menu menu;

	public enum ErrorType {
		NO_ERROR, NETWORK_OFFLINE, UNKNOWN,
	};

	public ErrorType error;

	ResponseData(Menu m) {
		menu = m;
		error = ErrorType.NO_ERROR;
	}

	ResponseData(ErrorType err) {
		menu = null;
		error = err;
	}
}
