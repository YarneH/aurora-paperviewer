package com.aurora.paperviewerenvironment;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.aurora.auroralib.ExtractedText;
import com.aurora.paperviewerprocessor.facade.PaperProcessorCommunicator;
import com.aurora.paperviewerprocessor.paper.Paper;

/**
 * The {@link AndroidViewModel} for maintaining the data and state of
 * the current Paper being visualized.
 */
public class PaperViewModel extends AndroidViewModel {

    /**
     * {@link LiveData} of the current paper to maintain the paper in memory
     * across activity lifecycles
     */
    private MutableLiveData<Paper> mPaper = new MutableLiveData<>();

    private MutableLiveData<String> mFontFamily = new MutableLiveData<>();

    private MutableLiveData<Integer> mFontSize = new MutableLiveData<>();

    private PaperProcessorCommunicator mCommunicator;

    public PaperViewModel(@NonNull Application application) {
        super(application);
        mCommunicator = new PaperProcessorCommunicator(application.getApplicationContext());
    }

    /**
     * Initialise the data with {@link ExtractedText}.
     *
     * @param extractedText where to get recipe from.
     */
    public void initialiseWithExtractedText(ExtractedText extractedText) {
        mPaper.setValue((Paper) mCommunicator.pipeline(extractedText));
        // TODO change to common resource
        mFontFamily.setValue("Arial");
        mFontSize.setValue(16);
    }

    public void initialiseWithPaper(Paper paper) {
        mPaper.setValue(paper);
    }

    public LiveData<Paper> getPaper() {
        return mPaper;
    }

    public int getNumberOfSections(){
        return mPaper.getValue().getSections().size();
    }

    public boolean hasAbstract(){
        return mPaper.getValue().getAbstract() != null;
    }

    public boolean hasImages(){ return !mPaper.getValue().getImages().isEmpty(); }

    public void setFontFamily(String fontFamily){
        mFontFamily.setValue(fontFamily);
    }

    public LiveData<String> getFontFamily(){
        return mFontFamily;
    }

    public LiveData<Integer> getFontSize(){
        return mFontSize;
    }

}
