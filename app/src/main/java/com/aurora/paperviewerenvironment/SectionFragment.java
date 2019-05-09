package com.aurora.paperviewerenvironment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aurora.paperviewerprocessor.paper.Paper;
import com.aurora.paperviewerprocessor.paper.PaperSection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment containing the view for a section of the paper
 */
public class SectionFragment extends Fragment implements View.OnClickListener {

    /**
     * The fragment argument representing the index of this section in the paper
     * for this fragment.
     */
    private static final String ARG_SECTION_INDEX = "section_index";

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
    private TextView mSectionHeader;

    /**
     * The {@link WebView} for displaying the content of the section
     */
    private WebView mSectionWebView;

    /**
     * Button for navigating to the previous section at the top of the fragment
     */
    private ImageButton mBtnTopNavLeft;

    /**
     * Button for navigating to the previous section at the top of the fragment
     */
    private ImageButton mBtnBottomNavLeft;

    /**
     * Button for navigating to the next section at the top of the fragment
     */
    private ImageButton mBtnTopNavRight;

    /**
     * Button for navigating to the next section at the bottom of the fragment
     */
    private ImageButton mBtnBottomNavRight;

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
        mSettingsListener = (SharedPreferences prefs, String key) -> {
            Paper paper = mPaperViewModel.getPaper().getValue();
            setContent(paper, sectionIndex, prefs);
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

    private void setContent(Paper paper, int sectionIndex, SharedPreferences sharedPref){
        // Obtain the current font settings
        String defaultFontFamily = getActivity().getResources().getString(R.string.default_font_family);
        String fontFamily = sharedPref.getString("fontFamily", defaultFontFamily);
        int defaultFontSize = getActivity().getResources().getInteger(R.integer.default_section_font_size);
        int fontSize = sharedPref.getInt("fontSize", defaultFontSize);

        // Set the text properties of the section content
        String htmlFront = "<html><head>" +
                "<style type=\"text/css\">body {" +
                "font-family: " + fontFamily + ", sans serif;" +
                "font-size: " + fontSize + ";" +
                "font-weight: " + getResources().getString(R.string.section_font_weight) + ";" +
                "text-align: " + getResources().getString(R.string.section_text_align) + ";" +
                "}</style></head><body>";
        String htmlEnd = "</body></html>";

        PaperSection section = paper.getSections().get(sectionIndex);
        StringBuilder headerBuilder = new StringBuilder();
        for(String header : section.getHeader()){
            headerBuilder.append(header).append("\n");
        }
        mSectionHeader.setText(headerBuilder.toString());

        String myHtmlString = htmlFront + htmlFormatContent(section.getContent()) + htmlEnd;
        mSectionWebView.loadDataWithBaseURL(null, myHtmlString, "text/html", "UTF-8", null);

        // Remove bottom navigation button in case the button is visible if positioned at the start view
        mSectionView.post(() -> {
            if(!isVisibleInRootView(mSectionView, mBtnBottomNavRight)){
                mBtnBottomNavRight.setVisibility(View.INVISIBLE);
            } else{
                mBtnBottomNavRight.setVisibility(View.VISIBLE);
            }
            if(!isVisibleInRootView(mSectionView, mBtnBottomNavLeft)){
                mBtnBottomNavLeft.setVisibility(View.INVISIBLE);
            } else{
                mBtnBottomNavLeft.setVisibility(View.VISIBLE);
            }
        });
    }

    private static String htmlFormatContent(String content){
        return content.replace("\n", "<br><br>");
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
                case R.id.btn_section_top_nav_left:
                case R.id.btn_section_bottom_nav_left:
                    if(canNavigateLeft(sectionIndex)) {
                        sectionViewPager.setCurrentItem(prevSectionPosition(sectionIndex));
                    }
                    break;
                case R.id.btn_section_top_nav_right:
                case R.id.btn_section_bottom_nav_right:
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
        mBtnTopNavLeft = mSectionView.findViewById(R.id.btn_section_top_nav_left);
        mBtnBottomNavLeft = mSectionView.findViewById(R.id.btn_section_bottom_nav_left);
        mBtnTopNavRight = mSectionView.findViewById(R.id.btn_section_top_nav_right);
        mBtnBottomNavRight = mSectionView.findViewById(R.id.btn_section_bottom_nav_right);
        mBtnTopNavLeft.setOnClickListener(this);
        mBtnBottomNavLeft.setOnClickListener(this);
        mBtnTopNavRight.setOnClickListener(this);
        mBtnBottomNavRight.setOnClickListener(this);
        if(canNavigateLeft(sectionIndex)){
            mBtnTopNavLeft.setVisibility(View.VISIBLE);
            mBtnBottomNavLeft.setVisibility(View.VISIBLE);
        } else{
            mBtnTopNavLeft.setVisibility(View.INVISIBLE);
            mBtnBottomNavLeft.setVisibility(View.INVISIBLE);
        }
        if(canNavigateRight(sectionIndex)){
            mBtnTopNavRight.setVisibility(View.VISIBLE);
            mBtnBottomNavRight.setVisibility(View.VISIBLE);
        } else{
            mBtnTopNavRight.setVisibility(View.INVISIBLE);
            mBtnBottomNavRight.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Checks whether the view is (partially) visible in the given root view
     *
     * @param rootView The root view in which to check the visibility
     * @param view The view to check it's visibility in the root view
     * @return Boolean indicating the visibility of the view in the root view
     */
    private boolean isVisibleInRootView(View rootView, View view){
        Rect scrollBounds = new Rect();
        rootView.getHitRect(scrollBounds);
        return view.getLocalVisibleRect(scrollBounds);
    }


}

