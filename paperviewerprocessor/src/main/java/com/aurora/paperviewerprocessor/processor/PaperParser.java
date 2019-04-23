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

/**
 * Parses the {@link ExtractedText} obtained from PaperViewer into a
 * custom PluginObject {@link Paper}.
 */
public final class PaperParser {

    private PaperParser(){}

    /**
     * Parses a {@link Paper} from {@link ExtractedText}.
     *
     * @param extractedText The {@link ExtractedText} received from aurora
     * @return A parsed {@link Paper}
     */
    public static Paper parsePaper(ExtractedText extractedText){
        Paper processedPaper = new Paper();
        processedPaper.setTitle(extractedText.getTitle());
        processedPaper.setImages(extractImages(extractedText));

        // Identify an abstract (if present) and the sections
        List<PaperSection> paperSections = new ArrayList<>();
        String currentSectionTitle = null;
        String currentSectionBody = "";
        int s = 0;
        while(s < extractedText.getSections().size()){
            Section section = extractedText.getSections().get(s);

            // Detect an abstract
            if(section.getTitle() != null && "Abstract".equals(section.getTitle())){
                processedPaper.setAbstract(section.getBody());
                s += 1;
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

            s++;
        }
        // Add last section to the list
        PaperSection paperSection = new PaperSection(currentSectionTitle, currentSectionBody);
        paperSections.add(paperSection);

        if(paperSections == null || paperSections.isEmpty()){
            Log.e("ERROR in PaperParser", "This is probably not a paper as no sections were recognized.");
        }
        processedPaper.setSections(paperSections);

        return processedPaper;
    }

    private static List<Bitmap> extractImages(ExtractedText extractedText){
        List<Bitmap> images = new ArrayList<>();
        for(Section section : extractedText.getSections()){
            for(String base64Image : section.getImages()){
                InputStream stream = new ByteArrayInputStream(Base64.decode(base64Image.getBytes(),
                        Base64.DEFAULT));
                Bitmap imageBitmap = BitmapFactory.decodeStream(stream);

                images.add(imageBitmap);
            }
        }
        return images;
    }

}
