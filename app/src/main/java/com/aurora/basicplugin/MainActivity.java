package com.aurora.basicplugin;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.paperviewerprocessor.basicpluginobject.BasicPluginObject;
import com.aurora.paperviewerprocessor.facade.BasicProcessorCommunicator;

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
    private BasicProcessorCommunicator mBasicProcessorCommunicator = new BasicProcessorCommunicator();

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Below is the code used to handle communication with aurora and plugins.
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
                ;
                // TODO: use the resulting information.
                // String result = basicPluginObject.getResult();
            }
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
        }
        return super.onOptionsItemSelected(item);
    }
}
