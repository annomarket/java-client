package com.annomarket.online;

public enum SupportedMimeType {
	
	PLAINTEXT("text/plain"),
	HTML("text/html"),
	XML_APPLICATION("application/xml"),
	XML_TEXT("text/xml"),
	PUBMED("text/x-pubmed"),
	COCHRANE("text/x-cochrane"),
	MEDIAWIKI("text/x-mediawiki"),
	FASTINFOSET("application/fastinfoset"),
	TWITTER_JSON("text/x-json-twitter"),
	PDF("application/pdf"),
	WORD("application/word");
	
	private SupportedMimeType(String type) {
		this.value = type;
	}
	
	public final String value;
}
