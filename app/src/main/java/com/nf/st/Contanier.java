package com.nf.st;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

//图形绘制容器
public class Contanier  extends View {
    private List<Contanier> list;
    private float x = 0, y = 0;

    public Contanier() {
        super(MainActivity.mContext);
        list = new ArrayList<>();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.save();
//		canvas.drawColor(MainActivity.resources.getColor(R.color.transparent));
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.translate(getX(), getY());
        childrenDraw(canvas);
        for (Contanier c : list) {
            c.draw(canvas);
        }
        canvas.restore();
    }

    public void childrenDraw(Canvas canvas) {

    }

    public void addChildren(Contanier child) {
        list.add(child);
    }

    public void removeChildren(Contanier child) {
        list.remove(child);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}