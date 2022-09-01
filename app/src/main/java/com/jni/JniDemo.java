package com.jni;

public class JniDemo {
    static {
        System.loadLibrary("JniDemo");
    }

    public static native String test(int x, int y);

    public JniDemo() {
        int fd = initEvent();
        System.out.println("fd =" + fd);
    }

    private static native float getW();

    private static native float getH();

    private static native int initEvent();

    private static native float getXX();

    private static native float getYY();

    public final float getX() {
        return getXX();
    }

    public final float getY() {
        return getYY();
    }

    public final float getWidth() {
        return getH();
    }

    public final float getHight() {
        return getW();
    }
}
