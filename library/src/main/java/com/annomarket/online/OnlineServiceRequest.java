package com.annomarket.online;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class OnlineServiceRequest {

	@JsonInclude(Include.NON_NULL)
	private String document;
	
	@JsonInclude(Include.NON_NULL)	
	private String documentUrl;
	private String mimeType;
	
	@JsonInclude(Include.NON_NULL)	
	private List<String> annotationSelectors;

	public OnlineServiceRequest(String document, SupportedMimeType type, List<AnnotationSelector> annotationSelectors) {
		this.document = document;
		this.mimeType = type.value;
		if(annotationSelectors != null) {
			this.annotationSelectors = new LinkedList<String>();
			for(AnnotationSelector as : annotationSelectors) {
				this.annotationSelectors.add(as.toString());
			}
		}
	}

	public OnlineServiceRequest(URL documentUrl, SupportedMimeType type, List<AnnotationSelector> annotationSelectors) {
		this.documentUrl = documentUrl.toString();
		this.mimeType = type.value;
		if(annotationSelectors != null) {
			this.annotationSelectors = new LinkedList<String>();
			for(AnnotationSelector as : annotationSelectors) {
				this.annotationSelectors.add(as.toString());
			}
		}
	}

	public String getDocument() {
		return document;
	}
	public String getDocumentUrl() {
		return documentUrl;
	}
	public String getMimeType() {
		return mimeType;
	}
	public List<String> getAnnotationSelectors() {
		return annotationSelectors;
	}	
}
