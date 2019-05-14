package com.aurora.paperviewerenvironment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurora.paperviewerprocessor.paper.Paper;
import com.aurora.paperviewerprocessor.paper.PaperSection;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

/**
 * A fragment containing the view for a section of the paper
 */
public class SectionFragment extends Fragment implements View.OnClickListener {

    /**
     * The fragment argument representing the index of this section in the paper
     * for this fragment
     */
    private static final String ARG_SECTION_INDEX = "section_index";

    /**
     * Placeholder in the content representing the approximated
     * location of an image in the section
     */
    private static final String IMG_PLACEHOLDER = "[IMG]";

    /**
     * Offset to previous section in the {@link ViewPager}
     */
    private static final int PREV_OFFSET = 1;

    /**
     * Offset to next section in the {@link ViewPager}
     */
    private static final int NEXT_OFFSET = 1;

    /**
     * Size of the abstract in terms of occupied {@link ViewPager} positions
     * TODO set this to the appropriate size based on the content of the abstract
     */
    private static final int ABSTRACT_SIZE = 1;

    /**
     * Reduction factor for subsequent titles in the header. This reduction factor
     * is multiplied with the base title size obtained from the resources to retrieve
     * the size for this particular header title
     */
    private static final double TITLE_SIZE_FACTOR = 0.15;

    /**
     * HTML formatted  start of the start page
     */
    private static final String HTML_HEAD = "<html><head><style type=\"text/css\">";

    /**
     * The header for the HTML style section. The header configures the size of the
     * images in the WebView to fit the screen and initializes the font styling of the body
     */
    private static final String HTML_STYLE_HEAD = "img{display: inline;height: auto;max-width: 100%;}body {";

    /**
     * HTML formatted  font family
     */
    private static final String HTML_FONT_FAMILY = "font-family: ";

    /**
     * HTML formatted  font size
     */
    private static final String HTML_FONT_SIZE = "font-size: ";

    /**
     * HTML formatted  font text align
     */
    private static final String HTML_TEXT_ALIGN = "text-align: ";

    /**
     * HTML format font text weight
     */
    private static final String HTML_FONT_WEIGHT = "font-weight: ";

    /**
     * HTML formatted  body ending
     */
    private static final String HTML_HEAD_END = "}</style></head><body>";

    /**
     * HTML formatted end of the page
     */
    private static final String HTML_END = "</body></html>";

    /**
     * Sans serif statement for the content font family
     */
    private static final String HTML_SANS_SERIF = ", sans serif";

    /**
     * Separator in css style html page heading
     */
    private static final String CSS_SEPARATOR = ";";

    /**
     * Html formatted newline
     */
    private static final String HTML_NEWLINE = "<br><br>";

    /**
     * Normal newline
     */
    private static final String NEWLINE = "\n";

    /**
     * The head of a HTML image
     */
    String HTML_IMAGE_HEAD = "<br><center><img src='";

    /**
     * The end of a HTML image
     */
    String HTML_IMAGE_END = "' /></center><br>";

    /**
     * The key for accessing the font family in the {@link SharedPreferences}
     */
    private static final String FONT_FAMILY_KEY = "fontFamily";

    /**
     * The key for accessing the font size in the {@link SharedPreferences}
     */
    private static final String FONT_SIZE_KEY = "fontSize";

    /**
     * The key for accessing if images should be represented in the {@link SharedPreferences}
     */
    private static final String IMAGE_TOGGLE_KEY = "imageToggle";

    /**
     * The {@link android.arch.lifecycle.AndroidViewModel}
     * for maintaining the paper it's data and state
     * across the lifecycles of the activity
     */
    private PaperViewModel mPaperViewModel;

    /**
     * The root {@link android.support.v4.widget.NestedScrollView} of this fragment
     */
    private View mSectionView;

    /**
     * The {@link TextView} for displaying the header of the section
     */
    private LinearLayout mSectionHeader;

    /**
     * The {@link WebView} for displaying the content of the section
     */
    private WebView mSectionWebView;

    /**
     * Button for navigating to the previous section at the bottom of the fragment
     */
    private ImageButton mBtnNavigateLeft;

    /**
     * Button for navigating to the next section at the bottom of the fragment
     */
    private ImageButton mBtnNavigateRight;

    /**
     * Button for navigating to the top of the {@link android.support.v4.widget.NestedScrollView}
     */
    private ImageButton mBtnNavigateTop;


    /**
     * Listener for changes in the settings, such as font family and font type
     */
    private SharedPreferences.OnSharedPreferenceChangeListener mSettingsListener;

    public SectionFragment() {
        // Empty constructor (generated by Android Studio)
    }

