package pt.ulisboa.tecnico.surething.wearable.activities;

import androidx.appcompat.app.AppCompatActivity;
import pt.ulisboa.tecnico.surething.wearable.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

public class VerifiedActivity extends WearableActivity {

    Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified);

        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset BLE
                Intent intent = new Intent(VerifiedActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}