package db.infiniti.config;

import java.util.LinkedHashMap;

public class CrawlingReportDS {

	private int countCrawlingQueries;
	private LinkedHashMap<String, QueryResStatistics> queryNumberofItsResults;
	private int numRepeatedLinks;

	public CrawlingReportDS() {
		super();
		this.countCrawlingQueries = 0;
		this.queryNumberofItsResults = new LinkedHashMap<String, QueryResStatistics>();
	}


	public int getCountCrawlingQueries() {
		return countCrawlingQueries;
	}

	public void setCountCrawlingQueries(int countCrawlingQueries) {
		this.countCrawlingQueries = countCrawlingQueries;
	}

	public LinkedHashMap<String, QueryResStatistics> getQueryNumberofItsResults() {
		return queryNumberofItsResults;
	}

	public void setQueryNumberofItsResults(LinkedHashMap<String, QueryResStatistics> queryNumberofItsResults) {
		this.queryNumberofItsResults = queryNumberofItsResults;
	}
	
	public void addQueryNumberofItsResults(String query, int qPosedIndex, int uniqResults, int RepeatedResults){
		
		this.queryNumberofItsResults.put(query, new QueryResStatistics(qPosedIndex, uniqResults, RepeatedResults));
	}
	
	public void incNoCrawlingQueries() {
		this.countCrawlingQueries = this.countCrawlingQueries+1;
	}

	public int getNumRepeatedLinks() {
		return numRepeatedLinks;
	}

	public void setNumRepeatedLinks(int numRepeatedLinks) {
		this.numRepeatedLinks = numRepeatedLinks;
	}

	public void incNoRepeatedLinks() {
		this.numRepeatedLinks = this.numRepeatedLinks+1;
	}
}
