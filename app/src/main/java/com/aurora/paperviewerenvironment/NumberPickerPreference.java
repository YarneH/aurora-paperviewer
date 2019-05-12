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
 * A {@link android.preference.Preference} that displays a number mPicker as a dialog. <br>
 * The result is a mValue preference that will be set <br>
 * For more information see to the following link: <br>
 * @see <a href="https://stackoverflow.com/questions/20758986/android-preferenceactivity-dialog-with-number-picker">
 *     https://stackoverflow.com/questions/20758986/android-preferenceactivity-dialog-with-number-picker</a>
 */
public class NumberPickerPreference extends DialogPreference {

    /**
     * Upper range of the mPicker
     */
    public static final int MAX_VALUE = 100;

    /**
     * Lower range for the mPicker
     */
    public static final int MIN_VALUE = 0;

    /**
     * Enable or disable the circular behavior of the mPicker
     */
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    /**
     * The number mPicker in the dialog box
     */
    private NumberPicker mPicker;

    /**
     * The current mValue represented in the NumberPicker
     */
    private int mValue;

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

        mPicker = new NumberPicker(getContext());
        mPicker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(mPicker);

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
        mPicker.setMinValue(MIN_VALUE);
        mPicker.setMaxValue(MAX_VALUE);
        mPicker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        mPicker.setValue(getValue());
    }

    /**
     * Called upon closing the dialog view
     *
     * @param positiveResult whether or not the mValue has been altered
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mPicker.clearFocus();
            int newValue = mPicker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    /**
     * Called when the default mValue is fetched, can be overridden
     * by specifying custom default mValue in the NumberPicker xml.
     *
     * @param a the values in the mPicker
     * @param index the index that should be set as default
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, MIN_VALUE);
    }

    /**
     * Called upon setting the initial mValue for the dialog view.
     *
     * @param restorePersistedValue whether or not a previous mValue should be fetched
     * @param defaultValue the default mValue for the mPicker
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if(restorePersistedValue){
            setValue(getPersistedInt(MIN_VALUE));
        } else {
            setValue((int) defaultValue);
        }
    }

    /**
     * Sets the mPicker to the given mValue
     *
     * @param value the mValue for the mPicker
     */
    public void setValue(int value) {
        this.mValue = value;
        persistInt(this.mValue);
    }

    /**
     * @return the mValue for the mPicker
      */
    public int getValue() {
        return this.mValue;
    }
}