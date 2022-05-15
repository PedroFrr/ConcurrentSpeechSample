package com.example.multipleaudioplayer.gestures

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.multipleaudioplayer.R
import com.example.multipleaudioplayer.databinding.ItemHomescreenBinding

class ItemsListAdapter :
    ListAdapter<String, RecyclerView.ViewHolder>(
        DailyAllowanceSummaryCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SportViewHolder(
            ItemHomescreenBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val iconTitle = getItem(position)
        (holder as SportViewHolder).bind(iconTitle)
    }

    class SportViewHolder(
        private val binding: ItemHomescreenBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val isTalkbackEnabled: Boolean by lazy {
            val am = binding.root.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            am.isTouchExplorationEnabled
        }

        private val keyPressed = MediaPlayer.create(binding.root.context, R.raw.key_press)

        fun bind(item: String) {
            binding.btnIcon.text = item
            binding.btnIcon.setOnClickListener {
                if (!isTalkbackEnabled) keyPressed.start()
            }
        }

    }
}

private class DailyAllowanceSummaryCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}