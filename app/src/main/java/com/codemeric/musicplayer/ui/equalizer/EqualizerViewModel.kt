package com.codemeric.musicplayer.ui.equalizer

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.codemeric.musicplayer.player.MediaPlayerManager

class EqualizerViewModel(
    private val mediaPlayerManager: MediaPlayerManager,
    private val prefs: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_PRESET = "preset"
    }

//    init {
//        loadSavedPreset()
//    }

    // =============================
    // PRESET PERSISTENCE
    // =============================

    fun loadSavedPreset() {

        val name = prefs.getString(
            KEY_PRESET,
            EqualizerPreset.FLAT.name
        )

        val preset = try {
            EqualizerPreset.valueOf(name ?: EqualizerPreset.FLAT.name)
        } catch (e: Exception) {
            EqualizerPreset.FLAT
        }

        mediaPlayerManager.applyPreset(preset)
    }

    fun applyPreset(preset: EqualizerPreset) {
        mediaPlayerManager.applyPreset(preset)

        prefs.edit()
            .putString(KEY_PRESET, preset.name)
            .apply()
    }

    // =============================
    // BAND CONTROL
    // =============================

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

    fun isEqualizerReady(): Boolean {
        return getBandLevelRange() != null
    }
}