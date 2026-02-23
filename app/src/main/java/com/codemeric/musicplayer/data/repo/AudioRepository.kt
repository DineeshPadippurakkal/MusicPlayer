package com.codemeric.musicplayer.data.repo

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.codemeric.musicplayer.data.model.AudioFile

class AudioRepository(private val context: Context) {

    fun loadAudioFiles(): List<AudioFile> {
        val files = listOf("Kannaana Kanney.mp3", "Kunjikkavil Meghame.mp3", "Etho Vaarmukilin.mp3")

        return files.map { fileName ->
            val afd = context.assets.openFd(fileName)

            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

            val title = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_TITLE
            ) ?: fileName

            val artist = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_ARTIST
            ) ?: "Unknown Artist"

            val duration = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: 0L

            val artBytes = retriever.embeddedPicture
            val bitmap = artBytes?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }

            retriever.release()

            AudioFile(title, artist, duration, fileName, bitmap)
        }
    }
}