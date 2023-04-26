package com.innotech.electionsim.candidate;

public class Stat {
    private final String label;
    private String value;
    private final boolean isNumeral;

    public Stat(String label, String value, boolean isNumeral) {
        this.label = label;
        this.value = value;
        this.isNumeral = isNumeral;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNumeral() {
        return isNumeral;
    }
}
