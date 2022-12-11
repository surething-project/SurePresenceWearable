package pt.ulisboa.tecnico.surething.wearable.activities;

import androidx.appcompat.app.AppCompatActivity;
import pt.ulisboa.tecnico.surething.client.tasks.LoginTask;
import pt.ulisboa.tecnico.surething.wearable.R;
import pt.ulisboa.tecnico.surething.wearable.utils.Constants;
import pt.ulisboa.tecnico.surething.wearable.utils.SSL;
import pt.ulisboa.tecnico.surething.wearable.utils.SaveSharedPreference;
import pt.ulisboa.tecnico.surething.wearable.utils.api;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

public class LoginActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            SSL.init(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "error initiating ssl", Toast.LENGTH_LONG).show();
        }

        Button btn_register = findViewById(R.id.btn_registerHere);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button button_login = findViewById(R.id.btn_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = findViewById(R.id.editText_name_login);
                EditText password = findViewById(R.id.editText_password_login);

                /*SureThing.UserSureThingProto user = SureThing.UserSureThingProto.newBuilder()
                        .setUsername(name.getText().toString())
                        .setPassword(password.getText().toString())
                        .build();
                LoginTask task = new LoginTask(LoginActivity.this, (TextView) findViewById(R.id.textView_errorLogin), MainActivity.class);
                task.execute(user);*/
            }
        });
    }
}