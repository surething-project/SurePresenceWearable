package pt.ulisboa.tecnico.surething.wearable.activities;

import androidx.appcompat.app.AppCompatActivity;
import pt.ulisboa.tecnico.surething.client.tasks.RegisterTask;
import pt.ulisboa.tecnico.surething.wearable.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class RegisterActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = findViewById(R.id.editText_name_register);
                EditText password = findViewById(R.id.editText_password_register);

                /*SureThing.UserSureThingProto user = SureThing.UserSureThingProto.newBuilder()
                        .setUsername(name.getText().toString())
                        .setPassword(password.getText().toString())
                        .build();

                RegisterTask task = new RegisterTask(RegisterActivity.this, (TextView) findViewById(R.id.textView_errorRegister), MainActivity.class);
                task.execute(user);*/

            }
        });
    }

}