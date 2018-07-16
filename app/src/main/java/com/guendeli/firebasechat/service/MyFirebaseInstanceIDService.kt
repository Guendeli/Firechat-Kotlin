package com.guendeli.firebasechat.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.guendeli.firebasechat.utils.firestoreUtils

class MyFirebaseInstanceIDService : FirebaseInstanceIdService(){
    override fun onTokenRefresh() {
        val newRegistrationToken = FirebaseInstanceId.getInstance().token;

        if(FirebaseAuth.getInstance().currentUser != null){
            addTokenToFirestore(newRegistrationToken)
        }
    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken : String?){
            if(newRegistrationToken == null) throw NullPointerException("FCM Token is null")

            firestoreUtils.getFCMRegistrationTokens { tokens ->
                if(tokens.contains(newRegistrationToken)){
                    return@getFCMRegistrationTokens;
                }
                tokens.add(newRegistrationToken);
                firestoreUtils.setFCMRegistrationTokens(tokens)
            }
        }
    }
}