package ph.sms.xenenergy.xenloc.firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import ph.sms.xenenergy.xenloc.MainActivity;
import ph.sms.xenenergy.xenloc.model.User;

/**
 * Created by Daryll-POGI on 05/12/2019.
 */

public class Authentication {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String APP_PROPERTY_SETTING = "app_config";
    private FirebaseAuth mAuth;
    private Context context;
    private Activity activity;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;

    public Authentication(Context context, Activity activity){
        this.activity=activity;
        this.context=context;
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        sharedPref = context.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        mDatabase = FirebaseDatabase.getInstance().getReference("data");
    }

    public void registerByEmail(final String email, final String password, final User user){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FireBase", "createUserWithEmail:success");

                            mDatabase.child(email.replaceAll("[-+.^:,@]","")).setValue(user);
                            try {
                                uploadImage(user.getImage(), user.getEmail());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            FirebaseUser user = mAuth.getCurrentUser();
                            editor.putString("usern", user.getEmail());
                            editor.commit();
                            activity.startActivity(new Intent(context,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Registration failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loginByEmail(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FireBase", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            editor.putString("user", user.getEmail());
                            editor.commit();
                            activity.startActivity(new Intent(context,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FireBase", "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadImage(String filePath, String email) throws FileNotFoundException {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ email);
            InputStream stream = new FileInputStream(filePath);
            UploadTask uploadTask = ref.putStream(stream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}
