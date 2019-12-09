package ph.sms.xenenergy.xenloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.sms.xenenergy.xenloc.firebase.Authentication;
import ph.sms.xenenergy.xenloc.firebase.InsertFireStoreData;

import static android.content.ContentValues.TAG;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String APP_PROPERTY_SETTING = "app_config";
    private GoogleMap mMap;
    Authentication authentication;
    GPSTracker gpsTracker;
    InsertFireStoreData insertFireStoreData;
    String username="";
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        username=sharedPref.getString("username","");
        if(username.equals("")){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            return;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        insertFireStoreData=new InsertFireStoreData(MainActivity.this);
//        authentication=new Authentication(MainActivity.this,MainActivity.this);
//
//        gpsTracker=new GPSTracker(MainActivity.this);
//        Map<String, Object> value = new HashMap<>();
//        value.put("long",gpsTracker.getLongitude());
//        value.put("lat",gpsTracker.getLatitude());
//        insertFireStoreData.save(value,username);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        getUsers();

    }

    public void getUsers(){
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

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
