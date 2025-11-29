// Firebase Configuration
const firebaseConfig = {
    apiKey: "AIzaSyBNcjHow6H6GGOYujKnp75PZlyT6LGzKso",
    authDomain: "slate-ae34b-fa076.firebaseapp.com",
    databaseURL: "https://slate-ae34b-default-rtdb.asia-southeast1.firebasedatabase.app",
    projectId: "slate-ae34b",
    storageBucket: "slate-ae34b.firebasestorage.app",
    messagingSenderId: "558370176302",
    appId: "1:558370176302:web:f1693957121dd870e44ebf"
};

// Initialize Firebase
try {
    firebase.initializeApp(firebaseConfig);
} catch (error) {
    console.error('Firebase initialization error:', error);
    // If appId causes issues, try without it
    if (error.message && error.message.includes('appId')) {
        delete firebaseConfig.appId;
        firebase.initializeApp(firebaseConfig);
    }
}

// Initialize Firebase
firebase.initializeApp(firebaseConfig);

// Get Firebase services
const auth = firebase.auth();
const database = firebase.database();

