package com.guendeli.firebasechat.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.guendeli.firebasechat.modules.User
import com.guendeli.firebasechat.recyclerView.item.PersonItem
import com.xwray.groupie.kotlinandroidextensions.Item

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

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
                }
                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()


}