package com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

@SuppressLint("AppCompatCustomView")
public class SeekBarCustom extends SeekBar {
    public SeekBarCustom(Context context) {
        super(context);
    }

    public SeekBarCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
