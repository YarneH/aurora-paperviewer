package com.aurora.paperviewerprocessor.paper;

import java.util.ArrayList;
import java.util.List;

public class PaperSection {

    private String mHeader;
    private String mContent;
    private List<MarkedText> mMarkedText;

    public PaperSection(String header, String content) {
        this.mHeader = header;
        this.mContent = content;
        this.mMarkedText = new ArrayList<>();
    }

    public String getHeader() {
        return mHeader;
    }

    public String getContent() {
        return mContent;
    }

    public List<MarkedText> getMarkedText(){
        return mMarkedText;
    }

    public void addMarkedText(MarkedText markedText){
        this.mMarkedText.add(markedText);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PaperSection) {
            PaperSection paperSection = (PaperSection) o;
            return (paperSection.getHeader().equals(mHeader)
                    && paperSection.getContent().equals(mContent));
        }
        return false;
    }

    @Override
    public String toString() {
        return "PaperSection{" +
                "header=" + mHeader + ", content=" + mContent + "}";
    }

}
