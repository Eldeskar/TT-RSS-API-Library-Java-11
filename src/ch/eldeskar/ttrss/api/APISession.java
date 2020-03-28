package ch.eldeskar.ttrss.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class APISession {

	public JSONObject logIn(URL url, String user, String password) throws IOException {
		JSONObject login = new JSONObject("{\"op\":\"login\",\"user\":\"" + user + "\",\"password\":\"" + password + "\"}");
		return sendRequest(url, login);
	}

	public JSONObject getApiLevel(URL url, String session_id) throws IOException {
		JSONObject getAPILvl = new JSONObject("{\"sid\":\"" + session_id + "\",\"op\":\"getApiLevel\"}");
		return sendRequest(url, getAPILvl);
	}

	private JSONObject sendRequest(URL url, JSONObject request) throws IOException {
		HttpURLConnection connecction = (HttpURLConnection) url.openConnection();
		// connecction.setRequestMethod("POST");
		// connecction.setRequestProperty("Content-Type", "application/json; utf-8");
		// connecction.setRequestProperty("Accept", "application/json");
		connecction.setDoOutput(true);
		String jsonInputString = request.toString();
		try (OutputStream os = connecction.getOutputStream()) {
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);
			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(connecction.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = bufferedReader.readLine()) != null) {
					response.append(responseLine.trim());
				}
				return new JSONObject(response.toString());
			}
		}
	}
}
