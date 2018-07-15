package com.guendeli.firebasechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ListenerRegistration
import com.guendeli.firebasechat.utils.firestoreUtils
import com.xwray.groupie.Item
import org.jetbrains.anko.toast

class ChatActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration : ListenerRegistration;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME);

        val otherUserId = intent.getStringExtra(AppConstants.USER_ID);

        firestoreUtils.getOrCreateChatChannel(otherUserId){channelId ->  
            messagesListenerRegistration =
                firestoreUtils.addChatMessagesListener(channelId, this, this::onMessagesChanged);
        }
    }

    private fun onMessagesChanged(messages : List<com.xwray.groupie.kotlinandroidextensions.Item>){
        toast("OnMessagesChanged Running");
    }
}
