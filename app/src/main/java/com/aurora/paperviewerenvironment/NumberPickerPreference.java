package com.aurora.paperviewerenvironment;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

/**
 * A {@link android.preference.Preference} that displays a number mFontSizePicker as a dialog. <br>
 * The result is a font size preference that can be set in the {@link android.content.SharedPreferences}. <br>
 * The font size preference can be set in the {@link SettingsActivity}. <br>
 * For more information see to the following link: <br>
 * @see <a href="https://stackoverflow.com/questions/20758986/android-preferenceactivity-dialog-with-number-picker">
 *     https://stackoverflow.com/questions/20758986/android-preferenceactivity-dialog-with-number-picker</a>
 */
public class NumberPickerPreference extends DialogPreference {

    /**
     * Upper range of the mFontSizePicker
     */
    public static final int MAX_FONT_SIZE = 100;

    /**
     * Lower range for the mFontSizePicker
     */
    public static final int MIN_FONT_SIZE = 0;

    /**
     * Enable or disable the circular behavior of the mFontSizePicker
     */
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    /**
     * The number mFontSizePicker in the dialog box
     */
    private NumberPicker mFontSizePicker;

    /**
     * The current mFontSize represented in the NumberPicker
     */
    private int mFontSize;

    public NumberPickerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public NumberPickerPreference(Context context, AttributeSet attributeSet, int defStyleAttribute) {
        super(context, attributeSet, defStyleAttribute);
    }

    /**
     * Called upon creation of the dialog view.
     *
     * @return the dialog view
     */
    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        mFontSizePicker = new NumberPicker(getContext());
        mFontSizePicker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(mFontSizePicker);

        return dialogView;
    }

    /**
     * Called upon binding the dialog view
     *
     * @param view the view on which it will be bound
     */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mFontSizePicker.setMinValue(MIN_FONT_SIZE);
        mFontSizePicker.setMaxValue(MAX_FONT_SIZE);
        mFontSizePicker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        mFontSizePicker.setValue(getValue());
    }

    /**
     * Called upon closing the dialog view
     *
     * @param positiveResult whether or not the mFontSize has been altered
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mFontSizePicker.clearFocus();
            int newValue = mFontSizePicker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    /**
     * Called when the default mFontSize is fetched, can be overridden
     * by specifying custom default mFontSize in the NumberPicker xml.
     *
     * @param a the values in the mFontSizePicker
     * @param index the index that should be set as default
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, MAX_FONT_SIZE);
    }

    /**
     * Called upon setting the initial mFontSize for the dialog view.
     *
     * @param restorePersistedValue whether or not a previous mFontSize should be fetched
     * @param defaultValue the default mFontSize for the mFontSizePicker
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if(restorePersistedValue){
            setValue(getPersistedInt(MAX_FONT_SIZE));
        } else {
            setValue((int) defaultValue);
        }
    }

    /**
     * Sets the mFontSizePicker to the given mFontSize
     *
     * @param value the mFontSize for the mFontSizePicker
     */
    public void setValue(int value) {
        this.mFontSize = value;
        persistInt(this.mFontSize);
    }

    /**
     * @return the mFontSize for the mFontSizePicker
      */
    public int getValue() {
        return this.mFontSize;
    }
}
