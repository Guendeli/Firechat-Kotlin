package com.guendeli.firebasechat.modules

data class ChatChannel (val userIds : MutableList<String>) {
    constructor() : this(mutableListOf());
}