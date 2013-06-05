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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import db.infiniti.config.CrawledLinkDS;
import db.infiniti.config.CrawlingConfig;
import db.infiniti.config.CrawlingReportDS;
import db.infiniti.config.DetailedInfoXPathDetectionDS;
import db.infiniti.config.QueryResStatistics;
import db.infiniti.sitedescription.WebsiteDS;
import db.infiniti.srf.Browser;
import db.infiniti.xpath.ResultLinkXpathFinder;

/**
 * @author mohammadreza
 * 
 */

public class CrawlerSellenium {

	ArrayList<String> listOfReturnedResults;
	String url;
	// Urlsetting urlSetting;
	String sourceURL;
	ArrayList<String> queries;
	boolean firstTryForLinkDetection = true;
	boolean firstTryForXpath = true;
	boolean firstTimeCrawl = true;
	ArrayList<String> linkDetectionxPath = new ArrayList<String>();
	String nextResultPagexPath = null;
	ResultLinkXpathFinder resultLinkXpathFinder = new ResultLinkXpathFinder();
	CrawlingReportDS crawlReport;
	DetailedInfoXPathDetectionDS detailedInfoXpathDetector;
	String saveLinkPath;
	boolean resultsXPathIsset = false;
	CrawlingConfig crawlingConfig;
	WebsiteDS siteDes;
	String item_xp;
	String link_xp;
	String title_xp;
	String desc_xp;
	String thumb_xp;
	HtmlPage emptyPage;
	CrawledLinkDS crawledLinkDS;
	Browser sRPagesbrowser;
	Browser detailedPagesbrowser;

	
	public CrawlerSellenium(CrawlingConfig crawlingConfig,
			ArrayList<String> listOfReturnedResults) {
		this.crawlingConfig = crawlingConfig;
		this.listOfReturnedResults = listOfReturnedResults;
		crawlReport = new CrawlingReportDS();
		queries = crawlingConfig.getQueries();
		saveLinkPath = crawlingConfig.getLinkContentSavePath();
		siteDes = crawlingConfig.getCurrentSiteDescription();
		item_xp = siteDes.getItemXPath();
		link_xp = siteDes.getLinkXPath();
		title_xp = siteDes.getTitleXPath();
		desc_xp = siteDes.getDescXPath();
		thumb_xp = siteDes.getThumbNXPath();
	}

