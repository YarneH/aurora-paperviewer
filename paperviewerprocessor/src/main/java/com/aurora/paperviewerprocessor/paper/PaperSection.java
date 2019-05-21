package com.aurora.paperviewerprocessor.paper;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Represents the smallest hierarchical section of a paper.
 * Meaning if a paper uses subsections at it's lowest level, the sections
 * will be split-up per subsection and not at section level.
 * </p>
 * <p>
 * Example: <br>
 * 1. Section title <br>
 * 1.1 Subsection title <br>
 * 1.1.1 Subsubsection title <br>
 * Will be split-up by subsubsection content.
 * </p>
 */
public class PaperSection {

    /**
     * Tag for logging.
     */
    private static final String CLASS_TAG = PaperSection.class.getSimpleName();

    /**
     * The header of the paper consisting of the title hierarchy.
     * The hierarchy is ordered from highest level to lowest level.
     * (section title, subsection title, ...)
     */
    private List<String> mHeader;

    /**
     * The images in this PaperSection.
     * Their positions are referenced in the content with a placeholder
     * tag.
     */
    private List<String> mImages;

    /**
     * The content of the paper.
     */
    private String mContent;

    public PaperSection(@NonNull List<String> header, @NonNull String content, @NonNull List<String> images) {
        this.mHeader = header;
        this.mContent = content;
        this.mImages = images;
    }

    /**
     * Default getter.
     *
     * @return the header of this paper
     */
    public List<String> getHeader() {
        return mHeader;
    }

    /**
     * Retrieves the hierarchical level of this paper section.
     * If there is no header content or only empty titles,
     * the assigned level will be 0.
     *
     * @return the hierarchical level of this paper section
     */
    public int getLevel() {
        if (mHeader == null || mHeader.isEmpty()) {
            return 0;
        }
        return (mHeader.size() - 1);
    }

    /**
     * Default getter.
     *
     * @return the content of this section
     */
    public String getContent() {
        return mContent;
    }

    /**
     * Default getter.
     *
     * @return the images contained in this paper section
     */
    public List<String> getImages() {
        return mImages;
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
