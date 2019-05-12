package com.aurora.paperviewerprocessor.paper;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the PluginObject Paper.
 */
public class PaperUnitTest {

    private static Paper paper;

    @BeforeClass
    public static void initialize() {
        List<PaperSection> paperSections = new ArrayList<>();
        List<String> header1 = new ArrayList<>();
        header1.add("Section1 header");
        List<String> header2 = new ArrayList<>();
        header1.add("Section2 header");
        PaperSection section1 = new PaperSection(header1, "Section1 content");
        PaperSection section2 = new PaperSection(header2, "Section2 content");
        paperSections.add(section1);
        paperSections.add(section2);

        List<String> authors = new ArrayList<>();
        authors.add("Author1");
        paper = new Paper("", authors, "Title", "Abstract content", paperSections);
    }

    @Test
    public void Paper_toJSON_afterConversionPaperIsEqual() {

        // Act
        // Json the Paper to prepare it to be sent to Aurora for caching
        String jsonPaper =  paper.toJSON();
        // de-JSON the JSON string that was sent to Aurora (and would be received back when opening a cached file)
        Paper extractedPaper = Paper.fromJson(jsonPaper, Paper.class);

        // Assert
        // Assert that JSONing and de-JSONing the Paper object does not alter it
        assert (paper.equals(extractedPaper));
    }
}