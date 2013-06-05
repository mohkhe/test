package db.infiniti.crawling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import NoHtmlUnitWarning.QuietCssErrorHandler;
import NoHtmlUnitWarning.SilentIncorrectnessListener;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import db.infiniti.config.CrawlingReportDS;
import db.infiniti.config.DetailedInfoXPathDetectionDS;
import db.infiniti.config.Urlsetting;
import db.infiniti.xpath.ResultLinkXpathFinder;

/**
 * @author mohammadreza
 * 
 */

public class TestWSCrawlingThread{

	ArrayList<String> listOfReturnedResults;
	String url;
	Urlsetting urlSetting;
	String itemXP;
	String sourceURL;
	int queryIndex;
	ArrayList<String> queries;
	boolean firstTryForLinkDetection = true;
	boolean firstTryForXpath = true;
	boolean firstTimeCrawl = true;
	ArrayList<String> linkDetectionxPath = new ArrayList<String>();
	String nextResultPagexPath = "";
	ResultLinkXpathFinder resultLinkXpathFinder = new ResultLinkXpathFinder();
	CrawlingReportDS crawlReport;
	DetailedInfoXPathDetectionDS detailedInfoXpathDetector;
	String saveLinkPath;
	boolean resultsXPathIsset = false;
	JSONObject objectJson;

	String titleXP = "";
	String desXP = "";
	String thumbXP = "";
	
	public TestWSCrawlingThread(String tempURL, String itemXP, String linkXP,
			String titleXP, String desXP, String thumbXP, JSONObject objectJson) {
		this.listOfReturnedResults = new ArrayList<String>();
		crawlReport = new CrawlingReportDS();
		this.url = tempURL;
		linkDetectionxPath.add(linkXP);
		this.itemXP = itemXP;
		this.objectJson = objectJson;
		this.titleXP = titleXP;
		this.desXP = desXP;
		this.thumbXP = thumbXP;
	}

