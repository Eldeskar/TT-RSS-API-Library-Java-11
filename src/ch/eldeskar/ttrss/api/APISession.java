package ch.eldeskar.ttrss.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

/**
 * This class handles all the API calls to a TT-RSS server. this version was
 * written for version 1.5.8 of the TT-RSS server More information about the
 * server and the API can be found here:
 * 
 * @see <a href="https://tt-rss.org">https://tt-rss.org<a\>
 * 
 * @author Eldeskar
 * @version 0.0.1
 *
 */
public class APISession {

	/**
	 * This method logs into the tt rss server and returns the client session.
	 * 
	 * Returns client session ID.
	 * 
	 * {"session_id":"xxx"} It can also return several error objects:
	 * 
	 * If API is disabled for this user: error: "API_DISABLED" If specified username
	 * and password are incorrect: error: "LOGIN_ERROR" In case it isn’t immediately
	 * obvious, you have to login and get a session ID even if you are using single
	 * user mode. You can omit user and password parameters.
	 * 
	 * On version:1.6.0 and above login also returns current API level as an
	 * api_level integer, you can use that instead of calling getApiLevel after
	 * login.
	 * 
	 * @param URL    url of the tt rss server
	 * @param Strin  user login name
	 * @param String password
	 * @return JSONObject Response of the server
	 * @throws IOException
	 */
	public JSONObject login(URL url, String user, String password) throws IOException {
		JSONObject login = new JSONObject("{\"op\":\"login\",\"user\":\"" + user + "\",\"password\":\"" + password + "\"}");
		return sendRequest(url, login);
	}

	public JSONObject logout(URL url, String session_id) throws IOException {
		JSONObject logout = new JSONObject("{\"sid\":\"" + session_id + "\",\"op\":\"getApiLevel\"}");
		return sendRequest(url, logout);
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
