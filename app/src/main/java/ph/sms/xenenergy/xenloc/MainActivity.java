package ph.sms.xenenergy.xenloc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.sms.xenenergy.xenloc.firebase.Authentication;
import ph.sms.xenenergy.xenloc.firebase.InsertFireStoreData;
import ph.sms.xenenergy.xenloc.model.Location;

import static android.content.ContentValues.TAG;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String APP_PROPERTY_SETTING = "app_config";
    private GoogleMap mMap;
    Authentication authentication;
    GPSTracker gpsTracker;
    InsertFireStoreData insertFireStoreData;
    String username = "";
    FirebaseFirestore db;
    static MainActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference mDatabase;
    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mDatabase = FirebaseDatabase.getInstance().getReference("data");
        instance = this;
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        username = sharedPref.getString("username", "");
        if (username.equals("")) {

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        insertFireStoreData = new InsertFireStoreData(MainActivity.this);
        authentication = new Authentication(MainActivity.this, MainActivity.this);

        updateLocation();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getUsers();

    }

    public void getUsers() {
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String user = document.getId().toString();
                                double longitude = Double.parseDouble(document.getData().get("long").toString());
                                double latitude = Double.parseDouble(document.getData().get("lat").toString());

                                LatLng loc = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(loc).title(user));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void updateLocation() {
        createLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, XenLocationService.class);
        intent.setAction(XenLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }

    public void updateLocationBg (final String value){
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(value);
                String[] a = value.split("/");
                Toast.makeText(MainActivity.this, "VALUE:" + value, Toast.LENGTH_SHORT).show();
//                gpsTracker = new GPSTracker(MainActivity.this);
//                Map<String, Object> value = new HashMap<>();
//                value.put("long", a[0]);
//                value.put("lat", a[1]);
//                insertFireStoreData.save(value, username);
                mDatabase.child(username.replaceAll("[-+.^:,@]","")).child("location").setValue(new Location(a[0], a[1]));
            }
        });
    }
}
