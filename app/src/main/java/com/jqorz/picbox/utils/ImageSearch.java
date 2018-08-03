package com.jqorz.picbox.utils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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
public class ImageSearch {

    public static Observable<File> listImageFiles(final File f) {

        if (!f.exists()) {
            return Observable.empty();
        }


        if (f.isDirectory()) {
            return Observable.fromArray(f.listFiles()).flatMap(new Function<File, ObservableSource<File>>() {
                @Override
                public ObservableSource<File> apply(File file) throws Exception {
                    //如果是文件夹就递归
                    return listImageFiles(file);
                }
            });
        } else {
            //filter操作符过滤图片文件,是图片文件就通知观察者
            return Observable.just(f).filter(new Predicate<File>() {
                @Override
                public boolean test(File file) throws Exception {
                    return isImage(file);
                }
            });
        }
    }

    private static boolean isImage(File file) {
        String name = file.getName();
        String lowerCase = name.toLowerCase();

        return lowerCase.length() > 4
                && (lowerCase.endsWith(".png") || lowerCase.endsWith(".jpg")||lowerCase.endsWith(".jpeg"));
    }
}
