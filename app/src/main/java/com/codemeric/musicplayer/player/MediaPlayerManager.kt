package com.codemeric.musicplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import com.codemeric.musicplayer.ui.equalizer.EqualizerPreset

class MediaPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var equalizer: Equalizer? = null
    var onPrepared: ((Int) -> Unit)? = null
    var onSongComplete: (() -> Unit)? = null

    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    fun play(assetPath: String) {

        mediaPlayer?.setOnCompletionListener(null)
        release()

        val afd = context.assets.openFd(assetPath)

        mediaPlayer = MediaPlayer().apply {

            setDataSource(
                afd.fileDescriptor,
                afd.startOffset,
                afd.length
            )

            setOnPreparedListener { player ->

                attachEqualizer(player)

                onPrepared?.invoke(player.duration)

                player.start()
            }

            setOnCompletionListener {
                onSongComplete?.invoke()
            }

            prepareAsync()
        }
    }

    private fun attachEqualizer(player: MediaPlayer) {

        equalizer?.release()

        equalizer = Equalizer(0, player.audioSessionId).apply {
            enabled = true
        }
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentPosition(): Int =
        mediaPlayer?.currentPosition ?: 0

    fun getDuration(): Int =
        mediaPlayer?.duration ?: 0

    fun getAudioSessionId(): Int =
        mediaPlayer?.audioSessionId ?: -1

    fun release() {
        equalizer?.release()
        equalizer = null

        mediaPlayer?.release()
        mediaPlayer = null
    }

    // ===========================
    // Equalizer Controls
    // ===========================

    fun applyPreset(preset: EqualizerPreset) {

        val eq = equalizer ?: return

        val range = eq.bandLevelRange
        val min = range[0]
        val max = range[1]
        val mid = (min + max) / 2

        val boost = (max * 0.6).toInt()   // 60% boost
        val cut = (min * 0.6).toInt()     // 60% cut

        when (preset) {

            EqualizerPreset.ROCK -> {
                eq.setBandLevel(0, cut.toShort())
                eq.setBandLevel(1, boost.toShort())
                eq.setBandLevel(2, boost.toShort())
                eq.setBandLevel(3, boost.toShort())
                eq.setBandLevel(4, cut.toShort())
            }

            EqualizerPreset.JAZZ -> {
                eq.setBandLevel(0, boost.toShort())
                eq.setBandLevel(1, mid.toShort())
                eq.setBandLevel(2, mid.toShort())
                eq.setBandLevel(3, mid.toShort())
                eq.setBandLevel(4, boost.toShort())
            }

            EqualizerPreset.CLASSICAL -> {
                for (i in 0 until eq.numberOfBands) {
                    eq.setBandLevel(i.toShort(), mid.toShort())
                }
            }

            EqualizerPreset.POP -> {
                eq.setBandLevel(0, cut.toShort())
                eq.setBandLevel(1, boost.toShort())
                eq.setBandLevel(2, boost.toShort())
                eq.setBandLevel(3, boost.toShort())
                eq.setBandLevel(4, cut.toShort())
            }

            EqualizerPreset.FLAT -> {
                for (i in 0 until eq.numberOfBands) {
                    eq.setBandLevel(i.toShort(), 0)
                }
            }

            else -> {
                for (i in 0 until eq.numberOfBands) {
                    eq.setBandLevel(i.toShort(), 0)
                }
            }
        }
    }

    fun setBandLevel(band: Short, level: Short) {
        equalizer?.setBandLevel(band, level)
    }

    fun getBandLevel(band: Short): Short =
        equalizer?.getBandLevel(band) ?: 0

    fun getBandLevelRange(): ShortArray? =
        equalizer?.bandLevelRange

    fun getNumberOfBands(): Short =
        equalizer?.numberOfBands ?: 0

    fun isInitialized(): Boolean {
        return mediaPlayer != null
    }
}