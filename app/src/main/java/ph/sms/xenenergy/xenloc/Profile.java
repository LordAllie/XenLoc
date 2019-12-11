package ph.sms.xenenergy.xenloc;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Daryll-POGI on 10/12/2019.
 */

public class Profile extends AppCompatActivity {

    ImageView ivAvatar;
    TextView tvUsername, tvEmail, tvAge;
    private DatabaseReference mDatabase;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        username=getIntent().getExtras().getString("username","");
        ivAvatar=(ImageView)findViewById(R.id.ivAvatar);
        tvAge=(TextView)findViewById(R.id.tvAge);
        tvEmail=(TextView)findViewById(R.id.tvEmail);
        tvUsername=(TextView)findViewById(R.id.tvUsername);


        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatSpaceRef = rootRef.child("data");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                System.out.println(dataSnapshot.child(username).getValue(String.class));
//                // Get Post object and use the values to update the UI
//                String  user = dataSnapshot.child(username).child("username").getValue(String.class);
//                String  age = dataSnapshot.child(username).child("age").getValue(Long.class).toString();
//                String  email = dataSnapshot.child(username).child("email").getValue(String.class);
//                tvEmail.setText(email);
//                tvAge.setText(age);
//                tvUsername.setText(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBase", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        chatSpaceRef.addValueEventListener(eventListener);

    }
}