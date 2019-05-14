package com.aurora.paperviewerprocessor.paper;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.aurora.auroralib.BitmapListAdapter;
import com.aurora.auroralib.PluginObject;
import com.aurora.paperviewerprocessor.PluginConstants;
import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Paper object for the enhanced representation of a paper.
 */
public class Paper extends PluginObject {

    /**
     * The authors the paper
     */
    private List<String> mAuthors;

    /**
     * The title of the paper
     */
    private String mTitle;

    /**
     * The content of the abstract
     */
    private String mAbstract;

    /**
     * The sections of the paper, index by occurrence
     */
    private List<PaperSection> mSections;

    /**
     * The images contained in the paper
     */
    @JsonAdapter(BitmapListAdapter.class)
    private List<Bitmap> mImages = new ArrayList<>();

    public Paper(String fileName) {
        super(fileName, PluginConstants.UNIQUE_PLUGIN_NAME);
    }

    public Paper(String fileName, @NonNull List<String> authors,@NonNull String title,
                 @NonNull String paperAbstract, @NonNull List<PaperSection> sections) {
        super(fileName, PluginConstants.UNIQUE_PLUGIN_NAME);
        this.mAuthors = authors;
        this.mTitle = title;
        this.mAbstract = paperAbstract;
        this.mSections = sections;
    }

    public void setAuthors(@NonNull List<String> authors) {
        this.mAuthors = authors;
    }

    public List<String> getAuthors() {
        return this.mAuthors;
    }

    public void setTitle(@NonNull String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setAbstract(@NonNull String abstractContent) {
        this.mAbstract = abstractContent;
    }

    public String getAbstract() {
        return this.mAbstract;
    }

    public void setSections(@NonNull List<PaperSection> sections) {
        this.mSections = sections;
    }

    public List<PaperSection> getSections() {
        return this.mSections;
    }

    public void setImages(List<Bitmap> images) {
        this.mImages = images;
    }

    public List<Bitmap> getImages() {
        return this.mImages;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mAuthors, mTitle, mAbstract, mSections, mImages);
    }

    /**
     * Currently ignoring the transient cache field
     *
     * @param o the object to compare to
     * @return true if both objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Paper) {
            Paper paper = (Paper) o;
            if(!paper.getAbstract().equals(mAbstract)){
                return false;
            }
            boolean equalHeaderContent = paper.getAuthors().equals(mAuthors)
                    && paper.getTitle().equals(mTitle);
            boolean equalAbstract = true;
            boolean equalContent = paper.getSections().equals(mSections);
            return  equalHeaderContent && equalAbstract && equalContent;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("Paper{");
        ret.append("title=").append(mTitle);
        ret.append(", author=").append(mAuthors);

        if(mAbstract != null){
            ret.append(",\nabstract=").append(mAbstract);
        }

        ret.append(",\nsections=").append(mSections);
        ret.append("}");
        return ret.toString();
    }

}
