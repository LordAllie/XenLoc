package ph.sms.xenenergy.xenloc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ph.sms.xenenergy.xenloc.firebase.Authentication;
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
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String username = etUsername.getText().toString();
                String age = etAge.getText().toString();
                String image = etImage.getText().toString();

                if(email.equals("") || email==null || password.equals("") || password==null){
                    Toast.makeText(RegisterActivity.this,"email and password are required.", Toast.LENGTH_LONG).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this,"Password does not match", Toast.LENGTH_LONG).show();
                } else {
                    User user = new User(username, email, age, password, image);
                    authentication.registerByEmail(email,password, user);
                }
            }
        });
    }
}
