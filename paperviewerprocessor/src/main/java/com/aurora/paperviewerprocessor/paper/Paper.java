package com.aurora.paperviewerprocessor.paper;

import android.graphics.Bitmap;
import java.util.List;

import com.aurora.auroralib.PluginObject;

public class Paper extends PluginObject {

    private String mAuthor;
    private String mTitle;
    private String mAbstract;
    private List<PaperSection> mSections;
    private List<Bitmap> mImages;

    public Paper() { }

    public Paper(String author, String title, String paperAbstract, List<PaperSection> sections) {
        this.mAuthor = author;
        this.mTitle = title;
        this.mAbstract = paperAbstract;
        this.mSections = sections;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getAuthor() {
        return this.mAuthor;
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

    public String getAbstract() {
        return this.mAbstract;
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

}
