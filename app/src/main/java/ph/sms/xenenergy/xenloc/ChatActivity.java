package ph.sms.xenenergy.xenloc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.sms.xenenergy.xenloc.adapter.InboxAdapter;
import ph.sms.xenenergy.xenloc.adapter.MessageAdapter;
import ph.sms.xenenergy.xenloc.model.ChatRecord;
import ph.sms.xenenergy.xenloc.model.Location;

public class ChatActivity extends AppCompatActivity {
    private EditText editText;
    private SharedPreferences sharedPref;
    public static final String APP_PROPERTY_SETTING = "app_config";
    DatabaseReference ownInbox, otherInbox, send;
    String username, emailTo, token;
    ListView listView;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listView = findViewById(R.id.messages_view);
        editText = findViewById(R.id.etMessageText);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        emailTo = getIntent().getStringExtra("Email");
        username = sharedPref.getString("username", "");
        token = getIntent().getStringExtra("Token");
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        send = rootRef.child("data").child(username.replaceAll("[-+.^:,@]","")).child("chatRecords");
        ownInbox = rootRef.child("data").child(username.replaceAll("[-+.^:,@]","")).child("chatRecords").child(emailTo.replaceAll("[-+.^:,@]",""));
        otherInbox = rootRef.child("data").child(emailTo.replaceAll("[-+.^:,@]","")).child("chatRecords");


        ownInbox.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ChatRecord> messageList = new ArrayList<>();
                List<Boolean> booleans = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ChatRecord chatRecord = postSnapshot.getValue(ChatRecord.class);
                    messageList.add(chatRecord);
                    booleans.add(chatRecord.getEmail().equals(username));

                    MessageAdapter adapter = new MessageAdapter(messageList, ChatActivity.this, booleans);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(listView.getCount() - 1);
//                    for (DataSnapshot first: postSnapshot.getChildren()) {
//
//                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

    }


    public void sendMessage(View view) {
        String message = editText.getText().toString();

        if (message.length() > 0) {
            send.child(emailTo.replaceAll("[-+.^:,@]","")).push().setValue(new ChatRecord(message, 0, username));
            otherInbox.child(username.replaceAll("[-+.^:,@]","")).push().setValue(new ChatRecord(message, 0, username));
            sendNotification(token);
            Toast.makeText(ChatActivity.this, "TOKEN: " + token, Toast.LENGTH_SHORT).show();
            editText.getText().clear();
        }
    }

    private void sendNotification(final String token) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body","Hi this is sent from device to device");
                    dataJson.put("title","dummy title");
                    json.put("notification",dataJson);
                    json.put("to", token);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+ "AIzaSyCCuvNnd-BABSXhhIe61Tpi35BeQROU25M")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch (Exception e){
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();

    }
}
