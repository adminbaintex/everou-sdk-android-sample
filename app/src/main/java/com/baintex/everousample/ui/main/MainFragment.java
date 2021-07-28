package com.baintex.everousample.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baintex.everoulibrary.exceptions.InitializationErrorException;
import com.baintex.everoulibrary.exceptions.InvalidAPIKeyException;
import com.baintex.everoulibrary.exceptions.ObtainingDevicesErrorException;
import com.baintex.everoulibrary.model.Device;
import com.baintex.everousample.Constants;
import com.baintex.everousample.R;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DeviceListAdapter mAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DeviceListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        new Thread(() -> {
            try {
                mViewModel.initEverouLibrary(getContext(), Constants.API_KEY);

                try {
                    ArrayList<Device> devices = mViewModel.getUserDevices();
                    new Handler(Looper.getMainLooper()).post(() -> mAdapter.setDevices(devices));

                } catch (ObtainingDevicesErrorException e) {
                    e.printStackTrace();
                }


            } catch (InvalidAPIKeyException e) {
                e.printStackTrace();
            } catch (InitializationErrorException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void enableAutoMode() {
        new Thread(() -> mViewModel.startAutoMode(getContext())).start();
    }

    public void disableAutoMode() {
        new Thread(() -> mViewModel.stopAutoMode()).start();
    }
}