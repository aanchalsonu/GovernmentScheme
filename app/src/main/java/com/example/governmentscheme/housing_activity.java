package com.example.governmentscheme;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.governmentscheme.R;
import com.example.governmentscheme.Scheme;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class housing_activity extends AppCompatActivity {

    private TextView lblXmlData;
    private TextView header_title;
    private FirebaseFirestore firestore;
    private CollectionReference schemesCollection;

    private static final String CHANNEL_ID = "NewDataChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        header_title = findViewById(R.id.header_title);
        header_title.setText("Housing Sector");

        lblXmlData = findViewById(R.id.lbl_xml_data);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        schemesCollection = firestore.collection("housing");

        createNotificationChannel();

        FloatingActionButton fabAddScheme = findViewById(R.id.add);
        fabAddScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddSchemeDialog();
            }
        });

        loadSchemesFromFirestore();
    }

    private void loadSchemesFromFirestore() {
        schemesCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        lblXmlData.setText("");
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Scheme scheme = documentSnapshot.toObject(Scheme.class);
                            if (scheme != null) {
                                // Format the scheme string to appear bold
                                String formattedScheme = "<b>" + scheme.getSchemeName() + "</b>";
                                String schemeHead = "<b><u>" + "SCHEME :" + "</b></u>";
                                String descHead = "<b><u>" + "DESCRIPTION :" + "</b></u>";
                                // Print the values with formatting
                                lblXmlData.append(Html.fromHtml(schemeHead + formattedScheme + "<br>"));
                                lblXmlData.append(Html.fromHtml(descHead + ""));
                                lblXmlData.append(scheme.getDescription() + "\n\n");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Log.e(TAG, "Error getting documents: ", e);
                    }
                });
    }

    private void openAddSchemeDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_scheme, null);
        dialogBuilder.setView(dialogView);

        final EditText etSchemeName = dialogView.findViewById(R.id.et_scheme_name);
        final EditText etDescription = dialogView.findViewById(R.id.et_description);

        // Set the dialog title and positive button (Add button)
        dialogBuilder.setTitle("Add Scheme");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String schemeName = etSchemeName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                // Create a new Scheme object
                Scheme newScheme = new Scheme(schemeName, description);

                // Add the scheme to Firestore
                schemesCollection.add(newScheme)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Data added successfully
                                String documentId = documentReference.getId();
                                Log.d(TAG, "Document added with ID: " + documentId);
                                // Refresh the data displayed in the activity
                                loadSchemesFromFirestore();
                                // Generate a notification
                                showNotification("New Data Added", "A new data entry has been added.");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error adding data
                                Log.e(TAG, "Error adding document", e);
                            }
                        });
            }
        });

        // Set the negative button (Cancel button)
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // Show the dialog
        dialogBuilder.create().show();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "New Data";
            String description = "Channel for new data notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
}
