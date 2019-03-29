package com.aurora.paperviewerprocessor.paper;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private String mHeader;
    private String mContent;
    private List<MarkedText> mMarkedText;

    public Section(String header, String content) {
        this.mHeader = header;
        this.mContent = content;
        this.mMarkedText = new ArrayList<>();
    }

    public String getmHeader() {
        return mHeader;
    }

    public String getmContent() {
        return mContent;
    }

    public List<MarkedText> getMarkedText(){
        return mMarkedText;
    }

    public void addMarkedText(MarkedText markedText){
        this.mMarkedText.add(markedText);
    }
}
