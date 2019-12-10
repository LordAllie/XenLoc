package ph.sms.xenenergy.xenloc.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ph.sms.xenenergy.xenloc.MainActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by Daryll-POGI on 05/12/2019.
 */

public class InsertFireStoreData implements OnMapReadyCallback {
    Context context;
    private GoogleMap mMap;
    FirebaseFirestore db;
    List<String> list;
    DocumentSnapshot documentSnapshot=null;

    public InsertFireStoreData(Context context){
        db = FirebaseFirestore.getInstance();
    }

    public void save(Map<String, Object> value, String doc){

        db.collection("users").document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error deleting document", e);
                    }
                });

        db.collection("users").document(doc)
                .set(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FireBase", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FireBase", "Error writing document", e);
                    }
                });
    }

    public List<String> getUsers(){
        list = new ArrayList<>();
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
                                mMap.addMarker(new MarkerOptions().position(loc).title(""));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return list;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getUsers();
    }
}
