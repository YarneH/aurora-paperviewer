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

    /**
     * Handles the communication with the paperviewerprocessor module
     */
    private PaperProcessorCommunicator mCommunicator;

    public PaperViewModel(@NonNull Application application) {
        super(application);
        mCommunicator = new PaperProcessorCommunicator(application.getApplicationContext());
    }

    /**
     * Initialise the data with {@link ExtractedText}.
     *
     * @param extractedText where to extract the paper from
     */
    public void initialiseWithExtractedText(ExtractedText extractedText) {
        mPaper.setValue((Paper) mCommunicator.pipeline(extractedText));
    }

    /**
     * Initialise the data with {@link Paper}.
     *
     * @param paper the paper to load the data from
     */
    public void initialiseWithPaper(Paper paper) {
        mPaper.setValue(paper);
    }

    /**
     * Get the paper LiveData object.
     *
     * @return live paper
     */
    public LiveData<Paper> getPaper() {
        return mPaper;
    }

    /**
     * Get the number of sections in the current LiveData {@Paper} object.
     *
     * @return number of sections
     */
    public int getNumberOfSections(){
        if(mPaper == null) {
            return 0;
        }
        return mPaper.getValue().getSections().size();
    }

    /**
     * Indication if the current LiveData {@Paper} object has an abstract.
     *
     * @return number of sections
     */
    public boolean hasAbstract(){
        if (mPaper == null) {
            return false;
        }
        return !mPaper.getValue().getAbstract().isEmpty();
    }

    /**
     * Indication if the current LiveData {@Paper} object contains any images.
     *
     * @return boolean indicating if there are images present
     */
    public boolean hasImages(){
        if (mPaper == null) {
            return false;
        }
        return !mPaper.getValue().getImages().isEmpty();
    }

}
