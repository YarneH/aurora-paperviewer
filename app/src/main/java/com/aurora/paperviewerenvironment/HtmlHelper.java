package com.aurora.paperviewerenvironment;

public final class HtmlHelper {

    /**
     * Html formatted newline
     */
    public static final String HTML_NEWLINE = "<br><br>";

    /**
     * Normal newline
     */
    public static final String NEWLINE = "\n";

    /**
     * The head of a HTML image
     */
    public static final String HTML_IMAGE_HEAD = "<br><center><img src='";

    /**
     * The end of a HTML image
     */
    public static final String HTML_IMAGE_END = "' /></center><br>";

    /**
     * HTML formatted  start of the start page
     */
    public static final String HTML_HEAD = "<html><head><style type=\"text/css\">";

    /**
     * The header for the HTML style of an image. The header configures the size of the
     * images in the WebView to fit the screen
     */
    public static final String HTML_STYLE_IMAGE = "img{display: inline;height: auto;max-width: 100%;}";

    /**
     * The header for the html body start, this body configures the font size, family, ...
     */
    public static final String HTML_STYLE_BODY = "body {";

    /**
     * HTML formatted  font family
     */
    public static final String HTML_FONT_FAMILY = "font-family: ";

    /**
     * HTML formatted  font size
     */
    public static final String HTML_FONT_SIZE = "font-size: ";

    /**
     * HTML formatted  font text align
     */
    public static final String HTML_TEXT_ALIGN = "text-align: ";

    /**
     * HTML format font text weight
     */
    public static final String HTML_FONT_WEIGHT = "font-weight: ";

    /**
     * HTML formatted  body ending
     */
    public static final String HTML_BODY_END = "}</style></head><body>";

    /**
     * HTML formatted end of the page
     */
    public static final String HTML_END = "</body></html>";

    /**
     * Separator in css style html page heading
     */
    public static final String CSS_SEPARATOR = ";";

    /**
     * Sans serif statement for the content font family
     */
    public static final String HTML_SANS_SERIF = ", sans serif";

    private HtmlHelper() {
        throw new IllegalStateException("Utility class");
    }




}