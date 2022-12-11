package pt.ulisboa.tecnico.surething.wearable.activities;

import androidx.appcompat.app.AppCompatActivity;
import eu.surething_project.core.SignedLocationClaim;
import pt.ulisboa.tecnico.surething.wearable.R;
import pt.ulisboa.tecnico.surething.wearable.utils.BleHandler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button kiosk;
    private Button profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kiosk = findViewById(R.id.btn_kiosk);
        profile = findViewById(R.id.btn_profile);

        kiosk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

    }
}