package com.devtech.pushnotificationdemo.view;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_PHONE_STATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.devtech.pushnotificationdemo.DeviceUuidFactory;
import com.devtech.pushnotificationdemo.FirebaseService;
import com.devtech.pushnotificationdemo.R;
import com.devtech.pushnotificationdemo.RegistrationIntentService;
import com.devtech.pushnotificationdemo.databinding.ActivityMainBinding;
import com.devtech.pushnotificationdemo.viewmodel.TokenViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private TokenViewModel model;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private static final int PERMISSION_REQUEST_CODE = 1307;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainActivity = this;
        registerWithNotificationHubs();
        FirebaseService.createChannelAndHandleNotifications(getApplicationContext());
        initViewModel();
        model.getUserLiveData().observe(this, userModel -> binding.tvIMEI.setText(userModel.getData().getEmail()));
        if (!checkPermissions()) {
            requestPermissions();
        }
        binding.btnToken.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Timber.tag(TAG).w(task.getException(), "Fetching FCM registration token failed");
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                Timber.tag(TAG).d("Token: %s", token);
                binding.tvToken.setText(token);
                binding.tvDeviceID.setText(DeviceUuidFactory.getDeviceUuid(this));
                binding.tvIMEI.setText(DeviceUuidFactory.getDeviceId(this));
            });
            Timber.e("UUID : %s", DeviceUuidFactory.getDeviceUuid(this));
            Timber.e("UUID : %s", DeviceUuidFactory.getDeviceId(this));
        });
    }

    private boolean checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            int result = ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS);
            int result1 = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        } else {
            int result = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{POST_NOTIFICATIONS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (!checkPermissions()) requestPermissions();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Alert");
                    builder.setMessage("Kindly provide the requested permissions to access all the features of the Application.");
                    builder.setPositiveButton("Provide Permission", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 101);
                    });
                    builder.setNegativeButton("Exit", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    });
                    builder.show();
                }
            }else{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!checkPermissions()) requestPermissions();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Alert");
                    builder.setMessage("Kindly provide the requested permissions to access all the features of the Application.");
                    builder.setPositiveButton("Provide Permission", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 101);
                    });
                    builder.setNegativeButton("Exit", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    });
                    builder.show();
                }
            }
            // If request is cancelled, the result arrays are empty.

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                TextView helloText = (TextView) findViewById(R.id.tvIMEI);
                helloText.setText(notificationMessage);
            }
        });
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog box that enables  users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Timber.i("This device is not supported by Google Play Services.");
                ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }
    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void initViewModel() {
        model = new ViewModelProvider(this).get(TokenViewModel.class);
        model.getUserData();
    }
}