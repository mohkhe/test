package db.infiniti.crawling;

import java.util.ArrayList;

import db.infiniti.config.CrawlingConfig;
import db.infiniti.webtools.WebTools;

public class MainClass extends Thread {
	ArrayList<String> listOfSources;
	static CrawlingConfig crawlingConfig;

	public static void main(String[] args) {

		ArrayList<String> listOfReturnedResults = new ArrayList<String>();

		String queryPoolPath = "querypool/wikiwebsorted";//complete list in /media/DATA/pool/webwords/wikiwebsorted
		String openDescFileDirPath = "websources/DT01/";//not needed if reading from DB
		// /home/mohammad/uni-work/DJOERD SOURCES/DT01/
		boolean readFromDB = true; //read from openFIleDS or DB
		
		//DetailedInfoXPathDetectionDS detailedInfoXpathDetector;
		 
		crawlingConfig = new CrawlingConfig();		
		crawlingConfig.setScrShotBrowser(); //for fedweb commented
		crawlingConfig.setDetailedPageBrowser();
		crawlingConfig.setWebTools(new WebTools());//to extract pages and the content

		int totalNumOfWebsites = 0;
		if (!readFromDB) {//read from openFIleDS or DB
			crawlingConfig.setOpenDescDirPath(openDescFileDirPath);
			crawlingConfig.setListOfSourcesFolders();
			totalNumOfWebsites = crawlingConfig.getListOfSourcesFolders()
					.size();
		} else {
			crawlingConfig.setAllSitesDescription();
			totalNumOfWebsites = crawlingConfig.getListOfWebsites().size();
		}
		//crawlingConfig.setQuerySelectionApproach(crawlingConfig.crawledTextBased);
		crawlingConfig.setQuerySelectionApproach(crawlingConfig.mostFrequentWebWords);

		crawlingConfig.setQueries(queryPoolPath);

		FedwebCrawler crawlingThread;
		CrawlerSellenium crawlingThreadTest;
		for (int numberOfCrawledSoerces = 0; numberOfCrawledSoerces < 1; numberOfCrawledSoerces++) {
			System.out.println("Number Of Crawled Sources: "
					+ numberOfCrawledSoerces);

			if (!readFromDB) {//setting for each of the websources
				crawlingConfig.setOpenDescFilePath(numberOfCrawledSoerces); 
				crawlingConfig.setCollectionName();
				crawlingConfig.setCurrentSiteDescription(readFromDB, numberOfCrawledSoerces);
			} else {
				crawlingConfig.setCurrentSiteDescription(readFromDB,
						numberOfCrawledSoerces);
				crawlingConfig.setCollectionName(crawlingConfig
						.getCurrentSiteDescription().getName());
			}
			crawlingConfig.setWebTools(new WebTools());//to extract pages and the content
			System.out.println("Collection name: "
					+ crawlingConfig.getCollectionName());
			crawlingConfig.setLinkContentSavePath("crawledData" + "/"
					+ crawlingConfig.getCollectionName() + "/"); 
	//		crawlingThread = new FedwebCrawler(crawlingConfig, listOfReturnedResults);
			//crawlingThreadTest = new CrawlerWithHtmlUnit(crawlingConfig, listOfReturnedResults);
			crawlingThreadTest = new CrawlerSellenium(crawlingConfig, listOfReturnedResults);

			crawlingThreadTest.run();
	//		crawlingThread.run();
			// crawlingThread.start();
			/*
			 * try { crawlingThread.join(); } catch (InterruptedException ie) {
			 * ie.printStackTrace(); }
			 */
			System.out.println("End of Crawling for this collection");
			System.out.println("======================");
		}
		crawlingConfig.stopFXDriver();
	}

}
