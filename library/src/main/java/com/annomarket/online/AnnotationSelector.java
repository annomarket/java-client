package com.annomarket.online;

public class AnnotationSelector {
	
	private String annotationSet;	
	private String annotationType;	
	
	public AnnotationSelector(String annotationSet, String annotationType) {
		this.annotationSet = annotationSet;
		this.annotationType = annotationType;
	}
	
	public String getAnnotationClass() {
		return annotationSet;
	}

	public String getAnnotationType() {
		return annotationType;
	}

	public static final AnnotationSelector ALL_FROM_DEFAULT_SET = new AnnotationSelector("","");
	
	public static AnnotationSelector allAnnotationsFromClass(String annotationSet) {
		return new AnnotationSelector(annotationSet, "");		
	}
	
	public static AnnotationSelector annotationTypeFromDefaultSet(String annotationType) {
		return new AnnotationSelector("", annotationType);
	}
	
	@Override
	public String toString() {
		return annotationSet + ":" + annotationType;
	}

}
