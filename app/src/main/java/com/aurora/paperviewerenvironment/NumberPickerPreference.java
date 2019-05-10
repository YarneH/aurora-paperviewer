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
 * A {@link android.preference.Preference} that displays a number picker as a dialog. <br>
 * For more information see to the following link: <br>
 * @see <a href="https://stackoverflow.com/questions/20758986/android-preferenceactivity-dialog-with-number-picker">
 *     https://stackoverflow.com/questions/20758986/android-preferenceactivity-dialog-with-number-picker</a>
 */
public class NumberPickerPreference extends DialogPreference {

    /**
     * Upper range of the picker
     */
    public static final int MAX_VALUE = 100;

    /**
     * Lower range for the picker
     */
    public static final int MIN_VALUE = 0;

    /**
     * Enable or disable the circular behavior of the picker
     */
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    /**
     * The number picker in the dialog box
     */
    private NumberPicker picker;

    /**
     * The current value represented in the NumberPicker
     */
    private int value;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

        picker = new NumberPicker(getContext());
        picker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

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
        picker.setMinValue(MIN_VALUE);
        picker.setMaxValue(MAX_VALUE);
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(getValue());
    }

    /**
     * Called upon closing the dialog view
     *
     * @param positiveResult whether or not the value has been altered
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    /**
     * Called when the default value is fetched, can be overridden
     * by specifying custom default value in the NumberPicker xml.
     *
     * @param a the values in the picker
     * @param index the index that should be set as default
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, MIN_VALUE);
    }

    /**
     * Called upon setting the initial value for the dialog view.
     *
     * @param restorePersistedValue whether or not a previous value should be fetched
     * @param defaultValue the default value for the picker
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
     * Sets the picker to the given value
     *
     * @param value the value for the picker
     */
    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    /**
     * @return the value for the picker
      */
    public int getValue() {
        return this.value;
    }
}