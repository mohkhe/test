package db.infiniti.webtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;

import NoHtmlUnitWarning.QuietCssErrorHandler;
import NoHtmlUnitWarning.SilentIncorrectnessListener;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebTools {

	HtmlPage page = null;
	WebClient webClient;
	java.sql.Connection connection;
	
	public WebTools() {
		super();
		webClient = new WebClient();
		
		webClient.setTimeout(6000);
		webClient.setJavaScriptTimeout(6000);
		webClient.setIncorrectnessListener(new SilentIncorrectnessListener());
		webClient.setCssErrorHandler(new QuietCssErrorHandler());
		webClient.setCssEnabled(false);
	
		webClient.setThrowExceptionOnFailingStatusCode(false);
		webClient.setThrowExceptionOnScriptError(false);
		
		webClient.setRedirectEnabled(true);
		webClient.setJavaScriptEnabled(true);
		    try {
				webClient.setUseInsecureSSL(true);
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		// webClient.throwFailingHttpStatusCodeExceptionIfNecessary(null);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				Level.OFF);
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
				.setLevel(Level.OFF);
		java.util.logging.Logger.getLogger(
				"com.gargoylesoftware.htmlunit.javascript").setLevel(Level.OFF);
		java.util.logging.Logger
				.getLogger(
						"com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement")
				.setLevel(Level.OFF);
		java.util.logging.Logger
				.getLogger(
						"com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement.jsxGet_spellcheck()")
				.setLevel(Level.OFF);
	}
	
	public void closeAllWindows(){
		this.webClient.closeAllWindows();
	}

	/**
	 * @param url
	 * @return it also increases the number of crawl queries
	 */
	public HtmlPage getThePage(String url) {

		// url = url.replace("the", "hi");
		try {
			page = webClient.getPage(url);
/*			crawlReport.incNoCrawlingQueries();
*/		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}/*catch (SocketException e){
			e.printStackTrace();
		}*/
		return page;
	}
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }


	public String getPageWebServ(String pageLink, String method, boolean snapshot) {
		String url = "http://bookstore.ewi.utwente.nl:12346/";
		url = url+ "?url="+pageLink;
		url = url+ "&method="+method;
		if(snapshot){
			url = url + "&take_snapshot";
		}
		return url;
	}
}
