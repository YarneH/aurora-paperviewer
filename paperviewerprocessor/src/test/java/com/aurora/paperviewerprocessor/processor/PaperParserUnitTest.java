package com.aurora.paperviewerprocessor.processor;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.paperviewerprocessor.facade.PaperDetectionException;
import com.aurora.paperviewerprocessor.paper.Paper;
import com.aurora.paperviewerprocessor.paper.PaperSection;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
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
        // Create mocked aurora (auroralib) ExtractedText object
        // Add title and authors
        String title = "Paper title";
        List<String> authors = new ArrayList<>();
        authors.add("Author");

        // Add abstract
        Section abstractSection = new Section("Abstract body.");
        abstractSection.setTitle("Abstract");

        // Add sections
        // First section with title and without body
        Section section1 = new Section("");
        section1.setTitle("1. Section title");
        section1.setLevel(0);
        // Subsection of first section with subsection header and title
        Section subSection1 = new Section("Section body 1.");
        subSection1.setTitle("1.1. Subsection title");
        subSection1.setLevel(1);
        // Second section with both header and title
        Section section2 = new Section("Section body 2.");
        section2.setTitle("2. Section title");

        // Initialize
        String irrelevantUri = "dummyFileUri";
        List<String> irrelevantFileNames = new ArrayList<>();
        String irrelevantFileName = "filename";
        irrelevantFileNames.add(irrelevantFileName);
        extractedText = new ExtractedText(irrelevantUri, irrelevantFileNames);
        extractedText.setTitle(title);
        extractedText.setAuthors(authors);
        extractedText.addSection(abstractSection);
        extractedText.addSection(section1);
        extractedText.addSection(subSection1);
        extractedText.addSection(section2);
    }

    @Test
    public void PaperParser_parsePaper_titleHasBeenSet() throws PaperDetectionException {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        assert(paper.getTitle() != null);
    }

    @Test
    public void PaperParser_parsePaper_paperCorrectTitle() throws PaperDetectionException {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        assert(paper.getTitle().equals("Paper title"));
    }

    @Test
    public void PaperParser_parsePaper_sectionsCorrectSize() throws PaperDetectionException {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        assert(paper.getSections().size() == 2);
    }

    @Test
    public void PaperParser_parsePaper_sectionsCorrectHeaderAndContent() throws PaperDetectionException {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        PaperSection paperSection1 = paper.getSections().get(0);
        List<String> header1 = new ArrayList<>();
        header1.add("1. Section title");
        header1.add("1.1. Subsection title");
        assert(paperSection1.getHeader().equals(header1));
        assert(paperSection1.getContent().equals("Section body 1."));

        PaperSection paperSection2 = paper.getSections().get(1);
        List<String> header2 = new ArrayList<>();
        header2.add("2. Section title");
        assert(paperSection2.getHeader().equals(header2));
        assert(paperSection2.getContent().equals("Section body 2."));
    }

    @Test
    public void PaperParser_parsePaper_sectionsCorrectLevel() throws PaperDetectionException {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        PaperSection paperSection1 = paper.getSections().get(0);

        assert(paperSection1.getContent().equals("Section body 1."));
        assert(paperSection1.getLevel() == 1);

        PaperSection paperSection2 = paper.getSections().get(1);
        assert(paperSection2.getLevel() == 0);
    }

    @Test
    public void PaperParser_parsePaper_abstractCorrectlyDetected() throws PaperDetectionException {
        // Act
        Paper paper = PaperParser.parse(extractedText);

        // Assert
        String abstractContent = paper.getAbstract();
        assert(abstractContent.equals("Abstract body."));
    }

    @Test(expected = PaperDetectionException.class)
    public void PaperParser_parsePaper_exceptionThrownForEmptyPaper() throws PaperDetectionException {
        // Initialize
        String irrelevantUri = "dummyFileUri";
        List<String> irrelevantFileNames = new ArrayList<>();
        String irrelevantFileName = "filename";
        irrelevantFileNames.add(irrelevantFileName);
        ExtractedText extractedText = new ExtractedText(irrelevantUri, irrelevantFileNames);

        // Act
        PaperParser.parse(extractedText);
    }
}