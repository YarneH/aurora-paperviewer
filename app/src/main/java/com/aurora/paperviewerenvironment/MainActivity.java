package com.aurora.paperviewerenvironment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.paperviewerprocessor.facade.PaperProcessorCommunicator;
import com.aurora.paperviewerprocessor.paper.Paper;

import java.util.Objects;

/**
 * <p>
 * Activity class that is shown on launch.
 * Handles the general setup of paperviewer, and is responsible for switching between fragments.
 * </p>
 * <p>
 * There are two main fragments: the fragment with the reading content, and a fragment
 * with the overview of the structure.
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Communicator that acts as an interface to the BasicPlugin's processor
     */
    private PaperProcessorCommunicator mBasicProcessorCommunicator = new PaperProcessorCommunicator();

    /**
     * {@link Toolbar} for displaying various functionality buttons at the top of the application
     */
    private Toolbar mToolbar;

    /**
     * Container for holding the gallery and the enlarged view for the images
     */
    private FrameLayout mImageContainer;

    /**
     * {@link ViewPager} for displaying the abstract and the sections of the paper.
     */
    private ViewPager mViewPager;

    /**
     * the {@link SectionPagerAdapter} for loading the correct fragments in the {@link ViewPager}
     */
    private SectionPagerAdapter mSectionPagerAdapter;

    /**
     * The processed paper
     */
    private Paper mPaper;

    public MainActivity() {
        // Default constructor
    }

    /**
     * Called upon creation of this activity. See android lifecycle for more info.
     *
     * @param savedInstanceState State to load
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the toolbar as supportActionBar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Disable the display of the app title in the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Below is the code used to handle communication with aurora and plugins.
        Intent intentThatStartedThisActivity = getIntent();
        if (Objects.equals(intentThatStartedThisActivity.getAction(), Constants.PLUGIN_ACTION)) {

            mPaper = null;

            // TODO remove this if statement probably.
            // TODO Is currently used to handle cases where a plain String is sent instead of an ExtractedText
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                String inputText =
                        intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
                mPaper = (Paper) mBasicProcessorCommunicator.process(inputText);
            }

            // Handle ExtractedText object (received when first opening a new file)
            else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                String inputTextJSON = 
                        intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT);
                ExtractedText inputText = ExtractedText.fromJson(inputTextJSON);
                mPaper = (Paper) mBasicProcessorCommunicator.process(inputText);

            // TODO handle a BasicPluginObject that was cached (will come in Json format)
            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)) {
                return;
            }
        }

        mPaper = new Paper();

        // Dummy paper makes this statement always true, but this will not be the case
        // when the dummy paper is removed
        if (mPaper != null) { // NOSONAR
            // Create the adapter for loading the correct section fragment
            mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the section adapter
            mViewPager = findViewById(R.id.vp_sections);
            mViewPager.setAdapter(mSectionPagerAdapter);
            // Allocate retention buffers for the loaded section/abstract fragments
            mViewPager.setOffscreenPageLimit(mSectionPagerAdapter.getCount());

            // Prepare the image container
            mImageContainer = findViewById(R.id.image_container);
            mImageContainer.setVisibility(View.GONE);

            // Add the fragment for the gallery / enlarged image to the image container
            FragmentManager fm = getSupportFragmentManager();
            ImageFragment imageFragment = ImageFragment.newInstance();
            imageFragment.setPaper(mPaper);
            fm.beginTransaction().add(R.id.image_container, imageFragment).commit();
        }
    }

    /**
     * Called on creation of the options menu.
     * Adds items to the action bar if it is present.
     *
     * @param menu Menu that is added
     * @return true when successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public ViewPager getSectionViewPager(){
        return mViewPager;
    }

    /**
     * Called when a menu option is selected.
     *
     * @param item the selected item
     * @return true when successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_search || id == R.id.action_overview) {
            return true;
        } else if (id == R.id.action_images) {
            if (mImageContainer.getVisibility() == View.VISIBLE) {
                mImageContainer.setVisibility(View.GONE);
            } else {
                mImageContainer.setVisibility(View.VISIBLE);
            }
            Log.d(MainActivity.class.getSimpleName(), "" + mImageContainer.getVisibility());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections of the paper.
     */
    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given section/abstract
            if (mPaper.getAbstract() != null) {
                if (position == 0) {
                    AbstractFragment abstractFragment = AbstractFragment.newInstance();
                    abstractFragment.setPaper(mPaper);
                    return abstractFragment;
                }
                SectionFragment sectionFragment = SectionFragment.newInstance(position - 1);
                sectionFragment.setPaper(mPaper);
                return sectionFragment;
            }
            SectionFragment sectionFragment = SectionFragment.newInstance(position);
            sectionFragment.setPaper(mPaper);
            return sectionFragment;
        }

        @Override
        public int getCount() {
            if (mPaper.getAbstract() != null) {
                return (1 + mPaper.getSections().size());
            }
            return mPaper.getSections().size();
        }
    }
}
