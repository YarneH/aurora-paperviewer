package com.aurora.paperviewerprocessor.paper;

import android.graphics.Bitmap;

import com.aurora.auroralib.PluginObject;

import java.util.ArrayList;
import java.util.List;

public class Paper extends PluginObject {

    private String mTitle;
    private String mAbstract;
    private List<Section> mSections;
    private List<Bitmap> mImages;

    public Paper(String title, String paperAbstract, List<Section> sections){
        this.mTitle = title;
        this.mAbstract = paperAbstract;
        this.mSections = sections;
    }

    public Paper(){}

    public String getTitle() {
        // Dummy data
        return "Dummy paper title";
    }

    public String getAbstract() {
        // Dummy data
        return "Dummy abstract of the paper.";
    }

    public List<Section> getSections() {
        // Dummy data
        List<Section> sections = new ArrayList<>();
        Section sect1 = new Section("header1", "content of section 1.");
        Section sect2 = new Section("header2", "content of section 2.");
        sections.add(sect1);
        sections.add(sect2);
        return sections;
    }

    public List<Bitmap> getImages() {
        // Dummy data
        return null;
    }

    public static PluginObject fromJson(String json){
        return mGson.fromJson(json, PluginObject.class);
    }

}
