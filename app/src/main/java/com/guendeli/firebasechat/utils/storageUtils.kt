package com.guendeli.firebasechat.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

object storageUtils {
    private val storageInstance : FirebaseStorage by lazy { FirebaseStorage.getInstance()}

    private val currentUserRef : StorageReference
        get() = storageInstance.reference
            .child(FirebaseAuth.getInstance().uid?: throw NullPointerException("UID is Null"));

    fun uploadProfilePicture(imageBytes : ByteArray, onSuccess: (imagePath : String) -> Unit){
        val ref = currentUserRef.child("profilePictures/${UUID.nameUUIDFromBytes(imageBytes)}");
        ref.putBytes(imageBytes).addOnSuccessListener {
            onSuccess(ref.path);
        }
    }

    fun pathToReference(path : String) = storageInstance.getReference(path);
}