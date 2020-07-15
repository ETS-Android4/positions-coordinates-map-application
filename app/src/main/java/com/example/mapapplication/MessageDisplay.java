package com.example.mapapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.mapapplication.activities.MapsActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MessageDisplay {

    public static void showToastLong(Context context, String message){
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_layout, (ViewGroup) ((Activity)context).findViewById(R.id.root));
        TextView text = (TextView) layout.findViewById(R.id.message);

        text.setText(message);

        Toast toast = new Toast(context);

        toast.setDuration(Toast.LENGTH_LONG);

        toast.setView(layout);
        toast.show();
    }
}
