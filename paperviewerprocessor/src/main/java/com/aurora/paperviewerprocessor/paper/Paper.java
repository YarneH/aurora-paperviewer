package com.aurora.paperviewerprocessor.paper;

import android.graphics.Bitmap;

import com.aurora.auroralib.PluginObject;

import java.util.ArrayList;
import java.util.List;

public class Paper extends PluginObject {

    private String mAuthor;
    private String mTitle;
    private String mAbstract;
    private List<Section> mSections;
    private List<Bitmap> mImages;

    public Paper(String author, String title, String paperAbstract, List<Section> sections){
        this.mAuthor = author;
        this.mTitle = title;
        this.mAbstract = paperAbstract;
        this.mSections = sections;
    }

    public Paper(){}

    public String getTitle() {
        // Dummy data
        return "What is all the fuzz about?";
    }

    public String getAbstract() {
        // Dummy data
        return "Fuzzing  is  an  automated  software  testing  technique  that  has  proven  " +
                "to  be  remarkably  effective  in  findingbugs  and  vulnerabilities  in  various  " +
                "programs.  Most  existing surveys  on  the  topic  discuss  the  conceptual  principles  of fuzzing.  " +
                "The  present  survey  paper  attempts  to  illustrate these  concepts  with  practical  examples  " +
                "from  industry  andacademia. This survey is directed at people who are new to the  field  of  fuzzing  " +
                "as  well  as  people  with  more  experience,who  want  to  refresh  their  general  knowledge  " +
                "on  the  topic.";
    }

    public List<Section> getSections() {
        // Dummy data
        List<Section> sections = new ArrayList<>();
        Section sect1 = new Section("Introduction", "Software  security  is  becoming  " +
                "increasingly  more  im-portant.  With  the  Internet  of  Things,  more  and  more devices  " +
                "are  connected  to  the  internet.  Usually,  programmers  use  low-level  languages  " +
                "like  C  or  C++  to  program these  devices.  However,  these  languages  do  not  provide" +
                "many   security   guarantees,   which   makes   many   of   " +
                "the devices prone to bugs and vulnerabilities.");
        Section sect2 = new Section("Fuzzing", "Fuzzing (also called fuzz testing) can be defined as: " +
                "“a highly  automated  testing  technique  that  covers  numerous boundary  cases  using  " +
                "invalid  data  (from  files,  networkprotocols, API calls, and other targets) as application " +
                "input to  better  ensure  the  absence  of  exploitable  vulnerabilities” [13].");
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
