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
        PaperSection section1 = new PaperSection("Section1 header", "Section1 content");
        PaperSection section2 = new PaperSection("Section2 header", "Section2 content");
        paperSections.add(section1);
        paperSections.add(section2);

        paper = new Paper("Author", "Title", "Abstract content", paperSections);
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
        System.out.println(paper);
        assert (paper.equals(extractedPaper));
    }
}