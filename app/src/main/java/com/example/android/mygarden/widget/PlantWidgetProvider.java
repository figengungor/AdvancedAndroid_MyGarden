package com.example.android.mygarden.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.mygarden.R;
import com.example.android.mygarden.ui.MainActivity;
import com.example.android.mygarden.ui.PlantDetailActivity;

import static com.example.android.mygarden.provider.PlantContract.INVALID_PLANT_ID;
import static com.example.android.mygarden.ui.PlantDetailActivity.EXTRA_PLANT_ID;

//https://developer.android.com/guide/topics/appwidgets/index.html
//https://developer.android.com/reference/android/widget/RemoteViews.html

/**
 * Implementation of App Widget functionality.
 */
public class PlantWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int imgRes, long plantId,
                                boolean needWater, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);

        //Update image
        views.setImageViewResource(R.id.widget_plant_image, imgRes);

        //Hide water drop image if doesn't need water(less than MIN_AGE_BETWEEN_WATER)
        if (!needWater)
            views.setViewVisibility(R.id.widget_water_button, View.INVISIBLE);
        else
            views.setViewVisibility(R.id.widget_water_button, View.VISIBLE);

        //Set plant Id text
        views.setTextViewText(R.id.widget_plant_id_text_view, String.valueOf(plantId));

        //PendingIntent is just a wrap around an intent that allows other applications to have
        // access and run that intent in your application
        //Create an Intent to launch MainActivity if plantId is INVALID_PLANT_ID
        // Else create an Intent to launch PlantDetailActivity with plantId EXTRA
        Intent intent;
        if (plantId == INVALID_PLANT_ID) {
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(EXTRA_PLANT_ID, plantId);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);


        // Add the wateringservice click handler
        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        wateringIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);
        PendingIntent wateringPendingIntent = PendingIntent.getService(
                context,
                0,
                wateringIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_button, wateringPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /*onUpdate is called once a new widget is created
    * and every update interval which we set 30 mins in plant_widget_info.xml
    *
    * AppWidgetManager class gives access to information about all existing widgets
    * on the home screen. It's also your access to forcing an update on all these widgets.
    * */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the Intent service update widget action, the service takes care of updating the widgets UI
        PlantWateringService.startActionUpdatePlantWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager, int imgRes, long plantId, boolean needWater, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, imgRes, plantId, needWater, appWidgetId);
        }
    }
}

