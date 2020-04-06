package com.jqorz.aydassistant.util;

import android.util.Log;

import com.jqorz.picbox.MyApplication;


/**
 * Log管理的工具类
 */
public class Logg {


    private static int logLevel = Log.VERBOSE;
    private volatile static Logg mInstance;
    private static String tag = "Logg";

    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();


        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }

            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }

            if (st.getClassName().equals(getInstance().getClass().getName())) {
                continue;
            }

            return "[" + Thread.currentThread().getName() + "(" + Thread.currentThread().getId() + "): " + st.getFileName() + ":" + st.getLineNumber() + "]";
        }

        return null;
    }

    /**
     * 单例模式的使用
     */
    private static Logg getInstance() {
        if (mInstance == null) {
            synchronized (Logg.class) {
                if (mInstance == null) {
                    mInstance = new Logg();
                }
            }
        }
        return mInstance;
    }


    public static boolean isDebug() {
        return MyApplication.isDebug();
    }

    public static void d(Object str) {
        if (isDebug() && str != null) {
            getInstance().debug(tag, str);
        }
    }

    public static void d(String tag, Object str) {
        if (isDebug() && str != null) {
            getInstance().debug(tag, str);
        }
    }

    public static void i(Object str) {
        if (isDebug() && str != null) {
            getInstance().info(tag, str);
        }
    }

    public static void i(String tag, Object str) {
        if (isDebug() && str != null) {
            getInstance().info(tag, str);
        }
    }

    public static void v(Object str) {
        if (isDebug() && str != null) {
            getInstance().verbose(tag, str);
        }
    }

    public static void v(String tag, Object str) {
        if (isDebug() && str != null) {
            getInstance().verbose(tag, str);
        }
    }

    public static void w(Object str) {
        if (isDebug() && str != null) {
            getInstance().warn(tag, str);
        }
    }

    public static void w(String tag, Object str) {
        if (isDebug() && str != null) {
            getInstance().warn(tag, str);
        }
    }

    public static void e(Object str) {
        if (isDebug() && str != null) {
            getInstance().error(tag, str);
        }
    }

    public static void e(String tag, Object str) {
        if (isDebug() && str != null) {
            if (str instanceof Exception){
                getInstance().error(tag, (Exception)str);
            }
        }
    }

    public static void e(Exception ex) {
        if (isDebug() && ex != null) {
            getInstance().error(tag, ex);
        }
    }

    private void debug(String tag, Object str) {
        if (logLevel <= Log.DEBUG) {
            String name = getFunctionName();
            String ls = (name == null ? str.toString() : (name + " - " + str));
            Log.d(tag, ls);
        }
    }

    private void info(String tag, Object str) {
        if (logLevel <= Log.INFO) {
            String name = getFunctionName();
            String ls = (name == null ? str.toString() : (name + " - " + str));
            Log.i(tag, ls);
        }
    }

    private void verbose(String tag, Object str) {
        if (logLevel <= Log.VERBOSE) {
            String name = getFunctionName();
            String ls = (name == null ? str.toString() : (name + " - " + str));
            Log.v(tag, ls);
        }
    }

    private void warn(String tag, Object str) {
        if (logLevel <= Log.WARN) {
            String name = getFunctionName();
            String ls = (name == null ? str.toString() : (name + " - " + str));
            Log.w(tag, ls);
        }
    }

    private void error(String tag, Object str) {
        if (logLevel <= Log.ERROR) {
            String name = getFunctionName();
            String ls = (name == null ? str.toString() : (name + " - " + str));
            Log.e(tag, ls);
        }
    }

    private void error(String tag, Exception ex) {
        if (logLevel <= Log.ERROR) {
            StringBuilder sb = new StringBuilder();
            String name = getFunctionName();
            StackTraceElement[] sts = ex.getStackTrace();

            if (name != null) {
                sb.append(name).append(" - ").append(ex).append("\r\n");
            } else {
                sb.append(ex).append("\r\n");
            }

            if (sts != null && sts.length > 0) {
                for (StackTraceElement st : sts) {
                    if (st != null) {
                        sb.append("[ ").append(st.getFileName()).append(":").append(st.getLineNumber()).append(" ]\r\n");
                    }
                }
            }

            Log.e(tag, sb.toString());
        }
    }


}
