package com.codemeric.musicplayer.ui.equalizer


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codemeric.musicplayer.databinding.ItemPresetBinding

class PresetAdapter(
    private val presets: List<EqualizerPreset>,
    private val onClick: (EqualizerPreset) -> Unit
) : RecyclerView.Adapter<PresetAdapter.PresetViewHolder>() {

    private var selectedPosition = -1

    inner class PresetViewHolder(
        private val binding: ItemPresetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(preset: EqualizerPreset, position: Int) {

            binding.tvPreset.text =
                preset.name.lowercase()
                    .replaceFirstChar { it.uppercase() }

            if (position == selectedPosition) {
                binding.cardPreset.setCardBackgroundColor(
                    binding.root.context.getColor(
                        com.google.android.material.R.color.design_default_color_primary
                    )
                )
                binding.cardPreset.strokeWidth = 4
            } else {
                binding.cardPreset.setCardBackgroundColor(
                    android.graphics.Color.DKGRAY
                )
                binding.cardPreset.strokeWidth = 0
            }

            binding.root.setOnClickListener {

                val previous = selectedPosition
                selectedPosition = position

                notifyItemChanged(previous)
                notifyItemChanged(position)

                onClick(preset)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PresetViewHolder {

        val binding = ItemPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PresetViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PresetViewHolder,
        position: Int
    ) {
        holder.bind(presets[position], position)
    }

    override fun getItemCount(): Int = presets.size

}