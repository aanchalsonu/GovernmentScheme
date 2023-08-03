package com.example.governmentscheme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView agriculture, bank, business, education, health, house, logout,subscribeButton;
    public static boolean isAdmin = false;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_id";
    private static final CharSequence CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESCRIPTION = "My Channel Description";
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken();
        agriculture = findViewById(R.id.agro);
        bank = findViewById(R.id.bank);
        business = findViewById(R.id.business);
        education = findViewById(R.id.education);
        health = findViewById(R.id.health);
        house = findViewById(R.id.house);
        logout = findViewById(R.id.logoutimage);
        logout.setOnClickListener(this);
        agriculture.setOnClickListener(this);
        bank.setOnClickListener(this);
        business.setOnClickListener(this);
        education.setOnClickListener(this);
        health.setOnClickListener(this);
        house.setOnClickListener(this);
        subscribeButton = findViewById(R.id.subscribeButton);

        // Create notification channel
        createNotificationChannel();

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNotificationPermission();
            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
        } else {
            showNotification();
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Subscription Complete")
                .setContentText("Stay updated to the new scheme's")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification();
            }
        }
    }

    @Override
    public void onClick(View view) {
        String enteredUsername = getIntent().getStringExtra("username");
        if (view.equals(agriculture)) {
            Intent agriculture = new Intent(MainActivity.this, com.example.governmentscheme.Agriculture_activity.class);
            agriculture.putExtra("isAdmin", isAdmin);
            agriculture.putExtra("username", enteredUsername);
            startActivity(agriculture);
        }
        if (view.equals(bank)) {
            Intent bank = new Intent(MainActivity.this, banking_activity.class);
            bank.putExtra("isAdmin", isAdmin);
            startActivity(bank);
        }
        if (view.equals(business)) {
            Intent business = new Intent(MainActivity.this, business_activity.class);
            startActivity(business);
        }
        if (view.equals(education)) {
            Intent education = new Intent(MainActivity.this, education_activity.class);
            startActivity(education);
        }
        if (view.equals(health)) {
            Intent health = new Intent(MainActivity.this, health_activity.class);
            startActivity(health);
        }
        if (view.equals(house)) {
            Intent housing = new Intent(MainActivity.this, housing_activity.class);
            startActivity(housing);
        }

        if (view.equals(logout)) {
            Intent logout = new Intent(MainActivity.this, LogoutActivity.class);
            startActivity(logout);
        }
    }
}
