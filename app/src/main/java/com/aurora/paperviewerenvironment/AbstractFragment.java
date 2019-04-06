package com.aurora.paperviewerenvironment;

import android.graphics.Color;
import android.graphics.Rect;
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
     * The size of this abstract
     * TODO set this to appropriate size based on the content
     */
    private static final int ABSTRACT_SIZE = 1;

    private Paper mPaper;

    /**
     * The root {@link android.support.v4.widget.NestedScrollView}
     */
    private View mAbstractView;

    /**
     * Text area's for displaying the content from the section
     */
    private TextView mSectionHeader;
    private WebView mAbstractWebView;

    /**
     * Button's for navigating to the other sections and the abstract
     */
    private ImageButton mBtnTopNavRight;
    private ImageButton mBtnBottomNavRight;

    public AbstractFragment() {
        // Empty constructor (generated by Android Studio)
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AbstractFragment newInstance() {
        return new AbstractFragment();
    }

    public void setPaper(Paper paper){
        this.mPaper = paper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the view for a section
        mAbstractView = inflater.inflate(R.layout.fragment_abstract, container, false);

        mSectionHeader = mAbstractView.findViewById(R.id.abstract_header);
        mSectionHeader.setText(getString(R.string.abstract_header));

        // Initialize navigation buttons
        mBtnTopNavRight = mAbstractView.findViewById(R.id.btn_abstract_top_nav_right);
        mBtnBottomNavRight = mAbstractView.findViewById(R.id.btn_abstract_bottom_nav_right);
        mBtnTopNavRight.setOnClickListener(this);
        mBtnBottomNavRight.setOnClickListener(this);
        if(canNavigateRight()){
            mBtnTopNavRight.setVisibility(View.VISIBLE);
            mBtnBottomNavRight.setVisibility(View.VISIBLE);
        } else {
            mBtnTopNavRight.setVisibility(View.INVISIBLE);
            mBtnBottomNavRight.setVisibility(View.INVISIBLE);
        }

        // Remove bottom navigation button in case the button is visible if positioned at the start view
        mAbstractView.post(() ->{
            if(isVisibleInRootView(mAbstractView, mBtnBottomNavRight)){
                mBtnBottomNavRight.setVisibility(View.INVISIBLE);
            }
        });

        // Disable focus on initial view and remove scrolling of the web view
        mAbstractWebView = mAbstractView.findViewById(R.id.abstract_webview);
        mAbstractWebView.setFocusable(false);
        mAbstractWebView.setBackgroundColor(Color.TRANSPARENT);
        mAbstractWebView.setScrollContainer(false);

        // Set the text properties of the abstract content
        String htmlFront = "<html><head>" +
                "<style type=\"text/css\">body {" +
                "font-family: " + getResources().getString(R.string.abstract_font_family) + ";" +
                "font-size: " + getResources().getDimension(R.dimen.abstract_font_size) + ";" +
                "font-weight: " + getResources().getString(R.string.abstract_font_weight) + ";" +
                "text-align: " + getResources().getString(R.string.abstract_text_align) + ";" +
                "}</style></head><body>";
        String htmlEnd = "</body></html>";
        String myHtmlString = htmlFront + mPaper.getAbstract() + htmlEnd;

        // Add content to the webview
        mAbstractWebView.loadDataWithBaseURL(null, myHtmlString, "text/html", "UTF-8", null);
        return mAbstractView;
    }

    private boolean canNavigateRight(){
        return mPaper.getSections().size() > 0;
    }

    @Override
    public void onClick(View view) {
        ViewPager sectionViewPager = ((MainActivity) getActivity()).getSectionViewPager();

        switch(view.getId()){
            case R.id.btn_abstract_top_nav_right:
            case R.id.btn_abstract_bottom_nav_right:
                if(canNavigateRight()) {
                    sectionViewPager.setCurrentItem(ABSTRACT_SIZE);
                }
                break;
            default:
                break;
        }
    }

    /***
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

