package db.infiniti.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.infiniti.sitedescription.WebsiteDS;
import db.infiniti.sitedescription.WebsiteDescReader;
import db.infiniti.srf.Browser;
import db.infiniti.webtools.WebTools;

public class CrawlingConfig {
	String currentCollectionName;
	ArrayList<String> queries;
	LinkedHashMap<String, String> queryNumberofResults;
	private String openDescFilePath = "";
	private String openDescDirPath = "";
	private String queriesPath = "";
	private String linkContentSavePath = "";
	Thread crawlingTh;
	String sourceURL;
	ArrayList<String> listOfSourcesFolders;
	WebsiteDescReader descripReader = new WebsiteDescReader();
	WebsiteDS currentSiteDescription;
	String currentURL;
	WebTools webTools;
	ArrayList<WebsiteDS> listOfWebsites;
	int queryIndex = 0;
	HashSet<String> listOfStopWords;
	Browser scrShots;
	Browser detailedPages;
	int querySelectionApproach;
	HashMap<String, Integer> querySet = new HashMap<String, Integer>();
	boolean firstQuery = true;
	public String query; 
	public int crawledTextBased =1;
	public int mostFrequentWebWords = 2;
	
	public void setAllSitesDescription() {
		listOfWebsites = descripReader.readDB();
	}

	public void setCurrentSiteDescription(boolean readFromDB, int index) {
		if (readFromDB) {
			currentSiteDescription = descripReader.readOneDSFromDB(index);
		} else {
			currentSiteDescription = descripReader
					.readOpenDSFile(openDescFilePath + currentCollectionName); // from
																				// openFileDescription
		}
	}


	public String getCollectionName() {
		return currentCollectionName;
	}

	public void setCollectionName() {
		String collectionName = null;
		File file = new File(openDescFilePath);
		if (file.isDirectory()) {
			String[] list = file.list();
			if (list.length > 1) {// to remove effect of .svn
				collectionName = list[1];
			} else {
				collectionName = list[0];
			}
			// there is only one openDescFile in each directory
		}
		this.currentCollectionName = collectionName;
	}

	public void setCollectionName(String name) {
		this.currentCollectionName = name;
	}

	public ArrayList<String> getQueries() {
		return queries;
	}

	public void setQueries(ArrayList<String> queries) {
		this.queries = queries;
	}

	public LinkedHashMap<String, String> getQueryNumberofResults() {
		return queryNumberofResults;
	}

	public void setQueryNumberofResults(
			LinkedHashMap<String, String> queryNumberofResults) {
		this.queryNumberofResults = queryNumberofResults;
	}

	public String getOpenDescFilePath() {
		return openDescFilePath;
	}

	public void setOpenDescFilePath(int numberOfCrawledSoerces) {
		this.openDescFilePath = this.openDescDirPath
				+ this.getListOfSourcesFolders().get(numberOfCrawledSoerces)
				+ "/";
	}

	public String getQueriesPath() {
		return queriesPath;
	}

	public void setQueriesPath(String queriesPath) {
		this.queriesPath = queriesPath;
	}

	public String getLinkContentSavePath() {
		return linkContentSavePath;
	}

	public void setLinkContentSavePath(String linkContentSavePath) {
		String filePath = linkContentSavePath;
		File file = new File(filePath);
		if (file.exists()) {
			this.linkContentSavePath = linkContentSavePath;
		} else if (file.mkdir()) {
			this.linkContentSavePath = linkContentSavePath;
			System.out.println("Save link path is set.");
		} else {
			System.err
					.println("Could not create the path to save the content of links returned by serch engine.");
		}
	}

	public Thread getCrawlingTh() {
		return crawlingTh;
	}

