package com.aurora.paperviewerenvironment;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.aurora.auroralib.ExtractedText;
import com.aurora.paperviewerprocessor.facade.PaperProcessorCommunicator;
import com.aurora.paperviewerprocessor.paper.Paper;

public class PaperViewModel extends AndroidViewModel {

    /**
     * {@link LiveData} of the current paper to maintain the paper in memory
     * across activity lifecycles
     */
    private MutableLiveData<Paper> mPaper = new MutableLiveData<>();

    private PaperProcessorCommunicator mCommunicator;

    public PaperViewModel(@NonNull Application application) {
        super(application);
        mCommunicator = new PaperProcessorCommunicator();
    }

    /**
     * Initialise the data from plain text.
     *
     * @param plainText where to extract recipe from.
     */
    public void initialiseWithPlainText(String plainText) {
        mPaper.setValue((Paper) mCommunicator.process(plainText));
    }

    /**
     * Initialise the data with {@link ExtractedText}.
     *
     * @param extractedText where to get recipe from.
     */
    public void initialiseWithExtractedText(ExtractedText extractedText) {
        mPaper.setValue((Paper) mCommunicator.process(extractedText));
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

}
