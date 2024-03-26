package com.example.elearning;

public class notesitem {
    public  String fileurl;
    public String filename;
    public  notesitem()
    {

    }

    public notesitem(String fileurl, String filename) {
        this.fileurl=fileurl;
        this.filename=filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
