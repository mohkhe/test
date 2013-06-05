package db.infiniti.config;

public class QueryResStatistics {

	int qPosedIndex;
	int uniqResults;
	int RepeatedResults;
	
	QueryResStatistics(int qPosedIndex,	int uniqResults,	int RepeatedResults){
		this.qPosedIndex = qPosedIndex;
		this.uniqResults = uniqResults;
		this.RepeatedResults = RepeatedResults;
	}

	public int getqPosedIndex() {
		return qPosedIndex;
	}

	public void setqPosedIndex(int qPosedIndex) {
		this.qPosedIndex = qPosedIndex;
	}

	public int getUniqResults() {
		return uniqResults;
	}

	public void setUniqResults(int uniqResults) {
		this.uniqResults = uniqResults;
	}

	public int getRepeatedResults() {
		return RepeatedResults;
	}

	public void setRepeatedResults(int repeatedResults) {
		RepeatedResults = repeatedResults;
	}
}
