package com.jqorz.picbox.ninepointlock;

/**
 * @author jqorz
 * @since 2018/8/21
 */
public class NinePoint {
    //默认状态
    public static int STATUS_DEFAULT = 0;
    //错误状态
    public static int STATUS_ERROR = 1;
    //选中状态
    public static int STATUS_PRESSED = 2;
    //x轴位置
    public float x;
    //y轴位置
    public float y;
    //状态
    public int status;
    //密码
    public int password;

    public NinePoint(float x, float y, int password) {
        this.x = x;
        this.y = y;
        this.password = password;
    }

    /**
     * 判断两个点之间的距离是否小于r
     */
    public static boolean checkPointDistance(float pointX, float pointY, float mouseX, float mouseY, float r) {
        return Math.sqrt((pointX - mouseX) * (pointX - mouseX) + (pointY - mouseY) * (pointY - mouseY)) < r;
    }

    public static double getDistance(NinePoint a, NinePoint b) {
        return getDistance(a.x, a.y, b.x, b.y);
    }

    public static double getDistance(float aX, float aY, float bX, float bY) {
        return Math.sqrt((aX - bX) * (aX - bX) + (aY - bY) * (aY - bY));
    }

    public double getDistance(float x, float y) {
        return getDistance(this.x, this.y, x, y);
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPassword() {
        return password;
    }
}