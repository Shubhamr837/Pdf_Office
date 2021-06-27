package com.shubhamr837.pdfoffice.utils;

public enum FileType {
    PDF("pdf"),DOCX("docx"),IMAGE("image"),TXT("txt");

    String type;

    FileType(String type){
        this.type=type;
    }

    @Override
    public String toString() {
        return type;
    }
}
