package com.example.multipleaudioplayer.scanning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.multipleaudioplayer.databinding.ItemAudioConfigurationBinding

class AudioChannelPropertiesListAdapter() :
    ListAdapter<AudioChannelProperty, AudioChannelPropertiesListAdapter.AudioChannelPropertiesViewHolder>(
        AudioChannelPropertiesCallback()
    ) {

    override fun onBindViewHolder(holder: AudioChannelPropertiesViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioChannelPropertiesViewHolder {
        return AudioChannelPropertiesViewHolder.from(parent)
    }

    class AudioChannelPropertiesViewHolder private constructor(
        private val binding: ItemAudioConfigurationBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        companion object {
            fun from(parent: ViewGroup): AudioChannelPropertiesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAudioConfigurationBinding.inflate(layoutInflater, parent, false)
                return AudioChannelPropertiesViewHolder(
                    binding
                )
            }
        }

        fun bind(
            item: AudioChannelProperty,
        ) {
            with(binding) {
            }
        }
    }
}

private class AudioChannelPropertiesCallback : DiffUtil.ItemCallback<AudioChannelProperty>() {
    override fun areContentsTheSame(
        oldItem: AudioChannelProperty,
        newItem: AudioChannelProperty
    ): Boolean = false

    override fun areItemsTheSame(
        oldItem: AudioChannelProperty,
        newItem: AudioChannelProperty
    ): Boolean =
        oldItem == newItem
}