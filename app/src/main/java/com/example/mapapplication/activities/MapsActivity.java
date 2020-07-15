package com.example.mapapplication.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mapapplication.GetContainersPlaces;
import com.example.mapapplication.MessageDisplay;
import com.example.mapapplication.R;
import com.example.mapapplication.data.Container;
import com.example.mapapplication.data.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    private static final int REQUEST_CODE = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    private DatabaseReference myRef;
    private FirebaseDatabase myDatabase;

    private Double latitide, longitude;

    private boolean button_Located_clicked = false;
    private boolean button_navigated_clicked = false;
    private static ArrayList<Marker> _marker_list;

    private static Marker MARKER;
    private static LatLng POINT;

    private static int _marker;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final double TopPosition = 40.0; // 39.95
    private static final double BottomPosition = 39.84; // 39.89
    private static final double LeftPosition = 32.65; // 32.80
    private static final double RightPosition = 32.97; // 32.89

    private String mURL = "https://map-application-17495.firebaseio.com/containers.json";

    public static AlertDialog alertDialog;
    public static AlertDialog.Builder dialogBuilder;
    public static PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        myRef = FirebaseDatabase.getInstance().getReference();
        myDatabase = myRef.getDatabase();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("containers")) {
                    for (int i = 0; i < 1000; i++) {
                        String countainerId = randomAlphaNumeric(25); // UUID.randomUUID().toString();

                        SimpleDateFormat dfDateTime  = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        double year = randBetweenInt(2019, 2021);
                        double month = randBetweenInt(0, 11);

                        GregorianCalendar gc = new GregorianCalendar((int)year, (int)month, 1);
                        double day = randBetweenInt(1, gc.getActualMaximum(gc.DAY_OF_MONTH));
                        gc.set((int)year, (int)month, (int)day);

                        List<Double> position = new ArrayList<Double>();
                        position.add(randBetweenDouble(TopPosition, BottomPosition));
                        position.add(randBetweenDouble(LeftPosition, RightPosition));

                        Container container = new Container();

                        container.id = (int) randBetweenInt(10000, 100000);
                        container.position = position;
                        container.collection = dfDateTime.format(gc.getTime());
                        container.fullness = (int) randBetweenInt(0, 100);

                        myRef.child("containers").child(countainerId).setValue(container);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static int randBetweenInt(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    public static double randBetweenDouble(double start, double end) {
        Random random = new Random();
        double randomValue = start + (end - start) * random.nextDouble();
        return randomValue;
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
                }
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng position;

        if (currentLocation != null) {
            position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).title("Your Position"));
        } else {
            position = new LatLng(39.9286, 32.8547);
            mMap.addMarker(new MarkerOptions().position(position).title("Ankara"));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        GetContainersPlaces getContainersPlaces = new GetContainersPlaces();

        Object trasferData[] = new Object[2];
        trasferData[0] = mMap;
        trasferData[1] = mURL;

        getContainersPlaces.execute(trasferData);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (MARKER != marker && !button_Located_clicked) {
                    if (!_marker_list.isEmpty() && MARKER != _marker_list.get(0)) {
                        return false;
                    }

                    MARKER = marker;
                    MARKER.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    _marker = Integer.valueOf(marker.getTitle());


                    myRef.child("containers").orderByChild("id").equalTo(_marker).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Container container = new Container();
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                container = singleSnapshot.getValue(Container.class);
                            }
                            showMarkDetails(MapsActivity.this, _marker, container.collection.toString(), container.fullness);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                return false;
            }
        });


        _marker_list = new ArrayList<Marker>();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (button_Located_clicked) {
                    if (!_marker_list.isEmpty()) {
                        _marker_list.get(0).remove();
                        _marker_list.clear();
                    }
                    POINT = point;
                    _marker_list.add(mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));

                }
            }
        });

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                myRef.child("flag").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean flag = dataSnapshot.getValue(Boolean.class);

                        if (flag) {
                            for (int i = 0; i < GetContainersPlaces.myMarkers.size(); i++){
                                GetContainersPlaces.myMarkers.get(i).remove();
                            }
                            GetContainersPlaces getContainersPlaces = new GetContainersPlaces();

                            Object trasferData[] = new Object[2];
                            trasferData[0] = mMap;
                            trasferData[1] = mURL;

                            getContainersPlaces.execute(trasferData);

                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    myRef.child("flag").setValue(false);
                                }
                            }, 2500);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navigate_button:
                System.out.println("clicked on nabigate button");

                break;

            case R.id.relocate_button:
                for (int i = 0; i < GetContainersPlaces.myMarkers.size(); i++){
                    GetContainersPlaces.myMarkers.get(i).setVisible(false);
                }
                MARKER.setVisible(true);
                button_Located_clicked = true;
                MARKER.setAlpha(0.35f);
                alertDialog.dismiss();
                showChangeSave(view);
                break;

            case R.id.save_button:
                _marker_list.get(0).remove();
                _marker_list.clear();
                MARKER.setPosition(POINT);
                for (int i = 0; i < GetContainersPlaces.myMarkers.size(); i++){
                    GetContainersPlaces.myMarkers.get(i).setVisible(true);
                    GetContainersPlaces.myMarkers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                MARKER.setAlpha(1f);
                button_Located_clicked = false;
                popupWindow.dismiss();

                myRef.child("containers").orderByChild("id").equalTo(_marker).addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Container container = new Container();
                        String s = null;
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            container = singleSnapshot.getValue(Container.class);
                            s = singleSnapshot.getRef().getKey().toString();
                        }

                        List<Double> position = new ArrayList<Double>();
                        position.add(POINT.latitude);
                        position.add(POINT.longitude);

                        container.setPosition(position);

                        dataSnapshot.getRef().child(s).setValue(container);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                myRef.child("flag").setValue(true);

                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showMarkDetails(Context context, int _message, String __message, int ___message) {

        dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.marker_detail_layout, (ViewGroup) ((Activity)context).findViewById(R.id.root));
        TextView _text = (TextView) layout.findViewById(R.id.message_container);
        TextView __text = (TextView) layout.findViewById(R.id.collection_date);
        TextView ___text = (TextView) layout.findViewById(R.id.fullness_rate);

        _text.setText(_text.getText() + String.valueOf(_message));
        __text.setText(__message);
        ___text.setText(String.valueOf(___message + " %"));

        dialogBuilder.setView(layout);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        alertDialog.show();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                alertDialog.dismiss();
                MARKER.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                MARKER.setAlpha(1f);
            }
        });
    }

    private void showChangeSave(View view) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.marker_change_layout, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height);

        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }
}