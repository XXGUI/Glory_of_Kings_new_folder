package com.qy;

import android.content.Context;
import android.graphics.Color;
import android.widget.TabWidget;
import android.widget.TextView;

public class TextAppearanceUtil {

    /**
     * 设置TabWidget的标题的字体
     *
     * @param tabWidget 要设置的TabWidget
     * @param size      字体大小
     */
    public static void setTabWidgetTitle(TabWidget tabWidget, int size) {
        for (int i = 0, count = tabWidget.getChildCount(); i < count; i++) {
            ((TextView) tabWidget.getChildAt(i)
                    .findViewById(android.R.id.title)).setTextSize(size);
        }
    }

    /**
     * 设置TabWidget
     *
     * @param tabWidget 要设置的TabWidget
     * @param size      字体大小
     * @param color
     */
    public static void setTabWidgetTitle(TabWidget tabWidget, int size,String color) {
        TextView tv = null;
        for (int i = 0, count = tabWidget.getChildCount(); i < count; i++) {
            tv = ((TextView) tabWidget.getChildAt(i).findViewById(
                    android.R.id.title));
            tv.setTextSize(size);
            String[] cs = color.split(",");
            tv.setTextColor(Color.rgb(Integer.valueOf(cs[0]), Integer.valueOf(cs[1]), Integer.valueOf(cs[2])));
        }
    }
}
