package com.codemeric.musicplayer.data.model

import android.graphics.Bitmap

data class AudioFile(
    val title: String,
    val artist: String,
    val duration: Long,
    val assetPath: String,
    val albumArt: Bitmap? = null
)