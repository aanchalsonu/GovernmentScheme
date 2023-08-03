const admin = require('firebase-admin');

const serviceAccount = require('./serviceAccountKey.json');
const express = require('express');
const app = express();

const port = 3000; // Replace with the desired port number

app.listen(port, () => {
  console.log(`Backend service running on port ${port}`);
});

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});
// Example: Monitor Firestore for new entries and send notifications
const db = admin.firestore();

const docRef = db.collection('agriculture').doc('agriculture.desc');

docRef.get()
  .then((doc) => {
    if (doc.exists) {
      console.log('Document data:', doc.data());
    } else {
      console.log('Document not found.');
    }
  })
  .catch((error) => {
    console.error('Error reading document:', error);
  });


db.collection('agriculture').onSnapshot((snapshot) => {
  snapshot.docChanges().forEach((change) => {
    if (change.type === 'added') {
      // New entry added to the collection, send notification
      const notification = {
        title: 'New Entry Added',
        body: 'A new entry has been added to the database.',
      };

      const message = {
        notification,
        topic: 'your_notification_topic', // Replace with your notification topic
      };

      admin.messaging().send(message)
        .then((response) => {
          console.log('Notification sent successfully:', response);
        })
        .catch((error) => {
          console.error('Error sending notification:', error);
        });
    }
  });
});
