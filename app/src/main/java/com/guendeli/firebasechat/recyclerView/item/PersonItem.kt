package com.guendeli.firebasechat.recyclerView.item

import android.content.Context
import com.guendeli.firebasechat.R
import com.guendeli.firebasechat.glide.GlideApp
import com.guendeli.firebasechat.modules.User
import com.guendeli.firebasechat.utils.storageUtils
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*

class PersonItem(val person : User, val userId : String, private val context : Context ) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.textView_name.text = person.name;
        viewHolder.textView_bio.text = person.bio;
        if(person.profilePicturePath != null){
            GlideApp.with(context)
                .load(storageUtils.pathToReference(person.profilePicturePath))
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.imageView_profile_picture);
        }
    }

    override fun getLayout() = R.layout.item_person;
}