package db.infiniti.crawling;

import java.util.ArrayList;

import org.json.JSONObject;
public class TestWSCrawler {
	public static void main(String[] args) {

		ArrayList<String> listOfReturnedResults = new ArrayList<String>();

		/*
		 * CrawlingReportDS crawlReport; DetailedInfoXPathDetectionDS
		 * detailedInfoXpathDetector;
		 */
		JSONObject objectJson= new JSONObject();
		
		TestWSCrawlingThread crawlingThread;

		String tempURL = "http://arxiv.org/find/all/1/all:hi/0/1/0/all/0/1";//"http://wwwhome.ewi.utwente.nl/~hiemstra/";
		String itemXP = "//dd[./div/div/a]";//"	//div[./h2/a]";
		String linkXP = "./preceding-sibling::dt//a";//"/h2/a";
		String titleXP = ".//div[@class='list-title']";
		String desXP = "";
		String thumbXP = "";
	
//http://localhost:8080/nl.utwente.db.infiniti/rest/crawler?tempURL=http://arxiv.org/find/all/1/all:hi/0/1/0/all/0/1
		//&itemXP=//dd[./div/div/a]&linkXP=./preceding-sibling::dt//a/@href&titleXP=.//div[@class='list-title'];
		
		crawlingThread = new TestWSCrawlingThread(tempURL, itemXP, linkXP, titleXP,
				desXP, thumbXP, objectJson);

		// detectXpathThread.start();
		crawlingThread.run();
/*		try {
			crawlingThread.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}*/
		System.out.println("End of Crawling for this collection");
		System.out.println("Jason: " + objectJson.toString());

	}
}
