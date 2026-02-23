package com.codemeric.musicplayer.ui.playback

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.codemeric.musicplayer.R
import com.codemeric.musicplayer.databinding.FragmentPlaybackBinding
import com.codemeric.musicplayer.ui.equalizer.EqualizerActivity
import com.codemeric.musicplayer.util.TimeFormatter

class PlaybackFragment :
    Fragment(R.layout.fragment_playback) {

    private var _binding: FragmentPlaybackBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaybackViewModel by activityViewModels {
        PlaybackViewModelFactory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPlaybackBinding.bind(view)

        observeState()
        setupClicks()
    }

    private fun observeState() {

        lifecycleScope.launchWhenStarted {

            viewModel.uiState.collect { state ->

                if (!isAdded || _binding == null) return@collect

                binding.tvTitle.text =
                    state.currentAudio?.title ?: "-"

                binding.tvArtist.text =
                    state.currentAudio?.artist ?: "-"

                binding.seekBar.max = state.duration
                binding.seekBar.progress = state.progress

                binding.waveformView.updateProgress(
                    state.progress,
                    state.duration
                )
                binding.tvCurrentTime.text =
                    TimeFormatter.format(state.progress)

                binding.tvDuration.text =
                    TimeFormatter.format(state.duration)

                binding.btnPlayPause.setIconResource(
                    if (state.isPlaying)
                        R.drawable.ic_pause
                    else
                        R.drawable.ic_play
                )



                updateCarouselImages(state)

                state.currentAudio?.albumArt?.let { bitmap ->

                    binding.imgAlbumArt.setImageBitmap(bitmap)
                    binding.imgBackground.setImageBitmap(bitmap)

                    applyDynamicButtonColor(bitmap)
                }
            }
        }
    }

    private fun animateButtonTint(newColor: Int) {

        val colorState = ColorStateList.valueOf(newColor)

        binding.btnPlayPause.animate().setDuration(200).withStartAction {
            binding.btnPlayPause.backgroundTintList = colorState
        }

        binding.btnNext.backgroundTintList = colorState
        binding.btnPrevious.backgroundTintList = colorState
    }

    private fun applyDynamicButtonColor(bitmap: android.graphics.Bitmap) {

        Palette.from(bitmap)
            .clearFilters()
            .maximumColorCount(16)
            .generate { palette ->
                val baseColor =
                    palette?.darkVibrantSwatch?.rgb
                        ?: palette?.vibrantSwatch?.rgb
                        ?: palette?.getDominantColor(Color.parseColor("#6200EE"))
                        ?: Color.parseColor("#6200EE")

                val darkerColor = darkenColor(baseColor, 0.85f)

                val tint = ColorStateList.valueOf(darkerColor)
                animateButtonTint(darkerColor)
                binding.btnPlayPause.backgroundTintList = tint
                binding.btnNext.backgroundTintList = tint
                binding.btnPrevious.backgroundTintList = tint

                binding.seekBar.progressTintList = tint
                binding.seekBar.thumbTintList = tint

                // Optional: change background track slightly transparent
                binding.seekBar.progressBackgroundTintList =
                    ColorStateList.valueOf(darkenColor(baseColor, 0.3f))
            }
    }

    private fun darkenColor(color: Int, factor: Float): Int {
        val r = (Color.red(color) * factor).toInt()
        val g = (Color.green(color) * factor).toInt()
        val b = (Color.blue(color) * factor).toInt()
        return Color.rgb(
            r.coerceAtLeast(0),
            g.coerceAtLeast(0),
            b.coerceAtLeast(0)
        )
    }

    private fun updateCarouselImages(state: PlaybackUiState) {

        val list = state.audioFiles
        val currentIndex = list.indexOf(state.currentAudio)

        if (currentIndex == -1 || list.isEmpty()) return

        val previousIndex =
            if (currentIndex - 1 < 0)
                list.lastIndex else currentIndex - 1

        val nextIndex =
            (currentIndex + 1) % list.size

        list[previousIndex].albumArt?.let {
            binding.imgPrevious.setImageBitmap(it)
        }

        list[nextIndex].albumArt?.let {
            binding.imgNext.setImageBitmap(it)
        }
    }

    private fun setupClicks() {
        binding.btnEqualizer.setCardBackgroundColor(
            android.graphics.Color.DKGRAY
        )
        binding.btnEqualizer.strokeWidth = 0

        binding.btnEqualizer.setOnClickListener {
            startActivity(
                Intent(requireContext(), EqualizerActivity::class.java)
            )
        }

        binding.btnNext.setOnClickListener {
            animateButtonClick(it)
            animateNext()
        }

        binding.btnPrevious.setOnClickListener {
            animateButtonClick(it)
            animatePrevious()
        }

        binding.btnPlayPause.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.seekBar.setOnSeekBarChangeListener(
            object : android.widget.SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: android.widget.SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        viewModel.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
            }
        )
    }

    // =============================
    // ANIMATIONS
    // =============================

    private fun animateNext() {

        val screenWidth =
            resources.displayMetrics.widthPixels.toFloat()

        // 1️⃣ Update data first
        viewModel.playNext()

        // 2️⃣ Prepare next image on right
        binding.imgNext.translationX = screenWidth
        binding.imgNext.alpha = 1f

        // 3️⃣ Animate current out
        binding.imgAlbumArt.animate()
            .translationX(-screenWidth)
            .alpha(0f)
            .setDuration(300)
            .start()

        // 4️⃣ Animate next in
        binding.imgNext.animate()
            .translationX(0f)
            .setDuration(300)
            .withEndAction {

                // Swap references visually
                binding.imgAlbumArt.setImageDrawable(
                    binding.imgNext.drawable
                )

                binding.imgAlbumArt.translationX = 0f
                binding.imgAlbumArt.alpha = 1f

            }
            .start()
    }

    private fun animatePrevious() {

        val screenWidth =
            resources.displayMetrics.widthPixels.toFloat()

        viewModel.playPrevious()

        binding.imgPrevious.translationX = -screenWidth
        binding.imgPrevious.alpha = 1f

        binding.imgAlbumArt.animate()
            .translationX(screenWidth)
            .alpha(0f)
            .setDuration(300)
            .start()

        binding.imgPrevious.animate()
            .translationX(0f)
            .setDuration(300)
            .withEndAction {

                binding.imgAlbumArt.setImageDrawable(
                    binding.imgPrevious.drawable
                )

                binding.imgAlbumArt.translationX = 0f
                binding.imgAlbumArt.alpha = 1f
            }
            .start()
    }


    private fun animateButtonClick(button: View) {
        button.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .duration = 100
            }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}