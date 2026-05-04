package com.xml.standards;

import java.util.ArrayList;
import java.util.List;

public class Method {
	private String method;
	//private MethodProceed methodProceed=new MethodProceed();
	private List<MethodProceed> methodProceed=new ArrayList<MethodProceed>();
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public List<MethodProceed> getMethodProceed() {
		return methodProceed;
	}
	public void setMethodProceeds(List<MethodProceed> methodProceed) {
		this.methodProceed = methodProceed;
	}
	

}
