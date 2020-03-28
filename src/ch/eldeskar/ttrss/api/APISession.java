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
	 * This method returns an abstracted integer API version level, increased with
	 * each API functionality change. This is the proper way to detect host API
	 * functionality, instead of using getVersion.
	 * 
	 * @param URL    url of the tt rss server
	 * @param String session_id
	 * @return JSONObject Response of the server. Example: {"level":1}
	 * @throws IOException
	 */
	public JSONObject getApiLevel(URL url, String session_id) throws IOException {
		JSONObject getApiLevel = new JSONObject("{\"sid\":\"" + session_id + "\",\"op\":\"getApiLevel\"}");
		return sendRequest(url, getApiLevel);
	}

	/**
	 * This method returns tt-rss version. As of, version:1.5.8 it is not
	 * recommended to use this to detect API functionality, please use getApiLevel
	 * instead.
	 * 
	 * @param URL    url of the tt rss server
	 * @param String session_id
	 * @return JSONObject Response of the server. Example: {"version":"1.4.0"}
	 * @throws IOException
	 */
	public JSONObject getVersion(URL url, String session_id) throws IOException {
		JSONObject getApiLevel = new JSONObject("{\"sid\":\"" + session_id + "\",\"op\":\"getApiLevel\"}");
		return sendRequest(url, getApiLevel);
	}

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
	 * @param String user login name
	 * @param String password
	 * @return JSONObject Response of the server. {"session_id":"xxx"}
	 * @throws IOException
	 */
	public JSONObject login(URL url, String user, String password) throws IOException {
		JSONObject login = new JSONObject("{\"op\":\"login\",\"user\":\"" + user + "\",\"password\":\"" + password + "\"}");
		return sendRequest(url, login);
	}

	/**
	 * This method closes your login session. Returns either status-message
	 * {"status":"OK"} or an error (e.g. {"error":"NOT_LOGGED_IN"}
	 * 
	 * @param URL    url of the tt rss server
	 * @param String session_id
	 * @return JSONObject Response of the server. Example: {"status":"OK"} or
	 *         {"error":"NOT_LOGGED_IN"}
	 * @throws IOException
	 */
	public JSONObject logout(URL url, String session_id) throws IOException {
		JSONObject logout = new JSONObject("{\"sid\":\"" + session_id + "\",\"op\":\"logout\"}");
		return sendRequest(url, logout);
	}

	/**
	 * This method returns a status message with boolean value showing whether your
	 * client (e.g. specific session ID) is currently logged in.
	 * 
	 * @param URL    url of the tt rss server
	 * @param String session_id
	 * @return JSONObject Response of the server. Example: {"status":false}
	 * @throws IOException
	 */
	public JSONObject isLoggedIn(URL url, String session_id) throws IOException {
		JSONObject isLoggedIn = new JSONObject("{\"sid\":\"" + session_id + "\",\"op\":\"isLoggedIn\"}");
		return sendRequest(url, isLoggedIn);
	}

	/**
	 * This method returns an integer value of currently unread articles.
	 * 
	 * @param URL    url of the tt rss server
	 * @param String session_id
	 * @return JSONObject Response of the server. Example: {"unread":"992"}
	 * @throws IOException
	 */
	public JSONObject getUnread(URL url, String session_id) throws IOException {
		JSONObject getUnread = new JSONObject("{\"sid\":\"" + session_id + "\",\"op\":\"getUnread\"}");
		return sendRequest(url, getUnread);
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
