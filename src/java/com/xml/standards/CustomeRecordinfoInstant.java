package com.xml.standards;

public class CustomeRecordinfoInstant {
	
	   
	    private String proceedName;
	    private String infomation;
		public String getProceedName() {
			return proceedName;
		}
		public void setProceedName(String proceedName) {
			this.proceedName = proceedName;
		}
		public String getInfomation() {
			return infomation;
		}
		public void setInfomation(String infomation) {
			this.infomation = infomation;
		}
	    
		@Override
	    public String toString() {
	        return "CustomeRecordinfoInstant{" +
	                "proceedName='" + proceedName + '\'' +
	                ", infomation=" + infomation  +
	                '}';
	    }

}
