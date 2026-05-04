package com.newOS.RecordData;

import java.util.List;

import com.example.demo.Simple;
import com.xml.standards.MethodProceed;

public class CombinedResponseQuee {
	
	 private List<Simple> cfusimpleList;
	 private List<Simple> mpnsimpleList;
	 private List<Simple> attrsimpleList;
	 private List<Simple> nonesimpleList;
	    private List<List<MethodProceed>> cfumethodProceedList;
	    private List<List<MethodProceed>> mpnmethodProceedList;
	    private List<List<MethodProceed>> attrmethodProceedList;
	    private List<List<MethodProceed>> nonemethodProceedList;

	    public CombinedResponseQuee( List<Simple> cfusimpleList,
	    		List<Simple> mpnsimpleList,
	    		List<Simple> attrsimpleList,
	    		List<Simple> nonesimpleList,
	    		List<List<MethodProceed>> cfumethodProceedList,
	    		List<List<MethodProceed>> mpnmethodProceedList,
	    		List<List<MethodProceed>> attrmethodProceedList,
	    		List<List<MethodProceed>> nonemethodProceedList) {
	    	this.cfusimpleList = cfusimpleList;
	    	this.mpnsimpleList = mpnsimpleList;
	    	this.attrsimpleList = attrsimpleList;
	    	this.nonesimpleList = nonesimpleList;
	        this.setCfumethodProceedList(cfumethodProceedList);
	        this.setMpnmethodProceedList(mpnmethodProceedList);
	        this.setAttrmethodProceedList(attrmethodProceedList);
	        this.setNonemethodProceedList(nonemethodProceedList);
	    }

	   

	    



		public List<Simple> getCfusimpleList() {
			return cfusimpleList;
		}



		public void setCfusimpleList(List<Simple> cfusimpleList) {
			this.cfusimpleList = cfusimpleList;
		}



		public List<Simple> getMpnsimpleList() {
			return mpnsimpleList;
		}



		public void setMpnsimpleList(List<Simple> mpnsimpleList) {
			this.mpnsimpleList = mpnsimpleList;
		}



		public List<Simple> getAttrsimpleList() {
			return attrsimpleList;
		}

		public void setAttrsimpleList(List<Simple> attrsimpleList) {
			this.attrsimpleList = attrsimpleList;
		}

		public List<Simple> getNonesimpleList() {
			return nonesimpleList;
		}

		public void setNonesimpleList(List<Simple> nonesimpleList) {
			this.nonesimpleList = nonesimpleList;
		}

		public List<List<MethodProceed>> getCfumethodProceedList() {
			return cfumethodProceedList;
		}

		public void setCfumethodProceedList(List<List<MethodProceed>> cfumethodProceedList) {
			this.cfumethodProceedList = cfumethodProceedList;
		}

		public List<List<MethodProceed>> getMpnmethodProceedList() {
			return mpnmethodProceedList;
		}

		public void setMpnmethodProceedList(List<List<MethodProceed>> mpnmethodProceedList) {
			this.mpnmethodProceedList = mpnmethodProceedList;
		}

		public List<List<MethodProceed>> getAttrmethodProceedList() {
			return attrmethodProceedList;
		}


		public void setAttrmethodProceedList(List<List<MethodProceed>> attrmethodProceedList) {
			this.attrmethodProceedList = attrmethodProceedList;
		}


		public List<List<MethodProceed>> getNonemethodProceedList() {
			return nonemethodProceedList;
		}


		public void setNonemethodProceedList(List<List<MethodProceed>> nonemethodProceedList) {
			this.nonemethodProceedList = nonemethodProceedList;
		}

}