	@SuppressWarnings("unchecked")
	public void run() {

		sourceURL = url;
		HtmlPage page = null;
		List<HtmlAnchor> nextPageResultLink = null;
		while (url != null) {
			// connect and get the page
			page = getThePage(url);
			try {
				objectJson.put("htmlPage", page.asXml());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			crawlReport.incNoCrawlingQueries();

			if (itemXP == null) {
				System.err
						.println("Unable to determine SearchResult xPath for: "
								+ url);
				System.err.flush();
				this.stopThread();
			}
			// extract results as html-elements by using detected xpath
			List<HtmlElement> elementList = (List<HtmlElement>) page
					.getByXPath(itemXP);
			// add results to list
			try {
				objectJson.put("itemXP", elementList.size());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(elementList.size()>0){
				JSONArray returnedResults = new JSONArray();
				JSONArray returnedResultsDesc = new JSONArray();
				JSONArray returnedResultsTitle = new JSONArray();
				int thumbs = 0;
				for (HtmlElement HE : elementList) {
					String resultLink = findURLsOfResults(HE, itemXP, false, 0);
					resultLink = getLinkedURL(url, resultLink);
					returnedResults.put(resultLink);
					if (!listOfReturnedResults.contains(resultLink)) {
						listOfReturnedResults.add(resultLink);
					} else {
						crawlReport.incNoRepeatedLinks();
					}
					if(!desXP.equals("")){
						returnedResultsDesc.put(findDesc(HE, desXP));
					}
					if(!titleXP.equals("")){
						returnedResultsTitle.put(findDesc(HE, titleXP));
					}
					//what to return for thumbXP
/*					if(!thumbXP.equals("")){
						findDesc(HE, thumbXP);
					}*/
				}
				try {
					objectJson.put("linkXP", listOfReturnedResults.size());
					objectJson.put("rrLinks", 1);
					objectJson.put("rrDescs", 2);
					objectJson.put("rrTitles", 3);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			nextPageResultLink = detectNextButtonXpath(page);
			url = null;
		}
		printQueriesResults();
		System.out.println("SourceUrl: " + this.sourceURL);
		System.out.println("xPath " + this.itemXP);
		if (linkDetectionxPath.size() > 0) {
			System.out.println("linkXPath " + linkDetectionxPath.get(0));
		}

		stopThread();
	}

	protected String findDesc(HtmlElement addressComponent, String descXPath) {
		String result = "";
		String componentxPath = addressComponent.getCanonicalXPath();
		/*
		 * Iterable<HtmlElement> iterChild=
		 * addressComponent.getHtmlElementDescendants();
		 */
			// numOfTries is zero for the first time, if cannot find xpath in
			// first try, increased
		if (descXPath.startsWith(".")){
			descXPath = descXPath.replaceFirst(".", "");
		}
		List<HtmlElement> links = null;
		links = (List<HtmlElement>) addressComponent
					.getByXPath(componentxPath + descXPath);

		if (links == null) {
			System.out
					.println("Could not find the link of the result for the xpath: ("
							+ componentxPath + " ) , ( " + linkDetectionxPath);
		} else {
			result = links.get(0).getTextContent();
		}
		return result;
	}
	// remove menu xpaths from the found xpaths
	private Vector<String> refineFoundXpaths(Vector<String> srfXpathOfMenues,
			Vector<String> srfXpathRes) {
		for (String foundInTestTryxPath : srfXpathOfMenues) {
			if (srfXpathRes.contains(foundInTestTryxPath)) {
				srfXpathRes.removeElement(foundInTestTryxPath);
			}
		}
		return srfXpathRes;
	}

	@SuppressWarnings("deprecation")
	public void stopThread() {
		listOfReturnedResults.clear();
	}

	/**
	 * @param url
	 * @return it also increases the number of crawl queries
	 */
	private HtmlPage getThePage(String url) {
		HtmlPage page = null;
		WebClient webClient = new WebClient();
		webClient.setIncorrectnessListener(new SilentIncorrectnessListener());
		webClient.setCssErrorHandler(new QuietCssErrorHandler());
		webClient.setCssEnabled(false);
		webClient.setThrowExceptionOnFailingStatusCode(false);
		webClient.setThrowExceptionOnScriptError(false);
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
		// url = url.replace("the", "hi");
		try {
			page = webClient.getPage(url);
			crawlReport.incNoCrawlingQueries();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		webClient.closeAllWindows();
		return page;
	}

	/**
	 * @param nextPageResultLink
	 *            if no nextResultsPage, set next query
	 * @return url
	 */
	private String setNextURL(List<HtmlAnchor> nextPageResultLink) {
		if (nextPageResultLink.size() > 0) {
			url = nextPageResultLink.get(0).getAttribute("href");
			crawlReport.addQueryNumberofItsResults(queries.get(queryIndex),
					listOfReturnedResults.size() + "");
		} else {
			crawlReport.addQueryNumberofItsResults(queries.get(queryIndex),
					listOfReturnedResults.size() + "");
			url = urlSetting.setNextQuery();
			url = getLinkedURL(sourceURL, url);
			// to complete nextResultPage link
			queryIndex++;
		}
		return url;
	}

	/**
	 * @param page
	 *            sets nextResultPagexPath
	 * @return list of candidates for
	 */
	@SuppressWarnings("unchecked")
	private List<HtmlAnchor> detectNextButtonXpath(HtmlPage page) {
		List<HtmlAnchor> nextPageResultLink;
		if (firstTimeCrawl) {
			nextPageResultLink = (List<HtmlAnchor>) page
					.getByXPath("//a[contains(lower-case(.), 'next')]");
			if (!nextPageResultLink.isEmpty()) {
				nextResultPagexPath = "//a[contains(lower-case(.), 'next')]";
			} else {
				nextPageResultLink = (List<HtmlAnchor>) page
						.getByXPath("//a[contains(lower-case(.), 'volgende')]");
				if (!nextPageResultLink.isEmpty()) {
					nextResultPagexPath = "//a[contains(lower-case(.), 'volgende')]";
				} else {
					nextPageResultLink = (List<HtmlAnchor>) page
							.getByXPath("//a[contains(lower-case(.), 'previous')]");
					if (!nextPageResultLink.isEmpty()) {
						nextResultPagexPath = "//a[contains(lower-case(.), 'previous')]";
					}
				}
			}
			if (nextPageResultLink.isEmpty()) {
				System.out
						.println("Could not detect the next page xpath and link for next page of results.");
			}
			firstTimeCrawl = false;
		} else {
			nextPageResultLink = (List<HtmlAnchor>) page
					.getByXPath(nextResultPagexPath);
		}
		return nextPageResultLink;
	}

	/**
	 * 
	 */
	private void printQueriesResults() {
		System.out
				.println("Total number of queries sent to the search engine - crawling: "
						+ crawlReport.getCountCrawlingQueries());
		Iterator<String> e = crawlReport.getQueryNumberofItsResults().keySet()
				.iterator();
		System.out.println("Number of Returned Results");
		while (e.hasNext()) {
			String query = (String) e.next();
			String freq = listOfReturnedResults.size() + "";
			System.out.println(query + "			" + "			" + freq);
		}
	}

	/**
	 * @param xpath
	 * @return
	 */
	private String CheckxPathWithUser(String xpath) {
		System.out.println("This is the found xpath for this link: " + xpath
				+ "\n Is this the correct one?(Y/newXpath)");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (line.equalsIgnoreCase("y")) {
			return xpath;
		} else {
			xpath = line;
		}
		return line;
	}

	/**
	 * @param sourceURL
	 * @param addressComponent
	 * @param xPathSource
	 * @param findxPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String findURLsOfResults(HtmlElement addressComponent,
			String xPathSource, boolean findxPath, int numOfTries) {
		String result = "";
		String componentxPath = addressComponent.getCanonicalXPath();
		/*
		 * Iterable<HtmlElement> iterChild=
		 * addressComponent.getHtmlElementDescendants();
		 */
/*		if (findxPath) {
			// numOfTries is zero for the first time, if cannot find xpath in
			// first try, increased
			linkDetectionxPath = resultLinkXpathFinder.findResultLinkxPath(
					addressComponent, itemXP, numOfTries);
			firstTryForLinkDetection = false;
		}*/

		List<HtmlElement> links = null;
		for (String linkxPath : linkDetectionxPath) {
			if (linkxPath.startsWith(".")){
				linkxPath = linkxPath.replaceFirst(".", "");
			}
			links = (List<HtmlElement>) addressComponent
					.getByXPath(componentxPath + linkxPath);
			if (!links.isEmpty()) {
				break;
			}
		}

		if (links == null) {
			System.out
					.println("Could not find the link of the result for the xpath: ("
							+ componentxPath + " ) , ( " + linkDetectionxPath);
			/*
			 * numOfTries++; if ( numOfTries <
			 * resultLinkXpathFinder.getCandidatesForEachElement
			 * ().get(addressComponent).size()){ result =
			 * findURLsOfResults(addressComponent, xPathSource, true,
			 * numOfTries); // adds another xpath }
			 */
		} else {
			result = links.get(0).getAttribute("href");
		}
		return result;
	}

	/**
	 * @param link
	 * @return
	 */
	@SuppressWarnings("unused")
	private String CheckLinkWithUser(String link) {
		System.out.println("Is this the correct link for a rsult: " + link
				+ "\n Is this the correct one?(Y/N)");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (line.equalsIgnoreCase("y")) {
			return "yes";
		} else {
			return "no";
		}
	}

	/**
	 * @param xPathSource
	 * @return
	 */
	@SuppressWarnings("unused")
	private String refinexPath(String xPathSource) {
		xPathSource = xPathSource.replaceFirst("//[a-z]*.", "");
		xPathSource = xPathSource.replace("[", "");
		xPathSource = xPathSource.replace("]", "");
		xPathSource = xPathSource.replace(".", "");
		return xPathSource;
	}

	/**
	 * @param originalURL
	 * @param linkedPath
	 * @return completed form of URL (with http) by Victor
	 */
	public String getLinkedURL(String originalURL, String linkedPath) {
		URL origURL = null;
		try {
			origURL = new URL(originalURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (linkedPath.startsWith("/")) {
			String portSuffix = origURL.getPort() == -1 ? "" : ":"
					+ origURL.getPort();
			linkedPath = origURL.getProtocol() + "://" + origURL.getHost()
					+ portSuffix + linkedPath;
		} else if (!linkedPath.startsWith("http")) {
			String originalUrlString = origURL.toString();
			linkedPath = originalUrlString.substring(0,
					originalUrlString.lastIndexOf("/") + 1)
					+ linkedPath;
		}
		return linkedPath;
	}

	/**
	 * @param textContentOfPage
	 * @param url
	 * @param index
	 */
	private void saveTextContentOfPage(String textContentOfPage, String url,
			int index) {
		try {
			String filePath = saveLinkPath + "link" + index;
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(url + "\n");
			out.flush();
			out.write(textContentOfPage);
			out.flush();
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * @param url
	 * @return
	 */
	private String extractTextFromLink(String url) {
		HtmlPage page = null;
		WebClient webClient = new WebClient();
		try {
			page = webClient.getPage(url);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String textContent = page.asText();
		return textContent;
	}
}
