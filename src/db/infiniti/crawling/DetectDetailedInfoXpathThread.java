package db.infiniti.crawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import db.infiniti.config.CrawlingConfig;
import db.infiniti.config.CrawlingReportDS;
import db.infiniti.config.DetailedInfoXPathDetectionDS;
import db.infiniti.srf.SearchResultFinder;

public class DetectDetailedInfoXpathThread extends Thread {
	DetailedInfoXPathDetectionDS detailedInfoXpathDetector;
	CrawlingConfig crawlingConfig;
	CrawlingReportDS crawlReport;
	boolean runFlag = true;
	String linkXPath;
	
	public DetectDetailedInfoXpathThread(CrawlingConfig crawlingConfig,
			CrawlingReportDS crawlReport) {
		super();
		this.crawlingConfig = crawlingConfig;
		this.crawlReport = crawlReport;
	}

	public void run() {
		while (runFlag) {

		}
		detailedInfoXpathDetector.getAllTheResultSnippets();
	}

	public void stopThread() {
		runFlag = false;
		this.stop();
	}

	public void process() {
		System.out.println("Hi. It works.");
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

	public void detectLinkXpath() {
		/*
		 * testEmptyPage = getThePage(urlSetting
		 * .setTestQuery("abcdefghijklmnopqrstuvwxvz"));
		 * crawlReport.incNoCrawlingQueries(); pageByNotStopWord = testEmptyPage
		 * = getThePage(urlSetting .setTestQuery("content"));
		 * crawlReport.incNoCrawlingQueries();
		 */

		boolean firstTryForXpath = true;
		HtmlPage page;
		/*
		 * String testEmptyPageXML = testEmptyPage.asXml(); String pageSWordXml
		 * = pageByStopWord.asXml(); String pageNoSWordsXML =
		 * pageByNotStopWord.asXml(); if
		 * (testEmptyPageXML.equalsIgnoreCase(pageSWordXml)) { System.out
		 * .println("Page retrieved by posing stop words is empty."); } else {
		 * chosenHtmlPage = pageByStopWord; } if
		 * (testEmptyPageXML.equalsIgnoreCase(pageNoSWordsXML)) { System.out
		 * .println("Page retrieved by posing 'content' is empty."); } else if
		 * (chosenHtmlPage == null) { chosenHtmlPage = pageByNotStopWord; }
		 */
		// detect results xpath if first time crawl of website
		if (firstTryForXpath) {
			Vector<String> srfXpathOfMenues = SearchResultFinder
					.robustSRF(crawlingConfig
							.setTestQuery("abcdefghijklmnopqrstuvwxvz"));
			crawlReport.incNoCrawlingQueries();
			Vector<String> srfXpathRes = SearchResultFinder
					.robustSRF(crawlingConfig.getCurrentURL());
			srfXpathRes = refineFoundXpaths(srfXpathOfMenues, srfXpathRes);
			if (srfXpathRes.isEmpty()) {// both with stopwords and
										// non-stopwords
				srfXpathRes = SearchResultFinder.robustSRF(crawlingConfig
						.setTestQuery("content"));
				/*
				 * crawlReport.incNoCrawlingQueries();
				 */// one of the non stop-words - most frequent one
				srfXpathRes = refineFoundXpaths(srfXpathOfMenues, srfXpathRes);
				if (srfXpathRes.isEmpty()) {
					System.err
							.println("Unable to determine SearchResult xPath for query 'content': "
									+ crawlingConfig.getCurrentURL());
					this.stopThread();
				} else {
					page = crawlingConfig.getWebTools().getThePage(
							crawlingConfig.setTestQuery("content"));
					srfXpathRes.trimToSize();
					linkXPath = (String) srfXpathRes.get(0);
				}
			} else {
				// xpath = CheckxPathWithUser(xpath);
				linkXPath = (String) srfXpathRes.get(0);
			}
			// don't repeat xpath finder for every next result page
			firstTryForXpath = false;
			/*
			 * crawlReport.incNoCrawlingQueries();
			 */}
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
}
