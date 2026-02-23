package com.codemeric.musicplayer.ui.playback

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codemeric.musicplayer.data.repo.AudioRepository
import com.codemeric.musicplayer.domain.usecase.GetAudioFilesUseCase
import com.codemeric.musicplayer.domain.usecase.PlayAudioUseCase
import com.codemeric.musicplayer.player.MediaPlayerManager
import com.codemeric.musicplayer.player.PlayerProvider
class PlaybackViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val mediaPlayerManager =
            PlayerProvider.provide(context)

        return PlaybackViewModel(
            GetAudioFilesUseCase(AudioRepository(context)),
            PlayAudioUseCase(mediaPlayerManager),
            mediaPlayerManager
        ) as T
    }
}