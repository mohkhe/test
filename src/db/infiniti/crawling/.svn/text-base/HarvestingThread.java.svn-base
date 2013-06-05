package db.infiniti.crawling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HarvestingThread extends Thread {
	ArrayList<String> listOfSites;
	int countScrapingQueries;

	public HarvestingThread(ArrayList<String> listOfSites,
			int countScrapingQueries) {
		this.listOfSites = listOfSites;
		this.countScrapingQueries = countScrapingQueries;
	}

	@Override
	public void run() {
		boolean harvesting = true;
		int index = 0;
		while (harvesting) {
			while (index < listOfSites.size()) {
				String textContentOfPage = extractTextFromLink(listOfSites
						.get(index));
				countScrapingQueries++;
				saveTextContentOfPage(textContentOfPage,
						listOfSites.get(index), index);
				index++;
			}
		}
	}

	private void saveTextContentOfPage(String textContentOfPage, String url,
			int index) {
		try {
			String filePath = "/home/mohammad/uni-work/crawling/djoerd/"
					+ "link" + index;
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

	private String extractTextFromLink(String url) {
		HtmlPage page = null;
		WebClient webClient = new WebClient();
		try {
			page = webClient.getPage(url);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String textContent = page.asText();
		return textContent;
	}
}
