package com.mahao.banner.util;

import android.content.Context;
import android.os.Handler;

/**
 * Created by sunkun on 2016/11/24.
 */
public class AppContext {
    private static Context sContext;
    private static Handler sHandler;

    public static Context getAppContext() {
        return sContext;
    }

    public static void setContext(Context sContext) {
        AppContext.sContext = sContext;
    }

    public static Handler getMainHandler() {
        return sHandler;
    }

    static void setHandler(Handler sHandler) {
        AppContext.sHandler = sHandler;
    }
}
