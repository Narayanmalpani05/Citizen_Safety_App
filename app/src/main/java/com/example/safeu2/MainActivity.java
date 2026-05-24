package com.example.safeu2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String[] featurearr = {"Malicious URL Detector", "Emergency SOS", "Strong Password Generator", "Password Manager", "Safety Guide"};
    int[] featureimages = {R.drawable.malurldet, R.drawable.emergency, R.drawable.strpassgen, R.drawable.passman, R.drawable.knowsupport};

    RecyclerView recyclerView;

    /*---------------for permissions---------------------*/
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 225;
    /*------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*---------------for permissions---------------------*/
        // Check if the user has granted the permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            // Request the permissions
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, 
                Manifest.permission.READ_SMS, 
                Manifest.permission.SEND_SMS}, REQUEST_CODE);
        }
        /*------------------------------------------------------------------*/

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        FeatureAdapter adapter = new FeatureAdapter(featurearr, featureimages, new FeatureAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, malUrlDet.class);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, emergencySos.class);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, strongPassGen.class);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, VaultPinActivity.class);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, KnowledgeSupport.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The user has granted the permission.
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                // The user has denied the permission.
                Log.d(TAG, "Permission denied.");
            }
        }
    }
}