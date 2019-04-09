package com.aurora.paperviewerprocessor.paper;

public class MarkedText {

    // TODO: remove
    private static final int DUMMY_BEGIN_INDEX = 0;
    private static final int DUMMY_END_INDEX = 5;
    private static final int DUMMY_COLOR = Color.YELLOW;

    private int mColor;
    private int mBeginIndex;
    private int mEndIndex;

    public MarkedText(int color, int beginIndex, int endIndex) {
        this.mColor = color;
        this.mBeginIndex = beginIndex;
        this.mEndIndex = endIndex;
    }


    public MarkedText(){
        mBeginIndex= DUMMY_BEGIN_INDEX;
        mEndIndex = DUMMY_END_INDEX;
        mColor = DUMMY_COLOR;
    }

    public int getColor() {
        // Dummy data
        return mColor;
    }

    public int getBeginIndex() {
        // Dummy data
        return mBeginIndex;
    }

    public int getEndIndex() {
        // Dummy data
        return mEndIndex;
    }
}
