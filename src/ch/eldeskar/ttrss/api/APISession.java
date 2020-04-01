package ch.eldeskar.ttrss.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
		JSONObject login = new JSONObject(
				"{\"op\":\"login\",\"user\":\"" + user + "\",\"password\":\"" + password + "\"}");
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

	/**
	 * This method required version: version:1.5.0
	 * 
	 * Returns a list of unread article counts for specified feed groups.
	 * 
	 * Parameters:
	 * 
	 * output_mode (string) - Feed groups to return counters for Output mode is a
	 * character string, comprising several letters (defaults to “flc”):
	 * 
	 * f - actual feeds l - labels c - categories t - tags Several global counters
	 * are returned as well, those can’t be disabled with output_mode.
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getCounters(URL url, String session_id, List<String> parameters) throws IOException {
		return createJSONParamString(session_id, parameters, "getCounters");
	}

	/**
	 * 
	 * This method returns JSON-encoded list of feeds. The list includes category
	 * id, title, feed url, etc.
	 * 
	 * Parameters:
	 * 
	 * cat_id (integer) - return feeds under category cat_id unread_only (bool) -
	 * only return feeds which have unread articles limit (integer) - limit amount
	 * of feeds returned to this value offset (integer) - skip this amount of feeds
	 * first include_nested (bool) - include child categories (as Feed objects with
	 * is_cat set) requires version:1.6.0 Pagination:
	 * 
	 * Limit and offset are useful if you need feedlist pagination. If you use them,
	 * you shouldn’t filter by unread, handle filtering in your app instead.
	 * 
	 * Special category IDs are as follows:
	 * 
	 * 0 Uncategorized -1 Special (e.g. Starred, Published, Archived, etc.) -2
	 * Labels Added in version:1.5.0:
	 * 
	 * -3 All feeds, excluding virtual feeds (e.g. Labels and such) -4 All feeds,
	 * including virtual feeds Known bug: Prior to version:1.5.0 passing null or 0
	 * cat_id to this method returns full list of feeds instead of Uncategorized
	 * feeds only.
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getFeeds(URL url, String session_id, List<String> parameters) throws IOException {
		return createJSONParamString(session_id, parameters, "getFeeds");
	}

	/**
	 * this method returns JSON-encoded list of categories with unread counts.
	 * 
	 * unread_only (bool) - only return categories which have unread articles
	 * enable_nested (bool) - switch to nested mode, only returns topmost categories
	 * requires version:1.6.0 include_empty (bool) - include empty categories
	 * requires version:1.7.6 Nested mode in this case means that a flat list of
	 * only topmost categories is returned and unread counters include counters for
	 * child categories.
	 * 
	 * This should be used as a starting point, to display a root list of all (for
	 * backwards compatibility) or topmost categories, use getFeeds to traverse
	 * deeper.
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getCategories(URL url, String session_id, List<String> parameters) throws IOException {
		return createJSONParamString(session_id, parameters, "getCategories");
	}

	/**
	 * Returns JSON-encoded list of headlines.
	 * 
	 * Parameters:
	 * 
	 * feed_id (integer) - only output articles for this feed limit (integer) -
	 * limits the amount of returned articles (see below) skip (integer) - skip this
	 * amount of feeds first filter (string) - currently unused (?) is_cat (bool) -
	 * requested feed_id is a category show_excerpt (bool) - include article excerpt
	 * in the output show_content (bool) - include full article text in the output
	 * view_mode (string = all_articles, unread, adaptive, marked, updated)
	 * include_attachments (bool) - include article attachments (e.g. enclosures)
	 * requires version:1.5.3 since_id (integer) - only return articles with id
	 * greater than since_id requires version:1.5.6 include_nested (boolean) -
	 * include articles from child categories requires version:1.6.0 order_by
	 * (string) - override default sort order requires version:1.7.6 sanitize (bool)
	 * - sanitize content or not requires version:1.8 (default: true) force_update
	 * (bool) - try to update feed before showing headlines requires version:1.14
	 * (api 9) (default: false) has_sandbox (bool) - indicate support for sandboxing
	 * of iframe elements (default: false) include_header (bool) - adds status
	 * information when returning headlines, instead of array(articles) return value
	 * changes to array(header, array(articles)) (api 12) Limit:
	 * 
	 * Before API level 6 maximum amount of returned headlines is capped at 60, API
	 * 6 and above sets it to 200.
	 * 
	 * This parameters might change in the future (supported since API level 2):
	 * 
	 * search (string) - search query (e.g. a list of keywords) search_mode (string)
	 * - all_feeds, this_feed (default), this_cat (category containing requested
	 * feed) match_on (string) - ignored Special feed IDs are as follows:
	 * 
	 * -1 starred -2 published -3 fresh -4 all articles 0 - archived IDs < -10
	 * labels Sort order values:
	 * 
	 * date_reverse - oldest first feed_dates - newest first, goes by feed date
	 * (nothing) - default
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getHeadlines(URL url, String session_id, List<String> parameters) throws IOException {
		return createJSONParamString(session_id, parameters, "getHeadlines");
	}

	/**
	 * This method update information on specified articles.
	 * 
	 * Parameters:
	 * 
	 * article_ids (comma-separated list of integers) - article IDs to operate on
	 * mode (integer) - type of operation to perform (0 - set to false, 1 - set to
	 * true, 2 - toggle) field (integer) - field to operate on (0 - starred, 1 -
	 * published, 2 - unread, 3 - article note since api level 1) data (string) -
	 * optional data parameter when setting note field (since api level 1) E.g. to
	 * set unread status of articles X and Y to false use the following:
	 * 
	 * ?article_ids=X,Y&mode=0&field=2
	 * 
	 * Since version:1.5.0 returns a status message:
	 * 
	 * {"status":"OK","updated":1} “Updated” is number of articles updated by the
	 * query.
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject updateArticle(URL url, String session_id, List<String> parameters) throws IOException {
		return createJSONParamString(session_id, parameters, "updateArticle");
	}

	/**
	 * This method requests JSON-encoded article object with specific ID.
	 * 
	 * article_id (integer) - article ID to return as of 15.10.2010 git or
	 * version:1.5.0 supports comma-separated list of IDs Since version:1.4.3 also
	 * returns article attachments.
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getArticle(URL url, String session_id, String article_Id) throws IOException {
		JSONObject getArticle = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"getArticle\"\"article_id\":\"" + article_Id + "\"}");
		return sendRequest(url, getArticle);
	}

	/**
	 * This method returns tt-rss configuration parameters:
	 * 
	 * {"icons_dir":"icons","icons_url":"icons","daemon_is_running":true,"num_feeds":71}
	 * icons_dir - path to icons on the server filesystem icons_url - path to icons
	 * when requesting them over http daemon_is_running - whether update daemon is
	 * running num_feeds - amount of subscribed feeds (this can be used to refresh
	 * feedlist when this amount changes)
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getConfig(URL url, String session_id, List<String> parameters) throws IOException {
		return createJSONParamString(session_id, parameters, "getConfig");
	}

	/**
	 * Tries to update specified feed. This operation is not performed in the
	 * background, so it might take considerable time and, potentially, be aborted
	 * by the HTTP server.
	 * 
	 * feed_id (integer) - ID of feed to update Returns status-message if the
	 * operation has been completed.
	 * 
	 * {"status":"OK"}
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject updateFeed(URL url, String session_id, String feed_Id) throws IOException {
		JSONObject updateFeed = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"updateFeed\"\"feed_id\":\"" + feed_Id + "\"}");
		return sendRequest(url, updateFeed);
	}

	/**
	 * Returns preference value of specified key.
	 * 
	 * pref_name (string) - preference key to return value of {"value":true}
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getPref(URL url, String session_id, String pref_name) throws IOException {
		JSONObject getPref = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"getPref\"\"pref_name\":\"" + pref_name + "\"}");
		return sendRequest(url, getPref);
	}

	/**
	 * Required version: version:1.4.3
	 * 
	 * Tries to catchup (e.g. mark as read) specified feed.
	 * 
	 * Parameters:
	 * 
	 * feed_id (integer) - ID of feed to update is_cat (boolean) - true if the
	 * specified feed_id is a category Returns status-message if the operation has
	 * been completed.
	 * 
	 * {"status":"OK"}
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject catchupFeed(URL url, String session_id) throws IOException {
		// TODO catchupFeed
		return null;
	}

	/**
	 * This method returns list of configured labels, as an array of label objects:
	 * 
	 * {"id":2,"caption":"Debian","fg_color":"#e14a00","bg_color":"#ffffff","checked":false},
	 * Before version:1.7.5
	 * 
	 * Returned id is an internal database id of the label, you can convert it to
	 * the valid feed id like this:
	 * 
	 * feed_id = -11 - label_id
	 * 
	 * After:
	 * 
	 * No conversion is necessary.
	 * 
	 * Parameters:
	 * 
	 * article_id (int) - set “checked” to true if specified article id has returned
	 * label.
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getLabels(URL url, String session_id, String article_Id) throws IOException {
		JSONObject getLabels = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"getLabels\",\"article_Id\":\"" + article_Id + "\"}");
		return sendRequest(url, getLabels);
	}

	/**
	 * Assigns article_ids to specified label.
	 * 
	 * Parameters:
	 * 
	 * article_ids - comma-separated list of article ids label_id (int) - label id,
	 * as returned in getLabels assign (boolean) - assign or remove label Note: Up
	 * until version:1.15 setArticleLabel() clears the label cache for the specified
	 * articles. Make sure to regenerate it (e.g. by calling API method getLabels()
	 * for the respecting articles) when you’re using methods which don’t do that by
	 * themselves (e.g. getHeadlines()) otherwise getHeadlines() will not return
	 * labels for modified articles.
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject setArticleLabel(URL url, String session_id, List<String> parameters) throws IOException {
		return createJSONParamString(session_id, parameters, "setArticleLabel");
	}

	/**
	 * Creates an article with specified data in the Published feed.
	 * 
	 * Parameters:
	 * 
	 * title - Article title (string) url - Article URL (string) content - Article
	 * content (string)
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject shareToPublished(URL url, String session_id, List<String> parameters) throws IOException {
		return createJSONParamString(session_id, parameters, "shareToPublished");
	}

	/**
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject subscribeToFeed(URL url, String session_id, String feed_url, String category_id)
			throws IOException {
		JSONObject subscribeToFeed = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"subscribeToFeed\",\"session_id\":\"" + session_id
						+ "\",\"feed_url\":\"" + feed_url + "\"\",\"category_id\":\"" + category_id + "\"\"}");
		return sendRequest(url, subscribeToFeed);
	}

	public JSONObject subscribeToFeed(URL url, String session_id, String feed_url, String category_id, String login,
			String password) throws IOException {
		JSONObject subscribeToFeed = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"subscribeToFeed\",\"session_id\":\"" + session_id
						+ "\",\"feed_url\":\"" + feed_url + "\"\",\"category_id\":\"" + category_id
						+ "\"\",\"login\":\"" + login + "\"\",\"password\":\"" + password + "\"\"}");
		return sendRequest(url, subscribeToFeed);
	}

	/**
	 * Unsubscribes specified feed.
	 * 
	 * Parameters:
	 * 
	 * feed_id - Feed id to unsubscribe from
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject unsubscribeFeed(URL url, String session_id, String feed_id) throws IOException {
		JSONObject unsubscribeFeed = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"unsubscribeFeed\",\"feed_id\":\"" + feed_id + "\"}");
		return sendRequest(url, unsubscribeFeed);
	}

	/**
	 * include_empty (bool) - include empty categories Returns full tree of
	 * categories and feeds.
	 * 
	 * Note: counters for most feeds are not returned with this call for performance
	 * reasons.
	 * 
	 * @param URL    url
	 * @param String session_id
	 * @return JSONObject Response of the server. Example:
	 * @throws IOException
	 */
	public JSONObject getFeedTree(URL url, String session_id, boolean includeEmpty) throws IOException {
		JSONObject getFeedTree = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"getFeedTree\",\"include_empty\":\"" + includeEmpty + "\"}");
		return sendRequest(url, getFeedTree);
	}

	private JSONObject createJSONParamString(String session_id, List<String> parameters, String apiMethodName) {
		String outputModes = "";
		for (String param : parameters) {
			outputModes = outputModes + "," + param;
		}
		JSONObject getJSONObject = new JSONObject(
				"{\"sid\":\"" + session_id + "\",\"op\":\"" + apiMethodName + "\"" + outputModes + "}");
		return getJSONObject;
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