    /**
     * Returns A new instance of this fragment for the given section
     * index.
     *
     * @param sectionIndex Index of the section for this fragment
     * @return A section fragment for this section
     */
    public static SectionFragment newInstance(int sectionIndex) {
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_INDEX, sectionIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPaperViewModel = ViewModelProviders.of(getActivity()).get(PaperViewModel.class);

        int sectionIndex = getArguments().getInt(ARG_SECTION_INDEX);

        // Inflate the scrollable view for the section container
        mSectionView = inflater.inflate(R.layout.fragment_section, container, false);
        mSectionHeader = mSectionView.findViewById(R.id.section_header);

        // Disable focus on initial view and remove scrolling of the web view
        mSectionWebView = mSectionView.findViewById(R.id.section_webview);
        mSectionWebView.setFocusable(false);
        mSectionWebView.setBackgroundColor(Color.TRANSPARENT);
        mSectionWebView.setScrollContainer(false);

        // Initialize and configure navigation buttons
        setUpNavigationButtons(sectionIndex);

        // Listen to changes in the settings, listener cannot be an inner anonymous or lambda method to prevent GC
        mSettingsListener = (SharedPreferences preferences, String key) -> {
            Paper paper = mPaperViewModel.getPaper().getValue();
            setContent(paper, sectionIndex, preferences);
        };
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(mSettingsListener);

        // Listen to changes in the paper
        mPaperViewModel.getPaper().observe(this, (Paper paper) -> {
            if(paper == null){
                return;
            }
            setContent(paper, sectionIndex, sharedPreferences);
        });

        return mSectionView;
    }

    /**
     * Set the content of the section.
     *
     * @param paper the current paper which contains the section
     * @param sectionIndex the index of the current section
     * @param sharedPref the current {@link SharedPreferences}
     */
    private void setContent(Paper paper, int sectionIndex, SharedPreferences sharedPref){
        PaperSection section = paper.getSections().get(sectionIndex);

        // Set up the header with the title hierarchy for this section
        setUpSectionHeader(section);

        // Obtain the current font settings
        String defaultFontFamily = getActivity().getResources().getString(R.string.default_font_family);
        String fontFamily = sharedPref.getString(FONT_FAMILY_KEY, defaultFontFamily);
        int defaultFontSize = getActivity().getResources().getInteger(R.integer.default_section_font_size);
        int fontSize = sharedPref.getInt(FONT_SIZE_KEY, defaultFontSize);

        // Obtain whether or not to visualize images
        boolean displayImages = sharedPref.getBoolean(IMAGE_TOGGLE_KEY, false);

        // Convert the section content to html page and add to the webview
        String htmlSectionString = createHtmlWebView(section, fontFamily, fontSize, displayImages);
        mSectionWebView.loadDataWithBaseURL(null, htmlSectionString, "text/html", "UTF-8", null);
    }

    /**
     * Creates a html formatted web page
     * for representing the section content in the {@link WebView}.
     *
     * @param section the section to represent in the webview
     * @param fontFamily the font family for the content
     * @param fontSize the font size for the content
     * @return
     */
    private String createHtmlWebView(PaperSection section, String fontFamily, int fontSize, boolean displayImages){
        // Set the text properties of the section content
        String htmlFront = HTML_HEAD + HTML_STYLE_HEAD +
                HTML_FONT_FAMILY + fontFamily +
                HTML_SANS_SERIF + CSS_SEPARATOR +
                HTML_FONT_SIZE + fontSize + CSS_SEPARATOR +
                HTML_FONT_WEIGHT + getResources().getString(R.string.section_font_weight) + CSS_SEPARATOR +
                HTML_TEXT_ALIGN + getResources().getString(R.string.section_text_align) + CSS_SEPARATOR +
                HTML_HEAD_END;
        String htmlEnd = HTML_END;

        return htmlFront + htmlFormatSection(section, displayImages) + htmlEnd;
    }

    /**
     * Format the content with the appropriate html tags.
     *
     * @param section The section to format
     * @return the content formatted with the html tags
     */
    private String htmlFormatSection(PaperSection section, boolean displayImages){
        String formattedContent = section.getContent().replace(NEWLINE, HTML_NEWLINE);

        for(Bitmap img : section.getImages()){
            if(displayImages){
                formattedContent = formattedContent.replaceFirst(Pattern.quote(IMG_PLACEHOLDER), htmlFormatImage(img));
            } else{
                formattedContent = formattedContent.replaceFirst(Pattern.quote(IMG_PLACEHOLDER), "");
            }
        }

        return formattedContent;
    }

    private String htmlFormatImage(Bitmap image){

        // Convert bitmap to Base64 encoded image for WebView
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String htmlImageSrc = "data:image/png;base64," + imageBase64;

        // Set the image source parameter
        return (HTML_IMAGE_HEAD + htmlImageSrc + HTML_IMAGE_END);
    }