	public void run() {
		boolean firstRunForWebsite = true;
	//	HtmlPage page = null;
		int repeatedLinks = 0;
		boolean continueCrawl = true;
		int posedQueiesIndex = 0;
		// checkForAcceptingStopWords();
		crawlingConfig.resetForNextCollection();
		url = crawlingConfig.setNextQuery();
		posedQueiesIndex++;
		crawlingConfig.setCurrentURL(url);
		List<HtmlAnchor> nextPageResultLink = null;
		int noOfReturnedResultsPages = 0;
		sRPagesbrowser = crawlingConfig.getScrShotBrowser();
		detailedPagesbrowser = crawlingConfig.getDetailedPageBrowser();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;
		Element rootElement = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("crawlResults");
			doc.appendChild(rootElement);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (continueCrawl) {
			noOfReturnedResultsPages++;
			if (firstRunForWebsite) {
				sourceURL = url;
			}
			System.out.println("Link: " + url);
			String pageSource = sRPagesbrowser.getPageSource(url);

			crawlReport.incNoCrawlingQueries();
			saveRetunredResultPage(pageSource, noOfReturnedResultsPages);
			Element srElem = doc.createElement("SearchResultPage");
			rootElement.appendChild(srElem);
			srElem.setAttribute("id", noOfReturnedResultsPages+"");
			
			crawlingConfig.updateQueryList(sRPagesbrowser.getPageText());
			
			List<WebElement> elementList = null;
			if (item_xp == null) {
				System.err
						.println("Unable to determine SearchResult xPath for: "
								+ url);
				System.err.flush();
				this.stopThread();
			} else {
				// this.item_xp = "//table[./tbody/tr/td/a]";
				// extract results as html-elements by using the given xpath
				if (item_xp.contains("\\")) {
					item_xp = item_xp.replace("\\", "");
				}
				elementList = (List<WebElement>) sRPagesbrowser
						.runXPathQuery(this.item_xp);
			}
			// add results to return list
			// extract details
			if (!elementList.isEmpty()) {
				int i = 0;
				for (WebElement HE : elementList) {
					Element resultElem = doc.createElement("Result");
					srElem.appendChild(resultElem);
					resultElem.setAttribute("id", i+"");
					i++;
					
					crawledLinkDS = new CrawledLinkDS();
					// crawledLinkDS.setItemXML(HE);
					/*
					 * if (resultsXPathIsset) { // age peyda shode unvaght
					 * extract kon
					 * detailedInfoXpathDetector.addSnippetToList(HE); }
					 */
					if (!link_xp.equals("?") && !link_xp.equals("")) {
						if (link_xp.contains("\\")) {
							link_xp = link_xp.replace("\\", "");
						}
						if (link_xp.startsWith("//")) {
							link_xp = link_xp.replace("//", "/");
						}
						// link_xp = "/ul/li/a";
						String resultLink = returnLinkInItem(HE, link_xp);
						resultLink = getLinkedURL(url, resultLink);// complete
																	// the
																	// incomplete
																	// links
						System.out.println("Extracted link: " + resultLink);
						crawledLinkDS.setLink(resultLink);
						Element aElem = doc.createElement("a");
						resultElem.appendChild(aElem);
						aElem.setAttribute("href", resultLink);
					//	.appendChild(doc.createTextNode("yong"));
						if (!listOfReturnedResults.contains(resultLink)) {
							// TODO check 
							//it loads the page
							this.setTextHtmlOfLink(detailedPagesbrowser.getPageSource(resultLink));
							//crawledLinkDS.getLinkTextContent();
							crawledLinkDS.setLinkTextContent(detailedPagesbrowser.getPageText());
							crawlingConfig.updateQueryList(crawledLinkDS.getLinkTextContent());

							crawlReport.incNoCrawlingQueries(); //
							listOfReturnedResults.add(resultLink);
							aElem.setAttribute("repeated", "No");
						} else {
							repeatedLinks++;// for one query
							crawlReport.incNoRepeatedLinks(); // in total
							aElem.setAttribute("repeated", "yes");
							// comment it is repeated link
						}
					}
					Element titleElem = doc.createElement("title");
					resultElem.appendChild(titleElem);
					if (title_xp != null && !title_xp.equals("")
							&& !title_xp.equals("?")) {
						String title = findTitle(HE, title_xp);
						crawledLinkDS.setTitle(title);
						titleElem.appendChild(doc.createTextNode(title));
					} else {
						crawledLinkDS.setTitle("No title_xpath.");
						titleElem.appendChild(doc.createTextNode("No title_xpath."));
					}
					
					Element descElem = doc.createElement("description");
					resultElem.appendChild(descElem);
					if (desc_xp != null && !desc_xp.equals("")
							&& !desc_xp.equals("?")) {
						String desc = findDesc(HE, desc_xp);
						crawledLinkDS.setDescription(desc);
						descElem.appendChild(doc.createTextNode(desc));

					} else {
						crawledLinkDS.setDescription("No description xpath.");
						descElem.appendChild(doc.createTextNode("No description xpath."));
					}
					
					// what to return for thumbXP
					// TODO how to deal with thumbnail
					Element thumbElem = doc.createElement("description");
					resultElem.appendChild(thumbElem);
					if (!thumb_xp.equals("") && !thumb_xp.equals("")
							&& !thumb_xp.equals("?")) {
						crawledLinkDS.setThumbLink(this.findThumbnailLink(HE,
								thumb_xp));
						thumbElem.appendChild(doc.createTextNode("Thmbnail Extracted and Saved."));

					} else {
						crawledLinkDS.setThumbLink("No thumb_xpath.");
						thumbElem.appendChild(doc.createTextNode("No thumbnail xpath."));

					}

					saveExtractedInfoOfLink(crawledLinkDS);
					if((listOfReturnedResults.size()) >= 4900){
						continueCrawl = false;
						System.out.println("Harvesting Completed!");
						System.out.print(" repetition in total = " + crawlReport.getNumRepeatedLinks());
					}
				}
			} else if (firstRunForWebsite && this.item_xp != null) {
				siteDes.setAcceptsStopWords(false);
			}
			firstRunForWebsite = false;

			if (this.nextResultPagexPath == null) {
				detectNextButtonXpath();
			}
			url = null;
			// if no next link, then, next query
			url = setURLNextResultPage();
			if (url != null) {
				url = getLinkedURL(sourceURL, url);
				if (url != null) {
					System.out.println("Next ResultPage");
				}
			} else if (url == null) {// no next page for results
				crawlReport.addQueryNumberofItsResults(
						//queries.get(crawlingConfig.getQueryIndex() - 1),
						crawlingConfig.query, 
						(crawlingConfig.getQueryIndex() - 1),
						listOfReturnedResults.size(),
						repeatedLinks);
				url = crawlingConfig.setNextQuery();
				posedQueiesIndex++;
				repeatedLinks = 0;
			}
			if (url == null) {// there is no next query or next result page for query
				continueCrawl = false;
			}
			
			
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("crawledData/"+crawlingConfig.getCollectionName()+"/crawledresults.xml"));
			transformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printQueriesResults();
	

 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		System.out.println("SourceUrl: " + this.sourceURL);
		System.out.println("xPath " + this.item_xp);
		stopThread();
	}

