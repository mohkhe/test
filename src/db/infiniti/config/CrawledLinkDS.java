package db.infiniti.config;

import org.w3c.dom.Document;

public class CrawledLinkDS {

	Document doc = null;
	String link = "Could not find.";
	String title = "Could not find.";
	String description = "Could not find.";
	String itemXML = "Could not find.";
	String thumbLink = "Could not find.";
	String comments = "Could not find.";
	String linkTextContent = "Could not find.";
	String linkHtmlContent = "Could not find.";
	
	public Document getXmlDoc() {
		return doc;
	}
	public void setXmlDoc(Document doc) {
		this.doc = doc;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getItemXML() {
		return itemXML;
	}
	public void setItemXML(String itemXML) {
		if(itemXML.isEmpty()){
			setItemXML("No XML found for this fragment.");
		}
		this.itemXML = itemXML;
	}
	public String getThumbLink() {
		return thumbLink;
	}
	public void setThumbLink(String thumbLink) {
		this.thumbLink = thumbLink;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getLinkTextContent() {
		return linkTextContent;
	}
	public void setLinkTextContent(String linkTextContent) {
		this.linkTextContent = linkTextContent;
	}
	public String getLinkHtmlContent() {
		return linkHtmlContent;
	}
	public void setLinkHtmlContent(String linkHtmlContent) {
		this.linkHtmlContent = linkHtmlContent;
	}

	
}
