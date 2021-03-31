package com.linguisticanalysis.scanner.scanner;

public class LexemeModel {
    private String name;
    private int code;

    public LexemeModel(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
