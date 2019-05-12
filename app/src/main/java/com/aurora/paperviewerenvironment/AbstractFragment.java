package com.aurora.paperviewerenvironment;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aurora.paperviewerprocessor.paper.Paper;

/**
 * A fragment containing the view for an abstract of the paper
 */
public class AbstractFragment extends Fragment implements View.OnClickListener{

    /**
     * The size of this abstract in terms of occupied {@link ViewPager} positions
     */
    private static final int ABSTRACT_SIZE = 1;

    /**
     * The {@link android.arch.lifecycle.AndroidViewModel}
     * for maintaining the paper it's data and state
     * across the lifecycles of the activity
     */
    private PaperViewModel mPaperViewModel;

    /**
     * The root {@link android.support.v4.widget.NestedScrollView} of this fragment
     */
    private View mAbstractView;

    /**
     * The {@link TextView} for displaying the header of the abstract
     */
    private TextView mAbstractHeader;

    /**
     * The {@link WebView} for displaying the content of the abstract
     */
    private WebView mAbstractWebView;

    /**
     * Button for navigating to the sections at the bottom of this fragment
     */
    private ImageButton mBtnNavigateRight;


    /**
     * HTML formatted  start of the start page
     */
    private static final String HTML_HEAD = "<html><head><style type=\"text/css\">body {";

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
    private static final String HTML_BODY_END = "\"}</style></head><body>\"";

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

    public AbstractFragment() {
        // Empty constructor (generated by Android Studio)
    }

    /**
     * Returns a new instance of this fragment
     */
    public static AbstractFragment newInstance() {
        return new AbstractFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPaperViewModel = ViewModelProviders.of(getActivity()).get(PaperViewModel.class);

        // Inflate the view for the abstract
        mAbstractView = inflater.inflate(R.layout.fragment_abstract, container, false);

        mAbstractHeader = mAbstractView.findViewById(R.id.abstract_header);
        mAbstractHeader.setText(getResources().getString(R.string.abstract_header_content));

        // Initialize navigation buttons
        setUpNavigationButton();

        // Disable focus on initial view and remove scrolling of the web view
        mAbstractWebView = mAbstractView.findViewById(R.id.abstract_webview);
        mAbstractWebView.setFocusable(false);
        mAbstractWebView.setBackgroundColor(Color.TRANSPARENT);
        mAbstractWebView.setScrollContainer(false);

        mPaperViewModel.getPaper().observe(this, (Paper paper) -> {
            if(paper == null){
                return;
            }

            // Convert the abstract content to html page and add to the webview
            String htmlAbstractString = createHtmlWebView(paper);
            mAbstractWebView.loadDataWithBaseURL(null, htmlAbstractString, "text/html", "UTF-8", null);
        });

        return mAbstractView;
    }

    /**
     * Creates a html formatted web page
     * for representing the abstract content in the {@link WebView}.
     *
     * @param paper the paper to represent the abstract from
     * @return a string representing this html formatted page
     */
    private String createHtmlWebView(Paper paper){
        // Set the text properties of the abstract content
        String htmlFront = HTML_HEAD +
                HTML_FONT_FAMILY + getResources().getString(R.string.abstract_font_family) +
                HTML_SANS_SERIF + CSS_SEPARATOR +
                HTML_FONT_SIZE + getResources().getDimension(R.dimen.abstract_font_size) + CSS_SEPARATOR +
                HTML_FONT_WEIGHT+ getResources().getString(R.string.abstract_font_weight) + CSS_SEPARATOR +
                HTML_TEXT_ALIGN + getResources().getString(R.string.abstract_text_align) + CSS_SEPARATOR +
                HTML_BODY_END;
        String htmlEnd = HTML_END;

        return htmlFront + paper.getAbstract() + htmlEnd;
    }

    /**
     * Determines if it possible to navigate to the sections.
     *
     * @return boolean indicating if navigation to the sections is possible
     */
    private boolean canNavigateRight(){
        return mPaperViewModel.getNumberOfSections() > 0;
    }

    /**
     * Setup the navigation button to navigate to the sections.
     */
    private void setUpNavigationButton(){
        mBtnNavigateRight = mAbstractView.findViewById(R.id.btn_abstract_nav_right);
        mBtnNavigateRight.setOnClickListener(this);
        if(canNavigateRight()){
            mBtnNavigateRight.setVisibility(View.VISIBLE);
        } else {
            mBtnNavigateRight.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        ViewPager sectionViewPager = ((MainActivity) getActivity()).getSectionViewPager();

        switch(view.getId()){
            case R.id.btn_abstract_nav_top:
                mAbstractView.scrollTo(0, 0);
                break;
            case R.id.btn_abstract_nav_right:
                if(canNavigateRight()) {
                    sectionViewPager.setCurrentItem(ABSTRACT_SIZE);
                }
                break;
            default:
                break;
        }
    }

}

