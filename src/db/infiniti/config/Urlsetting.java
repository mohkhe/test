package db.infiniti.config;

import java.util.ArrayList;

import db.infiniti.sitedescription.WebsiteDS;
import db.infiniti.sitedescription.WebsiteDescReader;

public class Urlsetting {

	WebsiteDS siteDescription;
	int usedQueriesNumber = 0;
	ArrayList<String> queries;
	boolean stopwordsArePossible = true;

	// set url and query
	public Urlsetting(String collectionName, String CollectionOSDescFilePath) {
		WebsiteDescReader descripReader = new WebsiteDescReader();
		siteDescription = descripReader.readOpenDSFile(CollectionOSDescFilePath
				+ collectionName);
	}

	public String setNextQuery() {
		if (usedQueriesNumber < queries.size()) {
			String query = queries.get(usedQueriesNumber);
			usedQueriesNumber++;
			String url = siteDescription.getAddress().replace("{searchTerms}",
					query);
			url = url.replace("amp;", "");
			System.out.println("New query, number " + usedQueriesNumber
					+ " : " + query);
			return url;
		} else {
			System.out.println("End of Query List");
			return null;
		}
	}

	public String setTestQuery(String query) {
		if (usedQueriesNumber < queries.size()) {
			String url = siteDescription.getAddress().replace("{searchTerms}",
					query);
			url = url.replace("amp;", "");
			System.out.println("Test query: " + query);
			return url;
		} else {
			System.out.println("Problem in test query url.");
			return null;
		}
	}

	public void setQueries(ArrayList<String> queries) {
		this.queries = queries;
	}

	public ArrayList<String> getQueries() {
		return queries;
	}

}
