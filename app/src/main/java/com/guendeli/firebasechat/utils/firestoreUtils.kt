package com.guendeli.firebasechat.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.guendeli.firebasechat.modules.User

object firestoreUtils {
    private val  firestoreInstance : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef : DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().uid
        ?: throw NullPointerException("UID is Null")}");

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit){
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()){
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "","",null);
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete();
                }
            } else {
                onComplete();
            }
        }
    }

    fun updateCurrentUser(name:String = "",bio:String = "",profilePicturePath:String? = null){
        val userFieldMap = mutableMapOf<String,Any>();
        if(name.isNotBlank()){
            userFieldMap["name"] = name;
        }
        if(bio.isNotBlank()){
            userFieldMap["bio"] = bio;
        }
        if(profilePicturePath != null){
            userFieldMap["profilePicturePath"] = profilePicturePath;
        }
        currentUserDocRef.update(userFieldMap);
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }


}