package com.codemeric.musicplayer.ui.equalizer

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.codemeric.musicplayer.databinding.FragmentEqualizerBinding
import com.codemeric.musicplayer.ui.playback.PlaybackViewModel
import com.codemeric.musicplayer.ui.playback.PlaybackViewModelFactory

class EqualizerActivity : AppCompatActivity() {

    private lateinit var binding: FragmentEqualizerBinding

    private val viewModel: EqualizerViewModel by viewModels {
        EqualizerViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEqualizerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)
        binding.toolbar.title="Equalizer"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!viewModel.isEqualizerReady()) {
            finish() // no player running
            return
        }

        setupSliders()
        setupButtons()
        setupPresetRecycler()
    }



    override fun onResume() {
        super.onResume()
        syncSliders()
    }

    private fun setupPresetRecycler() {

        val presets = EqualizerPreset.values().toList()

        val adapter = PresetAdapter(presets) { preset ->
            viewModel.applyPreset(preset)
            syncSliders()
        }

        binding.recyclerPresets.layoutManager =
            GridLayoutManager(this, 3) // 3 columns

        binding.recyclerPresets.adapter = adapter
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    private fun setupButtons() {



        binding.btnFlat.setOnClickListener {
            viewModel.applyPreset(EqualizerPreset.FLAT)
            syncSliders()
            highlightSelected(binding.btnFlat)
        }

        binding.btnRock.setOnClickListener {
            viewModel.applyPreset(EqualizerPreset.ROCK)
            syncSliders()
            highlightSelected(binding.btnRock)
        }

        binding.btnJazz.setOnClickListener {
            viewModel.applyPreset(EqualizerPreset.JAZZ)
            syncSliders()
            highlightSelected(binding.btnJazz)
        }

        binding.btnClassical.setOnClickListener {
            viewModel.applyPreset(EqualizerPreset.CLASSICAL)
            syncSliders()
            highlightSelected(binding.btnClassical)
        }

        binding.btnPop.setOnClickListener {
            viewModel.applyPreset(EqualizerPreset.POP)
            syncSliders()
            highlightSelected(binding.btnPop)
        }

        binding.btnVocal.setOnClickListener {
            viewModel.applyPreset(EqualizerPreset.VOCAL)
            syncSliders()
            highlightSelected(binding.btnVocal)
        }
    }

    private fun highlightSelected(selected: View) {

        val buttons = listOf(
            binding.btnFlat,
            binding.btnRock,
            binding.btnJazz,
            binding.btnClassical,
            binding.btnPop,
            binding.btnVocal
        )

        buttons.forEach {
            it.alpha = 0.5f
            it.scaleX = 1f
            it.scaleY = 1f
        }

        selected.alpha = 1f
        selected.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(150)
            .start()
    }

    private fun setupSliders() {

        val range = viewModel.getBandLevelRange() ?: return
        val min = range[0]
        val max = range[1]

        val seekBars = listOf(
            binding.seek1,
            binding.seek2,
            binding.seek3,
            binding.seek4,
            binding.seek5
        )

        seekBars.forEachIndexed { index, seekBar ->

            seekBar.max = 10

            seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {

                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) {

                            val mapped =
                                min + ((progress / 10f) * (max - min)).toInt()

                            viewModel.setBandLevel(
                                index.toShort(),
                                mapped.toShort()
                            )
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                }
            )
        }
    }

    private fun syncSliders() {

        val range = viewModel.getBandLevelRange() ?: return
        val min = range[0]
        val max = range[1]

        val seekBars = listOf(
            binding.seek1,
            binding.seek2,
            binding.seek3,
            binding.seek4,
            binding.seek5
        )

        seekBars.forEachIndexed { index, seekBar ->

            val level =
                viewModel.getBandLevel(index.toShort())

            val progress =
                ((level - min).toFloat() / (max - min) * 10).toInt()

            seekBar.progress = progress
        }
    }
}