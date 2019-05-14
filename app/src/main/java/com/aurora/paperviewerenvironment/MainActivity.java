package com.aurora.paperviewerenvironment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.paperviewerprocessor.paper.Paper;
import com.aurora.paperviewerprocessor.paper.PaperSection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Indication for a table of content entry that this entry does not
     * redirect to a certain section. The chosen value is -1 because
     * the index of a section can never be negative
     */
    private static final int NO_NAVIGATION = -1;

    /**
     * Tag for logging.
     */
    private static final String CLASS_TAG = MainActivity.class.getSimpleName();
    /**
     * {@link Toolbar} for displaying various functionality buttons at the top of the application
     */
    private Toolbar mToolbar;

    /**
     * {@link NavigationView} for navigating to the settings and the table of contents
     */
    private NavigationView mNavigationView;

    /**
     * {@link DrawerLayout} containing the NavigationView, can be opened by swiping
     * from the left or clicking the drawer icon in the {@link Toolbar}
     */
    private DrawerLayout mDrawerLayout;

    /**
     * {@link SubMenu} for representing the items in the table of contents
     */
    private SubMenu mTableOfContentsSubMenu;

    /**
     * {@link TextView} which contains the title of the paper if present
     */
    private TextView mPaperTitleView;

    /**
     * {@link TextView} which contains the authors of the paper if present
     */
    private TextView mPaperAuthorView;

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

        // Set the NavigationView
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Set the toolbar as supportActionBar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Disable the display of the app title in the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Listener and sync are for NavigationView functionality
        setUpDrawerLayout();

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

            // Set up the table of contents in the NavigationView
            Menu menu = mNavigationView.getMenu();
            mTableOfContentsSubMenu = menu.addSubMenu(R.string.table_of_contents);
            List<Integer> tocViewPagerPositions = setUpTableOfContents(paper);

            // Set up the ViewPager with the section adapter
            mViewPager = findViewById(R.id.vp_sections);
            mViewPager.setAdapter(mSectionPagerAdapter);
            // Allocate retention buffers for the loaded section/abstract fragments
            mViewPager.setOffscreenPageLimit(mSectionPagerAdapter.getCount());
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {
                    // No additional behavior needed when scrolling
                }
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // No additional behavior needed when scrolling
                }
                public void onPageSelected(int position) {
                    // Upon changing to different section, check the appropriate section in table of contents
                    setCheckedTableOfContents(tocViewPagerPositions);
                }
            });
            setCheckedTableOfContents(tocViewPagerPositions);

            // Set up  the paper title and authors in the NavigationView
            mPaperTitleView.setText(paper.getTitle());
            StringBuilder authorBuilder = new StringBuilder();
            for(String author : paper.getAuthors()){
                authorBuilder.append(author).append("\n");
            }
            mPaperAuthorView.setText(authorBuilder.toString());

            // Prepare the image container
            mImageContainer = findViewById(R.id.image_container);
            mImageContainer.setVisibility(View.VISIBLE);

            // Add the fragment for the gallery / enlarged image to the image container
            FragmentManager fm = getSupportFragmentManager();
            ImageFragment imageFragment = ImageFragment.newInstance();
            fm.beginTransaction().add(R.id.image_container, imageFragment).commit();
        });
    }

    /**
     * Sets up the drawer layout and adds a listener for opening from the menu bar.
     * The drawer layout includes the {@NavigationView} and the header which
     * contains the {@link TextView}'s for the paper title and the authors.
     */
    private void setUpDrawerLayout(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // The header in the NavigationView
        View headerView = mNavigationView.getHeaderView(0);
        mPaperTitleView = headerView.findViewById(R.id.paper_title);
        mPaperAuthorView = headerView.findViewById(R.id.paper_author);
    }

    /**
     * Fills up the table of content in the {@link NavigationView} with the header content of the sections.
     *
     * @param paper the current paper which contains the sections.
     * @return list mapping the TOC entries to their position in the ViewPager.
     */
    private List<Integer> setUpTableOfContents(Paper paper){
        List<Integer> tocViewPagerPositions = new ArrayList<>();
        if(!paper.getAbstract().isEmpty()){
            addTableOfContentEntry(tocViewPagerPositions, getString(R.string.abstract_header), 0,true,0);
        }

        PaperSection prevSection = paper.getSections().get(0);
        for(int i = 0; i < prevSection.getHeader().size(); i++){
            addTableOfContentEntry(tocViewPagerPositions, prevSection.getHeader().get(i), i,
                    (i == prevSection.getLevel()), getViewPagerPosition(0));
        }
        for(int sectionIndex = 1; sectionIndex < paper.getSections().size(); sectionIndex++){
            PaperSection section = paper.getSections().get(sectionIndex);

            for(int level = 0; level < section.getHeader().size(); level++){
                String headerTitle = section.getHeader().get(level);
                // Equal titles on the same level with the previous section should be skipped
                if(!(level <= prevSection.getLevel() &&
                        !prevSection.getHeader().isEmpty() &&
                        prevSection.getHeader().get(level).equals(headerTitle))){
                    addTableOfContentEntry(tocViewPagerPositions, headerTitle, level,
                            (level == section.getLevel()), getViewPagerPosition(sectionIndex));
                }
            }
            prevSection = section;
        }
        return tocViewPagerPositions;
    }

    /**
     * Adds a table of content entry to the table of contents (TOC) {@link SubMenu} in the {@link NavigationView}. <br>
     * This includes configuration of the listener for navigating to the correct section and
     * configuration for checking the correct TOC entry based on the active section in the {@link ViewPager}. <br>
     *
     * @param tocViewPagerPositions stores the checking configuration, it maps the TOC entries to their
     *                              corresponding ViewPager positions
     * @param title the title of the TOC entry
     * @param level the level of the title, used for indentation of the TOC entry
     * @param lowestLevel boolean indicating whether the TOC entry is on the lowest hierarchical level
     * @param position the corresponding position in the ViewPager for this TOC entry
     */
    private void addTableOfContentEntry(List<Integer> tocViewPagerPositions, String title, int level,
                                        boolean lowestLevel, int position){
        // If a TOC entry is not on the lowest hierarchical level, it should never be checked hence the -1
        if(lowestLevel){
            tocViewPagerPositions.add(position);
        }
        else{
            tocViewPagerPositions.add(NO_NAVIGATION);
        }

        StringBuilder tocEntry = new StringBuilder();
        for(int i = 0; i < level; i++){
            tocEntry.append("\t  ");
        }
        tocEntry.append(title);
        MenuItem item = mTableOfContentsSubMenu.add(tocEntry.toString());

        // Register listener for navigation to the abstract
        item.setOnMenuItemClickListener((MenuItem menuItem) -> {
            mViewPager.setCurrentItem(position);
            mDrawerLayout.closeDrawer(Gravity.START, false);
            return true;
        });
    }

    /**
     * Sets the table of contents entry of the currently active section as checked. <br>
     * Un-checks the other table of content entries.
     *
     * @param tocViewPagerPositions the TOC entries mapped to their corresponding ViewPager positions
     */
    private void setCheckedTableOfContents(List<Integer> tocViewPagerPositions){
        for(int i = 0; i < tocViewPagerPositions.size(); i++) {
            boolean isCurrentPage = (tocViewPagerPositions.get(i) == mViewPager.getCurrentItem());
            mTableOfContentsSubMenu.getItem(i).setChecked(isCurrentPage);
        }
    }

    /**
     * Retrieves the position in the Section {@link ViewPager}
     * based on the index of the section in the {@link Paper}.
     *
     * @param sectionIndex the index of the section in the {@link Paper}
     * @return the position in the {@link ViewPager}
     */
    public int getViewPagerPosition(int sectionIndex){
        if(mPaperViewModel.hasAbstract()){
            return sectionIndex + 1;
        } else{
            return sectionIndex;
        }
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
            Toast.makeText(this, "ERROR: The intent had no uri in the data field",
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
     * Handles when leaving NavigationView (Drawer). Go back to main view.
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
     * <p>
     * Handles selection of options in NavigationView (Drawer layout).
     * </p>
     * <p>
     * The NavigationView contains the settings and the table of contents.
     * Selecting a section in the navigationview navigates to the
     * corresponding section.
     * </p>
     *
     * @param item Selected menu item.
     * @return whether or not successful.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
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
        if(id == R.id.action_images) {
            if(!mPaperViewModel.hasImages()){
                Toast.makeText(this, "No images were found for this paper.", Snackbar.LENGTH_LONG).show();
                return false;
            }
            if (mImageContainer.getVisibility() == View.VISIBLE) {
                mImageContainer.setVisibility(View.GONE);
            } else {
                mImageContainer.setVisibility(View.VISIBLE);
            }
        } else if(id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
            if (!mPaper.getAbstract().isEmpty()) {
                if (position == 0) {
                    return AbstractFragment.newInstance();
                }
                return SectionFragment.newInstance(position - 1);
            }
            return SectionFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            if (!mPaper.getAbstract().isEmpty()) {
                return (1 + mPaper.getSections().size());
            }
            return mPaper.getSections().size();
        }
    }

}
