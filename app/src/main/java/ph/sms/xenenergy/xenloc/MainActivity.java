package ph.sms.xenenergy.xenloc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Environment;

import android.content.pm.PackageManager;

import android.os.Handler;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.socket.emitter.Emitter;
import ph.sms.xenenergy.xenloc.apiHandler.MyObservables;
import ph.sms.xenenergy.xenloc.apiHandler.RetClassGen;
import ph.sms.xenenergy.xenloc.apiHandler.RetrofitHandler;
import ph.sms.xenenergy.xenloc.apiHandler.ServiceGenerator;
import ph.sms.xenenergy.xenloc.firebase.Authentication;
import ph.sms.xenenergy.xenloc.firebase.InsertFireStoreData;
import ph.sms.xenenergy.xenloc.interfaces.APIHandler;
import ph.sms.xenenergy.xenloc.list.UserAndLocation;
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
    String token = "";
    static MainActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference mDatabase;

    private io.socket.client.Socket mSocket;

    {
        try {
            mSocket = io.socket.client.IO.socket("http://172.16.0.136:8084");
        } catch (URISyntaxException e) {
            Log.d("myTag", e.getMessage());
        }

    }

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference("data");
        instance = this;
        mSocket.connect();
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        username = sharedPref.getString("username", "");
        token = sharedPref.getString("token", "");
        FirebaseMessaging.getInstance().subscribeToTopic("topic");
        if (username.equals("")) {

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        if (token == null || token.equals("")) {
            mDatabase.child(username.replaceAll("[-+.^:,@]","")).child("token").setValue(refreshedToken);
            editor.putString("token", refreshedToken).apply();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        insertFireStoreData = new InsertFireStoreData(MainActivity.this);
        authentication = new Authentication(MainActivity.this, MainActivity.this);

//        getUsers();
//        updateLocation();
        //startService(new Intent(this, MyService.class));
        ServiceGenerator.changeApiBaseUrl("http://172.16.0.136:8084/XenLocation/");
        observables = new MyObservables(ServiceGenerator.createService(APIHandler.class), this);
        retrofitHandler = new RetrofitHandler(MainActivity.this);

        String jsonString = "{message: " + "'" + message + "'" + "}";

        try {
            JSONObject jsonData = new JSONObject(jsonString);
            mSocket.emit("CHAT", jsonData);
            System.out.println(mSocket.emit("CHAT", jsonData));
        } catch (JSONException e) {
            Log.d("me", "error send message " + e.getMessage());
            System.out.println("error send message " + e.getMessage());
        }
        setListening();

    }
    public String message;

    private void setListening() {
        mSocket.on("CHAT", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    message = messageJson.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("receive: " + message);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getUsers();
//
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                startActivity(new Intent(MainActivity.this,Profile.class).putExtra("username",marker.getTitle().toString()));

            }
        });
//        DownloadLocation downloadLocation = new DownloadLocation();
//        downloadLocation.execute();
    }

    public void getUsers(){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatSpaceRef = rootRef.child("data");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {


                    String image = ds.child("image").getValue(String.class);
                    final String user = ds.child("username").getValue(String.class);

                    DataSnapshot loc = ds.child("location");
                    final double locLong = Double.valueOf(String.valueOf(loc.child("sLong").getValue()));
                    final double locLat = Double.valueOf(String.valueOf(loc.child("sLat").getValue()));

                    if(image==null || image.equals("")){
                        LatLng latLng = new LatLng(locLat, locLong);

                        mMap.addMarker(new MarkerOptions().position(latLng).title(user));
                        //mMap.clear();
//                        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    }else {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference ref = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/xenloc.appspot.com/o/images%2F" + image + "?alt=media");
                        try {

                            final File localFile = File.createTempFile("Images", "bmp");
                            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap my_image;

                                    my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    System.out.println("done download image");
                                    System.out.println(my_image.getHeight());


                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(my_image);
                                    LatLng latLng = new LatLng(locLat, locLong);

                                    mMap.addMarker(new MarkerOptions().position(latLng).title(user).icon(icon));
                                    //mMap.clear();
//                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("TAG:"+databaseError.toString());
            }
        };
        chatSpaceRef.addValueEventListener(eventListener);
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
//                gpsTracker = new GPSTracker(MainActivity.this);
//                Map<String, Object> value = new HashMap<>();
//                value.put("long", a[0]);
//                value.put("lat", a[1]);
//                insertFireStoreData.save(value, username);
//                mMap.clear();
//                getUsers();
//                mMap.addMarker(new MarkerOptions().position(latLng).title(username));
//                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                mDatabase.child(username.replaceAll("[-+.^:,@]","")).child("location").setValue(new Location(a[1], a[0]));
            }
        });
    }


    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = 55;
            int height = 73;
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
//            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }


    private MyObservables observables;
    private RetrofitHandler retrofitHandler;

    private class DownloadLocation extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String status = "";
            RetClassGen retClassGen;
            retClassGen = getLocationSQL("user_location");
            if(retClassGen.getRespCode() ==200) {
                status = retClassGen.getResponseBodyList().toString();
            }
            return status;
        }

        private RetClassGen getLocationSQL(String urlPath) {
            return retrofitHandler.getLocations(urlPath);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println(s);
            try {
                String data = "{\"location\":"+s.replace("=","\":\"").replace("{","{\"").replace(", ","\", \"").replace("}","\"}").replace("}\"","}").replace("\"{","{")+"}";
                System.out.println(s);
                JSONObject jsnobject = new JSONObject(data);
                JSONArray jsonArray = jsnobject.getJSONArray("location");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject explrObject = jsonArray.getJSONObject(i);

                    String image = explrObject.get("user_marker").toString();
                    String user = explrObject.get("user_name").toString();
                    final double locLong = Double.valueOf(explrObject.get("user_long").toString());
                    final double locLat = Double.valueOf(explrObject.get("user_lat").toString());
                    BitmapDescriptor icon;
                    LatLng latLng = new LatLng(locLat, locLong);
                    if(image==null || image.equals("")){

                    }else {
                    }
                    mMap.addMarker(new MarkerOptions().position(latLng).title(user));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }


    public void goToChat(View view) {
        startActivity(new Intent(MainActivity.this, InboxActivity.class));
    }

}
