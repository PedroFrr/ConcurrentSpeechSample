package com.example.multipleaudioplayer.gestures

 import android.media.MediaPlayer
 import android.view.LayoutInflater
import android.view.ViewGroup
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

        fun bind(item: String) {
            val keyPressed = MediaPlayer.create(binding.root.context, R.raw.key_press)

            binding.btnIcon.text = item
            binding.btnIcon.setOnClickListener {
                keyPressed.start()
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