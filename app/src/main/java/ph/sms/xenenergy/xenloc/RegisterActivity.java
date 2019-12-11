package ph.sms.xenenergy.xenloc;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;

import ph.sms.xenenergy.xenloc.firebase.Authentication;
import ph.sms.xenenergy.xenloc.model.Location;
import ph.sms.xenenergy.xenloc.model.User;

/**
 * Created by Daryll-POGI on 06/12/2019.
 */

public class RegisterActivity extends AppCompatActivity {

    private Authentication authentication;
    Button btnRegister;
    EditText etEmail,etPassword, etConfirmPassword, etUsername, etAge, etImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        authentication = new Authentication(RegisterActivity.this, RegisterActivity.this);
        etPassword =(EditText)findViewById(R.id.etPassword);
        etEmail =(EditText)findViewById(R.id.etEmail);
        etConfirmPassword =(EditText)findViewById(R.id.etConfirmPassword);
        etUsername =(EditText)findViewById(R.id.etUsername);
        etAge =(EditText)findViewById(R.id.etAge);
        etImage =(EditText)findViewById(R.id.etImage);



        btnRegister=(Button)findViewById(R.id.btnRegister);

        etImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String username = etUsername.getText().toString();
                String age = etAge.getText().toString();
                String image = etImage.getText().toString();

                if(email.equals("") || email==null || password.equals("") || password==null || username.equals("") || username==null || image.equals("") || image==null){
                    Toast.makeText(RegisterActivity.this,"Email, Username and Password are required.", Toast.LENGTH_LONG).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this,"Password does not match", Toast.LENGTH_LONG).show();
                } else {
                    User user = new User(username, email, age, password, email, new Location("", ""));
                    authentication.registerByEmail(email,password, user);
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                etImage.setText(filePath);

                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                    //FINE
                } else {
                    //NOT IN REQUIRED FORMAT
                }
            }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }
}
