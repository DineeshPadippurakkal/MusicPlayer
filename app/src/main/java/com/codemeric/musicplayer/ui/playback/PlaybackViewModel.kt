package com.codemeric.musicplayer.ui.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codemeric.musicplayer.data.model.AudioFile
import com.codemeric.musicplayer.domain.usecase.GetAudioFilesUseCase
import com.codemeric.musicplayer.domain.usecase.PlayAudioUseCase
import com.codemeric.musicplayer.player.MediaPlayerManager
import com.codemeric.musicplayer.ui.equalizer.EqualizerPreset
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaybackViewModel(
    private val getAudioFilesUseCase: GetAudioFilesUseCase,
    private val playAudioUseCase: PlayAudioUseCase,
    private val mediaPlayerManager: MediaPlayerManager
) : ViewModel() {

    private var isSwitching = false
    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState

    init {
        val files = getAudioFilesUseCase()

        _uiState.value = _uiState.value.copy(
            audioFiles = files,
            currentAudio = files.firstOrNull()
        )



        mediaPlayerManager.onPrepared = { duration ->
            _uiState.value = _uiState.value.copy(
                duration = duration,
                isPlaying = true
            )
        }
        mediaPlayerManager.onSongComplete = {

            isSwitching = true

            viewModelScope.launch {
                playNext()
                delay(300)
                isSwitching = false
            }
        }

        startProgressUpdates()
    }

    // ========================
    // PLAYBACK CONTROLS
    // ========================

    fun play(audioFile: AudioFile) {

        _uiState.value = _uiState.value.copy(
            currentAudio = audioFile
        )

        playAudioUseCase(audioFile.assetPath)
    }


    fun togglePlayback() {

        if (mediaPlayerManager.isPlaying) {
            mediaPlayerManager.pause()
        } else {

            if (!mediaPlayerManager.isInitialized()) {
                _uiState.value.currentAudio?.let {
                    play(it)
                }
            } else {
                mediaPlayerManager.resume()
                _uiState.value = _uiState.value.copy(isPlaying = true)
            }
        }

    }

    fun playNext() {

        val list = _uiState.value.audioFiles
        val current = _uiState.value.currentAudio ?: return

        val index = list.indexOf(current)
        val nextIndex = (index + 1) % list.size

        play(list[nextIndex])
    }

    fun playPrevious() {
        val list = _uiState.value.audioFiles
        val current = _uiState.value.currentAudio ?: return

        val index = list.indexOf(current)
        val previousIndex =
            if (index - 1 < 0) list.lastIndex else index - 1

        play(list[previousIndex])
    }

    fun seekTo(position: Int) {
        mediaPlayerManager.seekTo(position)
        _uiState.value = _uiState.value.copy(progress = position)
    }

    // ========================
    // PROGRESS LOOP
    // ========================

    private fun startProgressUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(1000)

                if (mediaPlayerManager.isPlaying) {
                    _uiState.value = _uiState.value.copy(
                        progress = mediaPlayerManager.getCurrentPosition(),
                        duration = mediaPlayerManager.getDuration()
                    )
                }
            }
        }
    }

    // ========================
    // EQUALIZER DELEGATION
    // ========================

    fun applyPreset(preset: EqualizerPreset) {
        mediaPlayerManager.applyPreset(preset)
    }

    fun setBandLevel(band: Short, level: Short) {
        mediaPlayerManager.setBandLevel(band, level)
    }

    fun getBandLevel(band: Short): Short {
        return mediaPlayerManager.getBandLevel(band)
    }

    fun getBandLevelRange(): ShortArray? {
        return mediaPlayerManager.getBandLevelRange()
    }

    fun getNumberOfBands(): Short {
        return mediaPlayerManager.getNumberOfBands()
    }
}