package com.nf.st;

/**
 * 宏、脚本
 */
public class H {
    private String name;
    private boolean status;
    private String data;
    private Integer x;
    private Integer y;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "H{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", data='" + data + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
