package com.example.safeu2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safeu2.database.Contact;
import com.example.safeu2.database.dbhandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class emergencySos extends AppCompatActivity {

    Button emergency, add;
    EditText enternumber;
    RecyclerView recyclerView;
    TextView notetextView;

    List<String> phnumbers = new ArrayList<>();
    ContactAdapter adapter;

    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
    String resaddress;

    private BroadcastReceiver sentStatusReceiver;
    private static final String SENT_ACTION = "SMS_SENT_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_sos);

        emergency = findViewById(R.id.emergency);
        enternumber = findViewById(R.id.enternumber);
        add = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        notetextView = findViewById(R.id.notetextView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        sentStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String num = intent.getStringExtra("number");
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SOS Message successfully sent to " + num, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, "Failed to send SMS to " + num, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sentStatusReceiver, new IntentFilter(SENT_ACTION), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(sentStatusReceiver, new IntentFilter(SENT_ACTION));
        }

        dbhandler db = new dbhandler(emergencySos.this);

        //get all contacts
        List<Contact> allContacts = db.getAllContacts();
        for(Contact contact: allContacts) {
            phnumbers.add(contact.getNumber());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(phnumbers, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                new AlertDialog.Builder(emergencySos.this)
                        .setTitle("Remove Contact")
                        .setMessage("Do you want to remove " + phnumbers.get(position) + " from the list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.deleteContactByName(phnumbers.get(position));
                                phnumbers.remove(position);
                                adapter.notifyItemRemoved(position);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }
        });
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = enternumber.getText().toString().trim();
                if (!temp.isEmpty()) {
                    Contact cnt = new Contact();
                    cnt.setNumber(temp);
                    db.addContact(cnt);
                    phnumbers.add(temp);
                    adapter.notifyItemInserted(phnumbers.size() - 1);
                    enternumber.setText("");
                } else {
                    Toast.makeText(emergencySos.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        getLastLocation();
                    } else {
                        requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 1);
                    }
                }
            }
        });
    }

    public void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                Geocoder geocoder = new Geocoder(emergencySos.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    if(addresses != null && !addresses.isEmpty()){
                                        String lat = String.valueOf(addresses.get(0).getLatitude());
                                        String lng = String.valueOf(addresses.get(0).getLongitude());
                                        resaddress = "EMERGENCY my location: https://www.google.com/maps/place/"+lat+","+lng;
                                    } else {
                                        resaddress = "EMERGENCY! Unable to determine address. Coordinates: " + location.getLatitude() + ", " + location.getLongitude();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    resaddress = "EMERGENCY! I need help. Location unavailable.";
                                    Toast.makeText(emergencySos.this, "Failed to get location, sending SMS anyway", Toast.LENGTH_SHORT).show();
                                }
                                
                                if(phnumbers.isEmpty()) {
                                    Toast.makeText(emergencySos.this, "No emergency contacts added", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                for(String numS:phnumbers){
                                    sendSMS(numS);
                                }
                            } else {
                                Toast.makeText(emergencySos.this, "Location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            askPermission();
        }
    }

    public void askPermission(){
        ActivityCompat.requestPermissions(emergencySos.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Permission required to get location", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void sendSMS(String numG){
        String msg = resaddress;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            Intent sentIntent = new Intent(SENT_ACTION);
            sentIntent.putExtra("number", numG);
            
            PendingIntent sentPendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sentPendingIntent = PendingIntent.getBroadcast(this, numG.hashCode(), sentIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                sentPendingIntent = PendingIntent.getBroadcast(this, numG.hashCode(), sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            smsManager.sendTextMessage(numG, null, msg, sentPendingIntent, null);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Failed to send SMS to " + numG, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sentStatusReceiver != null) {
            unregisterReceiver(sentStatusReceiver);
        }
    }
}