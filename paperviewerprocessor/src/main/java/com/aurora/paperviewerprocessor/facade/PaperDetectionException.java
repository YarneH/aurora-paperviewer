package com.aurora.paperviewerprocessor.facade;

import com.aurora.auroralib.ProcessingFailedException;

/**
 * An exception that indicates that the passed text does not resemble a paper
 */
public class PaperDetectionException extends ProcessingFailedException {

    public PaperDetectionException(String message){
        super(message);
    }

}
