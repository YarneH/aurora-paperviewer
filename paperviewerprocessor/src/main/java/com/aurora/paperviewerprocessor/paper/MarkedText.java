package com.aurora.paperviewerprocessor.paper;

import android.graphics.Color;

public class MarkedText {

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
        return 0;
    }

    public int getEndIndex() {
        // Dummy data
        return 5;
    }
}
