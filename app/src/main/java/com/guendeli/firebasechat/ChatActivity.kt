package com.guendeli.firebasechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.guendeli.firebasechat.modules.MessageType
import com.guendeli.firebasechat.modules.TextMessage
import com.guendeli.firebasechat.utils.firestoreUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.toast
import java.util.Calendar

class ChatActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration : ListenerRegistration;
    private var shouldInitRecyclerView = true;
    private lateinit var  messagesSection : Section;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME);

        val otherUserId = intent.getStringExtra(AppConstants.USER_ID);

        firestoreUtils.getOrCreateChatChannel(otherUserId){channelId ->  
            messagesListenerRegistration =
                firestoreUtils.addChatMessagesListener(channelId, this, this::updateRecyclerView);

            imageView_send.setOnClickListener {
                val messageToSend =
                    TextMessage(editText_message.text.toString(), Calendar.getInstance().time, FirebaseAuth.getInstance().currentUser!!.uid,MessageType.TEXT);
                editText_message.setText("");
                firestoreUtils.sendMessage(messageToSend, channelId);
            }

            fab_send_image.setOnClickListener {
                // TODO: send image listener
            }
        }
    }

    private fun updateRecyclerView(messages : List<com.xwray.groupie.kotlinandroidextensions.Item>){
        fun init(){
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity);
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages);
                    this.add(messagesSection);
                }
            }
            shouldInitRecyclerView = false;
        }

        fun updateItems() = messagesSection.update(messages)

        if(shouldInitRecyclerView){
            init();
        } else {
            updateItems();
        }

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter.itemCount - 1);
    }
}
