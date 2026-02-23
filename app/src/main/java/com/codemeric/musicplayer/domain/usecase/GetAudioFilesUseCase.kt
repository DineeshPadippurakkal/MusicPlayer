package com.codemeric.musicplayer.domain.usecase

import com.codemeric.musicplayer.data.repo.AudioRepository

class GetAudioFilesUseCase(
    private val repository: AudioRepository
) {
    operator fun invoke() = repository.loadAudioFiles()
}