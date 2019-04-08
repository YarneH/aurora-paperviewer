package com.aurora.paperviewerprocessor.facade;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.paperviewerprocessor.ProcessorCommunicator;
import com.aurora.paperviewerprocessor.basicpluginobject.BasicPluginObject;
import com.aurora.paperviewerprocessor.paper.Paper;

public class PaperProcessorCommunicator extends ProcessorCommunicator {

    /**
     * Very simple process function that just adds some text to extractedText
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return A string that consists of standard text and the result of extractedText.toString()
     */
    @Override
    public PluginObject process(ExtractedText extractedText) {
        // TODO change this dummy paper to extracted and processed paper
        return new Paper();
    }

    // TODO depending on whether we will also allow regular Strings to be passed: either remove this
    // or include this as an abstract method maybe
    // Maybe also include it as an abstract method in the superclass  then because then it should
    // also always be implemented
    /**
     * Very simple process function that just adds some text to a String
     *
     * @param inputText The string that has to be processed
     * @return A string that consists of standard text and the inputText
     */
    public PluginObject process(String inputText) {
        // TODO change this dummy paper to extracted and processed paper
        return new Paper();
    }

}
