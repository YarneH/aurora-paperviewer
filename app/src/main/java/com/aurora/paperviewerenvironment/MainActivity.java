package com.aurora.paperviewerenvironment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import android.util.Pair;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
     * The table of contents of the {@link Paper}. Maps the table of content entry
     * to the hierarchy level and the section index of the entry.
     */
    private Map<String, Pair<Integer, Integer>> mTableOfContents;

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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // The header in the NavigationView
        View headerView = mNavigationView.getHeaderView(0);
        mPaperTitleView = headerView.findViewById(R.id.paper_title);
        mPaperAuthorView = headerView.findViewById(R.id.paper_author);

        // Below is the code used to handle communication with aurora and plugins.
        Intent intentThatStartedThisActivity = getIntent();
        if (Objects.equals(intentThatStartedThisActivity.getAction(), Constants.PLUGIN_ACTION)) {

            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                // Get the Uri to the transferred file
                Uri fileUri = intentThatStartedThisActivity.getData();

                // Convert the read file to an ExtractedText object
                ExtractedText inputText = getExtractedTextFromFile(fileUri);
                mPaperViewModel.initialiseWithExtractedText(inputText);
            // TODO handle a BasicPluginObject that was cached (will come in Json format)
            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)) {
                return;
            }
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
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {
                    // No additional behavior needed when scrolling
                }
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // No additional behavior needed when scrolling
                }
                public void onPageSelected(int position) {
                    // Upon changing to different section, check the appropriate section in table of contents
                    setCheckedTableOfContents();
                }
            });

            // Set up  the paper title and authors in the NavigationView
            mPaperTitleView.setText(paper.getTitle());
            StringBuilder authorBuilder = new StringBuilder();
            for(String author : paper.getAuthors()){
                authorBuilder.append(author).append("\n");
            }
            mPaperAuthorView.setText(authorBuilder.toString());

            // Set up the table of contents in the NavigationView
            Menu menu = mNavigationView.getMenu();
            mTableOfContentsSubMenu = menu.addSubMenu(R.string.table_of_contents);

            mTableOfContents = toTableOfContents(paper);
            Iterator<Map.Entry<String, Pair<Integer, Integer>>> itr = mTableOfContents.entrySet().iterator();
            while(itr.hasNext()) {
                Map.Entry<String, Pair<Integer, Integer>> tocEntry = itr.next();

                // Apply indentation based on hierarchy level
                StringBuilder tocEntryBuilder = new StringBuilder();
                for(int i = 0; i < tocEntry.getValue().first; i++){
                    tocEntryBuilder.append("\t  ");
                }
                tocEntryBuilder.append(tocEntry.getKey());
                MenuItem item = mTableOfContentsSubMenu.add(tocEntryBuilder.toString());

                // Register listener for navigation within table of contents to correct section
                item.setOnMenuItemClickListener((MenuItem menuItem) -> {
                    mViewPager.setCurrentItem(getViewPagerPosition(tocEntry.getValue().second));
                    mDrawerLayout.closeDrawer(Gravity.START, false);
                    return true;
                });
            }
            setCheckedTableOfContents();

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
     * Sets the table of contents entry of the currently active section as checked. <br>
     * Un-checks the other table of content entries.
     */
    private void setCheckedTableOfContents(){
        // Table of content values are indexed by their position in the table of content
        List<Pair<Integer, Integer>> tableOfContentValues = new ArrayList<>(mTableOfContents.values());

        for(int i = 0; i < tableOfContentValues.size(); i++){
            int sectionIndex = tableOfContentValues.get(i).second;
            boolean isCurrentSection = getViewPagerPosition(sectionIndex) == mViewPager.getCurrentItem();
            mTableOfContentsSubMenu.getItem(i).setChecked(isCurrentSection);
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
     * Helper function for creating the table of contents (TOC) for in the {@link NavigationView} <br>
     * This function will link the TOC entry (header string of a section) to the index in the paper.
     * While also maintaining the hierarchy level for identation in the TOC. <br>
     * For a header title which is not on the lowest hierarchical level, this index value
     * will be the index of the first lowest level section encountered. <br>
     * The {@link LinkedHashMap} preserves their respective position for in the table of contents. <br>
     *
     * @return the header string, mapped to: the hierarchy level and the index of the section. <br>
     */
    private Map<String, Pair<Integer, Integer>> toTableOfContents(Paper paper){
        LinkedHashMap<String, Pair<Integer, Integer>> tableOfContents = new LinkedHashMap<>();

        for(int sectionIndex = 0; sectionIndex < paper.getSections().size(); sectionIndex++){
            PaperSection section = paper.getSections().get(sectionIndex);
            int headerSize = section.getHeader().size();
            for(int h = 0; h < headerSize; h++){
                if(!tableOfContents.containsKey(section.getHeader().get(h))){
                    Pair<Integer, Integer> indices = new Pair<>(h, sectionIndex);
                    tableOfContents.put(section.getHeader().get(h), indices);
                }
            }
        }
        return tableOfContents;
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
        if (id == R.id.action_search) {
            // TODO implement searching content here
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
        int id = item.getItemId();
        if (id == R.id.nav_settings) {
            // TODO implement settings redirect activity such as text font here.
        }
        // The listener for the table of content entries has already been configured
        // in the creation of the menu items.
        return true;
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
