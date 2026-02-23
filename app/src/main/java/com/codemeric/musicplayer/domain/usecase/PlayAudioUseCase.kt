package com.codemeric.musicplayer.domain.usecase


import com.codemeric.musicplayer.player.MediaPlayerManager


class PlayAudioUseCase(
    private val mediaPlayerManager: MediaPlayerManager
) {
    operator fun invoke(assetPath: String) {
        mediaPlayerManager.play(assetPath)
    }
}