package com.urlaunched.android.player.mediacontroller.models

interface Track {
    val id: Int
    val name: String
    val author: String
    val url: String
    val imageMedia: String?
}