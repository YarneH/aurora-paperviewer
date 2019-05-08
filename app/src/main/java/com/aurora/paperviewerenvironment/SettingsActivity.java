package com.aurora.paperviewerenvironment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    // String[] panelFontOptions = getResources().getStringArray(R.array....);
    private List<String> fontOptions = new ArrayList<>();

    private RadioGroup mFontFamilyGroup;

    /**
     * The {@link android.arch.lifecycle.AndroidViewModel}
     * for maintaining the paper it's data and state
     * across the lifecycles of the activity
     */
    private PaperViewModel mPaperViewModel;

    /**
     * Called upon creation of this activity. See android lifecycle for more info.
     *
     * @param savedInstanceState State to load
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPaperViewModel = ViewModelProviders.of(this).get(PaperViewModel.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFontFamilyGroup = findViewById(R.id.radio_group_font_family);

        // TODO put in resources
        fontOptions.add("Arial");
        fontOptions.add("Helvetica");
        fontOptions.add("Times New Roman");
        fontOptions.add("Verdana");

        for(int i = 0; i < fontOptions.size(); i++){
            String fontFamily = fontOptions.get(i);

            RadioButton btn = new RadioButton(this);
            btn.setText(fontFamily);

            btn.setOnClickListener((View view) -> {
                mPaperViewModel.setFontFamily(fontFamily);
                // mPaperViewModel.setTextSize();
            });

            mFontFamilyGroup.addView(btn);
        }
    }

}
