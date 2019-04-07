package com.aurora.paperviewerprocessor.paper;

import android.graphics.Color;

public class MarkedText {

    // TODO: remove
    private static final int DUMMY_BEGIN_INDEX = 0;
    private static final int DUMMY_END_INDEX = 5;

    private int mColor;
    private int beginIndex;
    private int mEndIndex;

    public MarkedText(int color, int beginIndex, int endIndex) {
        this.mColor = color;
        this.beginIndex = beginIndex;
        this.mEndIndex = endIndex;
    }

    public MarkedText(){}

    public int getColor() {
        // Dummy data
        return Color.YELLOW;
    }

    public int getBeginIndex() {
        // Dummy data
        return DUMMY_BEGIN_INDEX;
    }

    public int getEndIndex() {
        // Dummy data
        return DUMMY_END_INDEX;
    }
}
