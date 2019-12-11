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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class InsertFireStoreData {
    Context context;
    private DatabaseReference mDatabase;

    public InsertFireStoreData(Context context){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void save(Map<String, Object> value, String doc){
        mDatabase.child("data").child("Allie").child("location").setValue(value);
    }

}
