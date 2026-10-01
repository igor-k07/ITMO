package com.itmo.util.request;

import java.io.Serial;
import java.io.Serializable;

// Пустой запрос при запуске клиента

public class InitRequest implements Request, Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
	private final String login;
	private final String password;

	public InitRequest() {
		this(null, null);
	}

	public InitRequest(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
}


