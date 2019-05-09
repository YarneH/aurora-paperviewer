package com.aurora.paperviewerprocessor.processor;

public class PaperDetectionException extends IllegalArgumentException {

    public PaperDetectionException(String message){
        super("Unable to detect a paper:\n" + message);
    }

}