	private void saveRetunredResultPage(String pageAsXML,
			int noOfReturnedResultsPages) {
		try {
			File saveDir = new File(saveLinkPath + "resultPages" + "/");
			if (!saveDir.exists()) {
				saveDir.mkdir();
			}
			String filePath = saveLinkPath + "resultPages" + "/"
					+ noOfReturnedResultsPages;

			crawlingConfig.getScrShotBrowser().captureSc(filePath);

			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<number> \n" + noOfReturnedResultsPages
					+ "\n</number>\n");
			out.flush();
			out.write("<html>\n" + pageAsXML + "\n</html>\n");
			out.flush();
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	private String findTitle(WebElement wE, String title_xp) {
		String result = "";
		if (!title_xp.equals(".")) {

		} else {
			// TODO check if this works
			return wE.getText();
		}
		if (title_xp.startsWith(".")) {
			title_xp = title_xp.replaceFirst(".", "");
		}
		if (title_xp.endsWith(";")) {
			title_xp = title_xp.replaceFirst(";", "");
		}
		WebElement links = null;
		links = wE.findElement(By.xpath("./" + title_xp));
		if (links == null) {
			System.out.println("Could not find the titleby the xpath: ");
		} else {
			// result = links.get(0).getNodeValue();
			// TODO not href
			result = links.getText();
		}
		return result;
	}

	private void checkForAcceptingStopWords() {

		HtmlPage stopWordPage = crawlingConfig.getWebTools().getThePage(
				crawlingConfig.setTestQuery("the"));
		if (isEmptyPage(stopWordPage)) {// has the same number of nodes as the
										// empty page
			System.out.println("Not accepting stop words.");
			siteDes.setAcceptsStopWords(false);
		} else {
			siteDes.setAcceptsStopWords(true);
		}
	}

	private boolean isEmptyPage(HtmlPage page) {
		//TODO change to adopt to browser xpath finder
		// String textFromTheQuery = page.asXml();// .replaceAll("the", "");
		if (emptyPage == null) {
			emptyPage = crawlingConfig
					.getWebTools()
					.getThePage(
							crawlingConfig
									.setTestQuery("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"));
		}
		int numNodesInThePage = page.getByXPath("//*").size();
		int numNodesInEmptyPage = emptyPage.getByXPath("//*").size();

		if (areCloseEnough(numNodesInEmptyPage, numNodesInThePage)) {
			return true;
		} else {
			return false;
		}
	}

	// to check if the two values are in each others' vicinity
	private boolean areCloseEnough(int numNodesInEmptyPage,
			int numNodesInThePage) {
		double a = ((double) numNodesInThePage - (double) numNodesInThePage);
		if ((numNodesInThePage - numNodesInEmptyPage) >= -10
				&& (numNodesInThePage - numNodesInEmptyPage) <= 10) {
			return true;
		}
		return false;
	}

	protected String findDesc(WebElement addressComponent, String descXPath) {
		String result = "";
		/*
		 * Iterable<HtmlElement> iterChild=
		 * addressComponent.getHtmlElementDescendants();
		 */
		// numOfTries is zero for the first time, if cannot find xpath in
		// first try, increased
		if (descXPath.equals(".")) {// remove the title from whole text and
									// return the remaining as description
			result = addressComponent.getText();
			if (result.contains(crawledLinkDS.getTitle())) {
				result.replace(crawledLinkDS.getTitle(), "");
			}
		} else {
			if (descXPath.startsWith(".")) {
				descXPath = descXPath.replaceFirst(".", "");
			}
			/*
			 * if (descXPath.startsWith("//")){ descXPath =
			 * descXPath.replaceFirst("//", "/"); }
			 */
			if (descXPath.endsWith(";")) {
				descXPath = descXPath.replaceFirst(";", "");
			}
			WebElement links = null;
			links = addressComponent.findElement(By.xpath("./" + desc_xp));

			if (links == null) {
				System.out
						.println("Could not find the description by the xpath: ");
			} else {
				// result = links.get(0).getNodeValue();
				result = links.getText();
			}
		}
		return result;
	}

	protected String findThumbnailLink(WebElement addressComponent,
			String thumbXPath) {
		String result = "";
		/*
		 * Iterable<HtmlElement> iterChild=
		 * addressComponent.getHtmlElementDescendants();
		 */
		// numOfTries is zero for the first time, if cannot find xpath in
		// first try, increased

		if (thumbXPath.startsWith(".")) {
			thumbXPath = thumbXPath.replaceFirst(".", "");
		}
		/*
		 * if (descXPath.startsWith("//")){ descXPath =
		 * descXPath.replaceFirst("//", "/"); }
		 */
		if (thumbXPath.endsWith(";")) {
			thumbXPath = thumbXPath.replaceFirst(";", "");
		}
		WebElement links = null;
		links = addressComponent.findElement(By.xpath("./" + thumbXPath));

		if (links == null) {
			System.out.println("Could not find thumbnail by the xpath: ");
		} else {
			// result = links.get(0).getNodeValue();
			result = links.getText();
		}
		return result;
	}

	public void stopThread() {
		// listOfReturnedResults reset
		// represents links extracted only for one web source
		listOfReturnedResults.clear();
		this.nextResultPagexPath = null;
	}

	/**
	 * @param nextPageResultLink
	 *            if no nextResultsPage, set next query
	 * @return url
	 */
	private String setURLNextResultPage() {
		List<WebElement> nextPageResultLink = null;
		if (nextResultPagexPath != null) {
		/*	nextPageResultLink = (List<HtmlAnchor>) page
					.getByXPath(nextResultPagexPath);*/
			nextPageResultLink = (List<WebElement>) sRPagesbrowser
					.runXPathQuery(this.nextResultPagexPath);
		}
		if (nextPageResultLink != null) {
			if (nextPageResultLink.size() > 0) {
				Object tempO = nextPageResultLink.get(0);
				if(tempO instanceof HtmlAnchor){
					HtmlAnchor a = (HtmlAnchor) nextPageResultLink.get(0);
					url = a.getHrefAttribute();
				}else if (((WebElement) tempO).getTagName().equalsIgnoreCase("a")){
					url = ((WebElement) tempO).getAttribute("href");
				}
/*				url = nextPageResultLink.get(0).getAttribute("href");
*/			}
		}
		/*
		 * crawlReport.addQueryNumberofItsResults(queries.get(crawlingConfig.
		 * getQueryIndex()), listOfReturnedResults.size() + "");
		 */
		/*
		 * else {
		 * crawlReport.addQueryNumberofItsResults(queries.get(queryIndex),
		 * listOfReturnedResults.size() + ""); url =
		 * crawlingConfig.setNextQuery(queryIndex); url =
		 * getLinkedURL(sourceURL, url); // to complete nextResultPage link
		 * queryIndex++; }
		 */
		if (url != null) {
			if (url.equals("")) {
				return null;
			}
		}

		return url;
	}

	/**
	 * @param page
	 *            sets nextResultPagexPath
	 * @return list of candidates for
	 */
	@SuppressWarnings("unchecked")
	private void detectNextButtonXpath() {
		List<WebElement> nextPageResultLink;
		if (nextResultPagexPath == null) {
			/*nextPageResultLink = (List<HtmlAnchor>) page
					.getByXPath("//a[contains(lower-case(.), 'next')]");*/
			nextPageResultLink = (List<WebElement>) sRPagesbrowser
			.runXPathQuery("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'next')]");
			// only for xpath 1.0 => "//a[contains(lower-case(.), '')]"
			if (!nextPageResultLink.isEmpty()) {
				nextResultPagexPath = "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'next')]";
			} else {
				/*nextPageResultLink = (List<HtmlAnchor>) page
						.getByXPath("//a[contains(lower-case(.), 'volgende')]");*/
				nextPageResultLink = (List<WebElement>) sRPagesbrowser
						.runXPathQuery("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'volgende')]");
				// only for xpath 1.0 => "//a[contains(lower-case(.), 'volgende')]"
				if (!nextPageResultLink.isEmpty()) {
					nextResultPagexPath = "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'volgende')]";
				} else {
	/*				nextPageResultLink = (List<HtmlAnchor>) page
							.getByXPath("//a[contains(lower-case(.), 'previous')]");*/
					//TODO check if previous works for all the pages  => done
					nextPageResultLink = (List<WebElement>) sRPagesbrowser
							.runXPathQuery("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'previous')]"); // for djoerd website
					if (!nextPageResultLink.isEmpty()) {
						nextResultPagexPath = "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'previous')]";
					}
				}
			}
			if (nextPageResultLink.isEmpty()) {
				System.out
						.println("Could not detect the next page xpath and link for next page of results.");
			}else{//it might be the cxase to have a page without any next button in the first try
				firstTimeCrawl = false;
			}
		}
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
		System.out.println("Index:			" +"  Query:			" + "		#Crawled Results" + "      " + "    #Repeated");
		while (e.hasNext()) {
			String query = (String) e.next();
			QueryResStatistics qResStat = crawlReport.getQueryNumberofItsResults().get(query);
			System.out.println(qResStat.getqPosedIndex() + " \t\t\t" +query + "			\t\t\t" + qResStat.getUniqResults() + "    \t\t\t" + qResStat.getRepeatedResults());
		}
	}

	protected String returnLinkInItem(WebElement addressComponent,
			String link_xp) {
		String result = "";
		String linkXP = link_xp;
		WebElement links = null;
		if (linkXP.startsWith(".")) {
			linkXP = linkXP.replaceFirst(".", "");
		}
		links = addressComponent.findElement(By.xpath("./" + linkXP));

		if (links == null) {
			System.out
					.println("Could not find the link of the result for the xpath: ");
			siteDes.setComments(siteDes.getComments() + "\n"
					+ "Could not find the link of the result for the xpath");
		} else {
			Object tempR = links;
			if (tempR instanceof HtmlAnchor) {
				String name = ((HtmlAnchor) tempR).getNameAttribute();
				String value = ((HtmlAnchor) tempR).getHrefAttribute();
				result = ((HtmlAnchor) tempR).getHrefAttribute();
			} else {
				// String name = links.get(0).getName();
				String value = links.getText();
				result = links.getAttribute("href");
			}
		}
		return result;
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
		//TODO change to adopt to browser xpath finder

		String result = "";
		String componentxPath = addressComponent.getCanonicalXPath();
		/*
		 * Iterable<HtmlElement> iterChild=
		 * addressComponent.getHtmlElementDescendants();
		 */
		if (findxPath) {
			// numOfTries is zero for the first time, if cannot find xpath in
			// first try, increased
			linkDetectionxPath = resultLinkXpathFinder.findResultLinkxPath(
					addressComponent, item_xp, numOfTries);
			firstTryForLinkDetection = false;
		}

		List<HtmlElement> links = null;
		for (String linkxPath : linkDetectionxPath) {
			links = (List<HtmlElement>) addressComponent
					.getByXPath(componentxPath + linkxPath);
			if (!links.isEmpty()) {
				break;
			}
		}

		if (links == null) {
			System.out
					.println("Could not find the link address by the xpath: ("
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
	private void saveExtractedInfoOfLink(CrawledLinkDS crawledLinkDS) {
		/*
		 * <?xml version="1.0" encoding="ISO-8859-1"?> <html
		 * xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
		 */
		try {
			String filePath = saveLinkPath + "link"
					+ this.listOfReturnedResults.size();
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<title> \n" + crawledLinkDS.getTitle() + "\n</title>\n");
			out.flush();
			out.write("<description>\n" + crawledLinkDS.getDescription()
					+ "\n</description>\n");
			out.flush();
			out.write("<link>\n " + crawledLinkDS.getLink() + "\n</link>\n");
			out.flush();
			out.write("<text>\n " + crawledLinkDS.getLinkTextContent()
					+ "\n</text>\n");
			out.flush();
			out.write("<html> \n" + crawledLinkDS.getLinkHtmlContent()
					+ "\n</html>\n");
			out.flush();
			out.write("<thumblink> \n" + crawledLinkDS.getThumbLink()
					+ "\n</thumblink>\n");
			out.flush();
			out.write("<itemXML>\n " + crawledLinkDS.getItemXML()
					+ "\n</itemXML>\n");
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
	private String setTextHtmlOfLink(String content) {
	//	HtmlPage page = null;
		String textContent = null;
		// WebClient webClient = new WebClient();
		try {
			// webClient.setJavaScriptTimeout(60000);
			// TODO check if there is only text version in htmlunit
		//	crawledLinkDS.setLinkTextContent(content);
			crawledLinkDS.setLinkHtmlContent(content);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (com.gargoylesoftware.htmlunit.ScriptException e) {
			textContent = "ScriptException. Could not extract the text."
					+ e.toString();
			System.out.println("ScriptException. Could not extract the text.");
		}
	/*	if (page != null) {
			textContent = content;
		}*/
		return textContent;
	}
}
