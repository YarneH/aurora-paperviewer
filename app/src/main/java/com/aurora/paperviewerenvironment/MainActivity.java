package com.aurora.paperviewerenvironment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.paperviewerprocessor.basicpluginobject.BasicPluginObject;
import com.aurora.paperviewerprocessor.facade.BasicProcessorCommunicator;
import com.aurora.paperviewerprocessor.paper.Paper;

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

    private static final int GALLERY_IMG_HEIGHT = 180;

    private BasicProcessorCommunicator mBasicProcessorCommunicator = new BasicProcessorCommunicator();

    private Toolbar mToolbar;
    private SectionPagerAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;

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

        // TODO load the paper with the communicator class
        Paper paper = new Paper();

        // TODO: Load actual images. These are dummy.
//        LayoutInflater inflater = LayoutInflater.from(this);
//        LinearLayout gallery = findViewById(R.id.ll_gallery);
//        View view = inflater.inflate(R.layout.gallery_item, gallery, false);
//        ImageView img = view.findViewById(R.id.iv_gallery_image);
//        img.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.woods));
//        img.setMinimumHeight(50);
//        gallery.addView(view);

        // Set the toolbar as supportActionBar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Disable the display of the app title in the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set the images
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.lena);
        ImageView lena = findViewById(R.id.testimage1);
        double ratio = ((double)bm.getWidth()/bm.getHeight());
        int lenaWidth = (int) (ratio * GALLERY_IMG_HEIGHT);
        lena.setImageBitmap(Bitmap.createScaledBitmap(bm, lenaWidth, GALLERY_IMG_HEIGHT, false));

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.woods);
        ImageView woods = findViewById(R.id.testimage2);
        ratio = ((double)bm.getWidth()/bm.getHeight());
        int woodsWidth = (int) (ratio * GALLERY_IMG_HEIGHT);
        woods.setImageBitmap(Bitmap.createScaledBitmap(bm, woodsWidth, GALLERY_IMG_HEIGHT, false));

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.vanguard);
        ImageView vang = findViewById(R.id.testimage3);
        ratio = ((double)bm.getWidth()/bm.getHeight());
        int vanguardWidth = (int) (ratio * GALLERY_IMG_HEIGHT);
        vang.setImageBitmap(Bitmap.createScaledBitmap(bm, vanguardWidth, GALLERY_IMG_HEIGHT, false));

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.beach);
        ImageView beach = findViewById(R.id.testimage3);
        ratio = ((double)bm.getWidth()/bm.getHeight());
        int beachWidth = (int) (ratio * GALLERY_IMG_HEIGHT);
        beach.setImageBitmap(Bitmap.createScaledBitmap(bm, beachWidth, GALLERY_IMG_HEIGHT, false));

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.city);
        ImageView city = findViewById(R.id.testimage4);
        ratio = ((double)bm.getWidth()/bm.getHeight());
        int cityWidth = (int) (ratio * GALLERY_IMG_HEIGHT);
        city.setImageBitmap(Bitmap.createScaledBitmap(bm, cityWidth, GALLERY_IMG_HEIGHT, false));

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        ImageView car = findViewById(R.id.testimage5);
        ratio = ((double)bm.getWidth()/bm.getHeight());
        int carWidth = (int) (ratio * GALLERY_IMG_HEIGHT);
        car.setImageBitmap(Bitmap.createScaledBitmap(bm, carWidth, GALLERY_IMG_HEIGHT, false));

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.train);
        ImageView train = findViewById(R.id.testimage6);
        ratio = ((double)bm.getWidth()/bm.getHeight());
        int trainWidth = (int) (ratio * GALLERY_IMG_HEIGHT);
        train.setImageBitmap(Bitmap.createScaledBitmap(bm, trainWidth, GALLERY_IMG_HEIGHT, false));

        // Create the adapter for loading the correct section fragment
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(), paper);

        // Set up the ViewPager with the section adapter
        mViewPager = (ViewPager) findViewById(R.id.vp_sections);
        mViewPager.setAdapter(mSectionPagerAdapter);
        // Allocate retention buffers for the loaded section/abstract fragments
        mViewPager.setOffscreenPageLimit(mSectionPagerAdapter.getCount());

        /*
        // Below is the code used to handle communication with aurora and plugins.
        /*
        Intent intentThatStartedThisActivity = getIntent();
        if (Objects.equals(intentThatStartedThisActivity.getAction(), Constants.PLUGIN_ACTION)) {

            BasicPluginObject basicPluginObject = null;

            // TODO remove this if statement probably.
            // TODO Is currently used to handle cases where a plain String is sent instead of an ExtractedText
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                String inputText =
                        intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
                basicPluginObject = (BasicPluginObject) mBasicProcessorCommunicator.process(inputText);
            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                String inputTextJSON =
                        intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT);
                ExtractedText inputText = ExtractedText.fromJson(inputTextJSON);
                basicPluginObject = (BasicPluginObject) mBasicProcessorCommunicator.process(inputText);
            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)) {
                // TODO handle a PluginObject that was cached
            }


            if (basicPluginObject != null) {

                // TODO: use the resulting information.
                // String result = basicPluginObject.getResult();
            }

        }
        */
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
            HorizontalScrollView hsv = this.findViewById(R.id.include_image_gallery);
            if (hsv.getVisibility() == View.VISIBLE) {
                hsv.setVisibility(View.GONE);
            } else {
                hsv.setVisibility(View.VISIBLE);
            }
            Log.d(MainActivity.class.getSimpleName(), "" + hsv.getVisibility());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections of the paper
     */
    public class SectionPagerAdapter extends FragmentPagerAdapter {

        private Paper mPaper;

        public SectionPagerAdapter(FragmentManager fm, Paper paper) {
            super(fm);
            this.mPaper = paper;
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
