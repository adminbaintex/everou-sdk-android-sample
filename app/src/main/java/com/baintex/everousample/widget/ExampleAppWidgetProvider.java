package com.baintex.everousample.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.baintex.everoulibrary.EverouManager;
import com.baintex.everoulibrary.exceptions.FailedEnablingDeviceException;
import com.baintex.everoulibrary.exceptions.GenerateBluetoothKeyException;
import com.baintex.everoulibrary.exceptions.InitializationErrorException;
import com.baintex.everoulibrary.model.Device;
import com.baintex.everousample.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ExampleAppWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_ENABLE_DEVICE = "com.baintex.everousample.widget.ENABLE_DEVICE";

    public static final String EXTRA_DEVICE = "device";

    public static final String EXTRA_APP_WIDGET_ID = "app_widget_id";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Device device = getWidgetFromSharedPreferences(context, appWidgetId);
            if (device == null)
                continue;

            Intent intent = new Intent(context, this.getClass());
            intent.setAction(ACTION_ENABLE_DEVICE);
            intent.putExtra(EXTRA_DEVICE, device);
            intent.putExtra(EXTRA_APP_WIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
            views.setTextViewText(R.id.textViewName, device.desc);
            views.setOnClickPendingIntent(R.id.button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId: appWidgetIds) {
            removeWidgetFromSharedPreferences(context, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_ENABLE_DEVICE.equals(intent.getAction())) {
            Device device = intent.getParcelableExtra(EXTRA_DEVICE);
            if (device != null)
                new Thread(() -> sendDeviceAction(context, device)).start();
            return;
        }

        super.onReceive(context, intent);
    }

    private void sendDeviceAction(Context context, Device device) {
        try {
            EverouManager everouManager = EverouManager.getInstance(context);
            everouManager.enableDevice(device);

        } catch (InitializationErrorException | GenerateBluetoothKeyException | FailedEnablingDeviceException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, R.string.error_sending_device_action, Toast.LENGTH_SHORT).show());
        }
    }

    // region SHARED-PREFERENCES
    private static final String PREFS_NAME = "shared_preferences";

    private static final String PREF_PREFIX_KEY = "device_uid";

    private static Device getWidgetFromSharedPreferences(Context context, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        String deviceJSON = preferences.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (deviceJSON != null) {
            try {
                Device device = new Device();
                device.parse(new JSONObject(deviceJSON));
                return device;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void removeWidgetFromSharedPreferences(Context context, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREF_PREFIX_KEY + appWidgetId);
        editor.apply();
    }

    public static void storeWidgetFromSharedPreferences(Context context, int appWidgetId, Device device) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_PREFIX_KEY + appWidgetId, device.toJSONObject().toString());
        editor.apply();
    }
    // endregion
}

