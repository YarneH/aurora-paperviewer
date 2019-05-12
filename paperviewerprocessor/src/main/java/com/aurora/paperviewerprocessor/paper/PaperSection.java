package com.aurora.paperviewerprocessor.paper;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Represents the smallest hierarchical section of a paper.
 * Meaning if a paper uses subsections at it's lowest level, the sections
 * will be split-up per subsection and not at section level.
 * </p>
 * <p>
 * Example: <br>
 *     1. Section title <br>
 *     1.1 Subsection title <br>
 *     1.1.1 Subsubsection title <br>
 * Will be split-up by subsubsection content.
 * </p>
 */
public class PaperSection {

    /**
     * The header of the paper consisting of the title hierarchy.
     * The hierarchy is ordered from highest level to lowest level.
     * (section title, subsection title, ...)
     */
    private List<String> mHeader;

    /**
     * The content of the paper.
     */
    private String mContent;

    public PaperSection(@NonNull List<String> header, String content) {
        this.mHeader = header;
        this.mContent = content;
    }

    public List<String> getHeader() {
        if(mHeader == null){
            return new ArrayList<>();
        }
        return mHeader;
    }

    public void addHeader(String header) {
        if(mHeader == null){
            mHeader = new ArrayList<>();
        }
        mHeader.add(header);
    }

    public int getLevel(){
        if(mHeader == null || mHeader.isEmpty()){
            return 0;
        }
        return (mHeader.size() - 1);
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mHeader, mContent);
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