    /**
     * Creates the section header for this section.
     *
     * @param section the current section
     */
    private void setUpSectionHeader(PaperSection section){
        // Clear header
        if(mSectionHeader.getChildCount() > 0){
            mSectionHeader.removeAllViews();
        }
        for(int headerIndex = 0; headerIndex < section.getHeader().size(); headerIndex++){
            String title = section.getHeader().get(headerIndex);
            TextView titleView = new TextView(getActivity());

            // Set the title font
            titleView.setText(title);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setTextColor(Color.BLACK);
            int baseSize = getResources().getInteger(R.integer.root_header_font_size);

            // rescale the title size based on the index in the header
            double resizeFactor = (1 - (headerIndex * TITLE_SIZE_FACTOR));
            if(resizeFactor < 0){
                resizeFactor = 0.0;
            }
            int size = (int) Math.round(baseSize * resizeFactor);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            titleView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mSectionHeader.addView(titleView);
        }
    }

    /**
     * Called when a navigation button has been clicked in this fragment. <br>
     * Navigates to the appropriate section/abstract.
     *
     * @param view The root view
     */
    @Override
    public void onClick(View view) {
        int sectionIndex = getArguments().getInt(ARG_SECTION_INDEX);
        ViewPager sectionViewPager = ((MainActivity) getActivity()).getSectionViewPager();

        if(sectionViewPager != null){
            switch(view.getId()){
                case R.id.btn_section_nav_top:
                    mSectionView.scrollTo(0, 0);
                    break;
                case R.id.btn_section_nav_left:
                    if(canNavigateLeft(sectionIndex)) {
                        sectionViewPager.setCurrentItem(prevSectionPosition(sectionIndex));
                    }
                    break;
                case R.id.btn_section_nav_right:
                    if(canNavigateRight(sectionIndex)) {
                        sectionViewPager.setCurrentItem(nextSectionPosition(sectionIndex));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Checks whether navigation is possible to the left from this section
     *
     * @param currSectionIndex the current index of the section
     * @return boolean indicating available navigation to the left
     */
    private boolean canNavigateLeft(int currSectionIndex){
        return (currSectionIndex > 0 || (currSectionIndex == 0 && mPaperViewModel.hasAbstract()));
    }

    /**
     * Checks whether navigation is possible to the right from this section
     *
     * @param currSectionIndex the current index of the section
     * @return boolean indicating available navigation to the right
     */
    private boolean canNavigateRight(int currSectionIndex){
        return currSectionIndex < (mPaperViewModel.getNumberOfSections() - 1);
    }

    /**
     * Retrieves the position of the next section in the section viewport
     *
     * @param currSectionIndex The index of the current section
     * @return the next position in the section viewport
     */
    private int nextSectionPosition(int currSectionIndex){
        if(mPaperViewModel.hasAbstract()){
            // add size of abstract extra for the abstract taking first positions in the viewport
            return currSectionIndex + ABSTRACT_SIZE + NEXT_OFFSET;
        } else{
            return currSectionIndex + NEXT_OFFSET;
        }
    }

    /**
     * Retrieves the position of the previous section in the section viewport
     *
     * @param currSectionIndex The index of the current section
     * @return the previous position in the section viewport
     */
    private int prevSectionPosition(int currSectionIndex){
        if(mPaperViewModel.hasAbstract()){
            // add size of abstract for the abstract taking first positions in the viewport
            return currSectionIndex + ABSTRACT_SIZE - PREV_OFFSET;
        } else{
            return currSectionIndex - PREV_OFFSET;
        }
    }

    /**
     * Sets the clicking behavior and the visibility of the navigation buttons
     *
     * @param sectionIndex The index of the current section
     */
    private void setUpNavigationButtons(int sectionIndex) {
        mBtnNavigateTop = mSectionView.findViewById(R.id.btn_section_nav_top);
        mBtnNavigateTop.setOnClickListener(this);
        mBtnNavigateLeft = mSectionView.findViewById(R.id.btn_section_nav_left);
        mBtnNavigateRight = mSectionView.findViewById(R.id.btn_section_nav_right);
        mBtnNavigateLeft.setOnClickListener(this);
        mBtnNavigateRight.setOnClickListener(this);
        if(canNavigateLeft(sectionIndex)){
            mBtnNavigateLeft.setVisibility(View.VISIBLE);
        } else{
            mBtnNavigateLeft.setVisibility(View.INVISIBLE);
        }
        if(canNavigateRight(sectionIndex)){
            mBtnNavigateRight.setVisibility(View.VISIBLE);
        } else{
            mBtnNavigateRight.setVisibility(View.INVISIBLE);
        }
    }

}

