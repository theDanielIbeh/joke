package com.example.jokes.fragments.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.jokes.data.joke.Joke
import com.example.jokes.databinding.JokeItemBinding

object JokeComparator : DiffUtil.ItemCallback<Joke>() {
    override fun areItemsTheSame(oldItem: Joke, newItem: Joke): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Joke, newItem: Joke): Boolean {
        return oldItem == newItem
    }
}

class FavouritesPagingDataAdapter :
    PagingDataAdapter<Joke, FavouritesPagingDataAdapter.JokeViewHolder>(JokeComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = JokeItemBinding.inflate(inflater, parent, false)

        return JokeViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        val item = getItem(position)
        // Note that item may be null. ViewHolder must support binding a
        // null item as a placeholder.
        holder.bind(item, position)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just an Affirmation object.
    inner class JokeViewHolder(private val binding: JokeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(joke: Joke?, position: Int) {
            try {
                if (joke == null) {
                    return
                }

                binding.isSingle = joke.type == "single"

                joke.setup?.let { i -> binding.textViewSetup.text = i }
                binding.textViewDelivery.text = if (joke.type == "single") {
                    joke.joke
                } else {
                    joke.delivery
                }

            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }
}