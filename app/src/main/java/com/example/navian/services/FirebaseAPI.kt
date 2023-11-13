package com.example.navian.services

import androidx.navigation.NavController
import com.example.navian.Observation
import com.example.navian.Screen
import com.example.navian.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun handleSignIn(navController: NavController, email: String, password: String)
{
    // Create an instance of FirebaseAuth
    val auth = FirebaseAuth.getInstance()

    // Sign In
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener    { task ->
        if (task.isSuccessful)
        { navController.navigate(Screen.HomeScreen.route) }
    }
}

fun handleSignUp(navController: NavController, email: String, password: String, passwordConfirm: String)
{
    // Create an instance of FirebaseAuth
    val auth = FirebaseAuth.getInstance()

    // Create User
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful)
            { navController.navigate(Screen.SignInScreen.route) }
        }
}

fun validatePassword(password: String): Boolean
{
    var valid = true

    if( password.length < 9 ||
        password.none { it.isDigit() } ||
        password.none { it.isLetterOrDigit().not() })
    { valid = false; }

    return valid
}

fun handleCreateObservation(observation: Observation)
{
    // Get the current user's UID
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    // Create a database reference to the "users" node in the database
    val usersRef = FirebaseDatabase.getInstance().getReference("users")

    // Write the new category and timesheet to the database under the user's node
    val observationsKey = usersRef.child(uid!!).child("observations").push().key

    val childUpdates = HashMap<String, Any>()
    childUpdates["$uid/observations/$observationsKey"] = observation

    usersRef.updateChildren(childUpdates)
        .addOnSuccessListener {
            // Category and timesheet data successfully written to the database
        }
        .addOnFailureListener { e ->
            // An error occurred while writing the data
        }
}

fun handleSettings(settings: Settings) {
    // Get the current user's UID
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    // Create a database reference to the "users" node in the database
    val usersRef = FirebaseDatabase.getInstance().getReference("users")

    // Check if the "settings" node exists for the user
    val settingsRef = usersRef.child(uid!!).child("settings")

    settingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                // "settings" node exists, update it
                updateSettings(uid, settings)
            } else {
                // "settings" node does not exist, create a new one
                createSettings(uid, settings)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle onCancelled event
        }
    })
}

fun updateSettings(uid: String, settings: Settings) {
    val settingsRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("settings")

    val settingsValues = settings.toMap() // Assume you have a toMap() function in your Settings class

    settingsRef.updateChildren(settingsValues)
        .addOnSuccessListener {
            // Settings data successfully updated in the database
        }
        .addOnFailureListener { e ->
            // An error occurred while updating the settings data
        }
}

fun createSettings(uid: String, settings: Settings) {
    val settingsRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("settings")

    val settingsKey = settingsRef.push().key
    val settingsValues = settings.toMap() // Assume you have a toMap() function in your Settings class

    val childUpdates = HashMap<String, Any>()
    childUpdates["$uid/settings/$settingsKey"] = settingsValues

    settingsRef.updateChildren(childUpdates)
        .addOnSuccessListener {
            // Settings data successfully written to the database
        }
        .addOnFailureListener { e ->
            // An error occurred while writing the settings data
        }
}

fun readObservations(): MutableList<Observation> {
    var observations: MutableList<Observation> = mutableListOf<Observation>()

    // Get the current user's UID
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    // Create a database reference to the "users" node in the database
    val usersRef = FirebaseDatabase.getInstance().getReference("users")

    // Read the user's categories from the database
    usersRef.child(uid!!).child("observations").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (observationSnapshot in dataSnapshot.children) {
                val observation = observationSnapshot.getValue(Observation::class.java)
                observation?.let {
                    observations.add(observation)
                }
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // An error occurred while reading the data
        }
    })
 return observations
}

// ReadSettings Here