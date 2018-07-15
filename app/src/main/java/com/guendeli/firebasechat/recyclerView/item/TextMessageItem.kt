package com.guendeli.firebasechat.recyclerView.item

import android.content.Context
import com.guendeli.firebasechat.R
import com.guendeli.firebasechat.modules.TextMessage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class TextMessageItem (val message : TextMessage,
                       val context : Context) : Item()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // Placeholder bind

    }

    override fun getLayout() = R.layout.item_text_message;
}