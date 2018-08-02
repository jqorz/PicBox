package com.jqorz.picbox.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * <pre>
 *     copyright: datedu
 *     author : br2ant3
 *     e-mail : xxx@xx
 *     time   : 2018/07/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ImageModel {
    private String path;
    private int num;
    private int groupNum;
    private String date;//2017-06-25
    private String time;//18:00

    public ImageModel(long key, String path) {
        this.path = path;
        SimpleDateFormat formatter = new
                SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        date = formatter.format(key);
        SimpleDateFormat formatter2 = new
                SimpleDateFormat("hh:mm", Locale.CHINA);
        time = formatter2.format(key);

    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "num=" + num +
                ", groupNum=" + groupNum +
                ", path='" + path + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getPath() {
        return path;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }
}
