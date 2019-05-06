package com.aurora.paperviewerprocessor.processor;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.paperviewerprocessor.paper.Paper;
import com.aurora.paperviewerprocessor.paper.PaperSection;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Testing the functionality of the processing of a paper
 */
public class PaperParserUnitTest {

    /**
     * Dummy ExtractedText received from aurora
     */
    private static ExtractedText extractedText;

    @BeforeClass
    public static void initialize() {
        String title = "Paper title";
        List<String> authors = new ArrayList<>();
        authors.add("Author");
        List<Section> sections = new ArrayList<>();
        List<String> irrelevantImages = new ArrayList<>();
        Section section1 = new Section("Section title1", "Section body1.", irrelevantImages);
        sections.add(section1);
        Section section2 = new Section("Section title2", "Section body2.", irrelevantImages);
        sections.add(section2);

        String irrelevantFileName = "filename";
        Date irrelevantDateLastEdit = new Date(0);
        extractedText = new ExtractedText(irrelevantFileName, irrelevantDateLastEdit,
                title, authors, sections);
    }

    @Test
    public void PaperParser_parsePaper_titleHasBeenSet() {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        assert(paper.getTitle() != null);
    }

    @Test
    public void PaperParser_parsePaper_paperCorrectTitle() {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        assert(paper.getTitle().equals("Paper title"));
    }

    @Test
    public void PaperParser_parsePaper_sectionsCorrectSize() {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        assert(paper.getSections().size() == 2);
    }

    @Test
    public void PaperParser_parsePaper_sectionsCorrectHeaderAndContent() {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        PaperSection paperSection1 = paper.getSections().get(0);
        List<String> header1 = new ArrayList<>();
        header1.add("Section title1");
        assert(paperSection1.getHeader().equals(header1));
        assert(paperSection1.getContent().equals("Section body1."));

        PaperSection paperSection2 = paper.getSections().get(1);
        List<String> header2 = new ArrayList<>();
        header2.add("Section title2");
        assert(paperSection2.getHeader().equals(header2));
        assert(paperSection2.getContent().equals("Section body2."));
    }

    // TODO test for correct image content

}