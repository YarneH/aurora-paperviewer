package com.aurora.paperviewerprocessor.processor;

import android.graphics.Bitmap;

import com.aurora.auroralib.ExtractedImage;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.paperviewerprocessor.paper.Paper;
import com.aurora.paperviewerprocessor.paper.PaperSection;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Utility class for parsing the {@link ExtractedText} received from aurora
 * into a custom {@link com.aurora.auroralib.PluginObject} {@link Paper}.
 */
public final class PaperParser {

    /**
     * The lower case title of an abstract
     */
    private static final String ABSTRACT_TITLE = "abstract";

    /**
     * Placeholder tag for the images in the content
     */
    private static final String IMG_PLACEHOLDER = "[IMG]";

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
            throw new PaperDetectionException(TAG + ": Failed to extract the paper sections.");
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
            for(ExtractedImage img : section.getExtractedImages()){
                images.add(img.getBitmap());
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
            if(ABSTRACT_TITLE.equalsIgnoreCase(section.getTitle())){
                return section.getBody();
            }
        }
        return "";
    }

    /**
     * Parse the sections from the {@link Paper} {@link com.aurora.auroralib.PluginObject} received from aurora.
     *
     * @param extractedText The extractedText passed by aurora
     * @return a list containing all the sections and their content of the {@link Paper}
     */
    private static List<PaperSection> parseSections(ExtractedText extractedText){
        List<PaperSection> paperSections = new ArrayList<>();

        // Keeps track of the previous header
        List<String> currentSectionHeader = new ArrayList<>();

        // Keeps track of the previous section level
        int prevSectionLevel = 0;
        List<String> sectionHeader = new ArrayList<>();
        List<String> sectionImages = new ArrayList<>();
        StringBuilder sectionContent = new StringBuilder();
        for(Section section : extractedText.getSections()){
            if(validSection(section)) {
                // Prepare the sectionHeader
                prevSectionLevel = adaptSectionHeader(sectionHeader, section.getTitle(),
                        section.getLevel(), prevSectionLevel);

                // Wrongfully split up sections, append to previous section content
                if(section.getTitle().isEmpty()){
                    appendContent(section, sectionContent, sectionImages);
                }
                // Reached new section
                else {
                    if (sectionContent.length() > 0) {
                        PaperSection paperSection = new PaperSection(currentSectionHeader,
                                sectionContent.toString(), sectionImages);
                        paperSections.add(paperSection);
                    }
                    // Prepare for new section
                    currentSectionHeader = new ArrayList<>(sectionHeader);
                    sectionContent = new StringBuilder();
                    sectionImages = new ArrayList<>();
                    appendContent(section, sectionContent, sectionImages);
                }
            }
        }
        // Add last section to the list
        PaperSection paperSection = new PaperSection(currentSectionHeader, sectionContent.toString(), sectionImages);
        paperSections.add(paperSection);

        return paperSections;
    }

    /**
     * Add the content and images of this aurora {@link Section}
     * to the content of the currently being processed {@link PaperSection}. <br>
     * For images a placeholder is introduced in the content of the {@link PaperSection}
     * to retrieve their location afterwards.
     *
     * @param section the current aurora section
     * @param content the current content for the section in progress
     * @param images the current images for the section in progress
     */
    private static void appendContent(Section section, StringBuilder content, List<String> images){
        content.append(section.getBody());

        // Add the image to the section and add placeholder for the image in the content
        for(ExtractedImage image : section.getExtractedImages()){
            images.add(image.getBase64EncodedImage());
            content.append(IMG_PLACEHOLDER);
        }
    }

    /**
     * Changes the section header to contain the appropriate title hierarchy for to the current section being processed.
     *
     * @param sectionHeader The previous title hierarchy, this will be changed to the correct current title hierarchy
     * @param sectionTitle The title of the section being processed
     * @param currSectionLevel The hierarchy level of the current section
     * @param prevSectionLevel The hierarchy level of the previous section
     * @return an adapted previous section level based on the current section level, for processing of the next section
     */
    private static int adaptSectionHeader(List<String> sectionHeader, String sectionTitle,
                                          int currSectionLevel, int prevSectionLevel){
        if(sectionTitle != null){
            if(currSectionLevel > prevSectionLevel){
                prevSectionLevel = currSectionLevel;
            } else if(currSectionLevel == prevSectionLevel && !sectionHeader.isEmpty()){
                sectionHeader.remove(sectionHeader.size()-1);
            } else if(currSectionLevel < prevSectionLevel){
                while(currSectionLevel <= prevSectionLevel && !sectionHeader.isEmpty()){
                    sectionHeader.remove(sectionHeader.size()-1);
                    prevSectionLevel--;
                }
                prevSectionLevel++;
            }
            sectionHeader.add(sectionTitle);
        }
        return prevSectionLevel;
    }

    /**
     * Returns whether this is a valid section.
     *
     * @param section the section to validate
     * @return boolean indicating whether the section is valid
     */
    private static boolean validSection(Section section){
        boolean isAbstract = false;
        boolean isEmpty = false;
        if(ABSTRACT_TITLE.equalsIgnoreCase(section.getTitle())){
            isAbstract = true;
        }
        if(section.getTitle().trim().isEmpty()){
            isEmpty = true;
        }
        return !(isAbstract || isEmpty);
    }

}
