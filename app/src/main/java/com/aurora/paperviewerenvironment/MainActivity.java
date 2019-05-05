package com.aurora.paperviewerenvironment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.paperviewerprocessor.paper.Paper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * <p>
 * Activity class that is shown on launch.
 * Handles the general setup of paperviewer, and is responsible for switching between fragments.
 * </p>
 * <p>
 * There are two main fragment containers:
 * A container for various enhanced split-screen functionality
 * (image gallery, enlarged image content, search browser, ...)
 * and a container for the textual content of the paper.
 * The sections within the textual content are encapsulated in a
 * {@link ViewPager} to allow for scrolling between the sections.
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Tag for logging.
     */
    private static final String CLASS_TAG = MainActivity.class.getSimpleName();
    /**
     * {@link Toolbar} for displaying various functionality buttons at the top of the application
     */
    private Toolbar mToolbar;

    /**
     * Container for holding the gallery and the enlarged view for the images
     */
    private FrameLayout mImageContainer;

    /**
     * {@link ViewPager} for displaying the abstract and the sections of the paper
     */
    private ViewPager mViewPager;

    /**
     * the {@link SectionPagerAdapter} for loading the correct fragments in the {@link ViewPager}
     */
    private SectionPagerAdapter mSectionPagerAdapter;

    /**
     * The {@link android.arch.lifecycle.AndroidViewModel}
     * for maintaining the paper it's data and state
     * across the lifecycles of the activity
     */
    private PaperViewModel mPaperViewModel;

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
        mPaperViewModel = ViewModelProviders.of(this).get(PaperViewModel.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the toolbar as supportActionBar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Disable the display of the app title in the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*
         * Handle Aurora starting the Plugin.
         */
        Intent intentThatStartedThisActivity = getIntent();

        boolean intentIsOkay = true;

        if(intentThatStartedThisActivity.getAction() == null) {
            Toast.makeText(this, "ERROR: The intent had no action.",
                    Snackbar.LENGTH_LONG).show();
            intentIsOkay = false;
        } else if(!intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            Toast.makeText(this, "ERROR: The intent had incorrect action.",
                    Snackbar.LENGTH_LONG).show();
            intentIsOkay = false;
        } else if(!intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TYPE)) {
            Toast.makeText(this, "ERROR: The intent had no specified input type.",
                    Snackbar.LENGTH_LONG).show();
            intentIsOkay = false;
        }

        if (intentIsOkay){
            handleIntentThatOpenedPlugin(intentThatStartedThisActivity);
        }


        mPaperViewModel.getPaper().observe(this, (Paper paper) -> {
            if(paper == null){
                return;
            }
            // Create the adapter for loading the correct section fragment
            mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(), paper);

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
            fm.beginTransaction().add(R.id.image_container, imageFragment).commit();
        });
    }

    /**
     * Initializes mPaper according to the parameters in the Intent that opened the plugin
     *
     * @param intentThatStartedThisActivity Intent that opened the plugin
     */
    private void handleIntentThatOpenedPlugin(Intent intentThatStartedThisActivity){
        // Get the Uri to the transferred file
        Uri fileUri = intentThatStartedThisActivity.getData();
        if(fileUri == null) {
            Toast.makeText(this, "ERROR: The intent had no url in the data field",
                    Snackbar.LENGTH_LONG).show();
        } else {
            // Get the input type
            String inputType = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TYPE);
            // Switch on the different kinds of input types that could be in the temp file
            switch (inputType) {
                case Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT:
                    // Convert the read file to an ExtractedText object
                    convertReadFileToExtractedText(fileUri);
                    break;
                case Constants.PLUGIN_INPUT_TYPE_OBJECT:
                    // Convert the read file to an PluginObject
                    convertReadFileToPaper(fileUri);
                    break;
                default:
                    Toast.makeText(this, "ERROR: The intent had an unsupported input type.",
                            Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Convert the read file to an ExtractedText object
     *
     * @param fileUri Uri to the file
     */
    private void convertReadFileToExtractedText(Uri fileUri){
        try {
            ExtractedText extractedText = ExtractedText.getExtractedTextFromFile( fileUri,
                    this);
            if (extractedText != null) {
                Log.d(CLASS_TAG, "Loading extracted text.");
                mPaperViewModel.initialiseWithExtractedText(extractedText);
            } else {
                // Error in case ExtractedText was null.
                Log.e(CLASS_TAG, "ExtractedText-object was null.");
            }
        } catch (IOException e) {
            Log.e(CLASS_TAG, "IOException while loading data from aurora", e);
        }
    }

    /**
     * Convert the read file to an PluginObject
     *
     * @param fileUri Uri to the file
     */
    private void convertReadFileToPaper(Uri fileUri){
        try {
            Paper receivedObject = Paper.getPluginObjectFromFile(fileUri, this, Paper.class);

            Log.d(CLASS_TAG, "Loading cashed Object.");
            mPaperViewModel.initialiseWithPaper(receivedObject);

        } catch (IOException e) {
            Log.e(CLASS_TAG, "IOException while loading data from aurora", e);
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
            if(!mPaperViewModel.hasImages()){
                Toast.makeText(this, "No images were found for this paper.", Snackbar.LENGTH_LONG).show();
                return false;
            }
            if (mImageContainer.getVisibility() == View.VISIBLE) {
                mImageContainer.setVisibility(View.GONE);
            } else {
                mImageContainer.setVisibility(View.VISIBLE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private ExtractedText getExtractedTextFromFile(Uri fileUri){
        StringBuilder total = new StringBuilder();
        ParcelFileDescriptor inputPFD = null;
        if(fileUri != null) {
            // Open the file
            try {
                inputPFD = getContentResolver().openFileDescriptor(fileUri, "r");
            } catch (FileNotFoundException e) {
                Log.e("MAIN", "There was a problem receiving the file from " +
                        "the plugin", e);
            }

            // Read the file
            if (inputPFD != null) {
                InputStream fileStream = new FileInputStream(inputPFD.getFileDescriptor());


                try (BufferedReader r = new BufferedReader(new InputStreamReader(fileStream))) {
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                } catch (IOException e) {
                    Log.e("MAIN", "There was a problem receiving the file from " +
                            "the plugin", e);
                }
            } else {
                Log.e("MAIN", "There was a problem receiving the file from " +
                        "the plugin");
            }
        } else {
                Log.e("MAIN", "There was a problem receiving the file from " +
                        "the plugin");
        }

        // Convert the read file to an ExtractedText object
        return ExtractedText.fromJson(total.toString());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections of the paper.
     */
    public class SectionPagerAdapter extends FragmentPagerAdapter {

        /**
         * The current processed paper
         */
        private Paper mPaper;

        public SectionPagerAdapter(FragmentManager fm, Paper paper) {
            super(fm);
            mPaper = paper;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given section/abstract
            if (mPaper.getAbstract() != null) {
                if (position == 0) {
                    return AbstractFragment.newInstance();
                }
                return SectionFragment.newInstance(position - 1);
            }
            return SectionFragment.newInstance(position);
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
