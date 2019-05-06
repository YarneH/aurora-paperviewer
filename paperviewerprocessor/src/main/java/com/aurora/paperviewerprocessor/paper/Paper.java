package com.aurora.paperviewerprocessor.paper;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.aurora.auroralib.PluginObject;
import com.aurora.paperviewerprocessor.PluginConstants;

public class Paper extends PluginObject {

    private List<String> mAuthors;
    private String mTitle;
    private String mAbstract;
    private List<PaperSection> mSections;
    private List<Bitmap> mImages;

    public Paper(String fileName) {
        super(fileName, PluginConstants.UNIQUE_PLUGIN_NAME);
    }

    public Paper(String fileName, List<String> authors, String title,
                 String paperAbstract, List<PaperSection> sections) {
        super(fileName, PluginConstants.UNIQUE_PLUGIN_NAME);
        this.mAuthors = authors;
        this.mTitle = title;
        this.mAbstract = paperAbstract;
        this.mSections = sections;
        this.mImages = new ArrayList<>();
    }

    public void setAuthors(List<String> authors) {
        this.mAuthors = authors;
    }

    public List<String> getAuthors() {
        return this.mAuthors;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setAbstract(String abstractContent) {
        this.mAbstract = abstractContent;
    }

    public void setSections(List<PaperSection> sections) {
        this.mSections = sections;
    }

    public List<PaperSection> getSections() {
        return this.mSections;
    }

    public String getAbstract() {
        return this.mAbstract;
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Paper) {
            Paper paper = (Paper) o;
            Boolean equalHeaderContent = paper.getAuthors().equals(mAuthors)
                    && paper.getTitle().equals(mTitle);
            Boolean equalAbstract = true;
            if(paper.getAbstract() != null && mAbstract != null && !paper.getAbstract().equals(mAbstract)){
                equalAbstract = false;
            }
            Boolean equalContent = paper.getSections().equals(mSections)
                    && paper.getImages().equals(mImages);
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
