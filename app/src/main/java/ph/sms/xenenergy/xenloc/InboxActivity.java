package ph.sms.xenenergy.xenloc;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.sms.xenenergy.xenloc.adapter.InboxAdapter;
import ph.sms.xenenergy.xenloc.model.ChatRecord;

public class InboxActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    public static final String APP_PROPERTY_SETTING = "app_config";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        final RecyclerView recyclerView = findViewById(R.id.inboxList);
        final String username = sharedPref.getString("username", "");

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatSpaceRef = rootRef.child("data").child(username.replaceAll("[-+.^:,@]","")).child("chatRecords");


        final List<ChatRecord> inboxList = new ArrayList<>();
        chatSpaceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                inboxList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String k1 = postSnapshot.getKey();
                    System.out.println(k1);
                    for (DataSnapshot first: postSnapshot.getChildren()) {

                            ChatRecord inbox = first.getValue(ChatRecord.class);
                            if (!inbox.getEmail().equals(username)) {
                                inboxList.add(inbox);
                            }
                            InboxAdapter adapter = new InboxAdapter(InboxActivity.this, inboxList);
                            recyclerView.setAdapter(adapter);


                    }



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });



    }



}
