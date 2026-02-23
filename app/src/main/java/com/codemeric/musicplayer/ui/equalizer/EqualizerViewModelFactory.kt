package com.codemeric.musicplayer.ui.equalizer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codemeric.musicplayer.player.PlayerProvider

class EqualizerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(EqualizerViewModel::class.java)) {

            val mediaPlayerManager =
                PlayerProvider.provide(context)

            val prefs = context.applicationContext
                .getSharedPreferences(
                    "equalizer_prefs",
                    Context.MODE_PRIVATE
                )

            return EqualizerViewModel(
                mediaPlayerManager,
                prefs
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}