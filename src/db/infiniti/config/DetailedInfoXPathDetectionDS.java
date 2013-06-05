package db.infiniti.config;

import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * @author mohammad
 * @Description Xpaths for Detailed inforamtion like Title, links, description,
 *              and etc need to be detected.
 * @Des Data Structure needed for this is implemented in this class
 */
public class DetailedInfoXPathDetectionDS {
	
	public DetailedInfoXPathDetectionDS() {
		super();
		this.allTheResultSnippets = new ArrayList<HtmlElement>();
	}
	
	ArrayList<HtmlElement> allTheResultSnippets;
	HtmlElement htmlElm;
	
	public ArrayList<HtmlElement> getAllTheResultSnippets() {
		return allTheResultSnippets;
	}
	public void setAllTheResultSnippets(ArrayList<HtmlElement> allTheResultSnippets) {
		this.allTheResultSnippets = allTheResultSnippets;
	}
	public void addSnippetToList(HtmlElement snippetHtmlElm) {
		this.allTheResultSnippets.add(snippetHtmlElm);
	}
	public HtmlElement getHtmlElm() {
		return htmlElm;
	}
	public void setHtmlElm(HtmlElement htmlElm) {
		this.htmlElm = htmlElm;
	}
	
	
}
