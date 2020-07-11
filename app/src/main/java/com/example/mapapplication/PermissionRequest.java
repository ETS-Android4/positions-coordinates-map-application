package com.example.mapapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class PermissionRequest {

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void checkNetworkConnected(Context context) {
        if (!isNetworkConnected(context)){
            showToastNoConnection(context);
        }
    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private static void showToastNoConnection(Context context) {
        Toast.makeText(context.getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
    }
}
