package com.newOS.RecordData;

import java.util.List;

import com.example.demo.Simple;

public class AutoUpdateRequest {
	
	private String table_name;
    private List<Simple> list;
    

    // Getters and Setters
    public String getTable_name() { return table_name; }
    public void setTable_name(String table_name) { this.table_name = table_name; }

    public List<Simple> getList() { return list; }
    public void setList(List<Simple> list) { this.list = list; }

}
