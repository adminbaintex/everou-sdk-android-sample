package com.baintex.everousample.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baintex.everoulibrary.EverouManager;
import com.baintex.everoulibrary.exceptions.InitializationErrorException;
import com.baintex.everoulibrary.exceptions.InvalidAPIKeyException;
import com.baintex.everoulibrary.exceptions.ObtainingDevicesErrorException;
import com.baintex.everoulibrary.model.Device;
import com.baintex.everousample.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.baintex.everousample.widget.ExampleAppWidgetProvider.ACTION_ENABLE_DEVICE;
import static com.baintex.everousample.widget.ExampleAppWidgetProvider.EXTRA_APP_WIDGET_ID;
import static com.baintex.everousample.widget.ExampleAppWidgetProvider.EXTRA_DEVICE;

public class ExampleAppWidgetConfigure extends Activity implements AdapterView.OnItemClickListener {

    ArrayList<Device> devices;

    int appWidgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_configuration_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        new Thread(() -> {
            try {
                EverouManager everouManager = EverouManager.getInstance(getApplicationContext());
                devices = everouManager.getDevices();

                new Handler(Looper.getMainLooper()).post(() -> showDevices(devices));

            } catch (InitializationErrorException | ObtainingDevicesErrorException | InvalidAPIKeyException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), R.string.error_obtaining_devices, Toast.LENGTH_SHORT).show());
                finish();
            }
        }).start();
    }

    private void showDevices(ArrayList<Device> devices) {
        String[] items = new String[devices.size()];
        int i=0;
        for (Device device: devices) {
            items[i] = device.desc;
            i++;
        }

        final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Device device = devices.get(position);
        ExampleAppWidgetProvider.storeWidgetFromSharedPreferences(getApplicationContext(), appWidgetId, device);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.example_appwidget);
        views.setTextViewText(R.id.textViewName, device.desc);

        Intent intent = new Intent(getApplicationContext(), ExampleAppWidgetProvider.class);
        intent.setAction(ACTION_ENABLE_DEVICE);
        intent.putExtra(EXTRA_DEVICE, device);
        intent.putExtra(EXTRA_APP_WIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.button, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
