package ph.sms.xenenergy.xenloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
        username=sharedPref.getString("user","");
        if(username.equals("")){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            return;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        insertFireStoreData=new InsertFireStoreData(MainActivity.this);
        authentication=new Authentication(MainActivity.this,MainActivity.this);

        gpsTracker=new GPSTracker(MainActivity.this);
        Map<String, Object> value = new HashMap<>();
        value.put("sLong",gpsTracker.getLongitude());
        value.put("sLat",gpsTracker.getLatitude());
        insertFireStoreData.save(value,username);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getUsers();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                startActivity(new Intent(MainActivity.this,Profile.class).putExtra("username",marker.getTitle().toString()));

            }
        });

    }

    public void getUsers(){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatSpaceRef = rootRef.child("data");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {


                    String image = ds.child("image").getValue(String.class);
                    final String user = ds.child("username").getValue(String.class);

                    DataSnapshot loc = ds.child("location");
                    final double locLong = loc.child("sLong").getValue(Double.class);
                    final double locLat = loc.child("sLat").getValue(Double.class);
                    if(image.equals("") || image==null){
                        LatLng latLng = new LatLng(locLat, locLong);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(user));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
        chatSpaceRef.addListenerForSingleValueEvent(eventListener);
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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

}
