package com.codemeric.musicplayer.player

import android.content.Context

object PlayerProvider {

    private var mediaPlayerManager: MediaPlayerManager? = null

    fun provide(context: Context): MediaPlayerManager {
        if (mediaPlayerManager == null) {
            mediaPlayerManager = MediaPlayerManager(context.applicationContext)
        }
        return mediaPlayerManager!!
    }
}