	public void setCrawlingTh(Thread crawlingTh) {
		this.crawlingTh = crawlingTh;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public void setQueries(String queriesFilePath) {
		 if(this.getQuerySelectionApproach() == this.mostFrequentWebWords){
			this.queriesPath = queriesFilePath;
			queries = new ArrayList<String>();
			queries = readQueriesFromFile(queries, queriesFilePath);
		}else if (this.getQuerySelectionApproach() == this.crawledTextBased) {
				//do nothing
		} 
	}

	// [the, of, on, and, in, content, to, as, have, not, is, will, home, from,
	// by, on, wikipedia, for, was, site]
	private ArrayList<String> readQueriesFromFile(ArrayList<String> queries,
			String filePath) {
		try {
			File file = new File(filePath);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			for (int i = 0; i < 100; i++) {
				String line = in.readLine();
				String query = line.replaceAll("[0-9]*", "").trim();
				if(!queries.contains(query)){
					queries.add(query);
				}
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return queries;
	}

	public ArrayList<String> getListOfSourcesFolders() {
		return listOfSourcesFolders;
	}

	public ArrayList<String> setListOfSourcesFolders() {
		listOfSourcesFolders = new ArrayList<String>();
		File file = new File(openDescDirPath);
		if (file.isDirectory()) {
			String[] list = file.list();
			for (String fileDir : list) {
				listOfSourcesFolders.add(fileDir);
			}
			if (listOfSourcesFolders.contains(".svn")) {
				listOfSourcesFolders.remove(".svn");
			}
		}
		return listOfSourcesFolders;
	}

	public void updateQueryList(String pageContent) {
		pageContent = this.refineText(pageContent);
		String[] tokens = tokenizer(pageContent);
		for (String token : tokens) {
			if (!token.equalsIgnoreCase("")) {
				if (this.querySet.containsKey(token)) {
					if (querySet.get(token) != 0) {// not used before
						querySet.put(token, querySet.get(token) + 1);
					}
				} else {
					querySet.put(token, 1);
				}
			}
		}
	}

	public String refineText(String pageContent){
		
		if (pageContent.contains("\n")){
			pageContent = pageContent.replaceAll("\n", " ");
		}
/*		if (pageContent.matches("[.,!?:;'\"-]")){
			pageContent = pageContent.replaceAll("\\p{punct}+", " ");
		}
		if (pageContent.contains("\\p{Punct}")){
			pageContent = pageContent.replaceAll("\\p{Punct}", " ");
		}*/
		if (pageContent.contains("\\") || pageContent.contains(":") || pageContent.contains(";") || pageContent.contains(".")){
			pageContent = pageContent.replaceAll("\\p{Punct}", " ");
		}
		return pageContent.trim();
	}
	
	private String[] tokenizer(String content) {
		String delims = "[+\\-*/\\^ .,?!:;=()]+";
		String[] tokens = content.split(delims);
		return tokens;
	}

	public String setNextQuery() {
		if (this.querySelectionApproach == this.crawledTextBased) {
			if(firstQuery){//set first query
				query = "content";
				firstQuery=false;
			}else{
				query = getMostFreqQuery();
			}
			querySet.put(query, 0); //not to use later
			String tempUrl = currentSiteDescription.getTemplate();
			String url = null;
			if (tempUrl.contains("{q}")) {
				url = currentSiteDescription.getTemplate().replace(
						"{q}", query);
			} else if (tempUrl.contains("{searchTerms}")) {
				url = currentSiteDescription.getTemplate().replace(
						"{searchTerms}", query);
			} else if (tempUrl.contains("{query}")) {
				url = currentSiteDescription.getTemplate().replace(
						"{query}", query);
			}
			/*
			 * currentSiteDescription.getTemplate().replace(
			 * "{searchTerms}", query);
			 */
			url = url.replace("amp;", "");
			System.out.println("New query, number " + queryIndex
					+ " : " + query);
			queryIndex++;
			return url;
		} else {
			if (queryIndex < queries.size()) {
				query = queries.get(queryIndex);
				if (!currentSiteDescription.isAcceptsStopWords()) {
					if (isStopWord(query)) {
						queryIndex++;
						return setNextQuery();
					} /*else if(query is used before){list does not have repeated queries
						queryIndex++;
						return setNextQuery();
					}*/{
						String tempUrl = currentSiteDescription.getTemplate();
						String url = null;
						if (tempUrl.contains("{q}")) {
							url = currentSiteDescription.getTemplate().replace(
									"{q}", query);
						} else if (tempUrl.contains("{searchTerms}")) {
							url = currentSiteDescription.getTemplate().replace(
									"{searchTerms}", query);
						} else if (tempUrl.contains("{query}")) {
							url = currentSiteDescription.getTemplate().replace(
									"{query}", query);
						}
						/*
						 * currentSiteDescription.getTemplate().replace(
						 * "{searchTerms}", query);
						 */
						url = url.replace("amp;", "");
						System.out.println("New query, number " + queryIndex
								+ " : " + query);
						queryIndex++;
						return url;
					}
				} else {
					String tempUrl = currentSiteDescription.getTemplate();
					String url = null;
					if (tempUrl.contains("{q}")) {
						url = currentSiteDescription.getTemplate().replace(
								"{q}", query);
					} else if (tempUrl.contains("{searchTerms}")) {
						url = currentSiteDescription.getTemplate().replace(
								"{searchTerms}", query);
					} else if (tempUrl.contains("{query}")) {
						url = currentSiteDescription.getTemplate().replace(
								"{query}", query);
					}
					/*
					 * currentSiteDescription.getTemplate().replace(
					 * "{searchTerms}", query);
					 */
					url = url.replace("amp;", "");
					System.out.println("New query, number " + queryIndex
							+ " : " + query);
					queryIndex++;
					return url;
				}
			} else {
				System.out.println("End of Query List");
				return null;
			}
		}
	}

	private String getMostFreqQuery() {
		/*
		 * List<Integer> mapValues = new
		 * ArrayList<Integer>(queriesSet.values()); // TreeSet<Integer>
		 * sortedSet = new TreeSet<Integer>(mapValues, );
		 * Collections.sort(mapValues); mapValues.get(0);
		 */

		List mapKeys = new ArrayList(querySet.keySet());
		List mapValues = new ArrayList(querySet.values());
		Collections.sort(mapValues, Collections.reverseOrder());

		Iterator valueIt = mapValues.iterator();
		// TODO check if this is the most frequent and not the least freq
		int val = (Integer) valueIt.next();
		Iterator keyIt = mapKeys.iterator();
		while (keyIt.hasNext()) {
			String key = (String) keyIt.next();
			int comp1 = querySet.get(key);

			if (comp1 == val) {
				return key;
			}
		}

		return null;
	}

	private boolean isStopWord(String query) {
		if (listOfStopWords == null) {
			setListOfStopWords();
		}
		if (listOfStopWords.contains(query)) {
			return true;
		}
		return false;
	}

	public String setTestQuery(String query) {
		String url = currentSiteDescription.getTemplate().replace("{q}", query);
		url = url.replace("amp;", "");
		System.out.println("Test query: " + query);
		return url;
	}

	public void resetForNextCollection() {
		this.queryIndex = 0;
	}

	public String getCurrentURL() {
		return currentURL;
	}

	public void setCurrentURL(String currentURL) {
		this.currentURL = currentURL;
	}

	public WebTools getWebTools() {
		return webTools;
	}

	public void setWebTools(WebTools webTools) {
		this.webTools = webTools;
	}

	public String getOpenDescDirPath() {
		return openDescDirPath;
	}

	public void setOpenDescDirPath(String openDescDirPath) {
		this.openDescDirPath = openDescDirPath;
	}

	public WebsiteDS getCurrentSiteDescription() {
		return currentSiteDescription;
	}

	public void setCurrentSiteDescription(WebsiteDS currentSiteDescription) {
		this.currentSiteDescription = currentSiteDescription;
	}

	public ArrayList<WebsiteDS> getListOfWebsites() {
		return listOfWebsites;
	}

	public int getQueryIndex() {
		return queryIndex;
	}

	public void setQueryIndex(int queryIndex) {
		this.queryIndex = queryIndex;
	}

	public HashSet<String> setListOfStopWords() {
		listOfStopWords = new HashSet<String>();
		listOfStopWords.add("the");
		listOfStopWords.add("a");
		listOfStopWords.add("an");
		listOfStopWords.add("of");
		listOfStopWords.add("in");
		listOfStopWords.add("and");
		listOfStopWords.add("is");
		listOfStopWords.add("to");
		listOfStopWords.add("at");
		listOfStopWords.add("on");
		listOfStopWords.add("as");
		listOfStopWords.add("not");
		listOfStopWords.add("from");
		listOfStopWords.add("by");
		listOfStopWords.add("for");
		listOfStopWords.add("was");
		// [the, of, on, and, in, content, to, as, have, not, is, will, home,
		// from, by, on, wikipedia, for, was, site]
		// [the, of, on, and, in, content, to, as, have, not, is, will, home,
		// from, by, on, wikipedia, for, was, site,
		// this, contains, their, as, edit, string, with, there, page, his,
		// also, when, org, here, data, that, wikimedia, me, world, at, video,
		// page, it, powered, content, than, http, links, work, he, had,
		// article, his, back, many, state, please, an, free, are, software,
		// after, or, must, january, cache, centralauth, high, about, be,
		// posted, expires, available, all, travel, book, also, mail, public,
		// internet, right, retrieved, private, national, which, media, game,
		// last, en, text, were, store, new, hotels, search, en, see, changes,
		// has, encyclopedia]
		return listOfStopWords;
	}

	public void setListOfStopWords(HashSet<String> listOfStopWords) {

		this.listOfStopWords = listOfStopWords;
	}

	public Browser getScrShotBrowser() {
		return scrShots;
	}

	public void setScrShotBrowser() {
		this.scrShots = new Browser();
	}

	public void stopFXDriver() {
		this.scrShots.stopFXDriver();
	}
	
	public Browser getDetailedPageBrowser() {
		return detailedPages;
	}

	public void setDetailedPageBrowser() {
		this.detailedPages = new Browser();
	}

	public int getQuerySelectionApproach() {
		return querySelectionApproach;
	}

	public void setQuerySelectionApproach(int querySelectionApproach) {
		this.querySelectionApproach = querySelectionApproach;
	}
}
