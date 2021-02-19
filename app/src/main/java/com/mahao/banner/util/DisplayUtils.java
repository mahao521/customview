package com.mahao.banner.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * utils for screen display
 */
public class DisplayUtils {

    private static float mCachedDensity = -1f;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue 虚拟像素
     * @return 像素
     */
    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * AppContext.getAppContext().getResources().getDisplayMetrics().density);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param pxValue 像素
     * @return 虚拟像素
     */
    public static float px2dp(int pxValue) {
        return (pxValue / AppContext.getAppContext().getResources().getDisplayMetrics().density);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue 虚拟像素
     * @return 像素
     */
    public static int dip2px(float dpValue) {
        return (int) (0.5f + dpValue * AppContext.getAppContext().getResources().getDisplayMetrics().density);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param pxValue 像素
     * @return 虚拟像素
     */
    public static float px2dip(int pxValue) {
        return (pxValue / AppContext.getAppContext().getResources().getDisplayMetrics().density);
    }

    public static int getPixel(Context context, float dp) {
        float density = getDensity(context);
        return (int) (dp * density);
    }

    public static float getDensity(Context context) {
        if (mCachedDensity > 0)
            return mCachedDensity;
        DisplayMetrics metrics = getMetric(context);
        if (metrics != null)
            mCachedDensity = metrics.density;
        return mCachedDensity < 0 ? 1.5f : mCachedDensity;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = getMetric(context);
        if (metrics != null)
            return metrics.heightPixels;
        else
            return 801;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = getMetric(context);
        if (metrics != null)
            return metrics.widthPixels;
        else
            return 481;
    }

    public static int getScreenResolution(Context context) {
        DisplayMetrics metrics = getMetric(context);
        if (metrics != null)
            return metrics.widthPixels * metrics.heightPixels;
        else
            return 480 * 800;
    }

    public static Point getScreenDimension(Context context) {
        DisplayMetrics metrics = getMetric(context);
        if (metrics != null)
            return new Point(metrics.widthPixels, metrics.heightPixels);
        return new Point(480, 800);
    }

    /**
     * x = screen width; y = screen height; in dp.
     */
    public static Point getScreenDimensionInDp(Context context) {
        DisplayMetrics metrics = getMetric(context);
        if (metrics != null) {
            float density = metrics.density == 0 ? 1 : metrics.density;
            return new Point((int) (metrics.widthPixels / density),
                    (int) (metrics.heightPixels / density));
        }
        return new Point(320, 534); // 480X800 with hdpi
    }

    public static DisplayMetrics getMetric(Context context) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager winMgr = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            winMgr.getDefaultDisplay().getMetrics(metrics);
            return metrics;
        } catch (Exception e) {
        }
        return null;
    }

    public static int measureViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();
        return height;
    }

    public static int measureViewWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int width = view.getMeasuredWidth();
        return width;
    }

    /**
     * 获取状态栏的高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBartHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int dimensionPixelSize = activity.getResources().getDimensionPixelSize(resourceId);
        return dimensionPixelSize;
    }

    /**
     * 获取NavigationBar的高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        if (!isNavigationBarShow(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 判断NavigationBar是否显示
     *
     * @param context
     * @return
     */
    public static boolean isNavigationBarShow(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager winMgr = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = winMgr.getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }
}
