package com.guendeli.firebasechat.modules

import android.media.Image
import java.util.Date

data class ImageMessage( val imagePath: String,
                         override val time: Date,
                         override val senderId: String,
                         override val recipientId : String,
                         override val senderName : String,
                         override val type: String = MessageType.IMAGE)
    : Message {

    constructor() : this("", Date(0),"","","");

}