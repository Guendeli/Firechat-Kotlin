package com.guendeli.firebasechat.modules

import java.util.Date

object MessageType{
    const val TEXT = "TEXT";
    const val IMAGE = "IMAGE";
}
interface Message {
    val time: Date;
    val senderId: String;
    val recipientId : String;
    val senderName : String;
    val type: String;
}