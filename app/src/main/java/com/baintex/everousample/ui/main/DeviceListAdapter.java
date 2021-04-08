package com.baintex.everousample.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baintex.everoulibrary.EverouManager;
import com.baintex.everoulibrary.exceptions.FailedEnablingDeviceException;
import com.baintex.everoulibrary.exceptions.GenerateBluetoothKeyException;
import com.baintex.everoulibrary.exceptions.InitializationErrorException;
import com.baintex.everoulibrary.model.Device;
import com.baintex.everousample.R;

import java.util.ArrayList;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Device> devices;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DeviceViewHolder(inflater.inflate(R.layout.list_item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DeviceViewHolder deviceViewHolder = (DeviceViewHolder) holder;

        final Device device = devices.get(position);

        deviceViewHolder.textView.setText(device.desc);

        deviceViewHolder.button.setOnClickListener((v -> new Thread(() -> {
            try {
                EverouManager everouManager = EverouManager.getInstance(holder.itemView.getContext());
                everouManager.enableDevice(device);

            } catch (InitializationErrorException e) {
                e.printStackTrace();

            } catch (GenerateBluetoothKeyException e) {
                e.printStackTrace();

            } catch (FailedEnablingDeviceException e) {
                e.printStackTrace();

            }
        }).start()));
    }

    @Override
    public int getItemCount() {
        return (devices != null)? devices.size(): 0;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        Button button;

        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            button = itemView.findViewById(R.id.button);
        }
    }
}
