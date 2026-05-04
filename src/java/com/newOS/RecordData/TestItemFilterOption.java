package com.newOS.RecordData;

import java.util.ArrayList;
import java.util.List;

public class TestItemFilterOption {
    private String key;
    private String label;
    private String unit;
    private String standardNumber;
    private int count;
    private List<String> aliases = new ArrayList<>();

    public TestItemFilterOption() {
    }

    public TestItemFilterOption(String key, String label, String unit, String standardNumber) {
        this.key = key;
        this.label = label;
        this.unit = unit;
        this.standardNumber = standardNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStandardNumber() {
        return standardNumber;
    }

    public void setStandardNumber(String standardNumber) {
        this.standardNumber = standardNumber;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public void addAlias(String alias) {
        if (alias != null && !alias.isEmpty() && !aliases.contains(alias)) {
            aliases.add(alias);
        }
    }

    public void incrementCount() {
        count++;
    }
}
