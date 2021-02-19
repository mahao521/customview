package com.mahao.banner;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mahao.banner.util.DisplayUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    public static final String url = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=186551341,670129749&fm=26&gp=0.jpg";
    private static final String TAG = "NewAppWidget";

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
    //    views.setImageViewResource(R.id.iv_round,R.drawable.money);

        int radius = DisplayUtils.dip2px(35);
  /*      Glide.with(context).asBitmap()
                .load(url)
                .transform(new CenterCrop(),new GranularRoundedCorners(radius,radius,radius,radius))
                .override(DisplayUtils.dip2px(70),DisplayUtils.dip2px(70))
                .into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {


                Log.d(TAG, "onResourceReady: " + resource.getWidth()+" "+ resource.getWidth());
               *//* Bitmap newBitmap = Bitmap.createBitmap(resource);
                Canvas canvas = new Canvas(newBitmap);
                Paint paint = new Paint();
                canvas.drawRoundRect(new RectF(0,0,resource.getWidth(),resource.getHeight()),10,10,paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(Bitmap,re);
                paint.setXfermode(null);*//*

                views.setImageViewBitmap(R.id.iv_round,resource);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });*/
        Glide.with(context).asBitmap()
                .load(R.drawable.money)
                .transform(new GranularRoundedCorners(radius,radius,radius,radius))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        views.setImageViewBitmap(R.id.iv_round,resource);
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                });

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

