package com.guendeli.firebasechat.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.guendeli.firebasechat.modules.ChatChannel
import com.guendeli.firebasechat.modules.ImageMessage
import com.guendeli.firebasechat.modules.Message
import com.guendeli.firebasechat.modules.MessageType
import com.guendeli.firebasechat.modules.TextMessage
import com.guendeli.firebasechat.modules.User
import com.guendeli.firebasechat.recyclerView.item.ImageMessageItem
import com.guendeli.firebasechat.recyclerView.item.PersonItem
import com.guendeli.firebasechat.recyclerView.item.TextMessageItem
import com.xwray.groupie.kotlinandroidextensions.Item

object firestoreUtils {
    private val  firestoreInstance : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef : DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().uid
        ?: throw NullPointerException("UID is Null")}");

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

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


    fun getOrCreateChatChannel(otherUserId : String,
                               onComplete: (channelId : String) -> Unit){
        currentUserDocRef.collection("engagedChatChannels").document(otherUserId).get().addOnSuccessListener {
            if(it.exists()){
                onComplete(it["channelId"] as String);
                return@addOnSuccessListener;
            }
            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid;
            val newChannel = chatChannelsCollectionRef.document();
            newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)));

            currentUserDocRef.collection("engagedChatChannels").document(otherUserId)
                .set(mapOf("channelId" to newChannel.id));

            firestoreInstance.collection("users").document(otherUserId)
                .collection("engagedChatChannels")
                .document(currentUserId)
                .set(mapOf("channelId" to newChannel.id));

            onComplete(newChannel.id);
        }
    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                        items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message : Message, channelId : String){
        chatChannelsCollectionRef.document(channelId)
            .collection("messages").add(message);
    }

}