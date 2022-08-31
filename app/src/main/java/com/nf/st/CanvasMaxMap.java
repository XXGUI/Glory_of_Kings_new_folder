package com.nf.st;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CanvasMaxMap extends View {
    public static List<Paint> paintList = new ArrayList<>();
    public static List<RectF> rectFList = new ArrayList<>();
    public static Integer width = 150, height = 270;

    /**
     * 调用初始化
     * @param context 上下文对象
     * @param number 生成方框数量
     */
    public CanvasMaxMap(Context context,int number) {
        super(context);
        for (int i = 0; i < number; i++) {
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#FFFFEB3B"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(7);
            paint.setFilterBitmap(true);
            paint.setAntiAlias(true);
            paintList.add(paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Paint paint : paintList) {
            RectF r = new RectF(0, 0, width, height);
            rectFList.add(r);
            canvas.drawRect(r, paint);
            canvas.save();
        }
    }

    /**
     * 更新数据，重绘View
     */
    public void notifyView() {
        invalidate();
    }

    /**
     * 设置坐标
     */
    public void setXY(List<String> xy) {
        for (int i = 0; i < xy.size(); i++) {
            String xyArr[] = xy.get(i).split(",");
            RectF rectF = rectFList.get(i);
            rectF.top = Float.valueOf(xyArr[0]);
            rectF.left = Float.valueOf(xyArr[1]);
        }
    }
}
