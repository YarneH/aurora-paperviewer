package com.aurora.paperviewerprocessor.facade;

import android.content.Context;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.ProcessorCommunicator;
import com.aurora.paperviewerprocessor.PluginConstants;
import com.aurora.paperviewerprocessor.processor.PaperParser;

/**
 * Communicates with the kernel and the UI of PaperViewer.
 */
public class PaperProcessorCommunicator extends ProcessorCommunicator {

    public PaperProcessorCommunicator(Context context){
        /*
         * A UNIQUE_PLUGIN_NAME needs to be passed to the constructor of ProcessorCommunicator for
         * proper configuration of the cache
         */
        super(PluginConstants.UNIQUE_PLUGIN_NAME, context);
    }

    /**
     * Very simple process function that just adds some text to extractedText
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return A string that consists of standard text and the result of extractedText.toString()
     */
    @Override
    protected PluginObject process(ExtractedText extractedText)throws PaperDetectionException {
        return PaperParser.parse(extractedText);
    }

}
