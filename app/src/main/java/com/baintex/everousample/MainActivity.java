package com.baintex.everousample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baintex.everousample.ui.main.MainFragment;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            menu.add(R.string.auto_mode);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(getText(R.string.auto_mode))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(
                    getString(R.string.auto_mode_status,
                            ((EverouSampleApp.scannerStatus)?
                                    getString(R.string.status_enabled)
                                    :
                                    getString(R.string.status_disabled))));

            builder.setPositiveButton(R.string.enable, (dialog, which) -> enableAutoMode());

            builder.setNegativeButton(R.string.disable, ((dialog, which) -> disableAutoMode()));

            builder.setNeutralButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()));

            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private final int REQUEST_LOCATION_PERMISSIONS = 255;

    private void enableAutoMode() {
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Fragment fragment = getSupportFragmentManager().getFragments().get(0);
            if (fragment instanceof MainFragment)
                ((MainFragment) fragment).enableAutoMode();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        },
                        REQUEST_LOCATION_PERMISSIONS);
            } else {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        },
                        REQUEST_LOCATION_PERMISSIONS);
            }
        }
    }

    private void disableAutoMode() {
        Fragment fragment = getSupportFragmentManager().getFragments().get(0);
        if (fragment instanceof MainFragment)
            ((MainFragment) fragment).disableAutoMode();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Fragment fragment = getSupportFragmentManager().getFragments().get(0);
                if (fragment instanceof MainFragment)
                    ((MainFragment) fragment).enableAutoMode();
            }
        }
    }
}