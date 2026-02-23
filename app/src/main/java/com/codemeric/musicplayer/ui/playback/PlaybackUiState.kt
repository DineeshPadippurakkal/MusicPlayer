package com.codemeric.musicplayer.ui.playback

import com.codemeric.musicplayer.data.model.AudioFile

data class PlaybackUiState(
    val audioFiles: List<AudioFile> = emptyList(),
    val currentAudio: AudioFile? = null,
    val progress: Int = 0,
    val duration: Int = 0,
    val isPlaying: Boolean = false
)