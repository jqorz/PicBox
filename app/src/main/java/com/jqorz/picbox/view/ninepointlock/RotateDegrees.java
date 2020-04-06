package com.jqorz.picbox.view.ninepointlock;

/**
 * @author jqorz
 * @since 2018/8/21
 */
public class RotateDegrees {
    public static float getDegrees(NinePoint a, NinePoint b) {
        return getDegrees(a.x, a.y, b.x, b.y);
    }

    public static float getDegrees(NinePoint a, float bX, float bY) {
        return getDegrees(a.x, a.y, bX, bY);
    }

    public static float getDegrees(float aX, float aY, float bX, float bY) {
        float degrees;
        if (aX == bX) {
            if (aY < bY) {
                degrees = 90;
            } else {
                degrees = 270;
            }
        } else if (bY == aY) {
            if (aX < bX) {
                degrees = 0;
            } else {
                degrees = 180;
            }

        } else {
            if (aX > bX) {
                if (aY > bY) {
                    degrees = 180 + (float) (Math.atan2(aY - bY, aX - bX) * 180 / Math.PI);
                } else {
                    degrees = 180 - (float) (Math.atan2(bY - aY, aX - bX) * 180 / Math.PI);
                }

            } else {
                if (aY > bY) {
                    degrees = 360 - (float) (Math.atan2(aY - bY, bX - aX) * 180 / Math.PI);
                } else {
                    degrees = (float) (Math.atan2(bY - aY, bX - aX) * 180 / Math.PI);
                }
            }
        }
        return degrees;
    }
}
