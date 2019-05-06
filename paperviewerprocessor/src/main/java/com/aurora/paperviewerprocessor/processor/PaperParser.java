package com.aurora.paperviewerprocessor.processor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
    public static Paper parse(ExtractedText extractedText){
        Paper processedPaper = new Paper(extractedText.getFilename());
        processedPaper.setAuthors(extractedText.getAuthors());
        processedPaper.setTitle(extractedText.getTitle());
        processedPaper.setImages(parseImages(extractedText));
        processedPaper.setAbstract(parseAbstract(extractedText));

        // Identify the sections
        List<PaperSection> paperSections = parseSections(extractedText);
        if(paperSections.isEmpty()){
            throw new PaperDetectionException(TAG + ": Could not extract parse sections.");
        }
        processedPaper.setSections(parseSections(extractedText));

        return processedPaper;
    }

    /**
     * Parse the images from the {@link Paper} {@link com.aurora.auroralib.PluginObject} received from aurora.
     *
     * @param extractedText The extractedText passed by aurora
     * @return a list containing all the images of the {@link Paper}
     */
    private static List<Bitmap> parseImages(ExtractedText extractedText){
        List<Bitmap> images = new ArrayList<>();
        for(Section section : extractedText.getSections()){
            if(section.getImages() == null){
                continue;
            }
            for(String base64Image : section.getImages()){
                InputStream stream = new ByteArrayInputStream(Base64.decode(base64Image.getBytes(),
                        Base64.DEFAULT));
                Bitmap imageBitmap = BitmapFactory.decodeStream(stream);

                images.add(imageBitmap);
            }
        }
        return images;
    }

    /**
     * Parses the abstract from the {@link Paper} {@link com.aurora.auroralib.PluginObject} received from aurora.
     *
     * @param extractedText The extractedText passed by aurora.
     * @return the abstract of the {@link Paper}
     */
    private static String parseAbstract(ExtractedText extractedText){
        for(Section section : extractedText.getSections()){
            if(section.getTitle() != null && "abstract".equals(section.getTitle().toLowerCase())){
                return section.getBody();
            }
        }
        return null;
    }

    /**
     * Parse the sections from the {@link Paper} {@link com.aurora.auroralib.PluginObject} received from aurora.
     *
     * @param extractedText The extractedText passed by aurora
     * @return a list containing all the sections and their content of the {@link Paper}
     * TODO generalize if statements
     */
    private static List<PaperSection> parseSections(ExtractedText extractedText){
        List<PaperSection> paperSections = new ArrayList<>();

        // Keeps track of the previous header
        List<String> currentSectionHeader = new ArrayList<>();

        // Keeps track of the section level
        int prevSectionLevel = 0;
        List<String> sectionHeader = new ArrayList<>();
        StringBuilder sectionContent = new StringBuilder();
        for(Section section : extractedText.getSections()){
            if(!validSection(section)){
                continue;
            }

            // Prepare the sectionHeader
            if (section.getTitle() != null) {
                if(section.getLevel() > prevSectionLevel){
                    prevSectionLevel = section.getLevel();
                } else if(section.getLevel() == prevSectionLevel && !sectionHeader.isEmpty()){
                    sectionHeader.remove(sectionHeader.size()-1);
                } else if(section.getLevel() < prevSectionLevel){
                    while(section.getLevel() <= prevSectionLevel && !sectionHeader.isEmpty()){
                        sectionHeader.remove(sectionHeader.size()-1);
                        prevSectionLevel--;
                    }
                    prevSectionLevel++;
                }
                sectionHeader.add(section.getTitle());
            }

            if(section.getBody() != null){
                // Wrongfully split up sections, append to previous section content
                if(section.getTitle() == null){
                    sectionContent.append(section.getBody());
                } else{
                    // Reached new section
                    if(sectionContent.length() > 0){
                        PaperSection paperSection = new PaperSection(currentSectionHeader,
                                sectionContent.toString());
                        paperSections.add(paperSection);
                    }
                    // Prepare for new section
                    currentSectionHeader = new ArrayList<>(sectionHeader);
                    sectionContent = new StringBuilder();
                    sectionContent.append(section.getBody());
                }
            }
        }
        // Add last section to the list
        PaperSection paperSection = new PaperSection(currentSectionHeader, sectionContent.toString());
        paperSections.add(paperSection);

        return paperSections;
    }

    private static boolean validSection(Section section){
        boolean isAbstract = false;
        boolean isEmpty = false;
        if(section.getTitle() != null){
            if("abstract".equals(section.getTitle().toLowerCase())){
                isAbstract = true;
            }
            if("".equals(section.getTitle().trim())){
                isEmpty = true;
            }
        }
        else{
            if("".equals(section.getBody().trim())){
                isEmpty = true;
            }
        }
        return !(isAbstract || isEmpty);
    }

}
