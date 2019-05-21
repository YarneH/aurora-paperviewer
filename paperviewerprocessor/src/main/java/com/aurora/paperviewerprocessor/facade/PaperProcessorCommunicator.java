package com.aurora.paperviewerprocessor.facade;

import android.content.Context;
import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.ProcessorCommunicator;
import com.aurora.paperviewerprocessor.processor.PaperParser;

/**
 * Communicates with the kernel and the UI of PaperViewer.
 */
public class PaperProcessorCommunicator extends ProcessorCommunicator {

    /**
     * The tag for logging purposes
     */
    private static final String TAG = PaperProcessorCommunicator.class.getSimpleName();

    public PaperProcessorCommunicator(Context context){
        super(context);
    }

    /**
     * Very simple process function that just adds some text to extractedText
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return A string that consists of standard text and the result of extractedText.toString()
     * @throws PaperDetectionException an indication that something went wrong during the processing of the paper
     */
    @Override
    protected PluginObject process(ExtractedText extractedText)throws PaperDetectionException {
        try {
            return PaperParser.parse(extractedText);
        } catch (PaperDetectionException pde) {
            // if something went wrong with the detection rethrow the error and let the
            // environment decide what to do in this case
            Log.e(TAG, "Detection failure", pde);
            throw pde;
        } catch (Exception e){
            // something else went wrong
            Log.e(TAG, "unexpected exception", e);
            throw new PaperDetectionException("Something unexpected happened: " + e.getMessage() + "\n\nAre you sure" +
                    " this is a paper?");
        }
    }

}
