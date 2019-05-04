package com.aurora.paperviewerprocessor.processor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.paperviewerprocessor.paper.Paper;
import com.aurora.paperviewerprocessor.paper.PaperSection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Utility class for parsing the {@link ExtractedText} received from aurora
 * into a custom {@link com.aurora.auroralib.PluginObject} {@link Paper}.
 */
public final class PaperParser {

    private PaperParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Parses a {@link Paper} from {@link ExtractedText}.
     *
     * @param extractedText The {@link ExtractedText} received from aurora
     * @return A parsed {@link Paper}
     */
    public static Paper parsePaper(ExtractedText extractedText){
        Paper processedPaper = new Paper(extractedText.getFilename());
        processedPaper.setTitle(extractedText.getTitle());
        processedPaper.setImages(extractImages(extractedText));

        // Identify an abstract (if present) and the sections
        List<PaperSection> paperSections = new ArrayList<>();
        String currentSectionTitle = null;
        String currentSectionBody = "";
        for(Section section : extractedText.getSections()){

            // Detect an abstract
            if(section.getTitle() != null && "Abstract".equals(section.getTitle())){
                processedPaper.setAbstract(section.getBody());
                continue;
            }
            if(section.getBody() != null){
                // Capture the title and content of the first section encountered
                if(section.getTitle() != null && "".equals(currentSectionBody)) {
                    currentSectionTitle = section.getTitle();
                    currentSectionBody = section.getBody();
                } else if(section.getTitle() != null){
                    // Reached a new section, add the completed previous section to the list
                    PaperSection paperSection = new PaperSection(currentSectionTitle, currentSectionBody);
                    paperSections.add(paperSection);
                    currentSectionTitle = section.getTitle();
                    currentSectionBody = section.getBody();
                }
                // As long as no new section title is encountered, continue adding content to the current section
                else{
                    // trim newlines
                    String body = section.getBody().replaceAll("^[\n]*|[\n]+$", "");

                    currentSectionBody +=  "\n\n" + body;
                }
            }
        }
        // Add last section to the list
        PaperSection paperSection = new PaperSection(currentSectionTitle, currentSectionBody);
        paperSections.add(paperSection);

        if(paperSections.isEmpty()){
            Log.e(TAG, "PaperParser: This is probably not a paper as no sections were recognized.");
            // TODO add custom exception
        }
        processedPaper.setSections(paperSections);

        return processedPaper;
    }

    /**
     * Extract the images from the {@link Paper} {@link com.aurora.auroralib.PluginObject} received from aurora.
     *
     * @param extractedText The extractedText passed by aurora
     * @return a list containing all the images of the {@link Paper}
     */
    private static List<Bitmap> extractImages(ExtractedText extractedText){
        List<Bitmap> images = new ArrayList<>();
        for(Section section : extractedText.getSections()){
            if(section.getImages() != null){
                for(String base64Image : section.getImages()){
                    InputStream stream = new ByteArrayInputStream(Base64.decode(base64Image.getBytes(),
                            Base64.DEFAULT));
                    Bitmap imageBitmap = BitmapFactory.decodeStream(stream);

                    images.add(imageBitmap);
                }
            }
        }
        return images;
    }

}
