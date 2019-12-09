package ph.sms.xenenergy.xenloc.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ph.sms.xenenergy.xenloc.MainActivity;

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

    public Authentication(Context context, Activity activity){
        this.activity=activity;
        this.context=context;
        mAuth = FirebaseAuth.getInstance();
        sharedPref = context.getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void registerByEmail(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FireBase", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            editor.putString("username", user.getEmail());
                            editor.commit();
                            activity.startActivity(new Intent(context,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "registration failed.",
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
                            editor.putString("username", user.getEmail());
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


}
